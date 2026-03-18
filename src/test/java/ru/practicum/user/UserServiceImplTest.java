package ru.practicum.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.config.AppConfig;
import ru.practicum.config.PersistenceConfig;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;

@Transactional
@TestPropertySource("classpath:application-test.properties")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({AppConfig.class, PersistenceConfig.class, UserServiceImpl.class})
class UserServiceImplTest {
    @Autowired
    private final EntityManager entityManager;
    @Autowired
    private final UserService userService;

    @Test
    void saveUser() {
        UserDto userDto = makeUserDto("oksana@yande.ru", "oksana", "chuff");

        userService.saveUser(userDto);

        TypedQuery<User> userQuery = entityManager.createQuery("select u from User u where u.email = :email",
                User.class);
        User user = userQuery.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
        assertThat(user.getFirstName(), equalTo(userDto.getFirstName()));
        assertThat(user.getLastName(), equalTo(userDto.getLastName()));
        assertThat(user.getRegistrationDate(), notNullValue());
        assertThat(user.getState(), equalTo(userDto.getState()));
    }

    @Test
    void getAllUsers() {
        UserDto userDto1 = makeUserDto("oksana1@yande.ru", "oksana1", "chuff1");
        UserDto userDto2 = makeUserDto("oksana2@yande.ru", "oksana2", "chuff2");

        User savedUser1 = UserMapper.mappToUser(userService.saveUser(userDto1));
        User savedUser2 = UserMapper.mappToUser(userService.saveUser(userDto2));

        TypedQuery<User> userQuery = entityManager.createQuery("select u from User u",
                User.class);
        List<User> users = userQuery.getResultList();
        assertThat(users, allOf(notNullValue(),
                hasItem(savedUser1),
                hasItem(savedUser2)));
    }

    private UserDto makeUserDto(String email, String firstName, String lastName) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setState(UserState.ACTIVE);
        return userDto;
    }
}