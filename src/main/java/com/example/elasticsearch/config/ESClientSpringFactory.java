package com.example.elasticsearch.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.List;

@Slf4j
public class ESClientSpringFactory {
    /**
     * 连接超时时间
     */
    public static int CONNECT_TIMEOUT_MILLIS = 1000;
    /**
     * socket 超时时间
     */
    public static int SOCKET_TIMEOUT_MILLIS = 30000;
    /**
     * 连接请求超时时间
     */
    public static int CONNECTION_REQUEST_TIMEOUT_MILLIS = 500;
    /**
     * 某一个服务每次能并行接收的请求数量
     */
    public static int MAX_CONN_PER_ROUTE = 10;
    /**
     * 最大连接数
     */
    public static int MAX_CONN_TOTAL = 30;

    private RestClientBuilder builder;
    private RestClient restClient;
    private RestHighLevelClient restHighLevelClient;
    private static List<HttpHost> httpHosts;

    private static final ESClientSpringFactory esClientSpringFactory = new ESClientSpringFactory();

    private ESClientSpringFactory() {
    }

    public static ESClientSpringFactory build(List<HttpHost> httpHosts) {
        ESClientSpringFactory.httpHosts = httpHosts;
        return esClientSpringFactory;
    }


    public void init() {
        builder = RestClient.builder(httpHosts.toArray(new HttpHost[]{}));
        setConnectTimeOutConfig();
        setMutiConnectConfig();
        restClient = builder.build();
        restHighLevelClient = new RestHighLevelClient(builder);
        log.info("init factory");
    }

    // 配置连接时间延时
    public void setConnectTimeOutConfig() {
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            requestConfigBuilder.setSocketTimeout(SOCKET_TIMEOUT_MILLIS);
            requestConfigBuilder.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MILLIS);
            return requestConfigBuilder;
        });
    }

    // 使用异步httpclient时设置并发连接数
    public void setMutiConnectConfig() {
        builder.setHttpClientConfigCallback(httpAsyncClientBuilder ->
                httpAsyncClientBuilder
                        .setMaxConnTotal(MAX_CONN_TOTAL)
                        .setMaxConnPerRoute(MAX_CONN_PER_ROUTE));
    }

    public RestClient getClient() {
        return restClient;
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public void close() {
        if (restClient != null) {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("close client");
    }
}