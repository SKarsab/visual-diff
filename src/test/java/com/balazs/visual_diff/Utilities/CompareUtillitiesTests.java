package com.balazs.visual_diff.Utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CompareUtillitiesTests {
    final float LUMINANCE_COEFFICIENT_1 = 0.29889531f;
    final float LUMINANCE_COEFFICIENT_2 = 0.58662247f;
    final float LUMINANCE_COEFFICIENT_3 = 0.11448223f;

    final float CHROMINANCE_I_COEFFICIENT_1 = 0.59597799f;
    final float CHROMINANCE_I_COEFFICIENT_2 = 0.27417610f;
    final float CHROMINANCE_I_COEFFICIENT_3 = 0.32180189f;

    final float CHROMINANCE_Q_COEFFICIENT_1 = 0.21147017f;
    final float CHROMINANCE_Q_COEFFICIENT_2 = 0.52261711f;
    final float CHROMINANCE_Q_COEFFICIENT_3 = 0.31114694f;

    final float DELTA_WEIGHT_Y = 0.5053f;
    final float DELTA_WEIGHT_I = 0.299f;
    final float DELTA_WEIGHT_Q = 0.1957f;

    private final static int COLOUR_MAX_VALUE = 255;
    private final static float ALPHA = 0.1f;
    private final static int GRAY = 48;

    @Test
    @DisplayName("Calculate Y from RGB, returns expected luminance")
    public void calculateY_fromRGB_shouldReturnExpectedLuminance() {
        //Arrange
        float deltaR = 100.0f;
        float deltaG = 150.0f;
        float deltaB = 200.0f;
        float expectedY = (100.0f * LUMINANCE_COEFFICIENT_1) + (150.0f * LUMINANCE_COEFFICIENT_2) + (200.0f * LUMINANCE_COEFFICIENT_3);

        //Act
        float actualY = CompareUtilities.calculateY(deltaR, deltaG, deltaB);

        //Assert
        assertEquals(expectedY, actualY);
    }

    @Test
    @DisplayName("Calculate I from RGB, returns expected chrominance")
    public void calculateI_fromRGB_shouldReturnExpectedChrominance() {
        //Arrange
        float deltaR = 100.0f;
        float deltaG = 150.0f;
        float deltaB = 200.0f;
        float expectedI = (100.0f * CHROMINANCE_I_COEFFICIENT_1) - (150.0f * CHROMINANCE_I_COEFFICIENT_2) - (200.0f * CHROMINANCE_I_COEFFICIENT_3);

        //Act
        float actualI = CompareUtilities.calculateI(deltaR, deltaG, deltaB);

        //Assert
        assertEquals(expectedI, actualI);
    }

    @Test
    @DisplayName("Calculate Q from RGB, returns expected chrominance")
    public void calculateQ_fromRGB_shouldReturnExpectedChrominance() {
        //Arrange
        float deltaR = 100.0f;
        float deltaG = 150.0f;
        float deltaB = 200.0f;
        float expectedQ = (100.0f * CHROMINANCE_Q_COEFFICIENT_1) - (150.0f * CHROMINANCE_Q_COEFFICIENT_2) + (200.0f * CHROMINANCE_Q_COEFFICIENT_3);

        //Act
        float actualQ = CompareUtilities.calculateQ(deltaR, deltaG, deltaB);

        //Assert
        assertEquals(expectedQ, actualQ);
    }

    @Test
    @DisplayName("Calculate percieved brightness with positive YIQ provided, returns expected delta")
    public void calculateCombinedDelta_fromPositiveYIQ_shouldReturnExpectedDelta() {
        //Arrange
        float y = 50.0f;
        float i = 30.0f;
        float q = 30.0f;
        float expectedDelta = (DELTA_WEIGHT_Y * y * y) + (DELTA_WEIGHT_I * i * i) + (DELTA_WEIGHT_Q * q * q);

        //Act
        float actualDelta = CompareUtilities.calculateCombinedDelta(y, i, q);

        //Assert
        assertEquals(expectedDelta, actualDelta);
    }

    @Test
    @DisplayName("Calculate percieved brightness with negative YIQ provided, returns expected delta")
    public void calculateCombinedDelta_fromNegativeYIQ_shouldReturnExpectedDelta() {
        //Arrange
        float y = -50.0f;
        float i = -30.0f;
        float q = -30.0f;
        float expectedDelta = (DELTA_WEIGHT_Y * y * y) + (DELTA_WEIGHT_I * i * i) + (DELTA_WEIGHT_Q * q * q);

        //Act
        float actualDelta = CompareUtilities.calculateCombinedDelta(y, i, q);

        //Assert
        assertEquals(expectedDelta, actualDelta);
    }

    @Test
    @DisplayName("Apply transparency with zero alpha/delta, returns zero")
    public void applyTransparency_zeroDelta_shouldReturnZero() {
        //Arrange
        int baselineR = 100;
        int baselineA = 0;
        int compareR = 100;
        int compareA = 0;
        float deltaA = 0f;
        float expectedDelta = (baselineR * baselineA - compareR * compareA - GRAY * deltaA) / 255f;

        //Act
        float actualDelta = CompareUtilities.applyTransparency(baselineR, baselineA, compareR, compareA, deltaA);

        //Assert
        assertEquals(expectedDelta, actualDelta);
    }

    @Test
    @DisplayName("Apply transparency with max alpha/delta, returns expected value")
    public void applyTransparency_maxDelta_shouldReturnExpectedValue() {
        //Arrange
        int baselineR = 100;
        int baselineA = 255;
        int compareR = 100;
        int compareA = 0;
        float deltaA = 255f;
        float expectedDelta = (baselineR * baselineA - compareR * compareA - GRAY * deltaA) / 255f;

        //Act
        float result = CompareUtilities.applyTransparency(baselineR, baselineA, compareR, compareA, deltaA);
        
        //Assert
        assertEquals(expectedDelta, result);
    }

    @Test
    @DisplayName("Apply transparency with partial alpha/delta, returns expected value")
    public void applyTransparency_partialDelta_shouldReturnExpectedValue() {
        //Arrange
        int baselineR = 100;
        int baselineA = 255;
        int compareR = 50;
        int compareA = 155;
        float deltaA = 100f;
        float expectedDelta = (baselineR * baselineA - compareR * compareA - GRAY * deltaA) / 255f;

        //Act
        float result = CompareUtilities.applyTransparency(baselineR, baselineA, compareR, compareA, deltaA);
        
        //Assert
        assertEquals(expectedDelta, result);
    }

    @Test
    @DisplayName("Calculate colour delta with baseline greater than compare, returns positive delta")
    public void calculateColourDelta_baselineGreaterThanCompare_shouldReturnPositiveDelta() {
        //Arrange
        byte baselineR = (byte) 255;
        byte compareR = (byte) 50;
        float expectedDelta = 205f;

        //Act
        float actualDelta = CompareUtilities.calculateColourDelta(baselineR, compareR);

        //Assert
        assertEquals(expectedDelta, actualDelta);
        assertTrue(actualDelta >= 0);
    }

    @Test
    @DisplayName("Calculate colour delta with baseline lesser than compare, returns negative delta")
    public void calculateColourDelta_baselineLesserThanCompare_shouldReturnNegativeDelta() {
        //Arrange
        byte baselineR = (byte) 50;
        byte compareR = (byte) 255;
        float expectedDelta = -205f;

        //Act
        float actualDelta = CompareUtilities.calculateColourDelta(baselineR, compareR);

        //Assert
        assertEquals(expectedDelta, actualDelta);
        assertTrue(actualDelta <= 0);
    }

    @Test
    @DisplayName("Calculate colour delta with baseline equal to compare, returns zero delta")
    public void calculateColourDelta_baselineEqualToCompare_shouldReturnZeroDelta() {
        //Arrange
        byte baselineR = (byte) 255;
        byte compareR = (byte) 255;
        float expectedDelta = 0f;

        //Act
        float actualDelta = CompareUtilities.calculateColourDelta(baselineR, compareR);

        //Assert
        assertEquals(expectedDelta, actualDelta);
        assertTrue(actualDelta == 0);
    }
    
    @Test
    @DisplayName("Calculate delta with max alpha, returns expected percieved brightness")
    public void calculateDelta_withMaxDelta_shouldReturnExpectedPerceptualDelta() {
        //Arrange
        byte[] baselineRGBA = {(byte) 255, (byte) 0, (byte) 0, (byte) 255};
        byte[] compareRGBA = {(byte) 0, (byte) 255, (byte) 0, (byte) 255};

        //RGB deltas
        float deltaR = baselineRGBA[0] - compareRGBA[0];
        float deltaG = baselineRGBA[1] - compareRGBA[1];
        float deltaB = baselineRGBA[2] - compareRGBA[2];

        //YIQ colour space
        float y = (deltaR * LUMINANCE_COEFFICIENT_1) + (deltaG * LUMINANCE_COEFFICIENT_2) + (deltaB * LUMINANCE_COEFFICIENT_3);
        float i = (deltaR * CHROMINANCE_I_COEFFICIENT_1) - (deltaG * CHROMINANCE_I_COEFFICIENT_2) - (deltaB * CHROMINANCE_I_COEFFICIENT_3);
        float q = (deltaR * CHROMINANCE_Q_COEFFICIENT_1) - (deltaG * CHROMINANCE_Q_COEFFICIENT_2) + (deltaB * CHROMINANCE_Q_COEFFICIENT_3);

        //Perceptual delta
        float expectedDelta = DELTA_WEIGHT_Y * y * y + DELTA_WEIGHT_I * i * i + DELTA_WEIGHT_Q * q * q;

        //Act
        int actualDelta = CompareUtilities.calculateDelta(baselineRGBA, compareRGBA, 0);

        //Assert
        assertEquals((int)expectedDelta, actualDelta);
    }

    @Test
    @DisplayName("Calculate delta with min alpha, returns expected percieved brightness")
    public void calculateDelta_withMinDelta_shouldReturnExpectedPerceptualDelta() {
        //Arrange
        byte[] baselineRGBA = {(byte) 255, (byte) 0, (byte) 0, (byte) 0};
        byte[] compareRGBA = {(byte) 0, (byte) 255, (byte) 0, (byte) 0};

        //RGB deltas
        float deltaR = baselineRGBA[0] - compareRGBA[0];
        float deltaG = baselineRGBA[1] - compareRGBA[1];
        float deltaB = baselineRGBA[2] - compareRGBA[2];
        float deltaA = baselineRGBA[3] - compareRGBA[3];

        //Apply transparency
        deltaR = (baselineRGBA[0] * baselineRGBA[3] - compareRGBA[0] * compareRGBA[3] - GRAY * deltaA) / 255f;
        deltaG = (baselineRGBA[1] * baselineRGBA[3] - compareRGBA[1] * compareRGBA[3] - GRAY * deltaA) / 255f;
        deltaB = (baselineRGBA[2] * baselineRGBA[3] - compareRGBA[2] * compareRGBA[3] - GRAY * deltaA) / 255f;

        //YIQ colour space
        float y = (deltaR * LUMINANCE_COEFFICIENT_1) + (deltaG * LUMINANCE_COEFFICIENT_2) + (deltaB * LUMINANCE_COEFFICIENT_3);
        float i = (deltaR * CHROMINANCE_I_COEFFICIENT_1) - (deltaG * CHROMINANCE_I_COEFFICIENT_2) - (deltaB * CHROMINANCE_I_COEFFICIENT_3);
        float q = (deltaR * CHROMINANCE_Q_COEFFICIENT_1) - (deltaG * CHROMINANCE_Q_COEFFICIENT_2) + (deltaB * CHROMINANCE_Q_COEFFICIENT_3);

        //Perceptual delta
        float expectedDelta = DELTA_WEIGHT_Y * y * y + DELTA_WEIGHT_I * i * i + DELTA_WEIGHT_Q * q * q;

        //Act
        int actualDelta = CompareUtilities.calculateDelta(baselineRGBA, compareRGBA, 0);

        //Assert
        assertEquals((int)expectedDelta, actualDelta);
    }

    @Test
    @DisplayName("Calculate delta with partial alpha, returns expected percieved brightness")
    public void calculateDelta_withPartialDelta_shouldReturnExpectedPerceptualDelta() {
        //Arrange
        byte[] baselineRGBA = {(byte) 255, (byte) 0, (byte) 0, (byte) 255};
        byte[] compareRGBA = {(byte) 0, (byte) 255, (byte) 0, (byte) 180};

        //RGB deltas
        float deltaR = baselineRGBA[0] - compareRGBA[0];
        float deltaG = baselineRGBA[1] - compareRGBA[1];
        float deltaB = baselineRGBA[2] - compareRGBA[2];
        float deltaA = baselineRGBA[3] - compareRGBA[3];

        //Apply transparency
        deltaR = (baselineRGBA[0] * baselineRGBA[3] - compareRGBA[0] * compareRGBA[3] - GRAY * deltaA) / 255f;
        deltaG = (baselineRGBA[1] * baselineRGBA[3] - compareRGBA[1] * compareRGBA[3] - GRAY * deltaA) / 255f;
        deltaB = (baselineRGBA[2] * baselineRGBA[3] - compareRGBA[2] * compareRGBA[3] - GRAY * deltaA) / 255f;

        //YIQ colour space
        float y = (deltaR * LUMINANCE_COEFFICIENT_1) + (deltaG * LUMINANCE_COEFFICIENT_2) + (deltaB * LUMINANCE_COEFFICIENT_3);
        float i = (deltaR * CHROMINANCE_I_COEFFICIENT_1) - (deltaG * CHROMINANCE_I_COEFFICIENT_2) - (deltaB * CHROMINANCE_I_COEFFICIENT_3);
        float q = (deltaR * CHROMINANCE_Q_COEFFICIENT_1) - (deltaG * CHROMINANCE_Q_COEFFICIENT_2) + (deltaB * CHROMINANCE_Q_COEFFICIENT_3);

        //Perceptual delta
        float expectedDelta = DELTA_WEIGHT_Y * y * y + DELTA_WEIGHT_I * i * i + DELTA_WEIGHT_Q * q * q;

        //Act
        int actualDelta = CompareUtilities.calculateDelta(baselineRGBA, compareRGBA, 0);

        //Assert
        assertEquals((int)expectedDelta, actualDelta);
    }

    @Test
    @DisplayName("Draw pixel with valid RGBA values, copies values to output array")
    public void drawPixel_validRGBAValues_shouldCopyToOutputArray() {
        //Arrange
        byte[] outputRGBA = new byte[4];
        byte[] incomingRGBA = {100, 100, 100, (byte)255};

        //Act
        CompareUtilities.drawPixel(outputRGBA, incomingRGBA, 0);

        //Assert
        assertEquals((byte) 100, outputRGBA[0]);
        assertEquals((byte) 100, outputRGBA[1]);
        assertEquals((byte) 100, outputRGBA[2]);
        assertEquals((byte) COLOUR_MAX_VALUE, outputRGBA[3]);
    }

    @Test
    @DisplayName("Draw gray pixel with max alpha pixel, adjusts for alpha")
    public void drawGrayPixel_withMaxAlphaPixel_shouldAdjustForAlpha() {
        //Arrange
        byte[] baselineRGBA = {(byte) 100, (byte) 100, (byte) 100, (byte) 255};
        byte[] outputRGBA = new byte[4];
        float value = 255 + (baselineRGBA[0] * LUMINANCE_COEFFICIENT_1 + baselineRGBA[1] * LUMINANCE_COEFFICIENT_2 + baselineRGBA[2] * LUMINANCE_COEFFICIENT_3 - 255) * ALPHA * (baselineRGBA[3] / 255.0f);
        byte gray = (byte)Math.max(0, Math.min(255, value));

        //Act
        CompareUtilities.drawGrayPixel(baselineRGBA, outputRGBA, 0);

        //Assert
        assertEquals(gray, outputRGBA[0]);
        assertEquals(gray, outputRGBA[1]);
        assertEquals(gray, outputRGBA[2]);
        assertEquals((byte) COLOUR_MAX_VALUE, outputRGBA[3]);
    }

    @Test
    @DisplayName("Draw gray pixel with zero alpha pixel, adjusts for alpha")
    public void drawGrayPixel_withZeroAlphaPixel_shouldAdjustForAlpha() {
        //Arrange
        byte[] baselineRGBA = {(byte) 100, (byte) 100, (byte) 100, (byte) 0};
        byte[] outputRGBA = new byte[4];
        float value = 255 + (baselineRGBA[0] * LUMINANCE_COEFFICIENT_1 + baselineRGBA[1] * LUMINANCE_COEFFICIENT_2 + baselineRGBA[2] * LUMINANCE_COEFFICIENT_3 - 255) * ALPHA * (baselineRGBA[3] / 255.0f);
        byte gray = (byte)Math.max(0, Math.min(255, value));

        //Act
        CompareUtilities.drawGrayPixel(baselineRGBA, outputRGBA, 0);

        //Assert
        assertEquals(gray, outputRGBA[0]);
        assertEquals(gray, outputRGBA[1]);
        assertEquals(gray, outputRGBA[2]);
        assertEquals((byte) COLOUR_MAX_VALUE, outputRGBA[3]);
    }

    @Test
    @DisplayName("Draw gray pixel with partially alpha pixel, adjusts for alpha")
    public void drawGrayPixel_withPartialAlphaPixel_shouldAdjustForAlpha() {
        //Arrange
        byte[] baselineRGBA = {(byte) 100, (byte) 100, (byte) 100, (byte) 180};
        byte[] outputRGBA = new byte[4];
        float value = 255 + (baselineRGBA[0] * LUMINANCE_COEFFICIENT_1 + baselineRGBA[1] * LUMINANCE_COEFFICIENT_2 + baselineRGBA[2] * LUMINANCE_COEFFICIENT_3 - 255) * ALPHA * (baselineRGBA[3] / 255.0f);
        byte gray = (byte)Math.max(0, Math.min(255, value));

        //Act
        CompareUtilities.drawGrayPixel(baselineRGBA, outputRGBA, 0);

        //Assert
        assertEquals(gray, outputRGBA[0]);
        assertEquals(gray, outputRGBA[1]);
        assertEquals(gray, outputRGBA[2]);
        assertEquals((byte) COLOUR_MAX_VALUE, outputRGBA[3]);
    }
}
