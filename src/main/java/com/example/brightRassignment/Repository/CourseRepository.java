package com.example.brightRassignment.Repository;

import com.example.brightRassignment.Document.CourseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {

    // Basic derived queries
    List<CourseDocument> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    List<CourseDocument> findByCategoryIgnoreCase(String category);

    List<CourseDocument> findByTypeIgnoreCase(String type);

    List<CourseDocument> findByMinAgeGreaterThanEqualAndMaxAgeLessThanEqual(Integer minAge, Integer maxAge);

    List<CourseDocument> findByPriceBetween(Double minPrice, Double maxPrice);

    List<CourseDocument> findByNextSessionDateAfter(Instant startDate);
}
