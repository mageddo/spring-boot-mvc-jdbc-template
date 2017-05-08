package com.mageddo;

import com.mageddo.controller.CustomerController;
import com.mageddo.dao.DatabaseBuilderDao;
import com.mageddo.utils.SpringUtils;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoader;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

import javax.servlet.ServletException;
import java.lang.management.ManagementFactory;

public class Application {

	public static void main(String[] args) throws LifecycleException, ServletException {
		// http://stackoverflow.com/questions/24915333/how-to-embed-tomcat-in-java
		// http://blog.sortedset.com/embedded-tomcat-jersey/
		// http://www.jofre.de/?p=1227
		SpringUtils.prepareEnv(args);
		new DatabaseBuilderDao().buildDatabase();

		final Tomcat tomcat = new Tomcat();
		final Environment env = SpringUtils.getEnv();
		final String ctxPath = env.getProperty("server.context-path");
		final String docBase = ClassUtils.getDefaultClassLoader().getResource("webapp").getFile();
// v2
//		final StandardContext context = new StandardContext();
//		context.setName(ctxPath);
//		context.setPath(docBase);
//		context.addLifecycleListener(new Tomcat.FixContextListener());
//		context.setParentClassLoader(ClassUtils.getDefaultClassLoader());
//		final WebappLoader loader = new WebappLoader(context.getParentClassLoader());
//		loader.setLoaderClass(WebappClassLoader.class.getName());
//
//		context.setLoader(loader);
//		tomcat.getHost().addChild(context);

// v3
		final Context ctx = tomcat.addWebapp(ctxPath, docBase);
		ctx.setParentClassLoader(ClassUtils.getDefaultClassLoader());
//		final CustomerController customerServlet = new CustomerController();
//		tomcat.addServlet(ctxPath, customerServlet.getClass().getSimpleName(), customerServlet);

// v1
//		final String docBase = "/home/system/Dropbox/dev/projects/spring-boot-mvc-jdbc-template/build/classes";
//		tomcat.addContext(ctxPath, docBase);
//		tomcat.addWebapp(tomcat.getHost(), "/tmp", docBase);
//		final CustomerController customerServlet = new CustomerController();
//		tomcat.addServlet(ctxPath, customerServlet.getClass().getSimpleName(), customerServlet);
//
		WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(new DirResourceSet(
			resources, "/WEB-INF/classes",
			"/home/system/Dropbox/dev/projects/spring-boot-mvc-jdbc-template/build/classes",
			"/"
		));
		ctx.setResources(resources);

		tomcat.setPort(Integer.parseInt(env.getProperty("server.port")));
		tomcat.start();

		System.out.println(ManagementFactory.getRuntimeMXBean().getName());
		tomcat.getServer().await();

	}
}
