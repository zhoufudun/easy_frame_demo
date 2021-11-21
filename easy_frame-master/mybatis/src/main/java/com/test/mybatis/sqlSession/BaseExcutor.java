package com.test.mybatis.sqlSession;

import com.test.mybatis.pool.DatabasePool;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;

/**
 * @author 老肥猪
 * @since 2019/3/7
 */
public class BaseExcutor<T> extends Excutor {
    @Override
    public <T> T queryOne(String sql, Object object, Object parameter) {
        Field[] fields = object.getClass().getDeclaredFields();//例如User类中有几个字段
        PreparedStatement pre = null;
        ResultSet set = null;
        Connection conn = connection();
        try {
            pre = conn.prepareStatement(sql);
            String id = parameter.toString();//本例子只有一个参数id
            pre.setString(1, id);
            set = pre.executeQuery();
//            set.next();
            if (set.next()) {
                ResultSetMetaData metaData = set.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    Object o = set.getObject(i + 1);
                    String columnName = metaData.getColumnName(i + 1);
                    for (int j = 0; j < fields.length; j++) {
                        if (fields[j].getName().equals(columnName)) {
                            Method method;
                            if(fields[j].getType().getSimpleName().equals("String")) {
//                                System.out.println(fields[j].getType().getSimpleName());
//                                System.out.println(columnName);
                                /**
                                 * 前提是User中的字段名字和表格中的名字一一对应
                                 */
                                method = object.getClass().getMethod("set" + letFirstLetter2Up(columnName),String.class);
                                method.invoke(object,(String)o);
                            } else if(fields[j].getType().getSimpleName().equals("Integer")
                                    || fields[j].getType().getSimpleName().equals("int")) {
                                method = object.getClass().getMethod("set" + letFirstLetter2Up(columnName),Integer.class);
                                method.invoke(object, Integer.parseInt(o.toString()));
                            } else if(fields[j].getType().getSimpleName().equals("Double")
                                    || fields[j].getType().getSimpleName().equals("double")) {
                                method = object.getClass().getMethod("set" + letFirstLetter2Up(columnName),Double.class);
                                method.invoke(object, Double.parseDouble(o.toString()));
                            } else {
                                throw new RuntimeException("this type conversion is not supported");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            closeAll(pre, conn, set);
//            System.out.println("---");
        }
        return (T) object;
    }

}
