package com.wpcc.userorderservice.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldInjectRepositoryAndManageUsers() {
        User createdUser = userService.createUser("alice");

        assertEquals("alice", createdUser.username());
        assertEquals(createdUser, userService.findUserById(createdUser.id()).orElseThrow());
    }
}
