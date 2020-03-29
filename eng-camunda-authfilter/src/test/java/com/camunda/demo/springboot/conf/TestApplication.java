package com.camunda.demo.springboot.conf;

import static org.mockito.Mockito.mock;
import it.eng.ms.camundademo.EngCamundaAuthFilterApplication;

import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses={EngCamundaAuthFilterApplication.class}) 
@EnableAutoConfiguration(exclude={RabbitAutoConfiguration.class})
@TestConfiguration
public class TestApplication {

  @Mock  
  private RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
  
  @Bean
  protected RabbitTemplate rabbitTemplate() {
    return rabbitTemplate;
  }
  
}
