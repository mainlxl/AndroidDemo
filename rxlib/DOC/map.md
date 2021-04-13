# map方法解析

* 首先是Map代码调用 把222转换为666
	* `StartOnSubscribe` 被订阅者
	* `Func1` 转换func
	* `ResultSubscriber` 订阅者

```java
public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Observable.create(new StartOnSubscribe()).map(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return 666;
            }
        }).subscribe(new ResultSubscriber());
    }

    public static class StartOnSubscribe implements Observable.OnSubscribe<Integer> {
        @Override
        public void call(Subscriber<? super Integer> subscriber) {
            subscriber.onNext(222);
        }
    }

    public static class ResultSubscriber<T> extends Subscriber<T> {
        @Override
        public void onCompleted() {}
        @Override
        public void onError(Throwable e) {}
        @Override
        public void onNext(T integer) {
            System.out.println(integer);
        }
    }
}
```

* 然后看Observable的map方法

```java
public final <R> Observable<R> map(Func1<? super T, ? extends R> func) {
    return lift(new OperatorMap<T, R>(func));
}
```
* 封装了一个`OperatorMap`,而`OperatorMap`实现`Operator`其实是一个`Func1`的实现,其中构造方法里又传入一个Func1(这个有点像使用变换过的装饰着模式),同时还含有一个call方法返回内部定义的一个子类`MapSubscriber`

```java
public final class OperatorMap<T, R> implements Operator<R, T> {
...省略代码
    @Override
    public Subscriber<? super T> call(final Subscriber<? super R> o) {
        MapSubscriber<T, R> parent = new MapSubscriber<T, R>(o, transformer);
        o.add(parent);
        return parent;
    }
...省略代码
```

* `MapSubscriber`一个订阅者在这个订阅者的内部`onNext`调用`map`方法传入的`Func1`执行map转换
* MapSubscriber其实是最后传入的订阅者`ResultSubscriber`的一个代理类

```
static final class MapSubscriber<T, R> extends Subscriber<T> {
...省略代码
        @Override
        public void onNext(T t) {
            R result;
            
            try {
                result = mapper.call(t);//此处mapper就是map方法传入的Func1(例子中222变成666的方法)
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                unsubscribe();
                onError(OnErrorThrowable.addValueAsLastCause(ex, t));
                return;
            }
            
            actual.onNext(result);//这里actual是OperatorMap中call方法传入的父订阅者(ResultSubscriber)
        }
        
        @Override
        public void onError(Throwable e) {
          ...省略代码
            actual.onError(e);
        }
        
        
        @Override
        public void onCompleted() {
       ...省略代码
            actual.onCompleted();
        }
...省略代码
    }
```

* 看完`OperatorMap`继续往下看`map`方法中调用的`lift`方法

```java
public final <R> Observable<R> lift(final Operator<? extends R, ? super T> operator) {
      return new Observable<R>(new OnSubscribeLift<T, R>(onSubscribe, operator));
}
```

* 这里又是一个代理模式,`OnSubscribeLift`代理原本的`onSubscribe`对象来完成<b>"订阅操作(具体流程比较复杂请参看源码)"</b>
* <b>"订阅操作"</b>简单说就`.subscribe(new ResultSubscriber())`之后执行的一系列方法大致分可简化为三步。
	* 首先`Subscriber`的`onStart()`
	* 然后是`OnSubscribe`的`call`方法
	* `call`方法中又相继执行`Subscriber`的`onNext`,`onError`,`onCompleted`方法
* 这里一个弯就是原本的`Observable.create(new StartOnSubscribe())`产生的`Observable`被替换成`lift`方法new出来的`Observable`给替换了,原本的`Observable`并没有执行**"订阅操作"**
* 过了这个弯就知道了`OnSubscribeLift`这个代理类执行了<b>"订阅操作"</b>,具体看`OnSubscribeLift`中都干了什么

```java
public final class OnSubscribeLift<T, R> implements OnSubscribe<R> {
...省略代码
    @Override
    public void call(Subscriber<? super R> o) {
        try {
            //operator是构造方法传入的Func1的代理类
            //hook.onLift方法把传入的operator返回也就是调用operator的call方法返回Subscriber(MapSubscriber)
            Subscriber<? super T> st = hook.onLift(operator).call(o);
            try {
                st.onStart();
            //parent是构造方法传入的最开始Observable中OnSubscribe也就是StartOnSubscribe
                parent.call(st);
            } catch (Throwable e) {
                ...省略代码
            }
        } catch (Throwable e) {
           ...省略代码
        }
    }
}

```

* `StartOnSubscribe`的`call`方法在这里被执行了,在`call`中调用`st`的`onNext`传入`222`,也就是调用`MapSubscriber`的`onNext`方法

```
static final class MapSubscriber<T, R> extends Subscriber<T> {
...省略代码
        @Override
        public void onNext(T t) {
            R result;
            
            try {
                result = mapper.call(t);//此处mapper就是map方法传入的Func1(例子中222变成666的方法)
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                unsubscribe();
                onError(OnErrorThrowable.addValueAsLastCause(ex, t));
                return;
            }
            
            actual.onNext(result);//这里actual是OperatorMap中call方法传入的父订阅者(ResultSubscriber)
        }
        
        @Override
        public void onError(Throwable e) {
          ...省略代码
            actual.onError(e);
        }
        
        
        @Override
        public void onCompleted() {
       ...省略代码
            actual.onCompleted();
        }
...省略代码
    }
```

* 再次看`MapSubscriber`的`onNext`执行过程,先转换然后调用被代理的真实Subscriber也就是`ResultSubscriber`的`onNext`方法


**到这里就看完了整个过程还是比较复杂的。使用了变种装饰者模式与代理模式(PS:这两个模式不太好区分结构上差别不大多在用法上不同)**

-----------------------------

### 补充
lift方法中为直接重新创建一个Observable未来有可能会改进,如果T与R类型一样,直接修改Observable的onSubscribe引用为新的OnSubscribeLift返回自身,可惜java没有判断泛型T与R类型上是否相同,只能期盼未来了
