package com.example.userservice.mapper;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    public UserDTO mapToUserDTO(User entity) {
        logger.debug("Mapping user to dto ID: " + entity.getId());
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setAge(entity.getAge());
        dto.setCreated_at(entity.getCreated_at());
        return dto;
    }

    public User mapToUserEntity(UserDTO dto) {
        logger.debug("Mapping dto to user ID: " + dto.getId());
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        return user;
    }

    public void updateUserFromDTO(UserDTO dto, User user) {
        logger.debug("Updating user from dto ID: " + user.getId());
        if (dto.getName() != null) {
            logger.trace("Update name from " + user.getName() + " to " + dto.getName());
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            logger.trace("Update email from " + user.getEmail() + " to " + dto.getEmail());
            user.setEmail(dto.getEmail());
        }
        if (dto.getAge() != null) {
            logger.trace("Update age from " + user.getAge() + " to " + dto.getAge());
            user.setAge(dto.getAge());
        }
    }
}
