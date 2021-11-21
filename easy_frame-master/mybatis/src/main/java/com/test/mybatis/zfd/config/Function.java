package com.test.mybatis.zfd.config;

import lombok.Data;

/**
 * function TODO
 *
 * @author 19026404
 * @date 2021/11/16 11:16
 */
@Data
public class Function {
	private String sqlType; //select、update、insert、delete
	private String funcName; //id
	private String sql;      // sql语句
	private Object resultType; //返回结果值,是一个对象实例
	private String parameterType; //入参类型
}
