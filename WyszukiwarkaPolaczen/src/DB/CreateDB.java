/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DB;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zwirek
 */
public class CreateDB {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new CreateDB();
            }
        });
    }

    public CreateDB(){
        String url = "jdbc:mysql://witak.net:3306/";
        Connection con;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            System.err.println("ClassNotFoundException: " + e.getMessage());
            System.exit(0);
        }
        try {
            con = DriverManager.getConnection(url, "norbert_zwirek", "cK3vbzdx");
            Statement stmt = con.createStatement();
            try {
                stmt.executeQuery("USE norbert_zwirek;");
            } catch (MySQLSyntaxErrorException e) {
                Logger.getLogger(CreateDB.class.getName()).log(Level.SEVERE, null, e);
                System.exit(0);
            }
            ResultSet rs = null;
            try {
                stmt.execute("CREATE TABLE linia (id_linii Integer NOT NULL AUTO_INCREMENT, nazwa varchar(30), PRIMARY KEY (id_linii));");
                stmt.execute("CREATE TABLE ulica (id_ulicy Integer NOT NULL AUTO_INCREMENT, nazwa varchar(30), PRIMARY KEY (id_ulicy));");
                stmt.execute("CREATE TABLE kierunek (id_kierunku Integer NOT NULL AUTO_INCREMENT, nazwa varchar(30), PRIMARY KEY (id_kierunku));");
                stmt.execute("CREATE TABLE trasa (id_kierunku Integer NOT NULL,  id_linii Integer NOT NULL, lista_id_przystankow blob);");
                stmt.execute("CREATE TABLE godzinyPowszedni (id_przystanku Integer NOT NULL,  id_linii Integer NOT NULL, godziny blob);");
                stmt.execute("CREATE TABLE godzinySobota (id_przystanku Integer NOT NULL,  id_linii Integer NOT NULL, godziny blob);");
                stmt.execute("CREATE TABLE godzinyNiedziela (id_przystanku Integer NOT NULL,  id_linii Integer NOT NULL, godziny blob);");
                stmt.execute("CREATE TABLE godzinyWigilia (id_przystanku Integer NOT NULL,  id_linii Integer NOT NULL, godziny blob);");
                stmt.execute("CREATE TABLE godzinyWielkanoc (id_przystanku Integer NOT NULL,  id_linii Integer NOT NULL, godziny blob);");
            } catch (MySQLSyntaxErrorException e) {
                Logger.getLogger(CreateDB.class.getName()).log(Level.SEVERE, null, e);
                System.exit(0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CreateDB.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

}
