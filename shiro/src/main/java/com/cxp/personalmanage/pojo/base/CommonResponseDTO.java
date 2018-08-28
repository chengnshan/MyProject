package com.cxp.personalmanage.pojo.base;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ASUS on 2017/11/9.
 */
public class CommonResponseDTO {
    @JsonProperty(value = "responseMsg")
    private String responseMsg;
    @JsonProperty(value = "responseCode")
    private String responseCode;
    @JsonProperty(value = "responseObject")
    private Object responseObject;

    public String getResponseMsg() {
        return responseMsg;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public void setResponseObject(Object responseObject) {
        this.responseObject = responseObject;
    }
}
