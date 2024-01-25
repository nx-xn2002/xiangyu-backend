package com.nx.content;

/**
 * 用户常量
 *
 * @author nx
 */
public interface UserContent {
    /**
     * 用户登录态
     */
    String USER_LOGIN_STATE = "user login";
    /**
     * 普通用户
     */
    int DEFAULT_ROLE = 0;
    /**
     * 默认头像
     */
    String DEFAULT_AVATAR = "/images/default_avatar.png";
    /**
     * 管理员用户
     */
    int ADMIN_ROLE = 1;
}
