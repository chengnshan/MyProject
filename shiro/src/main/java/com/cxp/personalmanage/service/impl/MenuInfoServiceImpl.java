package com.cxp.personalmanage.service.impl;

import com.cxp.personalmanage.mapper.MenuInfoMapper;
import com.cxp.personalmanage.pojo.MenuInfo;
import com.cxp.personalmanage.service.MenuInfoService;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(value = "menuInfoService")
public class MenuInfoServiceImpl implements MenuInfoService {

    @Autowired
    private MenuInfoMapper menuInfoMapper;

    @Override
    public List<MenuInfo> getMenuInfo(MenuInfo menuInfo) {
        return menuInfoMapper.getMenuInfo(menuInfo);
    }

    @Transactional
    @Override
    public List<MenuInfo> findMenuListByRoleId(Map<String,Object> param) {
        return menuInfoMapper.findMenuListByRoleId(param);
    }

    @Transactional(rollbackFor= {RuntimeException.class,Exception.class})
	@Override
	public int insertMenuInfo(MenuInfo menuInfo) throws Exception{
    	String parentId = menuInfo.getParentMenuId();
    	MenuInfo paramMenu=new MenuInfo();
    	paramMenu.setMenuId(parentId);
    	List<MenuInfo> menuInfo2 = getMenuInfo(paramMenu);
    	if(CollectionUtils.isNotEmpty(menuInfo2)) {
    		Integer paranLevel = menuInfo2.get(0).getMenuLevel();
    		menuInfo.setMenuLevel(++paranLevel);
    	}
    	int num = menuInfoMapper.insertMenuInfo(menuInfo);
    	if(num <= 0) {
    		throw new RuntimeException("添加菜单失败.");
    	}
		return num;
	}
    
}
