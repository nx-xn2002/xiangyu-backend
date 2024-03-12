package com.nx.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 删除队伍请求类
 *
 * @author nx
 */
@Data
public class TeamDeleteRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 7342675829990183638L;
    /**
     * id
     */
    private Long id;
}
