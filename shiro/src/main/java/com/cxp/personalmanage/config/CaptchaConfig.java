package com.cxp.personalmanage.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

@Configuration
public class CaptchaConfig {
	@Bean(name = "captchaProducer")
	public DefaultKaptcha getKaptchaBean() {
		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		Properties properties = new Properties();
		//<!-- 是否有边框 -->
		properties.setProperty("kaptcha.border", "yes");
		//<!-- 边框颜色 --> 
		properties.setProperty("kaptcha.border.color", "105,179,90");
		//<!-- 验证码字体颜色 -->  
		properties.setProperty("kaptcha.textproducer.font.color", "blue");
		//<!-- 验证码宽度 -->
		properties.setProperty("kaptcha.image.width", "100");
		//		/<!-- 验证码高度 -->
		properties.setProperty("kaptcha.image.height", "34");
		//<!-- 验证码字体大小 --> 
		properties.setProperty("kaptcha.textproducer.font.size", "30");
		properties.setProperty("kaptcha.session.key", "code");
		// <!-- 验证码个数 -->
		properties.setProperty("kaptcha.textproducer.char.length", "4");
		//<!-- 验证码所属字体样式 --> 
		properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
		//<!-- 生成验证码内容范围 -->  
		properties.setProperty("kaptcha.textproducer.char.string","13456789ABCEFGHIJKLMNPQRSTUVWXY");
		properties.setProperty("kaptcha.obscurificator.impl","com.google.code.kaptcha.impl.FishEyeGimpy");
		// <!-- 干扰线颜色 -->
		properties.setProperty("kaptcha.noise.color","white");
		properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.DefaultNoise");
		properties.setProperty("kaptcha.background.clear.from","185,56,213");
		properties.setProperty("kaptcha.background.clear.to","white");
		//<!-- 验证码文本字符间距 --> 
		properties.setProperty("kaptcha.textproducer.char.space","5");
		Config config = new Config(properties);
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}
}
