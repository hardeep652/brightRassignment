package com.example.brightRassignment.Document;


import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "courses")
public class CourseDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String type; // ONE_TIME, COURSE, CLUB

    @Field(type = FieldType.Keyword)
    private String gradeRange;

    @Field(type = FieldType.Integer)
    private Integer minAge;

    @Field(type = FieldType.Integer)
    private Integer maxAge;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant nextSessionDate;

    public CourseDocument() {}

    // getters and setters (generate from your IDE)
    public String getId()
     {
         return id;
     }

    public void setId(String id)
    {
         this.id = id; 
    }

    public String getTitle() 
    {
         return title; 
    }
    public void setTitle(String title)
    {
         this.title = title; 
    }

    public String getDescription() 
    {
         return description; 
    }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getGradeRange() { return gradeRange; }
    public void setGradeRange(String gradeRange) { this.gradeRange = gradeRange; }

    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }

    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Instant getNextSessionDate() { return nextSessionDate; }
    public void setNextSessionDate(Instant nextSessionDate) { this.nextSessionDate = nextSessionDate; }
}

