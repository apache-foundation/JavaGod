---
title: ThreadLocal源码分析
date: 2019-08-10 21:24:50
categories: 并发
description: 线程局部变量，为每一个使用该变量的线程都提供一个变量值的副本，每一个线程都可以独立地改变自己的副本，而不会和其它线程的副本冲突。即**“以空间换时间”**的方式解决线程不安全问题。


---

[TOC]



## 源码分析

### 原理

ThreadLocal中有一个静态内部类ThreadLocalMap（类似于HashMap但不是）。这个Map的**key是ThreadLocal当前对象，value就是我们存起来的值**。

下面通过分析重要方法的源码来一探究竟。

#### getMap()

取得的是当前线程的ThreadLocalMap

```
 ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }
```

注意：这个map取得的是Thread类的成员变量ThreadLocalMap

```
class Thread implements Runnable {
//每个线程都有自己ThreadLocalMap！
	ThreadLocal.ThreadLocalMap threadLocals = null;
```



#### set()

取当前线程的ThreadLocalMap，key设置为ThreadLocal当前对象，value设置为传入的值

```
   public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }
```



#### get()

取当前线程的ThreadLocalMap，将threadLocal作为key获取Entry后获取value。

```java
   public T get() {
        Thread t = Thread.currentThread();
        //取当前对象的map
        ThreadLocalMap map = getMap(t);
        if (map != null) {
        //将自己作为key取值
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                return (T)e.value;
            }
        }
        return setInitialValue();
    }
```



### 线性探测法

ThreadLocalMap是典型的使用线性探测法解决hash冲突的：发生冲突，从该位置向后找到表中的下一个空槽放入。这种简单的方法会导致相同hash值的元素挨在一起和其他hash值对应的槽被占用。

```java
private void set(ThreadLocal<?> key, Object value) {
......
//发生冲突，循环去找下一个为空的槽
//nextIndex()方法就是在长度范围内做i++的操作
 for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                ThreadLocal<?> k = e.get();
				//key是一样就更新
                if (k == key) {
                    e.value = value;
                    return;
                }
			//找到下一个空槽就放进去
                if (k == null) {
                    replaceStaleEntry(key, value, i);
                    return;
                }
            }
```



### 弱引用

总所周知，只要还有引用指向，这个对象就不会被回收。这是针对强引用而言。如果一个类继承了软引用指向实例，如果这个实例没有其他引用了，只有该类引用了，这个对象会被GC立即回收。

```
A a = new A();
B b = new B(a);
a = null;//这样a是不会被回收是，因为b还依赖a，造成了内存泄漏。

b = null;//此时，a才会被回收，因为没有引用指向了
```

如果b还有有用，不能赋值为空，岂非a一直不能被回收？非也，我们还可以用弱引用。

```
A a = new A();
WeakReference b = new WeakReference(a);
a=null//GC会立刻回收A这个对象
```

顺便说一嘴，软引用和弱引用一样, 但被GC回收的时候需要多一个条件: 当系统内存不足时才会被回收. 正因为有这个特性, 软引用比弱引用更加适合做缓存对象的引用。

**ThreadLocal使用了弱引用**.key指向ThreadLocal实例，当ThreadLocal外部强引用被回收时候，key虽然还是指向ThreadLocal，但因为是弱引用，GC会发现并回收。

```
     static class Entry extends WeakReference<ThreadLocal<?>> {
            /** The value associated with this ThreadLocal. */
            Object value;

            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
```

### 清理时机

线程退出时，会做一个清理工作，其中就包括清理ThreadLocalMap，即把threadLocals=null。

而然使用线程池会对线程进行**复用**，就意味当前线程未必会退出，可能会出现内存泄露，即你不用这个对象了，但它无法被回收。因此最好使ThreadLocal.remove()方法将这个变量移除。

> 参考
>
> 《Java高并发程序设计》
>
> [解决Hash冲突的几种方法](https://blog.csdn.net/u012104435/article/details/47951357)
>
> [ThreadLocal和弱引用](https://www.jianshu.com/p/d3e1282ba7ca)



​     