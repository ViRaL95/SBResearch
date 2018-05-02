package iot.database;

import iot.exceptions.DatabaseInitializationException;
import org.apache.catalina.User;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login extends DatabaseTemplate {

    public Login(){
        try {
            statement = getConnection().createStatement();
        }
        catch(DatabaseInitializationException | SQLException ex){
            System.out.println(ex.getMessage());
        }
    }
    public enum UserQueries{
        VERIFYUSER("SELECT * FROM LOGIN WHERE email = '%s' and password = '%s'");

        private String value;

        UserQueries(String value){
            this.value = value;
        }
    }

    public boolean verifyUser(JSONObject user_info){
        String email = user_info.get("email").toString();
        String password = user_info.get("password").toString();
        boolean user_exists = false;
        try{
            String query = String.format(UserQueries.VERIFYUSER.value, email, password);
            response = statement.executeQuery(query);
            while(response.next()){
                user_exists = true;
            }

        }
        catch (SQLException ex){
            System.out.println("SQL EXCEPTION "+ex);
        }
        return user_exists;
    }

}
