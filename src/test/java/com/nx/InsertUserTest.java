package com.nx;

import com.nx.mapper.UserMapper;
import com.nx.model.domain.User;
import com.nx.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class InsertUserTest {
    //    @Resource
//    private UserMapper userMapper;
    @Resource
    private UserService userService;
    private ExecutorService executorService = new ThreadPoolExecutor(60, 100, 100000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));


    /**
     * 批量插入用户
     *
     * @author nx
     */
//    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int insertNum = 100000;
        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<User> userList = new ArrayList<>();
            do {
                j++;
                User user = new User();
                user.setUserAccount("fake_nixiang");
                user.setUsername("假用户");
                user.setAvatarUrl("logo.svg");
                user.setGender(0);
                user.setProfile("假用户的自我介绍");
                user.setUserPassword("12345678");
                user.setPhone("1234567890");
                user.setEmail("1234567890");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setTags("[]");
                userList.add(user);
            } while (j % batchSize != 0);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
