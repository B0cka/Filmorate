package ru.yandex.practicum.filmorate.model;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Director {
    @NotBlank
    private String name;
    private long id;
}
