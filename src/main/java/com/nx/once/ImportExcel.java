package com.nx.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;

/**
 * 导入excel数据
 *
 * @author nx
 */
public class ImportExcel {
    /**
     * 读取数据
     *
     * @author nx
     */
    public static void main(String[] args) {
        String fileName = "D:\\MyWebProject\\xiangyu-backend\\src\\main\\resources\\testExcel.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, UserInfo.class, new TableListener()).sheet().doRead();
    }
}
