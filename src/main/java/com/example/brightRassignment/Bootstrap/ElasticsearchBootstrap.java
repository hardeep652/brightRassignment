package com.example.brightRassignment.Bootstrap;


import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

import com.example.brightRassignment.Document.CourseDocument;
import com.example.brightRassignment.Repository.CourseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ElasticsearchBootstrap implements ApplicationRunner {

    private final CourseRepository repo;
    private final ObjectMapper mapper;
    private final ElasticsearchOperations operations;

    public ElasticsearchBootstrap(CourseRepository repo, ObjectMapper mapper, ElasticsearchOperations operations) {
        this.repo = repo;
        this.mapper = mapper;
        this.operations = operations;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        IndexOperations indexOps = operations.indexOps(CourseDocument.class);
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping());
        }

        if (repo.count() == 0) {
            InputStream is = getClass().getResourceAsStream("/sample-courses.json");
            CourseDocument[] arr = mapper.readValue(is, CourseDocument[].class);
            List<CourseDocument> list = Arrays.asList(arr);
            repo.saveAll(list);
            indexOps.refresh();
            System.out.println("Bulk indexed " + list.size() + " courses.");
        } else {
            System.out.println("Courses index already has data, skipping bootstrap.");
        }
    }
}
