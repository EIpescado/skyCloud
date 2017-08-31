package org.skyCloud.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Created by yq on 2017/08/30 17:58.
 * 定义输出通道 输入通道返回SubscribableChannel
 */
public interface SimpleSource {

    String SIMPLE_OUTPUT = "simpleOutput";

    /**
     * 不指定名字 默认使用方法名
     */
    @Output(value = SIMPLE_OUTPUT)
    MessageChannel simpleOutput();
}
