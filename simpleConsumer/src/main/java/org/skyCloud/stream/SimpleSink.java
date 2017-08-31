package org.skyCloud.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by yq on 2017/08/31 10:10.
 */
public interface SimpleSink {

    String SIMPLE_INPUT = "simpleInput";

    /**
     * 不指定名字 默认使用方法名
     */
    @Input(value = SIMPLE_INPUT)
    SubscribableChannel simpleInput();
}
