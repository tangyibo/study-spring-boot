package io.gitee.inrgihc.webserver.web.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import io.gitee.inrgihc.webserver.web.TheWebApplicationInitializer;
import io.gitee.inrgihc.webserver.web.servlet.TestHttpServlet;

/**
 * 注册Servlet处理器
 * 
 * @author tang
 *
 */
public class MyWebAppInitializerImpl implements TheWebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) {
		servletContext.log("Tomcal 启动过程中注册自己的servlet...");

		ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", new TestHttpServlet());
		registration.setLoadOnStartup(1);
		registration.addMapping("/*");

		servletContext.log("Tomcal 的servlet注册完毕...");
	}

}
