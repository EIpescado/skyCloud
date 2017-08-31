package org.skyCloud;

import org.skyCloud.stream.SimpleSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Created by yq on 2017年8月9日 10:28:54
 * 定时任务
 */
@Component
@EnableScheduling
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private SimpleSource simpleSource;

    //每0.1s触发一次
    @Scheduled(fixedRate = 30000)
    public void processEvent() {
        logger.info("定时 发送消息..");
        simpleSource.simpleOutput().send(
                MessageBuilder.withPayload("{\"code\":\"1\",\"msg\":\"成功\",\"data\":[{\"code\":\"\",\"name\":\"全部\",\"tote\":74953,\"documentStatusId\":\"\"}]}")
                        .build());
    }

}
