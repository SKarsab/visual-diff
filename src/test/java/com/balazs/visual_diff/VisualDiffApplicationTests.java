package com.balazs.visual_diff;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.balazs.visual_diff.Blob.BlobController;
import com.balazs.visual_diff.Blob.BlobService;
import com.balazs.visual_diff.Compare.CompareController;
import com.balazs.visual_diff.Compare.CompareService;

@SpringBootTest
class VisualDiffApplicationTests {

	@Autowired
	private CompareController compareController;

	@Autowired
	private CompareService compareService;

	@Autowired
	private BlobController blobController;

	@Autowired
	private BlobService blobService;

	@Test
	@DisplayName("Sanity check to ensure that controllers and services are being created")
	public void contextLoads() {
		//Assert
		assertThat(compareController).isNotNull();
		assertThat(compareService).isNotNull();
		assertThat(blobController).isNotNull();
		assertThat(blobService).isNotNull();
	}
}
