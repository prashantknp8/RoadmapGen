package com.roadmapgen.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String resource1;

    private String resource2;

    private Integer days;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


}
