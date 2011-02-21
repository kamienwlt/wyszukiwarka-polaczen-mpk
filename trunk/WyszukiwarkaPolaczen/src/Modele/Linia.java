/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele;

import java.util.LinkedList;

/**
 *
 * @author x
 */
public class Linia {
    private int id;
    private String nazwa;
    private LinkedList<Trasa> trasy;

    public Linia(int id, String nazwa){
        this.id = id;
        this.nazwa = nazwa;
        trasy = new LinkedList<Trasa>();
    }

    public void addTrasa(Trasa trasa){
        trasy.add(trasa);
    }

    public void removeTrasa(Trasa trasa){
        trasy.remove(trasa);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the nazwa
     */
    public String getNazwa() {
        return nazwa;
    }

    /**
     * @param nazwa the nazwa to set
     */
    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    /**
     * @return the trasy
     */
    public LinkedList<Trasa> getTrasy() {
        return trasy;
    }

    /**
     * @param trasy the trasy to set
     */
    public void setTrasy(LinkedList<Trasa> trasy) {
        this.trasy = trasy;
    }
}
