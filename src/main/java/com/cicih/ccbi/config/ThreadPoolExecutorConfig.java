package com.cicih.ccbi.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolExecutorConfig {
    // 自定义一个单例线程池并交给Spring管理（为什么是单例？？
    @Bean
    public ThreadPoolExecutor taskExecutor() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;

            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("Task-thread-" + count++);
                return thread;
            }

            public String getName(){
                return Thread.currentThread().getName();
            }
        };
        // todo 核心线程数的设置需要根据业务瓶颈来调整
        return new ThreadPoolExecutor(2, 4, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), threadFactory);
    }

    @Bean
    public ThreadPoolExecutor commonExecutor() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;

            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("Common-Thread-" + count++);
                return thread;
            }
        };
        return new ThreadPoolExecutor(2, 4, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), threadFactory);
    }

}
