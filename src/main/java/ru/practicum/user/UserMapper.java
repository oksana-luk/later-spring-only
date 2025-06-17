package ru.practicum.user;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class UserMapper implements Function<User, UserDto> {
    private static DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy.MM.dd, hh:mm:ss").withZone(ZoneId.of("UTC"));

    @Override
    public UserDto apply(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFullName(user.getFullName());
        userDto.setEmail(user.getEmail());
        userDto.setRegistrationDate(dateTimeFormatter.format(user.getRegistrationDate()));
        return userDto;
    }

    public User mappToUser(UserCreateDto userCreateDto) {
        if (userCreateDto == null) {
            return null;
        }
        User user = new User();
        String[] partsOfName = userCreateDto.getFullName().split(" ");
        user.setFirstName(partsOfName[0]);
        if (partsOfName.length > 1) {
            user.setLastName(partsOfName[1]);
        }
        user.setEmail(userCreateDto.getEmail());
        user.setState(UserState.ACTIVE);
        return user;
    }
}
