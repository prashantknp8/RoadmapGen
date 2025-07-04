package com.roadmapgen.demo.service;


import com.roadmapgen.demo.dto.CourseResponse;
import com.roadmapgen.demo.dto.RoadmapResponse;
import com.roadmapgen.demo.model.Course;
import com.roadmapgen.demo.model.RoadmapStep;
import com.roadmapgen.demo.model.User;
import com.roadmapgen.demo.repository.CourseRepository;
import com.roadmapgen.demo.repository.RoadmapStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoadmapFetchService {

    private final CourseRepository courseRepository;
    private final RoadmapStepRepository roadmapStepRepository;


    public List<CourseResponse> getUserCoursesWithRoadmap(User user) {
        List<Course> courses = courseRepository.findByUser(user);

        return courses.stream().map(course -> {
            List<RoadmapStep> steps = roadmapStepRepository.findByCourse(course);

            List<RoadmapResponse> stepResponses = steps.stream().map(step -> {
                RoadmapResponse dto = new RoadmapResponse();
                dto.setId(Math.toIntExact(step.getId()));
                dto.setDay(step.getDay());
                dto.setTopic(step.getTopic());
                return dto;
            }).collect(Collectors.toList());

            CourseResponse response = new CourseResponse();
            response.setCourseId(course.getId());
            response.setName(course.getName());
            response.setResource1(course.getResource1());
            response.setResource2(course.getResource2());
            response.setDays(course.getDays());
            response.setRoadmapSteps(stepResponses);

            return response;
        }).collect(Collectors.toList());
    }
}
