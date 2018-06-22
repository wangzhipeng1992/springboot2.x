package com.hangxin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "market", produces = MediaType.APPLICATION_JSON_VALUE)
public class HelloWorldController {

	@Value("${server.port}")
	private String port;
	
	@RequestMapping(path = "/hello", method = RequestMethod.POST)
	@ApiOperation(value = "1.1", notes = "hello")
	public String hello() {
		System.out.println(port);
		return "Hello World";
	}

}
