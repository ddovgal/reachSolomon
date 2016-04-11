package ua.reapersworkgroup.reachsolomon.mathtype;

public class Monomial {

    private int xDegree;
    private int aDegree;

    public Monomial(int xDegree, int aDegree) {
        this.xDegree = xDegree;
        this.aDegree = aDegree;
    }

    public int getXDegree() {
        return xDegree;
    }

    public void setXDegree(int xDegree) {
        this.xDegree = xDegree;
    }

    public int getADegree() {
        return aDegree;
    }

    public void setADegree(int aDegree) {
        this.aDegree = aDegree;
    }
}
