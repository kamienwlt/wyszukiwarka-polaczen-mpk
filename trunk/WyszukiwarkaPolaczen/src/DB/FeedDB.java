/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DB;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
public class FeedDB {
    Statement stmt;
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FeedDB();
            }
        });
    }

    public FeedDB(){
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
            stmt = con.createStatement();
            try {
                stmt.executeQuery("USE norbert_zwirek;");
            } catch (MySQLSyntaxErrorException e) {
                Logger.getLogger(CreateDB.class.getName()).log(Level.SEVERE, null, e);
                System.exit(0);
            }
            ResultSet rs = null;
            try {
                stmt.execute("DELETE FROM linia WHERE 1;");
                stmt.execute("DELETE FROM kierunek WHERE 1;");
                stmt.execute("DELETE FROM ulica WHERE 1;");
                stmt.execute("DELETE FROM przystanek WHERE 1;");
                stmt.execute("DELETE FROM trasa WHERE 1;");
                insertLinie();
                insertKierunki();
                insertUlice();
                insertPrzystanki();
                insertTrasy();
            } catch (MySQLSyntaxErrorException e) {
                Logger.getLogger(CreateDB.class.getName()).log(Level.SEVERE, null, e);
                System.exit(0);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CreateDB.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    private void insertLinie() throws SQLException {
        File f = new File("data"+File.separator+"Rozkłady");
        if(!f.exists()){
            System.out.println("Problem ze znalezieniem katalogu z rozkładami!");
            return;
        }
        File[] list = f.listFiles();
        for (int i = 0; i < list.length; i++) {
            File file = list[i];
            if(file.getName().equalsIgnoreCase(".svn")){
                break;
            }
            stmt.execute("INSERT INTO linia VALUES(0, '"+file.getName()+"');");
        }
    }

    private void insertKierunki() throws SQLException  {
        File f = new File("data"+File.separator+"Trasy");
        if(!f.exists()){
            System.out.println("Problem ze znalezieniem katalogu z trasami!");
            return;
        }
        File[] list = f.listFiles();
        for (int i = 0; i < list.length; i++) {
            File file = list[i];
            if(file.getName().equalsIgnoreCase(".svn")){
                break;
            }
            File[] kierunki = file.listFiles();
            for (int j = 0; j < kierunki.length; j++) {
                File file1 = kierunki[j];
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM kierunek WHERE kierunek.nazwa='"+file1.getName()+"';");
                rs.next();
                if(rs.getInt(1)==0){
                    stmt.execute("INSERT INTO kierunek VALUES(0, '"+file1.getName()+"');");
                }
            }
        }
    }

    private void insertUlice() throws SQLException {
        File f = new File("data"+File.separator+"Rozkłady");
        if(!f.exists()){
            System.out.println("Problem ze znalezieniem katalogu z rozkładami!");
            return;
        }
        File[] list = f.listFiles();
        for (int i = 0; i < list.length; i++) {
            File file = list[i];
            if(file.getName().equalsIgnoreCase(".svn")){
                break;
            }
            File[] kierunki = file.listFiles();
            for (int j = 0; j < kierunki.length; j++) {
                File file1 = kierunki[j];
                if(file1.getName().equalsIgnoreCase(".svn")){
                    break;
                }
                File[] przystanki = file1.listFiles();
                for (int k = 0; k < przystanki.length; k++) {
                    File file2 = przystanki[k];
                    String name = file2.getName();
                    name = name.substring(1);
                    name = name.substring(0, name.indexOf("]"));
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM ulica WHERE ulica.nazwa='"+name+"';");
                    rs.next();
                    if(rs.getInt(1)==0){
                        stmt.execute("INSERT INTO ulica VALUES(0, '"+name+"');");
                    }
                }
            }

        }

    }

    private void insertPrzystanki() throws SQLException {
        File f = new File("data"+File.separator+"Rozkłady");
        if(!f.exists()){
            System.out.println("Problem ze znalezieniem katalogu z rozkładami!");
            return;
        }
        File[] list = f.listFiles();
        for (int i = 0; i < list.length; i++) {
            File file = list[i];
            File[] kierunki = file.listFiles();
            if(file.getName().equalsIgnoreCase(".svn")){
                break;
            }
            for (int j = 0; j < kierunki.length; j++) {
                File file1 = kierunki[j];
                if(file1.getName().equalsIgnoreCase(".svn")){
                    break;
                }
                File[] przystanki = file1.listFiles();
                for (int k = 0; k < przystanki.length; k++) {
                    File file2 = przystanki[k];
                    String ulica = file2.getName();
                    ulica = ulica.substring(1);
                    String reszta=ulica;
                    ulica = ulica.substring(0, ulica.indexOf("]"));
                    reszta = reszta.substring(reszta.indexOf("]")+1).trim();
                    int numer = Integer.parseInt(reszta.substring(0,reszta.indexOf("-")).trim());
                    String nazwa = reszta.substring(reszta.indexOf("]")+1).trim();
                    nazwa = nazwa.substring(nazwa.indexOf("-")+1).trim();
                    ResultSet rs2 = stmt.executeQuery("SELECT id_ulicy FROM ulica WHERE ulica.nazwa='"+ulica+"'");
                    rs2.next();
                    int id_ulicy = rs2.getInt(1);
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM przystanek WHERE przystanek.id_przystanku='"+numer+"';");
                    rs.next();
                    if(rs.getInt(1)==0){
                        stmt.execute("INSERT INTO przystanek VALUES("+numer+", "+id_ulicy+", '"+nazwa+"');");
                    }
                }
            }
        }
    }

    private void insertTrasy()throws SQLException  {
        File f = new File("data"+File.separator+"Trasy");
        if(!f.exists()){
            System.out.println("Problem ze znalezieniem katalogu z trasami!");
            return;
        }
        File[] list = f.listFiles();
        for (int i = 0; i < list.length; i++) {
            File file = list[i];
            File[] kierunki = file.listFiles();
            if(file.getName().equalsIgnoreCase(".svn")){
                break;
            }
            for (int j = 0; j < kierunki.length; j++) {
                File file1 = kierunki[j];
                if(file1.getName().equalsIgnoreCase(".svn")){
                    break;
                }
                ResultSet rs = stmt.executeQuery("SELECT id_kierunku FROM kierunek WHERE kierunek.nazwa='"+file1.getName()+"';");
                rs.next();
                int idkier = rs.getInt(1);
                rs = stmt.executeQuery("SELECT id_linii FROM linia WHERE linia.nazwa='"+file.getName()+"';");
                rs.next();
                int idlinii = rs.getInt(1);
                String przystanki="";
                try {
                    BufferedReader in = new BufferedReader(new FileReader(file1));
                    String line="";
                    while((line=in.readLine())!=null){
                        line = line.substring(0, line.indexOf("-")).trim();
                        przystanki+=line+" ";
                    }
                    in.close();
                    stmt.execute("INSERT INTO trasa VALUES("+idkier+", "+idlinii+", '"+przystanki+"');");
                } catch (IOException ex) {
                    System.out.println("Problemy z odczytem danych z pliku: "+file1.getName());
                }
            }
        }

    }

}
