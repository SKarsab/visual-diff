package com.balazs.visual_diff.Compare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CompareRequestTests {
    @Test
    @DisplayName("Default constructor, initializes empty object")
    public void CompareRequest_default_shouldInitializeEmptyObject() {
        //Act
        CompareRequest actualRequest = new CompareRequest();

        //Assert
        assertNull(actualRequest.getBaseline());
        assertNull(actualRequest.getComparison());
    }

    @Test
    @DisplayName("Parameterized constructor, initializes object correctly")
    public void CompareRequest_parameters_shouldInitializeObject() {
        //Arrange
        String expectedBaselineFileName = "baseline.png";
        String expectedComparisonFileName = "comparison.png";

        //Act
        CompareRequest actualRequest = new CompareRequest(expectedBaselineFileName, expectedComparisonFileName);

        //Assert
        assertEquals(expectedBaselineFileName, actualRequest.getBaseline());
        assertEquals(expectedComparisonFileName, actualRequest.getComparison());
    }

    @Test
    @DisplayName("Get and set baseline file name")
    public void changeBaselineFileName_withValue_changesBaselineFileName() {
        //Arrange
        CompareRequest actualRequest = new CompareRequest();
        String expectedBaselineFileName = "baseline.png";

        //Act
        actualRequest.setBaseline(expectedBaselineFileName);
        String actualBaselineFileName = actualRequest.getBaseline();

        //Assert
        assertEquals(expectedBaselineFileName, actualBaselineFileName);
    }

    @Test
    @DisplayName("Get and set comparison file name")
    public void changeComparisonFileName_withValue_changesComparisonFileName() {
        //Arrange
        CompareRequest actualRequest = new CompareRequest();
        String expectedComparisonFileName = "comparison.png";

        //Act
        actualRequest.setComparison(expectedComparisonFileName);
        String actualComparisonFileName = actualRequest.getComparison();

        //Assert
        assertEquals(expectedComparisonFileName, actualComparisonFileName);
    }
}
