/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele.Rozklad;

import java.util.LinkedList;
import java.util.Iterator;

/**
 *
 * @author x
 */

//czesc wzorca Kompozyt(Composite) - odpowiednik klasy Composite, tzn agregat na komponenty
public class RozkladPrzystanku extends RozkladAbstract{
    private LinkedList<RozkladAbstract> rozklady = new LinkedList<RozkladAbstract>();

    @Override
    public String pokazRozklad() {
        String wynik = "";
        for (RozkladAbstract r : rozklady)
            wynik += "Roklad linii " + r.getLinie() + "\n" + r.pokazRozklad() + "===========================\n";
        return wynik;
    }

    @Override
    public void addRozklad(RozkladAbstract rozklad){
        rozklady.add(rozklad);
        //rozklad.setRodzic(this);
    }

    @Override
    public void removeRozklad(RozkladAbstract rozklad){
        rozklady.remove(rozklad);
        rozklad.setRodzic(null);
    }

    @Override
    public String getLinie() {
        String linie = "";
        for (RozkladAbstract r : rozklady)
            linie += " " + r.getLinie();

        return linie;
    }

    @Override
    public RozkladAbstract getRozklad(String linia){
        RozkladAbstract wynik = null;
        RozkladAbstract rozklad = null;
        boolean found = false;
        Iterator<RozkladAbstract> i =  rozklady.listIterator();
        while(!found && i.hasNext()){
            rozklad = i.next();
            String nrLinii = rozklad.getLinie();
            if (nrLinii.equalsIgnoreCase(linia)){
                found = true;
                wynik = rozklad;
            }

        }

        return wynik;
    }
}
