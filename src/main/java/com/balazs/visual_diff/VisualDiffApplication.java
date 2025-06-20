package com.balazs.visual_diff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class VisualDiffApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisualDiffApplication.class, args);
	}
}
