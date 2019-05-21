package com.liuxi.rabbitmqdemo.Controller;

import com.liuxi.rabbitmqdemo.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: liuxi
 * @Date: 2019/5/17 17:07
 * @Description:
 */
@RestController
@RequestMapping("rabbit")
public class RabbitController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("hello")
    public String hello(){

        rabbitTemplate.convertAndSend(RabbitMqConfig.RABBITMQEXCHANGE, RabbitMqConfig.RABBITMQQUEUE, "hello world");

        return  "hello";

    }

}
