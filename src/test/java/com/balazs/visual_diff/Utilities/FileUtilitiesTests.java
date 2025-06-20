package com.balazs.visual_diff.Utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public class FileUtilitiesTests {
    @Test
    @DisplayName("Valid BufferedImage and png format is provided, returns byte array")
    public void convertBufferedImageToByteArray_formatPng_shouldReturnNonEmptyByteArray() throws IOException {
        //Arrange
        BufferedImage bufferedImage = mock(BufferedImage.class);
        String format = FileUtilities.FORMAT_PNG;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(0);

        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.write(bufferedImage, format, byteArrayOutputStream)).thenReturn(true);

            //Act
            byte[] actualByteArray = FileUtilities.convertBufferedImageToByteArray(bufferedImage, format);

            //Assert
            assertNotNull(actualByteArray);
        }
    }

    @Test
    @DisplayName("Valid BufferedImage and jpeg format is provided, returns byte array")
    public void convertBufferedImageToByteArray_formatJpeg_shouldReturnNonEmptyByteArray() throws IOException {
        //Arrange
        BufferedImage bufferedImage = mock(BufferedImage.class);
        String format = FileUtilities.FORMAT_JPEG;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(0);

        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.write(bufferedImage, format, byteArrayOutputStream)).thenReturn(true);

            //Act
            byte[] actualByteArray = FileUtilities.convertBufferedImageToByteArray(bufferedImage, format);

            //Assert
            assertNotNull(actualByteArray);
        }
    }

    @Test
    @DisplayName("Valid file with png type is provided, returns FileUtilities.FORMAT_PNG")
    public void getFileFormatFromMultipartFile_contentTypePng_shouldReturnFormatPng() {
        //Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(MediaType.IMAGE_PNG_VALUE);

        //Act
        String actualFormat = FileUtilities.getFileFormatFromMultipartFile(file);

        //Assert
        assertEquals(FileUtilities.FORMAT_PNG, actualFormat);
    }

    @Test
    @DisplayName("Valid file with png type is provided, returns FileUtilities.FORMAT_JPEG")
    public void getFileFormatFromMultipartFile_contentTypeJpeg_shouldReturnFormatJpeg() {
        //Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(MediaType.IMAGE_JPEG_VALUE);

        //Act
        String actualFormat = FileUtilities.getFileFormatFromMultipartFile(file);

        //Assert
        assertEquals(FileUtilities.FORMAT_JPEG, actualFormat);
    }

    @Test
    @DisplayName("Invalid file with text type is provided, returns null")
    public void getFileFormatFromMultipartFile_contentTypeNull_shouldReturnNull() {
        //Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(MediaType.TEXT_PLAIN_VALUE);

        //Act
        String actualFormat = FileUtilities.getFileFormatFromMultipartFile(file);

        //Assert
        assertEquals(null, actualFormat);
    }

    @Test
    @DisplayName("Generate file name starts with 'baseline' and ends with 'png'")
    public void generateFileName_formatPngTypeBaseline_shouldReturnCorrectFormat() { 
        //Act
        String actualFilelName = FileUtilities.generateFileName(FileUtilities.FORMAT_PNG, ValidationUtilities.TYPE_BASELINE);

        //Assert
        assertTrue(actualFilelName.startsWith(ValidationUtilities.TYPE_BASELINE));
        assertTrue(actualFilelName.endsWith(FileUtilities.FORMAT_PNG));
    }

    @Test
    @DisplayName("Generate file name starts with 'comparison' and ends with 'jpeg'")
    public void generateFileName_formatJpegTypeComparison_shouldReturnCorrectFormat() { 
        //Act
        String actualFilelName = FileUtilities.generateFileName(FileUtilities.FORMAT_JPEG, ValidationUtilities.TYPE_COMPARISON);

        //Assert
        assertTrue(actualFilelName.startsWith(ValidationUtilities.TYPE_COMPARISON));
        assertTrue(actualFilelName.endsWith(FileUtilities.FORMAT_JPEG));
    }

    @Test
    @DisplayName("Generate file name starts with 'diff' and ends with 'png'")
    public void generateFileName_formatPngTypeDiff_shouldReturnCorrectFormat() { 
        //Act
        String actualFilelName = FileUtilities.generateFileName(FileUtilities.FORMAT_PNG, ValidationUtilities.TYPE_DIFF);

        //Assert
        assertTrue(actualFilelName.startsWith(ValidationUtilities.TYPE_DIFF));
        assertTrue(actualFilelName.endsWith(FileUtilities.FORMAT_PNG));
    }

    @Test
    @DisplayName("Get type from file with 'baseline' provided, returns ValidationUtilities.TYPE_BASELINE")
    public void getTypeFromFileName_fileNameBaseline_shouldReturnTypeBaseline() {
        //Arrange
        String fileName = "baseline.png";

        //Act
        String actualType = FileUtilities.getTypeFromFileName(fileName);

        //Assert
        assertEquals(ValidationUtilities.TYPE_BASELINE, actualType);
    }

    @Test
    @DisplayName("Get type from file with 'comparison' provided, returns ValidationUtilities.TYPE_COMPARISON")
    public void getTypeFromFileName_fileNameComparison_shouldReturnTypeComparison() {
        //Arrange
        String fileName = "comparison.png";

        //Act
        String actualType = FileUtilities.getTypeFromFileName(fileName);

        //Assert
        assertEquals(ValidationUtilities.TYPE_COMPARISON, actualType);
    }

    @Test
    @DisplayName("Get type from file with 'diff' provided, returns ValidationUtilities.TYPE_DIFF")
    public void getTypeFromFileName_fileNameDiff_shouldReturnTypeDiff() {
        //Arrange
        String fileName = "diff.png";

        //Act
        String actualType = FileUtilities.getTypeFromFileName(fileName);

        //Assert
        assertEquals(ValidationUtilities.TYPE_DIFF, actualType);
    }

    @Test
    @DisplayName("Get type from file with 'diff' provided, returns ValidationUtilities.TYPE_DIFF")
    public void getTypeFromFileName_fileNameDiffWithCapitals_shouldReturnTypeDiff() {
        //Arrange
        String fileName = "DIFF.png";

        //Act
        String actualType = FileUtilities.getTypeFromFileName(fileName);

        //Assert
        assertEquals(ValidationUtilities.TYPE_DIFF, actualType);
    }
}
