package com.dj.dynamicapk.study.dynamicProxy;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dj.dynamicapk.application.DynamicApplication;
import com.dj.dynamicapk.utils.RefInvoke;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class PSAMSHookHelper {
    private static final String TAG = PSAMSHookHelper.class.getName();
    public static final String EXTRA_TARGET_INTENT = "extra_target_intent";
    /**
     * Hook AMS
     * 主要完成的操作是  "把真正要启动的Activity临时替换为在AndroidManifest.xml中声明的替身Activity",进而骗过AMS
     */
    public static void hookAMN() throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, NoSuchFieldException {
        //获取AMN的gDefault单例gDefault，gDefault是final静态的
        Object gDefault = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //获取ActivityTaskManager的单例IActivityTaskManagerSingleton，IActivityTaskManagerSingleton是final静态的
            gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityTaskManager", "IActivityTaskManagerSingleton");
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //获取ActivityManager的单例IActivityManagerSingleton，IActivityManagerSingleton是final静态的
            gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityManager", "IActivityManagerSingleton");
        }else if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //获取ActivityManagerNative的单例gDefault，gDefault静态的
            gDefault = RefInvoke.getStaticFieldObject("android.app.ActivityManagerNative", "gDefault");
        }

        // gDefault是一个 android.util.Singleton<T>对象; 我们取出这个单例里面的mInstance字段
        Object mInstance = RefInvoke.getFieldObject("android.util.Singleton", gDefault, "mInstance");

        // 创建一个这个对象的代理对象MockClass1, 然后替换这个字段, 让我们的代理对象帮忙干活
        //创建一个这个对象的代理对象MockClass，然后替换这个字段，让我们的代理干活
//        Class<?> classB2Interface = null;
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            classB2Interface = Class.forName("android.app.IActivityTaskManager");
//        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            classB2Interface = Class.forName("android.app.IActivityManager");
//        }
        Class<?> classB2Interface = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            classB2Interface = Class.forName("android.app.IActivityTaskManager");
        } else {
            classB2Interface = Class.forName("android.app.IActivityManager");
        }
        Object proxy = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] { classB2Interface },
                new MockClass1(mInstance));

        //把gDefault的mInstance字段，修改为proxy
        RefInvoke.setFieldObject("android.util.Singleton", gDefault, "mInstance", proxy);
    }

    /**
     * 由于之前我们用替身欺骗了AMS; 现在我们要换回我们真正需要启动的Activity
     * 不然就真的启动替身了, 狸猫换太子...
     * 到最终要启动Activity的时候,会交给ActivityThread 的一个内部类叫做 H 来完成
     * H 会完成这个消息转发; 最终调用它的callback
     */
    public static void hookActivityThread() throws Exception {

        // 先获取到当前的ActivityThread对象
        Object currentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread", "sCurrentActivityThread");

        // 由于ActivityThread一个进程只有一个,我们获取这个对象的mH
        Handler mH = (Handler) RefInvoke.getFieldObject(currentActivityThread, "mH");

        //把Handler的mCallback字段，替换为new MockClass2(mH)
        RefInvoke.setFieldObject(Handler.class,
                mH, "mCallback", new MockClass2(mH));
    }



    static class MockClass1 implements InvocationHandler {
        Object mBase;

        public MockClass1(Object base) {
            mBase = base;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Log.e(TAG, "执行了方法："+method.getName());
            // 替身Activity的包名, 也就是我们自己的包名
            String stubPackage = "com.dj.dynamicapk";
            if ("startActivity".equals(method.getName())) {
                // 只拦截这个方法
                // 替换参数, 任你所为;甚至替换原始Activity启动别的Activity偷梁换柱

                // 找到参数里面的第一个Intent 对象
                Intent raw;
                int index = 0;

                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }
                raw = (Intent) args[index];
                Intent newIntent = new Intent();
                // 这里我们把启动的Activity临时替换为 StubActivity
                ComponentName componentName = new ComponentName(stubPackage, PSStubActivity.class.getName());
                newIntent.setComponent(componentName);
                // 把我们原始要启动的TargetActivity先存起来
                newIntent.putExtra(EXTRA_TARGET_INTENT, raw);
                // 替换掉Intent, 达到欺骗AMS的目的
                args[index] = newIntent;
                Log.d(TAG, "hook success");
                return method.invoke(mBase, args);
            }else if ("startService".equals(method.getName())) {
                // 只拦截这个方法
                // 替换参数, 任你所为;甚至替换原始StubService启动别的Service偷梁换柱

                // 找到参数里面的第一个Intent 对象
                int index = 0;
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }

                //get StubService form DynamicApplication.pluginServices
                Intent rawIntent = (Intent) args[index];
                String rawServiceName = rawIntent.getComponent().getClassName();

                String stubServiceName = DynamicApplication.pluginServices.get(rawServiceName);

                // replace Plugin Service of StubService
                ComponentName componentName = new ComponentName(stubPackage, stubServiceName);
                Intent newIntent = new Intent();
                newIntent.setComponent(componentName);

                // Replace Intent, cheat AMS
                args[index] = newIntent;

                Log.d(TAG, "hook success");
                return method.invoke(mBase, args);
            } else if ("stopService".equals(method.getName())) {
                // 只拦截这个方法
                // 替换参数, 任你所为;甚至替换原始StubService启动别的Service偷梁换柱

                // 找到参数里面的第一个Intent 对象
                int index = 0;
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }

                //get StubService form DynamicApplication.pluginServices
                Intent rawIntent = (Intent) args[index];
                String rawServiceName = rawIntent.getComponent().getClassName();
                String stubServiceName = DynamicApplication.pluginServices.get(rawServiceName);

                // replace Plugin Service of StubService
                ComponentName componentName = new ComponentName(stubPackage, stubServiceName);
                Intent newIntent = new Intent();
                newIntent.setComponent(componentName);

                // Replace Intent, cheat AMS
                args[index] = newIntent;

                Log.d(TAG, "hook success");
                return method.invoke(mBase, args);
            }else if("bindService".equals(method.getName())){
                //找到参数里面的第一个Intent对象
                // 找到参数里面的第一个Intent 对象
                int index = 0;
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }
                Intent rawIntent = (Intent) args[index];
                String rawServiceName = rawIntent.getComponent().getClassName();
                String stubServiceName = DynamicApplication.pluginServices.get(rawServiceName);
                ComponentName componentName = new ComponentName(stubPackage, stubServiceName);
                Intent newIntent = new Intent();
                newIntent.setComponent(componentName);
                args[index] = newIntent;

                Log.d(TAG, "hook success");
                return method.invoke(mBase, args);
            }

            return method.invoke(mBase, args);
        }
    }


    static class MockClass2 implements Handler.Callback {

        Handler mBase;

        public MockClass2(Handler base) {
            mBase = base;
        }

        @Override
        public boolean handleMessage(Message msg) {
            //从android p开始，activity的启动流程有所变化。ActivityThread中，activity的生命周期，都是在EXECUTE_TRANSACTION回调中完成
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                switch (msg.what) {
                    // ActivityThread里面 "CREATE_SERVICE" 这个字段的值是114
                    // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
                    case 114:
//                        try{
//                            Object obj = msg.obj;
//                            //获取ClientTransaction中的mActivityCallbacks集合
//                            Class<?> clazz = Class.forName("android.app.servertransaction.ServiceArgsData");
//                            Field mActivityCallbacksFiled = clazz.getDeclaredField("mActivityCallbacks");
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
                        handleCreateService(msg);
                        break;
                    // ActivityThread里面 "EXECUTE_TRANSACTION" 这个字段的值是159
                    // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
                    case 159:
                        try {
                            Object obj = msg.obj;
                            //获取ClientTransaction中的mActivityCallbacks集合
                            Class<?> clazz = Class.forName("android.app.servertransaction.ClientTransaction");
                            Field mActivityCallbacksFiled = clazz.getDeclaredField("mActivityCallbacks");
                            mActivityCallbacksFiled.setAccessible(true);
                            List list = (List) mActivityCallbacksFiled.get(obj);
                            if (list != null && list.size() > 0 && "android.app.servertransaction.LaunchActivityItem".equals(list.get(0).getClass().getName())) {
                                //得到集合中的LaunchActivityItem
                                Object o = list.get(0);
                                //获取LaunchActivityItem中的mIntent
                                Class<?> LaunchActivityItemClazz = Class.forName("android.app.servertransaction.LaunchActivityItem");
                                Field mIntentFiled = LaunchActivityItemClazz.getDeclaredField("mIntent");
                                mIntentFiled.setAccessible(true);
                                Intent intent = (Intent) mIntentFiled.get(o);
                                //得到我们设置的class 替换进去
                                if (intent.getParcelableExtra(EXTRA_TARGET_INTENT) != null) {
                                    Intent target = intent.getParcelableExtra(EXTRA_TARGET_INTENT);
                                    intent.setComponent(target.getComponent());


                                    //修改packageName，这样缓存才能命中
                                    ActivityInfo activityInfo = (ActivityInfo) RefInvoke.getFieldObject(o, "mInfo");
                                    activityInfo.applicationInfo.packageName = target.getPackage() == null ?
                                            target.getComponent().getPackageName() : target.getPackage();

                                    try {
                                        hookPackageManager();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG,e.toString());
                        }
                        break;
                }
            } else{
                switch (msg.what) {
                    // ActivityThread里面 "LAUNCH_ACTIVITY" 这个字段的值是100
                    // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
                    case 100:
                        handleLaunchActivity(msg);
                        break;
                    // ActivityThread里面 "CREATE_SERVICE" 这个字段的值是114
                    // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
                    case 114:
                        handleCreateService(msg);
                        break;
                }
            }


            mBase.handleMessage(msg);
            return true;
        }

        private void handleLaunchActivity(Message msg) {
            // 这里简单起见,直接取出TargetActivity;
            Object obj = msg.obj;

            // 把替身恢复成真身
            Intent raw = (Intent) RefInvoke.getFieldObject(obj, "intent");

            Intent target = raw.getParcelableExtra(EXTRA_TARGET_INTENT);
            raw.setComponent(target.getComponent());

            //修改packageName，这样缓存才能命中
            ActivityInfo activityInfo = (ActivityInfo) RefInvoke.getFieldObject(obj, "activityInfo");
            activityInfo.applicationInfo.packageName = target.getPackage() == null ?
                    target.getComponent().getPackageName() : target.getPackage();

            try {
                hookPackageManager();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleCreateService(Message msg) {
            // 这里简单起见,直接取出插件Servie

            Object obj = msg.obj;
            ServiceInfo serviceInfo = (ServiceInfo) RefInvoke.getFieldObject(obj, "info");

            String realServiceName = null;

            for (String key : DynamicApplication.pluginServices.keySet()) {
                String value = DynamicApplication.pluginServices.get(key);
                if(value.equals(serviceInfo.name)) {
                    realServiceName = key;
                    break;
                }
            }

            serviceInfo.name = realServiceName;
        }

        private static void hookPackageManager() throws Exception {

            // 这一步是因为 initializeJavaContextClassLoader 这个方法内部无意中检查了这个包是否在系统安装
            // 如果没有安装, 直接抛出异常, 这里需要临时Hook掉 PMS, 绕过这个检查.
            Object currentActivityThread = RefInvoke.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread");

            // 获取ActivityThread里面原始的 sPackageManager
            Object sPackageManager = RefInvoke.getFieldObject(currentActivityThread, "sPackageManager");

            // 准备好代理对象, 用来替换原始的对象
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                    new Class<?>[] { iPackageManagerInterface },
                    new MockClass3(sPackageManager));

            // 1. 替换掉ActivityThread里面的 sPackageManager 字段
            RefInvoke.setFieldObject(currentActivityThread, "sPackageManager", proxy);
        }
    }


    static class MockClass3 implements InvocationHandler {

        private Object mBase;

        public MockClass3(Object base) {
            mBase = base;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("getPackageInfo")) {
                return new PackageInfo();
            }
            return method.invoke(mBase, args);
        }
    }
}
