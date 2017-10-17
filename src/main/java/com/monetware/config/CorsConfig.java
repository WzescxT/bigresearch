package com.monetware.config;
import org.springframework.context.annotation.Configuration;  
import org.springframework.web.servlet.config.annotation.CorsRegistry;  
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;  
 /**
  * @describle 配置跨域   
  * @author venbill
  *
  */
@Configuration  
public class CorsConfig extends WebMvcConfigurerAdapter {  
  
    @Override  
    public void addCorsMappings(CorsRegistry registry) {  
        registry.addMapping("/**")  
                .allowedOrigins("*")  
                .allowCredentials(true)  
                .allowedMethods("GET", "POST", "DELETE", "PUT")  
                .maxAge(3600);  
    }  
    
    
    
  
}  