import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class OtherTest {
    private static int linenums = 0;

    public static void main(String[] args) throws IOException {
        countLine(new File("C:\\Users\\26010\\Downloads\\netty-4.1\\game"));
        System.out.println(linenums);
    }

    public static void countLine(File file) throws IOException {
        if (!file.isDirectory()) {
            if (file.getName().endsWith(".java") || file.getName().endsWith(".html")) {
                FileReader fr = new FileReader(file);
                char[] chars = new char[1024];
                int len = 0;
                while ((len = fr.read(chars)) != -1) {
                    for (int i = 0; i < len; i++) {
                        if (chars[i] == '\n')
                            linenums++;
                    }
                }
            }
        } else {
            for (File file1 : file.listFiles()){
                countLine(file1);
            }
        }
    }
}
