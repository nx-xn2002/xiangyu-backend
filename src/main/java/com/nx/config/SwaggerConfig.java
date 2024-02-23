package com.nx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * swagger配置类
 *
 * @author nx
 */
@Configuration
@EnableSwagger2WebMvc
@Profile("dev")
public class SwaggerConfig {
    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //标注控制器位置
                .apis(RequestHandlerSelectors.basePackage("com.nx.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 用于定义API主界面的信息，比如可以声明所有的API的总标题、描述、版本
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //  可以用来自定义API的主标题
                .title("响遇 XIANGYU - 学习伙伴匹配系统")
                // 可以用来描述整体的API
                .description("学习伙伴匹配系统-接口文档")
                // 用于定义服务的域名
                .termsOfServiceUrl("https://github.com/nx-xn2002")
                //联系方式
                .contact(new Contact("nx", "https://github.com/nx-xn2002", "nixiang_2002@163.com"))
                // 可以用来定义版本
                .version("1.0")
                .build();
    }
}