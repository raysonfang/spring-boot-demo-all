package cn.raysonblog.shopserviceprovider;

import com.alibaba.nacos.spring.context.annotation.EnableNacos;
import org.apache.dubbo.config.spring.context.annotation.DubboConfigConfiguration;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;

/**
 * dubbo 服务提供方
 * @author raysonfang
 * @公众号 Java技术干货（ID:raysonfang）
 */
@SpringBootApplication
@EnableDubbo
public class ShopServiceProviderApplication {

    //使用jar方式打包的启动方式
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    public static void main(String[] args) throws InterruptedException{
        SpringApplication.run(ShopServiceProviderApplication.class, args).registerShutdownHook();
        countDownLatch.await();
    }
}
