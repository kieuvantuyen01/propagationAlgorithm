import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CNFConverter {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;
    public static int[] m_limit = new int[]{0, 1, 10, 1, 10};
    int[][] source = new int[100][2];
    int[][] target = new int[100][2];

    /*
    return binary strings with fixed length.
    i.e n=2 [00, 01, 10, 11]
    n = [log2(num of old variables)]
    */
    public static List<String> generateBinaryStrings(int n) {
        List<String> stringPermutations = new ArrayList<>();
        // number of permutations is 2^n (represented in binary by 2 bits 0 and 1)
        int permutations = (int) Math.pow(2, n);


        // Sinh du "permutations" chuoi nhi phan
        for (int bits = 0; bits < permutations; bits++) {
            String permutation = convert(bits, n);
            stringPermutations.add(permutation);
        }
        Collections.sort(stringPermutations);
        return stringPermutations;
    }

    public static String convert(int bits, int n) {
        String conversion = "";
        while (n-- > 0) {
            int bit = bits & 1; // Retrieves the rightmost bit
            if (bit == 0) {
                conversion += "0";
            } else {
                conversion += "1";
            }

            // >> means signed right shift
            bits >>= 1; // Removes the rightmost bit.
        }
        return conversion;
    }

    // i.e.
    public static String reverseString(String str) {
        String nstr = "";
        char ch;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i); //extracts each character
            nstr = ch + nstr; //adds each character in front of the existing string
        }
        return nstr;
    }

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

    // Tồn tại duy nhất = tối đa + tối thiểu

    boolean isREdgeCell(int i, int j) {
        return (j == m_limit[RIGHT] && (i > 1 && i < m_limit[DOWN]));
    }

    boolean isDEdgeCell(int i, int j) {
        return (i == m_limit[DOWN] && (j > 1 && j < m_limit[RIGHT]));
    }

    boolean isUEdgeCell(int i, int j) {
        return (i == 1 && (j > 1 && j < m_limit[RIGHT]));
    }

    /*
     * bits = 0 --> bit = 0 & 1 = 0 --> conversion = 0 --> bits = 0 >> 1 = 0 --> bit = 0 & 1 = 0 --> conversion = 00
     * bits = 1 --> bit = 1 & 1 = 1 --> conversion = 1 --> bits = 1 >> 1 = 0 --> bit = 0 & 1 = 0 --> conversion = 10
     * bits = 2 --> bit = 2 & 1 = 0 --> conversion = 0 --> bits = 2 >> 1 = 1 --> bit = 1 & 1 = 1 --> conversion = 01
     * bits = 3 --> bit = 3 & 1 = 1 --> conversion = 1 --> bits = 3 >> 1 = 1 --> bit = 1 & 1 = 1 --> conversion = 11
     * stringPermutations = [00, 01, 10, 11]
     * */
    // q: why the result of even number AND 1 is 0?
    // a: because the rightmost bit of even number is 0, so the result of AND 1 is 0
    // q: why we have to remove the rightmost bit in line 117?
    // a: because we have to check the next bit of the number

    // Các ô liền kề với ô đang xét
    List<Integer> adjacentCells(int i, int j, int value, NumberLink numberLink) {
        List<Integer> res = new ArrayList<>();
        if (isLUCornerCell(i, j)) {
            res.add(computePosition(i + 1, j, value, numberLink));
            res.add(computePosition(i, j + 1, value, numberLink));
        } else if (isLDCornerCell(i, j)) {
            res.add(computePosition(i - 1, j, value, numberLink));
            res.add(computePosition(i, j + 1, value, numberLink));
        } else if (isRDCornerCell(i, j)) {
            res.add(computePosition(i - 1, j, value, numberLink));
            res.add(computePosition(i, j - 1, value, numberLink));
        } else if (isRUCornerCell(i, j)) {
            res.add(computePosition(i + 1, j, value, numberLink));
            res.add(computePosition(i, j - 1, value, numberLink));
        } else if (isLEdgeCell(i, j)) {
            res.add(computePosition(i + 1, j, value, numberLink));
            res.add(computePosition(i, j + 1, value, numberLink));
            res.add(computePosition(i - 1, j, value, numberLink));
        } else if (isREdgeCell(i, j)) {
            res.add(computePosition(i + 1, j, value, numberLink));
            res.add(computePosition(i, j - 1, value, numberLink));
            res.add(computePosition(i - 1, j, value, numberLink));
        } else if (isUEdgeCell(i, j)) {
            res.add(computePosition(i, j - 1, value, numberLink));
            res.add(computePosition(i, j + 1, value, numberLink));
            res.add(computePosition(i + 1, j, value, numberLink));
        } else if (isDEdgeCell(i, j)) {
            res.add(computePosition(i - 1, j, value, numberLink));
            res.add(computePosition(i, j + 1, value, numberLink));
            res.add(computePosition(i, j - 1, value, numberLink));
        } else {
            res.add(computePosition(i + 1, j, value, numberLink));
            res.add(computePosition(i, j + 1, value, numberLink));
            res.add(computePosition(i - 1, j, value, numberLink));
            res.add(computePosition(i, j - 1, value, numberLink));
        }
        return res;
    }

    public SatEncoding generateSat(NumberLink numberLink) {
        m_limit[DOWN] = numberLink.getRow();
        m_limit[RIGHT] = numberLink.getCol();
        int max_num = numberLink.getMaxNum();
        int adding_vars = (int) Math.ceil((Math.log(max_num) / Math.log(2)));
        // Math.log(max_num) / Math.log(2) = log2(max_num)
        int[][] inputs = numberLink.getInputs();
        int variables = 0;
        int clauses = 0;
        List<String> rules = new ArrayList<>();
        List<String> additionalRule = new ArrayList<>();
        for (int i = 1; i < inputs.length; i++) {
            for (int j = 1; j < inputs[i].length; j++) {

                // cells have number
                if (inputs[i][j] != 0) {

                    List<String> rule0 = valueFromInput(i, j, inputs[i][j], numberLink);
                    List<String> rule1 = notValuesFromInput(i, j, inputs[i][j], numberLink);
                    List<String> rule2 = exact_one_direction(i, j, numberLink);

                    int index = inputs[i][j];
//                  Add index of numbered cells to source and target arrays
                    if (source[index][0] == 0 && source[index][1] == 0) {
                        source[index][0] = i;
                        source[index][1] = j;
                    } else {
                        target[index][0] = i;
                        target[index][1] = j;
                    }

                    rules.addAll(rule0);
                    rules.addAll(rule1);
                    rules.addAll(rule2);

                    clauses += rule0.size() + rule1.size() + rule2.size();

                    // blank cell
                } else {
                    List<String> baseRule1 = onlyOneValue(i, j, numberLink);

                    clauses += baseRule1.size();
                    rules.addAll(baseRule1);

                    List<String> rule2 = has_two_directions(i, j, numberLink);

                    clauses += rule2.size();

                    rules.addAll(rule2);
                }
            }
        }

        // Adding row and column contraints (addtional rule)
        additionalRule = additionalRule(source, target, max_num, m_limit[DOWN], m_limit[RIGHT], inputs, numberLink);
        rules.addAll(additionalRule);
        clauses += additionalRule.size();
        Arrays.stream(source).forEach(x -> Arrays.fill(x, 0));
        Arrays.stream(target).forEach(x -> Arrays.fill(x, 0));

        variables = m_limit[DOWN] * m_limit[RIGHT] * max_num +
                adding_vars * (m_limit[DOWN] * m_limit[RIGHT] - max_num * 2);
        // adding_vars * (m_limit[DOWN] * m_limit[RIGHT] - max_num * 2) --> for blank cells (not cells have number)
        return new SatEncoding(rules, clauses, variables);
    }

    public List<String> additionalRule(int[][] source, int[][] target, int maxNum, int row, int col, int[][] inputs, NumberLink numberlink) {
        List<String> res = new ArrayList<>();

        for (int i = 1; i <= maxNum; i++) {
            int startRow = source[i][0] > target[i][0] ? target[i][0] + 1 : source[i][0] + 1;
            int endRow = source[i][0] > target[i][0] ? source[i][0] - 1 : target[i][0] - 1;
            int startCol = source[i][1] > target[i][1] ? target[i][1] + 1 : source[i][1] + 1;
            int endCol = source[i][1] > target[i][1] ? source[i][1] - 1 : target[i][1] - 1;
            // Row constraints
            for (int j = startRow; j <= endRow; j++) {
                String rowConstraint = "";
                for (int k = 1; k <= col; k++) {
                    if (inputs[j][k] == 0) {
                        rowConstraint += computePosition(j, k, i, numberlink) + " ";
                    }
                }
                rowConstraint += "0";
                res.add(rowConstraint);
            }
            // Col constraints
            for (int j = startCol; j <= endCol; j++) {
                String colConstraint = "";
                for (int k = 1; k <= row; k++) {
                    if (inputs[k][j] == 0) {
                        colConstraint += computePosition(k, j, i, numberlink) + " ";
                    }
                }
                colConstraint += "0";
                res.add(colConstraint);
            }
        }
        return res;
    }

    // Blank cells have two directions
    private List<String> has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();

        String firstClause = "";
        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            List<Integer> adjacentCells = adjacentCells(i, j, k, numberLink);
            int numCells = adjacentCells.size();
            // numCells == 2: ô ở vị trí góc --> (-Xijk v Xi(j+1)k) ^ (-Xijk v X(i+1)jk)
            // numCells == 3: ô ở vị trí biên
            // numCells == 4: ô ở các vị trí còn lại
            if (numCells == 2) {
                for (int z = 0; z <= numCells - 1; z++) {
                    String tmp2 = firstClause + adjacentCells.get(z) + " ";
                    tmp2 += "0";
                    resultStringList.add(tmp2);
                }
            } else if (numCells == 3) {
                // 2 trong 3 hướng đi: (-Xijk v Xi(j-1)k v Xi(j+1)k) ^ (-Xijk v Xi(j-1)k v X(i+1)jk) ^ (-Xijk v X(i+1)jk v Xi(j+1)k)
                // At least 2 in 3 are TRUE
                for (int t = 0; t <= numCells - 2; t++) {
                    String tmp1 = firstClause + adjacentCells.get(t) + " ";
                    for (int z = t + 1; z <= numCells - 1; z++) {
                        String tmp2 = tmp1 + adjacentCells.get(z) + " ";
                        tmp2 += "0";
                        resultStringList.add(tmp2);
                    }
                }
            } else if (numCells == 4) {
                // 2 trong 4 hướng đi: -Xijk v X(i+1)jk v Xi(j+1)k v
                // At least 2 in 4 are TRUE
                for (int q = 0; q <= numCells - 3; q++) {
                    String tmp0 = firstClause + adjacentCells.get(q) + " ";
                    // tmp0 = -Xijk v X(i+1)jk, q = 0
                    // tmp0 = -Xijk v Xi(j+1)k, q = 1
                    for (int t = q + 1; t <= numCells - 2; t++) {
                        String tmp1 = tmp0 + adjacentCells.get(t) + " ";
                        // tmp1 = -Xijk v X(i+1)jk v Xi(j+1)k, q = 0 t = 1
                        // tmp1 = -Xijk v X(i+1)jk v X(i-1)jk, q = 0 t = 2
                        // tmp1 = -Xijk v Xi(j+1)k v X(i-1)jk, q = 1 t = 2
                        for (int z = t + 1; z <= numCells - 1; z++) {
                            String tmp2 = tmp1 + adjacentCells.get(z) + " ";
                            tmp2 += "0";
                            // tmp2 = -Xijk v X(i+1)jk v Xi(j+1)k v X(i-1)jk, q = 0 t = 1 z = 2
                            // tmp2 = -Xijk v X(i+1)jk v Xi(j+1)k v Xi(j-1)k, q = 0 t = 1 z = 3
                            // tmp2 = -Xijk v X(i+1)jk v X(i-1)jk v Xi(j-1)k, q = 0 t = 2 z = 3
                            // tmp2 = -Xijk v Xi(j+1)k v X(i-1)jk v Xi(j-1)k, q = 1 t = 2 z = 3
                            resultStringList.add(tmp2);
                        }
                    }
                }
            }
        }


//        UEdge: ←, ↓, →
//        res.add(computePosition(i, j - 1, value, numberLink));
//        res.add(computePosition(i, j + 1, value, numberLink));
//        res.add(computePosition(i + 1, j, value, numberLink));

        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            firstClause = -computePosition(i, j, k, numberLink) + " ";
            List<Integer> adjacentCells = adjacentCells(i, j, k, numberLink);
            int numCells = adjacentCells.size();
            if (numCells == 3) {
//              At most 2 in 3 are TRUE
                String tmp2 = firstClause;
                for (int z = 0; z <= numCells - 1; z++) {
                    tmp2 += (-adjacentCells.get(z)) + " ";
                    // tmp2 = -Xijk v -Xi(j-1)k v -Xi(j+1)k v -X(i+1)jk
                }
                tmp2 += "0";
                resultStringList.add(tmp2);
            } else if (numCells == 4) {
//              At most 2 in 4 are TRUE
//              res.add(computePosition(i + 1, j, value, numberLink));   0
//              res.add(computePosition(i, j + 1, value, numberLink));   1
//              res.add(computePosition(i - 1, j, value, numberLink));   2
//              res.add(computePosition(i, j - 1, value, numberLink));   3
                for (int q = 0; q <= numCells - 3; q++) {
                    String tmp0 = firstClause + (-adjacentCells.get(q)) + " ";
                    // tmp0 = -Xijk v -X(i+1)jk, q = 0
                    // tmp0 = -Xijk v -Xi(j+1)k, q = 1
                    for (int t = q + 1; t <= numCells - 2; t++) {
                        String tmp1 = tmp0 + (-adjacentCells.get(t)) + " ";
                        // tmp1 = -Xijk v -X(i+1)jk v -Xi(j+1)k, q = 0 t = 1
                        // tmp1 = -Xijk v -X(i+1)jk v -X(i-1)jk, q = 0 t = 2
                        // tmp1 = -Xijk v -Xi(j+1)k v -X(i-1)jk, q = 1 t = 2
                        for (int z = t + 1; z <= numCells - 1; z++) {
                            String tmp2 = tmp1 + (-adjacentCells.get(z)) + " ";
                            // tmp2 = -Xijk v -X(i+1)jk v -Xi(j+1)k v -X(i-1)jk, q = 0 t = 1 z = 2
                            // tmp2 = -Xijk v -X(i+1)jk v -Xi(j+1)k v -Xi(j-1)k, q = 0 t = 1 z = 3
                            // tmp2 = -Xijk v -X(i+1)jk v -X(i-1)jk v -Xi(j-1)k, q = 0 t = 2 z = 3
                            // tmp2 = -Xijk v -Xi(j+1)k v -X(i-1)jk v -Xi(j-1)k, q = 1 t = 2 z = 3
                            tmp2 += "0";
                            resultStringList.add(tmp2);
                        }
                    }
                }
            }
        }
        return resultStringList;
    }


    // for numbered cells
    private List<String> exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
        List<Integer> adjacentCells = adjacentCells(i, j, numberLink.getInputs()[i][j], numberLink);
        String firstClause = "";
        // AT LEAST 1 is TRUE
        for (int value : adjacentCells) {
            firstClause += value + " ";
        }
        firstClause += "0";
        resultStringList.add(firstClause);

        // AT MOST 1 is TRUE --> tại sao không dùng công thức dựa trên biến mới như trong slides thầy Khánh?
        // cannot calculate adding_vars for numbered cells --> use this method
        int numCells = adjacentCells.size();
        for (int k = 0; k <= numCells - 2; k++) {
            String secondClause = -adjacentCells.get(k) + " ";
            // secondClause = -Xi(j-1)k, k = 0
            // secondClause = -Xi(j+1)k, k = 1
            for (int q = k + 1; q <= numCells - 1; q++) {
                String tmp = secondClause + (-adjacentCells.get(q)) + " 0";
                // tmp = -Xi(j-1)k v -Xi(j+1)k, k = 0 q = 1
                // tmp = -Xi(j-1)k v -X(i-1)jk, k = 0 q = 2
                // tmp = -Xi(j+1)k v -X(i-1)jk, k = 1 q = 2
                resultStringList.add(tmp);
            }
        }
        return resultStringList;
    }

    // For blank cells: at most one value is TRUE in each blank cell
    private List<String> onlyOneValue(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "onlyOneValue";
//        resultStringList.add(tmpClause);
        int max_num = numberLink.getMaxNum(); // max_num = 4 (5x5 1.in)
        int adding_vars = (int) Math.ceil((Math.log(max_num) / Math.log(2))); // adding_vars = log2(4) = 2
        List<String> binaryStrings = generateBinaryStrings(adding_vars);
        // binaryStrings = ["00", "01", "10", "11"]
        // cắt bớt, chỉ lấy n = max_num xâu
        binaryStrings = binaryStrings.subList(0, max_num);
        // binaryStrings = ["00", "01", "10", "11"]

        // AMO
        for (int k = 1; k <= max_num; k++) { // k = 1 --> 4

            String binary = binaryStrings.get(k - 1);
            binary = reverseString(binary);
            // binary = "00"
            for (int q = max_num + 1; q <= max_num + adding_vars; q++) {  // q = 5 --> 6
                String clause = "";
                // -X v
                clause += -computePosition(i, j, k, numberLink) + " ";
                // clause = -Xijk
                char bit = binary.charAt(q - max_num - 1);
                if (bit == '0') {
                    // -Y
                    clause += -computePosition(i, j, q, numberLink) + " ";
                } else {
                    // Y
                    clause += computePosition(i, j, q, numberLink) + " ";
                }
                clause += "0";
                resultStringList.add(clause);
            }
        }

        // ALO
        String ALOclause = "";
        for (int k = 1; k <= max_num; k++) {
            ALOclause += computePosition(i, j, k, numberLink) + " ";
        }
        ALOclause += "0";
        resultStringList.add(ALOclause);


        return resultStringList;
    }

    // For numbered cells: only the existed value is TRUE
    private List<String> valueFromInput(int i, int j, int num, NumberLink numberLink) {
        int result = computePosition(i, j, num, numberLink);
        // result = Xijk (k represents for numbered cell's value)
        List<String> resultStringList = new ArrayList<>();
        //String tmpClause = "valueFromInput";
        //resultStringList.add(tmpClause);

        String exactNumLine = "";
        exactNumLine += result + " 0";
        resultStringList.add(exactNumLine);

        return resultStringList;
    }

    // For numbered cells: other values which are different from the existed one are FALSE
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
        int max_num = numberLink.getMaxNum();
        int adding_vars = (int) Math.ceil((Math.log(max_num) / Math.log(2)));
        int X_vars = numberLink.getRow() * numberLink.getCol() * max_num;
        if (value <= max_num)
            return n * (i - 1) * max_num + (j - 1) * max_num + value;
        return X_vars + n * (i - 1) * adding_vars + (j - 1) * adding_vars + value;
        // X_vars = row * col * max_num = 5 * 5 * 4 = 100
    }

    public int getValueOfY(int positionValue, int maxNum, NumberLink numberLink) {
        int rows = numberLink.getRow();
        int cols = numberLink.getCol();
        if (positionValue <= rows * cols * maxNum) {
            return (positionValue - 1) % maxNum + 1;
        }
        return -1;
    }
}