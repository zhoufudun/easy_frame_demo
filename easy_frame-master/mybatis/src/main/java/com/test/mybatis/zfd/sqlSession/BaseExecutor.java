package com.test.mybatis.zfd.sqlSession;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;

public class BaseExecutor<T> extends Excutor {
    @Override
    public <T> T queryOne(String sql, Object object, Object parameter) {
        //例如User类中有声明的几个字段
        Field[] fields = object.getClass().getDeclaredFields();
        PreparedStatement pre = null;
        ResultSet resultSet = null;
        Connection connection = connection();

        try {
            pre = connection.prepareStatement(sql);
            String id = parameter.toString();//本例子只有一个参数id
            pre.setString(1, id);
            resultSet = pre.executeQuery();

            if (resultSet.next()) {  //本例子只有一条数据
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount(); //User中国有几个字段就有几列
                for (int i = 0; i < columnCount; i++) {
                    String columnName = metaData.getColumnName(i + 1);
                    Object ob = resultSet.getObject(columnName);//通过列名获得对象值
                    /**
                     * 遍历没一个字段进行匹配
                     */
                    for (int j = 0; j < fields.length; j++) {
                        /**
                         * 之前这里错了
                         */
                        if (fields[j].getName().equals(columnName)) {

                            if (fields[j].getType().getSimpleName().equals("String")) {
                                Method method = object.getClass().getMethod("set" +
                                        letFirstLetter2Up(columnName), String.class);
                                method.invoke(object, (String) ob);
                            } else if (fields[j].getType().getSimpleName().equals("Integer")
                                    || fields[j].getType().getSimpleName().equals("int")) {
                                Method method =
                                        object.getClass().getMethod("set" + letFirstLetter2Up(columnName), Integer.class);
                                method.invoke(object, Integer.parseInt(ob.toString()));
                            } else if (fields[j].getType().getSimpleName().equals("Double")
                                    || fields[j].getType().getSimpleName().equals("double")) {
                                Method method =
                                        object.getClass().getMethod("set" + letFirstLetter2Up(columnName), Double.class);
                                method.invoke(object, Double.parseDouble(ob.toString()));
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
            closeAll(pre, connection, resultSet);
        }
        return (T) object;

    }
}
