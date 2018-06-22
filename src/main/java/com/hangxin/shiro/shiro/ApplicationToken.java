package com.hangxin.shiro.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

public class ApplicationToken extends UsernamePasswordToken {

    private static final long serialVersionUID = -5231798406820075026L;
    
    private String application;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public ApplicationToken(String application) {
        super();
        this.application = application;
    }

}
