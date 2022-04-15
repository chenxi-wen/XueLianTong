package com.Zhengqing;

import com.Zhengqing.dao.UserDOMapper;
import com.Zhengqing.dataobject.UserDO;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello world!
 *
 */

@SpringBootApplication(scanBasePackages = {"com.Zhengqing"})
//使用@SpringbootApplication注解  可以解决根类或者配置类头上注解过多的问题，一个@SpringbootApplication相当于
//@Configuration,@EnableAutoConfiguration和 @ComponentScan 并具有他们的默认属性值
@RestController
@Repository
@MapperScan("com.Zhengqing.dao")
public class App {

    @Autowired
    private UserDOMapper userDOMapper;

    @RequestMapping("/")
    public String home() {

        UserDO userDO = userDOMapper.selectByPrimaryKey(1);
        if (userDO == null) {
            return "用户对象不存在";
        } else {
            return userDO.getName();
        }
    }
    public static void main( String[] args )
    {

        System.out.println( "Hello World!" );
        SpringApplication.run(App.class,args);
    }
}

