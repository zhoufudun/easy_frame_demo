package com.test.mybatis.zfd.config;

import lombok.Data;

import java.util.List;

/**
 * function TODO
 *
 * @author 19026404
 * @date 2021/11/16 11:20
 */
@Data
public class MapperBean {
	private String namespace;
	private List<Function> functions;
}
