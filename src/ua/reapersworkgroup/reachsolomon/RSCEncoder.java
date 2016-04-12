package ua.reapersworkgroup.reachsolomon;

import ua.reapersworkgroup.reachsolomon.mathtype.Monomial;
import ua.reapersworkgroup.reachsolomon.mathtype.Polynomial;
import ua.reapersworkgroup.reachsolomon.util.GFOperations;
import ua.reapersworkgroup.reachsolomon.util.GFPrimitives;

import java.util.ArrayList;
import java.util.Arrays;

public class RSCEncoder {

    private final int codeSize;
    private final int errorsSize;

    private final GFOperations operations;

    private final Polynomial generatorPoly;

    public RSCEncoder(int codeSize, int errorsSize) {
        this.codeSize = codeSize;
        this.errorsSize = errorsSize;
        operations = new GFOperations(codeSize);
        generatorPoly = createGeneratorPoly();
    }

    public static boolean[] binaryToBools(byte[] ints) {
        boolean[] result = new boolean[ints.length];
        for (int i = 0; i < result.length; i++) {
            if (ints[i] == 1) result[i] = true;
            else if (ints[i] == 0) result[i] = false;
            else throw new IllegalArgumentException("Some of numbers isn't 0 or 1");
        }
        return result;
    }

    private Polynomial createGeneratorPoly() {
        int scopesNum = 2 * errorsSize;
        Polynomial[] scopes = new Polynomial[scopesNum];
        Polynomial result;

        for (int i = 0; i < scopesNum; i++) {
            ArrayList<Monomial> monomials = new ArrayList<>();
            monomials.add(new Monomial(1, 0));
            monomials.add(new Monomial(0, i + 1));
            scopes[i] = new Polynomial(monomials);
        }

        result = scopes[0];
        for (int i = 0; i < scopesNum - 1; i++) {
            result = operations.multiPolys(result, scopes[i + 1]);
            operations.simplifyPoly(result);
        }

        return result;
    }

    public boolean[] encode(boolean... bits) {
        if (bits.length % codeSize != 0)
            throw new IllegalArgumentException("Illegal numbers of bits. Must be " + codeSize + "N bits");

        int[] numbers = new int[bits.length / codeSize];
        GFPrimitives primitives = operations.getPrimitives();
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = primitives.getAlphaDegreeByCode(Arrays.copyOfRange(bits, i * codeSize, (i + 1) * codeSize));

        ArrayList<Monomial> monomials = getFullRemainderMonomials(numbers);

        boolean[] result = new boolean[bits.length + codeSize * monomials.size()];
        System.arraycopy(bits, 0, result, 0, bits.length);
        for (int i = 0; i < monomials.size(); i++) {
            boolean[] aCode = primitives.getAplphaCode(monomials.get(i).getADegree());
            for (int j = 0; j < codeSize; j++) {
                result[bits.length + i * codeSize + j] = aCode[j];
            }
        }

        return result;
    }

    public ArrayList<Integer> encode(int... numbers) {
        ArrayList<Integer> resultNumbers = new ArrayList<>();

        for (int i : numbers) resultNumbers.add(i);
        getFullRemainderMonomials(numbers).stream().forEach(
                monomial -> resultNumbers.add(monomial.getADegree())
        );

        return resultNumbers;
    }

    private ArrayList<Monomial> getFullRemainderMonomials(int[] numbers) {
        int fullSize = numbers.length + 2 * errorsSize;
        Polynomial sourcePoly = new Polynomial();

        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] > (int) Math.pow(2, codeSize) - 2 || numbers[i] < 0)
                throw new IllegalArgumentException("Some of numbers dose not contains in current Galua field");
            sourcePoly.addMonomial(fullSize - 1 - i, numbers[i]);
        }

        Polynomial remainder = operations.divPolys(sourcePoly, generatorPoly)[1]; //need only remainder
        inflatePoly(remainder); // if not all x degrees are exists

        return remainder.getMonomials();
    }

    private void inflatePoly(Polynomial remainder) {
        for (int i = 0; i < 2 * errorsSize; i++)
            if (remainder.indexByX(i) == -1) remainder.addMonomial(i, GFPrimitives.ZERO_DEGREE);
        operations.sortPolyByXDesc(remainder);
    }

    public int getCodeSize() {
        return codeSize;
    }

    public int getErrorsSize() {
        return errorsSize;
    }

    public GFOperations getOperations() {
        return operations;
    }

    public Polynomial getGeneratorPoly() {
        return generatorPoly;
    }
}
