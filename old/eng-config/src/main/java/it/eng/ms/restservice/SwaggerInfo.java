/* 
 * based on https://dzone.com/articles/spring-boot-2-restful-api-documentation-with-swagg
 * started from https://github.com/RameshMF/spring-boot-tutorial/tree/master/springboot2-jpa-swagger2/src/main/java/net/guides/springboot2/springboot2swagger2 
 * 
 *
 */

package it.eng.ms.restservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerInfo {
	
	@Autowired
	public String servicesBasePackage;
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage(servicesBasePackage))
				.paths(PathSelectors.regex("/.*"))
				.build().apiInfo(apiEndPointsInfo());
	}

	private ApiInfo apiEndPointsInfo() {

		return new ApiInfoBuilder().title("Engineering sample REST API")
				.description("Contains swagger")
				.version("1.0.0")
				.build();
	}
}
