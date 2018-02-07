package org.skyCloud.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Created by yq on 2017/08/30 17:58.
 * 定义输出通道 输入通道返回SubscribableChannel
 */
public interface SimpleSource {

    //消费-系统输出通道
    String SIMPLECONSUMER_SYSTEM_OUTPUT = "simpleConsumer_system_output";

    @Output(value = SIMPLECONSUMER_SYSTEM_OUTPUT)
    MessageChannel simpleConsumer_system_output();
}
