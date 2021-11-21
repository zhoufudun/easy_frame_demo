package com.test.mybatis.zfd.sqlSession;

import com.test.mybatis.zfd.sqlSession.BaseExecutor;
import com.test.mybatis.zfd.sqlSession.Excutor;

import java.lang.reflect.Proxy;

public class SqlSession {
    private Excutor executor=new BaseExecutor<>();
    private String mapperName=null;
    public <T> T selectOne(String sql,Object parameter,Object object){
        return executor.queryOne(sql,object,parameter);
    }

    public <T> T getMapper(Class<T> clazz) {
        this.mapperName=clazz.getSimpleName()+".xml";
        System.out.println("mapperName="+mapperName);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},new MapperProxy(this));
    }
    public String getMapperName() {
        return mapperName;
    }
}
