package com.example.gw.enterprise.common.base;

import android.os.Handler;

/**
 * Created by gw on 15/9/17.
 * 取代BaseTask，不需要设置TaskId，通过实现RefreshHandler接口进行调用刷新
 */
public abstract class BaseRequestor implements Runnable {

    Handler handler = new Handler();
    public RefreshHandler refreshHandler;

    public void start() {
        new Thread(this).start();
    }

    public abstract Object execute();

    @Override
    public void run() {

        final Object response = execute();


        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (refreshHandler!=null) {
                    refreshHandler.refresh(response);
                }
            }
        });

    }

    public interface RefreshHandler {
        void refresh(Object obj);
    }
}
