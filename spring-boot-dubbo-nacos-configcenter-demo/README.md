# 《Spring Boot 2.x 基础案例：整合Dubbo 2.7.3+Nacos1.1.3（配置中心）》

![本文的思维导图](https://upload-images.jianshu.io/upload_images/7253165-ce16eb9a42596da6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
> 本文原创首发于公众号：[Java技术干货](https://mp.weixin.qq.com/s?__biz=MzIzMDI5NDAwNg==&mid=2650997916&idx=1&sn=0e63ad00d34827c2cfe3f855bf0dadde&chksm=f3420c05c4358513ad022d4f392b23e5b2df6b905cd594f4cc5d3a3243d53f8ac500a3b95024&token=221164445&lang=zh_CN#rd)

### 1、概述
本文将Nacos作为配置中心，实现配置外部化，动态更新。这样做的优点：**不需要重启应用，便可以动态更新应用里的配置信息。**在如今流行的微服务应用下，将应用的配置统一管理，显得尤为重要。

上一篇写了《Spring Boot 2.x 基础案例：整合Dubbo 2.7.3+Nacos1.1.3（最新版）》[https://www.jianshu.com/p/b0dddce1d404](https://www.jianshu.com/p/b0dddce1d404)，在文章中，nacos的角色是注册中心。

本文也是在上一篇的基础上，继续学习和研究以Dubbo为微服务框架，nacos作为配置中心，应该如何进行实践。以及在此过程中，遇到了什么样的问题，如何解决。

---

### 2、nacos的必知必会

在进行编码之前，先看看当nacos作为配置中心时，操作界面是啥，有哪些新的知识点，需要我们先去了解和掌握呢？以免，在后面搭建环境时，全程懵逼。
![nacos配置管理](https://upload-images.jianshu.io/upload_images/7253165-6127194cc3f5c3be.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![新建配置](https://upload-images.jianshu.io/upload_images/7253165-aed99b1df42df95f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**重要参数说明**

##### Data Id

1.  Data Id的默认值为`${nacos.config.prefix}-${spring.profile.active}.${nacos.config.file-extension}`
2.  `nacos.config.prefix`的默认值为`${spring.application.name}`
3.  `nacos.config.file-extension`的默认值为`properties`
4.  当`spring.profiles.active`未配置时，则匹配`${spring.application.name}.properties`
5.  若设置了`spring.profiles.active`而Nacos中存在`${spring.application.name}.properties`时，若还存在`${spring.application.name}-${spring.profiles.active}.properties`，则默认匹配后者，若不存在，则会自动匹配前者
6.  由于Nacos建议且默认用`spring.application.name`作为Data Id的前缀，若要在不同服务中共享项目统一配置，则可以通过配置`nacos.config.shared-dataids`或`nacos.config.refreshable-dataids`来添加共享配置，前者不支持自动刷新，后者支持

##### Group

1.  这是一个很灵活的配置项，并没有固定的规定，可以用作多环境、多模块、多版本之间区分配置

##### Namespace

1.  推荐使用命名空间来区分不同环境的配置，因为使用`profiles`或`group`会是不同环境的配置展示到一个页面，而Nacos控制台对不同的`Namespace`做了Tab栏分组展示，如下图：

![命名空间ID](https://upload-images.jianshu.io/upload_images/7253165-020f9781f3c5e0e8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


2.  注意配置`Namespace`的时候不是通过名称，而是通过命名空间的ID(上图所示)，可通过如下配置来设置服务使用的命名空间：
```
nacos:
  service-address: 127.0.0.1
  port: 8848
  config:
    server-addr: ${nacos.service-address}:${nacos.port}
    namespace: 9af36d59-2efd-4f43-8a69-82fb37fc8094  # 命名空间ID 不是命名空间名称
```
---
### 3、基础框架搭建
我的建议，尽可能自己花点时间，在不熟悉的情况下，尽量按照自己的想法思路，从零开始搭建一下，加深印象。在搭建过程中，可能会遇到问题，此时不要慌（嘴上不说，心里却慌得狠）。但幸运的是，你遇到了我，可以联系，留言或关注我，一起交流。

为了不造成知识点的混淆，我将`spring-boot-dubbo-nacos-demo`的maven工程，源代码已同步于github，重新拷贝一份，项目重新命名为`spring-boot-dubbo-nacos-configcenter-demo`。
![](https://upload-images.jianshu.io/upload_images/7253165-c0b6b916f0d0b42a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

直接拷贝过来，项目名变更，对应的pom.xml还需要修改一下
![](https://upload-images.jianshu.io/upload_images/7253165-9029da317bdf0b95.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
修改**shop-service-provider和shop-service-consumer:**的pom.xml
![修改shop-service-provider的pom.xml](https://upload-images.jianshu.io/upload_images/7253165-284a977b34c27343.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![修改shop-service-consumer的pom.xml](https://upload-images.jianshu.io/upload_images/7253165-94db11b684fca0a1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

按照上一篇文章的**6、测试**，看一下项目是否能正常启动，如果正常，我们在开始整合nacos的配置中心。确保前面的功能都是正常的。

---
### 4、pom.xml说明
如果想nacos作为配置中心，需要在对应的maven工程中引入`nacos-config-spring-boot-starter`的依赖包，这里，将此依赖包在shop-service-provider和shop-service-consumer的项目中同时引入，这样可以方便服务提供者和服务消费者之间的测试。

这里就不把pom.xml的代码全部粘贴出来，大家想看的话，可以去上一篇文章中看。

`pom.xml`新增`nacos-config-spring-boot-starter`依赖
```
<!-- nacos config依赖 -->
        <dependency>
            <groupId>com.alibaba.boot</groupId>
            <artifactId>nacos-config-spring-boot-starter</artifactId>
            <version>0.2.3</version>
        </dependency>
```
### 5、配置文件说明
shop-service-provider和shop-service-consumer的application.yml，增如下配置，启动项目，会和nacos创建连接
```
nacos:
  service-address: 127.0.0.1
  port: 8848
  config:
    server-addr: ${nacos.service-address}:${nacos.port}
```
相关配置参数，请参考`com.alibaba.boot.nacos.config.properties.NacosConfigProperties.java`
![NacosConfigProperties](https://upload-images.jianshu.io/upload_images/7253165-01dba055d8d4496f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---

### 6、编写业务代码
##### 6.1、shop-service-provider增加配置实体类NacosConfig.java

NacosConfig.java代码实现：
```
package cn.raysonblog.shopserviceprovider.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 从Nacos外部拉取配置, 修改配置，自动会刷新应用的配置
 *
 * @author raysonfang
 */
@NacosPropertySource(dataId = "rayson", autoRefreshed = true)
@Data
@Component
public class NacosConfig {

    @NacosValue(value = "${service.name:1}", autoRefreshed = true)
    private String serviceName;
}
```

注解说明：
`@NacosPropertySource`注解其中包含两个属性，如下：
 - dataId：这个属性是需要在Nacos中配置的Data Id。
 - autoRefreshed：为true的话开启自动更新。

在使用Nacos做配置中心后，需要使用`@NacosValue`注解获取配置，使用方式与`@Value`一样。

其中`${service.name:1}`的service.name是属性key,  `1`是默认值。

##### 6.2、shop-service-provider将NacosConfig的信息暴露到接口中获取
```
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
```
`RpcShopService.java`接口新增`getConfigServiceName()`方法
```
package cn.raysonblog.shopserviceprovider.service;

/**
 * 提供暴露的Rpc接口
 * @author raysonfang
 */
public interface RpcShopService {
    String sayHello(String name);
    String getConfigServiceName(String desc);
}
```

##### 6.3、shop-service-consumer新增接口方法`/getConfig`
```
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
```
---
### 7、测试
项目启动顺序，这里再贴一下上一篇的图：
![image](https://upload-images.jianshu.io/upload_images/7253165-e7114a4e94be8074.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/851/format/webp)

nacos还没配置信息时，输入`http://localhost:8081/getConfig`，显示的是默认值
![显示默认值](https://upload-images.jianshu.io/upload_images/7253165-f1582d4912272624.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

去nacos控制台新增如下配置：
![新增配置](https://upload-images.jianshu.io/upload_images/7253165-1e51726e8008b316.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


![新增成功!](https://upload-images.jianshu.io/upload_images/7253165-ee8055f09fe26159.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

刷新`http://localhost:8081/getConfig`，配置由`1`更新为`hello nacos config-center!`
![hello nacos config-center](https://upload-images.jianshu.io/upload_images/7253165-01ae7c1b15b78c7a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---
### 8、问题记录及解决

##### 8.1、对于dataID的配置不清楚，当时我使用rayson.service，导致客户端解析错误。

**解决**：查看源码得知，在NacosUtils.java中，对dataId有解析，`.`后面的值相当于文件后缀名。故，如果需要配置，则配置成支持的文件后缀名。
![](https://upload-images.jianshu.io/upload_images/7253165-c4133c25001e283d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---
### 9、后记
> 由于能力有限，若有错误或者不当之处，还请大家批评指正，一起学习交流！

源代码放置Github: [https://github.com/raysonfang/spring-boot-demo-all](https://github.com/raysonfang/spring-boot-demo-all)

欢迎大家star, 批评

我平常学习，编码也都会放置github上，欢迎持续关注交流。
我的github: [https://github.com/raysonfang](https://github.com/raysonfang)

![Java技术干货](https://upload-images.jianshu.io/upload_images/7253165-4a99cec9508f7c11.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/601/format/webp)

---
文章推荐
[1. Spring Boot 2.x 基础案例：整合Dubbo 2.7.3+Nacos1.1.3（注册中心）](https://www.jianshu.com/p/b0dddce1d404)

