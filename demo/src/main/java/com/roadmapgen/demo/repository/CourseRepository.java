package com.roadmapgen.demo.repository;


import com.roadmapgen.demo.model.Course;
import com.roadmapgen.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUser(User user);
    void deleteAll();
    void deleteByUser(User user);
}


