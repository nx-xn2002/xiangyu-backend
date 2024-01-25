package com.nx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

/**
 * @author 18702
 */
@SpringBootApplication
@MapperScan("com.nx.mapper")
public class XiangYuBackend {

    public static void main(String[] args) {
        SpringApplication.run(XiangYuBackend.class, args);
    }

}
