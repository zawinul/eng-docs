package it.eng.ms.camundademo.paolo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class PaoloAdapter implements JavaDelegate {

	public Expression aaa, bbb, text2;

	@Override
	public void execute(DelegateExecution ctx) throws Exception {
		Object aval = aaa.getValue(ctx);
		ctx.setVariable("pippo", "pluto " + aval.toString());
		ctx.setVariable("mydouble", Math.floor(Math.random()*10000)/100.);
	}

	public void setBbb(Expression x) {
		bbb = x;
	}

	public void setAaa(Expression x) {
		aaa = x;
	}

	public void setText2(org.camunda.bpm.engine.delegate.Expression x) {
		text2 = x;
	}
}
