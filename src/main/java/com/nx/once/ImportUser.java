package com.nx.once;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 导入用户
 *
 * @author nx
 */
public class ImportUser {
    public static void main(String[] args) {
        String fileName = "src/main/resources/prodExcel.xlsx";
        List<UserInfo> list = EasyExcel.read(fileName).head(UserInfo.class).sheet().doReadSync();
        System.out.println("条数：" + list.size());
        //筛选重复昵称
        Map<String, List<UserInfo>> listMap = list.stream()
                .filter(userInfo -> StringUtils.isNotEmpty(userInfo.getUsername()))
                .collect(Collectors.groupingBy(UserInfo::getUsername));
        System.out.println("不重复的昵称数:" + listMap.keySet().size());
    }
}
