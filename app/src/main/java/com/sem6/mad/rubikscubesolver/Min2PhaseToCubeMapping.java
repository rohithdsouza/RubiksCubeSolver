package com.sem6.mad.rubikscubesolver;

/**
 * Takes i/p of Min2Phase Scrambled cube and Returns a string representing the colors
 * of the scrambled cube for 3DAnimCube
 *
 * Eg: DUUBULDBFRBFRRULLLBRDFFFBLURDBFDFDRFRULBLUFDURRBLBDUDL
 *                          to
 *     132304100521115322323524120540531314405043012554354204
 *
 *     test- 000000000333333555555555222111111111222222444444444333
 */
public class Min2PhaseToCubeMapping {
    public static String colorMapping (String initialSequence)
    {
        StringBuilder numberSequence = new StringBuilder(initialSequence);
        char[][] numSequence = new char[6][9];
        String finalSequence1;

        // first mapping to colors DUF.. to colors numeric


        for ( int i =0 ; i < initialSequence.length() ; i++)
        {
            switch (initialSequence.charAt(i))
            {
                case 'U' : numberSequence.setCharAt(i, '0'); //white
                    break;
                case 'B' : numberSequence.setCharAt(i, '1'); //yellow
                    break;
                case 'D' : numberSequence.setCharAt(i, '5'); //green
                    break;
                case 'L' : numberSequence.setCharAt(i, '4'); //blue
                    break;
                case 'R' : numberSequence.setCharAt(i, '3'); //red
                    break;
                case 'F' : numberSequence.setCharAt(i, '2'); //orange
                    break;
            }
        }

        numSequence = orientationMapping( numberSequence.toString() );
        finalSequence1 = properMapping ( numSequence );

        return finalSequence1;
    }

    //changing the <U R F D L B> to <U D F B L R> but to <U D L R B F>

    public static char[][] orientationMapping ( String numberSequence)
    {
        // mapping color to required orientation =
        String[] moduloSequence = new String[6];
        char[][] finalSequence = new char[6][9];

        for ( int j =0 ; j< 6 ; j++)
        {

            moduloSequence[j] = (String) numberSequence.subSequence(j*9,(j+1)*9);

        }

        finalSequence[0] = moduloSequence[0].toCharArray();
        finalSequence[1] = moduloSequence[3].toCharArray();
        finalSequence[2] = moduloSequence[2].toCharArray();
        finalSequence[3] = moduloSequence[5].toCharArray();
        finalSequence[4] = moduloSequence[4].toCharArray();
        finalSequence[5] = moduloSequence[1].toCharArray();

        return finalSequence;
    }

    public static String properMapping(char[][] numSeq)
    {
        char[][] newSeq = new char[6][9];
        String finalsequence = "";

        // i/p - 012345012101234512212345012312345013412345014512345015

        // U
        newSeq[0][0] = numSeq[0][7-1];
        newSeq[0][1] = numSeq[0][8-1];
        newSeq[0][2] = numSeq[0][9-1];
        newSeq[0][3] = numSeq[0][4-1];
        newSeq[0][4] = numSeq[0][5-1];
        newSeq[0][5] = numSeq[0][6-1];
        newSeq[0][6] = numSeq[0][1-1];
        newSeq[0][7] = numSeq[0][2-1];
        newSeq[0][8] = numSeq[0][3-1];

        //D
        newSeq[1][0] = numSeq[1][1-1];
        newSeq[1][1] = numSeq[1][4-1];
        newSeq[1][2] = numSeq[1][7-1];
        newSeq[1][3] = numSeq[1][2-1];
        newSeq[1][4] = numSeq[1][5-1];
        newSeq[1][5] = numSeq[1][8-1];
        newSeq[1][6] = numSeq[1][3-1];
        newSeq[1][7] = numSeq[1][6-1];
        newSeq[1][8] = numSeq[1][9-1];

        //F
        newSeq[2][0] = numSeq[2][1-1];
        newSeq[2][1] = numSeq[2][4-1];
        newSeq[2][2] = numSeq[2][7-1];
        newSeq[2][3] = numSeq[2][2-1];
        newSeq[2][4] = numSeq[2][5-1];
        newSeq[2][5] = numSeq[2][8-1];
        newSeq[2][6] = numSeq[2][3-1];
        newSeq[2][7] = numSeq[2][6-1];
        newSeq[2][8] = numSeq[2][9-1];

        //B
        newSeq[3][0] = numSeq[3][1-1];
        newSeq[3][1] = numSeq[3][4-1];
        newSeq[3][2] = numSeq[3][7-1];
        newSeq[3][3] = numSeq[3][2-1];
        newSeq[3][4] = numSeq[3][5-1];
        newSeq[3][5] = numSeq[3][8-1];
        newSeq[3][6] = numSeq[3][3-1];
        newSeq[3][7] = numSeq[3][6-1];
        newSeq[3][8] = numSeq[3][9-1];

        //L
        newSeq[4][0] = numSeq[4][3-1];
        newSeq[4][1] = numSeq[4][2-1];
        newSeq[4][2] = numSeq[4][1-1];
        newSeq[4][3] = numSeq[4][6-1];
        newSeq[4][4] = numSeq[4][5-1];
        newSeq[4][5] = numSeq[4][4-1];
        newSeq[4][6] = numSeq[4][9-1];
        newSeq[4][7] = numSeq[4][8-1];
        newSeq[4][8] = numSeq[4][7-1];

        //R
        newSeq[5][0] = numSeq[5][1-1];
        newSeq[5][1] = numSeq[5][4-1];
        newSeq[5][2] = numSeq[5][7-1];
        newSeq[5][3] = numSeq[5][2-1];
        newSeq[5][4] = numSeq[5][5-1];
        newSeq[5][5] = numSeq[5][8-1];
        newSeq[5][6] = numSeq[5][3-1];
        newSeq[5][7] = numSeq[5][6-1];
        newSeq[5][8] = numSeq[5][9-1];

        for ( int i =0 ; i < 6 ; i++)
        {
            for ( int j =0 ; j<9; j++)
            {
                finalsequence += newSeq[i][j];
            }
        }

        return finalsequence;

    }

}

