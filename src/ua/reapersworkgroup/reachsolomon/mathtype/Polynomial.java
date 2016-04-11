package ua.reapersworkgroup.reachsolomon.mathtype;

import java.util.ArrayList;

public class Polynomial {

    private ArrayList<Monomial> monomials;

    public Polynomial() {
        monomials = new ArrayList<>();
    }

    public Polynomial(int capacity) {
        monomials = new ArrayList<>(capacity);
    }

    public Polynomial(ArrayList<Monomial> monomials) {
        this.monomials = monomials;
    }

    public void addMonomial(Monomial monomial) {
        monomials.add(monomial);
    }

    public void addMonomial(int xDegree, int aDegree) {
        addMonomial(new Monomial(xDegree, aDegree));
    }

    public int indexByX(int xDegree) {
        for (int i = 0; i < monomials.size(); i++) {
            if (monomials.get(i).getXDegree() == xDegree) return i;
        }
        return -1;
    }

    public int indexByA(int aDegree) {
        for (int i = 0; i < monomials.size(); i++) {
            if (monomials.get(i).getADegree() == aDegree) return i;
        }
        return -1;
    }

    public ArrayList<Monomial> getMonomials() {
        return monomials;
    }
}
