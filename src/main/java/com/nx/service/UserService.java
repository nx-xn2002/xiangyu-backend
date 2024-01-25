package com.nx.service;

import com.nx.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户服务
 *
 * @author 18702
 * @description 针对表【user】的数据库操作Service
 * @createDate 2023-10-28 21:25:11
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return long 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return {@link User }
     * @author nx
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return {@link User }
     * @author nx
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return int
     * @author nx
     */
    int userLogout(HttpServletRequest request);

    /**
     * 按标签查询用户
     *
     * @param tagList
     * @return {@link List }<{@link User }>
     * @author nx
     */
    List<User> searchUsersByTags(List<String> tagList);
}
