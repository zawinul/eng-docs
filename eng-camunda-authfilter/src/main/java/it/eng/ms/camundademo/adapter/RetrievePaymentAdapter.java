package it.eng.ms.camundademo.adapter;

import it.eng.ms.camundademo.ProcessConstants;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


// PAOLO
// RetrievePaymentAdapter è un delegate quindi posso usarlo in un service task settando nel modeler
//     delegateExpression=#{retrievePaymentAdapter} 
// che corrisponde nel BPMN a
//     <serviceTask id="..." name="..." camunda:delegateExpression="#{retrievePaymentAdapter}">...

@Component
@ConfigurationProperties
public class RetrievePaymentAdapter implements JavaDelegate {

	@Override
	public void execute(DelegateExecution ctx) throws Exception {
		CreateChargeRequest request = new CreateChargeRequest();
		request.amount = (int) ctx.getVariable(ProcessConstants.VAR_NAME_amount);

		CreateChargeResponse response = rest.postForObject( 
			restEndpoint(), 
			request, 
			CreateChargeResponse.class
		);

		ctx.setVariable(ProcessConstants.VARIABLE_paymentTransactionId,	response.transactionId);
	}

	@Autowired
	private RestTemplate rest;

	private String restProxyHost;
	private String restProxyPort;

	private String restEndpoint() {
		return "http://" + restProxyHost + ":" + restProxyPort + "/payment/charges";
	}

	public static class CreateChargeRequest {
		public int amount;
	}

	public static class CreateChargeResponse {
		public String transactionId;
	}


	public String getRestProxyHost() {
		return restProxyHost;
	}

	public void setRestProxyHost(String restProxyHost) {
		this.restProxyHost = restProxyHost;
	}

	public String getRestProxyPort() {
		return restProxyPort;
	}

	public void setRestProxyPort(String restProxyPort) {
		this.restProxyPort = restProxyPort;
	}

}
