package com.dj.dynamicapk.study.instrumentationHook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dj.dynamicapk.utils.RefInvoke;

public abstract class PSInstrumentationHookBaseActivity extends Activity {
    private static final String TAG=  PSInstrumentationHookBaseActivity.class.getName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();

        //这种hook方式很大的缺点：
        //只针对当前Activity生效，因为它只修改了当前Activity实例的mInstrumentation字段
        //所以我们把这个hook代码放在了基类中，如果页面不继承这个基类，那么hook就无效

        //获取Activity类中的私有全局变量mInstrumentation
        Instrumentation mInstrumentation = (Instrumentation) RefInvoke.getFieldObject(Activity.class,this,"mInstrumentation");
        //创建我们自定义的Instrumentation，并且拦截execStartActivity页面跳转方法，加入我们自己要做的事情
        HookInstrumentation hookInstrumentation = new HookInstrumentation(mInstrumentation);
        RefInvoke.setFieldObject(Activity.class,this,"mInstrumentation",hookInstrumentation);
    }

    public abstract void setContentView();

    public class HookInstrumentation extends Instrumentation{
        private Instrumentation mInstrumentation;
        public HookInstrumentation(Instrumentation mInstrumentation){
            this.mInstrumentation = mInstrumentation;
        }

        /**
         * 重写execStartActivity方法，会导致所有继承了PSInstrumentationHookBaseActivity的页面，页面跳转时，都会走这个方法。参考系统源码Activity.java
         * @param who
         * @param contextThread
         * @param token
         * @param target
         * @param intent
         * @param requestCode
         * @param options
         * @return
         */
        public ActivityResult execStartActivity(
                Context who, IBinder contextThread, IBinder token, Activity target,
                Intent intent, int requestCode, Bundle options) {
            Log.e(TAG,"我在所有页面跳转之前，打印了这个日志");//此处延伸思考，可以对页面做打点，统计PV

            Class[] classes = {Context.class,IBinder.class,IBinder.class,Activity.class,Intent.class,int.class,Bundle.class};
            Object[] objects = {who,contextThread,token,target,intent,requestCode,options};

            //执行原始的Instrumentation的execStartActivity方法，否则页面不能跳转了。
            return (ActivityResult) RefInvoke.invokeInstanceMethod(mInstrumentation,"execStartActivity",classes,objects);
        }
    }
}
