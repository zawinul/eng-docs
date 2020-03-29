package it.eng.ms.camundademo;

import org.camunda.bpm.spring.boot.starter.SpringBootProcessApplication;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
//@EnableProcessApplication
public class EngCamundaAuthFilterApplication extends SpringBootProcessApplication {

	public static void main(String... args) {
		ConfigurableApplicationContext ctx;
		ctx = SpringApplication.run(EngCamundaAuthFilterApplication.class, args);
		System.out.println(ctx.getApplicationName() + "\n\n STARTED\n\n");
		
		//ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();
		//DummyDefaultUser.create(engine);
	}
	

}
