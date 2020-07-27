package com.dj.dynamicplugin1;

import com.dj.mypluginlibrary1.IBean;
import com.dj.mypluginlibrary1.ICallback;

public class Dynamic1Bean implements IBean {
    private ICallback callback;
    private String name = "axb";
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String paramString) {
        this.name = paramString;
    }

    @Override
    public void register(ICallback callback) {
        this.callback = callback;
    }

    @Override
    public void clickButton(){
        callback.sendResult("Hello:"+this.name);
    }

}
