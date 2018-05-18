$(document).ready(function(){
    retrieveObjects();
    //CALL TEH ADD FUNCTION METHOD WHEN THE BUTTON WITH ID add_function_button
    $("#add_function_button").click(addFunction);
    //CALL AN UPDATE METHOD WHEN THE UPDATE BUTTON IS CLICKED
    $("#update_button").click(submitUpdate);
    //CREAETE A NEW OBJECT
    $("#create_button").click(function(){
        object_id = $("#create_object_id_section").children("input")[0];

        object_type = $("#create_object_type_section").children("input")[0];

        object_room_input = $("#create_object_room_section").children("input")[0];

        object_building_input = $("#create_object_building_section").children("input")[0];

        function_ = $("#create_function_section").children("input")[0];
        //COLLECT THE OBJECT INFROMTAION FROM THE UI
        var create_info = {
            'object_id': $(object_id).val(),
            'object_type': $(object_type).val(),
            'object_room': $(object_room_input).val(),
            'object_building': $(object_building_input).val(),
            'function_name': $(function_).val()
        }

        var request = {
            type: 'POST',
            url: '/createObject',
            data: JSON.stringify(create_info),
            contentType: 'application/json',
            success: successfulObjectCreation,
            error: error
        }


        $.ajax(request);
        //ON SUCCESSFUL OBJECT CREATIONT THIS METHOD WILL BE CALLED
        function successfulObjectCreation(data){
            generateTable(JSON.parse(data));
        }

        function error(){
        }
    })

    function success(data){
    }
    function error(data){
    }
    //THIS METHOD IS CALLED WHEN THE SEARCH BUTTON IS CLICKED
    $("#search_object_button").click(function(){
        object_type = $("#search_object_type").val();
        object_room = $("#search_object_room").val();
        object_building = $("#search_object_building").val();
        //THIS METHOD BUILDS INFORMATION ABOUT THE TYPE OF SEARCHED FUNCTIONALITY
        //SO THE BACKEND CAN THEN KNOW HOW TO BUILD THE SQL QUERY
        search_info = [

        ]

        allEmpty = true;
        //BUILD INFORMATION ABOUT THE OBJECT TYPE CHOSEN IF ITS INPUT FIELD IS NOT EMPTY
        if (object_type != ""){
            object_type_structure = {"queryType": "object_type", "type": "string", "value": object_type};
            search_info.push(object_type_structure);
            allEmpty = false
        }
        //BUILD INFORMATION ABOUT THE OBJECT ROOM CHOSEN IF ITS INPUT FIELD IS NOT EMPTY

        if (object_room != ""){
            object_room_structure = {"queryType":"room", "type": "string", "value": object_room};
            search_info.push(object_room_structure);
            allEmpty = false;
        }
        //BUILD INFORMATION ABOUT THE OBJECT BUILDING CHOSEN IF ITS INPUT FIELD IS NOT EMPTY

        if (object_building != ""){
            object_building_structure = {"queryType": "building", "type": "string", "value": object_building };
            search_info.push(object_building_structure);
            allEmpty = false;
        }
        //IF NONE OF THE PARAMETERS WERE SEARCHED FOR THEN RETURN THE ENTIRE LIST OF OBJECTS
        if (allEmpty){
            retrieveObjects();
            return;
        }

        request = {
            type: 'POST',
            url: '/search',
            data: JSON.stringify(search_info),
            contentType: 'application/json',
            success: success,
            error: error
        }

        function success(data){
            data = JSON.parse(data)
            generateTable(data);
        }

        function error(data){
        }
        //MAKE SEARCH REQUEST
        $.ajax(request);

    })

})

//THIS METHOD RETRIEVES ALL OBJECTS IN THE DATABASE
function retrieveObjects(){
    request = {
        type: 'GET',
        url: '/retrieve_objects',
        success: success,
        error:error
    }
    $.ajax(request);
    //GENERATE THE TABLE DATA WHEN A SUCCESFULL CONNECTION IS MADE
    function success(data){
        data = JSON.parse(data);
        generateTable(data);
    }

    function error(){

    }
}

//ITERATE THROUGH THE data ROW BY ROW and CREATE ROWS IN A TABLE
function generateTable(data){
    $("table tbody").empty();
    for (index=0; index<data.length; index++){
        object = data[index];
        object_id = object.object_id;
        object_type = object.object_type;
        object_building = object.building;
        object_room = object.room;

        $("table tbody").append("<tr> </tr>");
        row = $("table tbody tr")[index];
        $(row).append("<td>" + object_id + "</td>");
        $(row).append("<td>" + object_type + "</td>");
        $(row).append("<td>" + object_room + "</td>");
        $(row).append("<td>" + object_building + "</td>");
        //ADD A DELETE BUTTON TO THE TABLE
        $(row).append("<td> <button class='glyphicon glyphicon-remove-sign btn btn-default delete' type='button'></button> </td>");
        //ADD A UPDATE BUTTON TO THE TABLE
        $(row).append("<td><button class='glyphicon glyphicon-refresh update btn btn-default' type='button'  data-toggle='modal' data-target='#update_dialog'></button></td>");
    }
    //CALL THESE METHODS FOR THE RESPECTIVE DELETION AND UPDATION
    $(".delete").click(deleteObject);
    $(".update").click(updateButtonClicked);
}


function retrieveAllFunctionsForObject(object_id){
    var objectIdData = {'object_id': object_id};

    var request = {
        type: 'POST',
        url: '/retrieveFunctionsForObject',
        contentType: 'application/json',
        data: JSON.stringify(objectIdData),
        success: generateFunctionsOnSelect
    }

    $("#functions").empty();
    $.ajax(request);


    function error(data){
    }
}

//RENDER ALL FUNCTIONS ON FUNCTIONS SELECT OPTION
function generateFunctionsOnSelect(data){
    functions = JSON.parse(data);
    $("#functions").empty();
    for (i=0; i<functions.length; i++){
        function_ = functions[i].function_name;
        //IF THE EDGE CASE WHERE functions JSON ARRAY IS =  [ { } ],
        //A JSON ARRAY CONTAINING AN EMPTY JSON OBJECT THEN CHECK
        //IF the function is undefined and if it is return
        if (function_ === undefined){
            return;
        }
        //APPEND AN OPTION TO THE SELECT
        $("#functions").append("<option>"+function_+"</option>");
    }
}

//THE UPDATE BUTTON IS CLICKED
function updateButtonClicked (){
    row_index = $(this).parent("td").parent("tr").index();
    row = $("table tbody tr")[row_index];
    object_id = $(row).children("td")[0];
    type = $(row).children("td")[1];
    room = $(row).children("td")[2];
    building = $(row).children("td")[3];
    retrieveAllFunctionsForObject($(object_id).html());
    //DELETE A FUNCTION THROUGH THIS
      $("#delete_function_button").click(function(){
        function_name = $("select").val();

        var objectData = {'object_id': $(object_id).html(), 'function_name': function_name};

        var request = {
            type: 'DELETE',
            url: '/deleteFunctionForObject',
            contentType: 'application/json',
            data: JSON.stringify(objectData),
            success: generateFunctionsOnSelect
        }
        $.ajax(request);

    })
    //SHOW THE UPDATE WINDOW WHEN THE UDPATE BUTTON IS CLICKED
    show_update_window($(object_id).html(), $(type).html(), $(room).html(), $(building).html());
}

//THIS METHOD WILL ADD A FUNCTION TO THE LIST OF FUNCTIONS THAT AN OBJECT CAN PERFORM
function addFunction(){
    function_name = $("#function_name_section").children("input")[0];
    //JSON CONTAINING THE OBJECT INFORMATION IM TRYING TO ADD A FUNCTION FOR AND THE FUNCTION NAME
    var functionObjectData = {'object_id': $(object_id).html(), 'function_name': $(function_name).val()};

    var request = {
        type: 'POST',
        url: '/addFunctionForObject',
        contentType: 'application/json',
        data: JSON.stringify(functionObjectData),
        success: generateFunctionsOnSelect
    }
    $.ajax(request);
}

//THIS METHOD IS CALLED WHEN ONE HITS THE UPDATE BUTTON AND HITS SUBMIT
//ON SOME INFORMATION
function submitUpdate(){

    object_id_input = $("#object_id_section").children("input")[0];

    object_type_input = $("#object_type_section").children("input")[0];

    object_room_input = $("#object_room_section").children("input")[0];

    object_building_input = $("#object_building_section").children("input")[0];

    //DATA SENT TO /updateObject
    var form_info = {
        'object_id': $(object_id_input).val(),
        'object_type' : $(object_type_input).val(),
        'object_room': $(object_room_input).val(),
        'object_building': $(object_building_input).val()
    }

    var request = {
        type: 'PUT',
        url: '/updateObject',
        data: JSON.stringify(form_info),
        contentType: 'application/json',
        success: functionSuccess,
        error: error
    }
    $.ajax(request)

    //GENEERATE THE TABEL DATA AGAAIN AFTER UPDATING
    function functionSuccess(data){
        data= JSON.parse(data);
        generateTable(data);
    }

    function error(){
    }
}

//DELETE AN OBJECT GIVEN ITS O BJECT ID
function deleteObject(){
    row = $(this).parent().parent("tr");
    object_id = $(row).children("td")[0];
    var form_info = {
        'object_id': $(object_id).html()
    }
    var request = {
        type: 'DELETE',
        url: '/deleteObject',
        data: JSON.stringify(form_info),
        contentType: 'application/json',
        success: success,
        error: error
    };
    $.ajax(request);
    //GENERATE TABLE DATA AFTER DELETION
    function success(data){
        data = JSON.parse(data);
        generateTable(data)
    }

    function error(){
    }
}


//RENDER THE UPDATE WINDOW WITH THE CORRECT INFORMATION
function show_update_window(object_id, type, room, building){
    object_id_input = $("#object_id_section").children("input")[0];
    $(object_id_input).val(object_id);

    object_type_input = $("#object_type_section").children("input")[0];
    $(object_type_input).val(type);

    object_room_input = $("#object_room_section").children("input")[0];
    $(object_room_input).val(room);

    object_building_input = $("#object_building_section").children("input")[0];
    $(object_building_input).val(building);

}


    
