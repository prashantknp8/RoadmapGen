package com.roadmapgen.demo.controller;

import com.roadmapgen.demo.config.JwtUtil;
import com.roadmapgen.demo.model.AuthRequest;
import com.roadmapgen.demo.model.Course;
import com.roadmapgen.demo.model.User;
import com.roadmapgen.demo.repository.UserRepository;
import com.roadmapgen.demo.service.CourseService;
import com.roadmapgen.demo.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;


    private CourseService courseService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepo.save(user);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails user = userRepo.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal User userDetails) {
        return ResponseEntity.ok("Hello, " + userDetails.getMobileNo() + "! This is your profile.");
    }

//    @PostMapping("/plan")
//    public Course getCourse(@RequestParam String skill) {
//        return courseService.getCourse(skill);
//    }


}

