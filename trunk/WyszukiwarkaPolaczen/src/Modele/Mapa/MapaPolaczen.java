/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Modele.Mapa;

/**
 *
 * @author x
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapaPolaczen extends MapaAbstract {

    public MapaPolaczen() {
        sciezka = "data" + File.separator + "MapaPolaczen";
        File plikMapy = new File(sciezka);
        listaKrawedzi = createListaKrawedzi(plikMapy);
        listaWierzch = createListaWierzcholkow(listaKrawedzi);
        String[] tab = new String[10];

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
        MapaPolaczen m = new MapaPolaczen();
        m.updateMapaNonConcurrent();
        //m.showPath(poczatek, koniec);
        //m.showCost(koniec);
    }

    public void updateMapa(LinkedList<String> lista) {
        int coresCount = Runtime.getRuntime().availableProcessors();
        int size = lista.size() / coresCount;
        for (int i = 0; i < coresCount; i++) {
            Thread t = new Thread(new UpdateThread(lista, i * size, (i + 1) * size));
            t.start();
        }
    }

    public void updateMapa() {
        updateMapa(getListaWierzch());
    }

    public void updateMapaNonConcurrent() {
        updateMapaNonConcurrent(listaWierzch);
    }

    public void updateMapaNonConcurrent(LinkedList<String> lista) {
        Stack<String> s = null;
        File f = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        File plikMapy = new File(sciezka);
        int i = 1;
        for (String przystanekPoczatkowy : listaWierzch) {
            try {
                calculateDandP(przystanekPoczatkowy);
                System.out.println("Tworze plik polaczen dla przystanku " + przystanekPoczatkowy + " [" + i + "/" + listaWierzch.size() + "]");
                boolean catalogCreated = new File("data" + File.separator + "SciezkiWzglPrzesiadek").mkdir();
                boolean fileCreated = new File("data" + File.separator + "SciezkiWzglPrzesiadek" + File.separator + przystanekPoczatkowy).createNewFile();
                f = new File("data" + File.separator + "SciezkiWzglPrzesiadek" + File.separator + przystanekPoczatkowy);
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

    private class UpdateThread implements Runnable {

        LinkedList<String> lista;
        int i;
        int i0;

        public UpdateThread(LinkedList<String> lista, int i, int i0) {
            this.lista = lista;
            this.i = i;
            this.i0 = i0;
        }

        public void run() {
            Stack<String> s = null;
            File f = null;
            FileWriter fw = null;
            BufferedWriter bw = null;
            for (int j = i; j < i0 && j < lista.size(); j++) {
                String przystanekPoczatkowy = lista.get(j);
                System.out.println("Tworze plik polaczen dla przystanku " + przystanekPoczatkowy + " [" + i + "/" + lista.size() + "]");
                try {
                    calculateDandP(przystanekPoczatkowy);
                    boolean catalogCreated = new File("data" + File.separator + "SciezkiWzglPrzesiadek").mkdir();
                    boolean fileCreated = new File("data" + File.separator + "SciezkiWzglPrzesiadek" + File.separator + przystanekPoczatkowy).createNewFile();
                    f = new File("data" + File.separator + "SciezkiWzglPrzesiadek" + File.separator + przystanekPoczatkowy);
                    fw = new FileWriter(f);
                    bw = new BufferedWriter(fw);
                    for (String przystanekKoncowy : lista) {
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
                    Logger.getLogger(MapaPolaczen.class.getName()).log(Level.SEVERE, null, ex);
                }
                i++;
            }
        }
    }
}
