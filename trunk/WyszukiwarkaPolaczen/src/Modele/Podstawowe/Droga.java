/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele.Podstawowe;

import java.util.LinkedList;

/**
 *
 * @author x
 */
public class Droga {
    private LinkedList<Przystanek> listaPrzyst;

    public Droga(){
        listaPrzyst = new LinkedList<Przystanek>();
    }

    public void addPrzystanekTail(Przystanek p){
        listaPrzyst.addLast(p);
    }

    public void addPrzystanekHead(Przystanek p){
        listaPrzyst.addFirst(p);
    }

    public void addPrzystanek(int index, Przystanek p){
        listaPrzyst.add(index, p);
    }

    public void removePrzystanek(Przystanek p){
        int idxP = listaPrzyst.indexOf(p);
        Przystanek poprzedni = null;
        Przystanek nastepny = null;
        
        if (idxP != 0){
            poprzedni = listaPrzyst.get(idxP - 1);
            poprzedni.removeSasiad(p);
        } 
        if (idxP != listaPrzyst.size() - 1){
            nastepny = listaPrzyst.get(idxP + 1);
            p.removeSasiad(nastepny);
        }
        if(nastepny != null && poprzedni != null)
            poprzedni.addSasiad(nastepny);

        listaPrzyst.remove(p);
    }

    /**
     * @return the listaPrzyst
     */
    public LinkedList<Przystanek> getListaPrzyst() {
        return listaPrzyst;
    }

    /**
     * @param listaPrzyst the listaPrzyst to set
     */
    public void setListaPrzyst(LinkedList<Przystanek> listaPrzyst) {
        this.listaPrzyst = listaPrzyst;
    }
}
