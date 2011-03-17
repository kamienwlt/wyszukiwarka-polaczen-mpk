/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Modele.Mapa;

import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URL;
import Modele.Mapa.Wagi.*;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author x
 */
public abstract class MapaAbstract implements MapaInterface {

    protected LinkedList<KrawedzPrzystankow> listaKrawedzi;
    protected LinkedList<String> listaWierzch;
    protected String sciezka = "";
    protected HashMap<String, String> p = new HashMap<String, String>();
    protected HashMap<String, Integer> d = new HashMap<String, Integer>();
    protected WagaCalculatorInterface wagaCalc = new SimpleWagaCalculator();

    abstract public void showPath(String poczatek, String koniec);

    /**
     * @return the listaWierzch
     */
    public LinkedList<String> getListaWierzch() {
        return listaWierzch;
    }

    protected LinkedList<KrawedzPrzystankow> createListaKrawedzi(File plikMapy) {
        LinkedList<KrawedzPrzystankow> wynik = null;
        try {
            Scanner input = new Scanner(plikMapy);
            wynik = new LinkedList<KrawedzPrzystankow>();
            while (input.hasNextLine()) {
                String s = input.nextLine();
                //rozdzielenie pelnych nazw przystankow
                String[] tab = s.split("#");
                for (int i = 1; i < tab.length; i++) {
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

    protected void showKrawedzie() {
        for (KrawedzPrzystankow k : listaKrawedzi) {
            k.showKrawedz();
        }
    }

    protected void calculateDandP(String v0) {
        System.out.println("Rozpoczeto glowne obliczenia dla przystanku nr " + v0);
        LinkedList<String> S = new LinkedList<String>();
        LinkedList<String> Q = new LinkedList<String>();
        p = new HashMap<String, String>();
        d = new HashMap<String, Integer>();

        for (String v : listaWierzch) {
            Q.add(v);
            p.put(v, null);
            d.put(v, 9999999);
        }
        d.remove(v0);
        d.put(v0, 0);
        while (!Q.isEmpty()) {
            System.out.println(Q.size());
            String u = min(Q, d);
            Q.remove(u);
            S.add(u);
            for (String v : Q) {
                if (krawedzExists(u, v)) {
                    int dv = d.get(v);
                    int du = d.get(u);
                    int waga = wagaCalc.getWaga(u, v);
                    int rob = du + waga;
                    if (dv > rob) {
                        d.remove(v);
                        d.put(v, rob);
                        p.remove(v);
                        p.put(v, u);
                    }
                }
            }
        }
        System.out.println("Zakonczono glowne obliczenia dla przystanku nr " + v0);
    }

    protected Stack<String> getPath(String i){
        String rob = i;
        System.out.println("Rozpoczeto budowanie sciezki do przystanku nr " + rob);
        Stack<String> wynik = new Stack<String>();
        while (i != null){
            wynik.add(i);
            i = p.get(i);
        }
        System.out.println("Zakonczono budowanie sciezki do przystanku nr " + rob);
        return wynik;
    }

    protected void showPath(Stack<String> s){
        while(!s.isEmpty()){
            String stop = s.pop();
            System.out.println(stop);
        }
    }

    protected String getPath(Stack<String> s){
        String wynik = "";
        while(!s.isEmpty()){
            String stop = s.pop();
            wynik += stop + " ";
        }

        return wynik;
    }

    protected LinkedList<String> createListaWierzcholkow(LinkedList<KrawedzPrzystankow> listaKrawedzi) {
        LinkedList<String> wynik = new LinkedList<String>();
        for (KrawedzPrzystankow k : listaKrawedzi) {
            String s = k.getKoniec();
            if (!wynik.contains(s)) {
                wynik.add(s);
            }
            s = k.getPoczatek();
            if (!wynik.contains(s)) {
                wynik.add(s);
            }
        }
        return wynik;
    }

    protected String min(LinkedList<String> Q, HashMap<String, Integer> d) {
        String wynik = "";
        int min = 9999999;
        for (String v : Q) {
            int rob = d.get(v);
            if (rob <= min) {
                min = rob;
                wynik = v;
            }
        }
        return wynik;
    }

    protected boolean krawedzExists(String u, String v) {
        boolean found = false;
        Iterator it = listaKrawedzi.iterator();
        while (!found && it.hasNext()) {
            KrawedzPrzystankow k = (KrawedzPrzystankow) it.next();
            if (k.getPoczatek().equals(u) && k.getKoniec().equals(v)) {
                found = true;
            }
        }
        return found;
    }

    public void showCost(String koniec) {
        System.out.println("Koszt = " + d.get(koniec));
    }

    /**
     * @param wagaCalc the wagaCalc to set
     */
    public void setWagaCalc(WagaCalculatorInterface wagaCalc) {
        this.wagaCalc = wagaCalc;
    }

    //======================== metody pomocnicze ==========================================================

    protected void writeDandP(int i){
        FileWriter fw = null;
        try {
            File f = new File("data" + File.separator + "dp" + i);
            fw = new FileWriter(f);
            BufferedWriter bf = new BufferedWriter(fw);
            for (String s : d.keySet()){
                String info = s + " " + d.get(s) + " " + p.get(s);
                bf.write(info);
                bf.newLine();
            }
            bf.flush();
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(MapaPrzystankow.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(MapaPrzystankow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected void writeDandP(){
        FileWriter fw = null;
        try {
            File f = new File("data" + File.separator + "dp");
            fw = new FileWriter(f);
            BufferedWriter bf = new BufferedWriter(fw);
            for (String s : d.keySet()){
                String info = s + " " + d.get(s) + " " + p.get(s);
                bf.write(info);
                bf.newLine();
            }
            bf.flush();
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(MapaPrzystankow.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(MapaPrzystankow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected void createStopBusFiles(LinkedList<String> listaWierzch) {
        FileWriter fw = null;
        try {
            File f = new File("data" + File.separator + "StopsBuses");
            fw = new FileWriter(f);
            BufferedWriter bf = new BufferedWriter(fw);
            for (String s : listaWierzch) {
                LinkedList<String> buses = getBuses(s);
                String info = s;
                for (String sPrim : buses) {
                    info += " " + sPrim;
                }
                bf.write(info);
                bf.newLine();
            }
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(MapaPrzystankow.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(MapaPrzystankow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    protected LinkedList<String> getBuses(String v) {
        LinkedList<String> wynik = null;
        try {
            String tempAdres = "http://mpk.lublin.pl/?przy=" + v;
            URL adr = new URL(tempAdres);
            URLConnection yc = adr.openConnection();
            Scanner input = new Scanner(new InputStreamReader(yc.getInputStream()));
            wynik = readBusses(input);
        } catch (IOException ex) {
            Logger.getLogger(SameBusWagaCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return wynik;
    }

    protected LinkedList<String> readBusses(Scanner input) throws IOException {
        LinkedList<String> wynik = new LinkedList<String>();
        String s = input.nextLine();
        while (input.hasNextLine()) {
            if (s.trim().startsWith("<a class=\"rozklad-nr-linii\" href=\"../../?")) {
                s = s.substring(s.indexOf("?") + 1);
                s = s.substring(0, s.indexOf("\""));
                s = s.replace("lin=", "");
                wynik.add(s);
            }
            s = input.nextLine();
        }
        return wynik;
    }
}
