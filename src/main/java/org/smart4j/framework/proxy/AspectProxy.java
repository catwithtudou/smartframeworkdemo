package org.smart4j.framework.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * @author 郑煜
 * @Title: AspectProxy
 * @ProjectName smartframework
 * @Description: 切面代理
 * @date 2019/3/11下午 06:37
 */
public abstract class AspectProxy implements Proxy  {

    private static final Logger LOGGER = LoggerFactory.getLogger(AspectProxy.class);

    @Override
    public final Object doProxy(ProxyChain proxyChain)throws  Throwable{
        Object result=null;

        Class<?> cls=proxyChain.getTargetClass();
        Method method=proxyChain.getTargetMethod();
        Object[] params =proxyChain.getMethodParams();

        begin();
        try{
            if(intercept(cls,method,params)){
                before(cls,method,params);
                result=proxyChain.doProxyChain();
                after(cls,method,params);
            }else{
                result= proxyChain.doProxyChain();
            }
        }catch (Exception e){
            LOGGER.error("proxy failure",e);
            error(cls,method,params);
            throw  e;
        }finally {
            end();
        }
        return result;
    }

    public void begin(){
    }

    public boolean intercept(Class<?> cls,Method method,Object[] params)throws Throwable{
        return  true;
    }

    public void before(Class<?> cls,Method method,Object[] pragmas)throws Throwable{

    }

    public void after(Class<?> cls,Method method,Object[] pragmas)throws Throwable{

    }

    public void error(Class<?> cls,Method method,Object[] pragmas)throws Throwable{

    }

    public void end(){

    }
}
