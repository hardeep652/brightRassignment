package com.example.brightRassignment.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // 1️⃣ Configure ObjectMapper with JavaTimeModule
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 2️⃣ JacksonJsonpMapper wraps the ObjectMapper
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(mapper);

        // 3️⃣ Create low-level REST client
        RestClient restClient = RestClient.builder(
            new HttpHost("localhost", 9200)
        ).build();

        // 4️⃣ Create transport layer
        RestClientTransport transport = new RestClientTransport(restClient, jsonpMapper);

        // 5️⃣ Return Elasticsearch client
        return new ElasticsearchClient(transport);
    }
}
