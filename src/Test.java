import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Test {
    private static final int MAX_NUM = 2;
    private static final int VARS_NUM = 3;
    private static final int CLAUSES_NUM = 4;
    Timer timer;
    public static File inFolder = new File("./input 2");
    public static File outFile = new File("./output/out2.txt");
    public static Controller controller = new Controller();

    public Test(int seconds) {
        timer = new Timer();
        timer.schedule(new RemindTask(), seconds * 1000);
    }

    public static ArrayList<String> listFilesForFolder(final File folder) throws TimeoutException, ParseFormatException, ContradictionException, IOException {
        ArrayList<String> resultList = new ArrayList<>();
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
                        long time = 0;
                        long t1 = System.currentTimeMillis();
                        List<Long> res = controller.encode(fileEntry);
                        long t2 = System.currentTimeMillis();
                        time += (t2 - t1);
                        System.out.println("\nTotal Time: " + time + " ms");
                        System.out.println("--------------------------------");
                        fileInfo += fileName + " " + res.get(MAX_NUM) + " " + res.get(VARS_NUM) + " " + res.get(CLAUSES_NUM) + " " + time;
                    }
                    resultList.add(fileInfo);
                }
            }
        }
        return resultList;
    }

    public static void main(String[] args) throws IOException, ParseFormatException, TimeoutException, ContradictionException {
        //new Test(300);
        ArrayList<String> resultList = listFilesForFolder(inFolder);
        outputToTxt(resultList);

    }

    private static void outputToTxt(ArrayList<String> resultList) {
        try {
            FileWriter writer = new FileWriter(outFile, true);
//            writer.write("Propagation:\n");
            for (String line : resultList) {
                writer.write(line + "\n");
            }
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
