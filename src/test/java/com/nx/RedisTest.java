package com.nx;

import com.nx.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

//    @Test
    void test() {
        ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
        //设置值
        valueOperations.set("nixiangString", "nx");
        valueOperations.set("nixiangInt", 666);
        User user = new User();
        user.setId(1L);
        user.setUsername("nixiang");
        valueOperations.set("nixiangUser", user);
        //获取值并断言
        Assertions.assertEquals("nx", (String) valueOperations.get("nixiangString"));
        Assertions.assertEquals(666, (int) valueOperations.get("nixiangInt"));
        Assertions.assertEquals(user, (User) valueOperations.get("nixiangUser"));
    }
}
