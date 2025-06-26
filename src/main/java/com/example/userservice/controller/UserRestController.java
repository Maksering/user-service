package com.example.userservice.controller;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Tag(name = "User REST Controller", description = "REST API для управления пользователями с поддержкой HATEOAS")
public class UserRestController {

    private final UserService userService;

    @Operation(
            summary = "Получить список всех пользователей",
            description = "Возвращает список всех пользователей с HATEOAS-ссылками"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список пользователей успешно получен"
    )
    @GetMapping
    public CollectionModel<EntityModel<UserDTO>> getAllUsers() {
        List<EntityModel<UserDTO>> users = userService.getAllUsers().stream()
                .map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserRestController.class).getUserById(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserRestController.class).updateUser(user.getId(), user)).withRel("update"),
                        linkTo(methodOn(UserRestController.class).deleteUser(user.getId())).withRel("delete")))
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(UserRestController.class).getAllUsers()).withSelfRel();
        Link createLink = linkTo(methodOn(UserRestController.class).createUser(null)).withRel("create");

        return CollectionModel.of(users, selfLink, createLink);
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает пользователя с указанным ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно найден"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Пользователь не найден"
            )
    })
    @GetMapping("/{id}")
    public EntityModel<UserDTO> getUserById(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return EntityModel.of(user,
                linkTo(methodOn(UserRestController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserRestController.class).updateUser(id, user)).withRel("update"),
                linkTo(methodOn(UserRestController.class).deleteUser(id)).withRel("delete"),
                linkTo(methodOn(UserRestController.class).getAllUsers()).withRel("users"));
    }

    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя и возвращает созданный ресурс"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Пользователь успешно создан"
    )
    @PostMapping
    public ResponseEntity<EntityModel<UserDTO>> createUser(
            @Parameter(description = "Данные пользователя", required = true)
            @RequestBody UserDTO userDto) {
        UserDTO createdUser = userService.createUser(userDto);
        EntityModel<UserDTO> resource = EntityModel.of(createdUser,
                linkTo(methodOn(UserRestController.class).getUserById(createdUser.getId())).withSelfRel());

        return ResponseEntity.created(
                        linkTo(methodOn(UserRestController.class).getUserById(createdUser.getId())).toUri())
                .body(resource);
    }

    @Operation(
            summary = "Обновить пользователя",
            description = "Обновляет данные пользователя с указанным ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно обновлен"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Пользователь не найден"
            )
    })
    @PutMapping("/{id}")
    public EntityModel<UserDTO> updateUser(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные пользователя", required = true)
            @RequestBody UserDTO userDto) {
        UserDTO updatedUser = userService.updateUser(id, userDto);
        return EntityModel.of(updatedUser,
                linkTo(methodOn(UserRestController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserRestController.class).getAllUsers()).withRel("users"));
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя с указанным ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно удален"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Пользователь не найден"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().body(
                EntityModel.of(Link.of(linkTo(methodOn(UserRestController.class).getAllUsers()).toString(), "all-users")));
    }
}