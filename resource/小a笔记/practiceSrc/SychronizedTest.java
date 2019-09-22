package resource.Ð¡a±Ê¼Ç.practiceSrc;

/**
 * @description:
 * @author: wangxuanni
 * @create: 2019-09-22 10:04
 **/

public class SychronizedTest extends Thread {
    public static void main(String[] args) {
        final SychronizedTest t = new SychronizedTest();
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
