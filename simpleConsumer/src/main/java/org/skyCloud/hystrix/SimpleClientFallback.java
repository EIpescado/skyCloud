package org.skyCloud.hystrix;

import org.skyCloud.client.SimpleClient;
import org.skyCloud.common.dataWrapper.BackResult;
import org.skyCloud.dto.UserRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.validation.Valid;


/**
 * 服务调用回调工厂 Hystrix的融断机制来避免在微服务架构中个别服务出现异常时引起的故障蔓延
 * @author yq on 2017/07/06 15:57.
 */
@Component
public class SimpleClientFallback implements SimpleClient {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public BackResult list() {
        return null;
    }

    @Override
    public BackResult register(@ModelAttribute @Valid UserRegister userRegister) {
        logger.info("系统服务异常.注册失败...");
        return null;
    }

    @Override
    public BackResult register2() {
        logger.info("系统服务异常.注册失败2...");
        return null;
    }
}
