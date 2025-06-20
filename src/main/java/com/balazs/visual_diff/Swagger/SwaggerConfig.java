package com.balazs.visual_diff.Swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    
    /**
     * Override default swagger generated properties. Provides additional information for title, version, and description.
     *
     * @return UserDetailsService
     */
    @Bean
    public OpenAPI openAPIConfig() {
        return new OpenAPI().info(new Info()
        .title("Visual Diff Service")
        .version("1.0.0")
        .description("Service for comparing two images and producing a diff. Intended to be connected to a CI/CD pipeline to compare baseline, and comparison images for an automated visual regression step in PRs to reduce human error. Baseline, and comparison files are converted to byte[] containing rgba values to produce deltas. Resulting deltas are converted to YIQ colour space to separate luminance from chrominance channels, and place a heavier emphasis on luminance as humans can percieve changes in brightness better than hue. The images are compared pixel by pixel, and any pixel resulting in a delta greater than a threshold is highlighted in red."));
    }
}
