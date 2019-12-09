package it.eng.model;

import it.eng.model.lib.JSONUtils;

public class EsempioSwagger {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new EsempioSwagger().test();

	}
	
	public void test() {
		SwaggerModel swaggerModel = new SwaggerModel(
			new SwaggerModel.Info("my title", "my description", "my version"),
			new SwaggerModel.Server[] {
				new SwaggerModel.Server("http://myserver:80"),
				new SwaggerModel.Server("https://myserver:43"),
			},
			new Path[]{
				new Path("/service/:servicename/info", new Method[]{
						new Method("get", new SwaggerModel.MethodDefinition(
							"bla bla bla",
							new Parameter[] {
								new Parameter("servicename", new SwaggerModel.ParameterDefinition("pippo"))	
							} 
						)),
						new Method("post", new SwaggerModel.MethodDefinition(
							"e ancora bla",
							new Parameter[] {
								new Parameter("servicename", new SwaggerModel.ParameterDefinition("pluto"))	
							} 
						)),
				})
			}
		);
		
		String json = JSONUtils.toYAML(swaggerModel);
		System.out.println(json);
			
	}

}
