package com.example.brightRassignment.Controller;

import com.example.brightRassignment.Document.CourseDocument;
import com.example.brightRassignment.Service.SearchService;
import com.example.brightRassignment.Service.SearchService.RepoSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public Object searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(defaultValue = "nextSessionDate") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws IOException {

        // Use Elasticsearch fuzzy search if a keyword is provided
        if (q != null && !q.isBlank()) {
            List<CourseDocument> results = searchService.searchCoursesFuzzy(q, minAge, maxAge,
                    category, type, minPrice, maxPrice, startDate, sort, page, size);
            return new RepoSearchResult(results.size(), results);
        }

        // Fallback: in-memory repository search
        return searchService.searchCoursesRepo(q, minAge, maxAge,
                category, type, minPrice, maxPrice, startDate, sort, page, size);
    }

    @GetMapping("/search/suggest")
    public List<String> autocomplete(@RequestParam String prefix) throws IOException {
        return searchService.getSuggestions(prefix);
    }
}
