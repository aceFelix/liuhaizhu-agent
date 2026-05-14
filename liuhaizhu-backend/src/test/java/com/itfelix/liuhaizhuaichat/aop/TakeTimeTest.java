package com.itfelix.liuhaizhuaichat.aop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

@SpringBootTest
public class TakeTimeTest {
    @Test
    void timeTest() throws InterruptedException {
        System.out.println("开始测试");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("任务一");
        Thread.sleep(1000);
        stopWatch.stop();

        stopWatch.start("任务二");
        Thread.sleep(2000);
        stopWatch.stop();

        stopWatch.start("任务三");
        Thread.sleep(3000);
        stopWatch.stop();
        // 打印任务耗时统计
        System.out.println("任务耗时统计：" + stopWatch.prettyPrint());
        System.out.println("任务耗时统计简要：" + stopWatch.shortSummary());
        System.out.println("任务总耗时：" + stopWatch.getTotalTimeMillis() + "毫秒");

        // 获取任务数量
        System.out.println("任务数：" + stopWatch.getTaskCount());
        System.out.println("结束测试");
    }
}
