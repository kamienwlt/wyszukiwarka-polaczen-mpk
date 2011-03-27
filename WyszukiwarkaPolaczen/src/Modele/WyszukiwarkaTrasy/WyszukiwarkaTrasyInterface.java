/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele.WyszukiwarkaTrasy;

/**
 *
 * @author x
 */
public interface WyszukiwarkaTrasyInterface {
    public static final String LICZBA_PRZESIADEK = "LICZBA_PRZESIADEK";
    public static final String LICZBA_PRZYSTANKOW = "LICZBA_PRZYSTANKOW";
    public void setKryterium(String kryterium);
    public String getPath(String poczatek, String koniec);
}
