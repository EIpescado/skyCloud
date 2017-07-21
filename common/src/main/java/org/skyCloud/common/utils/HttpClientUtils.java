package org.skyCloud.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.skyCloud.common.constants.EncodeConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yq on 2017/04/05 17:16.
 * httpClient 工具类
 */
public class HttpClientUtils{

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    //连接等待时间
    private static final int CONNECT_TIMEOUT  = 5000 ;

    //请求连接超时时间
    private static final int CONNECTION_REQUEST_TIMEOUT  = 1000 ;

    //数据传输时间
    private static final int SOCKET_TIMEOUT  = 5000 ;

    private HttpClientUtils() {
    }

    /**
     * 不需要导入证书，SSL信任所有证书，使用该方法
     */
    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
                    null, new TrustStrategy() {
                        // 信任所有证书
                        public boolean isTrusted(X509Certificate[] chain,
                                                 String authType) throws CertificateException {
                            return true;
                        }
                    }).build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    /**
     * 发送get请求
     * @param url 请求地址
     * @param config 自定义请求参数配置
     * @param params 参数
     * @return
     */
    public static String sendGet(String url, RequestConfig config,Map<String, Object> params){
        CloseableHttpClient httpClient =  createSSLClientDefault();
        CloseableHttpResponse response = null;
        HttpEntity entity = null ;
        String content = "";
        try {
            // 构建请求地址 创建httpGet.
            HttpGet httpget = new HttpGet(url + HttpUtils.buildParams(params));
            //设置请求参数
            httpget.setConfig(config);
            logger.info("get请求: {}",httpget.getURI());
            // 执行get请求.
            response = httpClient.execute(httpget);
            int responseCode = response.getStatusLine().getStatusCode();
            logger.info("响应状态: {}",responseCode);
            //响应成功
            if(HttpStatus.SC_OK == responseCode){
                // 获取响应实体
                entity = response.getEntity();
                if(entity != null){
                    content  =  EntityUtils.toString(entity, EncodeConsts.UTF_8);
                }
                EntityUtils.consume(entity);
            }
            logger.info("返回正文: {}",content);
            return content;
        } catch (Exception e) {
            logger.error(e.toString());
        }finally {
            close(entity,response,httpClient);
        }
        return content;
    }

    /**
     * 发送get请求
     * @param url 请求地址
     * @param heads 自定义请求头参数配置
     * @param params 参数
     * @return
     */
    public static String sendGet(String url, Map<String,String> heads,Map<String, Object> params){
        CloseableHttpClient httpClient =  createSSLClientDefault();
        CloseableHttpResponse response = null;
        String content = "";
        HttpEntity entity = null;
        try {
            // 构建请求地址 创建httpGet.
            HttpGet httpget = new HttpGet(url + HttpUtils.buildParams(params));
            //请求头参数配置
            if(heads != null && !heads.isEmpty()){
                for(Map.Entry<String,String> entry : heads.entrySet()){
                    httpget.setHeader(entry.getKey(),entry.getValue());
                }
            }
            logger.info("get请求: {}",httpget.getURI());
            // 执行get请求.
            response = httpClient.execute(httpget);
            int responseCode = response.getStatusLine().getStatusCode();
            logger.info("响应状态: {}",responseCode);
            //响应成功
            if(HttpStatus.SC_OK == responseCode){
                // 获取响应实体
                entity = response.getEntity();
                if(entity != null){
                    content  =  EntityUtils.toString(entity, EncodeConsts.UTF_8);
                }
            }
            logger.info("返回正文: {}",content);
            return content;
        } catch (IOException e) {
            logger.error(e.toString());
        }finally {
           close(entity,response,httpClient);
        }
        return content;
    }

    private static void close( HttpEntity entity ,CloseableHttpResponse response,CloseableHttpClient httpClient){
        try {
            //关闭响应实体
            EntityUtils.consume(entity);
        } catch (IOException e) {
            logger.error(e.toString());
        }
        try {
            if(response != null){
                response.close();
            }
        } catch (IOException e) {
            logger.error(e.toString());
        }
        try {
            if(httpClient != null){
                httpClient.close();
            }
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    /**
     * 发送get请求(默认)
     * @param url 请求地址
     * @param params 参数
     */
    public static String sendGet(String url ,Map<String,Object> params){
        //自定义请求参数配置
        RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
        return  sendGet(url,config,params);
    }

    /**
     * 发送post请求(默认)
     * @param url 请求地址
     * @param params 表单参数
     */
    public static String sendPost(String url,Map<String,Object> params){
        //自定义请求参数配置
        RequestConfig config = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
       return sendPost(url,config,params);
    }

    /**
     * 发送post请求
     * @param url 请求地址
     * @param heads 请求头
     * @param params 表单参数
     */
    public static String sendPost(String url,Map<String,String> heads,Map<String,Object> params){
        // 创建默认的httpClient实例.
        CloseableHttpClient httpClient = createSSLClientDefault();
        // 创建httpPost
        HttpPost httpPost = new HttpPost(url);
        //请求头参数配置
        if(heads != null && !heads.isEmpty()){
            for(Map.Entry<String,String> entry : heads.entrySet()){
                httpPost.setHeader(entry.getKey(),entry.getValue());
            }
        }
        //设置post请求参数
        httpPost.setEntity(buildPostParams(params));
        return post(httpClient,httpPost);
    }

    /**
     * 发送post请求
     * @param url 请求地址
     * @param config 请求头配置
     * @param params 表单参数
     */
    public static String sendPost(String url,RequestConfig config,Map<String,Object> params){
        // 创建默认的httpClient实例.
        CloseableHttpClient httpClient = createSSLClientDefault();
        // 创建httpPost
        HttpPost httpPost = new HttpPost(url);
        //设置post请求参数
        httpPost.setEntity(buildPostParams(params));
        httpPost.setConfig(config);
        return post(httpClient,httpPost);
    }

    private static String post(CloseableHttpClient httpClient,HttpPost httpPost){
        CloseableHttpResponse response = null ;
        HttpEntity entity  = null;
        String content = "";
        try {
            response = httpClient.execute(httpPost);
            logger.info("响应状态: {}",response.getStatusLine().getStatusCode());

            // 获取响应实体
            entity = response.getEntity();
            if(entity != null){
                content  =  EntityUtils.toString(entity, EncodeConsts.UTF_8);
            }
            logger.info("返回正文: {}",content);
            return content;
        } catch (IOException e) {
            logger.error(e.toString());
        }finally {
            close(entity,response,httpClient);
        }
        return content ;
    }

    /**
     * 构建post提交表单参数,url编码,utf-8
     * @param params
     * @return
     */
    public static UrlEncodedFormEntity buildPostParams(Map<String,Object> params){
        UrlEncodedFormEntity uefEntity = null ;
        // 创建参数队列
        List<NameValuePair> formParams = new ArrayList<>();
        if(params != null && !params.isEmpty()){
            for(Map.Entry<String,Object> entry : params.entrySet()){
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
//            params.forEach((k,v)->formParams.add(new BasicNameValuePair(k, v.toString())));
        }
        try {
            uefEntity = new UrlEncodedFormEntity(formParams, EncodeConsts.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  uefEntity ;
    }

    /**
     * 上传文件
     * @param url 上传地址
     * @param params 文件别名:file 其他参数
     */
    public static String upload(String url,Map<String,Object> params){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if(params != null && !params.isEmpty()){
            for(Map.Entry<String,Object> entry : params.entrySet()){
                if(entry.getValue() instanceof File){
                    builder.addPart(entry.getKey(),new FileBody((File)entry.getValue()));
                }else {
                    builder.addTextBody(entry.getKey(),entry.getValue().toString());
                }
            }
        }
        try {
            builder.setCharset(CharsetUtils.get(EncodeConsts.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.setEntity(builder.build());
        return post(httpClient,httpPost);
    }

    public static void downLoad(String url, Map<String,Object> params, OutputStream outputStream){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url + HttpUtils.buildParams(params));
        CloseableHttpResponse response = null;
        HttpEntity entity = null ;
        InputStream inputStream ;
        try {
            response = httpClient.execute(httpGet);
            logger.info("响应状态: {}",response.getStatusLine().getStatusCode());
            entity = response.getEntity();
            if(entity != null){
                inputStream = entity.getContent();
                FileUtil.copyStream(inputStream,outputStream);
            }else {
                logger.info("文件不存在!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(entity,response,httpClient);
        }
    }


    public static void main(String[] args) throws Exception {
/*        Map<String,Object> params = new HashMap<>();
        params.put("file",new File("D:/A/1.xls"));
        upload("http://localhost:8080/spring-boot-demo/fileUpload",params);*/
        downLoad("http://localhost:8080/spring-boot-demo/fileDownload/2c933b815b604bde015b6052d4e10000",null,new FileOutputStream("D:/A/3.xls"));
    }
}
