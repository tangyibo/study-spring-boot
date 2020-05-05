package io.gitee.inrgihc.webmvc.config;

import java.util.List;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan("io.gitee.inrgihc.webmvc")
public class MyAppConfig implements WebMvcConfigurer {

	/**
	 * 配置HTTP消息转换器
	 */
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		// 配置为fastjson的json转换器
		// FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
		// converters.add(converter);

		// 配置为Jackson的json转换器
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converters.add(converter);
	}

}