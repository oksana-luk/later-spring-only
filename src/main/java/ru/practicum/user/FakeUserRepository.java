package ru.practicum.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class FakeUserRepository implements UserRepository {
    private static final List<User> FAKE_USER = createManyFakeUsers(3);
    
    @Override
    public List<User> findAll() {
        return FAKE_USER;
    }

    @Override
    public User save(User user) {
        throw new UnsupportedOperationException("Метод save() ещё не готов");
    }

    private static List<User> createManyFakeUsers(int count) {
        List<User>  fakeUsers = new ArrayList<>();
        for (long i = 1; i <= count; i++) {
            fakeUsers.add(createFakeUser(i));
        }
        return Collections.unmodifiableList(fakeUsers);
    }

    private static User createFakeUser(long id) {
        User fakeUser = new User();
        fakeUser.setId(id);
        fakeUser.setEmail("mail" + id + "@example.com");
        fakeUser.setName("Akakiy Akakievich #" + id);
        return fakeUser;
    }
}
