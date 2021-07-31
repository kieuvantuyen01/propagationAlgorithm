import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Test {
    private static final int ROWS = 0;
    private static final int COLS = 1;
    private static final int MAX_NUM = 2;
    private static final int VARS_NUM = 3;
    private static final int CLAUSES_NUM = 4;
    static String inputFolderPath1 = "./input 2";
    static String inputFolderPath2 = "E:\\Lab\\TC";
    public static File inFolder = new File(inputFolderPath2);
    public static File outFile = new File("./output/out2.txt");
    //    public static File reformatFolder = new File("./input");
    public static Controller controller = new Controller();
    static List<Long> res;

    public static void listFilesForFolder(final File folder) throws InterruptedException {
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
                        ExecutorService executor = Executors.newFixedThreadPool(4);

                        Future<?> future = executor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    controller.encode(fileEntry);            //        <-- your job
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (TimeoutException e) {
                                    e.printStackTrace();
                                } catch (ParseFormatException e) {
                                    e.printStackTrace();
                                } catch (ContradictionException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        executor.shutdown();            //        <-- reject all further submissions

                        try {
                            future.get(600, TimeUnit.SECONDS);  //     <-- wait seconds to finish
                        } catch (InterruptedException e) {    //     <-- possible error cases
                            System.out.println("job was interrupted");
                        } catch (ExecutionException e) {
                            System.out.println("caught exception: " + e.getCause());
                        } catch (java.util.concurrent.TimeoutException e) {
                            future.cancel(true);              //     <-- interrupt the job
                            System.out.println("timeout");
                            //executor.shutdownNow();
                            executor.shutdown();
                        }

                        // wait all unfinished tasks for sec
                        if(!executor.awaitTermination(1, TimeUnit.SECONDS)){
                            // force them to quit by interrupting
                            executor.shutdownNow();
                        }
                        long t2 = System.currentTimeMillis();
                        long time = (t2 - t1);
                        res = Controller.inFoList();
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

    public static void main(String[] args) throws InterruptedException {
        //new Test(300);
        listFilesForFolder(inFolder);
        //reformatInput(reformatFolder);
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
}