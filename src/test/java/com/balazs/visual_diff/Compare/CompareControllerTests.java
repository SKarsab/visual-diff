package com.balazs.visual_diff.Compare;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import com.balazs.visual_diff.Blob.BlobResponse;
import com.balazs.visual_diff.Blob.BlobService;
import com.balazs.visual_diff.Utilities.FileUtilities;
import com.balazs.visual_diff.Utilities.ValidationUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;

@WithMockUser
@WebMvcTest(CompareController.class)
public class CompareControllerTests {
    private final static String PREFIX = "http://localhost";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CompareService compareService;

    @MockitoBean
    private BlobService blobService;

    @Test
    @DisplayName("POST /compare valid input is provided, returns 201 created")
	public void compare_validInput_shouldReturn201Created() throws Exception {
        //Arrange
        String expectedBaselineFileName = "baseline.png";
        String expectedComparisonFileName = "comparison.png";
        String expectedDiffFileName = "diff.png";

        //Create new request body since this method is the only one that accepts a RequestBody
        CompareRequest compareRequest = new CompareRequest(expectedBaselineFileName, expectedComparisonFileName);
        String requestBody = objectMapper.writeValueAsString(compareRequest);

        //Mock behaviour to isolate controller method
        when(blobService.doesFileExist(expectedBaselineFileName)).thenReturn(true);
        when(blobService.doesFileExist(expectedComparisonFileName)).thenReturn(true);
        when(blobService.getFile(expectedBaselineFileName)).thenReturn(new byte[4]);
        when(blobService.getFile(expectedComparisonFileName)).thenReturn(new byte[4]);
		when(compareService.compare(new byte[4], new byte[4])).thenReturn(new byte[2]);
        when(blobService.saveFile(new byte[4], expectedBaselineFileName)).thenReturn(new BlobResponse());

        //Static mock for file utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<FileUtilities> fileUtilities = mockStatic(FileUtilities.class)) {
            fileUtilities.when(() -> FileUtilities.generateFileName("png", "diff")).thenReturn(expectedDiffFileName);

            //Act & Assert
            mockMvc.perform(post("/compare").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.baselineFileName").value(expectedBaselineFileName))
                .andExpect(jsonPath("$.comparisonFileName").value(expectedComparisonFileName))
                .andExpect(jsonPath("$.diffFileName").value(expectedDiffFileName))
                .andExpect(jsonPath("$._links.self.href").value(PREFIX + "/compare"))
                .andExpect(jsonPath("$._links.get[0].href").value(PREFIX + "/files/" + expectedDiffFileName))
                .andExpect(jsonPath("$._links.get[1].href").value(PREFIX + "/files/metadata/" + expectedDiffFileName))
                .andExpect(jsonPath("$._links.get[2].href").value(PREFIX + "/files/metadata"))
                .andExpect(jsonPath("$._links.delete.href").value(PREFIX + "/files/" + expectedDiffFileName));
        }
	}

    @Test
    @DisplayName("POST /compare null input is provided, returns 400 bad request")
	public void compare_nullInput_shouldReturn400BadRequest() throws Exception {
        //Arrange
        //Create new request body since this method is the only one that accepts a RequestBody
        CompareRequest compareRequest = new CompareRequest(null, null);
        String requestBody = objectMapper.writeValueAsString(compareRequest);

        //Act & Assert
        mockMvc.perform(post("/compare").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody).with(csrf()))
            .andExpect(status().isBadRequest());
	}

    @Test
    @DisplayName("POST /compare empty input is provided, returns 400 bad request")
	public void compare_emptyInput_shouldReturn400BadRequest() throws Exception {
        //Arrange
        //Create new request body since this method is the only one that accepts a RequestBody
        CompareRequest compareRequest = new CompareRequest("", " ");
        String requestBody = objectMapper.writeValueAsString(compareRequest);

        //Act & Assert
        mockMvc.perform(post("/compare").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody).with(csrf()))
            .andExpect(status().isBadRequest());
	}

    @Test
    @DisplayName("POST non-existent baselineFileName is provided, returns 404 not found")
	public void compare_fakeBaselineFileName_shouldReturn404NotFound() throws Exception {
        //Arrange
        //Create new request body since this method is the only one that accepts a RequestBody
        CompareRequest compareRequest = new CompareRequest("doesntExist.png", "compare.png");
        String requestBody = objectMapper.writeValueAsString(compareRequest);

        //Mock behaviour to isolate controller method
        when(blobService.doesFileExist("doesntExist.png")).thenReturn(false);

        //Act & Assert
        mockMvc.perform(post("/compare").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody).with(csrf()))
            .andExpect(status().isNotFound());
	}

    @Test
    @DisplayName("POST non-existent comparisonFileName is provided, returns 404 not found")
	public void compare_fakeComparisonFileName_shouldReturn404NotFound() throws Exception {
        //Arrange
        //Create new request body since this method is the only one that accepts a RequestBody
        CompareRequest compareRequest = new CompareRequest("baseline.png", "doesntExist.png");
        String requestBody = objectMapper.writeValueAsString(compareRequest);

        //Mock behaviour to isolate controller method
        when(blobService.doesFileExist("baseline.png")).thenReturn(true);
        when(blobService.doesFileExist("doesntExist.png")).thenReturn(false);

        //Act & Assert
        mockMvc.perform(post("/compare").contentType(MediaType.APPLICATION_JSON_VALUE).content(requestBody).with(csrf()))
            .andExpect(status().isNotFound());
	}

    @Test
    @DisplayName("POST /compare-direct valid input is provided, returns 200 ok")
	public void compareDirect_validInput_shouldReturn200Ok() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("baseline", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);
        MockMultipartFile comparisonFile = new MockMultipartFile("comparison", "comparison.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isFileValid(comparisonFile)).thenReturn(true);

            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(comparisonFile)).thenReturn(true);

            validationUtilities.when(() -> ValidationUtilities.isImageContentsValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isImageContentsValid(comparisonFile)).thenReturn(true);
            
            validationUtilities.when(() -> ValidationUtilities.areImageDimensionsValid(baselineFile, comparisonFile)).thenReturn(true);
            when(compareService.compare(baselineFile.getBytes(), comparisonFile.getBytes())).thenReturn(new byte[4]);

            //Act & Assert
            mockMvc.perform(multipart("/compare-direct").file(baselineFile).file(comparisonFile).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE));
        }
	}

    @Test
    @DisplayName("POST /compare-direct invalid baselineFile input is provided, returns 400 bad request")
	public void compareDirect_invalidBaselineFile_shouldReturn400BadRequest() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("baseline", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);
        MockMultipartFile comparisonFile = new MockMultipartFile("comparison", "comparison.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(false);

            //Act & Assert
            mockMvc.perform(multipart("/compare-direct").file(baselineFile).file(comparisonFile).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
                .andExpect(status().isBadRequest());
        }
	}

    @Test
    @DisplayName("POST /compare-direct invalid comparisonFile input is provided, returns 400 bad request")
	public void compareDirect_invalidComparisonFile_shouldReturn400BadRequest() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("baseline", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);
        MockMultipartFile comparisonFile = new MockMultipartFile("comparison", "comparison.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isFileValid(comparisonFile)).thenReturn(false);

            //Act & Assert
            mockMvc.perform(multipart("/compare-direct").file(baselineFile).file(comparisonFile).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
                .andExpect(status().isBadRequest());
        }
	}

    @Test
    @DisplayName("POST /compare-direct invalid baseline content type input is provided, returns 415 unsupported media type")
	public void compareDirect_invalidBaselineContentType_shouldReturn415UnsupportedMedia() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("baseline", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);
        MockMultipartFile comparisonFile = new MockMultipartFile("comparison", "comparison.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isFileValid(comparisonFile)).thenReturn(true);

            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(baselineFile)).thenReturn(false);

            //Act & Assert
            mockMvc.perform(multipart("/compare-direct").file(baselineFile).file(comparisonFile).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
                .andExpect(status().isUnsupportedMediaType());
        }
	}

    @Test
    @DisplayName("POST /compare-direct invalid comparison content type input is provided, returns 415 unsupported media type")
	public void compareDirect_invalidComparisonContentType_shouldReturn415UnsupportedMedia() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("baseline", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);
        MockMultipartFile comparisonFile = new MockMultipartFile("comparison", "comparison.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isFileValid(comparisonFile)).thenReturn(true);

            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(comparisonFile)).thenReturn(false);

            //Act & Assert
            mockMvc.perform(multipart("/compare-direct").file(baselineFile).file(comparisonFile).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
                .andExpect(status().isUnsupportedMediaType());
        } 
	}

    @Test
    @DisplayName("POST /compare-direct invalid baseline image contents input is provided, returns 400 bad request")
	public void compareDirect_invalidBaselineContents_shouldReturn400BadRequest() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("baseline", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);
        MockMultipartFile comparisonFile = new MockMultipartFile("comparison", "comparison.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isFileValid(comparisonFile)).thenReturn(true);

            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(comparisonFile)).thenReturn(true);

            validationUtilities.when(() -> ValidationUtilities.isImageContentsValid(baselineFile)).thenReturn(false);

            //Act & Assert
            mockMvc.perform(multipart("/compare-direct").file(baselineFile).file(comparisonFile).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
                .andExpect(status().isBadRequest());
        } 
	}

    @Test
    @DisplayName("POST /compare-direct invalid comparison image contents input is provided, returns 400 bad request")
	public void compareDirect_invalidComparisonContents_shouldReturn400BadRequest() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("baseline", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);
        MockMultipartFile comparisonFile = new MockMultipartFile("comparison", "comparison.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isFileValid(comparisonFile)).thenReturn(true);

            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(comparisonFile)).thenReturn(true);

            validationUtilities.when(() -> ValidationUtilities.isImageContentsValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isImageContentsValid(comparisonFile)).thenReturn(false);

            //Act & Assert
            mockMvc.perform(multipart("/compare-direct").file(baselineFile).file(comparisonFile).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
                .andExpect(status().isBadRequest());
        }
	}

    @Test
    @DisplayName("POST /compare-direct invalid image dimensions is provided, returns 400 bad request")
	public void compareDirect_invalidDimensions_shouldReturn400BadRequest() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("baseline", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);
        MockMultipartFile comparisonFile = new MockMultipartFile("comparison", "comparison.png", MediaType.IMAGE_PNG_VALUE, new byte[4]);

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isFileValid(comparisonFile)).thenReturn(true);

            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(comparisonFile)).thenReturn(true);

            validationUtilities.when(() -> ValidationUtilities.isImageContentsValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isImageContentsValid(comparisonFile)).thenReturn(true);

            validationUtilities.when(() -> ValidationUtilities.areImageDimensionsValid(baselineFile, comparisonFile)).thenReturn(false);

            //Act & Assert
            mockMvc.perform(multipart("/compare-direct").file(baselineFile).file(comparisonFile).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
                .andExpect(status().isBadRequest());
        }
	}
}
