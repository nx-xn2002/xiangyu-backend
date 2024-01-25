package com.nx.content;

public interface RegisterContent {
    /**
     * 用户名已存在
     */
    long USER_ACCOUNT_EXIST = -1;
    /**
     * 用户名格式错误
     */
    long WRONG_ACCOUNT_FORMAT = -2;
    /**
     * 服务器异常
     */
    long SEVER_ERROR = -3;
    /**
     * 密码长度错误
     */
    long WRONG_PASSWORD_LENGTH = -4;
    /**
     * 用户名长度错误
     */
    long WRONG_ACCOUNT_LENGTH = -5;
    /**
     * 其他异常
     */
    long OTHER_ERROR = -6;
}
