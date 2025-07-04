package com.roadmapgen.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseResponse {
    private Integer courseId;
    private String name;
    private String resource1;
    private String resource2;
    private Integer days;
    private List<RoadmapResponse> roadmapSteps;
}
