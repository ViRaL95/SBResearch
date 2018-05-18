package iot.database;

import iot.exceptions.DatabaseInitializationException;
import iot.exceptions.RetrieveObjectsException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.*;
import java.util.Random;

public class Objects extends DatabaseTemplate {
    /**
     * An ENUM USED to retrieve all queries used in our program
     */
    public enum ObjectsQueries{
        QUERYSUBJECTSOBJECTSTABLEFORSUBJECT("SELECT * FROM SUBJECTS_OBJECTS_RELATIONSHIP WHERE subject_id=%d"),
        QUERYALLOBJECTS("SELECT * FROM OBJECTS WHERE object_id=%d"),
        QUERYOBJECTANDCOLORS("SELECT * FROM OBJECTS LEFT JOIN COLORS ON OBJECTS.object_type = COLORS.object_type ORDER BY object_id"),
        QUERYALLSUBJECTS("SELECT * FROM SUBJECTS"),
        QUERYUPDATEOBJECT("UPDATE OBJECTS SET building='%s', room='%s', object_type='%s' WHERE object_id=%d"),
        QUERYUPDATESUBJECT("UPDATE SUBJECTS SET subject_type='%s', first_name='%s', last_name='%s', email='%s' WHERE subject_id=%d"),
        DELETEOBJECT("DELETE FROM OBJECTS WHERE object_id=%d"),
        DELETESUBJECT("DELETE FROM SUBJECTS WHERE subject_id=%d"),
        INSERTFUNCTIONIDOBJECTSTABLE("UPDATE OBJECTS SET function_id=%d WHERE object_id=%d"),
        INSERTFUNCTIONIDSUBJECTSOBJECTSTABLE("UPDATE SUBJECTS_OBJECTS_RELATIONSHIP SET function_id=%d WHERE subject_id=%d AND object_id=%d"),
        CREATEOBJECT("INSERT INTO OBJECTS (object_id, object_type, room, building) VALUES (%d, '%s', '%s', '%s')"),
        CREATESUBJECT("INSERT INTO SUBJECTS (subject_id, subject_type, first_name, last_name, email) VALUES (%d, '%s', '%s', '%s', '%s')"),
        CREATEFUNCTIONFORSUBJECTOBJECT("INSERT INTO FUNCTIONS (function_id, function_name) VALUES (%d, '%s')"),
        SEARCHOBJECT("SELECT * FROM SUBJECTS WHERE "),
        CHECKIFFUNCTIONEXISTSFORSUBJECTUSER("SELECT * FROM SUBJECTS_OBJECTS_RELATIONSHIP LEFT JOIN FUNCTIONS ON SUBJECTS_OBJECTS_RELATIONSHIP.function_id=FUNCTIONS.function_id WHERE subject_id=%d AND object_id=%d AND function_name='%s'"),
        RETRIEVEFUNCTIONIDFROMSUBJECTSOBJECTSTABLE("SELECT * FROM SUBJECTS_OBJECTS_RELATIONSHIP WHERE subject_id=%d AND object_id=%d"),
        FUNCTIONSQUERY("SELECT * FROM FUNCTIONS WHERE function_id=%d"),
        FUNCTIONSQUERYWITHFUNCTIONNAME("SELECT * FROM FUNCTIONS LEFT JOIN PARAMETERS ON FUNCTIONS.parameter_id=PARAMETERS.parameter_id WHERE FUNCTIONS.function_id=%d AND function_name='%s'"),
        QUERYFUNCTIONSFOROBJECT("SELECT * FROM OBJECTS LEFT JOIN OBJECT_FUNCTIONS ON OBJECTS.function_id=OBJECT_FUNCTIONS.function_id WHERE object_id=%d"),
        RETRIEVEPARAMETERIDFROMOBJECTFUNCTIONSTABLE("SELECT * FROM OBJECT_FUNCTIONS WHERE function_id=%d AND function_name='%s'"),
        QUERYOBJECTSPARAMETERS("SELECT * FROM OBJECT_PARAMETERS WHERE parameter_id=%d"),
        CREATEFUNCTIONFOROBJECT("INSERT INTO OBJECT_FUNCTIONS (function_id, function_name) VALUES (%d, '%s')"),
        DELETEFUNCTIONFROMOBJECT("DELETE FROM OBJECT_FUNCTIONS WHERE function_id=%d AND function_name='%s'");
        private String query;
        ObjectsQueries(String query){
            this.query = query;
        }
    }

    /**
     * An enum used to describe the parameters in the Object Table
     */
    public enum ObjectParameters{
        OBJECTTYPE("object_type"),
        ROOM("room"),
        BUILDING("building"),
        COLOR("color"),
        OBJECTID("object_id"),
        FUNCTIONID("function_id");
        String object_info;
        ObjectParameters(String object_info){
            this.object_info = object_info;
        }
    }

    public Objects(){
        try{
            statement = getConnection().createStatement();
        }catch (DatabaseInitializationException | SQLException ex){
        }
    }


    /**
     * The purpose of this method is to retrieve all the objects in the entire database. The point of this is to
     * populate the obejcts table.
     * @return a JSONArray containing JSONObjects with each one containing information about an object
     * @throws RetrieveObjectsException
     */
    public JSONArray retrieveAllObjects() throws RetrieveObjectsException {
        try{
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            JSONArray objects = new JSONArray();
            ResultSet response = statement.executeQuery(ObjectsQueries.QUERYOBJECTANDCOLORS.query);
            while (response.next()){
                String object_type = response.getString(ObjectParameters.OBJECTTYPE.object_info);
                String room_number = response.getString(ObjectParameters.ROOM.object_info);
                String building = response.getString(ObjectParameters.BUILDING.object_info);
                String color = response.getString(ObjectParameters.COLOR.object_info);
                int object_id = response.getInt(ObjectParameters.OBJECTID.object_info);
                int function_id = response.getInt(ObjectParameters.FUNCTIONID.object_info);
                JSONObject object = new JSONObject();
                object.put(ObjectParameters.COLOR.object_info, color);
                object.put(ObjectParameters.OBJECTTYPE.object_info, object_type);
                object.put(ObjectParameters.ROOM.object_info, room_number);
                object.put(ObjectParameters.BUILDING.object_info, building);
                object.put(ObjectParameters.OBJECTID.object_info, object_id);
                objects.put(object);
            }
            return objects;
        }
        catch(SQLException | DatabaseInitializationException ex){
            throw new RetrieveObjectsException("Could not retrieve all objects");
        }
    }

    /**
     * This method is used to retrieve all the subjects in the subjects table
     * @return a JSONArray containing all the subjects in our subjects table
     *
     */

    public JSONArray retrieveAllSubjects(){
        JSONArray subjects = new JSONArray();
        try{
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet response = statement.executeQuery(ObjectsQueries.QUERYALLSUBJECTS.query);
            while (response.next()){
                String subject_type = response.getString("subject_type");
                String first_name = response.getString("first_name");
                String last_name = response.getString("last_name");
                String email = response.getString("email");
                int subject_id = response.getInt("subject_id");
                JSONObject subject = new JSONObject();
                subject.put("subject_type", subject_type);
                subject.put("first_name", first_name);
                subject.put("last_name", last_name);
                subject.put("email", email);
                subject.put("subject_id", subject_id);
                subjects.put(subject);
            }
        }
        catch(SQLException | DatabaseInitializationException ex){
        }
        return subjects;
    }

    /**
     * This method is used when one tries to update a given object in the objects table
     * @param new_object this is the information about the object that the user would
     *                   like to update
     *
     * @throws RetrieveObjectsException
     */
    public void update_objects(JSONObject new_object) throws RetrieveObjectsException{
        try{
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            int object_id = Integer.parseInt(new_object.getString("object_id"));
            String object_type = new_object.getString("object_type");
            String object_room = new_object.getString("object_room");
            String object_building = new_object.getString("object_building");
            String update_object = String.format(ObjectsQueries.QUERYUPDATEOBJECT.query, object_building, object_room, object_type, object_id);
            statement.executeUpdate(update_object);
        }
        catch(DatabaseInitializationException | SQLException ex){
            throw new RetrieveObjectsException("Could not retrieve all objects");
        }
    }

    /**
     * This method is used to update a subject in the subjects table
     * @param new_object this is the new object containing the information for the new object
     */

    public void update_subjects(JSONObject new_object){
        try{
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            int subject_id = Integer.parseInt(new_object.getString("subject_id"));
            String subject_type = new_object.getString("subject_type");
            String first_name = new_object.getString("first_name");
            String last_name = new_object.getString("last_name");
            String email = new_object.getString("email");
            String update_object = String.format(ObjectsQueries.QUERYUPDATESUBJECT.query, subject_type, first_name, last_name, email, subject_id);
            statement.executeUpdate(update_object);
        }
        catch(DatabaseInitializationException | SQLException ex){

        }
    }


    /**
     * This method is used when one deletes a given object.
     * @param object_to_delete the information about the objecct we are trying to delete. This
     *                         will occur in the objects page when a user selects an object to delete.
     */

    public void delete_object(JSONObject object_to_delete){
        int object_id = Integer.parseInt(object_to_delete.getString("object_id"));
        String delete_object_query = String.format(ObjectsQueries.DELETEOBJECT.query, object_id);
        try {
            Connection conn = getConnection();

            PreparedStatement preparedStmt = conn.prepareStatement(delete_object_query);
            preparedStmt.executeUpdate();
        }
        catch (DatabaseInitializationException | SQLException ex){
        }

    }

    /**
     * This method is used to d delete a given subject.
     * @param subject_to_delete the information about the object we are trying to delete
     */

    public void delete_subject(JSONObject subject_to_delete){
        int object_id = Integer.parseInt(subject_to_delete.getString("subject_id"));
        String delete_object_query = String.format(ObjectsQueries.DELETESUBJECT.query, object_id);
        try {
            Connection conn = getConnection();
            PreparedStatement preparedStmt = conn.prepareStatement(delete_object_query);
            preparedStmt.executeUpdate();
        }
        catch (DatabaseInitializationException | SQLException ex){
        }

    }

    /**
     * This method is used whne one wants to create a new object in the objects table
     * @param object_to_create This JSONObject contains information about the object the user would like to create
     */
    public void create_object(JSONObject object_to_create){
        int object_id = Integer.parseInt(object_to_create.getString("object_id"));
        String object_type = object_to_create.getString("object_type");
        String object_room = object_to_create.getString("object_room");
        String object_building = object_to_create.getString("object_building");
        String create_object_query = String.format(ObjectsQueries.CREATEOBJECT.query, object_id, object_type, object_room, object_building);
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(create_object_query);
            preparedStatement.executeUpdate();
            addFunctionForObject(object_to_create);
        }
        catch (DatabaseInitializationException | SQLException ex){
        }

    }

    /**
     * This method is used to create a new subject in the subjects table
     * @param subject_to_create The JSONObject containing information about the subject we would like to create
     */
    public void create_subject(JSONObject subject_to_create){
        int subject_id = Integer.parseInt(subject_to_create.getString("subject_id"));
        String subject_type = subject_to_create.getString("subject_type");
        String email = subject_to_create.getString("email");
        String first_name = subject_to_create.getString("first_name");
        String last_name = subject_to_create.getString("last_name");
        String create_subject_query = String.format(ObjectsQueries.CREATESUBJECT.query, subject_id, subject_type, first_name, last_name, email);
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(create_subject_query);
            preparedStatement.executeUpdate();
        }
        catch (DatabaseInitializationException | SQLException ex){
        }
    }

    /**
     * If a user wants to search for a given object he can use this method. This method builds
     * a query based on the object information (building, object type, room) retrieved from the
     * user. The javascript code sends this method information about the types of each property

     * @param objectToSearch Information on the object that we would like to search. The information in this is used to build th search query
     * @return a JSONArray containing all the objects that match the searched object
     */
    public JSONArray searchObject(JSONArray objectToSearch){
        String searchObject = ObjectsQueries.SEARCHOBJECT.query;
        JSONArray searchObjectsArray = new JSONArray();
        for (int i=0; i<objectToSearch.length(); i++){
            JSONObject currentObject = (JSONObject) objectToSearch.get(i);
            String query_type = currentObject.getString("queryType");
            String type = currentObject.getString("type");
            String value = currentObject.getString("value");
            searchObject += query_type + "=";
            if (type.equals("string")){
                searchObject += "'"+ value + "'";
            }
            if (type.equals("int")){
                searchObject += value;
            }
            if (i != objectToSearch.length() - 1){
                searchObject += " AND ";
            }
        }

        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet response = statement.executeQuery(searchObject);
            while (response.next()){
                int object_id = response.getInt("object_id");
                String building = response.getString("building");
                String room = response.getString("room");
                String objectType = response.getString("object_type");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("object_id", object_id);
                jsonObject.put("building", building);
                jsonObject.put("room", room);
                jsonObject.put("object_type", objectType);
                searchObjectsArray.put(jsonObject);
            }
        } catch (DatabaseInitializationException | SQLException ex){
        }
        return searchObjectsArray;
    }


    /**
     * If a user wants to search for a given subject he can use this method. This method builds
     * a query based on the object information (building, object type, room) retrieved from the
     * user. The javascript code sends this method information about the types of each property

     * @param objectToSearch Information on the object that we would like to search. The information in this is used to build th search query
     * @return a JSONArray containing all the objects that match the searched object
     */
    public JSONArray searchSubject(JSONArray objectToSearch){
        String searchSubject = ObjectsQueries.SEARCHOBJECT.query;
        JSONArray searchSubjectsArray = new JSONArray();
        for (int i=0; i<objectToSearch.length(); i++){
            JSONObject currentObject = (JSONObject) objectToSearch.get(i);
            String query_type = currentObject.getString("queryType");
            String type = currentObject.getString("type");
            String value = currentObject.getString("value");
            searchSubject += query_type + "=";
            if (type.equals("string")){
                searchSubject += "'"+ value + "'";
            }
            if (type.equals("int")){
                searchSubject += value;
            }
            if (i != objectToSearch.length() - 1){
                searchSubject += " AND ";
            }
        }

        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet response = statement.executeQuery(searchSubject);
            while (response.next()){
                int subject_id = response.getInt("subject_id");
                String first_name = response.getString("first_name");
                String last_name = response.getString("last_name");
                String email = response.getString("email");
                String subject_type = response.getString("subject_type");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("subject_id", subject_id);
                jsonObject.put("first_name", first_name);
                jsonObject.put("last_name", last_name);
                jsonObject.put("email", email);
                jsonObject.put("subject_type", subject_type);
                searchSubjectsArray.put(jsonObject);
            }
        } catch (DatabaseInitializationException | SQLException ex){
        }
        return searchSubjectsArray;
    }





    /**
     * This is for the relationship page when we are trying to find the list of
     * subjects that may be available for a given object according to the subjects_objects_relationship
     * table
     *
      * @param subject The subject that must be used to find all objects available to it
     * @return The JSONArray containing all the object's that are accessible to a given subject
     */
    public JSONArray queryAllObjectsForSubject(JSONObject subject){
        String querySubjects = String.format(ObjectsQueries.QUERYSUBJECTSOBJECTSTABLEFORSUBJECT.query, subject.getInt("subject_id"));
        JSONArray objects = new JSONArray();
        try {
            Statement statement = getConnection().createStatement();
            ResultSet response = statement.executeQuery(querySubjects);
            while (response.next()){
                int object_id = response.getInt("object_id");
                Statement statement2 = getConnection().createStatement();
                String queryObjects = String.format(ObjectsQueries.QUERYALLOBJECTS.query, object_id);
                ResultSet response2 = statement2.executeQuery(queryObjects);
                while (response2.next()){
                    String building = response2.getString("building");
                    String room = response2.getString("room");
                    String object_type = response2.getString("object_type");
                    JSONObject object = new JSONObject();
                    object.put("building", building );
                    object.put("room", room);
                    object.put("object_type", object_type);
                    object.put("object_id", object_id);
                    objects.put(object);
                }
            }

        } catch(SQLException | DatabaseInitializationException ex){
        }
        return objects;
    }

    /**
     * To find all the functions available to a given object. This may be compared to all
     * the functions available to a given subject and object. We may retrieve the object id that is selected, then
     * retrieve the function id from this and then query the object functions table to retrieve all the functions
     * available to a given object.
     *
     * @param function_information The information required to retrieve all the functions for a given object
     * @return A JSONArray containing an array of JSONObjects which in thus contain information about each individual
     * function
     */
    public JSONArray retrieveFunctionsForObject(JSONObject function_information){
        int object_id = function_information.getInt("object_id");
        String retrieveAllFunctionsQuery = String.format(ObjectsQueries.QUERYFUNCTIONSFOROBJECT.query, object_id);
        JSONArray functions = new JSONArray();
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet response = statement.executeQuery(retrieveAllFunctionsQuery);
            while(response.next()){
             String functionName = response.getString("function_name");
             functions.put(new JSONObject().put("function_name", functionName));
            }
        }
        catch(SQLException | DatabaseInitializationException ex){

        }
        return functions;
    }

    /**
     * This method is used to delete a given function under an object
     * @param functionObjectInformation
     */
    public void deleteFunctionForObject(JSONObject functionObjectInformation){
        int object_id = functionObjectInformation.getInt("object_id");
        String function_name = functionObjectInformation.getString("function_name");
        String queryObjects = String.format(ObjectsQueries.QUERYALLOBJECTS.query, object_id);
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet response = statement.executeQuery(queryObjects);
            while (response.next()){
                int function_id = response.getInt("function_id");
                String deleteFunctionQuery = String.format(ObjectsQueries.DELETEFUNCTIONFROMOBJECT.query, function_id, function_name);
                Statement statement2 = conn.createStatement();
                statement2.executeUpdate(deleteFunctionQuery);
            }
        } catch (SQLException | DatabaseInitializationException ex){

        }
        return;
    }

    /**
     * This method is used to add a function to a given object.
     * @param functionObjectInformation
     */
    public void addFunctionForObject(JSONObject functionObjectInformation){
        int object_id = functionObjectInformation.getInt("object_id");
        String function_name = functionObjectInformation.getString("function_name");
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            String queryObjects = String.format(ObjectsQueries.QUERYALLOBJECTS.query, object_id);
            ResultSet response = statement.executeQuery(queryObjects);
            while (response.next()){
                int function_id = response.getInt("function_id");
                if (response.wasNull()){
                    function_id = generateRandomNumberForPrimaryKey();
                    String primaryKeyQuery = String.format(ObjectsQueries.INSERTFUNCTIONIDOBJECTSTABLE.query, function_id, object_id);
                    Statement statement1 = conn.createStatement();
                    statement1.executeUpdate(primaryKeyQuery);
                }
                String functionsQuery = String.format(ObjectsQueries.CREATEFUNCTIONFOROBJECT.query, function_id, function_name);
                Statement statement2 = conn.createStatement();
                statement2.executeUpdate(functionsQuery);
            }
        } catch (SQLException | DatabaseInitializationException ex){

        }
    }

    /**
     * This method is used to generate a random number for the primary key
     * @return int. The primary key.
     */

    public int generateRandomNumberForPrimaryKey(){
        Random primaryKey = new Random();
        int Low = 1;
        int High = 1000000;
        return primaryKey.nextInt(High - Low) + Low;
    }

    /**
     * This method is used to retrieve all functions for a given subject and object.
     *
     * @param subjectObjectInformation The subject and object information we are trying to retrieve all the functions accessible for
     * @return Return a JSONArray containing JSONObjects with information regarding the functions accessible for a given subject and object
     */
    public JSONArray retrieveFunctionsForSubjectObject(JSONObject subjectObjectInformation){
        String query = String.format(ObjectsQueries.RETRIEVEFUNCTIONIDFROMSUBJECTSOBJECTSTABLE.query, subjectObjectInformation.getInt("subject_id"), subjectObjectInformation.getInt("object_id"));
        JSONArray functions = new JSONArray();
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet response = statement.executeQuery(query);
            while(response.next()){
                //RETRIEVE THE FUNCTION ID FOR GIVEN SUBJECT AND OBJECT. USE THAT TO RETRIEVE ALL FUNCTIONS
                int function_id = response.getInt("function_id");
                String query2 = String.format(ObjectsQueries.FUNCTIONSQUERY.query, function_id);
                Statement statement2 = conn.createStatement();
                ResultSet response2 = statement2.executeQuery(query2);
                while (response2.next()){
                    JSONObject function = new JSONObject();
                    function.put("function_name", response2.getString("function_name"));
                    functions.put(function);
                }

            }
        }
        catch(SQLException | DatabaseInitializationException ex){

        }
        return functions;
    }

    /**
     * This is used to check if a function exists for a given subject and object.
     * This can be used to disable or enable the enableFunction Button or the disableFunction Button.
     * @param relationshipInformation The JSONObject containing information on the subject, object and the function
     * @return a JSONObject with a true or false value signifying whether or not the function exists for the subject or not
     */
    public JSONObject checkIfFunctionExistsForUserAndObject(JSONObject relationshipInformation){
        int object_id  = relationshipInformation.getInt("object_id");
        int subject_id = relationshipInformation.getInt("subject_id");
        String function_name = relationshipInformation.getString("function");
        String query = String.format(ObjectsQueries.CHECKIFFUNCTIONEXISTSFORSUBJECTUSER.query, subject_id, object_id, function_name);
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet response = statement.executeQuery(query);
            while (response.next()) {
                return new JSONObject().put("function_exists", true);
            }
        } catch (SQLException | DatabaseInitializationException ex){

        }
        return new JSONObject().put("function_exists", false);
    }

    /**
     * This method is used to find whether to retrieve parameters from the object parameters table or the parameters table. Depending
     * on whether or not the function is accessible for a given subject, the parameter exists in the parameters table or the object parameters table.
     * This method utilizes the checkIfFunctionExistsForUserAndSubject method in order to find out which table to search
     *
     * Case 1: Function is accesible to a given subject. If this is the case then one should check the Parameters Table
     * Case 2: Fucntion is not accessible to a given subject. If this is the case then one should check the object parameters table.
     * @param subjectObjectRelationshipInformation
     * @return a JSONArray containing JSONObjects with the respective parameters for a function.
     */
    public boolean  checkWhereToRetrieveParametersFrom(JSONObject subjectObjectRelationshipInformation){
        int object_id = subjectObjectRelationshipInformation.getInt("object_id");
        JSONObject functionExistsForUser = checkIfFunctionExistsForUserAndObject(subjectObjectRelationshipInformation);
        JSONArray parameters;
        if (functionExistsForUser.getBoolean("function_exists")){
            //RETRIEVE PARAMETERS FROM FUNCTIONS TABLE
            return true;
        }
        else{
            return false;
            //RETRIEVE PARAMETERS FROM OBJECT FUNCTIONS TABE
        }
    }

    /**
     * This method finds all the parameters for a given object. One should search the objects table, the object functions table
     * and the object parameters table.
     * @param object_and_function_information
     * @return a JSONArray containing all the parameters for a given function
     */

    public JSONArray retrieveParametersForFunctionThatDoesntExist(JSONObject object_and_function_information){
        int object_id = object_and_function_information.getInt("object_id");
        String function_name = object_and_function_information.getString("function");
        String query = String.format(ObjectsQueries.QUERYALLOBJECTS.query, object_id);
        JSONArray parameters = new JSONArray();
        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet response = statement.executeQuery(query);
            while (response.next()) {
                int function_id = response.getInt("function_id");
                Statement statement2 = conn.createStatement();
                String parameterIDQuery = String.format(ObjectsQueries.RETRIEVEPARAMETERIDFROMOBJECTFUNCTIONSTABLE.query, function_id, function_name);
                ResultSet response2  = statement2.executeQuery(parameterIDQuery);
                while (response2.next()){
                    int parameter_id = response2.getInt("parameter_id");
                    String parametersQuery = String.format(ObjectsQueries.QUERYOBJECTSPARAMETERS.query, parameter_id);
                    Statement statement3 = conn.createStatement();
                    ResultSet response3 = statement3.executeQuery(parametersQuery);
                    while (response3.next()) {
                        String parameter = response3.getString("parameter_name");
                        parameters.put(new JSONObject().put("parameter_name", parameter));
                    }
                }
            }
        } catch (SQLException | DatabaseInitializationException ex){

        }
        return parameters;
    }

    /**
     * This method is used to find the parameters for a function that is accessible for a given subject and object.
     * @param relationshipInformation The JSONObject containing the subject id, object id and function name that
     * we are trying to retrieve the parameters for
     * @return a JSONArray with JSONObjects each containing a parameter.
     */

    public JSONArray retrieveParametersForFunctionThatExistsForSubject(JSONObject relationshipInformation){
        int object_id = relationshipInformation.getInt("object_id");
        int subject_id = relationshipInformation.getInt("subject_id");
        String function_name = relationshipInformation.getString("function");
        JSONArray parameters = new JSONArray();
        try {
            String query = String.format(ObjectsQueries.RETRIEVEFUNCTIONIDFROMSUBJECTSOBJECTSTABLE.query, subject_id, object_id);
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet response = statement.executeQuery(query);
            while (response.next()){
                int function_id = response.getInt("function_id");
                String query2 = String.format(ObjectsQueries.FUNCTIONSQUERYWITHFUNCTIONNAME.query, function_id, function_name);
                Statement statement2 = conn.createStatement();
                ResultSet response2 = statement2.executeQuery(query2);
                while (response2.next()){
                    JSONObject parameter = new JSONObject();
                    String parameter_name = response2.getString("parameter_name");
                    if (response2.wasNull()){
                        return parameters;
                    }
                    parameter.put("parameter_name", response2.getString("parameter_name"));
                    parameters.put(parameter);
                }
            }
        }
        catch (SQLException | DatabaseInitializationException ex){

        }
        return parameters;
    }
    /**
     * Enable Function that is not available for a given subject and object but which is available for an object.
     * @param relationshipInformation contains information about the object and subject
     */

    public void enableFunctionForSubjectObjectRelationship(JSONObject relationshipInformation){
        int object_id = relationshipInformation.getInt("object_id");
        int subject_id = relationshipInformation.getInt("subject_id");
        String function_name = relationshipInformation.getString("function_name");
        String query = String.format(ObjectsQueries.RETRIEVEFUNCTIONIDFROMSUBJECTSOBJECTSTABLE.query, subject_id, object_id);
        try{
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            ResultSet response = statement.executeQuery(query);
            while (response.next()){
                int function_id = response.getInt("function_id");
                if (response.wasNull()){
                    function_id = generateRandomNumberForPrimaryKey();
                    String primaryKeyQuery = String.format(ObjectsQueries.INSERTFUNCTIONIDSUBJECTSOBJECTSTABLE.query, function_id, subject_id, object_id);
                    Statement statement1 = conn.createStatement();
                    statement1.executeUpdate(primaryKeyQuery);
                }
                String createFunctionQuery = String.format(ObjectsQueries.CREATEFUNCTIONFORSUBJECTOBJECT.query, function_id, function_name);
                Statement statement2 = conn.createStatement();
                statement2.executeUpdate(createFunctionQuery);
            }
        } catch (DatabaseInitializationException | SQLException ex){

        }
    }
}
