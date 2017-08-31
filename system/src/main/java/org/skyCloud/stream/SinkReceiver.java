package org.skyCloud.stream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;

/**
 * Created by yq on 2017/08/30 15:04.
 * 消息消费者
 */
@EnableBinding({SystemSink.class,SystemSource.class})
public class SinkReceiver {

    private final Logger logger = LoggerFactory.getLogger(SinkReceiver.class);

    //对SYSTEM_INPUT监听,接收到消息后进行加工,将处理后的结果以消息形式发送到SYSTEM_OUTPUT通道
    @StreamListener(SystemSink.SYSTEM_INPUT)
    @SendTo(SystemSource.SYSTEM_OUTPUT)
    public Object receive(Message<Object> message){
        JSONObject json = JSON.parseObject(message.getPayload().toString());
        logger.info("接收消息 {},开始加工",json.toJSONString());
        json.put("addNew","新添加");
        return json;
    }

    //效果同上
//    @ServiceActivator(inputChannel = SystemSink.SYSTEM_INPUT,outputChannel = SystemSource.SYSTEM_OUTPUT)
//    public Object receiveFromInput(Object payload){
//        JSONObject json = JSON.parseObject(payload.toString());
//        logger.info("接收消息 {},开始加工",json.toJSONString());
//        json.put("addNew","新添加2");
//        return json;
//    }
}
