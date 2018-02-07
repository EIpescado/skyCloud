package org.skyCloud.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by yq on 2017/08/31 10:10.
 * 输入通道
 */
public interface SimpleSink {

    //消费-系统输入通道
    String SIMPLECONSUMER_SYSTEM_INPUT = "simpleConsumer_system_input";

    /**
     * 不指定名字 默认使用方法名
     */
    @Input(value = SIMPLECONSUMER_SYSTEM_INPUT)
    SubscribableChannel simpleConsumer_system_input();
}
