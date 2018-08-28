package com.cxp.personalmanage.mapper;

import com.cxp.personalmanage.pojo.MenuInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MenuInfoMapper {

    public List<MenuInfo> getMenuInfo(MenuInfo menuInfo);

    /*
    根据角色Id查询所拥有的菜单
     */
    public List<MenuInfo> findMenuListByRoleId(Map<String,Object> param);

    /**
     * 根据菜单Id获取子菜单
     * @param menuId
     * @return
     */
    public List<MenuInfo> getChildrenMenus(String menuId);
    
    /**
     * 添加菜单
     * @param menuInfo
     * @return
     */
    public int insertMenuInfo(MenuInfo menuInfo);
}
