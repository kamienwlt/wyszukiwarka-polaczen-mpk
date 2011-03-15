/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Modele.Mapa;

import java.io.File;
import java.util.Stack;


/**
 *
 * @author x
 */
public class MapaPrzystankow extends MapaAbstract{

    public MapaPrzystankow() {
        sciezka = "data" + File.separator + "MapaPrzystankow";
        File plikMapy = new File(sciezka);
        listaKrawedzi = createListaKrawedzi(plikMapy);
        listaWierzch = createListaWierzcholkow(listaKrawedzi);
    }

    public void showPath(String poczatek, String koniec){
        calculateDandP(poczatek);
        //writeDandP();
        Stack<String> s = getPath(koniec);
        showPath(s);
    }

    public static void main(String[] args) {
        String poczatek = "4572";
        String koniec = "1421";
        MapaPrzystankow m = new MapaPrzystankow();
        m.showPath(poczatek, koniec);
        m.showCost(koniec);
    }

    public void updateMapa() {
        File plikMapy = new File(sciezka);
        listaKrawedzi = createListaKrawedzi(plikMapy);
        listaWierzch = createListaWierzcholkow(listaKrawedzi);
    }
}
