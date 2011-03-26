/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DB.Connection;

import Modele.Podstawowe.Droga;
import Modele.Podstawowe.Linia;
import Modele.Podstawowe.Przystanek;
import Modele.Podstawowe.Trasa;
import Modele.Rozklad.RozkladAbstract;
import Modele.Rozklad.RozkladPrzystanku;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zwirek
 */
public class NotFakeDBConnection implements DbConnectionInterface{
    private static Statement stmt;
    private static Connection con;
    private HashMap<String, String> ulicaWzglPrzyst = new HashMap<String, String>();
    private HashMap<String, LinkedList<String>> stopsBuses = null;

    public Linia getLinia(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Linia getLinia(String id) {
//        try {
//            connect();
//        } catch (SQLException ex) {
//            return null;
//        }
        Linia wynik = null;
        try {
            ResultSet rs = executeQuery("SELECT id_linii FROM linia WHERE nazwa='" + id + "';");
            rs.next();
            int idLinii = rs.getInt(1);
            wynik = new Linia(idLinii, id);
            rs = executeQuery("SELECT id_kierunku from trasa where id_linii="+idLinii+";");
            LinkedList<String> kierunki = new LinkedList<String>();
            while(rs.next()){
                int kierunek_id = rs.getInt(1);
                kierunki.add(getKierunek(kierunek_id));
                ResultSet rs2 = executeQuery("SELECT lista_id_przystankow  from trasa where id_linii="+idLinii+" AND id_kierunku="+kierunek_id);
                rs2.next();
                Droga d = new Droga();
                String[] listap = rs2.getString(1).split(" ");
                for (int i = 0; i < listap.length; i++) {
                    Przystanek p;
                    String string = listap[i];
                    ResultSet rs3 = executeQuery("SELECT nazwa, id_ulicy from przystanek where id_przystanku="+string+";");
                    rs3.next();
                    String nazwa = rs3.getString(1);
                    int id_ulicy = rs3.getInt(2);
                    rs3 = executeQuery("SELECT nazwa FROM ulica WHERE id_ulicy="+id_ulicy+";");
                    rs3.next();
                    String ulica = rs3.getString(1);
                    p = new Przystanek(Integer.parseInt(string.trim()), nazwa, ulica);
                    //trzeba by jeszcze dopisać wyciąganie rozkładów z bazy
                    d.addPrzystanekTail(p);
                }
                rs2 = executeQuery("SELECT nazwa FROM kierunek WHERE id_kierunku="+kierunek_id+";");
                rs2.next();
                wynik.addTrasa(new Trasa(rs2.getString(1), d));
            }
        } catch (SQLException ex) {
            Logger.getLogger(NotFakeDBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wynik;
    }



    public static void main(String[] args) {
        NotFakeDBConnection nfdb = new NotFakeDBConnection();
        HashMap<String, LinkedList<String>> m = new FakeDbConnection().getStreetsAndStops();
        for (String ulica : m.keySet()) {
            for (String przystanek : m.get(ulica)) {
                System.out.println(ulica + " " + przystanek);
            }
        }
//        Linia l = nfdb.getLinia("008");
//        for(Trasa t : l.getTrasy()){
//            System.out.println("Trasa w kierunku " + t.getKierunek());
//            Droga d = t.getDroga();
//            for(Przystanek p : d.getListaPrzyst()){
//                System.out.println("Rozklad przystanku " + p.getNazwa() + " na ulicy " + p.getUlica());
//                RozkladAbstract r = p.getRozklad();
////                r.pokazRozklad();
//            }
//        }
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
        HashMap<String, LinkedList<String>> wynik = new HashMap<String, LinkedList<String>>();
        try {
            ResultSet rs = executeQuery("SELECT * FROM przystanek;");
            while (rs.next()) {
                ResultSet rs2 = executeQuery("SELECT nazwa FROM ulica WHERE id_ulicy="+rs.getInt(2)+";");
                rs2.next();
                String ulica = rs2.getString(1);
                String przystanek = rs.getString(1)+" "+rs.getString(3);
                ulicaWzglPrzyst.put(przystanek, ulica);
                //System.out.println(przystanek + " " + ulica);
                if (wynik.containsKey(ulica)) {
                    LinkedList<String> rob = wynik.get(ulica);
                    rob.addLast(przystanek);
                    //wynik.remove(ulica);
                } else {
                    LinkedList<String> rob = new LinkedList<String>();
                    rob.addLast(przystanek);
                    wynik.put(ulica, rob);
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(NotFakeDBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wynik;
    }

    public Przystanek getPrzystanek(String przystString) {
        String s = przystString.replaceFirst(" - ", "#");
        String[] tab = s.split("#");
        Przystanek wynik = new Przystanek(Integer.parseInt(tab[0]), tab[1], ulicaWzglPrzyst.get(przystString));
        if (stopsBuses == null) {
            createStopsBuses();
        }
        LinkedList<String> busList = stopsBuses.get("" + wynik.getId());
        RozkladAbstract rozkladPrzystanku = new RozkladPrzystanku();
        for(String bus : busList){
            RozkladAbstract rozkladLinii = getRozklad(bus, wynik.getNazwa());
            rozkladPrzystanku.addRozklad(rozkladLinii);
        }
        return wynik;
    }

    private String getKierunek(int id) throws SQLException{
        ResultSet rs = executeQuery("Select nazwa from kierunek where id_kierunku="+id+";");
        rs.next();
        return rs.getString(1);
    }



    public ResultSet executeQuery(String q) throws SQLException{
        connect();
        return stmt.executeQuery(q);
    }

    private static void connect() throws SQLException{
        String url = "jdbc:mysql://witak.net:3306/";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.err.println("ClassNotFoundException: " + e.getMessage());
            System.exit(0);
        }
        con = DriverManager.getConnection(url, "norbert_zwirek", "cK3vbzdx");
        con.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
        stmt = con.createStatement();
        try {
            stmt.executeQuery("USE norbert_zwirek;");
        } catch (MySQLSyntaxErrorException e) {
            Logger.getLogger(NotFakeDBConnection.class.getName()).log(Level.SEVERE, null, e);
            System.exit(0);
        }
    }

    private void createStopsBuses() {
        stopsBuses = new HashMap<String, LinkedList<String>>();
        try {
            ResultSet rs = executeQuery("SELECT id_linii, lista_id_przystankow FROM trasa;");
            while(rs.next()){
                ResultSet rs2 = executeQuery("SELECT nazwa FROM linia WHERE id_linii="+rs.getInt(1)+";");
                rs2.next();
                String linia = rs2.getString(1);
                String[] listaPrzyst = rs.getString(2).split(" ");
                for (int i = 0; i < listaPrzyst.length; i++) {
                    if (stopsBuses.containsKey(listaPrzyst[i])) {
                        LinkedList<String> rob = stopsBuses.get(listaPrzyst[i]);
                        rob.addLast(linia);
                        //wynik.remove(ulica);
                    } else {
                        LinkedList<String> rob = new LinkedList<String>();
                        rob.addLast(linia);
                        stopsBuses.put(listaPrzyst[i], rob);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(NotFakeDBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private RozkladAbstract getRozklad(String bus, String nazwa) {
        RozkladAbstract wynik = null;
        RozkladAbstract rob = null;
        Linia l = this.getLinia(bus);
        LinkedList<Trasa> trasy = l.getTrasy();
        Iterator<Trasa> it = trasy.iterator();
        boolean found = false;
        while (!found && it.hasNext()){
            Trasa t = it.next();
            Droga d = t.getDroga();
            Przystanek p = d.getPrzystanek(nazwa);
            if (p != null){
                found = true;
                rob = p.getRozklad();
            }
        }
        if (found) wynik = rob;
        return wynik;
    }
}
