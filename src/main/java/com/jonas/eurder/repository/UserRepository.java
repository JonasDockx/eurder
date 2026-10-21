package com.jonas.eurder.repository;

import com.jonas.eurder.domain.Role;
import com.jonas.eurder.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {
    private final ConcurrentHashMap<String, User> users;

    public UserRepository() {
        this.users = new ConcurrentHashMap<>();
        save(new User("admin@eurder.com", "admin", Role.ADMIN, null));
    }

    public Optional<User> getByEmail(String email) {
        return Optional.ofNullable(users.get(email));
    }

    public void save(User user) {
        users.put(user.getEmail(), user);
    }
}
