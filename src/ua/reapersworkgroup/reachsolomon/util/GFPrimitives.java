package ua.reapersworkgroup.reachsolomon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class GFPrimitives {

    public static final int ZERO_DEGREE = -1;
    private static final int WRONG_DEGREE = -3;

    private int codeSize;
    private int fieldNumbersQuantity; // with zero
    private boolean[][] alphaCodes;

    public GFPrimitives(int codeSize) throws Exception {
        this.codeSize = codeSize;
        fieldNumbersQuantity = (int) Math.pow(2, codeSize);
        alphaCodes = new boolean[fieldNumbersQuantity][codeSize];
        generateAlphaCodes();
    }

    public static byte[] toBytes(boolean[] input) {
        byte[] toReturn = new byte[input.length / 8];
        for (int entry = 0; entry < toReturn.length; entry++) {
            for (int bit = 0; bit < 8; bit++) {
                if (input[entry * 8 + bit]) {
                    toReturn[entry] |= (128 >> bit);
                }
            }
        }

        return toReturn;
    }

    private boolean[] loadPrimitivePoly() throws Exception {
        File file = new File("primitives");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> parts = reader.lines().filter(
                s -> Integer.parseInt(s.substring(2, s.indexOf(' '))) == codeSize).flatMap(
                s -> stream(s.substring(s.indexOf(' ') + 1, s.length()).split(" "))
        ).collect(Collectors.toList());

        if (parts.isEmpty())
            throw new Exception("Primitive polynomial for your code size isnt fond in file. Try some smaller size");
        boolean[] result = new boolean[codeSize];
        parts.stream().filter(s -> !s.equals("+")).forEach(s -> {
            switch (s) {
                case "1":
                    result[codeSize - 1] = true;
                    break;
                case "x":
                    result[codeSize - 2] = true;
                    break;
                default:
                    result[codeSize - 1 - Integer.parseInt(s.substring(2, s.length()))] = true;
                    break;
            }
        });

        return result;
    }

    private boolean[] shiftArray(boolean[] array) {
        boolean[] result = new boolean[array.length];
        System.arraycopy(array, 1, result, 0, result.length - 1);
        result[array.length - 1] = false;
        return result;
    }

    private void generateAlphaCodes() throws Exception {
        boolean[] primitive = loadPrimitivePoly();
        alphaCodes[1][codeSize - 1] = true; // zero degree
        for (int i = 2; i < fieldNumbersQuantity; i++) {
            boolean[] newCode = shiftArray(alphaCodes[i - 1]);
            if (alphaCodes[i - 1][0]) for (int j = 0; j < newCode.length; j++) newCode[j] ^= primitive[j];
            alphaCodes[i] = newCode;
        }
        //region Old
        /*// basic codes
        for (int i = 0; i < codeSize; i++) alphaCodes[i + 1][codeSize - i - 1] = true;

        // primitive poly
        alphaCodes[codeSize + 1] = polyToBoolArray(primitivePoly);


        // by increasing
        for (int i = codeSize + 2; i < fieldNumbersQuantity; i++) { // pos in table

            for (int j = 0; j < primitivePoly.getMonomials().size(); j++) { // by each bit
                Monomial tmp = primitivePoly.getMonomials().get(j);
                //int multiResult = getMultiResult(1, tmp.getADegree());
                tmp.setADegree(tmp.getADegree() + 1);
            }
            alphaCodes[i] = polyToBoolArray(primitivePoly);
        }
        System.out.println();*/
        //endregion
    }

    public boolean[] getAlphaCode(int aDegree) {
        return alphaCodes[aDegree + 1];
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
