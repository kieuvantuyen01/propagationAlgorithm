import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Test {
    Timer timer;
    public Test(int seconds) {
        timer = new Timer();
        timer.schedule(new RemindTask(), seconds * 1000);
    }
    public static File folder = new File("E:\\Lab\\Test cases");
    static File outFile = new File("E:\\Lab\\Output");

    static String temp = "";
    static List<Long> res;
    public static double[] listFilesForFolder(final File folder) throws TimeoutException, ParseFormatException, ContradictionException, IOException {
        long totalTime = 0, totalClause = 0, totalVars = 0;
        int m = 0;
        double tmp[] = new double[3];


        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {

                listFilesForFolder(fileEntry);
            } else {
                if (fileEntry.isFile()) {
                    m++;
                    System.out.println("This was " + m + "th Test case");
                    temp = fileEntry.getName();
                    if ((temp.substring(temp.lastIndexOf('.') + 1).toLowerCase()).equals("in")) {
                        long time = 0;
                        long t1 = System.currentTimeMillis();
                        res = Controller.main(fileEntry);
                        long t2 = System.currentTimeMillis();
                        time += (t2 - t1);
                        totalTime += time;
                        totalClause += res.get(1);
                        totalVars += res.get(2);
                        System.out.println("\nTotal Time: " + time + " ms");
                    }
                }
            }
        }
        double averageTime = 1.0 * totalTime / m;
        tmp[0] = averageTime;
        double averageClause = 1.0 * totalClause / m;
        tmp[1] = averageClause;
        double averageVar = 1.0 * totalVars / m;
        tmp[2] = averageVar;

        System.out.println("There are total: " + m + " files in this Folder Test cases");
        System.out.println("Average Time: " + averageTime + " ms");
        System.out.println("Average Clause Numbers: " + averageClause);
        System.out.println("Average Var Numbers: " + averageVar);
        return tmp;
    }

    public static void main(String[] args) throws IOException, ParseFormatException, TimeoutException, ContradictionException {
        //new Test(300);
        double[] result = listFilesForFolder(folder);
        double averageTime = result[0];
        double averageClause = result[1];
        double averageVar = result[2];
        try {
            FileOutputStream fos = new FileOutputStream("E:\\Lab\\Output\\out2.txt", true);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeUTF("Propagation: ");
            dos.writeUTF("" + res.get(0) + " " + averageVar + " " + averageClause + " " + averageTime);
            dos.writeChar('\n');
            dos.flush();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class RemindTask extends TimerTask {
        @Override
        public void run() {
            System.out.format("Time's up");
            System.exit(0);
            timer.cancel();
        }
    }
}
