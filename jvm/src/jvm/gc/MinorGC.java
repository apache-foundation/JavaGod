package jvm.gc;

public class MinorGC {



    private static final int _1MB = 1024 * 1024;

    /**
     * -verbose:oom -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
     * -Xms20M、-Xmx20M、-Xmn10M这3个参数限制了Java堆大小为20MB
     * 10M 新生代 10 M老年代
     * -XX：SurvivorRatio=8 Eden 区和Survivor 8：1
     *   eden space 8192K, 78% used [0x00000000ff600000,0x00000000ffc50670,0x00000000ffe00000)
     *   from space 1024K, 72% used [0x00000000ffe00000,0x00000000ffeba020,0x00000000fff00000)
     *   to   space 1024K, 0% used [0x00000000fff00000,0x00000000fff00000,0x0000000100000000)
     */
    public static void testAllocation() {

        byte[] a1, a2, a3, a4;
        a1 = new byte[2 * _1MB];
        a2 = new byte[2 * _1MB];
        a3 = new byte[2 * _1MB];
        // Minor GC
        a4 = new byte[4 * _1MB];
    }

    public static void testPretenureSizeThreshold() {
        byte[] a;
        a = new byte[4 * _1MB];
    }

    /**
     *-verbose:oom -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=1 -XX:+PrintTenuringDistribution
     */
    public static void testTentringThreshold() {
        byte[] allocation1, allocation2, allocation3;
        allocation1 = new byte[_1MB / 4];
//什么时候进入老年代取决于XX：MaxTenuringThreshold设置
        allocation2 = new byte[4 * _1MB];
        allocation3 = new byte[4 * _1MB];
        allocation3 = null;
        allocation3 = new byte[4 * _1MB];
    }

    public static void main(String[] args) {
        testTentringThreshold();

    }

}
