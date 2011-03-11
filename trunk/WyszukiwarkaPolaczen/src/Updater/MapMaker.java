/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Updater;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author x
 */
public class MapMaker {

    private HashMap<String, LinkedList<String>> g;

    public MapMaker() {
        //this.g = g;
    }

    void createMapFile(HashMap<String, LinkedList<String>> g, String nazwa) {
        System.out.println("Rozpoczeto tworzenie pliku mapy przystankow");
        FileWriter fw = null;
        try {
            File mapFile = new File("data" + File.separator + nazwa);
            fw = new FileWriter(mapFile);
            BufferedWriter bf = new BufferedWriter(fw);
            for (String k : g.keySet()) {
                String info = k;
                for (String v : g.get(k)) {
                    info += "#" + v;
                }
                bf.write(info);
                bf.newLine();
            }
            bf.flush();
            bf.close();
        } catch (IOException ex) {
            Logger.getLogger(MapMaker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(MapMaker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Zakonczono tworzenie pliku mapy przystankow");
    }
}
