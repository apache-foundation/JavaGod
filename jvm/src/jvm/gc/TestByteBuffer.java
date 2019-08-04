package jvm.gc;

import java.nio.ByteBuffer;

/**
 * @Author: xiantang
 * @Date: 2019/7/14 11:13
 */
public class TestByteBuffer {
    public static void main(String[] args) {
       while (true) {
           ByteBuffer buffer = ByteBuffer.allocate(10 * 1024 * 1024);
        }
    }
}
