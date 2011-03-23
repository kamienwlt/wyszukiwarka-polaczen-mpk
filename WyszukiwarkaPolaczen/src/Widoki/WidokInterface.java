/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Widoki;

import DB.Connection.DbConnectionInterface;

/**
 *
 * @author x
 */
public interface WidokInterface {
    //public void setLinia(int id);
    //public void setLinia(String id);
    public void setDbConnection(DbConnectionInterface dbc);
}
