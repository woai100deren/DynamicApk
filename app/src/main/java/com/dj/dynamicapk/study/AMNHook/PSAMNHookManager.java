package com.dj.dynamicapk.study.AMNHook;

import android.os.Build;
import android.util.Log;

import com.dj.dynamicapk.utils.RefInvoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PSAMNHookManager {
    private static final String TAG = PSAMNHookManager.class.getName();

    /**
     * 这种hook方式，拦截了整个工程的页面跳转，各页面无需再继承一个特殊的基类
     */
    public static void hook(){
        try {
            Object singleton = null;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //获取ActivityTaskManager的单例IActivityTaskManagerSingleton，IActivityTaskManagerSingleton是final静态的
                singleton = RefInvoke.getStaticFieldObject("android.app.ActivityTaskManager", "IActivityTaskManagerSingleton");
            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //获取ActivityManager的单例IActivityManagerSingleton，IActivityManagerSingleton是final静态的
                singleton = RefInvoke.getStaticFieldObject("android.app.ActivityManager", "IActivityManagerSingleton");
            }else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                //获取ActivityManagerNative的单例gDefault，gDefault静态的
                singleton = RefInvoke.getStaticFieldObject("android.app.ActivityManagerNative", "gDefault");
            }

            //IActivityManagerSingleton是一个android.util.Singleton<T>对象，取出这个单例中的mInstance字段
            Object mInstance = RefInvoke.getFieldObject("android.util.Singleton", singleton, "mInstance");

            //创建一个这个对象的代理对象MockClass，然后替换这个字段，让我们的代理干活
            Class<?> classB2Interface = null;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                classB2Interface = Class.forName("android.app.IActivityTaskManager");
            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                classB2Interface = Class.forName("android.app.IActivityManager");
            }

            //loader: 用哪个类加载器去加载代理对象
            //interfaces:动态代理类需要实现的接口
            //h:动态代理方法在执行时，会调用h里面的invoke方法去执行
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{classB2Interface},
                    new MockClass(mInstance));
            RefInvoke.setFieldObject("android.util.Singleton", singleton, "mInstance", proxy);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class MockClass implements InvocationHandler{
        private Object base;
        public MockClass(Object base){
            this.base = base;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if("startActivity".equals(method.getName())){
                Log.e(TAG,"AMNHook方式：我在所有页面跳转之前，打印了这个日志");//此处延伸思考，可以对页面做打点，统计PV
            }
            return method.invoke(base,args);
        }
    }
}
