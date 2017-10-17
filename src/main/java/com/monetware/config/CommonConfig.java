package com.monetware.config;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月28日 下午3:36:38 
 *@describle 公共配置类
 */
@Configuration
public class CommonConfig {
	//MultipartFile上传文件大小配置
	  @Bean
	  public MultipartConfigElement multipartConfigElement() {
	        MultipartConfigFactory factory = new MultipartConfigFactory();
	        factory.setMaxFileSize(100000000*1024L * 1024L);
	        return factory.createMultipartConfig();
	    }
}
