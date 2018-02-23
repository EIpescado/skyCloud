package org.skyCloud.fallBack;

import com.alibaba.fastjson.JSON;
import org.skyCloud.utils.BackResult;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author yq
 * @date 2018/02/23 10:52
 * @description 系统服务回退
 * @since V1.0.0
 */
@Component
public class SystemFallbackProvider implements ZuulFallbackProvider {
    /**服务名称*/
    private static final String SERVICE_NAME= "系统";

    private static final String SERVICE_NOT_AVAILABLE= "服务暂不可用,稍后重试";

    private static final String SERVICE_NOT_AVAILABLE_JSON = JSON.toJSONString(BackResult.failureBack(SERVICE_NAME + SERVICE_NOT_AVAILABLE));

    /**
     * 为哪个微服务提供回退操作
     */
    @Override
    public String getRoute() {
        return "system";
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
       return new ClientHttpResponse(){

           /**
            * 返回头
            */
           @Override
           public HttpHeaders getHeaders() {
               HttpHeaders headers = new HttpHeaders();
               MediaType mt = new MediaType("application","json", Charset.forName("UTF-8"));
               headers.setContentType(mt);
               return headers;
           }

           /**
            * 响应主体
            */
           @Override
           public InputStream getBody() throws IOException {
               return new ByteArrayInputStream(SERVICE_NOT_AVAILABLE_JSON.getBytes(Charset.forName("UTF-8")));
           }

           /**
            * 状态码
            */
           @Override
           public HttpStatus getStatusCode() throws IOException {
               return HttpStatus.OK;
           }

           /**
            * 状态码数字值
            */
           @Override
           public int getRawStatusCode() throws IOException {
               return getStatusCode().value();
           }

           /**
            * 状态原因短句
            */
           @Override
           public String getStatusText() throws IOException {
               return getStatusCode().getReasonPhrase();
           }

           @Override
           public void close() {

           }
       };
    }
}
