package cn.raysonblog.shopserviceprovider.service.impl;

import cn.raysonblog.shopserviceprovider.config.NacosConfig;
import cn.raysonblog.shopserviceprovider.service.RpcShopService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 接口实现类
 *
 * ## @Service 这个注解是使用dubbo提供的，
 *             这个注解中有很多属性，需要单独了解去进行配置
 *
 * @author raysonfang
 */
@Service
public class ShopServiceImpl implements RpcShopService {

    @Autowired
    NacosConfig nacosConfig;

    public String sayHello(String name) {
        return name;
    }

    /**
     * 将nacos config的配置信息暴露给服务消费者
     * @param desc
     * @return
     */
    public String getConfigServiceName(String desc){
        return nacosConfig.getServiceName()+desc;
    }
}
