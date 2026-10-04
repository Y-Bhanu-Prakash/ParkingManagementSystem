package com.parking.repository;

import com.parking.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {

    private final InMemoryDataStore dataStore;

    public UserRepository(InMemoryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public User save(User user) {
        dataStore.getUsers().put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        Object user = dataStore.getUsers().get(id);

        if (user instanceof User) {
            return Optional.of((User) user);
        }

        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        return dataStore.getUsers()
                .values()
                .stream()
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public List<User> findAll() {
        return dataStore.getUsers()
                .values()
                .stream()
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .toList();
    }

    public User update(User user) {
        dataStore.getUsers().put(user.getId(), user);
        return user;
    }

    public Optional<User> setActive(Long id, boolean active) {
        Optional<User> userOptional = findById(id);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        user.setActive(active);
        dataStore.getUsers().put(id, user);

        return Optional.of(user);
    }

    public Optional<User> activate(Long id) {
        return setActive(id, true);
    }

    public Optional<User> deactivate(Long id) {
        return setActive(id, false);
    }
}