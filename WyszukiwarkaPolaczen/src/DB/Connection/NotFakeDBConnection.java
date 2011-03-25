/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DB.Connection;

import Modele.Podstawowe.Linia;
import Modele.Podstawowe.Przystanek;
import Modele.Podstawowe.Trasa;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zwirek
 */
public class NotFakeDBConnection implements DbConnectionInterface{

    public Linia getLinia(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Linia getLinia(String id) {
        Linia wynik = null;
        try {
            ResultSet rs = executeQuery("SELECT id_linii FROM linia WHERE nazwa='" + id + "';");
            int idLinii = rs.getInt(1);
            wynik.setId(idLinii);
            wynik.setNazwa(id);
            LinkedList<Trasa> trasy = new LinkedList<Trasa>();
            rs = executeQuery("SELECT id_kierunku from trasa where id_linii="+idLinii+";");
            LinkedList<String> kierunki = new LinkedList<String>();
            while(rs.next()){
                int kierunek_id = rs.getInt(1);
                kierunki.add(getKierunek(kierunek_id));
                LinkedList<String> listaPrzystankow = new LinkedList<String>();
                ResultSet rs2 = executeQuery("SELECT lista_id_przystankow from trasa where id_linii="+idLinii+" AND id_kierunku="+kierunek_id);
                Blob listaprzyst = rs2.getBlob(1);
                
            }

        } catch (SQLException ex) {
            Logger.getLogger(NotFakeDBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wynik;
    }

    public Vector<String> getBuses() {
        Vector<String> wynik = new Vector<String>();
        try {
            ResultSet rs = executeQuery("Select name FROM linia;");
            while(rs.next()){
                wynik.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NotFakeDBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wynik;
    }

    public HashMap<String, LinkedList<String>> getStreetsAndStops() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Przystanek getPrzystanek(String przystString) {
        Przystanek wynik = null;
        
        return wynik;
    }

    private String getKierunek(int id) throws SQLException{
        ResultSet rs = executeQuery("Select nazwa from kierunek where id_kierunku="+id+";");
        return rs.getString(1);
    }



    public ResultSet executeQuery(String q) throws SQLException{
        String url = "jdbc:mysql://witak.net:3306/";
        Connection con;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.err.println("ClassNotFoundException: " + e.getMessage());
            System.exit(0);
        }
        con = DriverManager.getConnection(url, "norbert_zwirek", "cK3vbzdx");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(q);
        return rs;
    }
}
