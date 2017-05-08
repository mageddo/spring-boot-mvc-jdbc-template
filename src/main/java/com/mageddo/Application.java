package com.mageddo;

import com.mageddo.controller.CustomerController;
import com.mageddo.utils.SpringUtils;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

public class Application {

	public static void main(String[] args) throws LifecycleException {
		// http://stackoverflow.com/questions/24915333/how-to-embed-tomcat-in-java
		// http://blog.sortedset.com/embedded-tomcat-jersey/
		// http://www.jofre.de/?p=1227
		SpringUtils.prepareEnv(args);

		final Environment env = SpringUtils.getEnv();
		final Tomcat servletContainer = new Tomcat();


		final String ctx = env.getProperty("server.context-path");
		final String docBase = ClassUtils.getDefaultClassLoader().getResource("webapp").getFile();
//		final String docBase = "/home/system/Dropbox/dev/projects/spring-boot-mvc-jdbc-template/build/classes";
		servletContainer.addContext(ctx, docBase);
//		servletContainer.addWebapp(servletContainer.getHost(), "/tmp", docBase);
		final CustomerController customerServlet = new CustomerController();
		servletContainer.addServlet(ctx, customerServlet.getClass().getSimpleName(), customerServlet);

		servletContainer.setPort(Integer.parseInt(env.getProperty("server.port")));
		servletContainer.start();

		servletContainer.getServer().await();
	}
}
