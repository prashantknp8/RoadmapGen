package com.roadmapgen.demo.repository;

import com.roadmapgen.demo.model.Course;
import com.roadmapgen.demo.model.RoadmapStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadmapStepRepository extends JpaRepository<RoadmapStep, Long> {


    void deleteByCourse(Course course);

    void deleteAll();

    // Optional: If you only have course ID
    List<RoadmapStep> findByCourseId(Integer courseId);

    void deleteByCourseId(Integer courseId);

    List<RoadmapStep> findByCourse(Course course);
}

