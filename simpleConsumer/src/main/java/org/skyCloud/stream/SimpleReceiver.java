package org.skyCloud.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

/**
 * Created by yq on 2017/08/30 15:04.
 * 消息消费者
 */
@EnableBinding({SimpleSource.class,SimpleSink.class})
public class SimpleReceiver {

    private final Logger logger = LoggerFactory.getLogger(SimpleReceiver.class);

    //对SIMPLE_INPUT监听
    @StreamListener(SimpleSink.SIMPLE_INPUT)
    public void receive(Message<Object> message){
        logger.info("SimpleReceiver 测试接收消息 {}",message.getPayload().toString());
    }
}
