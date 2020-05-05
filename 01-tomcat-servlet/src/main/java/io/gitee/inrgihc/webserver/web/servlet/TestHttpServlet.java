package io.gitee.inrgihc.webserver.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 使用注解@WebServlet注解描述映射关系
 * 
 * <p>
 * 测试地址：http://localhost:8080/test?key=hello
 * </p>
 * 
 * @author tang
 *
 */
@WebServlet("/test")
public class TestHttpServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String value = request.getParameter("key");
		this.getServletContext().log("控制层处理URI的请求:" + request.getPathInfo() + "，参数key值为：" + value);

		String body = "{\"path\":\"" + request.getPathInfo() + "\",\"key\":\"" + value + "\"}";
		PrintWriter out = response.getWriter();
		out.write(body);
		out.flush();
		out.close();

		this.getServletContext().log("控制层处理URI的请求，处理结果为：" + body);
	}

}
