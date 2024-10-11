package com.budgetBook.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.budgetBook.common.FileManager;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	// 이미지 url Path 생성 설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**")
		.addResourceLocations("file:///" + FileManager.FILE_UPLOAD_PATH + "/"); // Mac이나 리눅스는 //, 윈도우는 ///
	}
}
