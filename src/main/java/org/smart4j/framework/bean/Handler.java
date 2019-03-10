package org.smart4j.framework.bean;

import java.lang.reflect.Method;

/**
 * @author 郑煜
 * @Title: Handler
 * @ProjectName smartframework
 * @Description: 封装Action信息
 * @date 2019/3/10下午 09:51
 */
public class Handler {

    /**
     * Controller类
     */
    private Class<?> controllerClass;

    /**
     * Action方法
     */
    private Method actionMethod;

    public Handler(Class<?> controllerClass,Method actionMethod){
        this.controllerClass = controllerClass;
        this.actionMethod=actionMethod;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }
}
