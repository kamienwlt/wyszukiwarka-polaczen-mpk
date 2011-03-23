/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pomocnicze;

import Modele.Mapa.MapaPrzystankow;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URL;

/**
 *
 * @author x
 */
public class StopBusFileCreator {
    static public void createStopBusFiles(LinkedList<String> listaWierzch) {
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
            Logger.getLogger(StopBusFileCreator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(StopBusFileCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    static protected LinkedList<String> getBuses(String v) {
        LinkedList<String> wynik = null;
        try {
            String tempAdres = "http://mpk.lublin.pl/?przy=" + v;
            URL adr = new URL(tempAdres);
            URLConnection yc = adr.openConnection();
            Scanner input = new Scanner(new InputStreamReader(yc.getInputStream()));
            wynik = readBusses(input);
        } catch (IOException ex) {
            Logger.getLogger(StopBusFileCreator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return wynik;
    }

    static protected LinkedList<String> readBusses(Scanner input) throws IOException {
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

    public static void main(String[] args){
        StopBusFileCreator.createStopBusFiles(new MapaPrzystankow().getListaWierzch());
    }
}
