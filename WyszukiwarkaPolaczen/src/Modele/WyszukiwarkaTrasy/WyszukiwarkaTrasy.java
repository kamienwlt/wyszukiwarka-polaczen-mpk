/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele.WyszukiwarkaTrasy;

import Modele.Mapa.MapaInterface;
import Modele.Mapa.MapaPolaczen;
import Modele.Mapa.MapaPrzystankow;

/**
 *
 * @author x
 */
public class WyszukiwarkaTrasy implements WyszukiwarkaTrasyInterface{

    public final MapaInterface MAPA_PRZYSTANKOW = new MapaPrzystankow();
    public final MapaInterface MAPA_POLACZEN = new MapaPolaczen();
    private MapaInterface mapa;
    private String poczatek;
    private String koniec;

    public WyszukiwarkaTrasy(MapaInterface mapa){
        this.mapa = mapa;
    }

    public void setMapa(MapaInterface mapa) {
        this.mapa = mapa;
    }

    public void setPoczatek(String poczatek) {
        this.poczatek = poczatek;
    }

    public void setKoniec(String koniec) {
        this.koniec = koniec;
    }

    public void showPath() {
        mapa.showPath(poczatek, koniec);
    }

}
