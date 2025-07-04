package com.roadmapgen.demo.controller;

import com.roadmapgen.demo.dto.CourseResponse;
import com.roadmapgen.demo.model.Course;
import com.roadmapgen.demo.model.User;
import com.roadmapgen.demo.service.CourseService;
import com.roadmapgen.demo.service.RoadmapFetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class UserController {

    private final CourseService courseService;
    private final  RoadmapFetchService roadmapFetchService;

//    @GetMapping("/plan")
//    public ResponseEntity<Course> getCourseBySkill(@AuthenticationPrincipal User userDetails, @RequestParam String skill) {
//        Course course = courseService.getCourse(skill);
//        return ResponseEntity.ok(course);
//
//    }



@PostMapping("/generate")
public Map<String, Object> generateRoadmap(
        @AuthenticationPrincipal User user,
        @RequestParam String skill,
        @RequestParam Integer days
) throws Exception {
    return courseService.generateCourseAndRoadmap(user, skill, days);
}

    @GetMapping("/getRoadmap")
    public List<CourseResponse> getUserRoadmaps(@AuthenticationPrincipal User user) {

        return roadmapFetchService.getUserCoursesWithRoadmap(user);
    }

}
