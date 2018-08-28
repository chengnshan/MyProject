package com.cxp.personalmanage.pojo.base;

/**
 * Created by ASUS on 2017/11/12.
 */
public class BaseResultVo {

    private Object resultData;
    private int resultCode;
    private String resultMessage;

    public Object getResultData() {
        return resultData;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public BaseResultVo(Object resultData, int resultCode) {
        this.resultData = resultData;
        this.resultCode = resultCode;
    }

    public BaseResultVo() {
    }

    public BaseResultVo(int resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
}
