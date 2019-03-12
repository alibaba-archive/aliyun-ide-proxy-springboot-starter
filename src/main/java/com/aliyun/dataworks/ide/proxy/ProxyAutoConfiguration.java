package com.aliyun.dataworks.ide.proxy;

import com.alibaba.dataworks.dataservice.sdk.common.BeanRegistryProcessor;
import com.alibaba.dataworks.dataservice.sdk.facade.DataApiClient;
import com.alibaba.dataworks.dataservice.sdk.loader.http.util.DataServiceHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springboot configuration class
 *
 * @author genxiaogu
 * @date 2019.03.12
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({ProxyProperties.class})
@ConditionalOnProperty(
    name = "dw.demo.enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class ProxyAutoConfiguration {

    static int order = 2 ;

    @Autowired
    ProxyProperties proxyProperties;

    @Bean
    public BeanRegistryProcessor beanRegistryProcessor(){
        return new BeanRegistryProcessor();
    }

    /**
     * 注册一个filter
     */
    @Bean
    public FilterRegistrationBean proxyFilter(ProxyProperties proxyProperties , DataApiClient dataApiClient){
        // 设置自定义的超时时间
        DataServiceHttpClient.initHttpClient(proxyProperties.getConnectTimeout() , proxyProperties.getReadTimeout() , proxyProperties.getConnectionRequestTimeout());
        FilterRegistrationBean registration = new FilterRegistrationBean();
        ProxyFilter proxyFilter = new ProxyFilter(proxyProperties,dataApiClient) ;
        registration.setName("proxyFilter");
        registration.setFilter(proxyFilter);
        registration.addUrlPatterns(proxyProperties.getApiPrefix());
        registration.setOrder(order);
        return registration;
    }

}
