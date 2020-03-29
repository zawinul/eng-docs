package it.eng.ms.camundademo.conf;


import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("cloud")
public class RabbitMqCloudConfiguration extends AbstractCloudConfig {

  @Bean
  public ConnectionFactory rabbitConnectionFactory() {
    return connectionFactory().rabbitConnectionFactory();
  }
}
