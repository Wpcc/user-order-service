package com.wpcc.userorderservice.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldInjectRepositoryAndReturnUsername() {
        assertEquals("user-1001", userService.getUsernameById(1001));
    }
}
