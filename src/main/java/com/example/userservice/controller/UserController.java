package com.example.userservice.controller;

import com.example.userservice.dto.UserDTO;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "User controller", description = "HTML-интерфейс для управления пользователями")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Operation(
            summary = "Получить список всех существующих пользователей",
            description = "Возвращает HTML-страницу со списком всех существующих пользователей"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Страница с пользователями успешно загружена"
    )
    @GetMapping
    public String getAllUsers(Model model) {
        logger.info("Request for all users");
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @Operation(
            summary = "Форма для создания нового пользователя",
            description = "Возвращает HTML-форму для создания нового пользователя"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Форма для создания пользователя успешно загружена"
    )
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        logger.info("Request for user create form");
        model.addAttribute("userDTO", new UserDTO());
        return "users/create";
    }

    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя. После успешного создания возвращает на список пользователей."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно создан"
            )
    })
    @PostMapping
    public String createUser(
            @Parameter(description = "Данные для создания нового пользователя")
            @ModelAttribute UserDTO userDto,
            RedirectAttributes redirectAttributes) {
        logger.info("Try to create new user");
        try {
            userService.createUser(userDto);
            logger.info("User created with ID: " + userDto.getId());
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
        } catch (Exception e) {
            logger.error("Error on user create: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @Operation(
            summary = "Форма для редактирования пользователя",
            description = "Возвращает HTML-форму для редактирования данных пользователя"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Форма редактирования успешно загружена"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Пользователь не найден на сервере"
            )
    })
    @GetMapping("/{id}/update")
    public String showEditForm(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id,
            Model model) {
        logger.info("Request for showing edit form for userID: " + id);
        model.addAttribute("userDto", userService.getUserById(id));
        return "users/update";
    }

    @Operation(
            summary = "Обновить данные пользователя",
            description = "Обновляет данные пользователя и возвращает к списку пользователей"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Обновление данных пользователя"
    )
    @PostMapping("/{id}")
    public String updateUser(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "Обновленные данные пользователя")
            @ModelAttribute UserDTO userDto,
            RedirectAttributes redirectAttributes) {
        logger.info("Try to update with id: " + id);
        try {
            userService.updateUser(id, userDto);
            logger.info("Updating user with id: " + id);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
        } catch (Exception e) {
            logger.error("Error on user update, userid: " + id + " Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя и отправляет событие удаления в Kafka. Перенаправляет на список пользователей."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Удаление пользователя"
    )
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Trying to delete user, userid: " + id);
        try {
            userService.deleteUser(id);
            logger.info("Deleting user with id: " + id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            logger.error("Error on user delete, userid: " + id + " Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }
}