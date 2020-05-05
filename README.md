
# SpringBoot原理分析

## 一、SpringBoot介绍与问题

SpringBoot可以基于内嵌的tomcat提供springmvc方式的web服务。它即具有tomcat的servlet容器功能，又具有spring-mvc的web框架功能。也就是说SpringBoot是将tomcat与Spring-mvc进行整合集成的产物。那么就存在以下几个问题：
 
- 1、tomcat等web容器如何去掉web.xml的配置？

- 2、怎么将tomcat的api将其嵌入到java的jar程序中的？

- 3、怎样基于spring-mvc编写tomcat的servlet程序,即如何配置spring的初始化过程？

## 二、Servlet3.0规范

要回答上述的第1个问题，需要从servlet的规范说起。

### 1、Servlet3.0规范

在Servlet3.0版本以前，对于tomcat等web容器来说，编写servelet程序需要存在web.xml文件用于配置容器启动时servlet程序的初始化等相关工作，例如注册可部署servlet，filter（过滤器）和listener（监听器）等。在Servlet3.0版本里，不要求一定存在web.xml文件了。

### 2、Servlet初始化

servlet3.0规范中可以通过javax.servlet.ServletContainerInitializer实现类来完成Servlet程序的初始化功能。每个servlet程序要使用javax.servlet.ServletContainerInitializer做初始化工作，就必须在对应的jar包的META-INF/services目录创建一个名为javax.servlet.ServletContainerInitializer的文件，文件内容指定具体的javax.servlet.ServletContainerInitializer实现类，那么，当web容器启动时就会找到这个初始化器并调用servelet程序编写的初始化工作。

一般伴随着javax.servlet.ServletContainerInitializer一起使用的还有javax.servlet.annotation.HandlesTypes注解，通过javax.servlet.annotation.HandlesTypes可以将感兴趣的一些类注入到javax.servlet.ServletContainerInitializer的onStartup方法作为参数传入。

## 三、 java程序中内嵌tomcat提供web服务

tomcat提供基于java API的接口调用。简单的使用示例代码如下：

```
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
```

参考示例工程代码：[01-tomcat-servlet](01-tomcat-servlet)

## 四、使用spring-mvc简化servlet程序的编写

在编写基于spring-mvc的servlet程序时，会引入spring-webmvc-5.2.5.RELEASE.jar，改jar依赖引入spring-web-5.2.5.RELEASE.jar。而在spring-web-5.2.5.RELEASE.jar中，已经按照上述提及的servlet3.0规范，在META-INF/services/javax.servlet.ServletContainerInitializer文本文件中配置了实现类，其配置的实现类为：

```
org.springframework.web.SpringServletContainerInitializer
```

在该实现类SpringServletContainerInitializer中的实现如下：

```
@HandlesTypes(WebApplicationInitializer.class)
public class SpringServletContainerInitializer implements ServletContainerInitializer {
	@Override
	public void onStartup(@Nullable Set<Class<?>> webAppInitializerClasses, ServletContext servletContext)
			throws ServletException {

		List<WebApplicationInitializer> initializers = new LinkedList<>();

		if (webAppInitializerClasses != null) {
			for (Class<?> waiClass : webAppInitializerClasses) {
				// Be defensive: Some servlet containers provide us with invalid classes,
				// no matter what @HandlesTypes says...
				if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) &&
						WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
					try {
						initializers.add((WebApplicationInitializer)
								ReflectionUtils.accessibleConstructor(waiClass).newInstance());
					}
					catch (Throwable ex) {
						throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
					}
				}
			}
		}

		if (initializers.isEmpty()) {
			servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
			return;
		}

		servletContext.log(initializers.size() + " Spring WebApplicationInitializers detected on classpath");
		AnnotationAwareOrderComparator.sort(initializers);
		for (WebApplicationInitializer initializer : initializers) {
			initializer.onStartup(servletContext);
		}
	}

}
```

从代码中可以看出，其实现过程中又调用了接口org.springframework.web.WebApplicationInitializer的实现类的onStartup()方法。

为此，我们基于spring-mvc编写servlet时，可实现org.springframework.web.WebApplicationInitializer接口，在onStartup()方法里实现spring相关的初始化工作，如下：

```
/**
 * 此处代码可参考：
 * <p>
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html
 * </p>
 *
 */
public class ApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) {
		// Load Spring web application configuration
		// 这里使用基于注解的方式配置Spring的元数据，当然也可以通过xml来配置
		AnnotationConfigWebApplicationContext ac = new AnnotationConfigWebApplicationContext();
		ac.register(MyAppConfig.class);       //注册配置类
		ac.setServletContext(servletContext); //设置web容器的context

		// Create and register the DispatcherServlet
		DispatcherServlet servlet = new DispatcherServlet(ac);
		ServletRegistration.Dynamic registration = servletContext.addServlet("app", servlet);
		registration.setLoadOnStartup(1);
		registration.addMapping("/");
	}

}
```

上述代码中，MyAppConfig为一个使用Spring-mvc注解@Configuration配置的java配置类，在该类里可以配置扫包的路径即HTTP消息的转换器等，代码如下：

```
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
```

然后即可编写各种Controller控制器接口了。

写好的程序打成war包后即可部署到tomcat容器中运行了。

参考示例工程代码：[02-springmvc-web](02-springmvc-web)

参考示例工程代码：[03-tomcat-springmvc](03-tomcat-springmvc)

## 五、基于spring-mvc及内嵌tomcat实现SpringBoot

### 1、整合tomcat与spring

将将第三章嵌入tomcat的代码与第四章中实现WebApplicationInitializer接口初始化spring的代码进行整理，即实现了简易类似SpringBoot的web功能，代码如下：

```
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
```

参考示例工程代码：[04-spring-webserver](04-spring-webserver)

### 2、SpringBoot的实现

SpringBoot将其进行了简化，代码为：

```
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
```

参考示例工程代码：[05-spring-boot](05-spring-boot)


