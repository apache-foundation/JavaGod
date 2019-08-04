package jvm.gc;

/**
 * -XX:+PrintGCDetails
 * 对象objA和objB都有字段
 * instance，赋值令objA.instance=objB及objB.instance=objA，除此之外，这两个对象再无任何引
 * 用，实际上这两个对象已经不可能再被访问，但是它们因为互相引用着对方，导致它们的引
 * 用计数都不为0，于是引用计数算法无法通知GC收集器回收它们。
 */
public class ReferenceCountingGC {
    public Object instance = null;
    private static final int _1MB = 1024 * 1024;
    private byte[] bigSize = new byte[2 * _1MB];

    public static void main(String[] args) {
        ReferenceCountingGC a = new ReferenceCountingGC();
        ReferenceCountingGC b = new ReferenceCountingGC();
        a.instance = b;
        b.instance = a;
        a = null;
        b = null;
        System.gc();

    }
}
