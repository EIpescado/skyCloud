package org.skyCloud.common.base;

import org.springframework.beans.factory.annotation.Value;

import javax.persistence.MappedSuperclass;

/**
 * Created by yq on 2017/08/15 18:08.
 * baseController
 */
@MappedSuperclass
public class BaseController {

    @Value("${token.secret}")
    protected String tokenSecret;  //token秘钥
    @Value("${token.expire.time}")
    protected long tokenExpireTime;  //token有效时间
}
