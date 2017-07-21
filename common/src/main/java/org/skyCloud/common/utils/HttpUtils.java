package org.skyCloud.common.utils;

import org.skyCloud.common.constants.EncodeConsts;
import org.skyCloud.common.constants.ExceptionConsts;
import org.skyCloud.common.exception.SkyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Map;

/**
 * Created by yq on 2017/02/06 11:05.
 */
public class HttpUtils {

    private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    //连接超时时间
    private static int CONNECT_OUT_TIME = 15000 ;

    //读入超时时间
    private static int READ_OUT_TIME = 15000 ;

    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    public static final char QP_SEP_A = '&';

    public static final char QP_SEP_Q = '?';

    public static final String NAME_VALUE_SEPARATOR = "=";


    private HttpUtils() {
    }

    //证书认证
    private static TrustManager x509TrustManager = new X509TrustManager(){
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(//不需要对客户端进行认证
                java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException {
        }

        @Override
        public void checkServerTrusted(//信任任何证书
                java.security.cert.X509Certificate[] chain, String authType)
                throws java.security.cert.CertificateException {
        }
    };

    private static TrustManager []  trustManagerArray = new TrustManager [] {x509TrustManager};

    /**
     * 发送get请求 ,支持Https
     * @param urlString
     * @param isHttps
     * @return
     * @throws Exception
     */
    public static String sendGet(String urlString,boolean isHttps) throws Exception {
        BufferedReader bufferedReader = null;
        InputStream inputStream = null;
        String line;
        StringBuffer buffer = null;
        try {
            URL url = new URL(urlString);
            // 打开和URL之间的连接
            HttpURLConnection con =isHttps ? (HttpsURLConnection) url.openConnection() : (HttpURLConnection) url.openConnection() ;
            // 设置通用的请求属性
            con.setRequestProperty("accept", "*/*");
            con.setRequestProperty("connection", "Keep-Alive");
            con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            con.setConnectTimeout(CONNECT_OUT_TIME);
            con.setReadTimeout(READ_OUT_TIME);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", CONTENT_TYPE);
            if (isHttps) {
                initSSLContext((HttpsURLConnection)con);
            }
            // 建立实际的连接
            con.connect();
            //获取返回http连接状态码
            int responseCode = con.getResponseCode();
            logger.info("返回http连接状态码: {}",responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = con.getInputStream();
            } else {
                //如果连接失败但服务器仍然发送了有用数据，则返回错误流。
                inputStream = con.getErrorStream();
            }
            // 定义 BufferedReader输入流来读取URL的响应
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, EncodeConsts.UTF_8));
            buffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            throw new SkyException(ExceptionConsts.SERVER_REQUEST_FAILURE_CODE,ExceptionConsts.SERVER_REQUEST_FAILURE_MESSAGE);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e2) {
                logger.error(e2.toString());
            }
        }
        return buffer.toString();
    }

    public static String sendGet(String urlString,Map<String, Object> params,boolean isHttps) throws Exception {
        String requestParams = buildParams(params);
        StringBuilder sb = new StringBuilder();
        sb.append(urlString);
        sb.append(requestParams);
        return sendGet(sb.toString(),isHttps);
    }

    /**
     * 发送post请求 ,支持Https
     * @param urlString
     * @param params
     * @param isHttps
     * @return
     * @throws Exception
     */
    public static String sendPost(String urlString, Map<String, Object> params,boolean isHttps) throws Exception {
        String requestParams = buildParams(params);
        return post(urlString,requestParams,isHttps);
    }

    /**
     *发送post请求
     * @param urlString
     * @param content
     * @param isHttps
     * @return
     * @throws Exception
     */
    private static String post(String urlString, String content,boolean isHttps) throws Exception{
        StringBuffer resultBuffer = null;
        HttpURLConnection con ;
        OutputStreamWriter osw  = null;
        BufferedReader br  = null ;
        InputStream inputStream  =null;
        // 发送请求
        try {
            URL url = new URL(urlString);
            con = isHttps ? (HttpsURLConnection) url.openConnection() : (HttpURLConnection) url.openConnection() ;
            con.setRequestMethod("POST");
            con.setConnectTimeout(CONNECT_OUT_TIME);
            con.setReadTimeout(READ_OUT_TIME);
            // 发送POST请求必须设置如下两行
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Type", CONTENT_TYPE);//表单
            con.setRequestProperty("Content-Length", Integer.toString(content.length()));
            if (isHttps) {//https需信任证书
                initSSLContext((HttpsURLConnection)con);
            }
            // 建立实际的连接
            con.connect();
            if (StringUtils.isNotEmpty(content)) {
                osw = new OutputStreamWriter(con.getOutputStream(), EncodeConsts.UTF_8);
                osw.write(content);//发送请求参数
                osw.flush();
                osw.close();
            }
            //获取返回http连接状态码
            int responseCode = con.getResponseCode();
            logger.info("返回http连接状态码: {}",responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = con.getInputStream();
            } else {
                inputStream = con.getErrorStream();
            }
            br = new BufferedReader(new InputStreamReader(inputStream, EncodeConsts.UTF_8));
            String temp;
            // 读取返回内容
            resultBuffer = new StringBuffer();
            while ((temp = br.readLine()) != null) {
                resultBuffer.append(temp);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            throw new SkyException(ExceptionConsts.SERVER_REQUEST_FAILURE_CODE,ExceptionConsts.SERVER_REQUEST_FAILURE_MESSAGE);
        } finally {
            try {
                if(osw != null ){
                    osw.close();
                }
                if(br != null ){
                    br.close();
                }
                if(inputStream != null ){
                    inputStream.close();
                }
            } catch (IOException e2) {
                logger.error(e2.toString());
            }
        }
        return resultBuffer.toString();
    }

    //转为安全url编码
    public static String urlEncode(String url){
        try{
           return URLEncoder.encode(url,EncodeConsts.UTF_8);
        }catch (Exception e){
            return "";
        }
    }

    //创建SSLContext对象，并使用我们指定的信任管理器初始化
    private static void  initSSLContext(HttpsURLConnection con) throws Exception{
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, trustManagerArray, new SecureRandom());
        SSLSocketFactory sockFact = context.getSocketFactory();
        con.setSSLSocketFactory(sockFact);
    }

    //构建请求参数
    public static String buildParams(Map<String, Object> params){
        // 构建请求参数 // StringBuilder线程不安全
        StringBuilder sbParams = new StringBuilder();
        sbParams.append(QP_SEP_Q);
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                sbParams.append(QP_SEP_A);
                sbParams.append(entry.getKey());
                sbParams.append(NAME_VALUE_SEPARATOR);
                if (entry.getValue() != null && !entry.getValue().equals("null")) {
                    sbParams.append(urlEncode(entry.getValue().toString()));
                }
            }
        }
        //请求参数 去掉特殊字符
        String requestParams = sbParams.toString().replaceAll("\\s", "").replaceAll("\\n", "");
        logger.info("参数构建结果: {} ",requestParams);
        return requestParams ;
    }

}
