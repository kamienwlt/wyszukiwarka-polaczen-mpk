/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Modele.Podstawowe;

import Modele.Rozklad.RozkladAbstract;
import java.util.LinkedList;

/**
 *
 * @author x
 */
public class Przystanek {
    private int id;
    private String nazwa;
    private String ulica;
    private RozkladAbstract rozklad;
    private LinkedList<Przystanek> sasiedzi = new LinkedList<Przystanek>();

    public Przystanek(int id, String nazwa, String ulica){
        this.id = id;
        this.nazwa = nazwa;
        this.ulica = ulica;
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
     * @return the ulica
     */
    public String getUlica() {
        return ulica;
    }

    /**
     * @param ulica the ulica to set
     */
    public void setUlica(String ulica) {
        this.ulica = ulica;
    }

    /**
     * @return the rozklad
     */
    public RozkladAbstract getRozklad() {
        return rozklad;
    }

    /**
     * @param rozklad the rozklad to set
     */
    public void setRozklad(RozkladAbstract rozklad) {
        this.rozklad = rozklad;
    }

    public void showRozklad(){
        rozklad.pokazRozklad();
    }

    public String getLinie(){
        return rozklad.getLinie();
    }

    public void addSasiad(Przystanek p){
        sasiedzi.add(p);
    }

    public void removeSasiad(Przystanek p){
        sasiedzi.remove(p);
        p.removeSasiad(this);
    }
}
