/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele;

/**
 *
 * @author x
 */
public class KrawedzPrzystankow {
    private String poczatek;
    private String koniec;

    public KrawedzPrzystankow(String poczatek, String koniec){
        this.koniec = koniec;
        this.poczatek = poczatek;
    }

    /**
     * @return the poczatek
     */
    public String getPoczatek() {
        return poczatek;
    }

    /**
     * @param poczatek the poczatek to set
     */
    public void setPoczatek(String poczatek) {
        this.poczatek = poczatek;
    }

    /**
     * @return the koniec
     */
    public String getKoniec() {
        return koniec;
    }

    /**
     * @param koniec the koniec to set
     */
    public void setKoniec(String koniec) {
        this.koniec = koniec;
    }

    public void showKrawedz() {
        System.out.println(poczatek + " " + koniec);
    }


}
