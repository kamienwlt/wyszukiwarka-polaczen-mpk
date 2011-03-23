/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DB.Connection;

import Modele.Podstawowe.Linia;
import java.util.Vector;

/**
 *
 * @author x
 */
public interface DbConnectionInterface {
    public Linia getLinia(int id);
    public Linia getLinia(String id);
    public Vector<String> getBuses();
}
