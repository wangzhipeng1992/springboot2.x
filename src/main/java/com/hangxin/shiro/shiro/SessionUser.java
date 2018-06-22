package com.hangxin.shiro.shiro;

import java.io.Serializable;

public class SessionUser implements Serializable {
    private static final long serialVersionUID = -4743073631282233791L;
    private String account;
    private Integer accountId;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

}
