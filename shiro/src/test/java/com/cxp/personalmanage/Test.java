package com.cxp.personalmanage;

public class Test {

	@org.junit.Test
	public void test1() {
		String sql = "SELECT b.id,b.roleid,b.rolename,b.decription,b.enable,b.create_time,b.create_uer,b.update_time,b.update_uer\r\n" + 
				"			FROM uer_role_info a ,roleinfo b\r\n" + 
				"			WHERE a.roleid = b.roleid  AND a.enable = 1\r\n" + 
				"			  		and a.uername = ?";
		String replaceFirst = sql.replaceFirst("\\?", "80003382");
		System.out.println(replaceFirst);
	}
}
