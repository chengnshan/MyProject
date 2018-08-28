package com.cxp.personalmanage.utils.webchat;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.cxp.personalmanage.pojo.webchat.Article;
import com.cxp.personalmanage.pojo.webchat.Image;
import com.cxp.personalmanage.pojo.webchat.ImageMsg;
import com.cxp.personalmanage.pojo.webchat.Music;
import com.cxp.personalmanage.pojo.webchat.MusicMessage;
import com.cxp.personalmanage.pojo.webchat.NewsMessage;
import com.cxp.personalmanage.pojo.webchat.ReplyTextMsg;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * ClassName: MessageUtil
 * @Description: 消息工具类
 */
public class MessageUtil {

	/**
	 * 返回消息类型：文本
	 */
	public static final String RESP_MESSAGE_TYPE_TEXT = "text";

	/**
	 * 返回消息类型：音乐
	 */
	public static final String RESP_MESSAGE_TYPE_MUSIC = "music";

	/**
	 * 返回消息类型：图文
	 */
	public static final String RESP_MESSAGE_TYPE_NEWS = "news";

	/**
	 * 请求消息类型：文本
	 */
	public static final String REQ_MESSAGE_TYPE_TEXT = "text";

	/**
	 * 请求消息类型：图片
	 */
	public static final String REQ_MESSAGE_TYPE_IMAGE = "image";

	/**
	 * 请求消息类型：链接
	 */
	public static final String REQ_MESSAGE_TYPE_LINK = "link";

	/**
	 * 请求消息类型：地理位置
	 */
	public static final String REQ_MESSAGE_TYPE_LOCATION = "location";

	/**
	 * 请求消息类型：音频
	 */
	public static final String REQ_MESSAGE_TYPE_VOICE = "voice";

	/**
	 * 请求消息类型：推送
	 */
	public static final String REQ_MESSAGE_TYPE_EVENT = "event";

	/**
	 * 事件类型：subscribe(订阅)
	 */
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

	/**
	 * 事件类型：unsubscribe(取消订阅)
	 */
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

	/**
	 * 事件类型：CLICK(自定义菜单点击事件)
	 */
	public static final String EVENT_TYPE_CLICK = "CLICK";
	/**扫码事件*/
	public static final String MESSAGE_SCANCODE= "scancode_push";

	/**
	 * @Description: 解析微信发来的请求（XML）
	 * @param @param request
	 * @param @return
	 * @param @throws Exception
	 * @author dapengniao
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
		// 将解析结果存储在 HashMap 中
		Map<String, String> map = new HashMap<String, String>();
		// 从 request 中取得输入流
		InputStream inputStream = request.getInputStream();
		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		// 得到 xml 根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();
		// 遍历所有子节点
		for (Element e : elementList)
			map.put(e.getName(), e.getText());

		// 释放资源
		inputStream.close();
		inputStream = null;
		return map;
	}

	@SuppressWarnings("unused")
	private static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// 对所有 xml 节点的转换都增加 CDATA 标记
				boolean cdata = true;

				@SuppressWarnings("rawtypes")
				public void startNode(String name, Class clazz) {
					super.startNode(name, clazz);
				}

				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});
	
	 /**
     * @Description: 文本消息对象转换成 xml
     * @param @param textMessage
     * @param @return
     */
    public static String textMessageToXml(ReplyTextMsg textMessage) {
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }
    
    /**
     * @Description: 图片消息对象转换成 xml
     * @param @param imageMessage
     * @param @return
     */
    public static String imageMessageToXml(ImageMsg imageMessage) {
        xstream.alias("xml", imageMessage.getClass());
        return xstream.toXML(imageMessage);
    }

    /**
     * @Description: 图文消息对象转换成 xml
     * @param @param newsMessage
     * @param @return
     */
    public static String newsMessageToXml(NewsMessage newsMessage) {
        xstream.alias("xml", newsMessage.getClass());
        xstream.alias("item", new Article().getClass());
        return xstream.toXML(newsMessage);
    }
    
    /**
     * @Description: 音乐消息对象转换成 xml
     * @param @param musicMessage
     * @param @return
     * @author dapengniao
     * @date 2016 年 3 月 8 日 下午 4:13:36
     */
    public static String musicMessageToXml(MusicMessage musicMessage) {
        xstream.alias("xml", musicMessage.getClass());
        return xstream.toXML(musicMessage);
    }

	/**
	 * 返回一个消息
	 * @param out
	 * @param map
	 */
	public static void replyMessage( PrintWriter out,Map<String,String> map) {
		String replyMsg="";
		
		String toUsername = map.get("FromUserName");
		String fromUserName = map.get("ToUserName");
		String msgType = map.get("MsgType");
		String content = map.get("Content");
		//判断是否为文本消息
		if (RESP_MESSAGE_TYPE_TEXT.equals(msgType)) {
			if("1".equals(content)) {
				replyMsg = initNewsMessage(toUsername, fromUserName);
				System.out.println(replyMsg);
			}else {
				replyMsg = initTextMessage(toUsername, fromUserName);
			}
			
		}else if(REQ_MESSAGE_TYPE_IMAGE.equals(msgType)) {
			replyMsg = initImageMessage(toUsername, fromUserName);
            
		}else if(REQ_MESSAGE_TYPE_EVENT.equals(msgType)) {  //消息推送
			String eventType = map.get("Event");
			if(EVENT_TYPE_SUBSCRIBE.equals(eventType)) {//被关注事件
				
			}else if(EVENT_TYPE_CLICK.equals(eventType)) { //点击菜单推送事件
				
			}else if(MESSAGE_SCANCODE.equals(eventType)) { //扫码菜单事件的回复
				
			}
		}else if(REQ_MESSAGE_TYPE_LOCATION.equals(msgType)) {   //地理位置菜单事件的回复
			String Label = map.get("Label");
		//	replyMsg = MessageUtil.initText(toUsername, fromUserName, Label);
		}
		
		try {
			System.out.println("replyMsg："+replyMsg);
			out.println(replyMsg);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 文本消息
	 * @param toUsername
	 * @param fromUserName
	 * @return
	 */
	public static String initTextMessage(String toUsername,String fromUserName) {
		String message = null;
		ReplyTextMsg textMsg = new ReplyTextMsg();
		textMsg.setFromUserName(fromUserName);
		textMsg.setToUserName(toUsername);
		textMsg.setMsgType(RESP_MESSAGE_TYPE_TEXT);//文本类型
		textMsg.setCreateTime("" + new Date().getTime());//当前时间
		String returnMsg = "欢迎来到接口测试号:\n 1、图片\n 2、音乐\n 3、查询";
		textMsg.setContent( returnMsg);//返回消息
		// 将对象转化为XML
		message = textMessageToXml(textMsg);
		return message;
	}
	
	/**
	 * 图文消息
	 * @param toUsername
	 * @param fromUserName
	 * @return
	 */
	public static String initNewsMessage(String toUsername,String fromUserName) {
		String message = null;
		List<Article> articleList = new ArrayList<>();
		NewsMessage newMessage = new NewsMessage();
		Article article =new Article();
		article.setTitle("Java程序员介绍");
		article.setDescription("这里我们同样需要把图文消息转化为XML文件的响应格式，在我们上一次创建的MessageUtil工具类中添加图文转xml及xml转图文的方法，添加图文消息静态常量");
		article.setPicUrl("http://c32e5711.ngrok.io/image/weixin/spring_initializr.png");
		article.setUrl("spring.io");
		articleList.add(article);
		
		newMessage.setToUserName(toUsername);
		newMessage.setFromUserName(fromUserName);
		newMessage.setCreateTime(new Date().getTime()+"");
		newMessage.setMsgType(RESP_MESSAGE_TYPE_NEWS);
		newMessage.setArticles(articleList);
		newMessage.setArticleCount(articleList.size());
		
		message = newsMessageToXml(newMessage);
		return message;
	}
	
	/**
	 * 图片消息
	 * @param toUsername
	 * @param fromUserName
	 * @return
	 */
	public static String initImageMessage(String toUsername,String fromUserName) {
		String message = null;
		//将发过来的图片转发回去
        ImageMsg imageMsg=new ImageMsg();
        imageMsg.setToUserName(toUsername);
        imageMsg.setFromUserName(fromUserName);
        imageMsg.setCreateTime("" + new Date().getTime());
        imageMsg.setMsgType(REQ_MESSAGE_TYPE_IMAGE);
        Image image=new Image();
 //       image.setMediaId(map.get("MediaId"));
        imageMsg.setImage(image);
        message = imageMessageToXml(imageMsg);
        return message;
	}
}
