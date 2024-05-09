package model;

/**
 *
 * @author janet
 */
public class ContaBonus extends Conta {
    
    private int bonus;
    
    public ContaBonus(int numero) {
        super(numero);
        this.bonus = 10;
    }
    
    public double getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
    
}
