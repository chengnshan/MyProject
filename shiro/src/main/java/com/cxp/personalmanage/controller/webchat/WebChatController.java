package com.cxp.personalmanage.controller.webchat;

import com.cxp.personalmanage.utils.webchat.MessageUtil;
import com.cxp.personalmanage.utils.webchat.SignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

@Controller
@RequestMapping(value = "/webchat")
public class WebChatController {
    private final Logger logger = LoggerFactory.getLogger(WebChatController.class);

    @RequestMapping(value = "/security")
    public String testChat(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(value = "signature", required = true) String signature,
                           @RequestParam(value = "timestamp", required = true) String timestamp,
                           @RequestParam(value = "nonce", required = true) String nonce,
                           @RequestParam(value = "echostr", required = true) String echostr) {
        System.out
                .println("signature : " + signature + " ,timestamp :" + timestamp + ", nonce : " + nonce + " , echostr : " + echostr);
        try {
            if (SignUtil.checkSignature(signature, timestamp, nonce)) {
                PrintWriter out = response.getWriter();
                out.print(echostr);
                out.close();
            } else {
                logger.info("这里存在非法请求！");
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    @RequestMapping(value = "/security", method = RequestMethod.POST)
    // post 方法用于接收微信服务端消息
    public void receiverMsg(HttpServletRequest request, HttpServletResponse response) {
        // 防止中文乱码
        response.setCharacterEncoding("UTF-8");
        System.out.println("这是 post 方法！");
        try {

            Map<String, String> map = MessageUtil.parseXml(request);
            
            System.out.println("=============================" + map.get("Content"));
            
            MessageUtil.replyMessage(response.getWriter(), map);
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
