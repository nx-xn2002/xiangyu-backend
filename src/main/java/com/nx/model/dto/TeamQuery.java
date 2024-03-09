package com.nx.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.nx.common.PageRequest;

import java.io.Serial;
import java.util.Date;

/**
 * 队伍查询封装类
 *
 * @author nx
 */
public class TeamQuery extends PageRequest {
    @Serial
    private static final long serialVersionUID = 4311288095002581904L;
    /**
     * id
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
     * 用户id
     */
    private Long userId;

    /**
     * 队伍状态 0-公开 1-私有 2-加密
     */
    private Integer status;
}
