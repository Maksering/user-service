package com.example.userservice.service;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.User;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
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
    private final KafkaProducerService kafkaProducer;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Transactional
    public List<UserDTO> getAllUsers() {
        logger.debug("Getting list of all users");
        List<UserDTO> users = circuitBreakerFactory.create("user-service")
                .run(() ->{
                    return userRepository.findAll().stream()
                            .map(userMapper::mapToUserDTO)
                            .sorted(Comparator.comparing(UserDTO::getId))
                            .collect(Collectors.toList());
                }, throwable -> {
                    logger.error("Fail on getAllUsers");
                    return List.of();
                });
        logger.info("Get " + users.size() + " users");
        return users;
    }


    @Transactional
    public UserDTO getUserById(Long id) {
        logger.debug("Getting user by id: " + id);

        return (UserDTO) circuitBreakerFactory.create("user-service")
                .run(() -> userRepository.findById(id)
                                .map(userMapper::mapToUserDTO)
                                .orElseThrow(() -> {
                                    logger.error("User not found by id: " +  id);
                                    return new RuntimeException("User not found by id: " + id);
                                }),
                        throwable -> {
                            logger.error("Failed to get user, ID: " +  id);
                            return  new RuntimeException("User service is unavailable");
                        });
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        logger.debug("Creating user: " + userDTO);

        return circuitBreakerFactory.create("user-service")
                .run(() -> {
                            User user = userMapper.mapToUserEntity(userDTO);
                            User savedUser = userRepository.save(user);
                            kafkaProducer.sendUserCreate(userDTO.getEmail());
                            logger.info("User created ID: " + savedUser.getId());
                            return userMapper.mapToUserDTO(savedUser);
                        },
                        throwable -> {
                            logger.error("Failed to create user " + userDTO);
                            throw new RuntimeException("User service is unavailable");
                        });
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        logger.debug("Update user ID: " + id);

        return circuitBreakerFactory.create("userServiceCB")
                .run(() -> {
                            User existingUser = userRepository.findById(id)
                                    .orElseThrow(() -> {
                                        logger.error("User not found by id: "+ id);
                                        return new RuntimeException("User not found by id: " + id);
                                    });

                            userMapper.updateUserFromDTO(userDTO, existingUser);
                            User updatedUser = userRepository.save(existingUser);
                            logger.info("User updated ID: {}", id);
                            return userMapper.mapToUserDTO(updatedUser);
                        },
                        throwable -> {
                            logger.error("Failed to update user ID: " + id, throwable);
                            throw new RuntimeException("User service is unavailable");
                        });
    }

    @Transactional
    public void deleteUser(Long id) {
        logger.debug("Deleting user ID: " + id);

        circuitBreakerFactory.create("userServiceCB")
                .run(() -> {
                            User user = userRepository.findById(id)
                                    .orElseThrow(() -> {
                                        logger.error("User not found by id: " + id);
                                        return new RuntimeException("User not found by id: " + id);
                                    });

                            userRepository.delete(user);
                            kafkaProducer.sendUserDelete(user.getEmail());
                            logger.info("User deleted ID: {}", id);
                            return null;
                        },
                        throwable -> {
                            logger.error("Failed to delete user by id " + id, throwable);
                            throw new RuntimeException("User service is unavailable");
                        });
    }
}
