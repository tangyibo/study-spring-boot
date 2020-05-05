package io.gitee.inrgihc.webmvc;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import io.gitee.inrgihc.webmvc.config.MyAppConfig;

public class WebApplication implements WebApplicationInitializer {
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		// Load Spring web application configuration
		AnnotationConfigWebApplicationContext ac = new AnnotationConfigWebApplicationContext();
		ac.register(MyAppConfig.class);
		//ac.refresh();

		// Create and register the DispatcherServlet
		DispatcherServlet servlet = new DispatcherServlet(ac);
		ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
		registration.setLoadOnStartup(1);
		registration.addMapping("/*");
	}

	public static void main(String[] args) throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		Server server = tomcat.getServer();

		Service service = tomcat.getService();
		service.setName("Tomcat-embbeded");

		Connector connector = new Connector("HTTP/1.1");
		connector.setPort(8080);
		service.addConnector(connector);

		// 这里不考虑jsp解析器的异常
		// 即忽略：java.lang.ClassNotFoundException: org.apache.jasper.servlet.JspServlet
		tomcat.addWebapp("", System.getProperty("user.dir") + File.separator);

		server.start();
		System.out.println("tomcat服务器启动成功..");

		server.await();
	}

}
