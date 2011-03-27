/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Modele.Mapa;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author x
 */
public class MapaPrzystankow extends MapaAbstract {

    public MapaPrzystankow() {
        sciezka = "data" + File.separator + "MapaPrzystankow";
        File plikMapy = new File(sciezka);
        listaKrawedzi = createListaKrawedzi(plikMapy);
        listaWierzch = createListaWierzcholkow(listaKrawedzi);
    }

    public void showPath(String poczatek, String koniec) {
        calculateDandP(poczatek);
        //writeDandP();
        Stack<String> s = getPath(koniec);
        showPath(s);
    }

    public static void main(String[] args) {
        String poczatek = "4571";
        String koniec = "4581";
        MapaPrzystankow m = new MapaPrzystankow();
        m.updateMapa();
        //m.showPath(poczatek, koniec);
        //m.showCost(koniec);
    }

    public void updateMapa() {
        Stack<String> s = null;
        File f = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        File plikMapy = new File(sciezka);
        setListaKrawedzi(createListaKrawedzi(plikMapy));
        setListaWierzch(createListaWierzcholkow(getListaKrawedzi()));
        int i = 1;
        for (String przystanekPoczatkowy : listaWierzch) {
            try {
                calculateDandP(przystanekPoczatkowy);
                System.out.println("Tworze plik polaczen dla przystanku " + przystanekPoczatkowy + " [" + i + "/" + listaWierzch.size() + "]");
                boolean catalogCreated = new File("data" + File.separator + "SciezkiWzglPrzystankow").mkdir();
                boolean fileCreated = new File("data" + File.separator + "SciezkiWzglPrzystankow" + File.separator + przystanekPoczatkowy).createNewFile();
                f = new File("data" + File.separator + "SciezkiWzglPrzystankow" + File.separator + przystanekPoczatkowy);
                fw = new FileWriter(f);
                bw = new BufferedWriter(fw);
                for (String przystanekKoncowy : listaWierzch) {
                    s = getPath(przystanekKoncowy);
                    String path = getPath(s);
                    if (path.startsWith(przystanekPoczatkowy)) {
                        bw.write(przystanekKoncowy + "#" + path);
                        bw.newLine();
                    }
                }
                bw.flush();
                bw.close();
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(MapaPrzystankow.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }
    }
}
