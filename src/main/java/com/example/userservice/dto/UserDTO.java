package com.example.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Integer id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime created_at;
}
