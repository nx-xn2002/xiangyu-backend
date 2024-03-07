package com.nx.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nx.mapper.UserMapper;
import com.nx.model.domain.User;
import com.nx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热
 *
 * @author nx
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RedissonClient redissonClient;
    /**
     * 重点用户
     */
    private List<Long> mainUserList = Arrays.asList(1L, 2L, 3L, 4L, 5L);

    /**
     * 推荐页预热，每天执行
     * cron表达式： 秒 分 时 日 月 年
     *
     * @author nx
     */
    @Scheduled(cron = "0 43 0 * * *")
    public void doCacheRecommendUser() {
        RLock lock = redissonClient.getLock("nx:precachejob:docache:lock");
        try {
            if (lock.tryLock(0, 30000L, TimeUnit.MILLISECONDS)) {
                System.out.println("get lock");
                for (Long userId : mainUserList) {
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    String redisKey = String.format("xiangyu:user:recommend:%s", userId);
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    //写入缓存
                    try {
                        valueOperations.set(redisKey, userPage, 23, TimeUnit.HOURS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("InterruptedException e", e);
        } finally {
            //避免释放其他服务器加的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unlock");
                lock.unlock();
            }
        }
    }
}
