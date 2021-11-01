package min2phase;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void simpleSolve(String scrambledCube) {
        String result = new Search().solution(scrambledCube, 21, 100000000, 0, 0);
        System.out.println(result);
        String input =  "R2 U2 B2 L2 F2 U' L2 R2 B2 R2 D  B2 F  L' F  U2 F' R' D' L2 R";
        //System.out.println("Length of solution is " + result.length());
        //System.out.println("Length of solution is " + getResultString(sol));
        // R2 U2 B2 L2 F2 U' L2 R2 B2 R2 D  B2 F  L' F  U2 F' R' D' L2 R'
        numberOfMoves(result);


    }


    /** prepare scrambledCube as
     *
     *             |************|
     *             |*U1**U2**U3*|
     *             |************|
     *             |*U4**U5**U6*|
     *             |************|
     *             |*U7**U8**U9*|
     *             |************|
     * ************|************|************|************|
     * *L1**L2**L3*|*F1**F2**F3*|*R1**R2**R3*|*B1**B2**B3*|
     * ************|************|************|************|
     * *L4**L5**L6*|*F4**F5**F6*|*R4**R5**R6*|*B4**B5**B6*|
     * ************|************|************|************|
     * *L7**L8**L9*|*F7**F8**F9*|*R7**R8**R9*|*B7**B8**B9*|
     * ************|************|************|************|
     *             |************|
     *             |*D1**D2**D3*|
     *             |************|
     *             |*D4**D5**D6*|
     *             |************|
     *             |*D7**D8**D9*|
     *             |************|
     *
     * -> U1 U2 ... U9 R1 ... R9 F1 ... F9 D1 ... D9 L1 ... L9 B1 ... B9
     */

    public static void numberOfMoves(String input) {
        // Counter used to keep track of the valid number inputs.
        int counter = 0;
        // The index directly after the number ends.
        int endIndex = 0;
        // Now we simply loop through the string.
        for (int beginIndex = 0; beginIndex < input.length(); beginIndex = endIndex + 1) {
            // Get the index of the next space character.
            endIndex = input.indexOf(" ", beginIndex);
            // If there are no more spaces, set the endIndex to the end of the string.
            if (endIndex == -1) {
                endIndex = input.length();
            }
            // Take out only the current number from the input.
            String numberString = input.substring(beginIndex, endIndex);
            counter++;

        }
        System.out.println("Total number of moves: " + counter);
    }


    public static void outputControl(String scrambledCube) {
        String result = new Search().solution(scrambledCube, 21, 100000000, 0, Search.APPEND_LENGTH);
        System.out.println(result);
        // R2 U2 B2 L2 F2 U' L2 R2 B2 R2 D  B2 F  L' F  U2 F' R' D' L2 R' (21f)

        result = new Search().solution(scrambledCube, 21, 100000000, 0, Search.USE_SEPARATOR | Search.INVERSE_SOLUTION);
        System.out.println(result);
        // R  L2 D  R  F  U2 F' L  F' .  B2 D' R2 B2 R2 L2 U  F2 L2 B2 U2 R2
        numberOfMoves(result);
    }

    public static void findShorterSolutions(String scrambledCube) {
        //Find shorter solutions (try more probes even a solution has already been found)
        //In this example, we try AT LEAST 10000 phase2 probes to find shorter solutions.
        String result = new Search().solution(scrambledCube, 21, 100000000, 10000, 0);
        System.out.println(result);
        // L2 U  D2 R' B  U2 L  F  U  R2 D2 F2 U' L2 U  B  D  R'
       // numberOfMoves(result);
    }

    public static void continueSearch(String scrambledCube) {
        //Continue to find shorter solutions
        Search searchObj = new Search();
        String result = searchObj.solution(scrambledCube, 21, 500, 0, 0);
        System.out.println(result);
        // R2 U2 B2 L2 F2 U' L2 R2 B2 R2 D  B2 F  L' F  U2 F' R' D' L2 R'

        result = searchObj.next(500, 0, 0);
        System.out.println(result);
        // D2 L' D' L2 U  R2 F  B  L  B  D' B2 R2 U' R2 U' F2 R2 U' L2

        result = searchObj.next(500, 0, 0);
        System.out.println(result);
        // L' U  B  R2 F' L  F' U2 L  U' B' U2 B  L2 F  U2 R2 L2 B2

        result = searchObj.next(500, 0, 0);
        System.out.println(result);
        // Error 8, no solution is found after 500 phase2 probes. Let's try more probes.

        result = searchObj.next(500, 0, 0);
        System.out.println(result);
        // L2 U  D2 R' B  U2 L  F  U  R2 D2 F2 U' L2 U  B  D  R'
        numberOfMoves(result);
    }

    public static void main(String[] args) {
        // Full initialization, which will take about 200ms.
        // The solver will be about 5x~10x slower without full initialization.
        long startTime = System.nanoTime();
        Search.init();
        System.out.println("Init time: " + (System.nanoTime() - startTime) / 1.0E6 + " ms");

        String scrambledCube = "DUUBULDBFRBFRRULLLBRDFFFBLURDBFDFDRFRULBLUFDURRBLBDUDL";
        // scrambledCube can also be obtained by specific moves
       // scrambledCube = Tools.fromScramble("B2 F2 R D B L F L F2 L B2 R B R' D' F U");
        System.out.println(scrambledCube);

        //simpleSolve(scrambledCube);
        //outputControl(scrambledCube);
       findShorterSolutions(scrambledCube);
       //continueSearch(scrambledCube);
    }
}
