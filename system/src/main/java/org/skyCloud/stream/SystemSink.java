package org.skyCloud.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by yq on 2017/08/30 17:58.
 * 定义输入通道 输出通道返回SubscribableChannel
 */
public interface SystemSink {

    String SYSTEM_INPUT = "systemInput";

    /**
     * 不指定名字 默认使用方法名
     */
    @Input(SYSTEM_INPUT)
    SubscribableChannel systemInput();
}
