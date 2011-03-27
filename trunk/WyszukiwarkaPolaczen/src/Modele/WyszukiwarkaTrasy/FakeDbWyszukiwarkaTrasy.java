/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele.WyszukiwarkaTrasy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author x
 */
public class FakeDbWyszukiwarkaTrasy implements WyszukiwarkaTrasyInterface{

    private File trasaWgPrzystankow;
    private File trasaWgPrzesiadek;
    private File plikPrzeszukiwan;
    private static final String sciezkaPlikuTrasWgPrzystankow = "data" + File.separator + "SciezkiWzglPrzystankow";
    private static final String sciezkaPlikuTrasWgPrzesiadek = "data" + File.separator + "SciezkiWzglPrzesiadek";

    public FakeDbWyszukiwarkaTrasy(){
        trasaWgPrzesiadek = new File(sciezkaPlikuTrasWgPrzesiadek);
        trasaWgPrzystankow = new File(sciezkaPlikuTrasWgPrzystankow);
        plikPrzeszukiwan = trasaWgPrzesiadek;
    }

    public void setKryterium(String kryterium) {
        if(kryterium.equalsIgnoreCase(LICZBA_PRZESIADEK))
            plikPrzeszukiwan = trasaWgPrzesiadek;
        if(kryterium.equalsIgnoreCase(LICZBA_PRZYSTANKOW))
            plikPrzeszukiwan = trasaWgPrzystankow;
    }

    public String getPath(String poczatek, String koniec) {
        String wynik = "";
        try {
            File plikTras = new File(plikPrzeszukiwan.getPath() + File.separator + poczatek);
            Scanner input = new Scanner(plikTras);
            boolean found = false;
            String trasa = "";
            while(!found && input.hasNextLine()){
                trasa = input.nextLine();
                if (trasa.startsWith(koniec))
                    found = true;
            }
            if(found) wynik = trasa.split("#")[1];

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FakeDbWyszukiwarkaTrasy.class.getName()).log(Level.SEVERE, null, ex);
        }

        return wynik;
    }

    public static void main(String[] args){
        FakeDbWyszukiwarkaTrasy w = new FakeDbWyszukiwarkaTrasy();
        w.setKryterium(WyszukiwarkaTrasyInterface.LICZBA_PRZYSTANKOW);
        System.out.println(w.getPath("1012", "1023"));
    }
}
