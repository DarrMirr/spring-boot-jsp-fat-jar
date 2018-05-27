package com.github.DarrMirr.jspinjar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.*;

@SpringBootApplication
public class JspinjarApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(JspinjarApplication.class, args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/hello").setViewName("hello");
	}
}
