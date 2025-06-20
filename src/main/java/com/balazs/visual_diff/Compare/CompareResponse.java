package com.balazs.visual_diff.Compare;

import org.springframework.hateoas.RepresentationModel;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CompareResponse", description = "This response object is returned for all endpoints in the CompareController. It includes a baselineFileName, the comparisonFileName, diffFileName, and related links for discovery.")
public class CompareResponse extends RepresentationModel<CompareResponse> {
    
    @Schema(description = "The 'fileName' of the baseline file compared. FileName will be in the format [type]\\_[UUID].[format]", example = "baseline_0a862592-3fc2-46a7-b74a-65261c5dec6e.png")
    private String baselineFileName;
    
    @Schema(description = "The 'fileName' of the comparison file compared. FileName will be in the format [type]\\_[UUID].[format]", example = "comparison_b67ca23a-28bd-4385-93b8-0068c7794066.png")
    private String comparisonFileName;
    
    @Schema(description = "The 'fileName' of the diff file produced. FileName will be in the format [type]\\_[UUID].[format]", example = "diff_a46b02ce-49d4-4c7c-b95f-510ad01c9477.png")
    private String diffFileName;
    
    public CompareResponse() { }

    public CompareResponse(String newBaselineFileName, String newComparisonFileName, String newDiffFileName) {
        baselineFileName = newBaselineFileName;
        comparisonFileName = newComparisonFileName;
        diffFileName = newDiffFileName;
    }

    public String getBaselineFileName() {
        return baselineFileName;
    }

    public void setBaselineFileName(String newBaselineFileName) {
        baselineFileName = newBaselineFileName;
    }

    public String getComparisonFileName() {
        return comparisonFileName;
    }

    public void setComparisonFileName(String newComparisonFileName) {
        comparisonFileName = newComparisonFileName;
    }

    public String getDiffFileName() {
        return diffFileName;
    }

    public void setDiffFileName(String newDiffFileName) {
        diffFileName = newDiffFileName;
    }
}
