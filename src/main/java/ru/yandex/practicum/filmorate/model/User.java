package ru.yandex.practicum.filmorate.model;

import lombok.Data;


import java.time.LocalDate;

@Data

public class User {
    private long id;
    private String email;
    private String login;
    private String username;
    private LocalDate birthday;


}

