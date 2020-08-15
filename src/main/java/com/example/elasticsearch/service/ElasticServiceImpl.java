package com.example.elasticsearch.service;

import com.example.elasticsearch.entity.DocBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service("elasticService")
public class ElasticServiceImpl{

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private Pageable pageable = PageRequest.of(0,10);


    public void createIndex() {
        elasticsearchRestTemplate.createIndex(DocBean.class);
    }


    public void deleteIndex(String index) {
        elasticsearchRestTemplate.deleteIndex(index);
    }


    public void save(DocBean docBean) {
        elasticsearchRestTemplate.save(docBean);
    }


    public void saveAll(List<DocBean> list) {

        elasticsearchRestTemplate.save(list);
    }


    public Iterator<DocBean> findAll() {
        return null;
    }


    public Page<DocBean> findByContent(String content) {
        return null;
    }


    public Page<DocBean> findByFirstCode(String firstCode) {
        return null;
    }


    public Page<DocBean> findBySecordCode(String secordCode) {
        return null;
    }

}