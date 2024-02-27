package com.nx.service;

import com.nx.model.domain.User;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    public void testSearchUsersByTags() {
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> users = userService.searchUsersByTags(tagNameList);
        for (User user : users) {
            System.out.println(user);
        }
    }
}