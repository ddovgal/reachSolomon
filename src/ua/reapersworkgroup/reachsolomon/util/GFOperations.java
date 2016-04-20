package ua.reapersworkgroup.reachsolomon.util;

import ua.reapersworkgroup.reachsolomon.mathtype.Monomial;
import ua.reapersworkgroup.reachsolomon.mathtype.Polynomial;

import java.util.ArrayList;
import java.util.Collections;

public class GFOperations {

    private final int codeSize;
    private final GFPrimitives primitives;

    public GFOperations(int codeSize) throws Exception {
        this.codeSize = codeSize;
        primitives = new GFPrimitives(this.codeSize);
    }

    public Monomial addMonos(Monomial m1, Monomial m2) {
        if (m1.getXDegree() != m2.getXDegree()) throw new IllegalArgumentException("X degrees must be same");
        int newADegree = primitives.getAddResult(m1.getADegree(), m2.getADegree());
        return new Monomial(m1.getXDegree(), newADegree);
    }

    public Monomial multMonos(Monomial m1, Monomial m2) {
        int newXDegree = m1.getXDegree() + m2.getXDegree();
        int newADegree = primitives.getMultiResult(m1.getADegree(), m2.getADegree());
        return new Monomial(newXDegree, newADegree);
    }

    public Monomial divMonos(Monomial m1, Monomial m2) {
        int newXDegree = m1.getXDegree() - m2.getXDegree();
        int newADegree = primitives.getDivResult(m1.getADegree(), m2.getADegree());
        return new Monomial(newXDegree, newADegree);
    }

    public Polynomial simplifyPoly(Polynomial poly) {
        sortPolyByXDesc(poly);

        ArrayList<Monomial> monomials = poly.getMonomials();
        for (int i = 0; i < monomials.size() - 1; i++) {
            int currentXDegree = monomials.get(i).getXDegree();

            if (currentXDegree == monomials.get(i + 1).getXDegree()) {
                // if equal monomials - they destroy each other
                if (monomials.get(i).getADegree() == monomials.get(i + 1).getADegree()) {
                    poly.getMonomials().remove(i);
                    poly.getMonomials().remove(i);
                    i--;
                } else { // else - just simplifying
                    poly.getMonomials().get(i).setADegree(
                            primitives.getAddResult(
                                    poly.getMonomials().get(i).getADegree(),
                                    poly.getMonomials().get(i + 1).getADegree()
                            )
                    );
                    poly.getMonomials().remove(i + 1);
                    i--;
                }
            }
        }
        return poly;
    }

    //region Old addPoly
    /*public Polynomial addPoly(Polynomial p1, Polynomial p2) {
        sortPolyByXDesc(p1);
        sortPolyByXDesc(p2);

        *//*Polynomial bigger, smaller;
        if (p1.getMonomials().size() > p2.getMonomials().size()) {
            bigger = p1;
            smaller = p2;
        } else {
            bigger = p2;
            smaller = p1;
        }*//*
        for (int i = 0; i < p1*//*bigger*//*.getMonomials().size(); i++) {
            int currentXDegree = p1*//*bigger*//*.getMonomials().get(i).getXDegree();
            int smallerXDegreeIndex = p2*//*smaller*//*.indexByX(currentXDegree);
            if (smallerXDegreeIndex != -1) {
                p1*//*bigger*//*.getMonomials().get(i).setADegree(
                        primitives.getAddResult(
                                p1*//*bigger*//*.getMonomials().get(i).getADegree(),
                                p2*//*smaller*//*.getMonomials().get(smallerXDegreeIndex).getADegree()
                        )
                );
                p2*//*smaller*//*.getMonomials().remove(smallerXDegreeIndex);
            }
        }
        if (!p2*//*smaller*//*.getMonomials().isEmpty()) p1*//*bigger*//*.getMonomials().addAll(p2*//*smaller*//*.getMonomials());
        sortPolyByXDesc(p1*//*bigger*//*);
        return p1*//*bigger*//*;
    }*/
    //endregion

    public Polynomial addPolys(Polynomial p1, Polynomial p2) {
        Polynomial result = new Polynomial();
        result.getMonomials().addAll(p1.getMonomials());
        result.getMonomials().addAll(p2.getMonomials());
        p1.getMonomials().clear();
        p2.getMonomials().clear();
        return simplifyPoly(result);
    }

    public Polynomial multiPolys(Polynomial p1, Polynomial p2) {
        Polynomial result = new Polynomial();
        for (int i = 0; i < p1.getMonomials().size(); i++) {
            for (int j = 0; j < p2.getMonomials().size(); j++) {
                Monomial m1 = p1.getMonomials().get(i);
                Monomial m2 = p2.getMonomials().get(j);
                Monomial monomial = new Monomial(
                        m1.getXDegree() + m2.getXDegree(),
                        primitives.getMultiResult(m1.getADegree(), m2.getADegree())
                );
                result.addMonomial(monomial);
            }
        }
        p1.getMonomials().clear();
        p2.getMonomials().clear();
        return simplifyPoly(result);
    }

    public Polynomial[] divPolys(Polynomial p1, Polynomial p2) {
        Polynomial result = new Polynomial(); // result of division
        sortPolyByXDesc(p1);
        sortPolyByXDesc(p2);
        ArrayList<Monomial> m2 = p2.getMonomials();
        if (p1.getMonomials().get(0).getXDegree() < m2.get(0).getXDegree())
            throw new IllegalArgumentException("First polynom must have degree, bigger than second");

        Monomial diffM; // part of result in each iteration
        Polynomial tmp = new Polynomial(); // polynom2 * diffM. Temporary multiply version of polynom2
        while (p1.getMonomials().get(0).getXDegree() >= m2.get(0).getXDegree()) {
            diffM = divMonos(p1.getMonomials().get(0), m2.get(0));
            result.addMonomial(diffM);

            for (Monomial tmpM : m2) {
                tmp.addMonomial(
                        tmpM.getXDegree() + diffM.getXDegree(),
                        primitives.getMultiResult(tmpM.getADegree(), diffM.getADegree()));
            }

            p1 = addPolys(p1, tmp);
        }

        return new Polynomial[]{result, p1};
    }

    public void sortPolyByXDesc(Polynomial p1) {
        Collections.sort(p1.getMonomials(), (o1, o2) -> {
            if (o1.getXDegree() > o2.getXDegree()) return -1;
            if (o1.getXDegree() < o2.getXDegree()) return 1;
            return 0;
        });
    }

    public int getCodeSize() {
        return codeSize;
    }

    public GFPrimitives getPrimitives() {
        return primitives;
    }
}
