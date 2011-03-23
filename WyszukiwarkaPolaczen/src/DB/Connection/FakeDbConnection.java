/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DB.Connection;

import Modele.Podstawowe.Droga;
import Modele.Podstawowe.Linia;
import Modele.Podstawowe.Przystanek;
import Modele.Podstawowe.Trasa;
import Modele.Rozklad.RozkladAbstract;
import Modele.Rozklad.RozkladLinii;
import Modele.Rozklad.RozkladPrzystanku;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author x
 */
public class FakeDbConnection implements DbConnectionInterface {

    private String sciezka = "data" + File.separator;
    private HashMap<String, String> ulicaWzglPrzyst = new HashMap<String, String>();
    private HashMap<String, LinkedList<String>> stopsBuses = null;

    public Linia getLinia(String id) {
        Linia wynik = null;
        File trasaFile = new File(sciezka + File.separator + "Trasy" + File.separator + id);
        boolean exists = trasaFile.exists();
        if (exists) {
            String[] kierunki = trasaFile.list();
            LinkedList<Trasa> trasy = new LinkedList<Trasa>();
            int idInt = -1;
            for (String k : kierunki) {
                LinkedList<String> listaPrzystankow = new LinkedList<String>();
                try {
                    File trasaKierunkuFile = new File(sciezka + File.separator + "Trasy" + File.separator + id + File.separator + k);
                    Scanner input = new Scanner(trasaKierunkuFile);
                    while (input.hasNextLine()) {
                        String przystanekString = input.nextLine();
                        listaPrzystankow.addLast(przystanekString);
                    }
                    Trasa t = new Trasa(k, null);
                    Droga d = new Droga();
                    for (final String przystanek : listaPrzystankow) {
                        File rozkladLiniiFile = new File(sciezka + File.separator + "Rozkłady" + File.separator + id + File.separator + k + File.separator + przystanek);
                        input = new Scanner(rozkladLiniiFile);
                        input.nextLine();
                        input.nextLine();
                        String info = "";
                        while (input.hasNextLine()) {
                            info += input.nextLine() + "\n";
                        }
                        String[] tab = przystanek.split("-", 2);
                        int numerPrzyst = Integer.parseInt(tab[0].trim());
                        String nazwaPrzyst = tab[1].trim();
                        String ulica = getUlica(przystanek);
                        Przystanek p = new Przystanek(numerPrzyst, nazwaPrzyst, ulica);
                        try {
                            idInt = Integer.parseInt(id);
                        } catch (Exception e) {
                        }

                        RozkladLinii r = new RozkladLinii(id, info);
                        p.setRozklad(r);
                        d.addPrzystanekTail(p);
                    }
                    t.setDroga(d);
                    trasy.add(t);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FakeDbConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            wynik = new Linia(idInt, id);
            wynik.setTrasy(trasy);
        }
        return wynik;
    }

    public static void main(String[] args) {
        HashMap<String, LinkedList<String>> m = new FakeDbConnection().getStreetsAndStops();
        for (String ulica : m.keySet()) {
            for (String przystanek : m.get(ulica)) {
                System.out.println(ulica + " " + przystanek);
            }
        }
//        Linia l = new FakeDbConnection().getLinia("001");
//        for(Trasa t : l.getTrasy()){
//            System.out.println("Trasa w kierunku " + t.getKierunek());
//            Droga d = t.getDroga();
//            for(Przystanek p : d.getListaPrzyst()){
//                System.out.println("Rozklad przystanku " + p.getNazwa() + " na ulicy " + p.getUlica());
//                RozkladAbstract r = p.getRozklad();
//                r.pokazRozklad();
//            }
//        }
    }

    public Linia getLinia(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String getUlica(String przystanek) {
        try {
            File f = new File("data" + File.separator + "PrzystankiWgUlic");
            String rob = "";
            Scanner input = new Scanner(f);
            while (!rob.startsWith(przystanek) && input.hasNextLine()) {
                rob = input.nextLine();
            }
            return rob.split("#")[1];
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FakeDbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Vector<String> getBuses() {
        Vector<String> wynik = new Vector<String>();
        File f = new File("data" + File.separator + "Trasy");
        String[] tab = f.list();
        for (String s : tab) {
            wynik.add(s);
            Collections.sort(wynik);
        }
        return wynik;
    }

    public HashMap<String, LinkedList<String>> getStreetsAndStops() {
        HashMap<String, LinkedList<String>> wynik = new HashMap<String, LinkedList<String>>();
        File f = new File(sciezka + "PrzystankiWgUlic");
        try {
            Scanner input = new Scanner(f);
            while (input.hasNextLine()) {
                String para = input.nextLine();
                String[] tab = para.split("#");
                String przystanek = tab[0];
                String ulica = tab[1];
                ulicaWzglPrzyst.put(przystanek, ulica);
                //System.out.println(przystanek + " " + ulica);
                if (wynik.containsKey(ulica)) {
                    LinkedList<String> rob = wynik.get(ulica);
                    rob.addLast(przystanek);
                    //wynik.remove(ulica);
                } else {
                    LinkedList<String> rob = new LinkedList<String>();
                    rob.addLast(przystanek);
                    wynik.put(ulica, rob);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FakeDbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wynik;
    }

    public Przystanek getPrzystanek(String przystString) {
        String s = przystString.replaceFirst(" - ", "#");
        String[] tab = s.split("#");
        Przystanek wynik = new Przystanek(Integer.parseInt(tab[0]), tab[1], ulicaWzglPrzyst.get(przystString));
        if (stopsBuses == null) {
            createStopsBuses();
        }
        LinkedList<String> busList = stopsBuses.get("" + wynik.getId());
        RozkladAbstract rozkladPrzystanku = new RozkladPrzystanku();
        for(String bus : busList){
            RozkladAbstract rozkladLinii = getRozklad(bus, wynik.getNazwa());
            rozkladPrzystanku.addRozklad(rozkladLinii);
        }
        wynik.setRozklad(rozkladPrzystanku);
        return wynik;
    }

    private void createStopsBuses() {
        File f = new File(sciezka + "StopsBuses");
        try {
            stopsBuses = new HashMap<String, LinkedList<String>>();
            Scanner input = new Scanner(f);
            while (input.hasNextLine()) {
                String linia = input.nextLine();
                String[] t = linia.split(" ");
                String przystanekId = t[0];
                LinkedList<String> listaAutobus = new LinkedList<String>();
                for (int i = 1; i < t.length; i++) {
                    listaAutobus.add(t[i]);
                }
                stopsBuses.put(przystanekId, listaAutobus);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FakeDbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private RozkladAbstract getRozklad(String bus, String nazwa) {
        RozkladAbstract wynik = null;
        RozkladAbstract rob = null;
        Linia l = this.getLinia(bus);
        LinkedList<Trasa> trasy = l.getTrasy();
        Iterator<Trasa> it = trasy.iterator();
        boolean found = false;
        while (!found && it.hasNext()){
            Trasa t = it.next();
            Droga d = t.getDroga();
            Przystanek p = d.getPrzystanek(nazwa);
            if (p != null){
                found = true;
                rob = p.getRozklad();
            }
        }
        if (found) wynik = rob;
        return wynik;
    }
}
