/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Widoki;

import DB.Connection.DbConnectionInterface;
import DB.Connection.FakeDbConnection;
import Modele.Podstawowe.Przystanek;
import Modele.Rozklad.RozkladAbstract;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author x
 */
public class PrzystanekWidok implements WidokInterface {

    public static final String ULICA_DOWOLNA = "Dowolna";
    public static final String LINIA_DOWOLNA = "Dowolna";
    private DbConnectionInterface dbConnection;
    private HashMap<String, LinkedList<String>> mapaUlic;
    private String ulica;
    private String linia;
    //elementy modelu
    private Przystanek przystanek;
    //elementy swing
    private JFrame frame;
    private JPanel mainPanel;
    private JComboBox ulicaComboBox;
    private JComboBox przystanekComboBox;
    private JScrollPane rozkladScrollPane;
    private JList rozkladJList;
    private JComboBox linieComboBox;

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
        PrzystanekWidok pw = new PrzystanekWidok();
    }

    public PrzystanekWidok() {
        this.dbConnection = new FakeDbConnection();
        przystanek = null;
        mapaUlic = null;
        ulica = PrzystanekWidok.ULICA_DOWOLNA;
        linia = PrzystanekWidok.LINIA_DOWOLNA;
        stworzWidok();
    }

    public void setDbConnection(DbConnectionInterface dbc) {
        this.dbConnection = dbc;
    }

    private void stworzWidok() {
        mapaUlic = dbConnection.getStreetsAndStops();
        frame = new JFrame();
        frame.setTitle("Szczegóły Przystanku");
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //----------- ulicaComboBox ---------------------
        setUlicaComboBox();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(ulicaComboBox, c);

        //----------- przystanekComboBox ----------------
        setPrzystanekComboBox();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        mainPanel.add(przystanekComboBox, c);

        //------------ rozkladScrollPane -------------------
        setRozkladScrollPane();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        mainPanel.add(rozkladScrollPane, c);

        //---------- linieComboBox ---------------------
        setLinieComboBox();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        mainPanel.add(linieComboBox, c);

        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void setUlicaComboBox() {
        Vector<String> ulice = new Vector<String>();
        if (mapaUlic != null) {
            Set<String> uliceSet = mapaUlic.keySet();
            for (String s : uliceSet) {
                ulice.add(s);
            }
            Collections.sort(ulice);
            ulice.add(0, PrzystanekWidok.ULICA_DOWOLNA);
        }
        ulicaComboBox = new JComboBox(ulice);
        TitledBorder tr = BorderFactory.createTitledBorder("Ulica");
        ulicaComboBox.setBorder(tr);
        ulicaComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ulica = (String) ulicaComboBox.getSelectedItem();
                updatePrzystanekComboBox();
                updateRozkladScrollPane();
                updateLinieComboBox();
            }
        });
    }

    private void setPrzystanekComboBox() {
        Vector<String> przystanki = new Vector<String>();
        if (mapaUlic != null) {
            if (ulica.equalsIgnoreCase(PrzystanekWidok.ULICA_DOWOLNA)) {
                Collection<LinkedList<String>> przystankiList = mapaUlic.values();
                for (LinkedList<String> l : przystankiList) {
                    for (String przystString : l) {
                        przystanki.add(przystString);
                    }
                }
            } else {
                LinkedList<String> rob = mapaUlic.get(ulica);
                for (String przystString : rob) {
                    przystanki.add(przystString);
                }
            }
            Collections.sort(przystanki);
        }
        przystanekComboBox = new JComboBox(przystanki);
        TitledBorder tr = BorderFactory.createTitledBorder("Przystanek");
        przystanekComboBox.setBorder(tr);
        przystanekComboBox.setPreferredSize(new Dimension(300, 50));
        przystanekComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String przystString = (String) przystanekComboBox.getSelectedItem();
                przystanek = dbConnection.getPrzystanekByWholeName(przystString);
                frame.setTitle("Szczegoly Przystanku " + przystanek.getId() + " " + przystanek.getNazwa());
                //System.out.println(przystanek.getRozklad().pokazRozklad());
                updateRozkladScrollPane();
                updateLinieComboBox();
            }
        });
    }

    private void updatePrzystanekComboBox() {
        mainPanel.remove(przystanekComboBox);
        setPrzystanekComboBox();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        mainPanel.add(przystanekComboBox, c);
        mainPanel.validate();
        if (przystanek != null) {
            przystanek = null;
        }
        ulica = PrzystanekWidok.ULICA_DOWOLNA;
        linia = PrzystanekWidok.LINIA_DOWOLNA;
    }

    private void updateRozkladScrollPane() {
        mainPanel.remove(rozkladScrollPane);
        setRozkladScrollPane();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        mainPanel.add(rozkladScrollPane, c);
        mainPanel.validate();
    }

    private void setRozkladScrollPane() {
        Vector<String> rozklad = new Vector<String>();
        String tytul = "Rozklad";
        if (przystanek != null) {
            RozkladAbstract r = przystanek.getRozklad();
            if (!linia.equalsIgnoreCase(PrzystanekWidok.LINIA_DOWOLNA)) {
                r = r.getRozklad(linia);
                tytul += " linii " + linia;
            }
            tytul += " na Przystanku " + przystanek.getNazwa();
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
                String wiersz = (String) rozkladJList.getSelectedValue();
            }
        });

        TitledBorder tr = BorderFactory.createTitledBorder(tytul);
        rozkladScrollPane = new JScrollPane(rozkladJList);
        rozkladScrollPane.setBorder(tr);
        rozkladScrollPane.setPreferredSize(new Dimension(350, 800));
    }

    private void updateLinieComboBox(){
        mainPanel.remove(linieComboBox);
        setLinieComboBox();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 0;
        mainPanel.add(linieComboBox, c);
        mainPanel.validate();
    }

    private void setLinieComboBox() {
        Vector<String> linie = new Vector<String>();
        if (przystanek != null) {
            RozkladAbstract r = przystanek.getRozklad();
            String[] tab = r.getLinie().split(" ");
            for (String s : tab) {
                linie.add(s);
            }
            Collections.sort(linie);
        }
        linie.add(0, PrzystanekWidok.LINIA_DOWOLNA);
        linieComboBox = new JComboBox(linie);
        TitledBorder tr = new TitledBorder("Linia");
        linieComboBox.setBorder(tr);
        linieComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                linia = (String) linieComboBox.getSelectedItem();
                updateRozkladScrollPane();
            }
        });
    }
}
