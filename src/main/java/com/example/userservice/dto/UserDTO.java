package com.example.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "UserDTO")
public class UserDTO {
    @Schema(description = "Уникальный идентификатор пользователя",
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "1"
    )
    private Long id;
    @Schema(description = "Имя пользователя", example = "Иван")
    private String name;
    @Schema(description = "Электронная почта пользователя", example = "example@example.com")
    private String email;
    @Schema(description = "Возраст пользователся", example = "20")
    private Integer age;
    @Schema(description = "Дата создания пользователя",
            accessMode = Schema.AccessMode.READ_ONLY,
            example = "2025-01-01 12:00:00"
    )
    private LocalDateTime created_at;
}
