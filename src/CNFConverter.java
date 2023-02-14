import java.util.ArrayList;
import java.util.List;

public class CNFConverter {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;
    //    public static final int[] DIR = new int[]{ -1000, -1, 1};
    public static int[] m_limit = new int[]{0, 1, 10, 1, 10};

    boolean isLUCornerCell(int i, int j) {
        return (i == 1 && j == 1);
    }

    boolean isLDCornerCell(int i, int j) {
        return (i == m_limit[DOWN] && j == 1);
    }

    boolean isRUCornerCell(int i, int j) {
        return (i == 1 && j == m_limit[RIGHT]);
    }

    boolean isRDCornerCell(int i, int j) {
        return (i == m_limit[DOWN] && j == m_limit[RIGHT]);
    }

    boolean isLEdgeCell(int i, int j) {
        return (j == 1 && (i > 1 && i < m_limit[DOWN]));
    }

    boolean isREdgeCell(int i, int j) {
        return (j == m_limit[RIGHT] && (i > 1 && i < m_limit[DOWN]));
    }

    boolean isDEdgeCell(int i, int j) {
        return (i == m_limit[DOWN] && (j > 1 && j < m_limit[RIGHT]));
    }

    boolean isUEdgeCell(int i, int j) {
        return (i == 1 && (j > 1 && j < m_limit[RIGHT]));
    }

    public SatEncoding generateSat(NumberLink numberLink) {
        m_limit[DOWN] = numberLink.getRow();
        m_limit[RIGHT] = numberLink.getCol();
        int[][] inputs = numberLink.getInputs();
        int variables = 0;
        int clauses = 0;
        List<String> rules = new ArrayList<>();
        for (int i = 1; i < inputs.length; i++) {
            for (int j = 1; j < inputs[i].length; j++) {

                // cells have number
                if (inputs[i][j] != 0) {

                    List<String> rule0 = valueFromInput(i, j, inputs[i][j], numberLink);
//                    System.out.println("Rule 0" + rule0);
                    List<String> rule1 = notValuesFromInput(i, j, inputs[i][j], numberLink);
                    clauses += rule1.size();
                    rules.addAll(rule1);
                    List<String> rule2;
                    if (isLUCornerCell(i, j)) {
                        rule2 = LUConner_exact_one_direction(i, j, numberLink);
                    } else if (isRUCornerCell(i, j)) {
                        rule2 = RUConner_exact_one_direction(i, j, numberLink);
                    } else if (isRDCornerCell(i, j)) {
                        rule2 = RDConner_exact_one_direction(i, j, numberLink);
                    } else if (isLDCornerCell(i, j)) {
                        rule2 = LDConner_exact_one_direction(i, j, numberLink);
                    } else if (isLEdgeCell(i, j)) {
                        rule2 = LEdge_exact_one_direction(i, j, numberLink);
                    } else if (isREdgeCell(i, j)) {
                        rule2 = REdge_exact_one_direction(i, j, numberLink);
                    } else if (isUEdgeCell(i, j)) {
                        rule2 = UEdge_exact_one_direction(i, j, numberLink);
                    } else if (isDEdgeCell(i, j)) {
                        rule2 = DEdge_exact_one_direction(i, j, numberLink);
                    } else {
                        rule2 = exact_one_direction(i, j, numberLink);
                    }
                    clauses += rule0.size() + rule2.size();

                    rules.addAll(rule0);
                    rules.addAll(rule2);
//                    System.out.println("Rule 2" + rule2);
                    // blank cell
                } else {
                    List<String> baseRule1 = onlyOneValue(i, j, numberLink);
                    clauses += baseRule1.size();
                    rules.addAll(baseRule1);

                    List<String> rule2 = new ArrayList<>();
                    if (isLUCornerCell(i, j)) {
                        rule2 = LUCorner_has_two_directions(i, j, numberLink);
                    } else if (isRUCornerCell(i, j)) {
                        rule2 = RUCorner_has_two_directions(i, j, numberLink);
                    } else if (isRDCornerCell(i, j)) {
                        rule2 = RDCorner_has_two_directions(i, j, numberLink);
                    } else if (isLDCornerCell(i, j)) {
                        rule2 = LDCorner_has_two_directions(i, j, numberLink);
                    } else if (isLEdgeCell(i, j)) {
                        rule2 = LEdge_has_two_directions(i, j, numberLink);
                    } else if (isREdgeCell(i, j)) {
                        rule2 = REdge_has_two_directions(i, j, numberLink);
                    } else if (isUEdgeCell(i, j)) {
                        rule2 = UEdge_has_two_directions(i, j, numberLink);
                    } else if (isDEdgeCell(i, j)) {
                        rule2 = DEdge_has_two_directions(i, j, numberLink);
                    } else {
                        rule2 = has_two_directions(i, j, numberLink);
                    }

                    clauses += rule2.size();

                    rules.addAll(rule2);
//                    System.out.println("Rule 2" + rule2);
                }

            }
        }
        variables = numberLink.getRow() * numberLink.getCol() * numberLink.getMaxNum();
        return new SatEncoding(rules, clauses, variables);
    }


    private List<String> has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "has_two_directions";
        //resultStringList.add(tmpClause);
        String firstClause = "";
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);
        }
        //

        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += -computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += -computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += -computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += -computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += -computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += -computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);
        }

        /*for (int k = 1; k <= 2; k++) {
            for (int q = k + 1; q <= 1; q++) {
                for (int p = q + 1; p <= NUM_OF_DIRECTION; p++) {
                    String firstClause = computePosition(i, j, k, numberLink) + " ";
                    firstClause += computePosition(i, j, q, numberLink) + " ";
                    firstClause += computePosition(i, j, p, numberLink) + " ";
                    firstClause += "0";
                    resultStringList.add(firstClause);
                }
            }

        }
        for (int k = 1; k <= 2; k++) {
            for (int q = k + 1; q <= 1; q++) {
                for (int p = q + 1; p <= NUM_OF_DIRECTION; p++) {
                    String firstClause = -computePosition(i, j, k, numberLink) + " ";
                    firstClause += -computePosition(i, j, q, numberLink) + " ";
                    firstClause += -computePosition(i, j, p, numberLink) + " ";
                    firstClause += "0";
                    resultStringList.add(firstClause);
                }
            }
        }*/
        return resultStringList;
    } 

    private List<String> exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "exact_one_direction";
        //resultStringList.add(tmpClause);

        // (x1 -> -x2) ^ (x1 -> -x3) ^ (x1 -> -x4)...
        String tmp = "";
        tmp += computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        tmp += computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        tmp += computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        tmp += computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        tmp += "0";
        resultStringList.add(tmp);

        //

        String firstClause = -computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += -computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        firstClause = -computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += -computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        firstClause = -computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += -computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        firstClause = -computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += -computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        firstClause = -computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += -computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        firstClause = -computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += -computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);


/*        int i0, j0;
        for (int k = 1; k <= 3; k++) {
            i0 = DIR[k];
            for (int q = k+1; k <= 4; k++) {
                j0 = DIR[q];
                String secondClause = -computePosition(i + i0, j, numberLink.getInputs()[i][j], numberLink) + " ";
                secondClause += -computePosition(i, j + j0, numberLink.getInputs()[i][j], numberLink) + " ";
            }
        }*/

        //secondClause += computePosition(i, j, NUM_OF_DIRECTION, numberLink) + "0";

        return resultStringList;
    }

    private List<String> LEdge_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "LEdge_exact_one_direction";
        //resultStringList.add(tmpClause);
        String firstClause = "";
        firstClause += computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        //

        String secondClause = "";
        secondClause += -computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        secondClause = "";
        secondClause += -computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        secondClause = "";
        secondClause += -computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        return resultStringList;
    }

    private List<String> REdge_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "REdge_exact_one_direction";
        //resultStringList.add(tmpClause);

        String firstClause = "";
        firstClause += computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        //

        String secondClause = "";
        secondClause += -computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        secondClause = "";
        secondClause += -computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        secondClause = "";
        secondClause += -computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        return resultStringList;
    }

    private List<String> DEdge_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "DEdge_exact_one_direction";
        //resultStringList.add(tmpClause);

        String firstClause = "";
        firstClause += computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        //

        String secondClause = "";
        secondClause += -computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        secondClause = "";
        secondClause += -computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        secondClause = "";
        secondClause += -computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        return resultStringList;
    }

    private List<String> UEdge_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "UEdge_exact_one_direction";
        //resultStringList.add(tmpClause);

        String firstClause = "";
        firstClause += computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        //

        String secondClause = "";
        secondClause += -computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        secondClause = "";
        secondClause += -computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        secondClause = "";
        secondClause += -computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);

        return resultStringList;
    }

    //
    private List<String> LUConner_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "LUConner_exact_one_direction";
        //resultStringList.add(tmpClause);
        String firstClause = "";
        firstClause += computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        String secondClause = "";
        secondClause += -computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);
        return resultStringList;
    }

    private List<String> RUConner_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "RUConner_exact_one_direction";
        //resultStringList.add(tmpClause);

        String firstClause = "";
        firstClause += computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        String secondClause = "";
        secondClause += -computePosition(i + 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);
        return resultStringList;
    }

    private List<String> RDConner_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "RDConner_exact_one_direction";
        //resultStringList.add(tmpClause);

        String firstClause = "";
        firstClause += computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        String secondClause = "";
        secondClause += -computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j - 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);
        return resultStringList;
    }

    private List<String> LDConner_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "LDConner_exact_one_direction";
        //resultStringList.add(tmpClause);

        String firstClause = "";
        firstClause += computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        firstClause += "0";
        resultStringList.add(firstClause);

        String secondClause = "";
        secondClause += -computePosition(i - 1, j, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += -computePosition(i, j + 1, numberLink.getInputs()[i][j], numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);
        return resultStringList;
    }

    private List<String> LEdge_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "LEdge_has_two_directions";
//        resultStringList.add(tmpClause);

        //co 2 trong 3
        String firstClause = "";
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);
        }
        //

        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += -computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);
        }
        return resultStringList;
    }

    private List<String> DEdge_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "DEdge_has_two_directions";
        //resultStringList.add(tmpClause);

        //co 2 trong 3
        String firstClause = "";
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);
        }
        //
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += -computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += -computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);
        }
        return resultStringList;
    }

    private List<String> REdge_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "REdge_has_two_directions";
        //resultStringList.add(tmpClause);

        //co 2 trong 3
        String firstClause = "";
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);
        }
        //
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += -computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i - 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);
        }
        return resultStringList;
    }

    private List<String> UEdge_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "UEdge_has_two_directions";
        //resultStringList.add(tmpClause);

        //co 2 trong 3
        String firstClause = "";
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);

            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);
        }
        //
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            firstClause += -computePosition(i + 1, j, k, numberLink) + " ";
            firstClause += -computePosition(i, j - 1, k, numberLink) + " ";
            firstClause += -computePosition(i, j + 1, k, numberLink) + " ";
            firstClause += "0";
            resultStringList.add(firstClause);
        }
        return resultStringList;
    }

    private List<String> LUCorner_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "LUCorner_has_two_directions";
        //resultStringList.add(tmpClause);
        String first = "";
        String second = "";
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            first = -computePosition(i, j, k, numberLink) + " ";
            second = -computePosition(i, j, k, numberLink) + " ";
            first += computePosition(i + 1, j, k, numberLink) + " 0";
            second += computePosition(i, j + 1, k, numberLink) + " 0";
            resultStringList.add(first);
            resultStringList.add(second);
        }
        return resultStringList;
    }

    private List<String> RUCorner_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "RUCorner_has_two_directions";
        //resultStringList.add(tmpClause);
        String first = "";
        String second = "";
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            first = -computePosition(i, j, k, numberLink) + " ";
            second = -computePosition(i, j, k, numberLink) + " ";
            first += computePosition(i + 1, j, k, numberLink) + " 0";
            second += computePosition(i, j - 1, k, numberLink) + " 0";
            resultStringList.add(first);
            resultStringList.add(second);
        }
        return resultStringList;
    }

    private List<String> RDCorner_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "RDCorner_has_two_directions";
        //resultStringList.add(tmpClause);
        String first = "";
        String second = "";
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            first = -computePosition(i, j, k, numberLink) + " ";
            second = -computePosition(i, j, k, numberLink) + " ";
            first += computePosition(i - 1, j, k, numberLink) + " 0";
            second += computePosition(i, j - 1, k, numberLink) + " 0";
            resultStringList.add(first);
            resultStringList.add(second);
        }
        return resultStringList;
    }

    private List<String> LDCorner_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "LDCorner_has_two_directions";
        //resultStringList.add(tmpClause);
        String first = "";
        String second = "";
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            first = -computePosition(i, j, k, numberLink) + " ";
            second = -computePosition(i, j, k, numberLink) + " ";
            first += computePosition(i - 1, j, k, numberLink) + " 0";
            second += computePosition(i, j + 1, k, numberLink) + " 0";
            resultStringList.add(first);
            resultStringList.add(second);
        }
        return resultStringList;
    }


    private List<String> onlyOneValue(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "onlyOneValue";
        //resultStringList.add(tmpClause);
//        String exactNumLine = "";
//
//        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
//            exactNumLine += computePosition(i, j, k, numberLink) + " ";
//        }
//        exactNumLine += "0";
//        resultStringList.add(exactNumLine);

        for (int k = 1; k <= numberLink.getMaxNum()-1; k++) {
            String firstClause = -computePosition(i, j, k, numberLink) + " ";
            for (int q = k + 1; q <= numberLink.getMaxNum(); q++) {
                String secondClause = -computePosition(i, j, q, numberLink) + " ";
                secondClause += "0";
                resultStringList.add(firstClause + secondClause);
            }
        }
        return resultStringList;
    }

    private List<String> valueFromInput(int i, int j, int num, NumberLink numberLink) {
        int result = computePosition(i, j, num, numberLink);
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "valueFromInput";
        //resultStringList.add(tmpClause);

        String exactNumLine = "";
        exactNumLine += result + " 0";
        resultStringList.add(exactNumLine);

        return resultStringList;
    }

    private List<String> notValuesFromInput(int i, int j, int num, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String firstClause = -computePosition(i, j, num, numberLink) + " ";
        for (int q = 1; q <= numberLink.getMaxNum(); q++) {
            if (q != num) {
                String secondClause = -computePosition(i, j, q, numberLink) + " ";
                secondClause += "0";
                resultStringList.add(secondClause);
            }
        }
        return resultStringList;
    }


    private int computePosition(int i, int j, int value, NumberLink numberLink) {
        int n = numberLink.getCol();
        return n * (i - 1) * numberLink.getMaxNum() + (j - 1) * numberLink.getMaxNum() + value;
    }


    public int getValueOf(int row, int col, int positionValue, NumberLink numberLink) {
        int n = numberLink.getCol();
        if (positionValue <= n * (n - 1)) {
            int JValue = (positionValue - 1) % (n - 1) + 1;
            if (JValue == col) {
                return RIGHT;
            } else if (JValue + 1 == col) {
                return LEFT;
            } else return 100;
        } else if (positionValue <= 2 * n * (n - 1)) {
            int IValue = (positionValue - n * (n - 1) - 1) % (n - 1) + 1;
            if (IValue == row) {
                return DOWN;
            } else if (IValue + 1 == row) {
                return UP;
            } else return 100;
        } else {
            int tmp = (positionValue - 2 * n * (n - 1 - 1)) % numberLink.getMaxNum() + 1;
            return ((positionValue - 2 * n * (n - 1) - tmp) / numberLink.getMaxNum() + 1 - col) / n + 1;
        }
    }

    public int getValueOfY(int positionValue, int maxNum) {
        return (positionValue - 1) % maxNum + 1;
    }

    public int getValueOfYJ(int positionValue, NumberLink numberLink) {
        return ((positionValue - 1) / numberLink.getMaxNum()) % numberLink.getCol() + 1;
    }

    public int getValueOfYI(int positionValue, NumberLink numberLink) {
        positionValue = Math.abs(positionValue);
        return (positionValue - 1) / (numberLink.getMaxNum() * numberLink.getCol()) + 1;
    }

}