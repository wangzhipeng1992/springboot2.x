package com.hangxin.basic;

import java.io.Serializable;

/**
 * 
 * return标识base类
 * 
 * @author wang
 * @since 2018-05-29 10:11
 *
 */
public class Result implements Serializable {

    private static final long serialVersionUID = -8681744122101661997L;
    
    private String resultCode = ResultCode.RESULT_SUCCESS;
    private String resultDesc;
    private Object resultData;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public Object getResultData() {
        return resultData;
    }

    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }

    public static Result result(String resultCode) {
        return result(resultCode, ResultCode.getValueByKey(resultCode));
    }

    public static Result result(String resultCode, String desc) {
        Result result = new Result();
        result.setResultCode(resultCode);
        result.setResultDesc(desc);
        return result;
    }
}
