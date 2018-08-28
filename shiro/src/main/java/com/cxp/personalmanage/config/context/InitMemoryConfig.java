package com.cxp.personalmanage.config.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;

import com.cxp.personalmanage.common.Constant;
import com.cxp.personalmanage.pojo.RoleInfo;
import com.cxp.personalmanage.service.RoleInfoService;

@Configuration
public class InitMemoryConfig implements ApplicationListener<ContextRefreshedEvent> {
	
	 @Autowired
	 private RoleInfoService roleInfoService;
	
	public static Map<String ,Object> initMap = null;
	
	public void init() {
		initMap = new HashMap<String, Object>();
		System.out.println("initMap初始化了");
		/**获取角色列表*/
		List<RoleInfo> roleList = roleInfoService.findRoleList(null);
		if(!CollectionUtils.isEmpty(roleList)) {
			initMap.put(Constant.initKey.ROLELIST, roleList);
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(event.getApplicationContext().getParent() == null ) {
			this.init();
		}
	}

}
