package it.eng.ms.camundademo.adapter;

import it.eng.ms.camundademo.ProcessConstants;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendShipGoodsAmqpAdapter implements JavaDelegate {

  @Autowired
  protected RabbitTemplate rabbitTemplate;
  
  @Override
  public void execute(DelegateExecution ctx) throws Exception {
    String orderId = (String) ctx.getVariable(ProcessConstants.VAR_NAME_orderId);    
    
    String exchange = "shipping";
    String routingKey = "createShipment";
    
    rabbitTemplate.convertAndSend(exchange, routingKey, orderId);
  }

}
