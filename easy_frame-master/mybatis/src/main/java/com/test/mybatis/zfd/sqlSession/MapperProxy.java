package com.test.mybatis.zfd.sqlSession;

import com.test.mybatis.zfd.config.Configuration;
import com.test.mybatis.zfd.config.Function;
import com.test.mybatis.zfd.config.MapperBean;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * function TODO
 *
 * @author 19026404
 * @date 2021/11/16 14:17
 */
public class MapperProxy implements InvocationHandler {

	private SqlSession sqlSession;

	public MapperProxy(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MapperBean mapperBean = Configuration.readMapper(sqlSession.getMapperName());
//		System.out.println(mapperBean.getNamespace()+"-------------");
//		System.out.println(method.getDeclaringClass().getName()+"---------------");

		Object result = null;
		if(method.getDeclaringClass().getName().equals(mapperBean.getNamespace())){
			List<Function> functions =
					mapperBean.getFunctions();
			if(functions==null || functions.size()==0) {
				System.out.println("functions is empty ");
				return null;
			}
//			System.out.println(method.getName()+"------");
			for(Function function:functions){
				System.out.println(function.getFuncName());
				/**
				 * 如果方法名匹配上就执行
				 */
				if(method.getName().equals(function.getFuncName())){
					 result = sqlSession.selectOne(function.getSql(), args[0],
							function.getResultType());
				}

			}
		}
		return result;
	}
}
