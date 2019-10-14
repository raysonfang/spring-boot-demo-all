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
