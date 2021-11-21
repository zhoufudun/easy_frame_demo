package com.test.mybatis.config;

import lombok.Data;

/**
 * @author 老肥猪
 * @since 2019/3/7
 */
@Data
public class Function {
    private String sqlType; //select、update、insert、delete
    private String funcName; //id
    private String sql;      // sql语句
    private Object resultType; //返回结果值
    private String parameterType; //入参类型
}
