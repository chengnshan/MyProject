package com.cxp.personalmanage.pojo.webchat;

import com.thoughtworks.xstream.XStream;

/**
 * 文本消息消息体
 * @author Administrator
 *
 */
public class ReplyTextMsg extends BaseMessage {
	// 回复的消息内容   
	private String Content;

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	/**
	 * 将对象转换为XML
	 * 
	 * @return
	 */
	public String Msg2Xml() {
		XStream xstream = new XStream();
		xstream.alias("xml", this.getClass());
		return xstream.toXML(this);
	}
}