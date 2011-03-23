/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele.Rozklad;

/**
 *
 * @author x
 */

//czesc wzorca Kompozyt(Composite) - odpowiednik klasy Leaf, tzn podstawowy element struktury
public class RozkladLinii extends RozkladAbstract{
    private int linia;
    private String rozklad;

    public RozkladLinii(int linia, String rozklad){
        this.rozklad = rozklad;
        this.linia = linia;
    }

    public RozkladLinii(){
        rozklad = "";
        linia = -1;
    }

    @Override
    public String pokazRozklad() {
        return rozklad;
    }

    @Override
    public String getLinie() {
        return "" + getLinia();
    }

    /**
     * @return the linia
     */
    public int getLinia() {
        return linia;
    }

    /**
     * @param linia the linia to set
     */
    public void setLinia(int linia) {
        this.linia = linia;
    }

}
