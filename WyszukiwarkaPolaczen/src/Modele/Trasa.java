/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele;

/**
 *
 * @author x
 */
public class Trasa {
    private String kierunek;
    private Droga droga;

    public Trasa(String kierunek, Droga droga){
        this.kierunek = kierunek;
        this.droga = droga;
    }

    /**
     * @return the kierunek
     */
    public String getKierunek() {
        return kierunek;
    }

    /**
     * @param kierunek the kierunek to set
     */
    public void setKierunek(String kierunek) {
        this.kierunek = kierunek;
    }

    /**
     * @return the droga
     */
    public Droga getDroga() {
        return droga;
    }

    /**
     * @param droga the droga to set
     */
    public void setDroga(Droga droga) {
        this.droga = droga;
    }
}
