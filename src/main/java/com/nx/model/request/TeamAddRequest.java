package com.nx.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 添加队伍请求类
 *
 * @author nx
 */
@Data
public class TeamAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 7342675829990183638L;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 队伍状态 0-公开 1-私有 2-加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}
