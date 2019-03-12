package com.aliyun.dataworks.ide.proxy;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * springboot configuration properties class
 * @author genxiaogu
 * @date 2019.03.12
 */
@ConfigurationProperties(prefix="ide.proxy.data.service")
@Data
public class ProxyProperties {

    private String host ;

    private String appKey ;

    private String appSecret ;

    private String apiPrefix = "/api/1.0/dsproxy";

    private int connectTimeout = 20000 ;

    private int readTimeout = 20000 ;

    private int connectionRequestTimeout = 20000 ;

}
