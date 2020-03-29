package it.eng.ms.camundademo.paolo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

/**
 * In this listener you can do basically everything. You could also implement it as 
 * Spring bean an e.g. send events via Kafka to a central monitor - or to call the ELK stack
 * to hand over some events.
 */
public class PaoloListener implements ExecutionListener {
	
	@Override
	public void notify(DelegateExecution x) throws Exception {
		System.out.println("From Paolo. There was an event '" + x.getEventName() + "'! It came from activity '"+x.getCurrentActivityId()+"' for process instance '" + x.getProcessInstanceId() + "'");
	}

	// FIELD injection
	public void setAaa(String x){
		System.out.println("aaa="+x);
	}
	public void setBbb(String x){
		System.out.println("bbb="+x);
	}

}
