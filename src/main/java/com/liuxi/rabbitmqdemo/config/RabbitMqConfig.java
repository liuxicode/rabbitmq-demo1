package com.liuxi.rabbitmqdemo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Auther: liuxi
 * @Date: 2019/5/17 14:28
 * @Description:
 */
@Component
public class RabbitMqConfig {

    public static final String RABBITMQEXCHANGE = "rabbitmqexchange1";
    public static final String RABBITMQQUEUE = "rabbitmqqueue1";

    public static final String CONSUMER_FAIL_EXCHANGE = "consumer_fail_exchange1";//消费失败 exhange
    public static final String CONSUMER_FAIL_QUEUE = "consumer_fail_queue1";//消费失败 queue

    @Bean
    public FanoutExchange rabbitMQQueueExchange(){
        return new FanoutExchange(RABBITMQEXCHANGE);
    }

    @Bean
    public Queue rabbitMQQueue(){
        return new Queue(RABBITMQQUEUE);
    }

    @Bean
    public Binding bindingQueueToExchange(){
        return BindingBuilder.bind(rabbitMQQueue()).to(rabbitMQQueueExchange());
    }

    @Bean
    public DirectExchange consumerFailExchange(){
        return new DirectExchange(CONSUMER_FAIL_EXCHANGE);
    }

    @Bean
    public Queue consumerFailQueue(){
        return new Queue(CONSUMER_FAIL_QUEUE);
    }

    @Bean
    public Binding bindingQueueToConsumerFailExchange(){
        return BindingBuilder.bind(consumerFailQueue()).to(consumerFailExchange()).with(CONSUMER_FAIL_QUEUE);
    }

}
