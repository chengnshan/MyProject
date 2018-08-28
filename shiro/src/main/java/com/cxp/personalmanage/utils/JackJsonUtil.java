package com.cxp.personalmanage.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JackJsonUtil {

	private final static ObjectMapper objectMapper = new ObjectMapper();

	private JackJsonUtil() {

	}

	public static ObjectMapper getInstance() {
		return objectMapper;
	}
	
	/**是否是JSON字符串
     * 暴力解析:Alibaba fastjson
     *
     * @param test
     * @return
     */
   /* public final static boolean isJSONValid(String test) {
        try {
            JSONObject.parseObject(test);
        } catch (JSONException ex) {
            try {
                JSONObject.parseArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }*/


    /**是否是JSON字符串
     * Jackson library
     *
     * @param jsonInString
     * @return
     */
    public final static boolean isJSONValid2(String jsonInString) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

	/**
	 * javaBean、列表数组转换为json字符串
	 */
	public static String obj2json(Object obj) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		return objectMapper.writeValueAsString(obj);
	}

	/**
	 * javaBean、列表数组转换为json字符串,忽略空值
	 * 
	 * @param obj
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String objectToString(Object obj){
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		SimpleDateFormat dft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objectMapper.setDateFormat(dft);
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		 return null;
	}

	/**
	 * json 转JavaBean
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public static <T> T jsonToObject(String jsonString, Class<T> clazz){
		// 配置为false表示mapper在遇到mapper对象中存在json对象中没有的数据变量时不报错，可以进行反序列化
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 配置为true表示mapper接受只有一个元素的数组的反序列化
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		// objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		T readValue = null;
		try {
			readValue = objectMapper.readValue(jsonString, clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readValue;
	}

	/**
	 * json字符串转换为map
	 * 
	 * @param jsonStr
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String jsonStr) throws IOException {
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return objectMapper.readValue(jsonStr, Map.class);
	}

	/**
	 * map 转JavaBean
	 * 
	 * @param clazz
	 * @param map
	 * @return
	 */
	public static <T> T MapToObject(Map map, Class<T> clazz) {
		return objectMapper.convertValue(map, clazz);
	}

	/**
	 * 把json解析成list，如果list内部的元素存在jsonString，继续解析
	 *
	 * @param json
	 * @param mapper
	 *            解析工具
	 * @return
	 * @throws Exception
	 */
	private static List<Object> json2ListRecursion(String json, ObjectMapper mapper) throws Exception {
		if (json == null) {
			return null;
		}

		List<Object> list = mapper.readValue(json, List.class);

		for (Object obj : list) {
			if (obj != null && obj instanceof String) {
				String str = (String) obj;
				if (str.startsWith("[")) {
					obj = json2ListRecursion(str, mapper);
				} else if (obj.toString().startsWith("{")) {
					obj = json2MapRecursion(str, mapper);
				}
			}
		}

		return list;
	}

	/**
	 * 把json解析成map，如果map内部的value存在jsonString，继续解析
	 *
	 * @param json
	 * @param mapper
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> json2MapRecursion(String json, ObjectMapper mapper) throws Exception {
		if (json == null) {
			return null;
		}

		Map<String, Object> map = mapper.readValue(json, Map.class);

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			Object obj = entry.getValue();
			if (obj != null && obj instanceof String) {
				String str = ((String) obj);

				if (str.startsWith("[")) {
					List<?> list = json2ListRecursion(str, mapper);
					map.put(entry.getKey(), list);
				} else if (str.startsWith("{")) {
					Map<String, Object> mapRecursion = json2MapRecursion(str, mapper);
					map.put(entry.getKey(), mapRecursion);
				}
			}
		}

		return map;
	}
}
