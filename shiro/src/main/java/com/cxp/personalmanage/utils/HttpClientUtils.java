package com.cxp.personalmanage.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

	private static final String CONTENT_CHARSET = "UTF-8";
	private static RequestConfig requestConfig = null;

	static {
		requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000)
				.setConnectionRequestTimeout(20000).build();
	}

	/**
	 * Get请求
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static String httpGet(String url, Map<String, String> param) {
		String result = null;
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
		HttpGet httpGet = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(url);
			if (param != null) {
				Set<Map.Entry<String, String>> entrySet = param.entrySet();
				for (Map.Entry<String, String> entry : entrySet) {
					uriBuilder.setParameter(entry.getKey(), entry.getValue());
				}
			}
			httpGet = new HttpGet(uriBuilder.build());

			httpGet.setHeader("Accept", "application/json;charset=" + CONTENT_CHARSET);
			httpGet.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			httpGet.setHeader("Content-type", "application/json;charset=" + CONTENT_CHARSET);
			httpGet.setHeader("Connection", "Close");

			CloseableHttpResponse response = httpClient.execute(httpGet);
			result = EntityUtils.toString(response.getEntity(), "UTF-8");

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			// 关闭连接,释放资源
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * POST请求方式一: 采用请求头为Json数据格式，服务器端采用对象接收并配注解@RequestBody
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static String httpPost(String url, Map<String, Object> map) {
		String result = null;

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
		StringEntity stringEntity = null;
		try {
			stringEntity = new StringEntity(JackJsonUtil.objectToString(map), Charset.forName("UTF-8"));
		} catch (UnsupportedCharsetException e) {
			e.printStackTrace();
		}
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(stringEntity);
		httpPost.setHeader("Accept", "application/json;charset=" + CONTENT_CHARSET);
		httpPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		httpPost.setHeader("Content-type", "application/json;charset=" + CONTENT_CHARSET);
		httpPost.setHeader("Connection", "Close");
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			result = EntityUtils.toString(response.getEntity(), "UTF-8");
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpPost.releaseConnection();
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * post请求方式二： 采用表单形式提交，服务器端用对象接收，不用注解 或者服务器用多个属性接收
	 * 
	 * @param url
	 * @param map
	 * @return
	 */

	public static String httpPost_header(String url, Map<String, String> map) {
		String result = null;
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
		List<NameValuePair> list = new ArrayList<>();
		if (null != map && map.size() > 0) {
			for (Map.Entry<String, String> param : map.entrySet()) {
				list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			}
		}

		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json;charset=" + CONTENT_CHARSET);
		httpPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		httpPost.setHeader("Content-type", "application/x-www-form-urlencoded;charset=" + CONTENT_CHARSET);
		httpPost.setHeader("Connection", "Close");
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "Utf-8");
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			result = EntityUtils.toString(response.getEntity(), "UTF-8");
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpPost.releaseConnection();
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
