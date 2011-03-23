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
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author x
 */
public class FakeDbConnection implements DbConnectionInterface{

    private String sciezka = "data" + File.separator;

    public Linia getLinia(String id) {
        Linia wynik = null;
        File trasaFile = new File(sciezka + File.separator + "Trasy" + File.separator + id);
        boolean exists = trasaFile.exists();
        if (exists){
            String[] kierunki = trasaFile.list();
            LinkedList<Trasa> trasy = new LinkedList<Trasa>();
            for(String k : kierunki){
                LinkedList<String> listaPrzystankow = new LinkedList<String>();
                try {
                    File trasaKierunkuFile = new File(sciezka + File.separator + "Trasy" + File.separator + id + File.separator + k);
                    Scanner input = new Scanner(trasaKierunkuFile);
                    while(input.hasNextLine()){
                        String przystanekString = input.nextLine();
                        listaPrzystankow.addLast(przystanekString);
                    }
                    Trasa t = new Trasa(k, null);
                    Droga d = new Droga();
                    for(final String przystanek : listaPrzystankow){
                        File rozkladLiniiFile = new File(sciezka + File.separator + "Rozkłady" + File.separator + id + File.separator + k + File.separator + przystanek);
                        input = new Scanner(rozkladLiniiFile);
                        input.nextLine();
                        input.nextLine();
                        String info = "";
                        while(input.hasNextLine()){
                            info += input.nextLine() + "\n";
                        }
                        String[] tab = przystanek.split("-", 2);
                        int numerPrzyst = Integer.parseInt(tab[0].trim());
                        String nazwaPrzyst = tab[1].trim();
                        String ulica = getUlica(przystanek);
                        Przystanek p = new Przystanek(numerPrzyst, nazwaPrzyst, ulica);
                        RozkladLinii r = new RozkladLinii(Integer.parseInt(id), info);
                        p.setRozklad(r);
                        d.addPrzystanekTail(p);
                    }
                    t.setDroga(d);
                    trasy.add(t);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FakeDbConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            wynik = new Linia(Integer.parseInt(id), id);
            wynik.setTrasy(trasy);
        }
        return wynik;
    }


    public static void main(String[] args){
        Linia l = new FakeDbConnection().getLinia("001");
        for(Trasa t : l.getTrasy()){
            System.out.println("Trasa w kierunku " + t.getKierunek());
            Droga d = t.getDroga();
            for(Przystanek p : d.getListaPrzyst()){
                System.out.println("Rozklad przystanku " + p.getNazwa() + " na ulicy " + p.getUlica());
                RozkladAbstract r = p.getRozklad();
                r.pokazRozklad();
            }
        }
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
        for(String s : tab){
            wynik.add(s);
            Collections.sort(wynik);
        }
        return wynik;
    }
}
