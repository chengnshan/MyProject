package com.cxp.personalmanage.controller;

import com.cxp.personalmanage.common.Constant;
import com.cxp.personalmanage.config.context.InitMemoryConfig;
import com.cxp.personalmanage.pojo.RoleInfo;
import com.cxp.personalmanage.service.RoleInfoService;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "role")
public class RoleController {
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleInfoService roleInfoService;

    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/findRoleList",method = RequestMethod.POST)
    public List<RoleInfo> findRoleList(){
    	List<RoleInfo> roleList = null;
    	Map<String, Object> initMap = InitMemoryConfig.initMap;
    	if(MapUtils.isNotEmpty(initMap)) {
    		roleList = (List<RoleInfo>) initMap.get(Constant.initKey.ROLELIST);
    		logger.info("从内存中拿值.");
    	}else {
    		 roleList = roleInfoService.findRoleList(null);
    	}
       
        return roleList;
    }
}
