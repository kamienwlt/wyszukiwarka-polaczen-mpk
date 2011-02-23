/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Modele.Mapa.Wagi;

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
public class SameBusWagaCalculator implements WagaCalculatorInterface {

    private LinkedList<String> primaryBuses;
    private LinkedList<String> presentBuses;
    private int mnoznik = 1;
  
    public int getWaga(String u, String v) {
        int waga;
        presentBuses = getBuses(v);
        LinkedList<String> iloczynZbiorow = getIloczynZbiorow(primaryBuses, presentBuses);
        boolean sameBus = !iloczynZbiorow.isEmpty();
        if (sameBus) {
            waga = 0;
        } else {
            waga = mnoznik * 10;
            mnoznik++;
            //System.out.println("=================================");
            //System.out.println("Przesiadka");
            //System.out.println("Z " + primaryBuses);
            //primaryBuses = presentBuses;
            //System.out.println("D0 " + presentBuses);
        }
        if (u.equals("4731") && v.equals("5601")) System.out.println(sameBus);
        return waga;
    }

    private LinkedList<String> getIloczynZbiorow(LinkedList<String> primaryBuses, LinkedList<String> presentBuses) {
        LinkedList<String> wynik = new LinkedList<String>();
        if (presentBuses.size() < primaryBuses.size()) {
            for (String s : presentBuses) {
                if (primaryBuses.contains(s)) {
                    wynik.add(s);
                }
            }
        } else {
            for (String s : primaryBuses) {
                if (presentBuses.contains(s)) {
                    wynik.add(s);
                }
            }
        }
        return wynik;
    }

    public LinkedList<String> getBuses(String v) {
        LinkedList<String> wynik = new LinkedList<String>();
        try {
            Scanner input = new Scanner(new File("data" + File.separator + "StopsBuses"));
            String s = input.nextLine();
            while(!s.startsWith(v) && input.hasNextLine()){
                s = input.nextLine();
            }
            String[] tab = s.split(" ");
            for (int i = 1; i < tab.length; i++){
                wynik.add(tab[i]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SameBusWagaCalculator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return wynik;
    }

    
    public static void main(String[] args) {
        SameBusWagaCalculator s = new SameBusWagaCalculator();
        s.setPrimaryBuses("3722");
        s.presentBuses = s.getBuses("3712");
        s.getWaga("3722", "3712");
    }

    /**
     * @param primaryBuses the primaryBuses to set
     */
    public void setPrimaryBuses(String u) {
        primaryBuses = getBuses(u);
        System.out.println(primaryBuses);
    }


}
