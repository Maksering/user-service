package com.example.userservice.controller;

import com.example.userservice.DataBaseIntegrationTestInitClass;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerIntegrationTest extends DataBaseIntegrationTestInitClass {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRedirectFromRootToUsers() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {

        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setAge(20);
        userRepository.save(user);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/list"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attribute("users", hasSize(greaterThan(0))));
    }

    @Test
    void shouldReturnUserCreateForm() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/create"))
                .andExpect(model().attributeExists("userDTO"));
    }

    @Test
    void shouldCreateNewUser() throws Exception {
        mockMvc.perform(post("/users")
                        .param("name", "test")
                        .param("email", "test@test.com")
                        .param("age", "20")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void shouldReturnUserEditForm() throws Exception {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setAge(20);
        User savedUser = userRepository.save(user);

        mockMvc.perform(get("/users/{id}/update", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("users/update"))
                .andExpect(model().attributeExists("userDto"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = new User();
        user.setName("test");
        user.setEmail("test@test.com");
        user.setAge(20);
        User savedUser = userRepository.save(user);

        mockMvc.perform(post("/users/{id}", savedUser.getId())
                        .param("name", "Update")
                        .param("email", "update@test.com")
                        .param("age", "21")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        User user = new User();
        user.setName("Delete User");
        user.setEmail("delete@example.com");
        user.setAge(50);
        User savedUser = userRepository.save(user);

        mockMvc.perform(post("/users/{id}/delete", savedUser.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}

