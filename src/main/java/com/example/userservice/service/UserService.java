package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.User;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public List<UserDTO> getAllUsers() {
        logger.debug("Getting the list of all users");
        List<UserDTO> users = userRepository.findAll().stream()
                .map(userMapper::mapToUserDTO)
                .sorted(Comparator.comparing(UserDTO::getId))
                .collect(Collectors.toList());
        logger.info("Get " + users.size() + " users");
        return users;
    }

    @Transactional
    public UserDTO getUserById(Long id) {
        logger.debug("Getting the user by id");
        return userRepository.findById(id)
                .map(userMapper::mapToUserDTO)
                .orElseThrow(() -> {
                    logger.error("User not found ID:" + id);
                    return new RuntimeException("User not found by id: " + id);
                });
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        logger.debug("Creating new user");
        User user = userMapper.mapToUserEntity(userDTO);
        logger.info("User created ID: " + user.getId());
        return userMapper.mapToUserDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        logger.debug("Updating user ID: " + id);
        User foundedUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found ID: " + id);
                    return new RuntimeException("User not found by id: " + id);
                });
        userMapper.updateUserFromDTO(userDTO, foundedUser);
        logger.info("User updated ID: " + id);
        return userMapper.mapToUserDTO(userRepository.save(foundedUser));
    }

    @Transactional
    public void deleteUser(Long id) {
        logger.debug("Deleting user ID: " + id);
        User found = userRepository.findById(id).orElseThrow(() -> {
            logger.error("User not found ID: " + id);
            return new RuntimeException("User not found by id: " + id);
        });
        if (found != null) {
            userRepository.delete(found);
            logger.info("User deleted ID: " + id);
        }
    }
}
