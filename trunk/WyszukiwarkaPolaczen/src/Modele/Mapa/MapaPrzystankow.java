/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele.Mapa;

import Wagi.*;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
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

    private WagaCalculatorInterface wagaCalc = new SimpleWagaCalculator();

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

    private void searchPathDijktry(String v0){
        LinkedList<String> S = new LinkedList<String>();
        LinkedList<String> Q = new LinkedList<String>();
        HashMap<String, String> p = new HashMap<String, String>();
        HashMap<String, Integer> d = new HashMap<String, Integer>();

        for(String v : listaWierzch){
            Q.add(v);
            p.put(v, null);
            d.put(v, Integer.MAX_VALUE);
        }
        d.remove(v0);
        d.put(v0, 0);
        while(!Q.isEmpty()){
            String u = min(Q, d);
            Q.remove(u);
            S.add(u);
            for (String v : Q){
                if(krawedzExists(u, v)){
                    int dv = d.get(v);
                    int du = d.get(u);
                    int waga = wagaCalc.getWaga(u, v);
                    int rob = du + waga;
                    if (dv > rob){
                        d.remove(v);
                        d.put(v, waga);
                        p.remove(v);
                        p.put(v, u);
                    }
                }
            }
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

    private String min(LinkedList<String> Q, HashMap<String, Integer> d) {
        String wynik = "";
        int min = Integer.MAX_VALUE;
        for(String v : Q){
            int rob = d.get(v);
            if (rob < min){
                min = rob;
                wynik = v;
            }
        }
        return wynik;
    }

    private boolean krawedzExists(String u, String v) {
        boolean found = false;
        Iterator it = listaKrawedzi.iterator();
        while(!found && it.hasNext()){
            KrawedzPrzystankow k = (KrawedzPrzystankow) it.next();
            if (k.getPoczatek().equals(u) && k.getKoniec().equals(v)){
                found = true;
            }
        }
        return found;
    }
}
