/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele.Rozklad;

/**
 *
 * @author x
 */

//czesc wzorca Kompozyt(Composite) - odpowiednik klasy Component
public abstract class RozkladAbstract {
    protected RozkladAbstract rodzic;

    public abstract String pokazRozklad();
    public void addRozklad(RozkladAbstract rozklad){}
    public void removeRozklad(RozkladAbstract rozklad){}
    public RozkladAbstract getRozklad(int linia){
        return null;
    }
    public abstract String getLinie();

    /**
     * @return the rodzic
     */
    public RozkladAbstract getRodzic() {
        return rodzic;
    }

    /**
     * @param rodzic the rodzic to set
     */
    public void setRodzic(RozkladAbstract rodzic) {
        this.rodzic = rodzic;
    }
}
