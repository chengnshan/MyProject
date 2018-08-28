package com.cxp.personalmanage.common;

public class Constant {
	public static final String SUCC="SUCC";
    public static final int ROWS = 10;
    
    public static final String SALT = "abcdefg";
    public static final String MD5 = "MD5";
    public static final int HASHITERATIONS = 3;
    
    public static final String SPRINGBOOT_FILENAME = "SpringBoot.docx";
    
    public static final String REDISDAO_LOGIN_PREFIX = "shiro_login_";
    public static final String REDISDAO_CACHE_PREFIX = "shiro_cache_";
    
    public static final String LOGIN_MENU_INFO ="userMenu";
    
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time"; 
    public static final String CREATE_USER = "create_user"; 
    public static final String UPDATE_USER = "update_user"; 
    
    public class WeiXin{
    	 public static final String APPID = "wx6354e8685cb793b1";
    	 public static final String APPSECRET = "73833d9e27d5773d53ea4ddeabff5f48";
    	 public static final String GRANT_TYPE = "client_credential";
    	 /**获取access_token*/
    	 public static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    	 /**创建菜单*/
    	 public static final String CUSTOM_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create";
    	 /**查询菜单接口*/
    	 public static final String QUERY_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
    	 /**查询菜单接口*/
    	 public static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
    }
    
    public class initKey{
    	public static final String ROLELIST = "roleList";
    }
}
