package com.mageddo;

import com.mageddo.utils.SpringUtils;
import org.springframework.core.env.Environment;

public class Application {

	public static void main(String[] args) {

		SpringUtils.prepareEnv(args);
		final Environment env = SpringUtils.getEnv();

		System.out.println(env.getProperty("logging.level.root"));

	}
}
