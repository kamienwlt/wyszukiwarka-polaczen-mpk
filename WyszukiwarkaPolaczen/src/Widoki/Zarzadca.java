/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Widoki;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author x
 */
public class Zarzadca {
    private JFrame frame;
    private JPanel mainPanel;
    private JButton liniaButton;
    private JButton przystanekButton;
    private JButton polaczenieButton;

    public Zarzadca(){
        stworzWidok();
    }

    private void stworzWidok() {
        frame = new JFrame("Wyszukiwarka Polaczen");
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        setLiniaButton();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(liniaButton, c);

        setPrzystanekButton();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(przystanekButton, c);

        setPolaczenieButton();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        mainPanel.add(polaczenieButton, c);

        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void setLiniaButton() {
        liniaButton = new JButton("Pokaz szczegoly linii");
        liniaButton.setActionCommand("linia");
        liniaButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ("linia".equalsIgnoreCase(e.getActionCommand())) {
                    new LiniaWidok();
                }
            }
        });
    }

    private void setPrzystanekButton() {
        przystanekButton = new JButton("Pokaz szczegoly przystanku");
        przystanekButton.setActionCommand("przystanek");
        przystanekButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ("przystanek".equalsIgnoreCase(e.getActionCommand())) {
                    new PrzystanekWidok();
                }
            }
        });
    }

    private void setPolaczenieButton() {
        polaczenieButton = new JButton("Pokaz polaczenie miedzy przystankami");
        polaczenieButton.setActionCommand("polaczenie");
        polaczenieButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if ("polaczenie".equalsIgnoreCase(e.getActionCommand())) {
                    new PolaczenieWidok();
                }
            }
        });
    }
}
