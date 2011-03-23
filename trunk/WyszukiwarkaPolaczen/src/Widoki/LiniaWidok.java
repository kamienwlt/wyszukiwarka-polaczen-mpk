/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Widoki;

import DB.Connection.DbConnectionInterface;
import DB.Connection.FakeDbConnection;
import Modele.Podstawowe.Droga;
import Modele.Podstawowe.Linia;
import Modele.Podstawowe.Przystanek;
import Modele.Podstawowe.Trasa;
import Modele.Rozklad.RozkladAbstract;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author x
 */
public class LiniaWidok implements LiniaWidokInterface {

    //polaczenie z baza danych
    private DbConnectionInterface dbConnection;
    //elementy modelu
    private Linia linia;
    private Trasa trasa;
    private Przystanek przystanek;
    //elementy swing
    private JFrame frame;
    private JPanel mainPanel;
    private JComboBox linieComboBox;
    private JComboBox kierunkiComboBox;
    private JScrollPane przystankiScrollPane;
    private JList przystankiJList;
    private JScrollPane rozkladScrollPane;
    private JList rozkladJList;

    public LiniaWidok() {
        linia = null;
        dbConnection = new FakeDbConnection();
        stworzWidok();
    }

    private void stworzWidok() {
        frame = new JFrame();
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //------------- liniaComboBox ----------------------
        setLinieComboBox();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(linieComboBox, c);
        //-------------- kierunekComboBox ------------------
        setKierunekComboBox();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        mainPanel.add(kierunkiComboBox, c);
        //------------- przystankiScrollPane ---------------
        setPrzystankiScrollPane();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        mainPanel.add(przystankiScrollPane, c);
        //------------ rozkladScrollPane -------------------
        setRozkladScrollPane();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        mainPanel.add(rozkladScrollPane, c);

        frame.add(mainPanel);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void setLinieComboBox() {
        Vector<String> linie = null;
        if (dbConnection != null) {
            linie = dbConnection.getBuses();
        }
        linieComboBox = new JComboBox(linie);
        TitledBorder tr = BorderFactory.createTitledBorder("Linia");
        linieComboBox.setBorder(tr);
        linieComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String liniaString = (String) linieComboBox.getSelectedItem();
                setLinia(liniaString);
                updateKierunekComboBox();
                updatePrzystankiScrollPane();
                updateRozkladScrollPane();
            }
        });
    }

    private void updateKierunekComboBox() {
        mainPanel.remove(kierunkiComboBox);
        setKierunekComboBox();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        mainPanel.add(kierunkiComboBox, c);
        mainPanel.validate();
        if (trasa != null) {
            trasa = null;
        }
    }

    private void setKierunekComboBox() {
        Vector<String> kierunki = new Vector<String>();
        if (linia != null) {
            for (Trasa t : linia.getTrasy()) {
                kierunki.add(t.getKierunek());
            }
        }
        kierunkiComboBox = new JComboBox(kierunki);
        TitledBorder tr = BorderFactory.createTitledBorder("Kierunek");
        kierunkiComboBox.setBorder(tr);
        kierunkiComboBox.setPreferredSize(new Dimension(300, 50));
        kierunkiComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String kierunekString = (String) kierunkiComboBox.getSelectedItem();
                trasa = linia.getTrasa(kierunekString);
                updatePrzystankiScrollPane();
                updateRozkladScrollPane();
            }
        });
    }

    private void updatePrzystankiScrollPane() {
        mainPanel.remove(przystankiScrollPane);
        setPrzystankiScrollPane();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        mainPanel.add(przystankiScrollPane, c);
        mainPanel.validate();
        if(przystanek != null){
            przystanek = null;
        }
    }

    private void setPrzystankiScrollPane() {
        Vector<String> przystanki = new Vector<String>();
        String tytul = "Przystanki";
        if (linia != null) {
            tytul += " Linii " + linia.getNazwa();
        }
        if (trasa != null) {
            tytul += " - Kierunek: " + trasa.getKierunek();
            Droga d = trasa.getDroga();
            for (Przystanek p : d.getListaPrzyst()) {
                przystanki.add(p.getNazwa());
            }
        }
        przystankiJList = new JList(przystanki);
        przystankiJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        przystankiJList.setVisibleRowCount(15);
        przystankiJList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                String przystanekString = (String) przystankiJList.getSelectedValue();
                przystanek = trasa.getPrzystanek(przystanekString);
                updateRozkladScrollPane();
            }
        });

        TitledBorder tr = BorderFactory.createTitledBorder(tytul);
        przystankiScrollPane = new JScrollPane(przystankiJList);
        przystankiScrollPane.setBorder(tr);
        przystankiScrollPane.setPreferredSize(new Dimension(350, 350));
    }

    private void updateRozkladScrollPane(){
        mainPanel.remove(rozkladScrollPane);
        setRozkladScrollPane();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        mainPanel.add(rozkladScrollPane, c);
        mainPanel.validate();
    }

    private void setRozkladScrollPane() {
        Vector<String> rozklad = new Vector<String>();
        String tytul = "Rozklad";
        if (przystanek != null) {
            tytul += " na Przystanku " + przystanek.getNazwa();
            RozkladAbstract r = przystanek.getRozklad();
            String rozkladString = r.pokazRozklad();
            String[] rozkladTab = rozkladString.split("\n");
            for (String s : rozkladTab) {
                rozklad.add(s);
            }
        }
        rozkladJList = new JList(rozklad);
        rozkladJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rozkladJList.setVisibleRowCount(20);
        rozkladJList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                String linia = (String) rozkladJList.getSelectedValue();
            }
        });

        TitledBorder tr = BorderFactory.createTitledBorder(tytul);
        rozkladScrollPane = new JScrollPane(rozkladJList);
        rozkladScrollPane.setBorder(tr);
        rozkladScrollPane.setPreferredSize(new Dimension(350, 400));
    }

    public void setLinia(int id) {
        linia = dbConnection.getLinia(id);
    }

    public void setLinia(String id) {
        linia = dbConnection.getLinia(id);
    }

    public void setDbConnection(DbConnectionInterface dbc) {
        this.dbConnection = dbc;
    }

    public static void main(String[] args) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }
        LiniaWidok lw = new LiniaWidok();
    }
}
