package com.balazs.visual_diff.Compare;

import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.balazs.visual_diff.Blob.BlobController;
import com.balazs.visual_diff.Blob.BlobService;
import com.balazs.visual_diff.Exceptions.CorruptImageException;
import com.balazs.visual_diff.Exceptions.DimensionMismatchException;
import com.balazs.visual_diff.Exceptions.EmptyFileException;
import com.balazs.visual_diff.Exceptions.GlobalExceptionHandler;
import com.balazs.visual_diff.Exceptions.NotFoundException;
import com.balazs.visual_diff.Exceptions.UnsupportedMediaTypeException;
import com.balazs.visual_diff.Utilities.FileUtilities;
import com.balazs.visual_diff.Utilities.ValidationUtilities;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@CrossOrigin
public class CompareController {
    
    @Autowired
    private CompareService compareService;

    @Autowired
    private BlobService blobService;

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Compares two images directly from the request body without using Azure Blob Storage.
     *
     * @param MultipartFile baselineFile file to be compared against
     * @param MultipartFile comparisonFile to compare against baseline file
     * @return ResponseEntity<byte[]> diffData of the image as a byte array
     * @throws IOException
     * @throws EmptyFileException
     * @throws UnsupportedMediaTypeException
     * @throws CorruptImageException
     * @throws DimensionMismatchException
     */
    @Tag(name = "Compare Controller", description = "Operations related to comparing images, and producing diffs. Diffs will be converted to YIQ colour space with a heavier weight placed on the luminance channel (Similar to gray scale, but lighter, but with colour channel information). Incoming baseline image, and comparison image will be compared pixel by pixel. Diffs past a threshold will be highlighted with red.")
    @Operation(summary = "Compare two images directly and return a diff.", description = "Directly compare two incoming images, 'baseline', and 'comparison' and return a diff. Nothing is saved to Azure, and nothing is accessed from Azure.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The incoming images were compared successfully and the diff image was returned."),
        @ApiResponse(responseCode = "400", description = "The RequestParam 'baseline', or 'comparison' is empty or null."),
        @ApiResponse(responseCode = "400", description = "The RequestParam 'baseline', or 'comparison' is corrupt or unable to be read."),
        @ApiResponse(responseCode = "400", description = "The RequestParam 'baseline', and 'comparison' have different dimensions."),
        @ApiResponse(responseCode = "413", description = "The RequestParam 'baseline' or 'comparison' is too large. Max 5MB size for each file, max 10MB size for the request."),
        @ApiResponse(responseCode = "415", description = "The RequestParam 'baseline' or 'comparison' is not PNG or JPEG format.")
    })
    @Parameter(name = "baseline", description = "The baseline image to be compared against. Must be PNG, or JPEG format. Must be the same dimensions as the comparison image and must be 5MB or less.")
    @Parameter(name = "comparison", description = "The comparison image to be compare against. Must be PNG, or JPEG format. Must be the same dimensions as the comparison image and must be 5MB or less.")
    @PostMapping(value = "/compare-direct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE })
	public ResponseEntity<byte[]> compareDirect(@RequestParam("baseline") MultipartFile baselineFile, @RequestParam("comparison") MultipartFile comparisonFile) throws IOException {        
        
        //Log start of request
        logger.info("Received POST /compare-direct request for files: {}, and {}", baselineFile.getOriginalFilename(), comparisonFile.getOriginalFilename());

        //Ensure file is not empty
        if (!ValidationUtilities.isFileValid(baselineFile)) { throw new EmptyFileException(); }
        if (!ValidationUtilities.isFileValid(comparisonFile)) { throw new EmptyFileException(); }

        //Ensure file is of PNG or JPEG format
        if (!ValidationUtilities.isContentTypeValid(baselineFile)) { throw new UnsupportedMediaTypeException(); }
        if (!ValidationUtilities.isContentTypeValid(comparisonFile)) { throw new UnsupportedMediaTypeException(); }

        //Ensure file can be converted to a BufferedImage successfully
        if (!ValidationUtilities.isImageContentsValid(baselineFile)) { throw new CorruptImageException(); }
        if (!ValidationUtilities.isImageContentsValid(comparisonFile)) { throw new CorruptImageException(); }

        //Ensure both baseline and comparison fileshave the same dimensions for comparison
        if (!ValidationUtilities.areImageDimensionsValid(baselineFile, comparisonFile)) { throw new DimensionMismatchException(); }

        //Compare baseline and comparison file. Return as byte array
        byte[] baselineData = baselineFile.getBytes();
        byte[] comparisonData = comparisonFile.getBytes();
        byte[] diffData = compareService.compare(baselineData, comparisonData);

        //Log end of request
        logger.info("POST /compare-direct successful on: {}, and {}", baselineFile.getOriginalFilename(), comparisonFile.getOriginalFilename());

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(diffData);
	}

    /**
     * Compares two images from Azure Blob Storage and saves the diff image to Azure Blob Storage.
     *
     * @param CompareRequest request containing the name of the baseline file and comparison file
     * @return ResponseEntity<CompareResponse>
     * @throws IOException
     */
    @Tag(name = "Compare Controller", description = "Operations related to comparing images, and producing diffs. Diffs will be converted to YIQ colour space with a heavier weight placed on the luminance channel (Similar to gray scale, but lighter, but with colour channel information). Incoming baseline image, and comparison image will be compared pixel by pixel. Diffs past a threshold will be highlighted with red.")
    @Operation(summary = "Compare two images on Azure Blob Storage.", description = "Compare two images from incoming 'baselineFileName', and 'comparisonFileName' if they exist on Azure Blob Storage. The diff is saved to Azure Blob Storage.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "The incoming 'baselineFileName', and 'comparisonFileName' were retrieved from Azure, compared successfully and the diff image was saved to Azure Blob Storage."),
        @ApiResponse(responseCode = "400", description = "The RequestParam 'baselineFileName', or 'comparisonFileName' is empty or null."),
        @ApiResponse(responseCode = "404", description = "The RequestParam 'baselineFileName', or 'comparisonFileName' does not exist on Azure Blob Storage.")
    })
    @Parameter(name = "request", description = "Contains 'baselineFileName', and 'comparisonFileName' to be retrieved from Azure, compared, and diff saved back to Azure. Must not be blank, or null.")
    @PostMapping(value = "/compare", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompareResponse> compare(@Valid @RequestBody CompareRequest request) throws IOException {        
        
        String baselineFileName = request.getBaseline();
        String comparisonFileName = request.getComparison();

        //Log start of request
        logger.info("Received POST /compare request for files: {}, and {}", baselineFileName, comparisonFileName);

        //Ensure files exists on Azure Blob Storage
        if (!blobService.doesFileExist(baselineFileName)) { throw new NotFoundException(); }
        if (!blobService.doesFileExist(comparisonFileName)) { throw new NotFoundException(); }

        //Compare the baseline and comparison images, upload the diff image to blob storage, and return the diff file name
        byte[] baselineData = blobService.getFile(baselineFileName);
        byte[] comparisonData = blobService.getFile(comparisonFileName);
        byte[] diffData = compareService.compare(baselineData, comparisonData);
        String fileName = FileUtilities.generateFileName(FileUtilities.FORMAT_PNG, ValidationUtilities.TYPE_DIFF);
        blobService.saveFile(diffData, fileName);

        //Log end of request
        logger.info("POST /compare successful on: {}, and {}, produced: {}", baselineFileName, comparisonFileName, fileName);

        //Create the HATEOAS response with links to related actions
        CompareResponse response = new CompareResponse(baselineFileName, comparisonFileName, fileName);
        response.add(linkTo(methodOn(CompareController.class).compare(new CompareRequest(fileName, "file2.png"))).withSelfRel().withType("POST"));
        response.add(linkTo(methodOn(BlobController.class).getFile(fileName)).withRel("get").withType("GET"));
        response.add(linkTo(methodOn(BlobController.class).getFileMetaData(fileName)).withRel("get").withType("GET"));
        response.add(linkTo(methodOn(BlobController.class).getAllFileMetaData()).withRel("get").withType("GET"));
        response.add(linkTo(methodOn(BlobController.class).deleteFile(fileName)).withRel("delete").withType("DELETE"));

        return ResponseEntity.created(URI.create("/files/" + fileName)).body(response);
	}
}
