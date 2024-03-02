package com.nx.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nx.mapper.UserMapper;
import com.nx.model.domain.User;
import com.nx.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
    @Scheduled(cron = "0 58 23 * * *")
    public void doCacheRecommendUser() {
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
}
