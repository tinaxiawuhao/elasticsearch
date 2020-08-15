package com.example.elasticsearch.config;

//@Configuration
//public class EsConf {
//
//    @Value("${elastic.host}")
//    private String host;
//
//    @Value("${elastic.port}")
//    private int port;
//
//
//    @Bean
//    public RestHighLevelClient elasticsearchClient() {
//        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port));
//        return new RestHighLevelClient(builder);
//    }
//
//    @Bean
//    public ElasticsearchRestTemplate elasticsearchTemplate() {
//        return new ElasticsearchRestTemplate(elasticsearchClient());
//    }
//}