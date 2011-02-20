/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Updater;

import java.util.LinkedList;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author x
 */
public class BusWrapper {

    private LinkedList<String> buses;

    public BusWrapper() {
        buses = new LinkedList<String>();
    }

    public void readBuses(){
        try {
            System.out.println("Rozpoczeto zbieranie linii");
            String addrString = "http://mpk.lublin.pl";
            URL addrURL = new URL(addrString);
            URLConnection urlCon = addrURL.openConnection();
            Scanner input = new Scanner(new InputStreamReader(urlCon.getInputStream()));
            readBuses(input);
            System.out.println("Zakonczono zbieranie linii");
        } catch (IOException ex) {
            Logger.getLogger(StopsWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readBuses(Scanner input) {
        String s = "";
        while (!s.trim().startsWith("<select name=\"lin\">") && input.hasNextLine()){
            s = input.nextLine();
        }
        s = input.nextLine();
        while (s.trim().startsWith("<option label=")){
            String t = s.trim();
            t = t.replaceFirst("<option label=", "");
            t = t.replaceAll("\"", "");
            t = t.split(" ")[0];
            getBuses().add(t);
            s = input.nextLine();
        }
    }

    /**
     * @return the buses
     */
    public LinkedList<String> getBuses() {
        return buses;
    }

    /**
     * @param buses the buses to set
     */
    public void setBuses(LinkedList<String> buses) {
        this.buses = buses;
    }
    //ciekawe czy zwróci awagę na komentarz
}
