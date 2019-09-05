线程池

[TOC]



# 使用线程池的好处

1. **降低资源销毁**。不用频繁的创建销毁线程，线程可以循环重复使用。
2. **提高响应速度**。每当任务到达时，无需创建新线程。
3. **提高线程的可管理性**。线程池可以统一分配、调优和监控。可以根据系统的承受能力，调整线程的数量，防止因为消耗过多内存导致服务器崩溃。

# 创建线程

创建线程池有两种方法。一、构造方法创建。二、通过Executor框架的工具类Executors实现。

## 一、构造方法创建

>     public ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,BlockingQueue<Runnable> workQueue,**ThreadFactory threadFactory,RejectedExecutionHandler handler**);

### 重要参数

这里使用了上面构造方法的第一种进行创建一个线程池

```
ThreadPoolExecutor threadPool= new ThreadPoolExecutor(
        10, 15, 60, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(1024));
```

第一个参数corePoolSize=10 表示这个线程池初始化了10个线程在里面工作
第二个参数maximumPoolSize=15 表示如果10个线程不够用了，就会自动增加到最多15个线程
第三个参数keepAliveTime=60 结合第四个参数TimeUnit.SECONDS，表示经过60秒，多出来的线程还没有接到活儿，就会回收，最后保持池子里就10个
第四个参数TimeUnit.SECONDS 第三参数的单位为秒，有7种静态属性。
第五个参数 new LinkedBlockingQueue() 用来放任务的集合。有三个选择   

- ArrayBlockingQueue;
- LinkedBlockingQueue;
- SynchronousQueue;

### 拒绝策略

handler：表示当处理任务时的四种拒绝策略：

- ThreadPoolExecutor.AbortPolicy:丢弃任务并**抛出RejectedExecutionException异常**。
- ThreadPoolExecutor.DiscardPolicy：丢弃任务（**当前将要加入队列的任务**），但是不抛出异常。
- ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列**最老的任务**，然后重新尝试执行任务，即重复此过程。
- ThreadPoolExecutor.CallerRunsPolicy：**由调用线程处理该任务**

## 二、通过Executor框架的工具类Executors实现

- newCachedThreadPool创建一个**可缓存线程池**，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。适合处理短时间工作任务。
- newFixedThreadPool 创建一个**定长线程池**，可控制线程最大并发数，超出的线程会在队列中等待。
- newScheduledThreadPool 创建一个**定长线程池**，支持**定时或者周期性**任务执行。
- newSingleThreadExecutor 创建一个**单线程化**的线程池，它只会用唯一的工作线程来执行任务，如何有异常结束，会有另一个线程去取代它。保证所有任务**按照指定顺序**(FIFO, LIFO, 优先级)执行。
- newWorkStealingPool:1.8版本出现，利用working-stealing算法，**可窃取任务，并行处理**，需要穿一个并行级别的参数，如果不传，则被设定为默认的CPU数量。

不要使用Executors**默认**创建线程池的方式，这可能会导致OOM，因为LinkedBlockingQueue时未指定容量，将是一个无边界的阻塞队列，最大长度为Integer.MAX_VALUE。是可以不断的向队列中加入任务的，这种情况下就有可能因为任务过多而导致内存溢出问题。
上面提到的问题主要体现在newFixedThreadPool和newSingleThreadExecutor两个工厂方法上，并不是说newCachedThreadPool和newScheduledThreadPool这两个方法就安全了，这两种方式默认创建的最大线程数可能是Integer.MAX_VALUE，而创建这么多线程，必然就有可能导致OOM。

# 线程的创建时机

- **线程池中的核心线程数，当提交一个任务时，线程池创建一个新线程执行任务，直到当前线程数等于corePoolSize；**
- **如果当前线程数为corePoolSize，继续提交的任务被保存到阻塞队列中，等待被执行；**
- **如果阻塞队列满了，那就创建新的线程执行当前任务；**
- **直到线程池中的线程数达到maxPoolSize,这时再有任务来，只能执行reject()处理该任务**。

# 如何合理配置线程池的大小？

如果是CPU密集型任务，就需要尽量压榨CPU，参考值可以设为 **NCPU+1**
如果是IO密集型任务（比如数据库数据交互、文件上传下载、网络数据传输等等），参考值可以设置为**2*NCPU**
*tips：这行代码可以查看Ncpu
`System.out.println(Runtime.getRuntime().availableProcessors());//输出运行时可用处理器
`*

# 源 码分析

## 线程池的重要方法

### execute()接受任务

```java
public void execute(Runnable command) {
    if (command == null)
        throw new NullPointerException();
    // 表示 “线程池状态” 和 “线程数” 的整数
    int c = ctl.get();
     //分为3种情况
    // 情况1：如果当前线程数少于corePoolSize，直接新创建一个 worker 线程，并把当前 command 作为这个线程firstTask
    if (workerCountOf(c) < corePoolSize) {
        if (addWorker(command, true))
            // 添加任务成功
            return;
          // 返回 false 代表线程池不允许提交任务
        c = ctl.get();
    }
    // 情况2：要么当前线程数大于等于corePoolSize，要么刚刚 addWorker 失败了
    // 如果线程池处于 RUNNING 状态，把这个任务添加到任务队列 workQueue 中
    if (isRunning(c) && workQueue.offer(command)) {
        int recheck = ctl.get();
        //下面这个分支的意图是：担心任务提交到队列中了，但是线程都关闭了
        // 如果线程池已不处于 RUNNING 状态，那么移除已经入队的这个任务，并且执行拒绝策略
        if (! isRunning(recheck) && remove(command))
            reject(command);
        // 如果线程池还是 RUNNING 的，并且线程数为 0，那么开启新的线程
        else if (workerCountOf(recheck) == 0)
            addWorker(null, false);
    }
    // 情况3:如果 workQueue 队列满了，以 maximumPoolSize 为界创建新的 worker，
    else if (!addWorker(command, false))
         // 如果失败，说明当前线程数已经达到 maximumPoolSize，执行拒绝策略
        reject(command);
}
```



解释一下这行代码，  这个方法会返回一个表示 “线程池状态” 和 “线程数” 的整数。



```java
//一开始的状态是ctlOf(RUNNING, 0)
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));

//  COUNT_BITS 设置为 29(32-3)，意味着前三位用于存放线程状态，后29位用于存放线程数
private static final int COUNT_BITS = Integer.SIZE - 3;

// 000 11111111111111111111111111111
// 这里得到的是 29 个 1，也就是说线程池的最大线程数是 2^29-1=536870911
private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

// 运算结果为 111跟29个0：111 00000000000000000000000000000
private static final int RUNNING    = -1 << COUNT_BITS;
// 000 00000000000000000000000000000
private static final int SHUTDOWN   =  0 << COUNT_BITS;
// 001 00000000000000000000000000000
private static final int STOP       =  1 << COUNT_BITS;
// 010 00000000000000000000000000000
private static final int TIDYING    =  2 << COUNT_BITS;
// 011 00000000000000000000000000000
private static final int TERMINATED =  3 << COUNT_BITS;

// 将整数 c 的低 29 位修改为 0，就得到了线程池的状态
private static int runStateOf(int c)     { return c & ~CAPACITY; }
// 将整数 c 的高 3 为修改为 0，就得到了线程池中的线程数
private static int workerCountOf(int c)  { return c & CAPACITY; }
```

线程池的状态

AtomicInteger变量ctl的功能非常强大：利用低29位表示线程池中线程数，通过高3位表示线程池的运行状态：
1、**RUNNING**：-1 << COUNT_BITS，即高3位为111，该状态的线程池会接收新任务，并处理阻塞队列中的任务；
2、**SHUTDOWN**： 0 << COUNT_BITS，即高3位为000，该状态的线程池**不会接收新任务，但会处理阻塞队列中的任务；**
3、**STOP** ： 1 << COUNT_BITS，即高3位为001，该状态的线程不**会接收新任务，也不会处理阻塞队列中的任务，而且会中断正在运行的任务；**
4、**TIDYING** ： 2 << COUNT_BITS，即高3位为010，该状态表示线程池对线程进行整理优化；所有的任务都销毁了，workCount 为 0。线程池的状态在转换为 TIDYING 状态时，会执行钩子方法 terminated()
5、**TERMINATED**： 3 << COUNT_BITS，即高3位为011，该状态表示线程池停止工作；terminated() 方法结束后，线程池的状态就会变成这个。



重点区分SHUTDOWN和STOP。

再提一下关闭线程池的方法。

shutdown()：设置 线程池的状态 为 SHUTDOWN，然后中断所有没有正在执行任务的线程

shutdownNow()：设置 线程池的状态 为 STOP，然后尝试停止所有的正在执行或暂停任务的线程，并返回等待执行任务的列表
***使用建议：一般调用shutdown（）关闭线程池；若任务不一定要执行完，则调用shutdownNow（）***

### addWorker()方法：增加工作线程

```java
// 参数一：第一个任务。参数二：是否使用核心线程数 corePoolSize 作为创建线程的界限
private boolean addWorker(Runnable firstTask, boolean core) {
    //打破多重循环的关键字retry
    retry:
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        //下面代码的意图：担心创建线程的时候，线程池已经关闭了
        // 如满足以下条件之一，那么不创建新的 worker：
        // 1. 线程池状态大于 SHUTDOWN，其实也就是 STOP, TIDYING, 或 TERMINATED
        // 2. firstTask != null
        // 3. workQueue.isEmpty()
        //  SHUTDOWN 的语义：不允许提交新的任务，但是要把已经进入到 workQueue 的任务执行完，所以在满足条件的基础上，是允许创建新的 Worker 的
        if (rs >= SHUTDOWN &&
            ! (rs == SHUTDOWN &&
               firstTask == null &&
               ! workQueue.isEmpty()))
            return false;

        for (;;) {
            int wc = workerCountOf(c);
            if (wc >= CAPACITY ||
                wc >= (core ? corePoolSize : maximumPoolSize))
                return false;
            // 如果成功，那么就是所有创建线程前的条件校验都满足了，准备创建线程执行任务了
            // 这里失败的话，说明有其他线程也在尝试往线程池中创建线程
            if (compareAndIncrementWorkerCount(c))
                //跳出多重循环
                break retry;
            // 由于有并发，重新再读取一下 ctl
            c = ctl.get();
            // 正常如果是 CAS 失败的话，进到下一个里层的for循环就可以了
            // 可是如果是因为其他线程导致线程池的状态发生了变更，比如有其他线程关闭了这个线程池
            // 那么需要回到外层的for循环
            if (runStateOf(c) != rs)
                continue retry;
            // else CAS failed due to workerCount change; retry inner loop
        }
    }

    /*
     * 啊终于可以开始创建线程了
     */

    // worker 是否已经启动
    boolean workerStarted = false;
    // 是否已将这个 worker 添加到 workers 这个 HashSet 中
    boolean workerAdded = false;
    Worker w = null;
    try {
        final ReentrantLock mainLock = this.mainLock;
        // 把 firstTask 传给 worker 的构造方法
        w = new Worker(firstTask);
        // 取 worker 中的线程对象，Worker的构造方法会调用 ThreadFactory 来创建一个新的线程
        final Thread t = w.thread;
        if (t != null) {
            // 这个是整个线程池的全局锁，持有这个锁才能让下面的操作“顺理成章”，
            // 因为关闭一个线程池需要这个锁，至少我持有锁的期间，线程池不会被关闭
            mainLock.lock();
            try {

                int c = ctl.get();
                int rs = runStateOf(c);

                // 小于 SHUTTDOWN 那就是 RUNNING
                // 如果等于 SHUTDOWN，不接受新的任务，但是会继续执行等待队列中的任务
                if (rs < SHUTDOWN ||
                    (rs == SHUTDOWN && firstTask == null)) {
                    // worker 里面的 thread 可不能是已经启动的
                    if (t.isAlive())
                        throw new IllegalThreadStateException();
                    // 加到 workers 这个 HashSet 中
                    workers.add(w);
                    int s = workers.size();
                    // largestPoolSize 用于记录 workers 中的个数的最大值
                    // 因为 workers 是不断增加减少的，通过这个值可以知道线程池的大小曾经达到的最大值
                    if (s > largestPoolSize)
                        largestPoolSize = s;
                    workerAdded = true;
                }
            } finally {
                mainLock.unlock();
            }
            // 添加成功的话，启动这个线程
            if (workerAdded) {
                t.start();
                workerStarted = true;
            }
        }
    } finally {
        // 如果线程没有启动，需要做一些清理工作，如前面 workCount 加了 1，将其减掉
        if (! workerStarted)
            addWorkerFailed(w);
    }
    // 返回线程是否启动成功
    return workerStarted;
}
```



> 参考博文
> [这篇比较详细，还有源码解读](https://www.cnblogs.com/dolphin0520/p/3932921.html)
> [关于ThreadFactory与BlockingQueue](https://www.cnblogs.com/leipDao/p/8436380.html)

