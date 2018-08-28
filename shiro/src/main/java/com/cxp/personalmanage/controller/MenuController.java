package com.cxp.personalmanage.controller;

import com.cxp.personalmanage.common.Constant;
import com.cxp.personalmanage.controller.base.BaseController;
import com.cxp.personalmanage.pojo.MenuInfo;
import com.cxp.personalmanage.pojo.UserInfo;
import com.cxp.personalmanage.service.MenuInfoService;
import com.cxp.personalmanage.utils.CommonUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/menu")
public class MenuController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
    
    @Autowired
	@Qualifier(value = "stringRedisTemplate")
	private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MenuInfoService menuInfoService;

    @RequestMapping(value = "/findMenuList",method = RequestMethod.POST)
    public String findMenuList(MenuInfo menuInfo){
    	/*MenuInfo menuInfo = new MenuInfo();
    	menuInfo.setMenuLevel(1);*/
    	List<MenuInfo> menuInfoList = menuInfoService.getMenuInfo(menuInfo);
        return buildSuccessResultInfo(menuInfoList);
    }
    
    @RequestMapping(value = "/getMenuInfoByMenuId",method = RequestMethod.POST)
    public String getMenuInfoByMenuId(String menuId){
    	MenuInfo menuInfo = new MenuInfo();
    	menuInfo.setMenuId(menuId);
    	List<MenuInfo> menuInfo2 = menuInfoService.getMenuInfo(menuInfo);
    	return buildSuccessResultInfo(menuInfo2.get(0));
    }
    
    @RequestMapping(value="/insertMenuInfo")
    public String insertMenuInfo(MenuInfo menuInfo) {
    	String returnStr = "";
    	logger.info("insertMenuInfo 入参: menuInfo : "+menuInfo);
    	if(menuInfo!=null && StringUtils.isNotBlank(menuInfo.getMenuId()) && StringUtils.isNotBlank(menuInfo.getMenuUrl())) {
    		try {
    			MenuInfo queryMenu = new MenuInfo();
    			queryMenu.setMenuId(menuInfo.getMenuId());
    			List<MenuInfo> exsitsMenuInfo = menuInfoService.getMenuInfo(queryMenu);
    			if(CollectionUtils.isEmpty(exsitsMenuInfo)) {
    				menuInfoService.insertMenuInfo(menuInfo);
    				returnStr = buildSuccessResultInfo("菜单添加成功!");
    				//清除redis中存储的菜单列表,重新从数据加载
    				UserInfo currentLoginUser = CommonUtil.getCurrentLoginUser();
    				stringRedisTemplate.delete(Constant.LOGIN_MENU_INFO+currentLoginUser.getUserName());
    			}else {
    				returnStr = buildFailedResultInfo(-1, "此菜单已经存在,不能重复添加!");
    			}
    			TimeUnit.SECONDS.sleep(30L);
			} catch (Exception e) {
				e.printStackTrace();
				returnStr = buildFailedResultInfo(-1, "服务器异常,添加失败!");
			}
    	}
    	return returnStr;
    }
}
