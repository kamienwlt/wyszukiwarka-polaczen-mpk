/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Updater;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author x
 */
public class Updater {

    private BusWrapper busWrapper;
    private LinkedList<String> buses;
    private StopsWrapper stopsWrapper;
    private MapMaker mapMaker;
    private HashMap<String, LinkedList<String>> grafMapyPrzystankow;
    private HashMap<String, LinkedList<String>> grafMapyPolaczen;

    public Updater() {
        busWrapper = new BusWrapper();
        stopsWrapper = new StopsWrapper();
        grafMapyPrzystankow = new HashMap<String, LinkedList<String>>();
        grafMapyPolaczen = new HashMap<String, LinkedList<String>>();
        mapMaker = new MapMaker();
    }

    public void update() {
        startWrapping();
        startUpdate();
    }

    private void startWrapping() {
        System.out.println("Rozpoczeto wstepne przygotowania");
        busWrapper.readBuses();
        buses = busWrapper.getBuses();

        stopsWrapper.readStops();
        System.out.println("Zakonczono wstepne przygotowania");
    }

    private void startUpdate() {
        System.out.println("Rozpoczeto glowna aktualizacje");
        int i = 1;
        for (String linia : buses) {
            try {
                URL adresTrasy = new URL("http://mpk.lublin.pl/index.php?s=rozklady&lin=" + linia);
                URLConnection atConnection = adresTrasy.openConnection();
                Scanner atConnectionInput = new Scanner(new InputStreamReader(atConnection.getInputStream()));
                String s = atConnectionInput.nextLine();
                System.out.println("Aktualizacja linii " + linia + " [" + i + "/" + buses.size() + "]");
                readTrasa(atConnectionInput, linia);
                atConnectionInput.close();
                i++;
            } catch (IOException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        createGrafy(buses);
        createMapFile(grafMapyPrzystankow, "MapaPrzystankow");
        createMapFile(grafMapyPolaczen, "MapaPolaczen");
        stopsWrapper.writePrzystWgUlicFile();
        System.out.println("Zakonczono glowna aktualizacje");
    }

    private void readTrasa(Scanner atConnectionInput, String linia) {
        try {
            BufferedWriter pkBufWriter = null;
            FileWriter pkWriter = null;
            String kierunek = "";
            File plikTrasyKierunku = null;
            String trasa = "";
            HashMap<String, Integer> kierunekCount = new HashMap<String, Integer>();
            String s = "";

            while (!s.trim().equals("<!-- KONIEC SRODKA -->") && atConnectionInput.hasNextLine()) {

                s = atConnectionInput.nextLine();
                //jeśli natrafimy na kierunek
                if (s.trim().startsWith("Kierunek:")) {
                    if (pkBufWriter != null) {
                        pkBufWriter.write(trasa);
                        pkBufWriter.flush();
                        pkBufWriter.close();
                        //updateGrafMapyPrzystankow(trasa);
                        //updateGrafMapyPolaczen(trasa);
                        trasa = "";
                    }
                    if (pkWriter != null) {
                        pkWriter.close();
                    }

                    s = s.trim().replaceFirst("Kierunek: <b style=\"font-size:14px\">", "");
                    s = s.replaceFirst("</b>", "");
                    kierunek = s.trim();
                    if (kierunekCount.containsKey(kierunek)) {
                        int newCount = kierunekCount.get(kierunek) + 1;
                        kierunekCount.remove(kierunek);
                        kierunekCount.put(kierunek, newCount);
                        kierunek = kierunek + newCount;
                    } else {
                        kierunekCount.put(kierunek, 1);
                    }


                    boolean utworzonoFolderRozkladow = new File("data" + File.separator + "Rozkłady" + File.separator + linia + File.separator + kierunek).mkdirs();
                    boolean utworzonoFolderTras = new File("data" + File.separator + "Trasy" + File.separator + linia).mkdirs();
                    boolean utworzonoPlikTrasyLiniiKierunek = new File("data" + File.separator + "Trasy" + File.separator + linia + File.separator + kierunek).createNewFile();

                    plikTrasyKierunku = new File("data" + File.separator + "Trasy" + File.separator + linia + File.separator + kierunek);
                    pkWriter = new FileWriter(plikTrasyKierunku);
                    pkBufWriter = new BufferedWriter(pkWriter);
                }

                //jeśli natrafimy na przystanek
                if (s.trim().startsWith("<td valign=top><a href=\"../../?przy=")) {
                    s = s.replaceAll("</a></td>", "");
                    s = s.split("\">")[1];
                    trasa += s + "\n";
                    String nrPrzystanku = s.split(" ")[0];
                    String przystanek = s;
                    boolean utworzonoPlikRozkladuPrzystanku = new File("data" + File.separator + "Rozkłady" + File.separator + linia + File.separator + kierunek + File.separator + przystanek).createNewFile();
                    File plikPrzystanku = new File("data" + File.separator + "Rozkłady" + File.separator + linia + File.separator + kierunek + File.separator + przystanek);
                    readPrzystanek(plikPrzystanku, linia, nrPrzystanku);
                }
            }



            if (pkBufWriter != null) {
                pkBufWriter.write(trasa);
                pkBufWriter.flush();
                pkBufWriter.close();
                trasa = "";
            }
            if (pkWriter != null) {
                pkWriter.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void readPrzystanek(File plikPrzystanku, String linia, String nrPrzystanku) {
        try {
            URL adresUrl = new URL("http://mpk.lublin.pl/?przy=" + nrPrzystanku + "&lin=" + linia);
            URLConnection adrUrlCon = adresUrl.openConnection();
            Scanner input = new Scanner(new InputStreamReader(adrUrlCon.getInputStream()));

            FileWriter writer = new FileWriter(plikPrzystanku);
            BufferedWriter output = new BufferedWriter(writer);

            String info = "";
            String kolejnoscPrzystankow = getKolejnoscPrzystankow(input);
            info += kolejnoscPrzystankow + "\n";

            String rozklad = getRozklad(input);

            info += rozklad;

            output.write(info);
            output.flush();
            writer.close();
            input.close();
            output.close();


        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getKolejnoscPrzystankow(Scanner input) {
        String s = input.nextLine();

        while (!s.trim().equals("<p class=\"rozklad-kolejnosc-przystankow\"><center>") && input.hasNextLine()) {
            s = input.nextLine();
            //System.out.println(s);
        }

        s = input.nextLine().trim().replaceFirst("<strong>", "");
        String kolejnoscPrzystankow = s.replaceFirst("</strong>", "") + "\n";

        return kolejnoscPrzystankow;
    }

    private String getRozklad(Scanner input) {
        String s = input.nextLine();
        String rozklad = "";
        String odjazdy = "";

        while (!s.trim().equals("<b>LEGENDA:</b><br/>") && input.hasNextLine()) {
            s = input.nextLine();

            if (s.trim().equals("<span class=\"rozklad-title\">WSZYSTKICH ŚWIĘTYCH</span>")) {
                s = s.trim().replaceFirst("<span class=\"rozklad-title\">", "");
                s = s.replaceFirst("</span>", "") + "\n";
                rozklad += s + "\n";
                odjazdy = getOdjazdy(input);
                rozklad += odjazdy + "\n";
            }

            if (s.trim().equals("<span class=\"rozklad-title\">DZIEŃ SPECJALNY</span>")) {
                s = s.trim().replaceFirst("<span class=\"rozklad-title\">", "");
                s = s.replaceFirst("</span>", "") + "\n";
                rozklad += s + "\n";
                odjazdy = getOdjazdy(input);
                rozklad += odjazdy + "\n";
            }

            if (s.trim().equals("<span class=\"rozklad-title\">NOC SYLWESTROWA</span>")) {
                s = s.trim().replaceFirst("<span class=\"rozklad-title\">", "");
                s = s.replaceFirst("</span>", "") + "\n";
                rozklad += s + "\n";
                odjazdy = getOdjazdy(input);
                rozklad += odjazdy + "\n";
            }

            if (s.trim().equals("<span class=\"rozklad-title\">KOMUNIKACJA NOCNA</span>")) {
                s = s.trim().replaceFirst("<span class=\"rozklad-title\">", "");
                s = s.replaceFirst("</span>", "") + "\n";
                rozklad += s + "\n";
                odjazdy = getOdjazdy(input);
                rozklad += odjazdy + "\n";
            }

            if (s.trim().equals("<span class=\"rozklad-title\">DZIEŃ POWSZEDNI, ROK SZKOLNY</span>")) {
                s = s.trim().replaceFirst("<span class=\"rozklad-title\">", "");
                s = s.replaceFirst("</span>", "") + "\n";
                rozklad += s + "\n";
                odjazdy = getOdjazdy(input);
                rozklad += odjazdy + "\n";
            }

            if (s.trim().equals("<span class=\"rozklad-title\">SOBOTA, ROK SZKOLNY</span>")) {
                s = s.trim().replaceFirst("<span class=\"rozklad-title\">", "");
                s = s.replaceFirst("</span>", "") + "\n";
                rozklad += s + "\n";
                odjazdy = getOdjazdy(input);
                rozklad += odjazdy + "\n";
            }

            if (s.trim().equals("<span class=\"rozklad-title\">DZIEŃ ŚWIĄTECZNY, ROK SZKOLNY</span>")) {
                s = s.trim().replaceFirst("<span class=\"rozklad-title\">", "");
                s = s.replaceFirst("</span>", "") + "\n";
                rozklad += s + "\n";
                odjazdy = getOdjazdy(input);
                rozklad += odjazdy + "\n";
            }

            if (s.trim().equals("<span class=\"rozklad-title\">WIGILIA I SYLWESTER</span>")) {
                s = s.trim().replaceFirst("<span class=\"rozklad-title\">", "");
                s = s.replaceFirst("</span>", "") + "\n";
                rozklad += s + "\n";
                odjazdy = getOdjazdy(input);
                rozklad += odjazdy + "\n";
            }

            if (s.trim().equals("<span class=\"rozklad-title\">WIELKANOC,BOŻE NARODZENIE,NOWY ROK</span>")) {
                s = s.trim().replaceFirst("<span class=\"rozklad-title\">", "");
                s = s.replaceFirst("</span>", "") + "\n";
                rozklad += s + "\n";
                odjazdy = getOdjazdy(input);
                rozklad += odjazdy + "\n";
            }

        }

        return rozklad;
    }

    private String getOdjazdy(Scanner input) {
        String odjazdy = "";
        String s = input.nextLine();
        while (!s.trim().equals("<th>Godz.</th>") && input.hasNextLine()) {
            s = input.nextLine();
        }

        LinkedList<String> godziny = new LinkedList<String>();

        s = input.nextLine();

        while (!s.trim().equals("</tr>") && input.hasNextLine()) {

            s = input.nextLine();
            godziny.add(s.trim());

            s = input.nextLine();
            s = input.nextLine();
        }

        s = input.nextLine();
        s = input.nextLine();
        s = input.nextLine();
        s = input.nextLine();
        s = input.nextLine();

        LinkedList<String> minuty = new LinkedList<String>();

        while (!s.trim().equals("</tr>") && input.hasNextLine()) {

            s = input.nextLine();
            s = s.trim().replaceAll("<br>", " ");

            //zakomentowac ponizsze jesli chce sie informacje o pojazdach niskopodlogowych i prywaciarzach
            s = s.replaceAll("n", "");
            s = s.replaceAll("p", "");
            s = s.replaceAll("a", "");

            minuty.add(s);


            s = input.nextLine();
            s = input.nextLine();

        }

        while (!minuty.isEmpty()) {
            odjazdy += godziny.pop() + ") " + minuty.pop() + "\n";
        }

        return odjazdy;
    }

    /**
     * @return the graf
     */
    public HashMap<String, LinkedList<String>> getGrafMapyPrzystankow() {
        return grafMapyPrzystankow;
    }

    /**
     * @param graf the graf to set
     */
    public void setGrafMapyPrzystankow(HashMap<String, LinkedList<String>> graf) {
        this.grafMapyPrzystankow = graf;
    }

    private void createMapFile(HashMap<String, LinkedList<String>> graf, String nazwa) {
        mapMaker.createMapFile(graf, nazwa);
    }

    private void updateGrafMapyPrzystankow(String trasa) {
        String popPrzystanek = "";
        String[] trasaTab = trasa.split("\n");
        for (String s : trasaTab) {
            if (popPrzystanek.equals("")) {
                popPrzystanek = s;
            } else {
                if (grafMapyPrzystankow.containsKey(popPrzystanek)) {
                    LinkedList<String> rob = grafMapyPrzystankow.get(popPrzystanek);
                    if (!rob.contains(s)) {
                        rob.addLast(s);
                        //grafMapyPrzystankow.remove(popPrzystanek);
                        //grafMapyPrzystankow.put(popPrzystanek, rob);
                    }
                } else {
                    LinkedList<String> rob = new LinkedList<String>();
                    rob.addLast(s);
                    grafMapyPrzystankow.put(popPrzystanek, rob);
                }
                popPrzystanek = s;
            }
        }
    }

    private void updateGrafMapyPolaczen(String trasa) {
        String[] trasaTab = trasa.split("\n");
        HashMap<String, LinkedList<String>> robGraf = new HashMap<String, LinkedList<String>>();
        for (int i = 0; i < trasaTab.length; i++) {
            String s = trasaTab[i];
            LinkedList<String> robList = new LinkedList<String>();
            if (i != trasaTab.length - 1) {
                for (int j = i + 1; j < trasaTab.length; j++) {
                    robList.addLast(trasaTab[j]);
                }
            }
            robGraf.put(s, robList);
        }

        for (String s : robGraf.keySet()) {
            LinkedList<String> robList = robGraf.get(s);
            if (grafMapyPolaczen.containsKey(s)) {
                LinkedList<String> rob = grafMapyPolaczen.get(s);
                for (String w : robList) {
                    if (!rob.contains(w)) {
                        rob.addLast(w);
                    }
                }
                //grafMapyPolaczen.remove(s);
                //grafMapyPolaczen.put(s, rob);
            } else {
                grafMapyPolaczen.put(s, robList);
            }
        }
    }

    private void createGrafy(LinkedList<String> buses) {
        for (String linia : buses){
            File f = new File("data" + File.separator + "Trasy" + File.separator + linia);
            String[] kierunki = f.list();
            for (String kierunek : kierunki){
                try {
                    File plikKierunku = new File("data" + File.separator + "Trasy" + File.separator + linia + File.separator + kierunek);
                    Scanner input = new Scanner(plikKierunku);
                    String trasa = "";
                    while (input.hasNextLine())
                        trasa += input.nextLine() + "\n";
                    updateGrafMapyPolaczen(trasa);
                    updateGrafMapyPrzystankow(trasa);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
