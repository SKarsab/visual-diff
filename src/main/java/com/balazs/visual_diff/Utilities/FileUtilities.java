package com.balazs.visual_diff.Utilities;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public final class FileUtilities {
    
    public final static String FORMAT_PNG = "png";
    public final static String FORMAT_JPEG = "jpeg";

    private FileUtilities() { }

    /**
     * Converts incoming BufferedImage to byte[] array.
     *
     * @param BufferedImage image to be converted
     * @param String format either FORMAT_PNG or FORMAT_JPEG
     * @return byte[] byte array of the BufferedImage
     * @throws IOException
     */
    public static byte[] convertBufferedImageToByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Gets the file format from the MultipartFile's content type.
     *
     * @param MultipartFile file to get contentType from
     * @return String format either FORMAT_PNG or FORMAT_JPEG based on the content type
     * @throws IOException
     */
    public static String getFileFormatFromMultipartFile(MultipartFile file) {
        if (file.getContentType().equals(MediaType.IMAGE_PNG_VALUE)) {
            return FileUtilities.FORMAT_PNG;
        }
        else if (file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)) {
            return FileUtilities.FORMAT_JPEG;
        }
        else {
            return null;
        }
    }

    /**
     * Generates a file name to be uploaded to Azure Blob Storage in the format of [type]_[timestamp]_[UUID].[format].
     *
     * @param String format either FORMAT_PNG or FORMAT_JPEG
     * @param String type either "baseline", "comparison" or 'diff'
     * @return String file name in the format of [type]_[timestamp]_[UUID].[format].
     * @throws IOException
     */
    public static String generateFileName(String format, String type) {
        return type + "_" + UUID.randomUUID().toString() + "." + format;
    }

    /**
     * Gets the type of the file uploaded, either 'baseline', 'comparison', or 'diff'
     *
     * @param String fileName of the incoming file
     * @return String type of the file either 'baseline', 'comparison', or 'diff'
     */
    public static String getTypeFromFileName(String fileName) {
        if (fileName.toLowerCase().startsWith(ValidationUtilities.TYPE_BASELINE)) { return ValidationUtilities.TYPE_BASELINE; }
        if (fileName.toLowerCase().startsWith(ValidationUtilities.TYPE_COMPARISON)) { return ValidationUtilities.TYPE_COMPARISON; }
        
        return ValidationUtilities.TYPE_DIFF;
    }
}
