package com.zhaoxuan.distribute.deprecated;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * 任务分发池
 * Created by lizhaoxuan on 16/3/7.
 */
public class DistributePool {
    private static final String TAG = "DistributePool";

    private static final String METHOD_NAME = "disInbox";

    private static DistributePool instance;

    private List<DisObserver> observers;

    public static DistributePool getInstance() {
        if (instance == null) {
            instance = new DistributePool();
        }
        return instance;
    }

    public DistributePool() {
        observers = new LinkedList<>();
    }

    public void addObserver(DisObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    /**
     * 事件发送
     *
     * @param data 发送数据包
     */
    public void post(Object data) {
        for (int i = 0, length = observers.size(); i < length; i++) {
            DisObserver observer = observers.get(i);
            //移除无效的观察者
            if (observer == null) {
                observers.remove(i);
                continue;
            }
            if (foundObserver(data, observer)){
                try {
                    observer.disInbox(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.w(TAG, "observer not found");
            }
        }
    }

    /**
     * 查找目标观察者
     *
     * @param target   目标类型
     * @param observer 观察者
     * @return 目标方法
     */
    private boolean foundObserver(Object target, Object observer) {
        Method[] methods = observer.getClass().getMethods();
        for (Method method : methods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> clazz : parameterTypes) {
                if (target.getClass().getName().equals(clazz.getName()))
                    return true;
            }
        }
        return false;
    }

}
