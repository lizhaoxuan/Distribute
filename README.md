##Distribute事件分发组件
基于观察者与java反射机制的事件分发组件

====================

###前言

这个组件的开发的初衷是：在开发Thrift（更节约的网络框架）后，因为请求的合并，返回结构也合并了，没有办法像从前一样一个请求对应一个回调方法，需要一个将数据按类型分发的组件。

经过初步设想决定的实现方案是观察者模式思想。

单例的被观察者接收网络加载的数据，加载数据后根据数据类型的不同分发到每个观察者上进行处理。

初版Demo很快做好了，却发现new观察者的实现并没有EventBus美观，想着我也能不能像EventBus一样，不需要实现任何接口，new任何的类，只要按照命名规范写方法就好……

EventBus怎么实现的来着？用反射，那我也用反射……

**然后，然后我就做出来一个超简版的EventBus，我真没抄啊。但实现原理完全一样啊……只能说殊途同归吧……**


###实现机制

核心原理非常简单。

- 1.被观察者只有一个，被设置一个单例。接收数据后遍历观察者进行消息分发。

- 2.观察者可以是任何对象，没错，任何对象：Object.

- 3.关键在于遍历观察者。通过反射机制取得所有观察者的Method,然后遍历所有Method,做关键字比对，发现目标Method后，遍历其所有参数，发现参数类型与被观察者接受的数据类型相同后，再通过反射进行调用。

- 4.over~


是不是很简单，实际上也真的是很简单，核心类只有两个方法。但你可能也发现问题了，效率问题！遍历了那么多次，反射的性能又低。

但通常在做反射优化时，都是将反射的结果缓存起来，可是这一块并不容易做缓存。所以就有一个取舍产生了：

**是为了性能，而选择代码编写不那么方便的传统观察者模式，还是为了编写方便，采用反射机制**

下面上代码，你一看就全懂了：



	/**
     * 任务分发池 - 观察者
     * Created by lizhaoxuan on 16/3/7.
     */
    public class DistributePool {
        private static final String TAG = "DistributePool";
    	
    	//方法的命名规则
        private static final String METHOD_NAME = "disInbox";
    
    	//单例的观察者
        private static DistributePool instance;
    
        private List<Object> observers;
    
        public static DistributePool getInstance() {
            if (instance == null) {
                instance = new DistributePool();
            }
            return instance;
        }
    
        public DistributePool() {
            observers = new LinkedList<>();
        }
    
    	//增加观察者，object类型
        public void addObserver(Object observer) {
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
                Object observer = observers.get(i);
                //移除无效的观察者
                if (observer == null) {
                    observers.remove(i);
                    continue;
                }
                //找到目标方法
                Method m1 = foundObserver(data, observer);
                if (m1 != null) {
                    try {
                    	 //执行观察者方法
                        m1.invoke(observer, data);
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
        private Method foundObserver(Object target, Object observer) {
            Method[] methods = observer.getClass().getMethods();
            //遍历所有方法
            for (Method method : methods) {
            	 //发现目标方法
                if (method.getName().contains(METHOD_NAME)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    //遍历所有参数
                    for (Class<?> clazz : parameterTypes) {
                    	 //判断是否与数据源相同
                        if (target.getClass().getName().equals(clazz.getName()))
                            return method;
                    }
                }
            }
            return null;
        }
    
    }


是不是真的很简单？具体调用看[Demo代码](./DistributeDemo/app/src/main/java/com/lizhaoxuan/distributedemo)

[另外需要具体观察者接口的实现方法]()：效率上较这样更优，但使用没有这样方便。
	













