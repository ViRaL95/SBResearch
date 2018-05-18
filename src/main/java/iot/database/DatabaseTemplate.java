package iot.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import iot.exceptions.DatabaseInitializationException;
public abstract class DatabaseTemplate{
    /**
     * An enum containing database information.
     */
    protected enum DatabaseConnectionTypes {
        USERNAME("user=root&"),  PASSWORD("password=FanYe-2018"), SERVER("localhost:3306/"), CONNECTIONTYPE("jdbc:mysql://"), DATABASENAME("MANAGEMENT_SYSTEM?");

        private String value;

        DatabaseConnectionTypes(String value){
            this.value = value;
        }
    }


    private static Connection conn;
    protected static Statement statement =               null;
    protected static ResultSet response  =               null;
    protected static String url   =                      DatabaseConnectionTypes.CONNECTIONTYPE.value + DatabaseConnectionTypes.SERVER.value + DatabaseConnectionTypes.DATABASENAME.value +
                                                         DatabaseConnectionTypes.USERNAME.value + DatabaseConnectionTypes.PASSWORD.value;

    /**
     *
     * @return This class follows a singleton patten to enure that only one connection is made to the database
     * @throws DatabaseInitializationException
     */

    public static Connection getConnection () throws DatabaseInitializationException {
        try{
            if (conn == null) {
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(url);
            }
              return conn;
        }
        catch(Exception ex){
            throw new DatabaseInitializationException("Database could not be intiialized");
        }
    }

    public void close_connection(){
        if (this.response != null){
            try{
                this.response.close();
            }
            catch (SQLException sqlEx){}
            this.response = null;
        }
        if (this.statement != null){
            try{
                this.statement.close();
            } catch(SQLException sqlEx){}
            this.statement = null;
        }
    }
}
