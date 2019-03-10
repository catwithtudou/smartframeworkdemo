package org.smart4j.framework.proxy;

/**
 * @author 郑煜
 * @Title: Proxy
 * @ProjectName smartframework
 * @Description: 代理接口
 * @date 2019/3/11上午 01:04
 */
public interface Proxy {
    /**
     * 执行链式代理
     */
    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
