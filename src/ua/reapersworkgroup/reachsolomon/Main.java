package ua.reapersworkgroup.reachsolomon;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        publicTest(3, 2, 1, 3, 5);
    }

    private static void publicTest(int codeSize, int errorsSize, int... numbers) {
        RSCEncoder rscEncoder = new RSCEncoder(codeSize, errorsSize);
        System.out.println("Encoding numbers sequence : ");
        Arrays.stream(numbers).forEach(ints -> System.out.print(ints + " "));
        System.out.println();
        printResult(rscEncoder.encode(numbers));
    }

    private static void printResult(ArrayList<Integer> results) {
        System.out.println("And they are encoded to");
        results.stream().forEach(integer -> System.out.print(integer + " "));
        System.out.println("\n");
    }

    //region Private testing
    /*private static void test() {
        GFPrimitives primitives = new GFPrimitives(4);
        for (int i = 0; i < primitives.getAlphaCodes().length; i++) {
            for (boolean b : primitives.getAlphaCodes()[i]) {
                System.out.print((b ? 1 : 0) + " ");
            }
            System.out.println();
        }
        System.out.println();

        GFOperations operations = new GFOperations(3);
        Polynomial p = new Polynomial();
        p.addMonomial(4, 0);
        p.addMonomial(3, 4);
        p.addMonomial(3, 3);
        p.addMonomial(2, 0);
        p.addMonomial(3, 2);
        p.addMonomial(2, 6);
        p.addMonomial(2, 5);
        p.addMonomial(1, 2);
        p.addMonomial(3, 1);
        p.addMonomial(2, 5);
        p.addMonomial(2, 4);
        p.addMonomial(1, 1);
        p.addMonomial(2, 3);
        p.addMonomial(1, 0);
        p.addMonomial(1, 6);
        p.addMonomial(0, 3);
        operations.simplifyPoly(p);
        p.getMonomials().forEach(m -> System.out.print(m.getXDegree() + "/" + m.getADegree() + " "));
        System.out.println("\n");

        RSCEncoder encoder = new RSCEncoder(3, 2);
        encoder.getGeneratorPoly().getMonomials().forEach(m -> System.out.print(m.getXDegree() + "/" + m.getADegree() + " "));
        System.out.println("\n");

        Polynomial p1 = new Polynomial();
        p1.addMonomial(6, 1);
        p1.addMonomial(5, 3);
        p1.addMonomial(4, 5);
        Polynomial p2 = encoder.getGeneratorPoly();
        Polynomial[] result = operations.divPolys(p1, p2);
        result[0].getMonomials().forEach(m -> System.out.print(m.getXDegree() + "/" + m.getADegree() + " "));
        System.out.println();
        result[1].getMonomials().forEach(m -> System.out.print(m.getXDegree() + "/" + m.getADegree() + " "));
        System.out.println("\n");

        RSCEncoder rscEncoder = new RSCEncoder(3, 2);
        printResult(rscEncoder.encode(1, 3, 5));
        System.out.println("\n");
    }*/
    //endregion
}
