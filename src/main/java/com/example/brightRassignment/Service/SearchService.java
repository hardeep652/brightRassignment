package com.example.brightRassignment.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.brightRassignment.Document.Autocomplete;
import com.example.brightRassignment.Document.CourseDocument;
import com.example.brightRassignment.Repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.suggest.Completion;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
public class SearchService {

    private static final String INDEX = "courses";

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    private ElasticsearchClient client;

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

    // ✅ Proper ES completion object
    c.setAutocomplete(new Completion(new String[]{c.getTitle()}));
});


            courseRepository.saveAll(courses);
            System.out.println("Indexed " + courses.size() + " courses with autocomplete.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= Repository-based search =================
    public RepoSearchResult searchCoursesRepo(
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

        return new RepoSearchResult(total, paginated);
    }
     
    // ================= Elasticsearch fuzzy search =================
public List<CourseDocument> searchCoursesFuzzy(String q, Integer minAge, Integer maxAge,
                                               String category, String type,
                                               Double minPrice, Double maxPrice,
                                               Instant startDate, String sort,
                                               int page, int size) throws IOException {

    final String queryText = (q == null || q.isBlank()) ? "" : q; // avoid null issues

    int from = page * size;

    SearchResponse<CourseDocument> response = client.search(s -> s
                    .index(INDEX)
                    .from(from)
                    .size(size)
                    .query(qb -> qb
                            .bool(b -> {
                                // Full-text fuzzy search on title
                                b.must(m -> m
                                        .match(ma -> ma
                                                .field("title")
                                                .query(queryText)
                                                .fuzziness("AUTO") // <-- actual fuzziness
                                        )
                                );

                                // Filters
                                if (minAge != null) b.filter(f -> f.range(r -> r.field("minAge").gte(co.elastic.clients.json.JsonData.of(minAge))));
                                if (maxAge != null) b.filter(f -> f.range(r -> r.field("maxAge").lte(co.elastic.clients.json.JsonData.of(maxAge))));
                                if (category != null && !category.isBlank()) b.filter(f -> f.term(t -> t.field("category").value(category)));
                                if (type != null && !type.isBlank()) b.filter(f -> f.term(t -> t.field("type").value(type)));
                                if (minPrice != null) b.filter(f -> f.range(r -> r.field("price").gte(co.elastic.clients.json.JsonData.of(minPrice))));
                                if (maxPrice != null) b.filter(f -> f.range(r -> r.field("price").lte(co.elastic.clients.json.JsonData.of(maxPrice))));
                                if (startDate != null) b.filter(f -> f.range(r -> r.field("nextSessionDate").gte(co.elastic.clients.json.JsonData.of(startDate.toString()))));
                                return b;
                            })
                    )
                    .sort(sb -> {
                        if ("priceAsc".equalsIgnoreCase(sort)) return sb.field(f -> f.field("price").order(co.elastic.clients.elasticsearch._types.SortOrder.Asc));
                        if ("priceDesc".equalsIgnoreCase(sort)) return sb.field(f -> f.field("price").order(co.elastic.clients.elasticsearch._types.SortOrder.Desc));
                        return sb.field(f -> f.field("nextSessionDate").order(co.elastic.clients.elasticsearch._types.SortOrder.Asc));
                    }),
            CourseDocument.class
    );

    List<CourseDocument> hits = response.hits().hits().stream()
            .map(Hit::source)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    return hits;
}

    private Comparator<CourseDocument> getComparator(String sort) {
        if ("priceAsc".equalsIgnoreCase(sort)) return Comparator.comparingDouble(CourseDocument::getPrice);
        if ("priceDesc".equalsIgnoreCase(sort)) return Comparator.comparingDouble(CourseDocument::getPrice).reversed();
        return Comparator.comparing(CourseDocument::getNextSessionDate);
    }

    public static class RepoSearchResult {
        private final int total;
        private final List<CourseDocument> courses;
        public RepoSearchResult(int total, List<CourseDocument> courses) {
            this.total = total;
            this.courses = courses;
        }
        public int getTotal() { return total; }
        public List<CourseDocument> getCourses() { return courses; }
    }

    // ================= Elasticsearch autocomplete =================
   public List<String> getSuggestions(String prefix) throws IOException {
    SearchResponse<CourseDocument> response = client.search(s -> s
                    .index(INDEX)
                    .suggest(sg -> sg
                        .suggesters("course-suggest", su -> su
                            .prefix(prefix)
                            .completion(c -> c
                                .field("autocomplete")
                                .skipDuplicates(true)
                                .size(5)
                            )
                        )
                    ),
            CourseDocument.class
    );

    if (response.suggest() == null || !response.suggest().containsKey("course-suggest")) {
        return Collections.emptyList();
    }

    // Map only the suggestion texts
    return response.suggest().get("course-suggest").stream()
            .flatMap(s -> s.completion().options().stream())
            .map(opt -> opt.text())  // <-- this avoids mapping to CourseDocument
            .collect(Collectors.toList());
}


}
