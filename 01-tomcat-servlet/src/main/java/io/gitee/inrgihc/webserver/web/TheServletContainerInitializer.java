package io.gitee.inrgihc.webserver.web;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

/**
 * 模仿spring-web实现servlet注册
 * 
 * @author tang
 *
 */
@HandlesTypes(TheWebApplicationInitializer.class)
public class TheServletContainerInitializer implements ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> webAppInitializerClasses, ServletContext servletContext)
			throws ServletException {
		List<TheWebApplicationInitializer> initializers = new LinkedList<TheWebApplicationInitializer>();

		// webAppInitializerClasses 就是servlet3.0规范中为我们收集的 WebApplicationInitializer
		// 接口的实现类的class
		// 从webAppInitializerClasses中筛选并实例化出合格的相应的类
		if (webAppInitializerClasses != null) {
			for (Class<?> waiClass : webAppInitializerClasses) {
				// Be defensive: Some servlet containers provide us with invalid classes,
				// no matter what @HandlesTypes says...
				if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers())
						&& TheWebApplicationInitializer.class.isAssignableFrom(waiClass)) {
					try {
						initializers.add((TheWebApplicationInitializer) waiClass.newInstance());
					} catch (Throwable ex) {
						throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
					}
				}
			}
		}

		if (initializers.isEmpty()) {
			servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
			return;
		}

		servletContext.log("Spring WebApplicationInitializers detected on classpath: " + initializers);
		for (TheWebApplicationInitializer initializer : initializers) {
			initializer.onStartup(servletContext);
		}

	}

}
