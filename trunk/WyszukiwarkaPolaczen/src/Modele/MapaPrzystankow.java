/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author x
 */
public class MapaPrzystankow {
    private LinkedList<KrawedzPrzystankow> listaKrawedzi;
    private LinkedList<String> listaWierzch;
    private String sciezka = "data" + File.separator + "MapaPrzystankow";

    public MapaPrzystankow(){
        File plikMapy = new File(sciezka);
        listaKrawedzi = createListaKrawedzi(plikMapy);
        listaWierzch = createListaWierzcholkow(listaKrawedzi);
    }

    private LinkedList<KrawedzPrzystankow> createListaKrawedzi(File plikMapy) {
        LinkedList<KrawedzPrzystankow> wynik = null;
        try {
            Scanner input = new Scanner(plikMapy);
            wynik = new LinkedList<KrawedzPrzystankow>();
            while(input.hasNextLine()){
                String s = input.nextLine();
                //rozdzielenie pelnych nazw przystankow
                String[] tab = s.split("#");
                for(int i = 1; i < tab.length; i++){
                    //wydzielenie numerow przystankow
                    String poczatek = tab[0].split(" ")[0];
                    String koniec = tab[i].split(" ")[0];
                    KrawedzPrzystankow rob = new KrawedzPrzystankow(poczatek, koniec);
                    wynik.add(rob);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MapaPrzystankow.class.getName()).log(Level.SEVERE, null, ex);
        }

        return wynik;
    }

    public void showKrawedzie(){
        for (KrawedzPrzystankow k : listaKrawedzi){
            k.showKrawedz();
        }
    }

    public static void main(String[] args){
        MapaPrzystankow m = new MapaPrzystankow();
        m.showKrawedzie();
    }

    private LinkedList<String> createListaWierzcholkow(LinkedList<KrawedzPrzystankow> listaKrawedzi) {
        LinkedList<String> wynik = new LinkedList<String>();
        for (KrawedzPrzystankow k : listaKrawedzi){
            String s = k.getKoniec();
            if(!wynik.contains(s)) wynik.add(s);
            s = k.getPoczatek();
            if(!wynik.contains(s)) wynik.add(s);
        }
        return wynik;
    }
}
