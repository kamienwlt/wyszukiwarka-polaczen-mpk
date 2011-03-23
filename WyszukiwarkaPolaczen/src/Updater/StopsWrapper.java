/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Updater;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author zwirek
 */
public class StopsWrapper {

    private HashMap<String, String> przystWgUlic;

    public StopsWrapper() {
        przystWgUlic = new HashMap<String, String>();
    }

    public void readStops() {
        try {
            System.out.println("Rozpoczeto tworzenie mapy przystankow wg ulic");
            for (int i = 0; i < 800; i += 50) {
                String tempAdres = "http://mpk.lublin.pl/?op=se_n_p&ofs=" + i;
                URL adr = new URL(tempAdres);
                URLConnection yc = adr.openConnection();
                Scanner input = new Scanner(new InputStreamReader(yc.getInputStream()));
                readStops(input);
            }
            System.out.println("Zakonczono tworzenie mapy przystankow wg ulic");
        } catch (IOException ex) {
            Logger.getLogger(StopsWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readStops(Scanner input) throws IOException {
        String s = input.nextLine();
        while (!s.trim().startsWith("<tr><td><a href=\"") && input.hasNextLine()) {
            s = input.nextLine();
        }
        String t = s;
        s = s.substring(s.indexOf("?"));
        s = s.substring(0, s.indexOf("\""));

        t = t.trim();
        t = t.replaceFirst("<tr><td><a href=\"../../\\" + s + "\">", "");
        t = t.replaceFirst("</a></td><td><a href=\"../../\\" + s + "\">", " - ");
        t = t.replaceFirst("</a></td><td><a href=\"../../\\?ulica=", "#");
        t = t.replaceFirst("</a></td></tr>", "");

        String t1 = t.split("#")[0];
        String t2 = t.split("#")[1].split("\">")[1];
        getPrzystWgUlic().put(t1, t2);

        s = input.nextLine();
        while (s.trim().startsWith("<tr><td><a href=\"") && input.hasNextLine()) {
            t = s;
            s = s.substring(s.indexOf("?"));
            s = s.substring(0, s.indexOf("\""));

            t = t.trim();
            t = t.replaceFirst("<tr><td><a href=\"../../\\" + s + "\">", "");
            t = t.replaceFirst("</a></td><td><a href=\"../../\\" + s + "\">", " - ");
            t = t.replaceFirst("</a></td><td><a href=\"../../\\?ulica=", "#");
            t = t.replaceFirst("</a></td></tr>", "");

            t1 = t.split("#")[0];
            t2 = t.split("#")[1].split("\">")[1];
            getPrzystWgUlic().put(t1, t2);

            s = input.nextLine();
        }
    }

    /**
     * @return the przystWgUlic
     */
    public HashMap<String, String> getPrzystWgUlic() {
        return przystWgUlic;
    }

    /**
     * @param przystWgUlic the przystWgUlic to set
     */
    public void setPrzystWgUlic(HashMap<String, String> przystWgUlic) {
        this.przystWgUlic = przystWgUlic;
    }

    public void writePrzystWgUlicFile(){
        FileWriter fw = null;
        try {
            File przystWgUlicFile = new File("data" + File.separator + "PrzystankiWgUlic");
            fw = new FileWriter(przystWgUlicFile);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String przystanek : przystWgUlic.keySet()){
                String info = przystanek + "#" + przystWgUlic.get(przystanek);
                bw.write(info);
                bw.newLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(StopsWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(StopsWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
