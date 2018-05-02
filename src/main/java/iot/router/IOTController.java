package iot.router;

import iot.database.Login;
import iot.database.Objects;
import iot.exceptions.RetrieveObjectsException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@org.springframework.stereotype.Controller
public class IOTController {
    Objects objects;

    @RequestMapping("/login")
    public String start(){
        return "index";
    }

    @RequestMapping("/")
    @ResponseBody
    public String verify_login(HttpEntity<String> httpEntity){
        JSONObject user_info_json = new JSONObject(httpEntity.getBody());
        Login login = new Login();
        return String.valueOf(login.verifyUser(user_info_json));
    }

    @RequestMapping("/dashboard")
    public String dashboard(){
        return "dashboard";
    }

    @RequestMapping(value = "/retrieve_objects", method = RequestMethod.GET)
    @ResponseBody
    public String get_objects(){
        JSONArray listOfObjects = new JSONArray();
        try {
            Objects objects = new Objects();
            listOfObjects = objects.retrieveAllObjects();
        }
        catch(RetrieveObjectsException ex){
            System.out.println(ex.getMessage());
        }
        return listOfObjects.toString();

    }

    @RequestMapping(value="/objects", method=RequestMethod.GET)
    public String renderObjects(){
        return "objects";
    }

    @RequestMapping(value="/updateObject", method= RequestMethod.PUT)
    @ResponseBody
    public String update_object(HttpEntity<String> httpEntity){
        JSONObject update_info_json = new JSONObject(httpEntity.getBody());
        JSONArray listOfObjects = new JSONArray();
        try{
            Objects objects = new Objects();
            objects.update_objects(update_info_json);
            listOfObjects = objects.retrieveAllObjects();
        }catch (RetrieveObjectsException ex){
            System.out.println(ex.getMessage());
        }
        return listOfObjects.toString();
    }

    @RequestMapping(value="/deleteObject", method=RequestMethod.DELETE)
    @ResponseBody
    public String delete_object(HttpEntity<String > httpEntity){
        JSONObject delete_info_json = new JSONObject(httpEntity.getBody());
        Objects objects = new Objects();
        objects.delete_object(delete_info_json);
        JSONArray listOfObjects = new JSONArray();
        try{
            listOfObjects = objects.retrieveAllObjects();
        } catch (RetrieveObjectsException ex){
            System.out.println(ex.getMessage());
        }
        return listOfObjects.toString();
    }


    @RequestMapping(value="/createObject", method=RequestMethod.POST)
    @ResponseBody
    public String create_object(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        objects.create_object(new JSONObject(httpEntity.getBody()));
        JSONArray listOfObjects = new JSONArray();
        try{
            listOfObjects = objects.retrieveAllObjects();
        } catch (RetrieveObjectsException ex){
            System.out.println(ex.getMessage());
        }
        return listOfObjects.toString();
    }

    @RequestMapping(value="/search", method=RequestMethod.POST)
    @ResponseBody
    public String searchObject(HttpEntity<String> httpEntity){
        JSONArray objectArray = new JSONArray(httpEntity.getBody());
        Objects objects = new Objects();
        JSONArray listOfObjects =  objects.searchObject(objectArray);
        return listOfObjects.toString();
    }

    @RequestMapping(value="/functions", method=RequestMethod.GET)
    public String retrieveFunctions(){
        return "functions";
    }

    @RequestMapping(value="/retrieveObjectsForSubject", method=RequestMethod.POST)
    @ResponseBody
    public String retrieveObjectsForSubject(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        String body = httpEntity.getBody();
        JSONArray listOfObjects = objects.queryAllObjectsForSubject(new JSONObject(httpEntity.getBody()));
        System.out.println(listOfObjects.toString());
        return listOfObjects.toString();
    }

    @RequestMapping(value = "/retrieveFunctionsForSubjectAndObject", method=RequestMethod.POST)
    @ResponseBody
    public String retrieveFunctionsForSubjectAndObject(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        JSONArray functions = objects.retrieveFunctionsForSubjectObject(new JSONObject(httpEntity.getBody()));
        return functions.toString();
    }

    @RequestMapping(value = "/retrieveFunctionsForObject", method=RequestMethod.POST)
    @ResponseBody
    public String retrieveFunctionsForObject(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        System.out.println(httpEntity.getBody());
        JSONArray functions = objects.retrieveFunctionsForObject(new JSONObject(httpEntity.getBody()));
        return functions.toString();
    }

    @RequestMapping(value="/checkIfFunctionIsAvailable", method=RequestMethod.POST)
    @ResponseBody
    public String checkIfFunctionIsAvailable(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        JSONObject objectExists = objects.checkIfFunctionExistsForUserAndObject(new JSONObject(httpEntity.getBody()));
        return objectExists.toString();
    }

    @RequestMapping(value="/retrieveParametersForFunction", method =RequestMethod.POST)
    @ResponseBody
    public String retrieveParametersForFunction(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        JSONArray parameters = objects.checkWhereToRetrieveParametersFrom(new JSONObject(httpEntity.getBody()));
        return parameters.toString();
    }

}

