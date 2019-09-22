
/**
 * @description:两个线程分别执行syc静态方法和非静态方法，会不会阻塞？
 * 如果阻塞，则过5秒后第二个线程的执行语句才会输出。否则，两者同时输出。run试试吧
 * @author: wangxuanni
 * @create: 2019-09-22 10:04
 **/
public class SynchronizedTest extends Thread {
    public static void main(String[] args) {
        final SynchronizedTest t = new SynchronizedTest();
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                t.aNonStatic();
            }
        });
        t1.setName("exec-NonStaticThread");

        t1.start();
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                aStatic();
            }
        });
        t2.setName("exec-StaticThread");
        t2.start();
    }

    public synchronized void aNonStatic() {

        System.out.print(Thread.currentThread().getName()+"->execute");
        System.out.println("");
        try {
            Thread.sleep(5000);
            System.out.println(Thread.currentThread().getName()+" time...");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }
    public static synchronized void aStatic() {

        System.out.print(Thread.currentThread().getName()+"->execute");
        System.out.println("");
        try {
            Thread.sleep(5000);
            System.out.println(Thread.currentThread().getName()+" time...");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}