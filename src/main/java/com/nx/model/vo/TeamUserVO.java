package com.nx.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍用户信息封装类（脱敏）
 *
 * @author nx
 */
@Data
public class TeamUserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1936735903807617751L;
    /**
     * 队伍id
     */
    private Long id;
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人用户信息
     */
    private UserVO createUser;

    /**
     * 是否已加入队伍
     */
    private boolean hasJoin;

    /**
     * 用户加入数
     */
    private Integer hasJoinNum;
}
