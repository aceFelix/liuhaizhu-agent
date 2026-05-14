package com.itfelix.liuhaizhuaichat;

import io.github.cdimascio.dotenv.Dotenv;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author aceFelix
 */
@EnableFileStorage
@SpringBootApplication
@EnableAsync
@EnableAspectJAutoProxy
public class LiuHaizhuAiChatApplication {

    public static void main(String[] args) {
        // 强制使用 IPv4，避免 IPv6 连接超时问题
        // System.setProperty("java.net.preferIPv4Stack", "true");

        // 程序启动前加载环境变量
        // 加载env文件
        Dotenv load = Dotenv.configure().ignoreIfMissing().load();
        // 把env文件里的参数设置到环境变量中
        load.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        SpringApplication.run(LiuHaizhuAiChatApplication.class, args);
    }
}
