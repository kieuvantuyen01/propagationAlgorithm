import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Test {
    private static final int ROWS = 0;
    private static final int COLS = 1;
    private static final int MAX_NUM = 2;
    private static final int VARS_NUM = 3;
    private static final int CLAUSES_NUM = 4;
    Timer timer;
    static String inputFolderPath1 = "./input 2";
    static String inputFolderPath2 = "E:\\Lab\\TC";
    public static File inFolder = new File(inputFolderPath1);
    public static File outFile = new File("./output/out2.txt");
    public static Controller controller = new Controller();

    public Test(int seconds) {
        timer = new Timer();
        timer.schedule(new RemindTask(), seconds * 1000);
    }

    public static void listFilesForFolder(final File folder) throws TimeoutException, ParseFormatException, ContradictionException, IOException {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);

            } else {
                if (fileEntry.isFile()) {
                    String fileInfo = "";
                    String fileName = "";
                    fileName = fileEntry.getName();
                    if ((fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()).equals("in")) {
                        System.out.println(fileName);
                        long t1 = System.currentTimeMillis();
                        List<Long> res = controller.encode(fileEntry);
                        long t2 = System.currentTimeMillis();
                        long time = (t2 - t1);
                        System.out.println("\nTotal Time: " + time + " ms");
                        System.out.println("--------------------------------");
                        fileInfo += fileName + "\t" + res.get(ROWS) + "x" + res.get(COLS) + "\t" + res.get(MAX_NUM) + "\t"
                                + res.get(VARS_NUM) + "\t" + res.get(CLAUSES_NUM) + "\t" + time;
                    }
                    outputToTxt(fileInfo);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, ParseFormatException, TimeoutException, ContradictionException {
        //new Test(300);
        listFilesForFolder(inFolder);

    }

    private static void outputToTxt(String result) {
        try {
            FileWriter writer = new FileWriter(outFile, true);
//            writer.write("Propagation:\n");
            writer.write(result + "\n");
            writer.close();
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
