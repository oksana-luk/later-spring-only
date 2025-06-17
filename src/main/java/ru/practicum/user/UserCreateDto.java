package ru.practicum.user;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserCreateDto {
    private String fullName;
    private String email;
}
