/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele.WyszukiwarkaTrasy;

import Modele.Mapa.MapaInterface;

/**
 *
 * @author x
 */
public interface WyszukiwarkaTrasyInterface {
    public void setMapa(MapaInterface mapa);
    public void setPoczatek(String poczatek);
    public void setKoniec(String koniec);
    public void showPath();
}
