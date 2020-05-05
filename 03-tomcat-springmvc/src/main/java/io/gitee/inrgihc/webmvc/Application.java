package io.gitee.inrgihc.webmvc;

import java.io.File;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

/**
 * 参考tomcat的嵌入式使用:
 * <p>
 * https://www.cnblogs.com/lmq-1048498039/p/8329481.html
 * </p>
 * 
 * @author tang
 *
 */
public class Application {

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
