package com.balazs.visual_diff.Blob;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.balazs.visual_diff.Compare.CompareController;
import com.balazs.visual_diff.Compare.CompareRequest;
import com.balazs.visual_diff.Exceptions.CorruptImageException;
import com.balazs.visual_diff.Exceptions.EmptyFileException;
import com.balazs.visual_diff.Exceptions.FilenameIsBlankException;
import com.balazs.visual_diff.Exceptions.GlobalExceptionHandler;
import com.balazs.visual_diff.Exceptions.InvalidTypeException;
import com.balazs.visual_diff.Exceptions.NotFoundException;
import com.balazs.visual_diff.Exceptions.UnsupportedMediaTypeException;
import com.balazs.visual_diff.Utilities.FileUtilities;
import com.balazs.visual_diff.Utilities.ValidationUtilities;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@RequestMapping("/files")
public class BlobController {
    
    @Autowired
    private BlobService blobService;

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Uploads a file to Azure Blob Storage. Will always overwrite existing files with the same name.
     *
     * @param MultipartFile file to be uploaded
     * @param String type of the file. 'baseline' or 'comparison'
     * @return ResponseEntity<BlobResponse>
     * @throws IOException
     * @throws InvalidTypeException
     * @throws EmptyFileException
     * @throws UnsupportedMediaTypeException
     * @throws CorruptImageException
     */
    @Tag(name = "Blob Controller", description = "Operations related to managing files on Azure Blob Storage. This includes uploading files, deleting files, retrieving files, and metadata.")
    @Operation(summary = "Upload a file to Azure Blob Storage.", description = "Uploads the incoming file to Azure Blob Storage if the 'file', and 'type' are valid. Will always overwrite existing files with the same name.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "The image was uploaded to Azure Blob Storage successfully."),
        @ApiResponse(responseCode = "400", description = "The RequestParam 'type' is not 'baseline' or 'comparison'."),
        @ApiResponse(responseCode = "400", description = "The RequestParam 'file' is empty or null."),
        @ApiResponse(responseCode = "400", description = "The RequestParam 'file' is corrupt or unable to be read."),
        @ApiResponse(responseCode = "413", description = "The RequestParam 'file' or 'type' is too large. Max 5MB size for the file, max 10MB size for the request."),
        @ApiResponse(responseCode = "415", description = "The RequestParam 'file' is not PNG or JPEG format.")
    })
    @Parameter(name = "file", description = "The 'file' to be uploaded. Must be PNG, or JPEG format, and must be 5MB or less.")
    @Parameter(name = "type", description = "The 'type' of the file to be uploaded. Must be 'baseline', or 'comparison'.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BlobResponse> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) throws IOException {        

        //Log start of request
        logger.info("Received POST /files/upload request for file: {} with type: {}", file.getOriginalFilename(), type);

        //Ensure type is either "baseline" or "comparison"
        if (!ValidationUtilities.isTypeValid(type)) { throw new InvalidTypeException(); }
        
        //Ensure file is not empty
        if (!ValidationUtilities.isFileValid(file)) { throw new EmptyFileException(); }

        //Ensure file is of PNG or JPEG format
        if (!ValidationUtilities.isContentTypeValid(file)) { throw new UnsupportedMediaTypeException(); }

        //Ensure file can be converted to a BufferedImage successfully
        if (!ValidationUtilities.isImageContentsValid(file)) { throw new CorruptImageException(); }

        //Save file to Blob Storage
        String format = FileUtilities.getFileFormatFromMultipartFile(file);
        String fileName = FileUtilities.generateFileName(format, type);
        byte[] fileData = file.getBytes();
        BlobResponse response = blobService.saveFile(fileData, fileName);

        //Log end of request
        logger.info("POST /files/upload successful on: {}", fileName);

        //Create the HATEOAS response with links to related actions
        response.add(linkTo(methodOn(BlobController.class).uploadFile(file, type)).withSelfRel().withType("POST"));
        response.add(linkTo(methodOn(BlobController.class).getFile(fileName)).withRel("get").withType("GET"));
        response.add(linkTo(methodOn(BlobController.class).getFileMetaData(fileName)).withRel("get").withType("GET"));
        response.add(linkTo(methodOn(BlobController.class).getAllFileMetaData()).withRel("get").withType("GET"));
        response.add(linkTo(methodOn(BlobController.class).deleteFile(fileName)).withRel("delete").withType("DELETE"));
        response.add(linkTo(methodOn(CompareController.class).compare(new CompareRequest(fileName, "file2.png"))).withRel("compare").withType("POST"));

        return ResponseEntity.created(URI.create("/files/" + fileName)).body(response);
	}

    /**
     * Retrieves a file from Azure Blob Storage if it exists.
     *
     * @param String fileName of the file to be retrieved
     * @return byte[] file data of the requested file
     * @throws FilenameIsBlankException
     * @throws NotFoundException
     */
    @Tag(name = "Blob Controller", description = "Operations related to managing files on Azure Blob Storage. This includes uploading files, deleting files, retrieving files, and metadata.")
    @Operation(summary = "Retrieve a file from Azure Blob Storage.", description = "Retreives a file from Azure Blob Storage with the incoming 'fileName' if it exists.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The image was found and returned successfully."),
        @ApiResponse(responseCode = "400", description = "The RequestParam 'file' is empty or null."),
        @ApiResponse(responseCode = "404", description = "The RequestParam 'file' does not exist on Azure Blob Storage.")
    })
    @Parameter(name = "fileName", description = "The 'fileName' to be retrieved from Azure Blob Storage.")
    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) {
        
        //Log start of request
        logger.info("Received GET /files/{fileName} request for file: {}", fileName);

        //Ensure provided fileName isn't blank
        if (fileName.isBlank()) { throw new FilenameIsBlankException(); }

        //Ensure file exists on Azure Blob Storage
        if (!blobService.doesFileExist(fileName)) { throw new NotFoundException(); }

        //Get image from azure Blob Storage
        byte[] fileData = blobService.getFile(fileName);

        //Log end of request
        logger.info("GET /files/{fileName} successful on: {}", fileName);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(fileData);
    }

    /**
     * Retrieves all file's metadata from Azure Blob Storage.
     *
     * @return ResponseEntity<CollectionModel<BlobResponse>> file metadata of all files
     * @throws IOException
     */
    @Tag(name = "Blob Controller", description = "Operations related to managing files on Azure Blob Storage. This includes uploading files, deleting files, retrieving files, and metadata.")
    @Operation(summary = "Retrieves all files' metadata from Azure Blob Storage.", description = "Retrieves all files' metadata from Azure Blob Storage. Metadata in this case is the 'fileName', 'type', 'size' in bytes, and 'creationTime'.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "All files' metadata found and returned successfully.")
    })
    @GetMapping("/metadata")
    public ResponseEntity<CollectionModel<BlobResponse>> getAllFileMetaData() throws IOException {
        
        //Log start of request
        logger.info("Received GET /files/metadata request for all files");

        //Get list of all files from Azure Blob Storage
        ArrayList<BlobResponse> responses = blobService.getAllFileProperties();

        //Log end of request
        logger.info("GET /files/metadata successful on all files");

        //Create the HATEOAS response with links to related actions
        for(BlobResponse response : responses) {
            response.add(linkTo(methodOn(BlobController.class).getAllFileMetaData()).withSelfRel().withType("GET"));
            response.add(linkTo(methodOn(BlobController.class).getFileMetaData(response.getFileName())).withRel("get").withType("GET"));
            response.add(linkTo(methodOn(BlobController.class).getFile(response.getFileName())).withRel("get").withType("GET"));
            response.add(linkTo(methodOn(BlobController.class).uploadFile(null, response.getType())).withRel("upload").withType("POST"));
            response.add(linkTo(methodOn(BlobController.class).deleteFile(response.getFileName())).withRel("delete").withType("DELETE"));
            response.add(linkTo(methodOn(CompareController.class).compare(new CompareRequest(response.getFileName(), "file2.png"))).withRel("compare").withType("POST"));
        }

        CollectionModel<BlobResponse> collectionModel = CollectionModel.of(responses);
        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Retrieves a file's metadata from Azure Blob Storage if it exists.
     *
     * @param String fileName of the file's metadata to be retrieved
     * @return ResponseEntity<BlobResponse> file metadata of the requested file
     * @throws IOException
     * @throws FilenameIsBlankException
     * @throws NotFoundException
     */
    @Tag(name = "Blob Controller", description = "Operations related to managing files on Azure Blob Storage. This includes uploading files, deleting files, retrieving files, and metadata.")
    @Operation(summary = "Retrieve a file's metadata from Azure Blob Storage.", description = "Retrieves a files' metadata with the incoming 'fileName' from Azure Blob Storage. Metadata in this case is the 'fileName', 'type', 'size' in bytes, and 'creationTime'.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The file's metadata found and returned successfully."),
        @ApiResponse(responseCode = "400", description = "The RequestParam 'fileName' is empty or null."),
        @ApiResponse(responseCode = "404", description = "The RequestParam 'fileName' does not exist on Azure Blob Storage.")
    })
    @Parameter(name = "fileName", description = "The 'fileName' to be retrieved from Azure Blob Storage with metadata.")
    @GetMapping("/metadata/{fileName}")
    public ResponseEntity<BlobResponse> getFileMetaData(@PathVariable String fileName) throws IOException {
        
        //Log start of request
        logger.info("Received GET /files/metadata/{fileName} request for file: {}", fileName);

        //Ensure provided fileName isn't blank
        if (fileName.isBlank()) { throw new FilenameIsBlankException(); }

        //Ensure file exists on Azure Blob Storage
        if (!blobService.doesFileExist(fileName)) { throw new NotFoundException(); }

        //Log end of request
        logger.info("GET /files/metadata/{fileName} successful on: {}", fileName);

        //Create the HATEOAS response with links to related actions
        BlobResponse response = blobService.getFileProperties(fileName);
        response.add(linkTo(methodOn(BlobController.class).getFileMetaData(fileName)).withSelfRel().withType("GET"));
        response.add(linkTo(methodOn(BlobController.class).getAllFileMetaData()).withRel("get").withType("GET"));
        response.add(linkTo(methodOn(BlobController.class).getFile(fileName)).withRel("get").withType("GET"));
        response.add(linkTo(methodOn(BlobController.class).uploadFile(null, response.getType())).withRel("upload").withType("POST"));
        response.add(linkTo(methodOn(BlobController.class).deleteFile(fileName)).withRel("delete").withType("DELETE"));
        response.add(linkTo(methodOn(CompareController.class).compare(new CompareRequest(fileName, "file2.png"))).withRel("compare").withType("POST"));

        return ResponseEntity.ok().body(response);
    }

    /**
     * Deletes a file from Azure Blob Storage if it exists.
     *
     * @param String fileName of the file to be deleted
     * @return ResponseEntity<BlobResponse>
     * @throws FilenameIsBlankException
     * @throws NotFoundException
     * @throws IOException
     */
    @Tag(name = "Blob Controller", description = "Operations related to managing files on Azure Blob Storage. This includes uploading files, deleting files, retrieving files, and metadata.")
    @Operation(summary = "Deletes a file from Azure Blob Storage.", description = "Deletes a file from Azure Blob Storage with the incoming 'fileName' if it exists.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The image was found and deleted successfully."),
        @ApiResponse(responseCode = "400", description = "The RequestParam 'fileName' is empty or null."),
        @ApiResponse(responseCode = "404", description = "The RequestParam 'fileName' does not exist on Azure Blob Storage.")
    })
    @Parameter(name = "fileName", description = "The 'fileName' to be deleted from Azure Blob Storage.")
    @DeleteMapping("/{fileName}")
    public ResponseEntity<BlobResponse> deleteFile(@PathVariable String fileName) throws IOException {

        //Log start of request
        logger.info("Received DELETE /files/{fileName} request for file: {}", fileName);

        //Ensure provided fileName isn't blank
        if (fileName.isBlank()) { throw new FilenameIsBlankException(); }

        //Ensure file exists on Azure Blob Storage
        if (!blobService.doesFileExist(fileName)) { throw new NotFoundException(); }
        
        //Delete file from azure Blob Storage
        BlobResponse response = blobService.deleteFile(fileName);

        //Log end of request
        logger.info("DELETE /files/{fileName} successful on: {}", fileName);

        //Create the HATEOAS response with links to related actions
        response.add(linkTo(methodOn(BlobController.class).deleteFile(fileName)).withSelfRel().withType("DELETE"));
        response.add(linkTo(methodOn(BlobController.class).uploadFile(null, response.getType())).withRel("upload").withType("POST"));
        response.add(linkTo(methodOn(BlobController.class).getFile(fileName)).withRel("get").withType("GET"));
        response.add(linkTo(methodOn(BlobController.class).getFileMetaData(fileName)).withRel("get").withType("GET"));
        response.add(linkTo(methodOn(BlobController.class).getAllFileMetaData()).withRel("get").withType("GET"));
        response.add(linkTo(methodOn(CompareController.class).compare(new CompareRequest(fileName, "file2.png"))).withRel("compare").withType("POST"));

        return ResponseEntity.ok().body(response);
    }
}
