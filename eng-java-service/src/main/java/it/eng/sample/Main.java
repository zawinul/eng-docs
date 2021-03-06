/* 
 * based on https://dzone.com/articles/spring-boot-2-restful-api-documentation-with-swagg
 * started from https://github.com/RameshMF/spring-boot-tutorial/tree/master/springboot2-jpa-swagger2/src/main/java/net/guides/springboot2/springboot2swagger2 
 * 
 *
 */

package it.eng.sample;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("it.eng.ms.restservice")
@ComponentScan("it.eng.sample")
//@SpringBootApplication(scanBasePackages="it.eng.sample, it.eng.ms.restservice")
@SpringBootApplication
public class Main {
	
	
	public static void main(String[] args) {
		
		ConfigurableApplicationContext ctx;
		ctx = SpringApplication.run(Main.class, args);
		String port = ctx.getEnvironment().getProperty("server.port");
		
		System.out.println("\n\n\tSTARTED ON PORT "+port+"\n");  
		System.out.println("\thttp://localhost:"+port+"/api/v1/...");
		System.out.println("\thttp://localhost:"+port+"/v2/api-docs");
		System.out.println("\thttp://localhost:"+port+"/swagger-ui.html");
	}
}
