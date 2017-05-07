package com.mageddo;

import com.mageddo.utils.SpringUtils;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.core.env.Environment;

public class Application {

	public static void main(String[] args) throws LifecycleException {

		SpringUtils.prepareEnv(args);

		final Environment env = SpringUtils.getEnv();
		final Tomcat servletContainer = new Tomcat();
//		servletContainer.addContext(env.getProperty("server.context-path"), "webapp");
		servletContainer.setPort(Integer.parseInt(env.getProperty("server.port")));
		servletContainer.start();
		servletContainer.getServer().await();
	}
}
