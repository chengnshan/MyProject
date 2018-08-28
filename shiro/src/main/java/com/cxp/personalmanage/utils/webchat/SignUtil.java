package com.cxp.personalmanage.utils.webchat;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import net.sf.json.JSONObject;

/**
 * ClassName: SignUtil
 *
 * @author dapengniao
 * @Description: 请求校验工具类
 * @date 2016 年 3 月 4 日 下午 6:25:41
 */
public class SignUtil {
    // 与接口配置信息中的 Token 要一致
    private static String token = "cheng3_shan3_webchat";

    /**
     * 验证签名
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        String[] arr = new String[]{token, timestamp, nonce};
        // 将 token、timestamp、nonce 三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行 sha1 加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        content = null;
        // 将 sha1 加密后的字符串可与 signature 对比，标识该请求来源于微信
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        String s = new String(tempArr);
        return s;
    }

    public static String getToken(String AppID, String AppSecret, String access_token) {
        HttpURLConnection conn = null;
        if (access_token == null || access_token.equals("")) {
            try {
                String Strurl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + AppID + "&secret=" + AppSecret;
                URL url = new URL(Strurl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                    InputStream in = conn.getInputStream();
                    //      String backcontent= IOUtil.readString(in);
                    List<String> readLines = IOUtils.readLines(in);
                    String backcontent = readLines.get(0);
                    backcontent = URLDecoder.decode(backcontent, "UTF-8");

                    JSONObject json = JSONObject.fromObject(backcontent);
                    access_token = json.getString("access_token");
                    if (access_token != null) {
                        System.out.println("获取token成功：" + access_token);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
        } else {
            System.out.println("token获取失败或尚未失效：" + access_token);
        }
        return access_token;
    }

    public static void main(String[] args) {
        getToken("wx6354e8685cb793b1", "73833d9e27d5773d53ea4ddeabff5f48", null);
    }
}
