function generateObjectHeaders(){
    //GENERATE HEADER
    //$("table thead").append("<tr> <th scope='col'> object id </th> <th scope='col'> building </th> <th scope='col'> room </th> ");
}

function generateTableData(arrayData, subject_id){
    arrayData = JSON.parse(arrayData);
    for (i=0; i<arrayData.length; i++){
        var box = "<td style='background-color: red'>  </td>"
        var innerDiv = "<div style='width:50px'> </div>"
        $("table tbody tr").append($(box).append(innerDiv));
        dataJson = arrayData[i];
        building = dataJson.building;
        room = dataJson.room;
        object_type = dataJson.object_type;
        object_id = dataJson.object_id;

        retrieveAllFunctionsForAGivenObject(subject_id, object_id, building, room, object_type);
        // $("table tbody").append(cellInfo);
        // $("table tbody").append("<tr><td> "+ object_id + "</td> <td>"+building + "</td> <td>"+ room +"</td> </tr>");


    }
}

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

        function success(data) {
            data = JSON.parse(data);
            addFunctionsToFunctionBox(data);
            retrieveFunctionsForSubjectObject(object_id, subject_id);
            enableFunctionBox();


            function error() {

            }
        }
    })
}

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

    function retrieveFunctionsSuccess(data){
        data = JSON.parse(data);
        for (i=0; i<data.length; i++){

            $("#functions > option").each(function(){
                var yah = this.value == data[i].function_name;
                if (this.value.trim() == data[i].function_name){
                    $(this).css("background-color", "white");
                    return true;
                }
                else if (this.value.trim() != data[i].function_name && this.value !="-- select a function --"){
                    $(this).css("background-color", "gray");
                }

            })

        }
    }

    $("select").change(function(){
        var selectedValue = $(this).val();
        var data = {"object_id": object_id, "subject_id": subject_id, "function": selectedValue};
        var request = {
            type: 'POST',
            url: '/checkIfFunctionIsAvailable',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: success
        }

        function success(data){
            data = JSON.parse(data);
            var function_exists = data.function_exists;
            if (function_exists ){
                $("#enable").prop("disabled", true);
                $("#disable").prop("disabled", false);
                var parameters = data.parameters;
            }
            else{
                $("#enable").prop("disabled", false);
                $("#disable").prop("disabled", true);
            }
        }
        $.ajax(request);
        retrieveParameters(object_id, subject_id, selectedValue);

    })

}


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
    function success(data) {
        parameters = JSON.parse(data);
        for (i = 0; i < parameters.length; i++) {
            var parameterOption = "<option>" + parameters[i].parameter_name + " </option>";
            $("#parameterBox").append(parameterOption);
        }
    }
    $.ajax(request);
}


function addFunctionsToFunctionBox(arraydata){
    for(i=0; i<arraydata.length; i++){
        var functionOption = "<option>" + arraydata[i].function_name + " </option>";
        $("#functions").append(functionOption);
    }
}

function enableFunctionBox(){
    $("#functionBox").show();
}

$(document).ready(function(){
    $("#search_button").click(function(){
        if ($("#object_id").val().trim() == "" && ($("#subject_id").val().trim() == "")){
            console.log("both object id and subject id are missing");
        }
        else if ($("#object_id").val().trim() == "" && $("#subject_id").val().trim() != ""){
            var data = {
                'subject_id': $("#subject_id").val()
            }
            var request = {
                type: 'POST',
                url: '/retrieveObjectsForSubject',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: success
            }

            function success(data){
                console.log(data);
                //generateObjectHeaders()f;
                generateTableData(data, $("#subject_id").val());
            }
            $.ajax(request);

        }


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
                console.log(data);
                generateTableData(data);
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