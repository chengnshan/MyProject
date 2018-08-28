package com.cxp.personalmanage.pojo.base;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ASUS on 2017/11/8.
 */
public class CommonRequestDTO {
    @JsonProperty(value = "requestId")
    private String requestId;
    @JsonProperty(value = "requestUser")
    private String requestUser;
    @JsonProperty(value = "requestObject")
    private Object requestObject;

    public String getRequestId() {
        return requestId;
    }

    public String getRequestUser() {
        return requestUser;
    }

    public Object getRequestObject() {
        return requestObject;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setRequestUser(String requestUser) {
        this.requestUser = requestUser;
    }

    public void setRequestObject(Object requestObject) {
        this.requestObject = requestObject;
    }

    @Override
    public String toString() {
        return "CommonDTO{" +
                "requestId='" + requestId + '\'' +
                ", requestUser='" + requestUser + '\'' +
                ", requestObject=" + requestObject +
                '}';
    }
}
