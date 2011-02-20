/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Updater;

import java.io.BufferedWriter;
import java.io.File;
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
    private HashMap<String, String> przystWgUlic;
    private HashMap<String, LinkedList<String>> graf;

    public Updater() {
        busWrapper = new BusWrapper();
        stopsWrapper = new StopsWrapper();
        graf = new HashMap<String, LinkedList<String>>();
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
        przystWgUlic = stopsWrapper.getPrzystWgUlic();
        System.out.println("Zakonczono wstepne przygotowania");
    }

    private void startUpdate() {
        System.out.println("Rozpoczeto glowna aktualizacje");
        for (String linia : buses) {
            try {
                URL adresTrasy = new URL("http://mpk.lublin.pl/index.php?s=rozklady&lin=" + linia);
                URLConnection atConnection = adresTrasy.openConnection();
                Scanner atConnectionInput = new Scanner(new InputStreamReader(atConnection.getInputStream()));
                String s = atConnectionInput.nextLine();
                System.out.println("Aktualizacja linii " + linia);
                readTrasa(atConnectionInput, linia);
                atConnectionInput.close();
            } catch (IOException ex) {
                Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Zakonczono glowna aktualizacje");
    }

    private void readTrasa(Scanner atConnectionInput, String linia) {
        try {
            BufferedWriter pkBufWriter = null;
            FileWriter pkWriter = null;
            String kierunek = "";
            File plikTrasyKierunku = null;
            String trasa = "";
            String popPrzystanek = "";
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
                        trasa = "";
                        popPrzystanek = "";
                    }
                    if (pkWriter != null) {
                        pkWriter.close();
                    }

                    s = s.trim().replaceFirst("Kierunek: <b style=\"font-size:14px\">", "");
                    s = s.replaceFirst("</b>", "");
                    kierunek = s.trim();
                    if (kierunekCount.containsKey(kierunek)){
                        int newCount = kierunekCount.get(kierunek) + 1;
                        kierunekCount.remove(kierunek);
                        kierunekCount.put(kierunek, newCount);
                        kierunek = kierunek + newCount;
                    } else{
                        kierunekCount.put(kierunek, 1);
                    }


                    boolean utworzonoFolderRozkladow = new File("data" + File.separator + "Rozkłady" + File.separator + linia + File.separator + "Kierunek - " + kierunek).mkdirs();
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
                    if (popPrzystanek.equals(""))
                        popPrzystanek = s;
                    else {
                        if (getGraf().containsKey(popPrzystanek)){
                            LinkedList<String> rob = getGraf().get(popPrzystanek);
                            rob.addLast(s);
                            getGraf().remove(popPrzystanek);
                            getGraf().put(popPrzystanek, rob);
                        } else {
                            LinkedList<String> rob = new LinkedList<String>();
                            rob.addLast(s);
                            getGraf().put(popPrzystanek, rob);
                        }
                        popPrzystanek = s;
                    }

                    String przystanek = s;
                    String ulica = przystWgUlic.get(przystanek);
                    boolean utworzonoPlikRozkladuPrzystanku = new File("data" + File.separator + "Rozkłady" + File.separator + linia + File.separator + "Kierunek - " + kierunek + File.separator + "[" + ulica + "] " + przystanek).createNewFile();
                    File plikPrzystanku = new File("data" + File.separator + "Rozkłady" + File.separator + linia + File.separator + "Kierunek - " + kierunek + File.separator + "[" + ulica + "] " + przystanek);
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
//a tu zauważy komentarz?
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
            odjazdy += godziny.pop() + "\t" + minuty.pop() + "\n";
        }

        return odjazdy;
    }

    /**
     * @return the graf
     */
    public HashMap<String, LinkedList<String>> getGraf() {
        return graf;
    }

    /**
     * @param graf the graf to set
     */
    public void setGraf(HashMap<String, LinkedList<String>> graf) {
        this.graf = graf;
    }
}
