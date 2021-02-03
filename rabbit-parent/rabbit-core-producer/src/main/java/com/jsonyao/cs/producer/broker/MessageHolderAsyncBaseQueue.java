package com.jsonyao.cs.producer.broker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * MessageHolder异步线程池
 */
@Slf4j
public class MessageHolderAsyncBaseQueue {

    /**
     * 线程池核心线程数 = 当前CPU计算资源
     */
    public static final int CORE_THREAD_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池最大线程数 = 当前CPU计算资源
     */
    public static final int MAX_THREAD_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池工作队列长度
     */
    public static final int QUEUE_SIZE = 10000;

    /**
     * 实例化线程池
     *  int corePoolSize: 线程池核心线程数
     *  int maximumPoolSize: 线程池最大线程数
     *  long keepAliveTime: 线程空闲等待时间
     *  TimeUnit unit: 线程空闲等待时间单位
     *  BlockingQueue<Runnable> workQueue: 工作队列
     *  ThreadFactory threadFactory: 线程工厂
     *  RejectedExecutionHandler handler: 拒绝策略
     */
    private static ExecutorService senderAsync = new ThreadPoolExecutor(
            CORE_THREAD_SIZE,
            MAX_THREAD_SIZE,
            60L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_SIZE),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("rabbitmq_client_async_sender");
                    return thread;
                }
            },
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    log.error("async sender is error rejected, runnable: {}, executor: {}", r, executor);
                }
            }
    );

    /**
     * 线程池提交Runnable任务
     * @param task
     */
    public static void submit(Runnable task) {
        senderAsync.submit(task);
    }
}