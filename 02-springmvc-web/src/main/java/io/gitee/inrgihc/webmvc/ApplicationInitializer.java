package io.gitee.inrgihc.webmvc;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import io.gitee.inrgihc.webmvc.config.MyAppConfig;

/**
 * 此处代码转自：
 * <p>
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html
 * </p>
 * 
 * @author tang
 *
 */
public class ApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) {
		// Load Spring web application configuration
		AnnotationConfigWebApplicationContext ac = new AnnotationConfigWebApplicationContext();
		ac.register(MyAppConfig.class);
		ac.setServletContext(servletContext);
		
		// Create and register the DispatcherServlet
		DispatcherServlet servlet = new DispatcherServlet(ac);
		ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
		registration.setLoadOnStartup(1);
		registration.addMapping("/");
	}

}
