package ua.reapersworkgroup.reachsolomon;

import ua.reapersworkgroup.reachsolomon.mathtype.Monomial;
import ua.reapersworkgroup.reachsolomon.mathtype.Polynomial;
import ua.reapersworkgroup.reachsolomon.util.GFOperations;
import ua.reapersworkgroup.reachsolomon.util.GFPrimitives;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class RSCEncoder {

    private final int codeSize;
    private final int errorsSize;

    private final GFOperations operations;

    private final Polynomial generatorPoly;

    public RSCEncoder(int codeSize, int errorsSize) throws Exception {
        this.codeSize = codeSize;
        this.errorsSize = errorsSize;
        operations = new GFOperations(codeSize);
        generatorPoly = createGeneratorPoly();
    }

    public void encodeFile(String path) throws Exception {
        if (codeSize != 8) throw new IllegalArgumentException("Yor code size must be only 8");
        byte[] source = new byte[(int) new File(path).length()];
        new FileInputStream(path).read(source);
        FileOutputStream fos = new FileOutputStream(
                path.replace(
                        path.substring(
                                path.lastIndexOf('.'),
                                path.length()
                        ),
                        ".rs"
                )
        );
        fos.write(encode(source));
        fos.close();
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

    public byte[] encode(byte[] bytes) {
        ArrayList<Integer> resultNumbers = new ArrayList<>();
        for (byte i : bytes) resultNumbers.add((int) i);

        getFullRemainderMonomials(bytes).stream().forEach(
                monomial -> resultNumbers.add(monomial.getADegree())
        );

        byte[] result = new byte[resultNumbers.size()];
        for (int i = 0; i < resultNumbers.size(); i++)
            result[i] = resultNumbers.get(i).byteValue();
        return result;
    }

    public ArrayList<Integer> encode(int... ints) {
        ArrayList<Integer> resultNumbers = new ArrayList<>();
        byte[] bytes = new byte[ints.length];

        for (int i = 0; i < ints.length; i++) {
            resultNumbers.add(ints[i]);
            bytes[i] = (byte) ints[i];
        }

        getFullRemainderMonomials(bytes).stream().forEach(
                monomial -> resultNumbers.add(monomial.getADegree())
        );

        return resultNumbers;
    }

    private ArrayList<Monomial> getFullRemainderMonomials(byte[] numbers) {
        int fullSize = numbers.length + 2 * errorsSize;
        Polynomial dataPoly = new Polynomial();

        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] > Math.pow(2, codeSize) - 2 || numbers[i] < 0)
                throw new IllegalArgumentException("Some of numbers dose not contains in current Galua field");
            dataPoly.addMonomial(fullSize - 1 - i, numbers[i]);
        }

        Polynomial remainder = operations.divPolys(dataPoly, generatorPoly)[1]; //need only remainder
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
