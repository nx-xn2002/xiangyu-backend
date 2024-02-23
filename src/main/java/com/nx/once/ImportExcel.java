package com.nx.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;

import java.util.List;

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
        readByListener();
        synchronousRead("D:\\MyWebProject\\xiangyu-backend\\src\\main\\resources\\prodExcel.xlsx");
    }

    /**
     * 监听器读取
     *
     * @author nx
     */
    public static void readByListener() {
        String fileName = "D:\\MyWebProject\\xiangyu-backend\\src\\main\\resources\\prodExcel.xlsx";
        EasyExcel.read(fileName, UserInfo.class, new TableListener()).sheet().doRead();
    }

    /**
     * 同步读
     *
     * @param fileName 文件名
     * @author nx
     */
    public static void synchronousRead(String fileName) {
        List<UserInfo> list = EasyExcel.read(fileName).head(UserInfo.class).sheet().doReadSync();
        for (UserInfo userInfo : list) {
            System.out.println(userInfo);
        }
    }
}
