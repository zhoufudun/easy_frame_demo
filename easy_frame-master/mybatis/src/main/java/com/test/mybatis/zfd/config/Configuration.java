package com.test.mybatis.zfd.config;


import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * function TODO
 *
 * @author 19026404
 * @date 2021/11/16 11:21
 */
public class Configuration {
	private static ClassLoader loader = ClassLoader.getSystemClassLoader();

	/**
	 * 资源读取以及构建
	 *
	 * @param resources
	 * @return
	 */
	public static Connection build(String resources) {
		try {
			InputStream inputStream = loader.getResourceAsStream(resources);
			SAXReader reader = new SAXReader();
			Document document = reader.read(inputStream);
			Element rootElement = document.getRootElement();
			return evalDataSource(rootElement);
		} catch (DocumentException e) {
			throw new RuntimeException("xml read error");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("not found driver");
		}
	}

	/**
	 * 获取数据库的连接
	 * <database>
		 * <property name="driverClassName">com.mysql.jdbc.Driver</property>
		 * <property name="url">jdbc:mysql://localhost:3306/test?useUnicode=true&amp;characterEncoding=utf8&amp;tinyInt1isBit=false&amp;useSSL=false</property>
		 * <property name="username">root</property>
		 * <property name="password">88105156</property>
	 * </database>
	 *
	 * @param root xml的节点
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static Connection evalDataSource(Element root) throws ClassNotFoundException {
		if (!root.getName().equals("database")) {
			throw new RuntimeException("root should be database");
		}
		String driver = null;
		String url = null;
		String username = null;
		String password = null;
		for (Object item : root.elements("property")) {
			Element element = (Element) item;
			String value = getValue(element);

			String name = element.attributeValue("name");
			switch (name) {
				case "url":
					url = value;
					break;
				case "username":
					username = value;
					break;
				case "driverClassName":
					driver = value;
					break;
				case "password":
					password = value;
					break;
				default:
					throw new RuntimeException("not found name is " + name);
			}
		}
		Class.forName(driver);
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * 获得值
	 *
	 * @param element
	 * @return
	 */
	private static String getValue(Element element) {
		/**
		 * 中间是否有值，有的话去找中间的，没有的话去找属性为value的值
		 */
		return element.hasContent() ? element.getText() : element.attributeValue(
				"value");
	}

	/**
	 * 读取mapper的xml
	 * <mapper  namespace="com.test.mybatis.mapper.UserMapper">
	 * 		<select id="getUserById" resultType ="com.test.mybatis.bean.User">
	 * 				select * from user where id = ? (element.getText())
	 * 		</select>
	 * </mapper>
	 *
	 * @param path 位置
	 * @return
	 */
	public static MapperBean readMapper(String path) {
		MapperBean mapperBean = new MapperBean();
		/**
		 * 获得读取流
		 */
		InputStream resourceAsStream = loader.getResourceAsStream(path);
		SAXReader saxReader = new SAXReader();
		Document document = null;

		try {
			/**
			 * 读取xml文档
			 */
			document = saxReader.read(resourceAsStream);
		} catch (DocumentException e) {
			throw new RuntimeException("read xml error");
		}
		/**
		 * 获得xml文档根元素
		 */
		Element root = document.getRootElement();
		if(root==null){
			throw new RuntimeException("not found root element");
		}

		if(!root.getName().equals("mapper")){
			throw new RuntimeException("root element should be mapper");
		}

		/**
		 * 获取namespace
		 */
		String namespace = root.attributeValue("namespace");
		if(namespace==null||"".equals(namespace.trim())){
			throw new RuntimeException("root element attributeValue should be" +
					"not null");
		}
//		System.out.println(path);
//		System.out.println("-----------------------");
		/**
		 * 设置扫描的类
		 */
		mapperBean.setNamespace(namespace);

		ArrayList<Function> functions = new ArrayList<>();

		root.elements().forEach(new Consumer() {
			@Override
			public void accept(Object o) {
				Function function = new Function();
				Element element = (Element) o;
				//设置类别 update || select || delete || insert
				function.setSqlType(element.getName().trim());
				/**
				 * 获得中间的sql
				 */
				function.setSql(element.getText());
				/**
				 * 获得id的值
				 */
				function.setFuncName(element.attributeValue("id"));
				/**
				 * 实例化返回对象类型
				 */
				String resultType = element.attributeValue("resultType");
				Object newInstance=null;
				try {
					newInstance = Class.forName(resultType).newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				function.setResultType(newInstance);
				functions.add(function);
			}
		});
		mapperBean.setFunctions(functions);
		return mapperBean;
	}
}
