package com.cxp.personalmanage.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.cxp.personalmanage.common.Constant;
import com.cxp.personalmanage.pojo.UserInfo;
import com.cxp.personalmanage.utils.CommonUtil;

/**
 * 拦截器作用：给各实体对象在增加、修改时，自动添加操作属性信息。
 * 
 * @author Administrator
 *
 */
@Intercepts(value = {
		@Signature(args = { MappedStatement.class, Object.class }, method = "update", type = Executor.class) })
public class EntityInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		System.out.println("-----------参数拦截---------------------------------------------------");
		System.out.println("02 当前线程ID:" + Thread.currentThread().getId());
		String crud = null;
		// 遍历处理所有参数，update方法有两个参数，参见Executor类中的update()方法。
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			String className = arg.getClass().getName();
			System.out.println(i + " 参数类型：" + className);

			// 第一个参数处理。根据它判断是否给“操作属性”赋值。
			if (arg instanceof MappedStatement) {// 如果是第一个参数 MappedStatement
				MappedStatement ms = (MappedStatement) arg;
				SqlCommandType sqlCommandType = ms.getSqlCommandType();
				System.out.println("操作类型：" + sqlCommandType);
				crud = sqlCommandType.toString();
				// 如果是“增加”或“更新”操作，则继续进行默认操作信息赋值。否则，则退出
				if (sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.UPDATE) {
					continue;
				} else {
					break;
				}
			}
			// 第二个参数处理。（只有第二个程序才能跑到这）
			if (arg instanceof Map) {// 如果是map，有两种情况：（1）使用@Param多参数传入，由Mybatis包装成map。（2）原始传入Map
				System.out.println("这是一个包装过的类型!");
				Map map = (Map) arg;
				for (Object obj : map.values()) {
					
					if(obj instanceof List) {
						List objList = (List)obj;
						for (Object object : objList) {
							setProperty(object,crud);
						}
					}else {
						setProperty(obj,crud);
					}
				}
			} else {// 原始参数传入
				setProperty(arg,crud);
			}
		}
		return invocation.proceed();
	}

	/**
	 * 76 * 为对象的操作属性赋值 77 * @param obj 78
	 */
	private void setProperty(Object obj,String methodType) {
		try {
			UserInfo currentLoginUser = CommonUtil.getCurrentLoginUser();
			// TODO: 根据需要，将相关属性赋上默认值
			if(SqlCommandType.INSERT.toString().equalsIgnoreCase(methodType)) {
				BeanUtils.setProperty(obj, Constant.CREATE_TIME, new Date());
				BeanUtils.setProperty(obj, Constant.CREATE_USER, currentLoginUser.getUserName());
			}
			BeanUtils.setProperty(obj, Constant.UPDATE_TIME, new Date());
			BeanUtils.setProperty(obj, Constant.UPDATE_USER, currentLoginUser.getUserName());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * plugin方法主要是用于封装目标对象，通过该方法我们可以决定是否要进行拦截进而决定返回什么样的目标对象
	 */
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	//setProperties方法是用于在Mybatis配置文件中指定一些属性的。
	@Override
	public void setProperties(Properties properties) {

	}

}
