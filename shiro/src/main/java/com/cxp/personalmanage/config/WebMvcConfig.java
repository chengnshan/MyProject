package com.cxp.personalmanage.config;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter{

	//输入http://localhost:8088 即可跳转到主页面
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
//		registry.addViewController("/index.html").setViewName("html/index.html");
//		registry.addViewController("/login.html").setViewName("html/login.html");
//		registry.addViewController("/portfolio.html").setViewName("html/template/portfolio");
//		registry.addViewController("/kickoutUser.html").setViewName("html/login.html");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		super.addViewControllers(registry);
	}
}
