package com.liuxi.rabbitmqdemo.consumer;

import com.alibaba.fastjson.JSONObject;
import com.liuxi.rabbitmqdemo.config.RabbitMqConfig;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liuxi
 * @Date: 2019/5/17 19:33
 * @Description:
 */
public interface BaseRabbitMqConsumer {

    public static Logger baseRabbitMqConsumerLogger = LoggerFactory.getLogger(BaseRabbitMqConsumer.class);

    /**
     * 获取重试次数
     * @param header
     * @return
     */
    public default long getRetryTimes(Map<String,Object> header){

        long retryCount = 0L;

        Object retryTimes = header.get("retryTimes");

        if(retryTimes != null){
            return Long.valueOf(retryTimes.toString());
        }

        return retryCount;
    }

    @RabbitHandler
    public default void processMessage(@Payload Message message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel){

        String messageStr = String.valueOf(message.getBody());

        try {

            baseRabbitMqConsumerLogger.info("baseRabbitMqConsumerLogger consumer message : {}",message);


            process(messageStr);

        }catch (Exception e){

            MessageProperties messageProperties = message.getMessageProperties();

            Map<String,Object> headers = messageProperties.getHeaders();

            long retryTimes = getRetryTimes(headers);

            if(retryTimes > 3){


                baseRabbitMqConsumerLogger.info("重试超过3次");

                try {
                    channel.basicNack(deliveryTag, false, false);
                } catch (IOException e1) {
                    baseRabbitMqConsumerLogger.error("重试超过3次,确认不重放队列失败：{}",e1);
                }

            }else{
                headers.put("retryTimes" , retryTimes + 1);

                AMQP.BasicProperties basicProperties = new DefaultMessagePropertiesConverter().fromMessageProperties(messageProperties,"UTF-8");

                basicProperties.builder().headers(headers);

                try {
                    channel.basicNack(deliveryTag, false, false);

                    channel.basicPublish(messageProperties.getReceivedExchange(),messageProperties.getReceivedRoutingKey(),basicProperties,message.getBody());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }



        }

    }

    /*@RabbitHandler
    public default void processMessage(@Payload String message, @Headers Map<String,Object> headers,Channel channel){

        try {

            baseRabbitMqConsumerLogger.info("baseRabbitMqConsumerLogger consumer message : {}",message);

            process(message);

        }catch (Exception e){

            long retryTimes = getRetryCount(headers);

            if(retryTimes > 3) {

                baseRabbitMqConsumerLogger.info("消费失败3次: message = {}",message);
                try {

                    Map<String, Object> failHeaders = new HashMap<>();
                    failHeaders.put("exchange", "1111");
                    failHeaders.put("queue", "2222");

                    AMQP.BasicProperties failbasicProperties = new AMQP.BasicProperties().builder()
                            .deliveryMode(2) // 传送方式
                            .contentEncoding("UTF-8") // 编码方式
                            .expiration("10000") // 过期时间
                            .headers(failHeaders).build(); //自定义属性

                    channel.basicPublish(RabbitMqConfig.CONSUMER_FAIL_EXCHANGE, RabbitMqConfig.CONSUMER_FAIL_QUEUE, failbasicProperties, message.getBytes());
                } catch (IOException e1) {
                    baseRabbitMqConsumerLogger.error("消费失败3次, 发送失败消息队列 error: message = {}, e = {}", message, e);
                }
            }else{

                Map<String,Object> reHeaders = new HashMap<String,Object>();

                reHeaders.putAll(headers);
                try {
                    reHeaders.put("retryTimes",retryTimes +1);
                }catch (Exception ex){
                    baseRabbitMqConsumerLogger.error("ex : {}",ex);
                }


                //AMQP.BasicProperties rebasicProperties = buildBasicProperties(retryTimes);
                AMQP.BasicProperties rebasicProperties = new AMQP.BasicProperties();

                try {
                    channel.basicPublish(headers.get("amqp_receivedExchange").toString(), headers.get("amqp_receivedRoutingKey").toString(),rebasicProperties,message.getBytes());
                } catch (Exception e1) {
                    baseRabbitMqConsumerLogger.error("重试消息发送失败：message = {} , e = {}",message,e1);
                }
            }
        }

    }*/

  /*  public default AMQP.BasicProperties buildBasicProperties(long retryTimes){
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().build();

        basicProperties.getHeaders().put("retryTimes", retryTimes);
        return basicProperties;
    }*/

    public void process(String messsage);
}
