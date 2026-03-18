package ru.practicum.item;

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
import ru.practicum.item.dto.AddItemRequest;
import ru.practicum.item.model.Item;
import ru.practicum.user.*;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@TestPropertySource("classpath:application-test.properties")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({AppConfig.class, PersistenceConfig.class, ItemServiceImpl.class, UrlMetadataRetrieverImpl.class, UserServiceImpl.class})
class ItemServiceImplTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @Test
    public void shouldAddNewItem() {
        User user = createUser();
        AddItemRequest addItemRequest = new AddItemRequest();
        addItemRequest.setUrl("https://images.app.goo.gl/9S87i8PKtRCDV1Jc6");
        addItemRequest.setTags(Set.of("Pause", "Peuschen", "Peusla"));

        itemService.addNewItem(user.getId(), addItemRequest);

        TypedQuery<Item> itemTypedQuery = entityManager.createQuery("select it from Item it where it.url = :url", Item.class);
        Item item = itemTypedQuery.setParameter("url", "https://images.app.goo.gl/9S87i8PKtRCDV1Jc6").getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getTags(), equalTo(Set.of("Pause", "Peuschen", "Peusla")));
        assertThat(item.getUrl(), equalTo("https://images.app.goo.gl/9S87i8PKtRCDV1Jc6"));
    }

    private User createUser() {
        UserDto userDto = makeUserDto("oksana@yande.ru", "oksana", "chuff");

        userService.saveUser(userDto);

        TypedQuery<User> userQuery = entityManager.createQuery("select u from User u where u.email = :email",
                User.class);
        User user = userQuery.setParameter("email", userDto.getEmail()).getSingleResult();
        return user;
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