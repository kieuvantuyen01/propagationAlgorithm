import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.io.*;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test {
    public static File folder = new File("E:\\Lab\\Test cases");
    static File outFile = new File("E:\\Lab\\Output");

    static String temp = "";

    public static double listFilesForFolder(final File folder) throws TimeoutException, ParseFormatException, ContradictionException, IOException {
        long totalTime = 0;
        int m = 0;

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                if (fileEntry.isFile()) {
                    m++;
                    temp = fileEntry.getName();
                    if ((temp.substring(temp.lastIndexOf('.') + 1).toLowerCase()).equals("in")) {
                        long time = 0;
                        long t1 = System.currentTimeMillis();
                        int n = Controller.main(fileEntry);
                        long t2 = System.currentTimeMillis();
                        time += (t2 - t1);
                        totalTime += time;
                        System.out.println("\nTotal Time: " + time + " ms");
                        for (int i = 0; i < 2 * n - 1; i++) {
                            System.out.print('*');
                        }
                        System.out.println();
                    }
                }
            }
        }
        double averageTime = 1.0 * totalTime / m;

        System.out.println("There are total: " + m + " files in this Folder Test cases");
        System.out.println("Average Time: " + averageTime + " ms");
        return averageTime;
    }

    public static void main(String[] args) throws IOException, ParseFormatException, TimeoutException, ContradictionException {

        double averageTime = listFilesForFolder(folder);
        try {
            FileOutputStream fos = new FileOutputStream("E:\\Lab\\Output\\out.txt", true);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeUTF("SAT Encoding: ");
            dos.writeUTF("" + averageTime);
            dos.writeChar('\n');
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
