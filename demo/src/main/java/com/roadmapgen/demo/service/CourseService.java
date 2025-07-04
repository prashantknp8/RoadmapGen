package com.roadmapgen.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadmapgen.demo.dto.RoadmapDto;
import com.roadmapgen.demo.model.Course;
import com.roadmapgen.demo.model.RoadmapStep;
import com.roadmapgen.demo.model.User;
import com.roadmapgen.demo.repository.CourseRepository;
import com.roadmapgen.demo.repository.RoadmapStepRepository;
import com.roadmapgen.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final RoadmapStepRepository roadmapStepRepository;
    private final ObjectMapper objectMapper;
    private final  UserRepository userRepository;
    private final CourseRepository clearRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${openrouter.base-url}")
    private String baseUrl;

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.model}")
    private String model;

    @Transactional
    public Map<String, Object> generateCourseAndRoadmap(User user, String skill, Integer days) throws Exception {

        WebClient webClient = webClientBuilder.baseUrl(baseUrl).build();


        User managedUser = userRepository.findByIdWithCourses(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (managedUser.getCourse() != null && !managedUser.getCourse().isEmpty()) {
            clearUserCourse(managedUser);
        }

        // 1. Generate Course JSON
        String coursePrompt = String.format("""
            You are an expert learning advisor.
            Given a skill name, return a detailed learning plan in the following strict JSON format:

            {
              "name": "<skill name>",
              "resource1": "<link to first recommended resource>",
              "resource2": "<link to second recommended resource>",
              "days": <estimated number of days to complete the skill as an integer>
            }

            Only return valid JSON. No explanation.

            Skill: %s
            """, skill);

        String courseContent = callAiApi(webClient, coursePrompt);
        Course course = objectMapper.readValue(courseContent, Course.class);
        course.setUser(user);
        course = courseRepository.save(course);

        // 2. Generate Day-wise Roadmap
        String roadmapPrompt = String.format("""
            Generate a %d-day day-wise learning roadmap for %s.
            Return STRICTLY valid JSON as an array of objects, each containing "day" and "topic" fields.

            Example:
            [
              { "day": 1, "topic": "Introduction to ..." },
              { "day": 2, "topic": "Next topic ..." }
            ]

            Only return valid JSON. No explanation.
            """, days, skill);

        String roadmapContent = callAiApi(webClient, roadmapPrompt);

        // Deserialize into DTO list
        List<RoadmapDto> stepDtos = objectMapper.readValue(roadmapContent, new TypeReference<List<RoadmapDto>>() {});

        // Convert DTOs to entities, set course
        Course finalCourse = course;
        List<RoadmapStep> steps = stepDtos.stream().map(dto -> {
            RoadmapStep step = new RoadmapStep();
            step.setDay(dto.getDay());
            step.setTopic(dto.getTopic());
            step.setCourse(finalCourse);
            return step;
        }).toList();

        roadmapStepRepository.saveAll(steps);

        // 3. Prepare response
        Map<String, Object> result = new HashMap<>();
        result.put("course", course);
        result.put("roadmapSteps", steps);

        return result;
    }

    private void clearUserCourse(User user) {
        roadmapStepRepository.deleteByCourse(user.getCourse().get(0));
        clearRepository.deleteByUser(user);
    }

    private String callAiApi(WebClient webClient, String prompt) throws Exception {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "You are a helpful assistant."),
                Map.of("role", "user", "content", prompt)
        );

        requestBody.put("messages", messages);

        String response = webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode root = objectMapper.readTree(response);
        return root.get("choices").get(0).get("message").get("content").asText();
    }
}
