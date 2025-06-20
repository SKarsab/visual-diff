package com.balazs.visual_diff.Utilities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public class ValidationUtilitiesTests {
    @Test
    @DisplayName("Valid type 'baseline' is provided, return is true")
    public void isTypeValid_typeBaseline_shouldReturnTrue() {
        //Arrange
        String type = "baseline";

        //Act
        boolean actualState = ValidationUtilities.isTypeValid(type);

        //Assert
        assertTrue(actualState);
    }

    @Test
    @DisplayName("Valid type 'comparison' is provided, return is true")
    public void isTypeValid_typeComparison_shouldReturnTrue() {
        //Arrange
        String type = "comparison";

        //Act
        boolean actualState = ValidationUtilities.isTypeValid(type);

        //Assert
        assertTrue(actualState);
    }

    @Test
    @DisplayName("Valid type 'BASELINE' is provided, return is true")
    public void isTypeValid_typeBaselineWithCapitals_shouldReturnTrue() {
        //Arrange
        String type = "BASELINE";

        //Act
        boolean actualState = ValidationUtilities.isTypeValid(type);

        //Assert
        assertTrue(actualState);
    }

    @Test
    @DisplayName("Valid type 'COMPARISON' is provided, return is true")
    public void isTypeValid_typeComparisonWithCapitals_shouldReturnTrue() {
        //Arrange
        String type = "COMPARISON";

        //Act
        boolean actualState = ValidationUtilities.isTypeValid(type);

        //Assert
        assertTrue(actualState);
    }

    @Test
    @DisplayName("Invalid type 'test' is provided, return is false")
    public void isTypeValid_typeTest_shouldReturnFalse() {
        //Arrange
        String type = "test";

        //Act
        boolean actualState = ValidationUtilities.isTypeValid(type);

        //Assert
        assertFalse(actualState);
    }

    @Test
    @DisplayName("Invalid type '' is provided, return is false")
    public void isTypeValid_typeEmpty_shouldReturnFalse() {
        //Arrange
        String type = "";

        //Act
        boolean actualState = ValidationUtilities.isTypeValid(type);

        //Assert
        assertFalse(actualState);
    }

    @Test
    @DisplayName("Invalid type ' ' is provided, return is false")
    public void isTypeValid_typeBlank_shouldReturnFalse() {
        //Arrange
        String type = " ";

        //Act
        boolean actualState = ValidationUtilities.isTypeValid(type);

        //Assert
        assertFalse(actualState);
    }

    @Test
    @DisplayName("Invalid type null is provided, return is false")
    public void isTypeValid_typeNull_shouldReturnFalse() {
        //Arrange
        String type = null;

        //Act
        boolean actualState = ValidationUtilities.isTypeValid(type);

        //Assert
        assertFalse(actualState);
    }

    @Test
    @DisplayName("Valid file not empty or null is provided, return is true")
    public void isFileValid_fileNotEmpty_shouldReturnTrue() throws IOException {
        //Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);

        //Act
        boolean actualState = ValidationUtilities.isFileValid(file);

        //Asset
        assertTrue(actualState);
    }

    @Test
    @DisplayName("Invalid file is null is provided, return is false")
    public void isFileValid_fileNull_shouldReturnFalse() throws IOException {
        //Arrange
        MultipartFile file = null;

        //Act
        boolean actualState = ValidationUtilities.isFileValid(file);

        //Asset
        assertFalse(actualState);
    }

    @Test
    @DisplayName("Invalid file is empty is provided, return is false")
    public void isFileValid_fileEmpty_shouldReturnFalse() throws IOException {
        //Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        //Act
        boolean actualState = ValidationUtilities.isFileValid(file);

        //Asset
        assertFalse(actualState);
    }

    @Test
    @DisplayName("Valid content type image/png is provided, return is true")
    public void isContentTypeValid_contentTypePng_shouldReturnTrue() throws IOException {
        //Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(MediaType.IMAGE_PNG_VALUE);

        //Act
        boolean actualState = ValidationUtilities.isContentTypeValid(file);

        //Assert
        assertTrue(actualState);
    }

    @Test
    @DisplayName("Valid content type image/jpeg is provided, return is true")
    public void isContentTypeValid_contentTypeJpeg_shouldReturnTrue() throws IOException {
        //Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(MediaType.IMAGE_JPEG_VALUE);

        //Act
        boolean actualState = ValidationUtilities.isContentTypeValid(file);

        //Asset
        assertTrue(actualState);
    }

    @Test
    @DisplayName("Invalid content type text/plain is provided, return is false")
    public void isContentTypeValid_contentTypeTextPlain_shouldReturnFalse() throws IOException {
        //Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn(MediaType.TEXT_PLAIN_VALUE);

        //Act
        boolean actualState = ValidationUtilities.isContentTypeValid(file);

        //Asset
        assertFalse(actualState);
    }

    @Test
    @DisplayName("Invalid content type null is provided, return is false")
    public void isContentTypeValid_contentTypeNull_shouldReturnFalse() throws IOException {
        //Act
        boolean actualState = ValidationUtilities.isContentTypeValid(null);

        //Asset
        assertFalse(actualState);
    }

    @Test
    @DisplayName("Valid file is provided. Successfully converted to BufferedImage, return is true")
    public void isImageContentsValid_validImage_shouldReturnTrue() throws IOException {
        //Arrange
        MultipartFile file = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        BufferedImage bufferedImage = mock(BufferedImage.class);

        when(file.getInputStream()).thenReturn(inputStream);

        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(inputStream)).thenReturn(bufferedImage);

            //Act
            boolean actualState = ValidationUtilities.isImageContentsValid(file);

            //Assert
            assertTrue(actualState);
        }
    }

    @Test
    @DisplayName("Invalid file null is provided, return is false")
    public void isImageContentsValid_nullImage_shouldReturnFalse() throws IOException {
        //Act
        boolean actualState = ValidationUtilities.isImageContentsValid(null);

        //Assert
        assertFalse(actualState);
    }

    @Test
    @DisplayName("Images with same dimensions is provided, return is true")
    public void areImageDimensionsValid_sameDimensions_shouldReturnTrue() throws IOException {
        //Arrange
        MultipartFile baselineFile = mock(MultipartFile.class);
        MultipartFile comparisonFile = mock(MultipartFile.class);
        InputStream baselineInputStream = mock(InputStream.class);
        InputStream comparisonInputStream = mock(InputStream.class);
        BufferedImage baselineImage = mock(BufferedImage.class);
        BufferedImage comparisonImage = mock(BufferedImage.class);

        when(baselineFile.getInputStream()).thenReturn(baselineInputStream);
        when(comparisonFile.getInputStream()).thenReturn(comparisonInputStream);
        when(baselineImage.getWidth()).thenReturn(100);
        when(baselineImage.getHeight()).thenReturn(100);
        when(comparisonImage.getWidth()).thenReturn(100);
        when(comparisonImage.getHeight()).thenReturn(100);

        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(baselineInputStream)).thenReturn(baselineImage);
            imageIO.when(() -> ImageIO.read(comparisonInputStream)).thenReturn(comparisonImage);

            //Act
            boolean actualState = ValidationUtilities.areImageDimensionsValid(baselineFile, comparisonFile);

            //Assert
            assertTrue(actualState);
        }
    }

    @Test
    @DisplayName("Images with different widths is provided, return is false")
    public void areImageDimensionsValid_differentWidths_shouldReturnTrue() throws IOException {
        //Arrange
        MultipartFile baselineFile = mock(MultipartFile.class);
        MultipartFile comparisonFile = mock(MultipartFile.class);
        InputStream baselineInputStream = mock(InputStream.class);
        InputStream comparisonInputStream = mock(InputStream.class);
        BufferedImage baselineImage = mock(BufferedImage.class);
        BufferedImage comparisonImage = mock(BufferedImage.class);

        when(baselineFile.getInputStream()).thenReturn(baselineInputStream);
        when(comparisonFile.getInputStream()).thenReturn(comparisonInputStream);
        when(baselineImage.getWidth()).thenReturn(100);
        when(baselineImage.getHeight()).thenReturn(100);
        when(comparisonImage.getWidth()).thenReturn(200);
        when(comparisonImage.getHeight()).thenReturn(100);

        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(baselineInputStream)).thenReturn(baselineImage);
            imageIO.when(() -> ImageIO.read(comparisonInputStream)).thenReturn(comparisonImage);

            //Act
            boolean actualState = ValidationUtilities.areImageDimensionsValid(baselineFile, comparisonFile);

            //Assert
            assertFalse(actualState);
        }
    }

    @Test
    @DisplayName("Images with different heights is provided, return is false")
    public void areImageDimensionsValid_differentHeights_shouldReturnTrue() throws IOException {
        //Arrange
        MultipartFile baselineFile = mock(MultipartFile.class);
        MultipartFile comparisonFile = mock(MultipartFile.class);
        InputStream baselineInputStream = mock(InputStream.class);
        InputStream comparisonInputStream = mock(InputStream.class);
        BufferedImage baselineImage = mock(BufferedImage.class);
        BufferedImage comparisonImage = mock(BufferedImage.class);

        when(baselineFile.getInputStream()).thenReturn(baselineInputStream);
        when(comparisonFile.getInputStream()).thenReturn(comparisonInputStream);
        when(baselineImage.getWidth()).thenReturn(100);
        when(baselineImage.getHeight()).thenReturn(100);
        when(comparisonImage.getWidth()).thenReturn(100);
        when(comparisonImage.getHeight()).thenReturn(200);

        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(baselineInputStream)).thenReturn(baselineImage);
            imageIO.when(() -> ImageIO.read(comparisonInputStream)).thenReturn(comparisonImage);

            //Act
            boolean actualState = ValidationUtilities.areImageDimensionsValid(baselineFile, comparisonFile);

            //Assert
            assertFalse(actualState);
        }
    }

    @Test
    @DisplayName("Images with baselineFile null is provided, return is false")
    public void areImageDimensionsValid_baselineFileNull_shouldReturnFalse() throws IOException {
        //Arrange
        MultipartFile comparisonFile = mock(MultipartFile.class);

        //Act
        boolean actualState = ValidationUtilities.areImageDimensionsValid(null, comparisonFile);

        //Assert
        assertFalse(actualState);
    }

    @Test
    @DisplayName("Images with comparisonFile null is provided, return is false")
    public void areImageDimensionsValid_comparisonFileNull_shouldReturnFalse() throws IOException {
        //Arrange
        MultipartFile baselineFile = mock(MultipartFile.class);

        //Act
        boolean actualState = ValidationUtilities.areImageDimensionsValid(baselineFile, null);

        //Assert
        assertFalse(actualState);
    }
}
