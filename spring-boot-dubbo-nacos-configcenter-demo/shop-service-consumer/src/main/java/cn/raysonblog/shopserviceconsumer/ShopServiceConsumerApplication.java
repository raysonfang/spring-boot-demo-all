package cn.raysonblog.shopserviceconsumer;

import cn.raysonblog.shopserviceprovider.service.RpcShopService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * 把主类和controller写在一起，方便简单测试演示。
 *
 * @author raysonfang
 */
@SpringBootApplication
@RestController
public class ShopServiceConsumerApplication {
    @Reference
    RpcShopService shopService;

    /**
     * 注释原因： 在主应用入口，只运行有一个RequestMapping 否则会报错。
     */
  /*  @RequestMapping(name = "/sayHello", method = RequestMethod.GET)
    public String sayHello(){
        return shopService.sayHello("Hello Dubbo Nacos!更多原创分享，技术交流，关注：Java技术干货（ID:raysonfang）");
    }*/

    /**
     * Nacos config配置中心 获取配置信息 测试接口
     * @return
     */
    @RequestMapping(name = "/getConfig", method = RequestMethod.GET)
    public String getConfig(){
        return shopService.getConfigServiceName("更多原创分享，技术交流，关注：Java技术干货（ID:raysonfang）");
    }

    public static void main(String[] args) {
        SpringApplication.run(ShopServiceConsumerApplication.class, args);
    }

}
