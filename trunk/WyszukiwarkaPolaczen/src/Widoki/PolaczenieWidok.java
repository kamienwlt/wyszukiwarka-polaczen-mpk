/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Widoki;

import DB.Connection.DbConnectionInterface;
import DB.Connection.FakeDbConnection;
import Modele.Podstawowe.Przystanek;
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
import javax.swing.JButton;
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
public class PolaczenieWidok implements WidokInterface {

    public static final String ULICA_DOWOLNA = "Dowolna";
    private String poczatekUlica;
    private String koniecUlica;
    private DbConnectionInterface dbConnection;
    private HashMap<String, LinkedList<String>> mapaUlic;
    private String polaczenie;
    //elementy modelu
    private Przystanek poczatekPrzystanek;
    private Przystanek koniecPrzystanek;
    //elementy swing
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel poczatekTrasyPanel;
    private JComboBox poczatekTrasyUlicaComboBox;
    private JComboBox poczatekTrasyPrzystanekComboBox;
    private JPanel koniecTrasyPanel;
    private JComboBox koniecTrasyUlicaComboBox;
    private JComboBox koniecTrasyPrzystanekComboBox;
    private JButton obliczTraseButton;
    private JScrollPane polaczenieScrollPane;
    private JList polaczenieJList;

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
        PolaczenieWidok pw = new PolaczenieWidok();
    }

    public PolaczenieWidok() {
        dbConnection = new FakeDbConnection();
        mapaUlic = null;
        poczatekUlica = PolaczenieWidok.ULICA_DOWOLNA;
        koniecUlica = PolaczenieWidok.ULICA_DOWOLNA;
        stworzWidok();
    }

    public void setDbConnection(DbConnectionInterface dbc) {
        this.dbConnection = dbc;
    }

    private void stworzWidok() {
        mapaUlic = dbConnection.getStreetsAndStops();
        frame = new JFrame();
        frame.setTitle("Szczegóły Polaczenia");

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //poczatekTrasyPanel
        setPoczatekTrasyPanel();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(poczatekTrasyPanel, c);

        //koniecTrasyPanel
        setKoniecTrasyPanel();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        mainPanel.add(koniecTrasyPanel, c);

        //obliczTraseButton
        setObliczTraseButton();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        mainPanel.add(obliczTraseButton, c);

        //polaczenieScrollPaneList
        setPolaczenieScrollPane();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        mainPanel.add(polaczenieScrollPane, c);

        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void setPoczatekTrasyPanel() {
        poczatekTrasyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        TitledBorder tr = BorderFactory.createTitledBorder("Poczatek Trasy");
        poczatekTrasyPanel.setBorder(tr);

        setPoczatekTrasyUlicaComboBox();
        gbc.gridx = 0;
        gbc.gridy = 0;
        poczatekTrasyPanel.add(poczatekTrasyUlicaComboBox, gbc);

        setPoczatekTrasyPrzystanekComboBox();
        gbc.gridx = 0;
        gbc.gridy = 1;
        poczatekTrasyPanel.add(poczatekTrasyPrzystanekComboBox, gbc);
    }

    private void setPoczatekTrasyUlicaComboBox() {
        Vector<String> ulice = new Vector<String>();
        if (mapaUlic != null) {
            Set<String> uliceSet = mapaUlic.keySet();
            for (String s : uliceSet) {
                ulice.add(s);
            }
            Collections.sort(ulice);
            ulice.add(0, PrzystanekWidok.ULICA_DOWOLNA);
        }
        poczatekTrasyUlicaComboBox = new JComboBox(ulice);
        TitledBorder tr = BorderFactory.createTitledBorder("Ulica");
        poczatekTrasyUlicaComboBox.setBorder(tr);
        poczatekTrasyUlicaComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                poczatekUlica = (String) poczatekTrasyUlicaComboBox.getSelectedItem();
                updatePoczatekPrzystanekComboBox();
                //updateRozkladScrollPane();
                //updateLinieComboBox();
            }
        });
    }

    private void setPoczatekTrasyPrzystanekComboBox() {
        Vector<String> przystanki = new Vector<String>();
        if (mapaUlic != null) {
            if (poczatekUlica.equalsIgnoreCase(PrzystanekWidok.ULICA_DOWOLNA)) {
                Collection<LinkedList<String>> przystankiList = mapaUlic.values();
                for (LinkedList<String> l : przystankiList) {
                    for (String przystString : l) {
                        przystanki.add(przystString);
                    }
                }
            } else {
                LinkedList<String> rob = mapaUlic.get(poczatekUlica);
                for (String przystString : rob) {
                    przystanki.add(przystString);
                }
            }
            Collections.sort(przystanki);
        }
        poczatekTrasyPrzystanekComboBox = new JComboBox(przystanki);
        TitledBorder tr = BorderFactory.createTitledBorder("Przystanek");
        poczatekTrasyPrzystanekComboBox.setBorder(tr);
        poczatekTrasyPrzystanekComboBox.setPreferredSize(new Dimension(300, 50));
        poczatekTrasyPrzystanekComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String przystString = (String) poczatekTrasyPrzystanekComboBox.getSelectedItem();
                poczatekPrzystanek = dbConnection.getPrzystanekByWholeName(przystString);
                //System.out.println(przystanek.getRozklad().pokazRozklad());
                //updateRozkladScrollPane();
                //updateLinieComboBox();
            }
        });
    }

    private void updatePoczatekPrzystanekComboBox() {
        poczatekTrasyPanel.remove(poczatekTrasyPrzystanekComboBox);
        setPoczatekTrasyPrzystanekComboBox();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        poczatekTrasyPanel.add(poczatekTrasyPrzystanekComboBox, c);
        poczatekTrasyPanel.validate();
//        if (poczatekPrzystanek != null) {
//            poczatekPrzystanek = null;
//        }
        poczatekUlica = PrzystanekWidok.ULICA_DOWOLNA;
    }

    private void setKoniecTrasyPanel() {
        koniecTrasyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        TitledBorder tr = BorderFactory.createTitledBorder("Koniec Trasy");
        koniecTrasyPanel.setBorder(tr);

        setKoniecTrasyUlicaComboBox();
        gbc.gridx = 0;
        gbc.gridy = 0;
        koniecTrasyPanel.add(koniecTrasyUlicaComboBox, gbc);

        setKoniecTrasyPrzystanekComboBox();
        gbc.gridx = 0;
        gbc.gridy = 1;
        koniecTrasyPanel.add(koniecTrasyPrzystanekComboBox, gbc);
    }

    private void setKoniecTrasyUlicaComboBox() {
        Vector<String> ulice = new Vector<String>();
        if (mapaUlic != null) {
            Set<String> uliceSet = mapaUlic.keySet();
            for (String s : uliceSet) {
                ulice.add(s);
            }
            Collections.sort(ulice);
            ulice.add(0, PrzystanekWidok.ULICA_DOWOLNA);
        }
        koniecTrasyUlicaComboBox = new JComboBox(ulice);
        TitledBorder tr = BorderFactory.createTitledBorder("Ulica");
        koniecTrasyUlicaComboBox.setBorder(tr);
        koniecTrasyUlicaComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                koniecUlica = (String) koniecTrasyUlicaComboBox.getSelectedItem();
                updateKoniecPrzystanekComboBox();
                //updateRozkladScrollPane();
                //updateLinieComboBox();
            }
        });
    }

    private void setKoniecTrasyPrzystanekComboBox() {
        Vector<String> przystanki = new Vector<String>();
        if (mapaUlic != null) {
            if (koniecUlica.equalsIgnoreCase(PrzystanekWidok.ULICA_DOWOLNA)) {
                Collection<LinkedList<String>> przystankiList = mapaUlic.values();
                for (LinkedList<String> l : przystankiList) {
                    for (String przystString : l) {
                        przystanki.add(przystString);
                    }
                }
            } else {
                LinkedList<String> rob = mapaUlic.get(koniecUlica);
                for (String przystString : rob) {
                    przystanki.add(przystString);
                }
            }
            Collections.sort(przystanki);
        }
        koniecTrasyPrzystanekComboBox = new JComboBox(przystanki);
        TitledBorder tr = BorderFactory.createTitledBorder("Przystanek");
        koniecTrasyPrzystanekComboBox.setBorder(tr);
        koniecTrasyPrzystanekComboBox.setPreferredSize(new Dimension(300, 50));
        koniecTrasyPrzystanekComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String przystString = (String) koniecTrasyPrzystanekComboBox.getSelectedItem();
                koniecPrzystanek = dbConnection.getPrzystanekByWholeName(przystString);
                //System.out.println(przystanek.getRozklad().pokazRozklad());
                //updateRozkladScrollPane();
                //updateLinieComboBox();
            }
        });
    }

    private void updateKoniecPrzystanekComboBox() {
        koniecTrasyPanel.remove(koniecTrasyPrzystanekComboBox);
        setKoniecTrasyPrzystanekComboBox();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        koniecTrasyPanel.add(koniecTrasyPrzystanekComboBox, c);
        koniecTrasyPanel.validate();
//        if (koniecPrzystanek != null) {
//            koniecPrzystanek = null;
//        }
        koniecUlica = PrzystanekWidok.ULICA_DOWOLNA;
    }

    private void setObliczTraseButton() {
        obliczTraseButton = new JButton("Oblicz Trase");
        obliczTraseButton.setActionCommand("oblicz");
        obliczTraseButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ("oblicz".equalsIgnoreCase(e.getActionCommand())) {
                    polaczenie = dbConnection.getTrasa("" + poczatekPrzystanek.getId(), "" + koniecPrzystanek.getId());
                }
                frame.setTitle("Szczegóły połączenia " + poczatekPrzystanek.getNazwa() + " ---> " + koniecPrzystanek.getNazwa());
                updatePolaczenieScrollPane();
            }
        });
    }

    private void updatePolaczenieScrollPane() {
        mainPanel.remove(polaczenieScrollPane);
        setPolaczenieScrollPane();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        mainPanel.add(polaczenieScrollPane, c);
        mainPanel.validate();
    }

    private void setPolaczenieScrollPane() {
        Vector<Przystanek> trasa = new Vector<Przystanek>();
        Vector<String> trasaAndLinie = new Vector<String>();
        if (polaczenie != null) {
            if (!polaczenie.equalsIgnoreCase("")) {
                for (String p : polaczenie.split(" ")) {
                    Przystanek przystanek = dbConnection.getPrzystanekById(p);
                    trasa.add(przystanek);
                }
                for (Przystanek p : trasa) {
                    trasaAndLinie.add("[" + p.getUlica() + "]" + p.getId() + " " + p.getNazwa() + p.getLinie());
                }
            } else {
                trasaAndLinie.add("brak polaczenia");
            }

        }
        polaczenieJList = new JList(trasaAndLinie);
        polaczenieJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        polaczenieJList.setVisibleRowCount(20);
        polaczenieJList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                String wiersz = (String) polaczenieJList.getSelectedValue();
            }
        });
        TitledBorder tr = new TitledBorder("Polaczenie");
        polaczenieScrollPane = new JScrollPane(polaczenieJList);
        polaczenieScrollPane.setBorder(tr);
        polaczenieScrollPane.setPreferredSize(new Dimension(300, 400));
    }
}
