package cn.raysonblog.quartz.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2的配置类
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * swagger2扫描的基本包路径
     */
    private static final String basePackage = "cn.raysonblog.hotdog.module";

    @Bean
    public Docket defaultApi(){
        
        /**
         * 配置请求头参数
         */
        ParameterBuilder aParameterBuilder = new ParameterBuilder();
        aParameterBuilder.name("Authorization").description("input the token for authentication either in the authorization field or in the token field").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        ParameterBuilder aParameterBuilder1 = new ParameterBuilder();
        aParameterBuilder1.name("token").description("input the token for authentication either in the authorization field or in the token field").modelRef(new ModelRef("string")).parameterType("query").required(false).build();
        List<Parameter> aParameters = new ArrayList<Parameter>();
        aParameters.add(aParameterBuilder.build());
        aParameters.add(aParameterBuilder1.build());
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).useDefaultResponseMessages(false).globalOperationParameters(aParameters).select()
                .apis(RequestHandlerSelectors.basePackage(basePackage)).paths(PathSelectors.any()).build();
    }
    
    // 预览地址:swagger-ui.html
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("利用swagger2构建【spring-boot-demo-all】系统api文档")
                .description("基于Spring Boot 2.x为基础上集成其他组件的案例, 方便大家学习使用。<br/>接口访问地址：http://localhost:8081/, by raysonfang")
                .termsOfServiceUrl("http://localhost:8081/")
                .contact(new Contact("author: raysonfang, 微信公众号：Java技术干货", "https://www.raysonblog.cn/", "793514387@qq.com"))
                .version("1.0")
                .build();
    }
}
