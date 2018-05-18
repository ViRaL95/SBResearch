package iot.router;

import iot.database.Objects;
import iot.exceptions.RetrieveObjectsException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@org.springframework.stereotype.Controller
public class IOTController {
    Objects objects;

    /**
     * This method is called when a request ot the uri /dashboard is made
     * @return
     */

    @RequestMapping("/")
    public String dashboard(){
        return "dashboard";
    }

    /**
     * This method is called when a request ot the uri /retrieve_objects is made
     * @return
     */
    @RequestMapping(value = "/retrieve_objects", method = RequestMethod.GET)
    @ResponseBody
    public String get_objects(){
        JSONArray listOfObjects = new JSONArray();
        try {
            Objects objects = new Objects();
            listOfObjects = objects.retrieveAllObjects();
        }
        catch(RetrieveObjectsException ex){
        }
        return listOfObjects.toString();

    }

    /**
     * This method is called when a request ot the uri /retrieve_subjects is made
     * @return
     */
    @RequestMapping(value = "/retrieve_subjects", method = RequestMethod.GET)
    @ResponseBody
    public String get_subjects(){
        JSONArray listOfSubjects = new JSONArray();
        Objects objects = new Objects();
        listOfSubjects = objects.retrieveAllSubjects();
        return listOfSubjects.toString();
    }

    /**
     * This method is called when a request ot the uri /objects is made
     * @return
     */
    @RequestMapping(value="/objects", method=RequestMethod.GET)
    public String renderObjects(){
        return "objects";
    }
    /**
     * This method is called when a request ot the uri /updateObject is made
     * @param httpEntity
     * @return
     */
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
        }
        return listOfObjects.toString();
    }

    /**
     * This method is called when a request ot the uri /updateSubject is made
     * @param httpEntity
     * @return
     */
    @RequestMapping(value="/updateSubject", method= RequestMethod.PUT)
    @ResponseBody
    public String update_subject(HttpEntity<String> httpEntity){
        JSONObject update_info_json = new JSONObject(httpEntity.getBody());
        JSONArray listOfSubjects = new JSONArray();
        Objects objects = new Objects();
        objects.update_subjects(update_info_json);
        listOfSubjects = objects.retrieveAllSubjects();
        return listOfSubjects.toString();
    }

    /**
     * This method is called when a request ot the uri /deleteObject is made
     * @param httpEntity
     * @return
     */

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
        }
        return listOfObjects.toString();
    }

    /**
     * This method is called when a request ot the uri /deleteSubject is made
     * @param httpEntity
     * @return
     */
    @RequestMapping(value="/deleteSubject", method=RequestMethod.DELETE)
    @ResponseBody
    public String delete_subject(HttpEntity<String > httpEntity){
        JSONObject delete_info_json = new JSONObject(httpEntity.getBody());
        Objects objects = new Objects();
        objects.delete_subject(delete_info_json);
        JSONArray listOfSubjects = new JSONArray();
        listOfSubjects = objects.retrieveAllSubjects();
        return listOfSubjects.toString();
    }

    /**
     * This method is called when a request ot the uri /createObject is made
     * @param httpEntity
     * @return
     */
    @RequestMapping(value="/createObject", method=RequestMethod.POST)
    @ResponseBody
    public String create_object(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        objects.create_object(new JSONObject(httpEntity.getBody()));
        JSONArray listOfObjects = new JSONArray();
        try{
            listOfObjects = objects.retrieveAllObjects();
        } catch (RetrieveObjectsException ex){
        }
        return listOfObjects.toString();
    }

    /**
     * This method is called when a request ot the uri /createSubject is made
     * @param httpEntity
     * @return
     */

    @RequestMapping(value="/createSubject", method=RequestMethod.POST)
    @ResponseBody
    public String create_subject(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        objects.create_subject(new JSONObject(httpEntity.getBody()));
        JSONArray listOfSubjects = new JSONArray();
        listOfSubjects = objects.retrieveAllSubjects();
        return listOfSubjects.toString();
    }

    /**
     * This method is called when a request ot the uri /addFunctionforObject is made
     * @param httpEntity
     * @return
     */
    @RequestMapping(value="/search", method=RequestMethod.POST)
    @ResponseBody
    public String searchObject(HttpEntity<String> httpEntity){
        JSONArray objectArray = new JSONArray(httpEntity.getBody());
        Objects objects = new Objects();
        JSONArray listOfObjects =  objects.searchObject(objectArray);
        return listOfObjects.toString();
    }

    /**
     * This method is called when a request ot the uri /searchForSubject is made
     * @param httpEntity
     * @return
     */
    @RequestMapping(value="/searchForSubject", method=RequestMethod.POST)
    @ResponseBody
    public String searchSubject(HttpEntity<String> httpEntity){
        JSONArray subjectArray = new JSONArray(httpEntity.getBody());
        Objects objects = new Objects();
        JSONArray listOfObjects =  objects.searchSubject(subjectArray);
        return listOfObjects.toString();
    }

    /**
     * This method is called when a request ot the uri /functions is made
     * @return
     */
    @RequestMapping(value="/functions", method=RequestMethod.GET)
    public String retrieveFunctions(){
        return "functions";
    }

    /**
     * This method is called when a request ot the uri /subjects is made
     * @return
     */
    @RequestMapping(value="/subjects", method=RequestMethod.GET)
    public String retrieveSubjects(){
        return "subjects";
    }

    /**
     * This method is called when a request ot the uri /retrieveObjectsForSubject is made
     * @param httpEntity
     * @return
     */
    @RequestMapping(value="/retrieveObjectsForSubject", method=RequestMethod.POST)
    @ResponseBody
    public String retrieveObjectsForSubject(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        String body = httpEntity.getBody();
        JSONArray listOfObjects = objects.queryAllObjectsForSubject(new JSONObject(httpEntity.getBody()));
        return listOfObjects.toString();
    }

    /**
     * This method is called when a request ot the uri /retrieveFunctionsForSubjectAndObject is made
     * @param httpEntity
     * @return
     */

    @RequestMapping(value = "/retrieveFunctionsForSubjectAndObject", method=RequestMethod.POST)
    @ResponseBody
    public String retrieveFunctionsForSubjectAndObject(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        JSONArray functions = objects.retrieveFunctionsForSubjectObject(new JSONObject(httpEntity.getBody()));
        return functions.toString();
    }

    /**
     * This method is called when a request ot the uri /retrieveFunctionsForObject is made
     * @param httpEntity
     * @return
     */
    @RequestMapping(value = "/retrieveFunctionsForObject", method=RequestMethod.POST)
    @ResponseBody
    public String retrieveFunctionsForObject(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        JSONArray functions = objects.retrieveFunctionsForObject(new JSONObject(httpEntity.getBody()));
        return functions.toString();
    }

    /**
     * This method is called when a request ot the uri /checkIfFunctionIsAvailable is made
     * @param httpEntity
     * @return
     */
    @RequestMapping(value="/checkIfFunctionIsAvailable", method=RequestMethod.POST)
    @ResponseBody
    public String checkIfFunctionIsAvailable(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        JSONObject objectExists = objects.checkIfFunctionExistsForUserAndObject(new JSONObject(httpEntity.getBody()));
        return objectExists.toString();
    }

    /**
     * This method is called when a request ot the uri /retrieveParametersForFunction is made
     * @param httpEntity
     * @return
     */
    @RequestMapping(value="/retrieveParametersForFunction", method =RequestMethod.POST)
    @ResponseBody
    public String retrieveParametersForFunction(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        JSONArray parameters;
        if (objects.checkWhereToRetrieveParametersFrom(new JSONObject(httpEntity.getBody()))){
            parameters = objects.retrieveParametersForFunctionThatExistsForSubject(new JSONObject(httpEntity.getBody()));
        }
        else{
            parameters = objects.retrieveParametersForFunctionThatDoesntExist(new JSONObject(httpEntity.getBody()));
        }
        return parameters.toString();
    }

    /**
     * This method is called when a request ot the uri /deleteFunctionForObject is made
     * @param httpEntity
     * @return
     */
    @RequestMapping(value="/deleteFunctionForObject", method =RequestMethod.DELETE)
    @ResponseBody
    public String deleteFunctionForObject(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        objects.deleteFunctionForObject(new JSONObject(httpEntity.getBody()));
        JSONArray functions = objects.retrieveFunctionsForObject(new JSONObject(httpEntity.getBody()));
        return functions.toString();
    }

    /**
     * This method is called when a request ot the uri /addFunctionforObject is made
     * @param httpEntity
     * @return
     */

    @RequestMapping(value="/addFunctionForObject", method=RequestMethod.POST)
    @ResponseBody
    public String createFunctionForObject(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        objects.addFunctionForObject(new JSONObject(httpEntity.getBody()));
        JSONArray functions = objects.retrieveFunctionsForObject(new JSONObject(httpEntity.getBody()));
        return functions.toString();
    }

    /**
     * This method is called when a request to the uri /enableFunctionforSubjectObject is made
     * @param httpEntity
     * @return
     */
    @RequestMapping(value="/enableFunctionForSubjectObject", method=RequestMethod.POST)
    @ResponseBody
    public String enableFunctionForSubjectObject(HttpEntity<String> httpEntity){
        Objects objects = new Objects();
        objects.addFunctionForObject(new JSONObject(httpEntity.getBody()));
        objects.enableFunctionForSubjectObjectRelationship(new JSONObject(httpEntity.getBody()));
        return "";
    }

}

