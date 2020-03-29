package it.eng.ms.camundademo.r3;

import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/r3/richiesta-invio")
public class R3RestController {

	@Autowired
	private ProcessEngine camunda;

	@RequestMapping(method = RequestMethod.POST)
	public String richiediInvioR3POST(@RequestBody RichiestaR3Model obj) {
		System.out.println(obj);
		R3Utils cu = new R3Utils();
		cu.startNewWorkflowFromREST(camunda, obj);
		return "OK";
	}
}
