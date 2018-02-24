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
import java.util.ArrayList;
import java.util.List;

/**
 * 业务请求Token检查
 */
@Component
public class TokenFilter extends ZuulFilter {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * token秘钥
     */
    @Value("${token.secret}")
    private String tokenSecret;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    /**
     * 不拦截的请求链接
     */
    private static List<String> noInterceptorList = new ArrayList<String>() {
        {
            add("/api/system/user/login");
            add("/api/system/user/register");
        }
    };

    private static final String FAIL_CODE = "10086";
    private static final String FAIL_MSG = "登录已失效";
    /**登录失效返回json*/
    private static final String FAIL_RESPONSE_JSON =  new JSONObject()
                                                         .fluentPut("code", FAIL_CODE)
                                                         .fluentPut("msg", FAIL_MSG).toJSONString();


    /**
     * 过滤器类型
     * pre：可以在请求被路由之前调用
     * route：在路由请求时候被调用
     * post：在route和error过滤器之后被调用
     * error：处理请求时发生错误时被调用
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 定义路由执行顺序 数值越小优先级越高
     **/
    @Override
    public int filterOrder() {
        return 1;
    }

    /**
     * 指定过滤器的有效范围 判断该过滤器是否要执行
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器具体逻辑
     */
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest req = ctx.getRequest();
        if (isExclude(req)) {
            return null;
        }
        String token = req.getHeader(RequestConstant.TOKEN);
        logger.debug("请求头中的token:" + token);

        if (null != token && !"".equals(token)) {
            //检查token是否有效
            boolean bo = TokenUtil.isValid(token, tokenSecret);
            //检查token是否已经被弃用(session redis中能查到代表已弃用)
            String outToken;
            try {
                outToken = redisTemplate.opsForValue().get(token);
            } catch (Exception e) {
                logger.error("redis异常", e);
                return null;
            }
            logger.debug("redis中取出的token:" + outToken);
            if (bo && (null == outToken || "".equals(outToken))) {
                return null;
            } else {
                failLogin(ctx);
            }
        } else {
            failLogin(ctx);
        }
        return null;
    }

    /**
     * 请求是否拦截
     */
    private boolean isExclude(final HttpServletRequest request) {
        String url = request.getServletPath();
        for (String pattern : noInterceptorList) {
            if (pathMatcher.match(pattern, url)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 响应客户端
     */
    public void failLogin(RequestContext ctx) {
        try {
            HttpServletResponse response = ctx.getResponse();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            /**过滤该请求 不对其进行路由*/
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
            ctx.setResponseBody(FAIL_RESPONSE_JSON);
            ctx.setResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
