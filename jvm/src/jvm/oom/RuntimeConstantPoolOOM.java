package jvm.oom;


public class RuntimeConstantPoolOOM {
    /**
     * JDK 1.6 方法会把首次遇到的字符串实例复制
     * 到永久代中，返回的也是永久代中这个字符串实例的引用
     * java 这个字符串在执行的时候已经出现过了不符合首次出现的原则
     */
    public static void main(String[] args) {
//        List<String> list = new ArrayList<String>();
//        int i = 0;
//        while (true) {
//            list.add(String.valueOf(i++).intern());
//        }

        String str1 = new StringBuilder("计算机").append("软件").toString();
        System.out.println(str1.intern() == str1);
        String str2 = new StringBuilder("ja").append("va").toString();
        System.out.println(str2.intern() == str2);
    }

}
