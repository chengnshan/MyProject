package com.cxp.personalmanage.pojo.webchat;

public class BaseMessage {
	// 接收方帐号（收到的 OpenID）
	protected String ToUserName;
	// 开发者微信号 
	protected String FromUserName;
	// 消息创建时间 
	protected String CreateTime;
	// 消息类型（text/music/news）
	protected String MsgType;

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public String getToUserName() {
		return ToUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public String getCreateTime() {
		return CreateTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	@Override
	public String toString() {
		return "ReplyBaseMessage [ToUserName=" + ToUserName + ", FromUserName=" + FromUserName + ", CreateTime="
				+ CreateTime + ", MsgType=" + MsgType + "]";
	}
}
