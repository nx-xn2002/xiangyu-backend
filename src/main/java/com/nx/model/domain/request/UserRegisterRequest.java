package com.nx.model.domain.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author nx
 */
@Data
public class UserRegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1650046433749698189L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
