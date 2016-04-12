package ua.reapersworkgroup.reachsolomon.util;

import java.util.Arrays;

public class GFPrimitives {

    public static final int ZERO_DEGREE = -1;
    private static final int WRONG_DEGREE = -3;

    private int codeSize;
    private int fieldNumbersQuantity; // with zero
    private boolean[][] alphaCodes;

    public GFPrimitives(int codeSize) {
        this.codeSize = codeSize;
        fieldNumbersQuantity = (int) Math.pow(2, codeSize);
        alphaCodes = new boolean[fieldNumbersQuantity][codeSize];
        generateAlphaCodes();
    }

    private void generateAlphaCodes() {
        // basic codes
        for (int i = 0; i < codeSize; i++) {
            alphaCodes[i + 1][codeSize - i - 1] = true;
        }

        // by increasing
        for (int i = codeSize + 1; i < fieldNumbersQuantity; i++)
            for (int j = 0; j < codeSize; j++)
                alphaCodes[i][j] = alphaCodes[i - (codeSize - 1)][j] ^ alphaCodes[i - codeSize][j];
    }

    public boolean[] getAplphaCode(int aDegree) {
        return alphaCodes[aDegree + 1];
    }

    public String showAplhaCode(int aDegree) {
        StringBuilder sb = new StringBuilder(codeSize);
        for (int i = 0; i < codeSize; i++) sb.append(alphaCodes[aDegree][i] ? 1 : 0);
        return sb.toString();
    }

    public int getAlphaDegreeByCode(boolean[] code) {
        if (Arrays.equals(alphaCodes[0], code)) return 1;
        for (int i = 0; i < alphaCodes.length; i++)
            if (Arrays.equals(alphaCodes[i], code)) return i - 1;
        return WRONG_DEGREE;
    }

    public int getAddResult(int a1Degree, int a2Degree) {
        boolean[] a1Code = alphaCodes[a1Degree + 1];
        boolean[] a2Code = alphaCodes[a2Degree + 1];
        boolean[] resultCode = new boolean[codeSize];
        for (int i = 0; i < resultCode.length; i++) {
            resultCode[i] = a1Code[i] ^ a2Code[i];
        }
        return getAlphaDegreeByCode(resultCode);
    }

    public int getMultiResult(int a1Degree, int a2Degree) {
        return (a1Degree + a2Degree) % (fieldNumbersQuantity - 1);
    }

    public int getDivResult(int a1Degree, int a2Degree) {
        int remainder = a2Degree % (fieldNumbersQuantity - 1);
        if (remainder <= a1Degree) return a1Degree - remainder;
        else return fieldNumbersQuantity - 1 - remainder + a1Degree;
    }

    public int getCodeSize() {
        return codeSize;
    }

    public boolean[][] getAlphaCodes() {
        return alphaCodes;
    }
}
