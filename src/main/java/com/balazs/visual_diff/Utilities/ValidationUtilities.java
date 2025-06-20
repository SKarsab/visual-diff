package com.balazs.visual_diff.Utilities;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public final class ValidationUtilities {

    public final static String TYPE_BASELINE = "baseline";
    public final static String TYPE_COMPARISON = "comparison";
    public final static String TYPE_DIFF = "diff";

    private ValidationUtilities() { }

    /**
     * Checks if the incoming type is equalsIgnoreCase to the accepted types 'baseline' or 'comparison' or null
     *
     * @param String type from the controller request
     * @return boolean false if type is invalid, true if type is valid
     */
    public static boolean isTypeValid(String type) {
        if (type == null) { return false; }
        else if (type.isBlank()) { return false; }
        else if (!type.equalsIgnoreCase(TYPE_BASELINE) && !type.equalsIgnoreCase(TYPE_COMPARISON)) { return false; }

        return true;
    }

    /**
     * Checks if the incoming file is isEmpty or null
     *
     * @param MultipartFile file from the controller request
     * @return boolean false if file is empty/invalid, true if file is not empty/valid
     * @throws IOException
     */
    public static boolean isFileValid(MultipartFile file) throws IOException {
        if (file == null) { return false; }
        else if (file.isEmpty()) { return false; }

        return true;
    }

    /**
     * Checks if the incoming file's content type is either 'image/png' or 'image/jpeg'.
     *
     * @param MultipartFile file from the controller request
     * @return boolean false if file is not 'image/png' or 'image/jpeg', true if file is
     * @throws IOException
     */
    public static boolean isContentTypeValid(MultipartFile file) throws IOException {
        if (file == null) { return false; }
        else if (file.getContentType() == null) { return false; }
        else if (!file.getContentType().equals(MediaType.IMAGE_PNG_VALUE) && !file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the incoming file can be read as an image and converted to a BufferedImage successfully.
     *
     * @param MultipartFile file from the controller request
     * @return boolean false if file was unsuccessfully converted to BufferedImage, true if file was successfully converted
     * @throws IOException
     */
    public static boolean isImageContentsValid(MultipartFile file) throws IOException {
        if (file == null) { return false; }

        InputStream inputStream = file.getInputStream();
        BufferedImage image = ImageIO.read(inputStream);
        inputStream.close();

        if (image == null) { return false; }
        return true;
    }

    /**
     * Checks if the incoming baseline and comparison images have the same dimensions.
     *
     * @param MultipartFile baselineFile from the controller request
     * @param MultipartFile comparisonFile from the controller request
     * @return boolean false if images have different dimensions, true if they have the same dimensions
     * @throws IOException
     */
    public static boolean areImageDimensionsValid (MultipartFile baselineFile, MultipartFile comparisonFile) throws IOException {
        if (baselineFile == null) { return false; }
        else if (comparisonFile == null) { return false; }
        
        InputStream baselineInputStream = baselineFile.getInputStream();
        BufferedImage baselineImg = ImageIO.read(baselineInputStream);
        baselineInputStream.close();

        InputStream comparisonInputStream = comparisonFile.getInputStream();
        BufferedImage comparisonImg = ImageIO.read(comparisonInputStream);
        comparisonInputStream.close();

        if (baselineImg.getWidth() != comparisonImg.getWidth() || baselineImg.getHeight() != comparisonImg.getHeight()) {
            return false;
        }

        return true;
    }
}
