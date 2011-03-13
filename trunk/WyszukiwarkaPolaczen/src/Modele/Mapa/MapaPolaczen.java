/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele.Mapa;

/**
 *
 * @author x
 */
import java.io.File;
import java.util.Stack;

public class MapaPolaczen extends MapaAbstract{

    public MapaPolaczen() {
        sciezka = "data" + File.separator + "MapaPolaczen";
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
        MapaPolaczen m = new MapaPolaczen();
        m.showPath(poczatek, koniec);
        m.showCost(koniec);
    }

}
