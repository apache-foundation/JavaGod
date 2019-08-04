package jvm.oom;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 *VM Args：-Xmx20M-XX：MaxDirectMemorySize=10M
 *@author zzm
 */
public class MaxDirectMemoryOOM {
    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) throws IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        int count = 0;

        while (true) {
            unsafe.allocateMemory(_1MB);
            count++;
            System.out.println(count);
        }
    }
}
