package it.eng.ms.camundademo.adapter;

import it.eng.ms.camundademo.ProcessConstants;

import java.util.List;
import java.util.UUID;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;
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
public class AmqpReceiver {

	@Autowired
	private ProcessEngine camunda;

	public AmqpReceiver() {
	}

	public AmqpReceiver(ProcessEngine camunda) {
		this.camunda = camunda;
	}

	/**
	 * Dummy method to handle the shipGoods command message - as we do not have
	 * a shipping system available in this small example
	 */
	@RabbitListener(bindings = @QueueBinding( //
	value = @Queue(value = "shipping_create_test", durable = "true"), exchange = @Exchange(type = "topic", value = "shipping", durable = "true"), key = "*"))
	@Transactional
	public void dummyShipGoodsCommand(String orderId) {
		doit(orderId);
	}

	public void doit(String orderId) {
		// and call back directly with a generated transactionId
		try {
			handleGoodsShippedEvent(orderId, UUID.randomUUID().toString());
		} catch (Exception e) {
			System.out.println("\t" + e.getMessage());
		}
		System.out.println("\n\t\t\tFUORI DAL TRY CATCH\n");
	}

	public void handleGoodsShippedEvent(String orderId, String shipmentId) {

		RuntimeService rs = camunda.getRuntimeService();

		MessageCorrelationBuilder mcb;
		mcb = rs.createMessageCorrelation(ProcessConstants.MSG_NAME_GoodsShipped);
		mcb = mcb.processInstanceVariableEquals(ProcessConstants.VAR_NAME_orderId, orderId);
		mcb = mcb.setVariable(ProcessConstants.VAR_NAME_shipmentId, shipmentId);

		@SuppressWarnings("unused")
		List<MessageCorrelationResult> result = mcb.correlateAllWithResult();
	}
}
