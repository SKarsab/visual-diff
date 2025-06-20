package com.balazs.visual_diff.HealthCheck;

import com.balazs.visual_diff.Blob.BlobService;
import com.balazs.visual_diff.Exceptions.GlobalExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class BlobHealthIndicator implements HealthIndicator {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private BlobService blobService;

    /**
     * Extension to spring actuator's default health check to also check connection to Azure.
     *
     * @return Health object with default information + blob storage connection status
     */
    public Health health() {
        try {
            //Log start of request
            logger.info("Received GET /actuator/health request");

            blobService.healthCheckAzure();

            //Log end of request
            logger.info("GET /actuator/health successful");

            return Health.up().build();
        }
        catch (Exception e) {
            return Health.down().build();
        }
    }
}
