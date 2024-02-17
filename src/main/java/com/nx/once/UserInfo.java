package com.nx.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author nx
 */
@Data
public class UserInfo {

    /**
     * 登录账号
     */
    @ExcelProperty("成员编号")
    private String userAccount;
    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;
}
