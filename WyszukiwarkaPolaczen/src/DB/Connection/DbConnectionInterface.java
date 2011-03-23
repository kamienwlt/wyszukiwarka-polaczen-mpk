/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DB.Connection;

import Modele.Podstawowe.Linia;
import Modele.Podstawowe.Przystanek;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

/**
 *
 * @author x
 */
public interface DbConnectionInterface {
    public Linia getLinia(int id);
    public Linia getLinia(String id);
    public Vector<String> getBuses();

    public HashMap<String, LinkedList<String>> getStreetsAndStops();

    public Przystanek getPrzystanek(String przystString);
}
