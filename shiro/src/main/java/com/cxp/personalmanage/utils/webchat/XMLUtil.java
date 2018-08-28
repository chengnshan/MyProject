package com.cxp.personalmanage.utils.webchat;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLUtil {
	public static String content = "";

	public static Document readString2XML(String str) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(str);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	};

	/**
	 * 读取根节点下每一个节点信息
	 * 
	 * @param node
	 * @return
	 */
	public static String readNodes(Element node) {
		content += node.getName() + ":" + node.getTextTrim() + "\n";
		// 递归遍历当前节点所有的子节点
		List<Element> listElement = node.elements();// 所有一级子节点的list
		for (Element e : listElement) {// 遍历所有一级子节点
			readNodes(e);// 递归
		}
		return content;
	}

	/**
	 * 读取单个节点信息
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	public static String readNode(Element node, String name) {
		Element e = node.element(name);
		String nodeString = e.getTextTrim();
		return nodeString;

	}
}
