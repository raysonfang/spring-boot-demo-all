#《Spring Boot 2.x 基础案例：整合Dubbo 2.7.3+Nacos1.1.3（最新版）》

![本文的思维导图](https://upload-images.jianshu.io/upload_images/7253165-077b393fe4f0e2db.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
### 1、概述
本文将介绍如何基于Spring Boot 2.x的版本，通过Nacos作为配置与注册中心，实现Dubbo服务的注册与消费。

整合组件的版本说明：
* `Spring Boot 2.1.9`
* `Dubbo 2.7.3`
* `Nacos 1.1.3`

本文的亮点：
- 1.采用yml方式进行dubbo的配置。
- 2.相关组件采用较新版本进行整合。
- 3.相关源代码放置于Github上，可随时查看。

源代码放置Github: [https://github.com/raysonfang/spring-boot-demo-all](https://github.com/raysonfang/spring-boot-demo-all)

---

之前公司在使用Dubbo 2.6.1的时候，采用Zookeeper作为注册中心。当时，也只是仅仅拿来作为注册中心使用，一没有专门的管理后台进行可视化管理操作，二是功能单一，仅作为注册中心使用。

经过一段时间的学习和了解以后，发现采用阿里开源的Nacos作为注册中心与外部配置中心。它比Zookeeper更适合做服务的注册与配置，毕竟是大厂开源，经过大量实践。

如果不清楚Nacos是什么，或具有什么主要功能以及架构设计思想。自行花点时间查一下资料。

**Nacos：**

**注：**`此次主要实践Nacos作为注册中心，后面会单独整合Nacos作为配置中心的实践分享。`

---
### 2、基础框架搭建
使用idea+maven多模块进行项目搭建

**spring-boot-dubbo-nacos-demo：**父工程
![](https://upload-images.jianshu.io/upload_images/7253165-839a252ccd2dd1b0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**shop-service-provider：** dubbo服务提供者
![](https://upload-images.jianshu.io/upload_images/7253165-0c1f4a84227a3905.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**shop-service-consumer:** dubbo服务消费者，是一个web工程
![](https://upload-images.jianshu.io/upload_images/7253165-b6a4aac580569d63.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---
### 3、pom.xml说明
**spring-boot-dubbo-nacos-demo：**父工程的pom.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>cn.raysonblog</groupId>
    <artifactId>misco-dubbo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>misco-dubbo</name>
    <packaging>pom</packaging>
    <description>Demo project for Spring Boot Dubbo Nacos</description>

    <modules>
        <module>shop-service-provider</module>
        <module>shop-service-consumer</module>
    </modules>

    <properties>
        <java.version>1.8</java.version>
        <spring-boot.version>2.1.9.RELEASE</spring-boot.version>
        <dubbo.version>2.7.3</dubbo.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- Apache Dubbo  -->
            <dependency>
                <groupId>org.apache.dubbo</groupId>
                <artifactId>dubbo-dependencies-bom</artifactId>
                <version>${dubbo.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- Dubbo Spring Boot Starter -->
            <dependency>
                <groupId>org.apache.dubbo</groupId>
                <artifactId>dubbo-spring-boot-starter</artifactId>
                <version>${dubbo.version}</version>
            </dependency>
            <dependency>
                <groupId>org.apache.dubbo</groupId>
                <artifactId>dubbo</artifactId>
                <version>${dubbo.version}</version>
                <exclusions>
                    <exclusion>
                        <groupId>org.springframework</groupId>
                        <artifactId>spring</artifactId>
                    </exclusion>
                    <exclusion>
                        <groupId>javax.servlet</groupId>
                        <artifactId>servlet-api</artifactId>
                    </exclusion>
                    <exclusion>
                        <groupId>log4j</groupId>
                        <artifactId>log4j</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <repositories>
        <repository>
            <id>apache.snapshots.https</id>
            <name>Apache Development Snapshot Repository</name>
            <url>https://repository.apache.org/content/repositories/snapshots</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```
---
**shop-service-provider：** pom.xml引入dubbo与nacos相关的jar
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>cn.raysonblog</groupId>
        <artifactId>misco-dubbo</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath> <!-- lookup parent from repository -->
    </parent>
    <groupId>cn.raysonblog</groupId>
    <artifactId>shop-service-provider</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>shop-service-provider</name>
    <description>服务者 Demo project for Spring Boot dubbo nacos</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <exclusions>
                <!-- 排除自带的logback依赖 -->
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </dependency>
        <!--<dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- Dubbo -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
        </dependency>

        <!-- Dubbo Registry Nacos -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-nacos</artifactId>
            <version>2.7.3</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
        </dependency>
    </dependencies>
    <repositories>
        <repository>
            <id>apache.snapshots.https</id>
            <name>Apache Development Snapshot Repository</name>
            <url>https://repository.apache.org/content/repositories/snapshots</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```
---
**shop-service-consumer:** pom.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>cn.raysonblog</groupId>
        <artifactId>misco-dubbo</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath> <!-- lookup parent from repository -->
    </parent>
    <groupId>cn.raysonblog</groupId>
    <artifactId>shop-service-consumer</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>shop-service-consumer</name>
    <description>Demo project for Spring Boot dubbo nacos</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <!-- 排除自带的logback依赖 -->
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
        </dependency>

        <!-- Dubbo Registry Nacos -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-nacos</artifactId>
            <version>2.7.3</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-spring-context</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
        </dependency>
        <dependency>
            <groupId>cn.raysonblog</groupId>
            <artifactId>shop-service-provider</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
    <repositories>
        <repository>
            <id>apache.snapshots.https</id>
            <name>Apache Development Snapshot Repository</name>
            <url>https://repository.apache.org/content/repositories/snapshots</url>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
    </repositories>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

```
---
### 4、配置文件说明
网上大部分资料都是基于application.properties配置，或者是基于xml配置dubbo的相关参数。而实际上，在Spring Boot工程中，
**shop-service-provider：**application.yml配置文件说明
```

spring:
  application:
    name: shop-service-provider
# log config
logging:
  config: classpath:log4j2.xml
  level:
    root: info
    web: info
  file: logs/shop-service-provider.log

# Dubbo Application  nacos
## The default value of dubbo.application.name is ${spring.application.name}
## dubbo.application.name=${spring.application.name}
nacos:
  service-address: 127.0.0.1
  port: 8848
dubbo:
  registry:
    address: nacos://${nacos.service-address}:${nacos.port}
  protocol:
    name: dubbo
    port: 20881
  scan:
   base-packages: cn.raysonblog.*.service.impl
```
---
**shop-service-consumer:** application.yml说明
```
server:
  address:
  port: 8081
  servlet:
    context-path: /
  tomcat:
    uri-encoding: UTF-8

spring:
  application:
    name: shop-service-consumer

# log config
logging:
  config: classpath:log4j2.xml
  level:
    root: info
    web: info
  file: logs/shop-service-provider.log

# Dubbo Application  nacos
## The default value of dubbo.application.name is ${spring.application.name}
## dubbo.application.name=${spring.application.name}
nacos:
  service-address: 127.0.0.1
  port: 8848
dubbo:
  registry:
    address: nacos://${nacos.service-address}:${nacos.port}
```

dubbo相关参数：也可以在`DubboConfigurationProperties`类中查看
![](https://upload-images.jianshu.io/upload_images/7253165-7af386ac25bfdfaf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---
### 5、编写业务代码
#####**shop-service-provider：**代码实现

针对dubbo服务提供者，我没有单独把`RpcShopService`接口单独放到一个子模块提供，建议在引用到实际项目中，可以单独提供接口包，在消费端直接引用接口包，这样就可以脱离服务提供者。

`ShopServiceProviderApplication.java`启动主类
```
package cn.raysonblog.shopserviceprovider;

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
```
需要注意`@EnableDubbo`这个注解，是开启Dubbo服务必要的。

---
`RpcShopService.java`：暴露接口，供消费端使用
```
package cn.raysonblog.shopserviceprovider.service;

/**
 * 提供暴露的Rpc接口
 * @author raysonfang
 */
public interface RpcShopService {
    String sayHello(String name);
}

```
---

`ShopServiceImpl.java`: 实现类

```
package cn.raysonblog.shopserviceprovider.service.impl;

import cn.raysonblog.shopserviceprovider.service.RpcShopService;
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

```

---
#####**shop-service-consumer:** 代码实现
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

    @RequestMapping(name = "/sayHello", method = RequestMethod.GET)
    public String sayHello(){
        return shopService.sayHello("Hello Dubbo Nacos!更多原创分享，技术交流，关注：Java技术干货（ID:raysonfang）");
    }

    public static void main(String[] args) {
        SpringApplication.run(ShopServiceConsumerApplication.class, args);
    }

}

```

---
###6、测试
测试的时候，启动顺序
![启动顺序](https://upload-images.jianshu.io/upload_images/7253165-e7114a4e94be8074.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### 6.1、启动nacos-server注册中心

下载nacos-server:[https://github.com/alibaba/nacos/releases](https://github.com/alibaba/nacos/releases)

![nacos-server下载](https://upload-images.jianshu.io/upload_images/7253165-c23be4d47feada66.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

解压nacos-server, 找到bin目录
![](https://upload-images.jianshu.io/upload_images/7253165-a3c8d25e9f2c2680.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

windows点击startup.cmd, 启动nacos
![nacos-server](https://upload-images.jianshu.io/upload_images/7253165-888039c6921d3b7a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

在浏览器中输入：`http://localhost:8848/nacos/index.html`, 便可以访问到nacos的控制台。用户名和密码默认为`nacos`
![](https://upload-images.jianshu.io/upload_images/7253165-c6fc8f5773247914.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### 6.2、启动shop-service-provider服务提供者
![](https://upload-images.jianshu.io/upload_images/7253165-eef3866099b5a1d8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

在nacos控制台可以看到信息：
![](https://upload-images.jianshu.io/upload_images/7253165-03fa2ac1bf81d221.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

###### 6.3、启动shop-service-consumer服务消费者

![](https://upload-images.jianshu.io/upload_images/7253165-b01fa24e6bb9522d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

在nacos控制台可以看到如下信息：
![](https://upload-images.jianshu.io/upload_images/7253165-e0894e862dbbe110.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

在浏览器端输入：[http://localhost:8081/sayHello](http://localhost:8081/sayHello)， 便会返回结果。
![](https://upload-images.jianshu.io/upload_images/7253165-40f28b57fb658311.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

---
###7、问题记录及解决

###### 7.1、在整合的时候，pom引入dubbo及nacos相关依赖包，花费时间蛮多。主要是包引入不成功。

解决：去maven的本地依赖库中，删除引入不成功的依赖包，在重新reimport。

###### 7.2、 在开启dubbo的时候，注解引用不正确：错误注入`@EnableDubboConfig`。

解决： 换成使用`@EnableDubbo`。

###### 7.3、yml配置Dubbo的相关属性，网上资料蛮少的。

解决：通过查看`DubboConfigurationProperties.java`源码，去分析属性配置。

---
###8、后记

> 由于能力有限，若有错误或者不当之处，还请大家批评指正，一起学习交流！

源代码放置Github: [https://github.com/raysonfang/spring-boot-demo-all](https://github.com/raysonfang/spring-boot-demo-all)

欢迎大家star, 批评

我平常学习，编码也都会放置github上，欢迎持续关注交流。
我的github: [https://github.com/raysonfang](https://github.com/raysonfang)


![](https://upload-images.jianshu.io/upload_images/7253165-4a99cec9508f7c11.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



