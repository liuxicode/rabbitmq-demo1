package com.liuxi.rabbitmqdemo.aop;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @Auther: liuxi
 * @Date: 2018/8/28 12:38
 * @Description: 全局日志切面，用于答应请求方法和类名及参数，和最终方法请求响应时间
 */
@Component
@Aspect
public class RabbitMqConsumerAspect {

    private final static Logger baseRabbitMqConsumerLogger = LoggerFactory.getLogger(RabbitMqConsumerAspect.class);

    @Pointcut("@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
    public void controllerAspect(){


    }

    @Around("controllerAspect()")
    public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {

        try {

            Object object = joinPoint.proceed();

            return object;

        }catch (Exception e){
            Object[] argValues = joinPoint.getArgs();

            Message message = null;

            Channel channel = null;

            for (Object argValue : argValues) {

                if(argValue instanceof Message){

                    message = (Message) argValue;
                }

                if(argValue instanceof Channel){

                    channel = (Channel) argValue;
                }

            }

            String messageStr = String.valueOf(message.getBody());

            MessageProperties messageProperties = message.getMessageProperties();

            Map<String,Object> headers = messageProperties.getHeaders();

            long retryTimes = getRetryTimes(headers);

            if(retryTimes > 3){

                baseRabbitMqConsumerLogger.info("重试超过3次");

                try {
                    channel.basicNack(messageProperties.getDeliveryTag(), false, false);
                } catch (IOException ex) {
                    baseRabbitMqConsumerLogger.error("重试超过3次,确认不重放队列失败：message = {} , ex = {}", message, ex);
                }

            }else{
                headers.put("retryTimes" , retryTimes + 1);

                AMQP.BasicProperties basicProperties = new DefaultMessagePropertiesConverter().fromMessageProperties(messageProperties,"UTF-8");

                basicProperties.builder().headers(headers);

                try {
                    channel.basicNack(messageProperties.getDeliveryTag(), false, false);

                    channel.basicPublish(messageProperties.getReceivedExchange(),messageProperties.getReceivedRoutingKey(),basicProperties,message.getBody());
                } catch (IOException ex) {
                    baseRabbitMqConsumerLogger.error("确认消费异常,重放消息队列失败: messgae = {} , ex = {}",messageStr,ex);
                }
            }

        }

        return null;

    }


    /**
     * 获取重试次数
     * @param header
     * @return
     */
    public long getRetryTimes(Map<String,Object> header){

        long retryCount = 0L;

        Object retryTimes = header.get("retryTimes");

        if(retryTimes != null){
            return Long.valueOf(retryTimes.toString());
        }

        return retryCount;
    }

}
