package org.smart4j.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author 郑煜
 * @Title: StreamUtil
 * @ProjectName smartframework
 * @Description: 流操作工具类
 * @date 2019/3/10下午 10:37
 */

public final class StreamUtil {
    private static final Logger LOGGER= LoggerFactory.getLogger(StreamUtil.class);


    /**
     * 从流中获取字符串
     */
    public static String getString(InputStream is){
        StringBuilder sb=new StringBuilder();

        try{
            BufferedReader reader=new BufferedReader(new InputStreamReader(is));
            String line;
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
        }catch (Exception e){
            LOGGER.error("get String failure ",e);
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
