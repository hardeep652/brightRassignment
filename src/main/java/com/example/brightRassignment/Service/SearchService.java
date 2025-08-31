package com.example.brightRassignment.Service;

import com.example.brightRassignment.Document.CourseDocument;
import com.example.brightRassignment.Repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SearchService {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    public SearchService(CourseRepository courseRepository, ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.objectMapper = objectMapper;
        bulkIndexSampleData();
    }

    public void bulkIndexSampleData() {
        try (InputStream is = getClass().getResourceAsStream("/sample-courses.json")) {
            if (is == null) {
                System.out.println("sample-courses.json not found!");
                return;
            }

            List<CourseDocument> courses = objectMapper.readValue(is, new TypeReference<>() {});
            courses.forEach(c -> {
                if (c.getNextSessionDate() == null) c.setNextSessionDate(Instant.now());
            });
            courseRepository.saveAll(courses);
            System.out.println("Indexed " + courses.size() + " courses.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SearchResult searchCourses(
            String q,
            Integer minAge,
            Integer maxAge,
            String category,
            String type,
            Double minPrice,
            Double maxPrice,
            Instant startDate,
            String sort,
            int page,
            int size
    ) {
        // Fetch all first, then filter in Java
        List<CourseDocument> filtered = StreamSupport.stream(courseRepository.findAll().spliterator(), false)
                .filter(c -> q == null || q.isBlank() || c.getTitle().toLowerCase().contains(q.toLowerCase()) || c.getDescription().toLowerCase().contains(q.toLowerCase()))
                .filter(c -> minAge == null || c.getMinAge() >= minAge)
                .filter(c -> maxAge == null || c.getMaxAge() <= maxAge)
                .filter(c -> category == null || category.isBlank() || c.getCategory().equalsIgnoreCase(category))
                .filter(c -> type == null || type.isBlank() || c.getType().equalsIgnoreCase(type))
                .filter(c -> minPrice == null || c.getPrice() >= minPrice)
                .filter(c -> maxPrice == null || c.getPrice() <= maxPrice)
                .filter(c -> startDate == null || !c.getNextSessionDate().isBefore(startDate))
                .sorted(getComparator(sort))
                .collect(Collectors.toList());

        int total = filtered.size();
        int from = page * size;
        int to = Math.min(from + size, total);
        List<CourseDocument> paginated = from >= total ? List.of() : filtered.subList(from, to);

        return new SearchResult(total, paginated);
    }

    private Comparator<CourseDocument> getComparator(String sort) {
        if ("priceAsc".equalsIgnoreCase(sort)) return Comparator.comparingDouble(CourseDocument::getPrice);
        if ("priceDesc".equalsIgnoreCase(sort)) return Comparator.comparingDouble(CourseDocument::getPrice).reversed();
        return Comparator.comparing(CourseDocument::getNextSessionDate);
    }

    public static class SearchResult {
        private final int total;
        private final List<CourseDocument> courses;
        public SearchResult(int total, List<CourseDocument> courses) { this.total = total; this.courses = courses; }
        public int getTotal() { return total; }
        public List<CourseDocument> getCourses() { return courses; }
    }
}
