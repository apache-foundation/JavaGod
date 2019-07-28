import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class A {
    static final int ArrayLength = 20000;

    public static Long Get(int ThreadNum) throws InterruptedException {
        ArrayList<Integer> array = new ArrayList<>();

        for (int i = 0; i < ArrayLength; i++) {
            int num = (int)(Math.random() * 100000);
            if (!array.contains(num))
                array.add(num);
        }

        long startTime=System.currentTimeMillis();   //获取开始时间

        ForkJoinPool fjp = new ForkJoinPool(ThreadNum); // 最大并发数40
        SortTask sortTask = new SortTask(array.toArray());
        int[] a = fjp.invoke(sortTask);

        long endTime=System.currentTimeMillis(); //获取结束时间

        return endTime - startTime;

    }
}

class SortTask extends RecursiveTask<int[]> {
    static final int THRESHOLD = 2;
    private int[] array; //要排序的数组
    private int length;

    public SortTask(Object[] array) {
        this.length = array.length;
        this.array = new int[length];
        for (int i = 0; i < length; i++) {
            this.array[i] = (int) array[i];
        }
    }

    @Override
    protected int[] compute() {
        if (length <= 2) {
            if (length == 2 && array[0] > array[1]) {
                int temp = array[0];
                array[0] = array[1];
                array[1] = temp;
                return array;
            } else {
                return array;
            }
        } else {
            ArrayList<Integer> array1 = new ArrayList<>();
            ArrayList<Integer> array2 = new ArrayList<>();
            int mid = 0;

            do {
                array1.clear();
                array2.clear();
                int middle = array[mid];

                for (int i = 0; i < length; i++) {
                    if (array[i] < middle) array1.add(array[i]);
                    else                   array2.add(array[i]);
                }
                mid++;
            } while ((array1.size() == 0 || array2.size() == 0) && mid < length);

            SortTask sortTask1 = new SortTask(array1.toArray());
            SortTask sortTask2 = new SortTask(array2.toArray());

            invokeAll(sortTask1, sortTask2); //这必须用 invokeAll 要是用那个 fork 就有一个线程不干了

            int[] a1 = sortTask1.join();
            int[] a2 = sortTask2.join();

            System.arraycopy(a1, 0, array, 0, a1.length);
            System.arraycopy(a2, 0, array, a1.length, a2.length);

            return array;
        }
    }
}