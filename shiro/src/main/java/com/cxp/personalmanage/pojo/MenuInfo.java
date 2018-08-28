package com.cxp.personalmanage.pojo;

import java.io.Serializable;
import java.util.List;

public class MenuInfo extends BaseEntityInfo implements Serializable{

	private static final long serialVersionUID = 8684119698965611167L;
	
	private Integer id;
	private String menuId;
	private String menuName;
	private String menuUrl;
	private String remark;
	private String classStyle;
	private int sort;
	private String parentMenuId;
	private List<MenuInfo> childrenMenus;

	private int enable =1;
	private Integer menuLevel;

	public Integer getMenuLevel() {
		return menuLevel;
	}

	public void setMenuLevel(Integer menuLevel) {
		this.menuLevel = menuLevel;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String getClassStyle() {
		return classStyle;
	}

	public void setClassStyle(String classStyle) {
		this.classStyle = classStyle;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public List<MenuInfo> getChildrenMenus() {
		return childrenMenus;
	}

	public void setChildrenMenus(List<MenuInfo> childrenMenus) {
		this.childrenMenus = childrenMenus;
	}

	public String getParentMenuId() {
		return parentMenuId;
	}

	public void setParentMenuId(String parentMenuId) {
		this.parentMenuId = parentMenuId;
	}

	@Override
	public String toString() {
		return "MenuInfo{" + "menuId='" + menuId + '\'' + ", menuName='" + menuName + '\'' + ", menuUrl='" + menuUrl
				+ '\'' + ", remark='" + remark + '\'' + ", sort=" + sort + '}';
	}
}
