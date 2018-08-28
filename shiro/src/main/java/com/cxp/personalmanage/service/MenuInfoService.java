package com.cxp.personalmanage.service;

import com.cxp.personalmanage.pojo.MenuInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MenuInfoService {

    public List<MenuInfo> getMenuInfo(MenuInfo menuInfo);

    /**
      *根据角色Id查询所拥有的菜单
     */
    public List<MenuInfo> findMenuListByRoleId(Map<String,Object> param);
    
    /**
     * 添加菜单
     * @param menuInfo
     * @return
     */
    public int insertMenuInfo(MenuInfo menuInfo) throws Exception;
}
