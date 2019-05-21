package com.liuxi.rabbitmqdemo.consumer;

import com.liuxi.rabbitmqdemo.config.RabbitMqConfig;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;

/**
 * @Auther: liuxi
 * @Date: 2019/5/17 14:20
 * @Description:
 */
@RabbitListener(queues = RabbitMqConfig.CONSUMER_FAIL_QUEUE)
public class RabbitMqConsumerFail {

    private static Logger logger = LoggerFactory.getLogger(RabbitMqConsumerFail.class);

    @RabbitHandler
    public void process(String msg){

    }

    @RabbitHandler
    public void process(@Payload String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {

        logger.info("RabbitMqConsumerFail consumer : " + message);
    }
}
