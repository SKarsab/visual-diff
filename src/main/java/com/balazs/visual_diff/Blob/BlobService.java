package com.balazs.visual_diff.Blob;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.balazs.visual_diff.Utilities.FileUtilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class BlobService {

    private final BlobContainerClient containerClient;

    public BlobService(@Value("${spring.cloud.azure.storage.blob.container-name}") String containerName, BlobServiceClient blobServiceClient) {
        this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
    }

    /**
     * Saves the incoming file to Azure Blob Storage.
     *
     * @param byte[] fileData of the desired file to be saved to blob storage
     * @param String fileName of the file to be saved
     * @return BlobResponse with the files's information
     * @throws IOException
     */
    public BlobResponse saveFile(byte[] fileData, String fileName) throws IOException {
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
        blobClient.upload(inputStream, fileData.length, true);
        inputStream.close();
        return buildBlobResponse(blobClient);
    }

    /**
     * Retrieves a file from Azure blob storage if it exists.
     *
     * @param String fileName of the file to be retrieved
     * @return byte[] fileData of the requested file
     */
    public byte[] getFile(String fileName) {
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        byte[] fileData = blobClient.downloadContent().toBytes();
        return fileData;
    }

    /**
     * Retrieves the file's metadata: file name, type, size, and creation time
     *
     * @param String fileName of the file to be saved
     * @return BlobResponse with the files's information
     */
    public BlobResponse getFileProperties(String fileName) {
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        return buildBlobResponse(blobClient);
    }

    /**
     * Retrieves a list of files' metadata: file name, type, size, and creation time
     *
     * @return ArrayList<BlobResponse> of all file's present on Azure Blob Storage
     */
    public ArrayList<BlobResponse> getAllFileProperties() {
        ArrayList<BlobResponse> responses = new ArrayList<BlobResponse>();
        containerClient.listBlobs().forEach(blobItem -> {
            responses.add(getFileProperties(blobItem.getName()));
        });

        return responses;
    }

    /**
     * Deletes a file from Azure blob storage if it exists.
     *
     * @param String fileName of the file to be deleted
     * @return BlobResponse with the files's information
     */
    public BlobResponse deleteFile(String fileName) {
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        BlobResponse response = buildBlobResponse(blobClient);
        blobClient.delete();
        return response;
    }

    /**
     * Checks if the file exists on Azure Blob Storage
     *
     * @param String fileName of the file to be checked if it exists
     * @return boolean false if file does not exist, true if file exists
     */
    public boolean doesFileExist(String fileName) {
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        if (!blobClient.exists()) { return false;}
        return true;
    }

    /**
     * Verifies connectivity to Azure Blob Storage.
     *
     * @return boolean false if cannot connect to Azure, true if can
     */
    public boolean healthCheckAzure() {
        return containerClient.exists();
    }

    /**
     * Builds a BlobResponse object to be returned tot he calling controller. BlobResponse incldues 
     * the file's metadata: file name, type, size, and creation time if it exists.
     *
     * @param BlobClient blobClient
     * @return BlobResponse response will name, type, size, and creation time populated
     */
    public BlobResponse buildBlobResponse(BlobClient blobClient) {
        BlobResponse response = new BlobResponse();
        response.setFileName(blobClient.getBlobName());
        response.setType(FileUtilities.getTypeFromFileName(blobClient.getBlobName()));
        response.setSize(Long.toString(blobClient.getProperties().getBlobSize()));
        response.setCreationTime(blobClient.getProperties().getCreationTime().atZoneSameInstant(ZoneId.of("America/New_York")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return response;
    }
}
