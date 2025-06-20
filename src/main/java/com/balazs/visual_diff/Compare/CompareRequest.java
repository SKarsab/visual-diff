package com.balazs.visual_diff.Compare;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(name = "CompareRequest", description = "This request object is accepted for the /compare endpoint in the CompareController. It includes a baselineFileName, and comparisonFileName.")
public class CompareRequest {
    
    @NotEmpty(message = "baselineFileName must not be null.")
    @Schema(description = "The 'fileName' of the baseline file to compare. This file will be retrieved from Azure if it exists, and used for the comparison. FileName will be in the format [type]\\_[timestamp]\\_[UUID].[format]", example = "baseline_2025-06-14_17-47-16_0a862592-3fc2-46a7-b74a-65261c5dec6e.png")
    private String baselineFileName;

    @NotEmpty(message = "comparisonFileName must not be null.")
    @Schema(description = "The 'fileName' of the comparison file to compare. This file will be retrieved from Azure if it exists, and used for the comparison. FileName will be in the format [type]\\_[timestamp]\\_[UUID].[format]", example = "comparison_2025-06-05_22-14-10_b67ca23a-28bd-4385-93b8-0068c7794066.png")
    private String comparisonFileName;

    public CompareRequest() { }

    public CompareRequest(String newBaselineFileName, String newComparisonFileName) {
        baselineFileName = newBaselineFileName;
        comparisonFileName = newComparisonFileName;
    }

    public String getBaseline() {
        return baselineFileName;
    }

    public void setBaseline(String newBaselineFileName) {
        baselineFileName = newBaselineFileName;
    }

    public String getComparison() {
        return comparisonFileName;
    }

    public void setComparison(String newComparisonFileName) {
        comparisonFileName = newComparisonFileName;
    }
}
