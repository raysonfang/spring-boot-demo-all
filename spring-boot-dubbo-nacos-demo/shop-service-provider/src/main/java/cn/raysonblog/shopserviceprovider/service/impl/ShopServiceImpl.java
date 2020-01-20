package cn.raysonblog.shopserviceprovider.service.impl;

import cn.raysonblog.shopservice.api.service.RpcShopService;
import org.apache.dubbo.config.annotation.Service;

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

    public String sayHello(String name) {
        return name;
    }
}
