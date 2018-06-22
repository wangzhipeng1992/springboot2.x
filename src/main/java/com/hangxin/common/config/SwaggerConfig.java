package com.hangxin.common.config;

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
public class SwaggerConfig {

	@Bean
	public Docket createApi() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.hangxin.controller")).paths(PathSelectors.any())
				// .paths(PathSelectors.regex("/Pe.*"))
				.build();
	}

	private ApiInfo apiInfo() {
		String mail = "zhipengwang1992@163.com";
		return new ApiInfoBuilder().title("spring-boot API")
				.description("Any questions, please email to <a href='mailTo:" + mail + "'>" + mail + "</a>")
				// .termsOfServiceUrl("serviceUrl")
				// .contact(new Contact("name", "url", "email"))
				// .license("license")
				// .licenseUrl("licenseUrl")
				// .version("1.0")
				.build();
	}

}
