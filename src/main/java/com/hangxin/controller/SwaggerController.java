package com.hangxin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class SwaggerController {

	@RequestMapping("/showApi")
	public String showApi() {
		return "redirect:/swagger-ui.html";
	}

}
