package com.nx.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用分页请求类
 *
 * @author nx
 */
@Data
public class PageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 5701968045047841012L;
    /**
     * 页面大小
     */
    protected int pageSize = 10;
    /**
     * 当前页码
     */
    protected int pageNum = 1;
}
