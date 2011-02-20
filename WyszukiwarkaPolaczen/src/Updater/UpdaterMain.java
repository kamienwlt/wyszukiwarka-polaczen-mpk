/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Updater;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author x
 */
public class UpdaterMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Updater u = new Updater();
        u.update();
        HashMap<String, LinkedList<String>> g = u.getGraf();
        System.out.println(g.toString());
    }

}
