package ua.reapersworkgroup.reachsolomon;

import ua.reapersworkgroup.reachsolomon.mathtype.Monomial;
import ua.reapersworkgroup.reachsolomon.mathtype.Polynomial;
import ua.reapersworkgroup.reachsolomon.util.GFOperations;

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

    public ArrayList<Integer> encode(int... numbers) {
        int fullSize = numbers.length + 2 * errorsSize;
        Polynomial sourcePoly = new Polynomial();

        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] > (int) Math.pow(2, codeSize) - 2 || numbers[i] < 0)
                throw new IllegalArgumentException("Some of numbers dose not contains in current Galua field");
            sourcePoly.addMonomial(fullSize - 1 - i, numbers[i]);
        }

        ArrayList<Integer> resultNumbers = new ArrayList<>();
        Arrays.asList(operations.divPolys(sourcePoly, generatorPoly)).stream().flatMap(
                polynomial -> polynomial.getMonomials().stream()).forEach(
                monomial -> resultNumbers.add(monomial.getADegree()));

        return resultNumbers;
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
