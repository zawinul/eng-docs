/* 
 * based on https://dzone.com/articles/spring-boot-2-restful-api-documentation-with-swagg
 * started from https://github.com/RameshMF/spring-boot-tutorial/tree/master/springboot2-jpa-swagger2/src/main/java/net/guides/springboot2/springboot2swagger2 
 * 
 *
 */

package it.eng.config;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;



@ComponentScan(basePackages = { "it.eng.config", "it.eng.ms.restservice", "it.eng.ms.restservice.security"} )
@SpringBootApplication
@Configuration
public class Main {

	
	public static void main(String[] args) {
		ApplicationContext c = SpringApplication.run(Main.class, args);
		c.getBean(Main.class, c);
		String port = c.getEnvironment().getProperty("server.port");
		System.out.println("\n\n\tSTARTED ON PORT "+port+"\n");
		System.out.println("\thttp://localhost:"+port+"/api/v1/...");
		System.out.println("\thttp://localhost:"+port+"/v2/api-docs");
		System.out.println("\thttp://localhost:"+port+"/swagger-ui.html");
	}
	
	@Bean
	public String servicesBasePackage() {
		return "it.eng.config";
	}
	
}
