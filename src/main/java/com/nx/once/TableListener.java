package com.nx.once;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TableListener implements ReadListener<UserInfo> {
    @Override
    public void invoke(UserInfo data, AnalysisContext context) {
        System.out.println(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("解析完成");
    }
}