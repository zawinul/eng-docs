package it.eng.ms.camundademo.paolo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public class LogListener implements ExecutionListener {

    public void notify(DelegateExecution execution) throws Exception {
    	for(String varName: execution.getVariableNames()) {
    		Object varValue = execution.getVariable(varName);
    		if (varValue!=null)
    			System.out.println("\t"+varName+" ("+varValue.getClass().getName()+") = "+varValue.toString());
    		else
    			System.out.println("\t"+varName+" ("+varValue.getClass().getName()+") = NULL");
    	}
    }
  }