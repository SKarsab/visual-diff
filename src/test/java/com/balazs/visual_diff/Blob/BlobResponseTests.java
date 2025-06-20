package com.balazs.visual_diff.Blob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BlobResponseTests {
     @Test
    @DisplayName("Default constructor, initializes empty object")
    public void BlobResponse_default_shouldInitializeEmptyObject() {
        //Act
        BlobResponse actualResponse = new BlobResponse();

        //Assert
        assertNull(actualResponse.getFileName());
        assertNull(actualResponse.getType());
        assertNull(actualResponse.getSize());
        assertNull(actualResponse.getCreationTime());
    }

    @Test
    @DisplayName("Parameterized constructor, initializes object correctly")
    public void BlobResponse_parameters_shouldInitializeObject() {
        //Arrange
        String expectedFileName = "baseline.png";
        String expectedType = "baseline";
        String expectedSize = "5";
        String expectedCreationTime = "2025-06-05_22-14-22";

        //Act
        BlobResponse actualResponse = new BlobResponse(expectedFileName, expectedType, expectedSize, expectedCreationTime);

        //Assert
        assertEquals(expectedFileName, actualResponse.getFileName());
        assertEquals(expectedType, actualResponse.getType());
        assertEquals(expectedSize, actualResponse.getSize());
        assertEquals(expectedCreationTime, actualResponse.getCreationTime());
    }

    @Test
    @DisplayName("Get and set file name")
    public void changeFileName_withValue_shouldChangeBaselineFileName() {
        //Arrange
        BlobResponse actualResponse = new BlobResponse();
        String expectedFileName = "baseline.png";

        //Act
        actualResponse.setFileName(expectedFileName);
        String actualFileName = actualResponse.getFileName();

        //Assert
        assertEquals(expectedFileName, actualFileName);
    }

    @Test
    @DisplayName("Get and set type")
    public void changeType_withValue_shouldChangesType() {
        //Arrange
        BlobResponse actualResponse = new BlobResponse();
        String expectedType = "baseline";

        //Act
        actualResponse.setType(expectedType);
        String actualType = actualResponse.getType();

        //Assert
        assertEquals(expectedType, actualType);
    }

    @Test
    @DisplayName("Get and set size")
    public void changeSize_withValue_shouldChangesSize() {
        //Arrange
        BlobResponse actualResponse = new BlobResponse();
        String expectedSize = "5";

        //Act
        actualResponse.setSize(expectedSize);
        String actualSize = actualResponse.getSize();

        //Assert
        assertEquals(expectedSize, actualSize);
    }

    @Test
    @DisplayName("Get and set creation time")
    public void changeCreationTime_withValue_shouldCreationTime() {
        //Arrange
        BlobResponse actualResponse = new BlobResponse();
        String expectedCreationTime = "2025-06-15T14:36:00Z";

        //Act
        actualResponse.setCreationTime(expectedCreationTime);
        String actualCreationTime = actualResponse.getCreationTime();

        //Assert
        assertEquals(expectedCreationTime, actualCreationTime);
    }
}
