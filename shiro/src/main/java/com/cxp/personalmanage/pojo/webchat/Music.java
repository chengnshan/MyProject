package com.cxp.personalmanage.pojo.webchat;

/**
 * 1）参数Title：标题，本例中可以设置为歌曲名称；
2）参数Description：描述，本例中可以设置为歌曲的演唱者；
3）参数MusicUrl：普通品质的音乐链接；
4）参数HQMusicUrl：高品质的音乐链接，在WIFI环境下会优先使用该链接播放音乐；
5）参数ThumbMediaId：缩略图的媒体ID，通过上传多媒体文件获得；它指向的是一张图片，最终会作为音乐消息左侧绿色方形区域的背景图片。
 * @author Administrator
 *
 */
public class Music {

	private String Title;
	private String Description;
	private String MusicUrl;
	private String HQMusicUrl;//高质量的链接
	private String ThumbMediaId;
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getMusicUrl() {
		return MusicUrl;
	}
	public void setMusicUrl(String musicUrl) {
		MusicUrl = musicUrl;
	}
	public String getHQMusicUrl() {
		return HQMusicUrl;
	}
	public void setHQMusicUrl(String hQMusicUrl) {
		HQMusicUrl = hQMusicUrl;
	}
	public String getThumbMediaId() {
		return ThumbMediaId;
	}
	public void setThumbMediaId(String thumbMediaId) {
		ThumbMediaId = thumbMediaId;
	}
	
}
