package org.skyCloud.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yq on 2017/04/01 16:40.
 * ip工具类
 */
public class IpUtils {

    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "X-Real-IP"};

    //未知ip
    private static final String UN_KNOWN = "unknown";

    private IpUtils() {
    }

    /**
     * 根据请求获取用户ip 取第一个非unknown的ip,穿透代理
     * @param request 请求
     */
    public static String getIp(HttpServletRequest request){
        String ip = "";
        for(String head:HEADERS_TO_TRY){
            ip = request.getHeader(head);
            if(StringUtils.isNotEmpty(ip) &&  !UN_KNOWN.equalsIgnoreCase(ip)){
                break;
            }
        }
        return StringUtils.isEmpty(ip) ?  request.getRemoteAddr() : ip;
    }
}
