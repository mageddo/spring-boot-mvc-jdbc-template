package com.mageddo;

import com.mageddo.controller.CustomerController;
import com.mageddo.utils.SpringUtils;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoader;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

public class Application {

	public static void main(String[] args) throws LifecycleException {
		// http://stackoverflow.com/questions/24915333/how-to-embed-tomcat-in-java
		// http://blog.sortedset.com/embedded-tomcat-jersey/
		// http://www.jofre.de/?p=1227
		SpringUtils.prepareEnv(args);

		final Tomcat tomcat = new Tomcat();
		final Environment env = SpringUtils.getEnv();
		final String ctx = env.getProperty("server.context-path");
		final String docBase = ClassUtils.getDefaultClassLoader().getResource("webapp").getFile();

		final StandardContext context = new StandardContext();
		context.setName(ctx);
		context.setPath(docBase);
		context.addLifecycleListener(new Tomcat.FixContextListener());
		context.setParentClassLoader(ClassUtils.getDefaultClassLoader());
		final WebappLoader loader = new WebappLoader(context.getParentClassLoader());
		loader.setLoaderClass(WebappClassLoader.class.getName());
		
		context.setLoader(loader);
		tomcat.getHost().addChild(context);


//		final String docBase = "/home/system/Dropbox/dev/projects/spring-boot-mvc-jdbc-template/build/classes";
//		tomcat.addContext(ctx, docBase);
//		tomcat.addWebapp(tomcat.getHost(), "/tmp", docBase);
//		final CustomerController customerServlet = new CustomerController();
//		tomcat.addServlet(ctx, customerServlet.getClass().getSimpleName(), customerServlet);
//
		tomcat.setPort(Integer.parseInt(env.getProperty("server.port")));
		tomcat.start();

		tomcat.getServer().await();
	}
}
