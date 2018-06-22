package com.hangxin.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "jenkins", produces = MediaType.APPLICATION_JSON_VALUE)
public class JenkinsController {

    @RequestMapping(path = "/jenkins", method = RequestMethod.POST)
    @ApiOperation(value = "jenkins 1.1", notes = "Jenkins自动部署测试一")
    public String JenkinsName() {
        String JenkinsBuildAutoMaticResult = new String("Success");
        return JenkinsBuildAutoMaticResult.toString();
    }
    
}
