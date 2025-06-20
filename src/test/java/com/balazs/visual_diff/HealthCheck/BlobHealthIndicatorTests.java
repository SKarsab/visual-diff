package com.balazs.visual_diff.HealthCheck;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.azure.storage.blob.models.BlobStorageException;
import com.balazs.visual_diff.Blob.BlobService;

@SpringBootTest
@AutoConfigureMockMvc
public class BlobHealthIndicatorTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlobService blobService;

    @Test
    @DisplayName("GET /actuator/health with successful connection to azure blob storage, returns 200 ok")
	public void health_withConnectionSuccess_shouldReturn200Ok() throws Exception {
        //Arrange
        String expectedStatus = "UP";

        when(blobService.healthCheckAzure()).thenReturn(true);
        
        //Act & Assert
         mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.components.blob.status").value(expectedStatus));
	}

    @Test
    @DisplayName("GET /actuator/health with unsuccessful connection to azure blob storage, returns 503 service unavailable")
	public void health_withConnectionUnsuccessful_shouldReturn503ServiceUnavailable() throws Exception {
        //Arrange
        String expectedStatus = "DOWN";

        when(blobService.healthCheckAzure()).thenThrow(new BlobStorageException("Connection failed", null, null));
        
        //Act & Assert
         mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.components.blob.status").value(expectedStatus));
	}
}
