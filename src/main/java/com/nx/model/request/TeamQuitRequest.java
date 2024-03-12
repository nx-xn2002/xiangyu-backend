package com.nx.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 退出队伍请求封装类
 *
 * @author nx
 */
@Data
public class TeamQuitRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7759833470210061267L;
    /**
     * id
     */
    private Long teamId;
}
