package com.cxp.personalmanage.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class BaseEntityInfo {

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
	protected Date create_time;
	protected String create_user;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8" )
	protected Date update_time;
	protected String update_user;
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getCreate_user() {
		return create_user;
	}
	public void setCreate_user(String create_user) {
		this.create_user = create_user;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	public String getUpdate_user() {
		return update_user;
	}
	public void setUpdate_user(String update_user) {
		this.update_user = update_user;
	}
	
}
