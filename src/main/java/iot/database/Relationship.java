package iot.database;
import iot.exceptions.DatabaseInitializationException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import iot.exceptions.RetrieveFunctionsException;
import iot.exceptions.RetrieveParametersException;
import iot.exceptions.RetrieveRelationshipException;
import org.json.JSONArray;
import org.json.JSONObject;


public class Relationship extends DatabaseTemplate{
    private Connection conn = null;
    private JSONArray subjects;
    private JSONArray objects;

    public enum RelationshipQueries{
        QUERYRELATIONSHIP("SELECT SUBJECTS.subject_id, SUBJECTS.first_name, SUBJECTS.last_name, SUBJECTS.email, SUBJECTS.subject_type, " +
                          "SUBJECTS_OBJECTS_RELATIONSHIP.object_id, SUBJECTS_OBJECTS_RELATIONSHIP.function_id FROM SUBJECTS LEFT JOIN " +
                          "SUBJECTS_OBJECTS_RELATIONSHIP ON SUBJECTS.subject_id = SUBJECTS_OBJECTS_RELATIONSHIP.subject_id"),
        QUERYPARAMETERS("SELECT * FROM PARAMETERS WHERE parameter_id=%d"),
        QUERYFUNCTIONS("SELECT * FROM FUNCTIONS WHERE function_id=%d");
        private String value;

        RelationshipQueries(String value){
            this.value = value;
        }
    }

    public void set_subjects(JSONArray subjects){
        this.subjects = subjects;
    }



    public void set_relationship() throws RetrieveRelationshipException {
        int prev_subject_id = -1;
        JSONObject subject = new JSONObject();
        JSONArray subjects = new JSONArray();
        JSONArray objects = new JSONArray();
        try{
            Statement statement = this.conn.createStatement();
            ResultSet response = statement.executeQuery(RelationshipQueries.QUERYRELATIONSHIP.value);
            while (response.next()){
                int subject_id = response.getInt("subject_id");
                if (subject_id != prev_subject_id){
                    String first_name = response.getString("first_name");
                    String last_name = response.getString("last_name");
                    String subject_type = response.getString("subject_type");
                    String email = response.getString("email");
                    if (prev_subject_id != -1){
                        subject.put("objects", objects);
                        subjects.put(subject);
                    }
                    subject = new JSONObject();
                    subject.put("subject_id", subject_id);
                    subject.put("first_name", first_name);
                    subject.put("last_name", last_name);
                    subject.put("email", email);
                    subject.put("subject_type", subject_type);
                    prev_subject_id = subject_id;
                    objects = new JSONArray();
                }
                JSONObject object = new JSONObject();
                int object_id = response.getInt("object_id");
                if (object_id != 0){
                    int function_id = response.getInt("function_id");
                    object.put("object_id", object_id);
                    JSONArray functions = retrieve_functions(function_id);
                    object.put("functions", functions);
                    objects.put(object);
                }
            }
            subject.put("objects", objects);
            System.out.println(objects.toString());
        }
        catch(SQLException ex){
            throw new RetrieveRelationshipException("Could not retrieve relationships");
        }
        catch (RetrieveFunctionsException ex){
            System.out.println("could not retrieve functions");
        }
        set_subjects(subjects);
        System.out.println(subjects.toString());
    }


    public JSONArray retrieve_functions(int function_id) throws RetrieveFunctionsException{
        JSONArray functions = new JSONArray();
        try{
            Statement statement = this.conn.createStatement();
            ResultSet response = statement.executeQuery(RelationshipQueries.QUERYFUNCTIONS.value);
            while (response.next()){
                String function_name = response.getString("function_name");
                int parameter_id = response.getInt("parameter_id");
                JSONObject function = new JSONObject();
                function.put("function_name", function_name);
                JSONArray parameters= retrieve_parameters(parameter_id);
                function.put("parameters", parameters);
                functions.put(function);
            }
        }
        catch(SQLException ex){
            throw new RetrieveFunctionsException("Could not retrieve functions");
        }
        catch(RetrieveParametersException ex){
            System.out.println("Could not retrieve parameters");
        }
        return functions;
    }


    public JSONArray retrieve_parameters(int parameter_id) throws RetrieveParametersException{
        JSONArray parameters = new JSONArray();
        try{
            Statement statement = this.conn.createStatement();
            ResultSet response = statement.executeQuery(RelationshipQueries.QUERYPARAMETERS.value);
            while(response.next()){
                JSONObject parameter = new JSONObject();
                String parameter_name = response.getString("parameter_name");
                String type = response.getString("type");
                parameter.put("parameter_name", parameter_name);
                parameter.put("type", type);
                parameters.put(parameter);
            }
        }
        catch(SQLException ex){
            throw new RetrieveParametersException("Could not retrieve parameters");
        }
        return parameters;
    }

    public void close_database_connection(){
        try{
            this.conn.close();
        }
        catch(SQLException ex){
            System.out.println("hi");
        }
    }
}
