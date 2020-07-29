package com.dj.dynamicapk.study.dynamicService;

import com.dj.dynamicapk.utils.RefInvoke;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * 由于应用程序使用的ClassLoader为PathClassLoader
 * 最终继承自 BaseDexClassLoader
 * 查看源码得知,这个BaseDexClassLoader加载代码根据一个叫做
 * dexElements的数组进行, 因此我们把包含代码的dex文件插入这个数组
 * 系统的classLoader就能帮助我们找到这个类
 *
 * 这个类用来进行对于BaseDexClassLoader的Hook
 */
public class PSBaseDexClassLoaderHookHelper {
    public static void patchClassLoader(ClassLoader cl, File apkFile, File optDexFile) throws IOException {
        //获取BaseDexClassloader:pathList
        Object pathListObj = RefInvoke.getFieldObject(DexClassLoader.class.getSuperclass(),cl,"pathList");
        //获取PathList：Element[] dexElements
        Object[] dexElements = (Object[]) RefInvoke.getFieldObject(pathListObj,"dexElements");
        //Element 类型
        Class elementClass = dexElements.getClass().getComponentType();

        // 创建一个数组, 用来替换原始的数组
        Object[] newElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);

        //构造插件Element(File file,boolean isDirectory,File zip,DexFile dexFile)这个构造函数
        Class[] p1 = {File.class,boolean.class,File.class, DexFile.class};
        Object[] v1 = {apkFile,false,apkFile,DexFile.loadDex(apkFile.getCanonicalPath(),optDexFile.getAbsolutePath(),0)};
        Object o = RefInvoke.createObject(elementClass,p1,v1);

        Object[] toAddElementArray = new Object[]{o};
        //把原始的elements赋值进去
        System.arraycopy(dexElements,0,newElements,0,dexElements.length);
        //把插件的那个element赋值进去
        System.arraycopy(toAddElementArray,0,newElements,dexElements.length,toAddElementArray.length);

        //替换
        RefInvoke.setFieldObject(pathListObj,"dexElements",newElements);
    }
}
