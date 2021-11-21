package com.test.mybatis.pool;

import com.test.mybatis.config.Configuration;

import java.sql.Connection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class DatabasePool2 {
    /**
     * 初始化连接数
     */
    private static final int INIT_LINK = 5;

    private static final int MAX_LINK = 20;

    /**
     * 使用中的连接数
     */
    private AtomicInteger alreadyUseCount = new AtomicInteger(0);
    /**
     * 是否已经使用到了最大连接数，没有可以用的链接，也不能新建连接了
     */
    private AtomicBoolean NoAvailable = new AtomicBoolean(false);

    /**
     * 连接池队列
     */
    private static ArrayBlockingQueue<Connection> queue;

    static {
        queue = new ArrayBlockingQueue<>(INIT_LINK);
        for (int i = 0; i < INIT_LINK; i++) {
            queue.offer(Configuration.build("database.xml"));
        }
    }

    private synchronized void extend() {
        int extendNum = MAX_LINK - INIT_LINK;
        System.out.println("thread name="+Thread.currentThread().getName()+"," +
                " extend="+extendNum);
        System.out.println("userd="+alreadyUseCount.get());
        System.out.println("queueSize="+queue.size());
        for (int i = 0; i < extendNum; i++) {
            queue.offer(Configuration.build("database.xml"));
        }
    }


    /**
     * 内部类实现单例
     */
    private static class SingletonHolder2 {
        private static final DatabasePool2 INSTANCE = new DatabasePool2();
    }

    /**
     * 获得一个数据库连接池
     *
     * @return
     */
    public static DatabasePool2 getDatabasePool() {
        return SingletonHolder2.INSTANCE;
    }

    /**
     * 获得实例
     *
     * @return
     * @throws InterruptedException
     */
    public synchronized Connection getConnection() throws InterruptedException {
        if (queue.isEmpty()) {
            if (alreadyUseCount.get() < MAX_LINK) {
                extend();

                Connection connection = queue.poll();
//                System.out.println(queue.size());
                alreadyUseCount.getAndIncrement();
                return connection;
            }else{
                NoAvailable.set(true);
                while(NoAvailable.get()){
                    this.wait();
                }
                Connection connection = queue.poll();
                alreadyUseCount.getAndIncrement();
//                System.out.println(alreadyUseCount.get());
                return connection;
            }
        }else{
            Connection connection = queue.poll();
            alreadyUseCount.getAndIncrement();
//                System.out.println(alreadyUseCount.get());
            return connection;
        }
    }

    /**
     * 回收
     *
     * @param connection
     * @return
     */
    public synchronized void recycle(Connection connection) {
        queue.offer(connection);
        alreadyUseCount.getAndDecrement();
        while(NoAvailable.get()){
            NoAvailable.getAndSet(false);
            this.notifyAll();
        }
//        System.out.println(queue.size());
    }
    public int getQueueSize(){
        return queue.size();
    }
}
