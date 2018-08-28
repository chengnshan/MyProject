package com.cxp.personalmanage.pojo;

import java.io.Serializable;

public class UserRoleInfo extends BaseEntityInfo implements Serializable{
    
	private static final long serialVersionUID = -6495861512374197391L;
	private int id;
    private String roleId;
    private String userName;
    private int enable;

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserRoleInfo{" +
                "id=" + id +
                ", roleId='" + roleId + '\'' +
                ", userName='" + userName + '\'' +
                ", create_time=" + create_time +
                ", create_user='" + create_user + '\'' +
                ", update_time=" + update_time +
                ", update_user='" + update_user + '\'' +
                '}';
    }
}
