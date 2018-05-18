/**
 * This mthod creates the table with objects. It recieves all  objects
 *
 * @param arrayData
 * @param subject_id
 */
function generateTableData(arrayData){
    arrayData = JSON.parse(arrayData);
    for (i=0; i<arrayData.length; i++){
        dataJson = arrayData[i];
        var box = "<td>" + dataJson.object_id + "</td>";
        var innerDiv = "<div style='width:50px'> </div>";
        $("table tbody tr").append($(box).append(innerDiv));
        building = dataJson.building;
        room = dataJson.room;
        subject_id = $("#subject_id").val();
        object_type = dataJson.object_type;
        object_id = dataJson.object_id;

        retrieveAllFunctionsForAGivenObject(subject_id, object_id, building, room, object_type);
    }
}

/**
 * Thisi method makes an AJAX calls and retrieves All Functions For A Given Object.
 * @param subject_id
 * @param object_id
 * @param building
 * @param room
 * @param object_type
 */
function retrieveAllFunctionsForAGivenObject(subject_id, object_id, building, room, object_type){

    $("table tbody td").last().click(function() {
        var objectIdData = {'object_id': object_id};

        var request = {
            type: 'POST',
            url: '/retrieveFunctionsForObject',
            contentType: 'application/json',
            data: JSON.stringify(objectIdData),
            success: success
        }

        $.ajax(request);
        //ON SUCCESFULLY RETRIEVING ALL FUNCTIONS ALL FUNCTIONS ARE ADDED TO A FUNCTIONS BOX
        function success(data) {
            $("#functions").empty();
            data = JSON.parse(data);
            addFunctionsToFunctionBox(data);
            retrieveFunctionsForSubjectObject(object_id, subject_id);
        }
    })
}

/**
 * This method retrieve Functions For A given Subject and Object
 * @param object_id
 * @param subject_id
 */
function retrieveFunctionsForSubjectObject(object_id, subject_id){
    var subjectObjectData = {"object_id": object_id, "subject_id": subject_id};

    var request = {
        type: 'POST',
        url: '/retrieveFunctionsForSubjectAndObject',
        contentType: 'application/json',
        data: JSON.stringify(subjectObjectData),
        success: retrieveFunctionsSuccess
    }

    $.ajax(request);
    //THIS FUNCTIONS WILL rednder all functions
    function  retrieveFunctionsSuccess(data) {
        data = JSON.parse(data);
        func_array = []
        functions_ = $("#functions option").each(function(){
            func_array.push($(this).val())
        })
        for (i = 0; i < data.length; i++) {
            function_name = data[i].function_name;
            select_no = 0;
            for (j = 0; j < func_array.length; j++){
                if (j == i){
                    continue;
                }
                if (function_name == func_array[j]){
                    $($("#functions option")[select_no]).css("background-color", "white");
                    index = func_array.indexOf(func_array[j]);
                    func_array.splice(index, 1);
                    break;
                }
                else{
                    $($("#functions option")[select_no]).css("background-color", "gray");
                }
                select_no++;
            }
        }
        $("#functionBox").show();
    }

    /**
     * This method is called when the functions select options is changed
     * It will check if the Function Is Available
     */
    $("#functions").change(function(){
        var selectedValue = $(this).val();
        var data = {"object_id": object_id, "subject_id": subject_id, "function": selectedValue};
        //A BACKEND CHECK IS MADE TO CHECK IF THE FUNCTION IS AVAILABLE FOR A  GIVEN USER
        var request = {
            type: 'POST',
            url: '/checkIfFunctionIsAvailable',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: successfulFunctionCheck
        }
        //THIS SHOULD ENABLE A GIVEN FUNCTION FOR SUBJECT AND OBJECT
        $("#enable").click(function(){
            var data = {"object_id": object_id, "subject_id": subject_id, "function_name": selectedValue};
            var request = {
                type: 'POST',
                url: '/enableFunctionForSubjectObject',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: success
            }
            $.ajax(request);
            function success(data){
                data = JSON.parse(data);
                if (data.success){


                }
            }
        })
        //DISABLE A FUNCTIONF OR A GIVEN SUBJECT OBJECT
        $("#disable").click(function(){

        })

        /*
        DEPENDING ON WHETHER THE FUNCTION WAS AVAILABLE OR NOT 'DISABLE' OR 'ENABLE'
        the enable button
        */
        function successfulFunctionCheck(data){
            data = JSON.parse(data);
            var function_exists = data.function_exists;
            //THE FUNCTION EXISTS SO ENABLE THE DISABLE BUTTON DISABLE THE ENABLE BUTTON
            if (function_exists ){
                $("#enable").prop("disabled", true);
                $("#disable").prop("disabled", false);
                var parameters = data.parameters;
            }
            //THE FUNCTION EXISTS SO DISABLE THE DISABLE  BUTTON AND ENABLE THE DISABLE BUTTON
            else{
                $("#enable").prop("disabled", false);
                $("#disable").prop("disabled", true);
            }
        }
        $.ajax(request);
        retrieveParameters(object_id, subject_id, selectedValue);
    })

}

/**
 * This method retrieves all parameters for a given subject, object and function name.
 * @param object_id
 * @param subject_id
 * @param function_name
 */
function retrieveParameters(object_id, subject_id, function_name){
    $("#parameterBox").find("option").remove();
    data = {"object_id": object_id, "subject_id": subject_id, "function": function_name};
    var request = {
        type: 'POST',
        url: '/retrieveParametersForFunction',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: success
    }
    //ITERATE THROUGH PARAMETERS AND RENDER IT ON UI
    function success(data) {
        parameters = JSON.parse(data);
        for (i = 0; i < parameters.length; i++) {
            var parameterOption = "<option>" + parameters[i].parameter_name + " </option>";
            $("#parameterBox").append(parameterOption);
        }
    }
    $.ajax(request);
}


/**
 * This method adds an array of functions to a select
 * @param arraydata
 */
function addFunctionsToFunctionBox(arraydata){
    $("#parameterBox").empty();
    for(i=0; i<arraydata.length; i++){
        var functionOption = "<option>" + arraydata[i].function_name + " </option>";
        $("#functions").append(functionOption);
    }
    $("#functionBox").show();
}

$(document).ready(function(){
    //THIS METHOD IS CALLED WHEN A SEARCH OPRATAION WANTS TO BE FORMED
    $("#search_button").click(function(){
        //A SUBJECT ID IS NOT GIVEN BUT AN OBJECT ID IS GIVEN
        if ($("#object_id").val().trim() == "" && $("#subject_id").val().trim() != ""){
            var subjectInformation = {
                'subject_id': $("#subject_id").val()
            }
            var request = {
                type: 'POST',
                url: '/retrieveObjectsForSubject',
                contentType: 'application/json',
                data: JSON.stringify(subjectInformation),
                success: success
            }

            function success(data){
                generateTableData(data);
            }
            $.ajax(request);

        }

        //THIS METHOD IS CALLED WHEN THE OBJECT ID IS PROVIDED BUT THE SUBJECT ID IS EMPTY
        else if (!$("#object_id").val().trim() == "" && $("#subject_id").val().trim() == ""){
            var data = {
                'object_id': $("#object_id").val()
            }
            var request = {
                type: 'POST',
                url: '/retrieveSubjectsForObject',
                data: JSON.stringify(data),
                success: success(data)
            }
            $.ajax(request)
            function success(data){
                data.object_id = $("#object_id").val();
            }
        }
        else{
            var data = {
                "object_id": $("#object_id").val()
            }
            var request = {
                type: 'POST',
                url: '/retrieveSubjectsForObject',
                data: JSON.stringify(data)
            }
            $.ajax(request)
        }
    })
})
