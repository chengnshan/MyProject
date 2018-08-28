package com.cxp.personalmanage.controller.base;

import com.cxp.personalmanage.pojo.base.BaseResultVo;
import com.cxp.personalmanage.pojo.base.CommonRequestDTO;
import com.cxp.personalmanage.pojo.base.CommonResponseDTO;
import com.cxp.personalmanage.utils.JackJsonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
/**
 * Created by ASUS on 2017/11/10.
 */
public class BaseController {
    static final Logger logger = LogManager.getLogger(BaseController.class.getName());

    /**
     * 返回成功结构
     *
     * @param resultData
     * @return
     */
    protected String buildSuccessResultInfo(Object resultData) {
        logger.info(String.format("enter function,%s", resultData));
        BaseResultVo resultVo = new BaseResultVo();
        resultVo.setResultData(resultData);
        resultVo.setResultMessage("success");
        String result = JackJsonUtil.objectToString(resultVo);
        logger.info(String.format("exit function,%s", result));
        return result;
    }

    /**
     * 成功返回结构
     *
     * @param resultCode
     * @param resultData
     * @return
     */
    protected String buildSuccessResultInfo(int resultCode, Object resultData) {
        logger.info(String.format("enter function,%s", resultData));
        BaseResultVo resultVo = new BaseResultVo();
        resultVo.setResultData(resultData);
        resultVo.setResultCode(resultCode);
        resultVo.setResultMessage("success");
        String result = JackJsonUtil.objectToString(resultVo);
        logger.info(String.format("exit function,%s", result));
        return result;
    }

    /**
     * 失败返回结果
     *
     * @param resultCode
     * @param failedMsg
     * @return
     */
    protected String buildFailedResultInfo(int resultCode, String failedMsg) {
        BaseResultVo resultVo = new BaseResultVo(resultCode, failedMsg);
        return JackJsonUtil.objectToString(resultVo);
    }

    /**
     * 返回JsonP对象或Json对象字符串
     *
     * @param callback
     * @param data
     * @return
     */
    protected String returnJsonResult(String callback, Object data) {
        return StringUtils.isNotBlank(callback) ? buildSuccessResultInfo(data) :
                callback + "(" + buildSuccessResultInfo(data) + ")";
    }

    /**
     * 返回成功API格式数据
     *
     * @param responseObj
     * @return
     */
    protected CommonResponseDTO buildApiSuccessResultInfo(Object responseObj) {
        CommonResponseDTO response = new CommonResponseDTO();
        response.setResponseCode("life-00001");
        response.setResponseMsg("success");
        response.setResponseObject(responseObj);
        return response;
    }

    /**
     * 返回失败API格式数据
     *
     * @param responseObj
     * @return
     */
    protected CommonResponseDTO buildApiFailedResultInfo(Object responseObj) {
        CommonResponseDTO response = new CommonResponseDTO();
        response.setResponseCode("life-99999");
        response.setResponseMsg(responseObj.toString());
        return response;
    }

    /**
     * 返回失败API格式数据
     *
     * @param responseObj
     * @return
     */
    protected CommonResponseDTO buildApiFailedResultInfo(String code,Object responseObj) {
        CommonResponseDTO response = new CommonResponseDTO();
        response.setResponseCode(code);
        response.setResponseObject(responseObj.toString());
        return response;
    }

    /**
     * 请求异常，熔断异常
     *
     * @param requestDTO
     * @return
     */
    protected CommonResponseDTO apiError(CommonRequestDTO requestDTO) {
        CommonResponseDTO response = new CommonResponseDTO();
        response.setResponseCode("life-83334");
        response.setResponseMsg("请求异常");
        return response;
    }
}

