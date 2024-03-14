package com.nx.model.dto;

import com.nx.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * 队伍查询封装类
 *
 * @author nx
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    @Serial
    private static final long serialVersionUID = 4311288095002581904L;
    /**
     * id
     */
    private Long id;
    /**
     * id list
     */
    private List<Long> idList;
    /**
     * 搜索关键词
     */
    private String searchText;

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
