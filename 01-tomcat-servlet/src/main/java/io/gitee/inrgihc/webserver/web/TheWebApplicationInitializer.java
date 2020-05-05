package io.gitee.inrgihc.webserver.web;

import javax.servlet.ServletContext;

/**
 * web应用初始化：当在spring-boot中，可以在实现类中初始化ApplicationContext等
 * 
 * @author tang
 *
 */
public interface TheWebApplicationInitializer {
	
	void onStartup(ServletContext servletContext);
}
