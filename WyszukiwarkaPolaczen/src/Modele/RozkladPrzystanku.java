/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele;

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
    public void pokazRozklad() {
        for (RozkladAbstract r : rozklady)
            r.pokazRozklad();
    }

    @Override
    public void addRozklad(RozkladAbstract rozklad){
        rozklady.add(rozklad);
        rozklad.setRodzic(this);
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
            linie += r.getLinie();

        return linie;
    }

    @Override
    public RozkladAbstract getRozklad(int linia){
        RozkladAbstract wynik = null;
        RozkladAbstract rozklad = null;
        boolean found = false;
        Iterator<RozkladAbstract> i =  rozklady.listIterator();
        while(!found && i.hasNext()){
            rozklad = i.next();
            int nrLinii = Integer.parseInt(rozklad.getLinie());
            if (nrLinii == linia){
                found = true;
                wynik = rozklad;
            }

        }

        return wynik;
    }
}
