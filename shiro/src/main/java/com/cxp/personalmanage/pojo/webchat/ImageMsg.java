package com.cxp.personalmanage.pojo.webchat;

import com.thoughtworks.xstream.XStream;

/**
 * 图片消息
 * @author Administrator
 *
 */
public class ImageMsg extends BaseMessage {
	private Image Image;

	public Image getImage() {
		return Image;
	}

	public void setImage(Image image) {
		Image = image;
	}

	/**
	 * 将对象转换为XML
	 * @return
	 */
	public String Msg2Xml() {
		XStream xstream = new XStream();
		xstream.alias("xml", this.getClass());
		xstream.alias("Image", Image.getClass());
		return xstream.toXML(this);
	}
}
