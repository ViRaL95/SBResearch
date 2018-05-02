$(document).ready(function(){
    $("#object_content").hide(); 
    $("#subjects_search_bar").keyup(search_for_subject);
    $("#objects_search_bar").keyup(search_for_object);
    generate_content(data);
    $("table thead tr th").click(generate_object_information);
    $("table tbody td").click(generate_relationship_information);

    $("#searchButton").click(enableDisplayForPageNumberBox);
});


function generate_object_information(){
    $("#object_content").show();
    current_object_index = $("th").index($(this)) - 1;
    console.log(current_object_index);
    console.log("data before is ", data);
    data_json = JSON.parse(data);
    objects = data_json.objects;
    object = objects[current_object_index];
    object_id = object.object_id;
    console.log(object);
    room_content = $(this).data("object-room");
    object_type_content =$(this).data("object-type");
    object_building_content = $(this).data("object-building");
    $("#building span").html(object_building_content);
    $("#room span").html(room_content);
    $("#type span").html(object_type_content); 
    $("#delete").click(function(current_object_index){
        data = {
            "object_id": object_id
        }
        request = {
            type: 'DELETE',
            url: '/delete_info',
            data: JSON.stringify(data),
            success: success,
            error: error
        }
        $.ajax(request);
        function success(data){
            console.log("yesssss");
            $("table tbody").empty();
            $("table thead"). empty();
            generate_content(data);
        }
        
        function error(){
            console.log("error");
        }
    });
}


function create_update_delete(){
    
    request = {
        type: 'GET',
        url: '/retrieve_info',
        success: generate_content
    }
    $.ajax(request);
}

function generate_content(data){
    data = JSON.parse(data);
    generate_column_headers(data.objects);
    generate_row_headers(data.subjects);
    generate_relationship(data.subjects, data.objects);
    generate_colors_on_palette();
    fill_empty_cells();
}

function enableDisplayForPageNumberBox(){
    $("#pageNumbersBox").show();
}


function generate_column_headers(objects){
    column_header_content = "";
    for (index=0; index<objects.length; index++){
        object = objects[index];
        object_type = object.object_type;
        object_id = object.object_id;
        building = object.building;
        room = object.room_number;
        color = object.color;
        column_header_content += "<th style='background-color:"+color+"'id='object_id_"+ object_id +"'data-object-room='"+room +"'data-object-type='"+object_type+"' data-object-building='"+building+"'>" + object_id + " </th>";
    }
    blank_head = "<th>&nbsp</th>";
    column_header_content = "<tr>"+ blank_head + column_header_content + "</tr>";
    $("table thead").append(column_header_content);
}

function generate_row_headers(subjects){
    row_header_content = "";
    for (index=0; index< subjects.length; index++){
        subject = subjects[index];
        subject_type = subject.subject_type;
        subject_id = subject.subject_id;
        full_name = subject.first_name + " " + subject.last_name;
        email = subject.email;
        row_header_content += "<tr> <td id='subject_id_"+ subject_id +"'class='subject' data-subject-type='"+subject_type+"'data-subject-name='"+ full_name +"'>" + full_name + "</td> </tr>";
    }
    $("table tbody").append(row_header_content);
}

function generate_relationship(subjects, real_objects){
    console.log(subjects);
    for (subject_index=0; subject_index<subjects.length; subject_index++){
        subject = subjects[subject_index];
        subject_id = "subject_id_" + subject.subject_id;
        $("table tbody tr").each(function(index){
            if ($(this).children("td").attr("id") == subject_id){
                console.log("entered");
                return true;
            }
            else{
            console.log("md");
            }
        })
        objects = subject.objects;
        for (object_index=0; object_index<objects.length; object_index++){
            object = objects[object_index];
            console.log(object);
            object_id = "object_id_" + object.object_id;
            console.log(object_id);
            functions = object.functions;
            console.log(functions);

            console.log("entered");
            $("table thead tr th").each(function(){
                if ($(this).attr("id") == object_id){
                    console.log("found it");
                    return true;
                }
            })
            for(real_objects_index=0; real_objects_index<real_objects.length; real_objects_index++){
                real_object = real_objects[real_objects_index];
                console.log("real object id is ",real_object.object_id);
                console.log("object id is ", object_id);
                if (real_object.object_id == object.object_id){
                    functions_length = real_object.functions.length;
                }
            }
            if (functions.length == 0){
                color_code = '#F67280';
            }
            else if (functions.length < functions_length){
                color_code = '#C06C84';
            }
            else if (functions.length == functions_length){
                color_code = '#6C5B7B';
            }
            end_index = object_id.split("_")[2];
            for (index=0; index<end_index; index++){
                current_td = $($("table tbody tr")[subject_index]).children("td")[index];
                if (! current_td){
                    not_accessible_color_code = "#F67280";
                    not_accessible_content = "<td style='background-color:"+ not_accessible_color_code +"'></td>";
                    $($("table tbody tr")[subject_index]).append(not_accessible_content);     
                }
            }
            element_content = "<td style='background-color:"+ color_code +"'></td>";
            $($("table tbody tr")[subject_index]).append(element_content);
        }
    }
}


function generate_colors_on_palette(){
    colors = {"#F67280": "No Access", "#C06C84": "Partial Access", "#6C5B7B": "Complete Access"};
    for (var color in colors){
        if (colors.hasOwnProperty(color)){
            meaning = colors[color];
            color_content = "<span class='color' style=background-color:"+ color +"></span>";
            color_significance = "<span class='color_significance'>"+meaning+"</span>";
            palette_content = "<div>" + color_content + color_significance + "</div>"
            $("#palette_of_colors").append(palette_content);
        }
    }
}

function fill_empty_cells(){
    table_length = $("table thead tr th").length;
    console.log(table_length);
    $("table tbody tr").each(function(){
        for(cell_index=0; cell_index<table_length; cell_index++){
            cell = $(this).children("td")[cell_index];
            if(!cell){
                not_accessible_color_code = "#F67280";
                not_accessible_content = "<td style='background-color:"+ not_accessible_color_code +"'></td>";
                $(this).append(not_accessible_content);
            }
        }
    })
}


function search_for_subject(){
    var subject_name = $("#subjects_search_bar").val().toLowerCase();
    console.log
    console.log("entered keypress for subject search bar");
    if (subject_name == ""){
        console.log("empty string");
        reconstruct_rows();
    }
    $("table tbody tr").each(function(){
        subject_val = $($(this).children("td")[0]).data("subject-name").toLowerCase();
        if (!subject_val.match(subject_name)){
            $(this).hide();
        }
        else{
            $(this).show();
        }
    })
}


function reconstruct_rows(){
    $("table tbody tr").each(function(){
        if ($(this).is(":hidden")){
            $(this).show();
        }
    })
}

function reconstruct_columns(){
    console.log("true");
    $("table thead tr th").each(function(index){
        visibility = $(this).css("visibility");
        if(visibility == "hidden"){
            $(this).css("visibility", "visible");
        }
    })
    $("table tbody tr td").each(function(row_index){
        visibility = $(this).css("visibility");
        console.log(visibility);
        console.log($(this));
        console.log("--------------");
        if(visibility == "hidden"){
            console.log("true");
            $(this).css("visibility", "visible");
        }
    })
}

function search_for_object(){
    var object_name = $("#objects_search_bar").val();
    object_index = 0;
    if (object_name == ""){
        reconstruct_columns();
        return;
    }
    $("table thead tr th").each(function(index){
        object_val = $(this).html();
        object_val  = object_val.replace(/\s/g, '');
        if (object_val.match(object_name)){
            object_index = index;
        }
        else if(index!=0){
            $(this).css("visibility", "hidden");
        }
    });
    $("table tbody tr").each(function(){
        $(this).children("td").each(function(index){
            if (index != object_index && index != 0){
                $(this).css("visibility", "hidden");
            }
            else{
                $(this).css("visibility", "visible");
            }
        })
    })
}


