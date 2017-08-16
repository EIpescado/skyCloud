package org.skyCloud.service;

import org.skyCloud.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/**
 * Created by yq on 2017/08/15 17:51.
 * 国际化
 */
@Service
public class MessageService {

    private static Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String code,Object[] params){
        String msg = "";
        if(StringUtils.isNotEmpty(code)){
            try {
                msg = messageSource.getMessage(code, params, LocaleContextHolder.getLocale());
            } catch(Exception e){
                logger.error("未找到编码" + code + "映射",e);
                return code;
            }
        }
        return msg;
    }

    public String getMessage(String code){
        String msg = "";
        if(StringUtils.isNotEmpty(code)){
            try {
                msg = messageSource.getMessage(code,null,LocaleContextHolder.getLocale());
            } catch(Exception e){
                logger.error("未找到编码" + code + "映射",e);
                return code;
            }
        }
        return msg;
    }

}
