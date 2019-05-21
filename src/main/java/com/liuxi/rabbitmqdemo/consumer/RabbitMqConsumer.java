package com.liuxi.rabbitmqdemo.consumer;

import com.liuxi.rabbitmqdemo.config.RabbitMqConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: liuxi
 * @Date: 2019/5/17 14:20
 * @Description:
 */
@Component
public class RabbitMqConsumer {

    private static Logger logger = LoggerFactory.getLogger(RabbitMqConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.RABBITMQQUEUE)
    public void processMessage(@Payload Message message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel){

        int i = 1/0;

    }


   /* @RabbitHandler
    public void process(String msg){

        try{
            logger.info("RabbitMqConsumer : {}",msg);

            int i = 1/0;
        }catch (Exception e) {
            logger.error("RabbitMqConsumer fail : {}",msg);
        }

    }*/

    /*@RabbitHandler
    public void process(@Payload String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel, AMQP.BasicProperties properties) throws Exception {

        try{
            logger.info("RabbitMqConsumer : message =  {}， deliveryTag={}", message, deliveryTag);

            int i = 1/0;
        }catch (Exception e) {
            logger.error("RabbitMqConsumer fail : {}",message);

            throw e;
            //消费者确认没消费，重新放入队列
            //channel.basicNack(deliveryTag,false,false);
        }

    }*/

    /*@RabbitHandler
    public void process(@Payload String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel, AMQP.BasicProperties properties){

        try {
            logger.info("consumer message : {}",message);

            int i = 1/0;
        }catch (Exception e){

            long retryCount = getRetryCount(properties);

            if(retryCount > 3){
                try {
                    channel.basicPublish(RabbitMqConfig.CONSUMER_FAIL_EXCHANGE, RabbitMqConfig.CONSUMER_FAIL_QUEUE, properties, message.getBytes());
                } catch (IOException e1) {
                    logger.error("消费失败3次, 发送失败消息队列失败:{}",e);
                }
            }

            throw e;
        }

    }*/


    /*@RabbitHandler
    public void process(@Payload String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {

        //消费者确认消费完成
        channel.basicAck(deliveryTag,false);

        //消费者拒绝消费
        channel.basicReject(deliveryTag, false);


        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("my1", "1111");
        headers.put("my2", "2222");
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder()
                .deliveryMode(2) // 传送方式
                .contentEncoding("UTF-8") // 编码方式
                .expiration("10000") // 过期时间
                .headers(headers) //自定义属性
                .build();
        //重新放入一个队列
        channel.basicPublish(RabbitMqConfig.CONSUMER_FAIL_EXCHANGE,RabbitMqConfig.CONSUMER_FAIL_QUEUE,true, true, basicProperties, message.getBytes());
    }*/
}
