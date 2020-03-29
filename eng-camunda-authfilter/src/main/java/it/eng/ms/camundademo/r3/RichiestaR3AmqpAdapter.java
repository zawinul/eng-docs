package it.eng.ms.camundademo.r3;

import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
public class RichiestaR3AmqpAdapter  {

	@Autowired
	private ProcessEngine camunda;

	public RichiestaR3AmqpAdapter() {
	}

	public RichiestaR3AmqpAdapter(ProcessEngine camunda) {
		this.camunda = camunda;
	}


	@RabbitListener(
		bindings = @QueueBinding( 
			value = @Queue(
				value = "richieste_invio_r3", 
				durable = "true"
			), 
			exchange = @Exchange(
				type = "topic", 
				value = "richieste_invio_r3", 
				durable = "true"
			), 
			key = "*"
		)
	)
	@Transactional
	public void onQueueMessage(Message message)  {
		try {
			String json = new String (message.getBody(),"UTF-8");
			RichiestaR3Model r3 = R3Utils.richiestaR3fromJSON(json);
			new R3Utils().startNewWorkflowFromQueue(camunda, r3);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	


}
