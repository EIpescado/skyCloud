package org.skyCloud.filter;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.skyCloud.constants.RequestConstant;
import org.skyCloud.utils.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *业务请求Token检查
 */
@Component
public class TokenFilter extends ZuulFilter{


    private final PathMatcher pathMatcher = new AntPathMatcher();

    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

     //不拦截的请求链接
    private  static List<String> noInterceptorList=new ArrayList<String>(){
        {
            add("/api/system/user/login");
        }
    };

    private static final String FAIL_CODE = "10086";
    private static final String FAIL_MSG = "登录已失效";

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Value("${token.secret}")
    private String tokenSecret;  //token秘钥

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req= ctx.getRequest();

        if(isExclude(req)){
            return null;
        }
        String token = req.getHeader(RequestConstant.TOKEN);
        logger.debug("请求头中的token:" + token);

        if ( null != token && "".equals(token)){
            //检查token是否有效
            boolean bo = TokenUtil.isValid(token,tokenSecret);
            //检查token是否已经被弃用(session redis中能查到代表已弃用)
            String outToken ;
            try{
                outToken= redisTemplate.opsForValue().get(token);
            }catch (Exception e){
                logger.error("redis异常",e);
                return null;
            }
            logger.debug("redis中取出的token:" + outToken);
            if (bo && (null == outToken || "".equals(outToken))){
               return null;
            }else {
                failLogin(ctx);
            }
        }else {
            failLogin(ctx);
        }

        return null;
    }

    /**
     * 请求是否拦截
     */
    private boolean isExclude(final HttpServletRequest request) {
        String url = request.getServletPath();
        for(String pattern : noInterceptorList) {
            if(pathMatcher.match(pattern, url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 响应客户端
     */
    public void failLogin(RequestContext ctx){
        try{
            HttpServletResponse response = ctx.getResponse();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = response.getWriter();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code",FAIL_CODE);
            jsonObject.put("msg",FAIL_MSG);
            out.write(jsonObject.toString());
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            ctx.setResponse(response);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
