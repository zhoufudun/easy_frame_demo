package com.test.mybatis.zfd;


import com.test.mybatis.zfd.bean.User;
import com.test.mybatis.zfd.mapper.UserMapper;
import com.test.mybatis.zfd.pool.DatabasePool;
import com.test.mybatis.zfd.sqlSession.SqlSession;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * zfd
 */
public class MainTest {
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
