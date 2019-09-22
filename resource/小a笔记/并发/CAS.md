内容介绍

[TOC]

# CAS



## 什么是乐观锁和悲观锁？cas怎么解决更新变量时线程不安全的问题？

众所周知，悲观锁的先加锁在操作，因为悲观锁悲观的认为一定会有别的线程来修改数据。而乐观锁不同，它先操作，在判断数据是否被修改，修改就重试或者抛出异常，因为乐观锁乐观的认为不会有别的线程来修改数据（有句谚语：原谅比准许更容易得到。）。它两的**关键区别在于是先加锁还是先操作**。Java实现乐观锁最常采用的是CAS无锁算法。

因为更新的值是原值算出来的，关键在于**如果原值已经被更改了，那么自增的更新值肯定也不对了。**最后一步要赋值的时候，把主内存的值与之前没计算的值做比较，相等一步到位把值更新了，不相等就放弃计算出来的值。



在多线程更新一个变量的情况下，可能会出现这样的问题，当a线程拷贝了一份主内存的变量到工作内存进行计算的时候，这个变量已经其他线程改变了，a做完了计算，此时的结果的错误的，如果刷新到主内存会覆盖中间其他线程的改变，因此我们希望出现这种情况的时候，**a能检测到此时的数据已经变脏，并把这个计算结果丢弃，重新计算，这就是cas。**

缺点和使用场景

## CAS的关键是什么？

关键在于，比较与交换这个**两个步骤**，在操作系统中对应的是**一条处理器指令**——CMPXCHG指令，而它是原子的！

两个步骤是怎么做到原子的呢？当然是加锁啦，CAS通过LOCK CMPXCHG`（带`LOCK`前缀）来加锁。是不是有点晕了，说好的CAS是无锁的呢！其实，无锁算法的并不是完全消除同步、完全不加锁，只是它将同步减少到**CPU本身提供的单个指令原子操作**。

无锁指的是不加high-level locks。high-level locks指可以保证任何时间只有一个线程操作数据的那种锁，想做到这个地步，它必须将其他线程挂起，将一个线程挂起大概花费**8万个时钟周期**，而且它还需要维护一个线程的等待队列（就像AQS所做的一样）。这与CPU 的LOCK前缀功能完全不同，LOCK前缀仅保护**单个指令原子性**，因此可能仅在该单个指令的持续时间内保留其他线程。这是由CPU本身实现的，一条指令大概花费**3个时钟周期**。当然CAS失败之后需要重试，但不论如何都比直接挂起线程效率高。

可以想想，悲观锁在操作的过程一直持有锁。而乐观锁操作的过程是没有加锁的，其他线程也可以修改这个数据，乐观锁只有在最后的比较交换这条cpu指令加锁检查是否出现不一致。

[如果还不理解可以看看stackoverflow的这个回答](https://stackoverflow.com/questions/27837731/is-x86-cmpxchg-atomic-if-so-why-does-it-need-lock)





CAS全称 Compare And Swap（比较与交换）

它的问题

1. ABA问题

   CAS需要在操作值的时候检查内存值是否发生变化，没有发生变化才会更新内存值。但是如果内存值原来是A，后来变成了B，然后又变成了A，那么CAS进行检查时会发现值没有发生变化，但是实际上是有变化的。ABA问题的解决思路就是在变量前面添加版本号，每次变量更新的时候都把版本号加一，这样变化过程就从“A－B－A”变成了“1A－2B－3A”。

   - JDK从1.5开始提供了AtomicStampedReference类来解决ABA问题，具体操作封装在compareAndSet()中。compareAndSet()首先检查当前引用和当前标志与预期引用和预期标志是否相等，如果都相等，则以原子方式将引用值和标志的值设置为给定的更新值。

2. **循环时间长开销大**。CAS操作如果长时间不成功，会导致其一直自旋，给CPU带来非常大的开销。

# volatile+Unsafe

value是一个volatile变量，因此JVM可以保证任何时刻任何线程都能拿到该变量的最新值。

```
private volatile int value;
```



整个atomic都是基于`Unsafe`实现的，这个类里都是native本地方法。Unsafe通过通过单例模式来提供实例对象。

```java
public final native boolean compareAndSwapInt(
            Object o, long offset, int expected, int x);
```

这个方法可以在获得对象o的**内存偏移量**offset后与期望值比较，如果等于期望值，就更新为x。

一个对象的属性=该对象在内存当中的偏移量，这样我们就可以根据这个偏移量在对象内存当中找到这个属性。这直接操作内存的。

**为什么要直接操作内存呢？因为快呀。尤其当被修改的属性是一个对象的时候。**

*插一嘴题外话，为什么它叫unsafe类？因为它封装了一些计算指针偏移量的操作，这种指针操作是很危险的，指针操作不当有可能覆盖别人的内存导致系统崩溃。Java抛弃了指针，jdk是不许用户应用程序获取Unsafe这个类的实例。在获得唯一可以Unsafe类实例的工厂方法里，它会检查调用类的类加载器如果不为null，就抛出异常拒绝工作。而只有Bootstrap类加载器会返回null。也就是说我们自己写的java类是无法直接使用Unsafe类（除非用反射）*

这里我们主要关注它提供的几个方法：

# 12个类

## 原子更新基本类型3个

- 
  AtomicBoolean
- AtomicInteger
- AtomicLong

1.7,1.8 的实现方式有一丝丝的不同，1.7看似更好理解，这里以1.7为例。

```java
    public final int getAndIncrement() {
        for (;;) {
            int current = get();
            int next = current + 1;
            if (compareAndSet(current, next))
                return current;
        }
    }
```

这是我们熟悉的CAS算法套路：

- 循环重试，如果下一步的CAS更新失败，说明有线程修改了当前值。没关系。舍弃失败的操作，再次循环直达成功
- 获取当前值进行加1 操作
- 调用compareAndSet方法原子更新操作

compareAndSet调用unsafe类的compareAndSwapInt**本地方法**

- unsafe： 获取并操作内存的数据。
- valueOffset： 存储value在AtomicInteger中的偏移量。
- value： 存储AtomicInteger的int值，该属性需要借助volatile关键字保证其在线程间是可见的。

```
public final boolean compareAndSet(int expect, int update) {
//unsafe.compareAndSwapInt为native方法
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }
```

compareAndSwapInt本地方法

```
 public final native boolean compareAndSwapInt(Object var1, long var2, int var4, int var5);
```

## 原子更新数组3个

- AtomicIntegerArray
- AtomicLongArray
- AtomicReferenceArray

## 原子更新引用类型3个

- AtomicReference：原子更新引用类型
- AtomicMarkableReference：原子更新带有标记位的引用类型。
- AtomicStampedReference：原子更新带有**版本号**的引用类型。

特别说一下AtomicStampedReference，它与原子引用不一样的地方在于，它使用版本号解决ABA问题。它调用比较交换方法时除了传入原值和要更新的值，还需要传入原版本号和版本号加一。这样每次变量更新的时候都把版本号加一，这样变化过程就从“A－B－A”变成了“1A－2B－3A”。

## 原子更新器3个

可以让普通的变量也享受CAS操作。

- AtomicIntegerFieldUpdater：原子更新整型字段

- AtomicLongFieldUpdater：原子更新长整型字段

- AtomicReferenceFieldUpdater：原子更新引用类型里的字段

  

实例代码

```java
   //创建一个原子更新器
    private static AtomicIntegerFieldUpdater<User> atomicIntegerFieldUpdater =
            AtomicIntegerFieldUpdater.newUpdater(User.class,"old");

    public static void main(String[] args){
      	volatile  User user = new User("Tom",15);
        //原来的年龄
        System.out.println(atomicIntegerFieldUpdater.getAndIncrement(user));
        //现在的年龄
        System.out.println(atomicIntegerFieldUpdater.get(user));
    }

```



集合

默认容量



hashmap KV散列表

实现1.7数组+双向链表，数组中的每一个元素是一个链表。结合数组和链表的优点。1.8红黑树优化，树化的阈值和最小树化容量，get方法key允许为空

负载因子0.75扩容

位操作：2的整次幂、扰动函数、链表的复制

线程不安全的：更新丢失和链表死循环、集合类的s和hashtable。chm

与hm：null、安全、字典和map



