package it.eng.ms.camundademo.r3;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class R3Utils {
	
	public static void mergeJSON(String json, Object container) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		ObjectReader reader = mapper.readerForUpdating(container);
		
		reader.readValue(json);
	}

	
	public static<T> T fromJSON(String json, Class<T> cl) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		T container = cl.getConstructor().newInstance();
		ObjectReader reader = mapper.readerForUpdating(container);
		
		reader.readValue(json);
		return container;
	}

	public static RichiestaR3Model richiestaR3fromJSON(String json) throws Exception {
		RichiestaR3Model r3 = new RichiestaR3Model();
		ObjectMapper mapper = new ObjectMapper();
		ObjectReader reader = mapper.readerForUpdating(r3);
		
		reader.readValue(json);
		return r3;
	}

	public static  String toJSON(RichiestaR3Model model) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String s = mapper.writer().writeValueAsString(model);
		return s;
	}

	public static void startNewWorkflowFromQueue(ProcessEngine camunda, RichiestaR3Model obj) {
		startNewWorkflow(camunda, obj, "richiestaInvioR3MessageQueue");
	}
	public static void startNewWorkflowFromREST(ProcessEngine camunda, RichiestaR3Model obj) {
		startNewWorkflow(camunda, obj, "richiestaInvioR3MessageREST");
	}
	
	public static void startNewWorkflow(ProcessEngine camunda, RichiestaR3Model obj, String msgName) {
		RuntimeService rs = camunda.getRuntimeService();
		String k = (""+(10000+(int) Math.floor(Math.random()*10000))).substring(1);
		MessageCorrelationBuilder mcb;
		mcb = rs.createMessageCorrelation(msgName)
			.processInstanceBusinessKey("r3b "+k)
			.setVariable("destinatario", obj.destinatario)
			.setVariable("oggetto", obj.oggetto)
			.setVariable("mittente", obj.mittente)
			.setVariable("testo", obj.testo);
		
		ProcessInstance pi = mcb.correlateStartMessage();
		System.out.println("bk="+pi.getBusinessKey());
		System.out.println("id="+pi.getId());
		System.out.println("piid="+pi.getProcessInstanceId());
	}
}
