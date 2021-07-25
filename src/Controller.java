import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.TimeoutException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controller {
    private static CNFConverter cnfConverter = new CNFConverter();
    private static SATSolver satSolver;


    public List<Long> encode(File file) throws IOException, TimeoutException, ParseFormatException, ContradictionException {
        List<Long> res = new ArrayList<>();
        Scanner sc = new Scanner(file);
        NumberLink numberLink = new NumberLink();
        numberLink.setRow(sc.nextInt());
        int rows = numberLink.getRow();
        numberLink.setCol(sc.nextInt());
        int cols = numberLink.getCol();
        int maxNum = sc.nextInt();
        NumberLink.setMaxNum(maxNum);
        int[][] input = new int[numberLink.getRow() + 1][numberLink.getCol() + 1];
        for (int i = 1; i < numberLink.getRow() + 1; i++) {
            for (int j = 1; j < numberLink.getCol() + 1; j++) {
                input[i][j] = sc.nextInt();
            }
        }
        numberLink.setInputs(input);

        // in ra de bai
        System.out.println(numberLink);

        // Ghi ra file CNF
        File fileCNF = new File("text.cnf");
        FileWriter writer = new FileWriter(fileCNF);

        //long t1 = System.currentTimeMillis();
        SatEncoding satEncoding = cnfConverter.generateSat(numberLink);

        long clause = satEncoding.getClauses();
        long vars = satEncoding.getVariables();
        String firstLine = "p cnf " + vars + " " + clause;
        System.out.println("So luong bien la: " + vars);
        System.out.println("So luong menh de la: " + clause);
        writer.write(firstLine + "\n");
        List<String> rules = satEncoding.getRules();
        for (int i = 0; i < rules.size(); i++) {
            // dong cuoi khong xuong dong
            if (i == rules.size() - 1) {
                writer.write(rules.get(i));
                continue;
            }
            writer.write(rules.get(i) + "\n");
        }
        writer.flush();
        writer.close();

        // SAT Solve
        NumberLinkResponse response = new NumberLinkResponse();
        DimacsReader reader = new DimacsReader(SolverFactory.newDefault());
        reader.parseInstance("text.cnf");
        satSolver = new SATSolver(reader);
        IProblem problem = null;

        //while (System.currentTimeMillis() < t1 + 360 * 1000) {
             problem = satSolver.solve("text.cnf");
        //}
        //long t2 = System.currentTimeMillis();
        if (problem.isSatisfiable()) {
            System.out.println("SAT");
            int[] model = problem.model();
            printResult(model, numberLink);

        }  else {
            System.out.println("UNSAT");
        }
        res.add((long) rows);
        res.add((long) cols);
        res.add((long) maxNum);
        res.add(vars);
        res.add(clause);

        //res.add(t2-t1);
        return res;
    }

    private void printResult(int[] model, NumberLink numberLink) {
        int countBreak = 0;
        for (int k = 0; k < model.length; k++) {

            if (model[k] > 0) {

                int positionValue = model[k];
                int i = cnfConverter.getValueOfYI(positionValue, numberLink);

                int breakPoint = (i - 1) % numberLink.getCol();
                int value = cnfConverter.getValueOfY(model[k], numberLink);

                if (breakPoint == countBreak) {
                    System.out.println();
                    countBreak++;
                }
                if (value < 10) {
                    System.out.print(" ");
                }
                System.out.print((value) + " ");
            }
        }
    }
}
