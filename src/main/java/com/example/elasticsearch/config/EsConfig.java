package com.example.elasticsearch.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@ComponentScan(basePackageClasses=ESClientSpringFactory.class)
public class EsConfig {

    @Value("${elasticsearch.uris}")
    private String uris;

    @Bean(initMethod="init",destroyMethod="close")
    public ESClientSpringFactory getFactory(){
        String[] uriArr = uris.split(",");
        List<HttpHost> httpHosts = new ArrayList<>(uriArr.length);
        for (String uri : uriArr) {
            String[] hostPortArr = uri.split(":");
            String host = hostPortArr[0];
            int port = Integer.parseInt(hostPortArr[1]);
            httpHosts.add(new HttpHost(host,port,"http"));
        }

        return ESClientSpringFactory.
                build(httpHosts);
    }

    @Bean
    public RestClient getRestClient(){
        return getFactory().getClient();
    }

    @Bean
    public RestHighLevelClient getRestHighLevelClient(){
        return getFactory().getRestHighLevelClient();
    }
}