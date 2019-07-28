/*
 * 没有同步的单例模式 
 */
public class SingleTon {
    private static SingleTon singleTon;
    private SingleTon() {}

    public static SingleTon getInstance() {
        if (singleTon == null) {
            singleTon = new SingleTon();
        }
        return singleTon;
    }
}

//以下是几种同步方法
public class SingleTon {
    private static SingleTon singleTon;
    private SingleTon() {}

    public synchronized static SingleTon getInstance() {
        if (singleTon == null) {
            singleTon = new SingleTon();
        }
        return singleTon;
    }
}

public class Singleton {
    private static class SingletonHolder {
        private static final Singleton INSTANCE = new Singleton();
    }

    private Singleton (){}

    public static final Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}

public class SingleTon {
    private static SingleTon singleTon = new SingleTon(); 
    private SingleTon() {}

    public static SingleTon getInstance() {
        if (singleTon == null) {
            singleTon = new SingleTon();
        }
        return singleTon;
    }
}

class SingleTon {
    private volatile static SingleTon singleTon;
    private SingleTon() {}

    public static SingleTon getInstance() {
        if (singleTon == null) {
            synchronized (SingleTon.class) {
                if (singleTon == null) {
                    singleTon = new SingleTon();
                }
            }
        }
        return singleTon;
    }
}