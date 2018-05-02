$(document).ready(function(){
    retrieveObjects();

    $("#create_button").click(function(){
        object_id = $("#create_object_id_section").children("input")[0];

        object_type = $("#create_object_type_section").children("input")[0];

        object_room_input = $("#create_object_room_section").children("input")[0];

        object_building_input = $("#create_object_building_section").children("input")[0];

        var create_info = {
            'object_id': $(object_id).val(),
            'object_type': $(object_type).val(),
            'object_room': $(object_room_input).val(),
            'object_building': $(object_building_input).val()
        }
        console.log(create_info);

        var request = {
            type: 'POST',
            url: '/createObject',
            data: JSON.stringify(create_info),
            contentType: 'application/json',
            success: successfulObjectCreation,
            error: error
        }


        $.ajax(request);

        function successfulObjectCreation(data){
            generateTable(JSON.parse(data))
            console.log("correct");
        }

        function error(){
            console.log("errror");
        }
    })

    function success(data){
        console.log(data);
    }
    function error(data){
        console.log(data);
    }

    $("#search_object_button").click(function(){
        object_type = $("#search_object_type").val();
        object_room = $("#search_object_room").val();
        object_building = $("#search_object_building").val();

        console.log(object_type);
        console.log(object_room);
        console.log(object_building);
        console.log(object_room);

        search_info = [

        ]

        allEmpty = true;
        if (object_type != ""){
            object_type_structure = {"queryType": "object_type", "type": "string", "value": object_type};
            search_info.push(object_type_structure);
            allEmpty = false
        }
        if (object_room != ""){
            object_room_structure = {"queryType":"room", "type": "string", "value": object_room};
            search_info.push(object_room_structure);
            allEmpty = false;
        }
        if (object_building != ""){
            object_building_structure = {"queryType": "building", "type": "string", "value": object_building };
            search_info.push(object_building_structure);
            allEmpty = false;
        }
        if (allEmpty){
            retrieveObjects();
            return;
        }

        console.log(search_info);
        console.log(JSON.stringify(search_info));
        request = {
            type: 'POST',
            url: '/search',
            data: JSON.stringify(search_info),
            contentType: 'application/json',
            success: success,
            error: error
        }

        function success(data){
            console.log(data);
            data = JSON.parse(data)
            generateTable(data);
        }

        function error(data){
            console.log(data);
        }
        $.ajax(request);

    })

})



function fillUpdateWindow(){

}


function retrieveObjects(){
    request = {
        type: 'GET',
        url: '/retrieve_objects',
        success: success,
        error:error
    }
    $.ajax(request);
    function success(data){
        data = JSON.parse(data);
        generateTable(data);
    }

    function error(){
        console.log("error");
    }
}


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
        $(row).append("<td> <button class='glyphicon glyphicon-remove-sign btn btn-default delete' type='button'></button> </td>");
        $(row).append("<td><button class='glyphicon glyphicon-refresh update btn btn-default' type='button'  data-toggle='modal' data-target='#update_dialog'></button></td>");
        $(".delete").click(deleteObject);
        $(".update").click(update)
    }
}

function update (){
    console.log("heeee");
    row_index = $(this).parent("td").parent("tr").index();
    row = $("table tbody tr")[row_index];
    object_id = $(row).children("td")[0];
    type = $(row).children("td")[1];
    room = $(row).children("td")[2];
    building = $(row).children("td")[3];
    show_update_window($(object_id).html(), $(type).html(), $(room).html(), $(building).html());
    $("#update_button").click(function(){

        object_id_input = $("#object_id_section").children("input")[0];

        object_type_input = $("#object_type_section").children("input")[0];

        object_room_input = $("#object_room_section").children("input")[0];

        object_building_input = $("#object_building_section").children("input")[0];

        var form_info = {
            'object_id': $(object_id_input).val(),
            'object_type' : $(object_type_input).val(),
            'object_room': $(object_room_input).val(),
            'object_building': $(object_building_input).val()
        }
        console.log(JSON.stringify(form_info));
        var request = {
            type: 'PUT',
            url: '/updateObject',
            data: JSON.stringify(form_info),
            contentType: 'application/json',
            success: success,
            error: error
        }
        console.log(request);
        $.ajax(request)

        function success(data){
            console.log("yeah");
            data= JSON.parse(data);
            console.log(data);
            generateTable(data);
        }

        function error(){
            console.log("Error");
        }


    })

}

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

    function success(data){
        console.log("this time around hehehee");
        data = JSON.parse(data);
        generateTable(data)
    }

    function error(){
        console.log("ahauhaha");
    }
}



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


    
