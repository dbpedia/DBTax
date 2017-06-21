package org.dbpedia.dbtax.database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by shashank on 21/6/17.
 */
public class H2Database {
    public static void main(String[] a)
            throws Exception {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.
                getConnection("jdbc:h2::tcp://localhost/server~/test", "sa", "");
        System.out.println("Hello Done");
        // add application code here
        conn.close();
    }
}
