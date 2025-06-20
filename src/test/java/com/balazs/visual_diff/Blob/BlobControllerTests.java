package com.balazs.visual_diff.Blob;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.ArrayList;

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

import com.balazs.visual_diff.Utilities.FileUtilities;
import com.balazs.visual_diff.Utilities.ValidationUtilities;

@WithMockUser
@WebMvcTest(BlobController.class)
public class BlobControllerTests {
    private final static String PREFIX = "http://localhost";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlobService blobService;

    @Test
    @DisplayName("POST /upload valid input is provided, returns 201 created")
	public void uploadFile_validInput_shouldReturn201Created() throws Exception {
        //Arrange
        String expectedFileName = "baseline.png";
        String expectedType = "baseline";
        String expectedSize = "724171";
        String expectedTime = "2025-06-15T14:36:00Z";
        MockMultipartFile baselineFile = new MockMultipartFile("file", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[]{(byte)255, (byte)0, (byte)0, (byte)255});
        BlobResponse expectedResponse = new BlobResponse(expectedFileName, expectedType, expectedSize, expectedTime);

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isTypeValid(expectedType)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isImageContentsValid(baselineFile)).thenReturn(true);

            try (MockedStatic<FileUtilities> fileUtilities = mockStatic(FileUtilities.class)) {
                fileUtilities.when(() -> FileUtilities.generateFileName("png", "baseline")).thenReturn(expectedFileName);
                fileUtilities.when(() -> FileUtilities.getFileFormatFromMultipartFile(baselineFile)).thenReturn("png");
                when(blobService.saveFile(baselineFile.getBytes(), expectedFileName)).thenReturn(expectedResponse);

                //Act & Assert
                mockMvc.perform(multipart("/files/upload").file(baselineFile).param("type", expectedType).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.fileName").value(expectedFileName))
                    .andExpect(jsonPath("$.type").value(expectedType))
                    .andExpect(jsonPath("$.size").value(expectedSize))
                    .andExpect(jsonPath("$.creationTime").value(expectedTime))
                    .andExpect(jsonPath("$._links.self.href").value(PREFIX + "/files/upload?type=" + expectedType))
                    .andExpect(jsonPath("$._links.get[0].href").value(PREFIX + "/files/" + expectedFileName))
                    .andExpect(jsonPath("$._links.get[1].href").value(PREFIX + "/files/metadata/" + expectedFileName))
                    .andExpect(jsonPath("$._links.get[2].href").value(PREFIX + "/files/metadata"))
                    .andExpect(jsonPath("$._links.delete.href").value(PREFIX + "/files/" + expectedFileName))
                    .andExpect(jsonPath("$._links.compare.href").value(PREFIX + "/compare"));
            }
        }
	}

    @Test
    @DisplayName("POST /upload invalid type is provided, returns 400 bad request")
	public void uploadFile_invalidType_shouldReturn400BadRequest() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("file", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[]{(byte)255, (byte)0, (byte)0, (byte)255});
        String expectedType = "baseline";

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isTypeValid(expectedType)).thenReturn(false);
            
            //Act & Assert
            mockMvc.perform(multipart("/files/upload").file(baselineFile).param("type", expectedType).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
            .andExpect(status().isBadRequest());
        }
	}

    @Test
    @DisplayName("POST /upload invalid file is provided, returns 400 bad request")
	public void uploadFile_invalidFile_shouldReturn400BadRequest() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("file", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[]{(byte)255, (byte)0, (byte)0, (byte)255});
        String expectedType = "baseline";

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isTypeValid(expectedType)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(false);

            //Act & Assert
            mockMvc.perform(multipart("/files/upload").file(baselineFile).param("type", expectedType).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
            .andExpect(status().isBadRequest());
        }
	}

    @Test
    @DisplayName("POST /upload invalid content type is provided, returns 415 unsupported media type")
	public void uploadFile_invalidContentType_shouldReturn415UnsupportedMedia() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("file", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[]{(byte)255, (byte)0, (byte)0, (byte)255});
        String expectedType = "baseline";

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isTypeValid(expectedType)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(baselineFile)).thenReturn(false);

            //Act & Assert
            mockMvc.perform(multipart("/files/upload").file(baselineFile).param("type", expectedType).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
            .andExpect(status().isUnsupportedMediaType());
        }
	}

    @Test
    @DisplayName("POST /upload invalid file contents is provided, returns 400 bad request")
	public void uploadFile_invalidFileContents_shouldReturn400BadRequest() throws Exception {
        //Arrange
        MockMultipartFile baselineFile = new MockMultipartFile("file", "baseline.png", MediaType.IMAGE_PNG_VALUE, new byte[]{(byte)255, (byte)0, (byte)0, (byte)255});
        String expectedType = "baseline";

        //Static mock for validation utilities similar to ValidationUtiltiiesTests
        try (MockedStatic<ValidationUtilities> validationUtilities = mockStatic(ValidationUtilities.class)) {
            validationUtilities.when(() -> ValidationUtilities.isTypeValid(expectedType)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isFileValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isContentTypeValid(baselineFile)).thenReturn(true);
            validationUtilities.when(() -> ValidationUtilities.isImageContentsValid(baselineFile)).thenReturn(false);

            //Act & Assert
            mockMvc.perform(multipart("/files/upload").file(baselineFile).param("type", expectedType).contentType(MediaType.MULTIPART_FORM_DATA_VALUE).with(csrf()))
            .andExpect(status().isBadRequest());
        }
	}

    @Test
    @DisplayName("GET /{fileName} valid input is provided, returns 200 ok")
	public void getFile_validInput_shouldReturn200Ok() throws Exception {
        //Arrange
        String expectedFileName = "baseline.png";

        when(blobService.doesFileExist(expectedFileName)).thenReturn(true);
        when(blobService.getFile(expectedFileName)).thenReturn(new byte[4]);

        //Act & Assert
        mockMvc.perform(get("/files/{fileName}", expectedFileName))
            .andExpect(status().isOk())
            .andExpect(content().bytes(new byte[4]));     
	}

    @Test
    @DisplayName("GET /{fileName} blank fileName is provided, returns 400 bad request")
	public void getFile_blankFileName_shouldReturn400BadRequest() throws Exception {
        //Arrange
        String expectedFileName = " ";

        //Act & Assert
        mockMvc.perform(get("/files/{fileName}", expectedFileName))
            .andExpect(status().isBadRequest());    
	}

    @Test
    @DisplayName("GET /{fileName} file does not exist, returns 404 not found")
	public void getFile_fileDoesNotExist_shouldReturn404NotFound() throws Exception {
        //Arrange
        String expectedFileName = "baseline.png";

        when(blobService.doesFileExist(expectedFileName)).thenReturn(false);

        //Act & Assert
        mockMvc.perform(get("/files/{fileName}", expectedFileName))
            .andExpect(status().isNotFound());    
	}

    @Test
    @DisplayName("GET /metadata valid input is provided, returns 200 ok")
	public void getAllFileMetaData_validInput_shouldReturn200Ok() throws Exception {
        //Arrange
        String expectedFileName = "baseline.png";
        String expectedType = "baseline";
        String expectedSize = "724171";
        String expectedTime = "2025-06-15T14:36:00Z";
        ArrayList<BlobResponse> expectedResponses = new ArrayList<BlobResponse>();
        expectedResponses.add(new BlobResponse(expectedFileName, expectedType, expectedSize, expectedTime));

        when(blobService.getAllFileProperties()).thenReturn(expectedResponses);

        try (MockedStatic<FileUtilities> fileUtilities = mockStatic(FileUtilities.class)) {
            fileUtilities.when(() -> FileUtilities.getTypeFromFileName(expectedResponses.getFirst().getFileName())).thenReturn(expectedType);
            
            //Act & Assert
            mockMvc.perform(get("/files/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.blobResponseList[0].fileName").value(expectedFileName))
                .andExpect(jsonPath("$._embedded.blobResponseList[0].type").value(expectedType))
                .andExpect(jsonPath("$._embedded.blobResponseList[0].size").value(expectedSize))
                .andExpect(jsonPath("$._embedded.blobResponseList[0].creationTime").value(expectedTime))
                .andExpect(jsonPath("$._embedded.blobResponseList[0]._links.self.href").value(PREFIX + "/files/metadata"))
                .andExpect(jsonPath("$._embedded.blobResponseList[0]._links.get[0].href").value(PREFIX + "/files/metadata/" + expectedFileName))
                .andExpect(jsonPath("$._embedded.blobResponseList[0]._links.get[1].href").value(PREFIX + "/files/" + expectedFileName))
                .andExpect(jsonPath("$._embedded.blobResponseList[0]._links.upload.href").value(PREFIX + "/files/upload?type=" + expectedType))
                .andExpect(jsonPath("$._embedded.blobResponseList[0]._links.delete.href").value(PREFIX + "/files/" + expectedFileName))
                .andExpect(jsonPath("$._embedded.blobResponseList[0]._links.compare.href").value(PREFIX + "/compare"));
        } 
	}

    @Test
    @DisplayName("GET /metadata/{fileName} valid input is provided, returns 200 ok")
	public void getFileMetaData_validInput_shouldReturn200Ok() throws Exception {
        //Arrange
        String expectedFileName = "baseline.png";
        String expectedType = "baseline";
        String expectedSize = "724171";
        String expectedTime = "2025-06-15T14:36:00Z";
        BlobResponse expectedResponse = new BlobResponse(expectedFileName, expectedType, expectedSize, expectedTime);

        when(blobService.doesFileExist(expectedFileName)).thenReturn(true);
        when(blobService.getFileProperties(expectedFileName)).thenReturn(expectedResponse);

        try (MockedStatic<FileUtilities> fileUtilities = mockStatic(FileUtilities.class)) {
            fileUtilities.when(() -> FileUtilities.getTypeFromFileName(expectedFileName)).thenReturn(expectedType);
            
            //Act & Assert
            mockMvc.perform(get("/files/metadata/{fileName}", expectedFileName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(expectedFileName))
                .andExpect(jsonPath("$.type").value(expectedType))
                .andExpect(jsonPath("$.size").value(expectedSize))
                .andExpect(jsonPath("$.creationTime").value(expectedTime))
                .andExpect(jsonPath("$._links.self.href").value(PREFIX + "/files/metadata/" + expectedFileName))
                .andExpect(jsonPath("$._links.get[0].href").value(PREFIX + "/files/metadata"))
                .andExpect(jsonPath("$._links.get[1].href").value(PREFIX + "/files/" + expectedFileName))
                .andExpect(jsonPath("$._links.upload.href").value(PREFIX + "/files/upload?type=" + expectedType))
                .andExpect(jsonPath("$._links.delete.href").value(PREFIX + "/files/" + expectedFileName))
                .andExpect(jsonPath("$._links.compare.href").value(PREFIX + "/compare"));
        } 
	}

    @Test
    @DisplayName("GET /metadata/{fileName} file does not exist, returns 404 not found")
	public void getFileMetaData_fileDoesNotExist_shouldReturn404NotFound() throws Exception {
        //Arrange
        String expectedFileName = "baseline.png";

        when(blobService.doesFileExist(expectedFileName)).thenReturn(false);

        //Act & Assert
        mockMvc.perform(get("/files/metadata/{fileName}", expectedFileName))
            .andExpect(status().isNotFound());
	}

    @Test
    @DisplayName("DELETE /{fileName} valid input is provided, returns 200 ok")
	public void deleteFile_validInput_shouldReturn200Ok() throws Exception {
        //Arrange
        String expectedFileName = "baseline.png";
        String expectedType = "baseline";
        String expectedSize = "724171";
        String expectedTime = "2025-06-15T14:36:00Z";
        BlobResponse expectedResponse = new BlobResponse(expectedFileName, expectedType, expectedSize, expectedTime);

        when(blobService.doesFileExist(expectedFileName)).thenReturn(true);
        when(blobService.deleteFile(expectedFileName)).thenReturn(expectedResponse);

        try (MockedStatic<FileUtilities> fileUtilities = mockStatic(FileUtilities.class)) {
            fileUtilities.when(() -> FileUtilities.getTypeFromFileName(expectedFileName)).thenReturn(expectedType);
            
            //Act & Assert
            mockMvc.perform(delete("/files/{fileName}", expectedFileName).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value(expectedFileName))
                .andExpect(jsonPath("$.type").value(expectedType))
                .andExpect(jsonPath("$.size").value(expectedSize))
                .andExpect(jsonPath("$.creationTime").value(expectedTime))
                .andExpect(jsonPath("$._links.self.href").value(PREFIX + "/files/" + expectedFileName))
                .andExpect(jsonPath("$._links.upload.href").value(PREFIX + "/files/upload?type=" + expectedType))
                .andExpect(jsonPath("$._links.get[0].href").value(PREFIX + "/files/" + expectedFileName))
                .andExpect(jsonPath("$._links.get[1].href").value(PREFIX + "/files/metadata/" + expectedFileName))
                .andExpect(jsonPath("$._links.get[2].href").value(PREFIX + "/files/metadata"))
                .andExpect(jsonPath("$._links.compare.href").value(PREFIX + "/compare"));
        } 
	}

    @Test
    @DisplayName("DELETE /{fileName} file does not exist, returns 404 not found")
	public void deleteFile_fileDoesNotExist_shouldReturn404NotFound() throws Exception {
        //Arrange
        String expectedFileName = "baseline.png";

        when(blobService.doesFileExist(expectedFileName)).thenReturn(false);

        //Act & Assert
        mockMvc.perform(delete("/files/{fileName}", expectedFileName).with(csrf()))
            .andExpect(status().isNotFound());
	}
}
