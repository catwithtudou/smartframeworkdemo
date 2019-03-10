package org.smart4j.framework.annotation;

import java.lang.annotation.*;

/**
 * @author 郑煜
 * @Title: Aspect
 * @ProjectName smartframework
 * @Description: 切面注解
 * @date 2019/3/11上午 01:01
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {

    /**
     * 注解
     */
    Class<? extends Annotation> value();
}
