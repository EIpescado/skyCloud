package org.skyCloud.lcn;

import com.codingapi.tx.springcloud.interceptor.TxManagerInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TxTransactionInterceptor implements Ordered {

    @Autowired
    private TxManagerInterceptor txManagerInterceptor;

    @Override
    public int getOrder() {
        return 1;
    }

    @Around("execution(* org.skyCloud.service.*Service.*(..)) or execution(* org.skyCloud.lcn.*Service.*(..))")
    public Object around(ProceedingJoinPoint point)throws Throwable{
        return txManagerInterceptor.around(point);
    }
}
