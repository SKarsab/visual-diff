package com.balazs.visual_diff.Blob;

import org.springframework.hateoas.RepresentationModel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BlobResponse", description = "This response object is returned for all endpoints in the BlobController. It includes a fileName, type, size, creation time, and related links for discovery. A list of this object will be returned for the /files/metadata endpoint.")
public class BlobResponse extends RepresentationModel<BlobResponse> {
    
    @Schema(description = "The 'fileName' of the file uploaded, retrieved, or deleted. FileName will be in the format [type]\\_[UUID].[format]", example = "baseline_0a862592-3fc2-46a7-b74a-65261c5dec6e.png")
    private String fileName;

    @Schema(description = "The 'type' of the file uploaded, retrieved, or deleted. Type will be 'baseline', 'comparison', or 'diff'.", example = "baseline")
    private String type;

    @Schema(description = "The 'size' of the file uploaded, retrieved, or deleted. Size will be in bytes.", example = "724171")
    private String size;

    @Schema(description = "The 'creationtime' of the file. Time will be in the format [yyyy-MM-ddHH:mm:ss]", example = "2025-06-15T14:36:00Z")
    private String creationTime;

    public BlobResponse() { }

    public BlobResponse(String newFileName, String newType, String newSize, String newCreationTime) {
        fileName = newFileName;
        type = newType;
        size = newSize;
        creationTime = newCreationTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String newFileName) {
        fileName = newFileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String newType) {
        type = newType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String newSize) {
        size = newSize;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String newCreationTime) {
        creationTime = newCreationTime;
    }
}
