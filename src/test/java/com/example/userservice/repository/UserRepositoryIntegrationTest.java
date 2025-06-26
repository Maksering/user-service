package com.example.userservice.repository;

import com.example.userservice.DataBaseIntegrationTestInitClass;
import com.example.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class UserRepositoryIntegrationTest extends DataBaseIntegrationTestInitClass {
    @Autowired
    UserRepository userRepository;

    @Test
    void whenSaveAndExistById_ShouldBeTrue(){
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setAge(20);

        userRepository.save(user);
        boolean existUser = userRepository.existsById(Long.valueOf(user.getId()));

        assertThat(existUser).isTrue();
    }

    @Test
    void whenExistById_NonExistedId_ShouldReturnNull() {
        boolean foundUserBoolean = userRepository.existsById(-1L);

        assertThat(foundUserBoolean).isFalse();
    }

    @Test
    void whenFindById_ShouldBeFound(){
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setAge(20);
        userRepository.save(user);

        Optional<User> existUser = userRepository.findById(Long.valueOf(user.getId()));

        assertThat(existUser.isPresent()).isTrue();
        assertThat(existUser.get().getName()).isEqualTo(user.getName());
    }

    @Test
    void whenFindById_NonExistedId_ShouldReturnEmptyOptional() {
        Optional<User> foundUser = userRepository.findById(-1L);

        assertThat(foundUser).isEmpty();
    }

    @Test
    void whenFoundAll_ListSizeShouldEqualsNumOfUsers(){
        int numOfUsers = 3;
        for(int i = 1; i<=numOfUsers;i++){
            User user = new User();
            user.setName("Test" + i);
            user.setEmail("test"+ i +"@test.com");
            user.setAge(20+i);
            userRepository.save(user);
        }
        List<User> listOfUsers = userRepository.findAll();

        assertThat(listOfUsers.size()).isEqualTo(numOfUsers);
    }

    @Test
    void whenFoundAll_NoUsers_ListShouldBeEmpty(){
        List<User> listOfUsers = userRepository.findAll();

        assertThat(listOfUsers.isEmpty()).isTrue();
    }

    @Test
    void whenDeleteById_ShouldNotExistById(){
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setAge(20);
        userRepository.save(user);

        userRepository.deleteById(Long.valueOf(user.getId()));
        boolean notExist = userRepository.existsById(Long.valueOf(user.getId()));

        assertThat(notExist).isFalse();
    }

    @Test
    void whenDeleteById_NonExistedId_ShouldNotThrowException(){
        assertThatNoException()
                .isThrownBy(() -> userRepository.deleteById(-1L));
    }
}
