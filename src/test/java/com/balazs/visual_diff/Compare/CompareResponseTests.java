package com.balazs.visual_diff.Compare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CompareResponseTests {
    @Test
    @DisplayName("Default constructor, initializes empty object")
    public void CompareResponse_default_shouldInitializeEmptyObject() {
        //Act
        CompareResponse actualResponse = new CompareResponse();

        //Assert
        assertNull(actualResponse.getBaselineFileName());
        assertNull(actualResponse.getComparisonFileName());
        assertNull(actualResponse.getDiffFileName());
    }

    @Test
    @DisplayName("Parameterized constructor, initializes object correctly")
    public void CompareResponse_parameters_shouldInitializeObject() {
        //Arrange
        String expectedBaselineFileName = "baseline.png";
        String expectedComparisonFileName = "comparison.png";
        String expectedDiffFileName = "diff.png";

        //Act
        CompareResponse actualResponse = new CompareResponse(expectedBaselineFileName, expectedComparisonFileName, expectedDiffFileName);

        //Assert
        assertEquals(expectedBaselineFileName, actualResponse.getBaselineFileName());
        assertEquals(expectedComparisonFileName, actualResponse.getComparisonFileName());
        assertEquals(expectedDiffFileName, actualResponse.getDiffFileName());
    }

    @Test
    @DisplayName("Get and set baseline file name")
    public void changeBaselineFileName_withValue_changesBaselineFileName() {
        //Arrange
        CompareResponse actualResponse = new CompareResponse();
        String expectedBaselineFileName = "baseline.png";

        //Act
        actualResponse.setBaselineFileName(expectedBaselineFileName);
        String actualBaselineFileName = actualResponse.getBaselineFileName();

        //Assert
        assertEquals(expectedBaselineFileName, actualBaselineFileName);
    }

    @Test
    @DisplayName("Get and set comparison file name")
    public void changeComparisonFileName_withValue_changesComparisonFileName() {
        //Arrange
        CompareResponse actualResponse = new CompareResponse();
        String expectedComparisonFileName = "comparison.png";

        //Act
        actualResponse.setComparisonFileName(expectedComparisonFileName);
        String actualComparisonFileName = actualResponse.getComparisonFileName();

        //Assert
        assertEquals(expectedComparisonFileName, actualComparisonFileName);
    }

    @Test
    @DisplayName("Get and set diff file name")
    public void changeDiffFileName_withValue_changesBDiffFileName() {
        //Arrange
        CompareResponse actualResponse = new CompareResponse();
        String expectedDiffFileName = "diff.png";

        //Act
        actualResponse.setDiffFileName(expectedDiffFileName);
        String actualDiffFileName = actualResponse.getDiffFileName();

        //Assert
        assertEquals(expectedDiffFileName, actualDiffFileName);
    }
}
