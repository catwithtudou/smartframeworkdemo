package org.smart4j.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author 郑煜
 * @Title: ClassUtil
 * @ProjectName smartframework
 * @Description: 类操作工具类
 * @date 2019/3/7下午 08:15
 */
public final class ClassUtil {
    private static final Logger LOGGER= LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader(){
        //TODO
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     */
    public static Class<?> loadClass(String className,boolean isInitalized){
    //TODO
        Class<?> cls;
        try{
            cls=Class.forName(className,isInitalized,getClassLoader());
        }catch (ClassNotFoundException e){
            LOGGER.error("load class failure",e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 获取指定包名下的所有类
     */
    public static Set<Class<?>> getClassSet(String packgeName){
        //TODO
        Set<Class<?>> classSet=new HashSet<>();
        try{
            Enumeration<URL> urls=getClassLoader().getResources(packgeName.replace(".","/"));
            while (urls.hasMoreElements()){
                URL url=urls.nextElement();
                if(url!=null){
                    String protocol=url.getProtocol();
                    if(protocol.equals("file")){
                        String packgePath=url.getPath().replaceAll("%20","");
                        addClass(classSet,packgePath,packgeName);
                    }else if(protocol.equals("jar")){
                        JarURLConnection jarURLConnection=(JarURLConnection)url.openConnection();
                        if(jarURLConnection!=null){
                            JarFile jarFile=jarURLConnection.getJarFile();
                            if(jarFile!=null){
                                Enumeration<JarEntry> jarEntryEnumeration=jarFile.entries();
                                while (jarEntryEnumeration.hasMoreElements()){
                                    JarEntry jarEntry=jarEntryEnumeration.nextElement();
                                    String jarEntryName=jarEntry.getName();
                                    if(jarEntryName.endsWith(".class")){
                                        String className=jarEntryName.substring(0,jarEntryName.lastIndexOf(".")).replaceAll("/",".");
                                        doAddClass(classSet,className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            LOGGER.error("get class failure",e);
            throw  new RuntimeException(e);
        }
        return classSet;
    }

    private static void addClass(Set<Class<?>> classset,String packgePath,String packgeName){
        File[] files=new File(packgePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.isFile()&&pathname.getName().endsWith(".class"))||pathname.isDirectory();
            }
        });
        for(File file:files){
            String fileName=file.getName();
            if(file.isFile()){
                String className=fileName.substring(0,fileName.lastIndexOf("."));
                if(StringUtil.isNotEmpty(packgeName)){
                    className=packgeName+"."+className;
                }
                doAddClass(classset,className);
            }else {
                String subPackagePath=fileName;
                if(StringUtil.isNotEmpty(packgePath)){
                    subPackagePath=packgePath+"/"+subPackagePath;
                }
                String subPackageName=fileName;
                if(StringUtil.isNotEmpty(packgeName)){
                    subPackageName=packgeName+"."+subPackageName;
                }
                addClass(classset,subPackagePath,subPackageName);
            }
        }
    }

    private static void doAddClass(Set<Class<?>> classSet,String className){
        Class<?> cls=loadClass(className,false);
        classSet.add(cls);
    }

}
