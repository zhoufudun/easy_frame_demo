package com.test.mybatis;


import com.test.mybatis.pool.DatabasePool2;
import com.test.mybatis.bean.User;
import com.test.mybatis.pool.DatabasePool;
import com.test.mybatis.mapper.UserMapper;
import com.test.mybatis.sqlSession.SqlSession;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 老肥猪
 * @since 2019/3/7
 */
public class Main {
    private static ExecutorService executorService =
            Executors.newFixedThreadPool(100);

    public static void main(String[] args) throws InterruptedException {
        SqlSession sqlSession = new SqlSession();
        for(int i=0;i<100;i++){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                    User user = userMapper.getUserById(1);
                    System.out.println(user);
                }
            });
        }
//        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//                    User user = userMapper.getUserById(1);
//                    System.out.println(user);
        Thread.sleep(3000);
        System.out.println(DatabasePool2.getDatabasePool().getQueueSize());
        Thread.currentThread().join();
    }

    @Test
    public void test1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        User user = new User();
        Field[] fields = user.getClass().getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getName());
        }

        Method method = user.getClass().getMethod("setName", String.class);
        method.invoke(user, "zhoufudun");
        System.out.println(user.getName());
    }
}
