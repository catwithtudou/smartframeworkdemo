package org.smart4j.framework.util;

/**
 * @author 郑煜
 * @Title: CastUtil
 * @ProjectName smartframework
 * @Description: 转型操作工具类
 * @date 2019/3/7下午 08:13
 */
public final class CastUtil {
    /**
     　　* @Description:转为String型
     　　* @param :
     　　* @return :
     　　*/
    public static  String castString(Object obj){
        return CastUtil.castString(obj,"");
    }
    /**
     　　* @Description:转为String型(提供默认值)
     　　* @param :
     　　* @return :
     　　*/
    public static  String castString(Object obj,String defaultValue){
        return obj!=null?String.valueOf(obj):defaultValue;
    }

    /**
     　　* @Description:转为double型
     　　* @param :
     　　* @return :
     　　*/
    public static double castDouble(Object obj){
        return CastUtil.castDouble(obj,0);
    }

    /**
     　　* @Description:转为double型(提供默认值)
     　　* @param :
     　　* @return :
     　　*/
    public static double castDouble(Object obj,double defaultDouble){
        double doubleValue=defaultDouble;
        if(obj!=null){
            String strValue=castString(obj);
            if(StringUtil.isNotEmpty(strValue)){
                try{
                    doubleValue=Double.parseDouble(strValue);
                }catch (NumberFormatException e){
                    doubleValue=defaultDouble;
                }
            }
        }
        return doubleValue;
    }

    /**
     　　* @Description:转为long型
     　　* @param :
     　　* @return :
     　　*/
    public static long castLong(Object object){
        return  CastUtil.castLong(object,0);
    }

    /**
     　　* @Description:转为long型(提供默认值)
     　　* @param :
     　　* @return :
     　　*/
    public static long castLong(Object object,long defalutValue){
        long longValue=defalutValue;
        if(object!=null){
            String str=castString(object);
            if(StringUtil.isNotEmpty(str)){
                try{
                    longValue=Long.parseLong(str);
                }catch (NumberFormatException e){
                    longValue=defalutValue;
                }
            }
        }
        return longValue;
    }

    /**
     　　* @Description:int型
     　　* @param :
     　　* @return :
     　　*/
    public static int castInt(Object o){
        return CastUtil.castInt(o,0);
    }
    /**
     　　* @Description:int型(提供默认值)
     　　* @param :
     　　* @return :
     　　*/
    public static int castInt(Object o,int de){
        int intvalue=de;
        if(o!=null){
            String str=castString(o);
            if(StringUtil.isNotEmpty(str)){
                try{
                    intvalue=Integer.parseInt(str);
                }catch (NumberFormatException e){
                    intvalue=de;
                }
            }
        }
        return intvalue;
    }

    /**
     　　* @Description:boolean型
     　　* @param :
     　　* @return :
     　　*/
    public static boolean castBoolean(Object o){
        return  CastUtil.castBoolean(o,false);
    }

    /**
     　　* @Description:boolean型(提供默认值)
     　　* @param :
     　　* @return :
     　　*/
    public static boolean castBoolean(Object o,boolean de){
        boolean booleanvalue=de;
        if(o!=null){
            booleanvalue=Boolean.parseBoolean(castString(o));
        }
        return booleanvalue;
    }



}
