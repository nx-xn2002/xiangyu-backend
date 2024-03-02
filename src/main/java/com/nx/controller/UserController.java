package com.nx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nx.common.BaseResponse;
import com.nx.common.ErrorCode;
import com.nx.common.ResultUtils;
import com.nx.exception.BusinessException;
import com.nx.model.domain.User;
import com.nx.model.domain.request.UserLoginRequest;
import com.nx.model.domain.request.UserRegisterRequest;
import com.nx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.nx.content.UserContent.ADMIN_ROLE;
import static com.nx.content.UserContent.USER_LOGIN_STATE;

/**
 * @author nx
 */
@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long res = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(res);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        if (user != null) {
            return ResultUtils.success(user);
        } else {
            return ResultUtils.error(null);
        }
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            return ResultUtils.error(null);
        }
        return ResultUtils.success(userService.userLogout(request));
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        //鉴权，仅管理员可查询
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        return ResultUtils.success(userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList()));
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        //如果有缓存，则直接读取缓存
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String redisKey = String.format("xiangyu:user:recommend:%s", loginUser.getId());
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }
        //否则，查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        //写入缓存
        try {
            valueOperations.set(redisKey, userPage, 600, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        //返回结果
        return ResultUtils.success(userPage);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        //TODO:校验用户是否合法
        User user = userService.getById(userId);
        return ResultUtils.success(user);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody User user, HttpServletRequest request) {
        //1.校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.校验权限
        User loginUser = userService.getLoginUser(request);
        //3.触发更新
        Boolean result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }


    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody Long id, HttpServletRequest request) {
        //鉴权，仅管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //尝试逻辑删除
        return userService.removeById(id);
    }


}
