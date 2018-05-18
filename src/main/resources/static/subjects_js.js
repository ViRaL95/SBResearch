//WHEN THE DOM IS READY THIS METHOD IS CALLED (JQUERY METHOD)
$(document).ready(function(){
    //RETRIEVES ALL SUBJECTS INITIALLY AND LOADS THE TABLE
    retrieveSubjects();
    //CALL THE SUBMIT UPDATE METHOD WHEN SOMEONE IS TRYING TO UPDATE
    $("#update_button").click(submitUpdate);
    //THE CRATE BUTTON WILL RETRIEVE INFORMATION FROM A UI WHICH YOU ENTER INFO
    //SENDS IT TO /createSubject
    $("#create_button").click(function(){
        subject_id = $("#create_subject_id_section").children("input")[0];

        subject_type = $("#create_subject_type_section").children("input")[0];

        subject_first_name = $("#create_subject_first_name_section").children("input")[0];

        subject_last_name = $("#create_subject_last_name_section").children("input")[0];

        subject_email = $("#create_subject_email_section").children("input")[0];

        var create_info = {
            'subject_id': $(subject_id).val(),
            'subject_type': $(subject_type).val(),
            'first_name': $(subject_first_name).val(),
            'last_name': $(subject_last_name).val(),
            "email": $(subject_email).val()
        }
        var request = {
            type: 'POST',
            url: '/createSubject',
            data: JSON.stringify(create_info),
            contentType: 'application/json',
            success: successfulSubjectCreation,
            error: error
        }


        $.ajax(request);

        function successfulSubjectCreation(data){
            generateTable(JSON.parse(data));
        }

        function error(){
        }
    })

    function success(data){
    }
    function error(data){
    }
    //THIS METHOD BUILDS A STRUCTURE CONTAINING INFROMATION ABOUT
    //WHAT THE USER CHOSE IN ORDER TO BUILD A MYsql query in the backend
    $("#search_subject_button").click(function(){
        subject_type = $("#search_subject_type").val();
        subject_first_name = $("#search_first_name").val();
        subject_last_name = $("#search_last_name").val();

        search_info = [

        ]

        allEmpty = true;
        //CREATE A STRUCTURE FOR THE SUBJECT TYPE
        if (subject_type != ""){
            subject_type_structure = {"queryType": "subject_type", "type": "string", "value": subject_type};
            search_info.push(subject_type_structure);
            allEmpty = false
        }
        //CREATE A STRUCTURE FOR THE SUBJECT FIRST NAME
        if (subject_first_name != ""){
            first_name_structure = {"queryType":"first_name", "type": "string", "value": subject_first_name};
            search_info.push(first_name_structure);
            allEmpty = false;
        }
        if (subject_last_name != ""){
            last_name_structure = {"queryType": "last_name", "type": "string", "value": subject_last_name };
            search_info.push(last_name_structure);
            allEmpty = false;
        }
        //IF NO DATA WAS ENTERED THEN RETRIEVE ALL SUBJECTS IN DATABASE
        if (allEmpty){
            retrieveSubjects();
            return;
        }

        request = {
            type: 'POST',
            url: '/searchForSubject',
            data: JSON.stringify(search_info),
            contentType: 'application/json',
            success: success,
            error: error
        }
        //GENERATE TABLES ON SUCCESS
        function success(data){
            data = JSON.parse(data)
            generateTable(data);
        }

        function error(data){
        }
        $.ajax(request);

    })

})


function retrieveSubjects(){
    request = {
        type: 'GET',
        url: '/retrieve_subjects',
        success: success,
        error:error
    }
    $.ajax(request);
    function success(data){
        data = JSON.parse(data);
        generateTable(data);
    }

    function error(){
    }
}

//iterate through data and generate rows on table
function generateTable(data){
    $("table tbody").empty();
    for (index=0; index<data.length; index++){
        subject = data[index];
        subject_id = subject.subject_id;
        subject_type = subject.subject_type;
        first_name = subject.first_name;
        last_name = subject.last_name;
        email = subject.email;
        $("table tbody").append("<tr> </tr>");
        row = $("table tbody tr")[index];
        $(row).append("<td>" + subject_id + "</td>");
        $(row).append("<td>" + subject_type + "</td>");
        $(row).append("<td>" + first_name + "</td>");
        $(row).append("<td>" + last_name + "</td>");
        $(row).append("<td>" + email + "</td>");
        $(row).append("<td> <button class='glyphicon glyphicon-remove-sign btn btn-default delete' type='button'></button> </td>");
        $(row).append("<td><button class='glyphicon glyphicon-refresh update btn btn-default' type='button'  data-toggle='modal' data-target='#update_dialog'></button></td>");
    }
    $(".delete").click(deleteSubject);
    $(".update").click(updateButtonClicked);
}

//WHEN THE UPDATE BUTTON IS CLICKED SHOW AN UPDATE WINDOW
function updateButtonClicked (){
    row_index = $(this).parent("td").parent("tr").index();
    row = $("table tbody tr")[row_index];
    subject_id = $(row).children("td")[0];
    subject_type = $(row).children("td")[1];
    first_name = $(row).children("td")[2];
    last_name = $(row).children("td")[3];
    email = $(row).children("td")[4];
    show_update_window($(subject_id).html(), $(subject_type).html(), $(first_name).html(), $(last_name).html(), $(email).html());
}

//WHEN A SUBMIT BUTTON IS CLICKED FOR UPDATION
function submitUpdate(){

    subject_id_input = $("#subject_id_section").children("input")[0];

    subject_type_input = $("#subject_type_section").children("input")[0];

    subject_first_name_input = $("#subject_first_name_section").children("input")[0];

    subject_last_name_input = $("#subject_last_name_section").children("input")[0];

    subject_email_input = $("#subject_email_section").children("input")[0];


    var form_info = {
        'subject_id': $(subject_id_input).val(),
        'subject_type' : $(subject_type_input).val(),
        'first_name': $(subject_first_name_input).val(),
        'last_name': $(subject_last_name_input).val(),
        'email': $(subject_email_input).val()
    }

    //MAKE A SPECIAL PUT REQUEST FOR UPDATE
    var request = {
        type: 'PUT',
        url: '/updateSubject',
        data: JSON.stringify(form_info),
        contentType: 'application/json',
        success: functionSuccess,
        error: error
    }

    $.ajax(request)
    //GENDERATE TABLE DYNAMICALLY
    function functionSuccess(data){
        data= JSON.parse(data);
        generateTable(data);
    }

    function error(){
    }
}
//DELETE A SUBJECT BASED ON WHERE WAS CLICKED ON THE TABLE AND ITS SUBJECT ID
function deleteSubject(){
    row = $(this).parent().parent("tr");
    subject_id = $(row).children("td")[0];
    var form_info = {
        'subject_id': $(subject_id).html()
    }
    var request = {
        type: 'DELETE',
        url: '/deleteSubject',
        data: JSON.stringify(form_info),
        contentType: 'application/json',
        success: success,
        error: error
    };
    $.ajax(request);

    function success(data){
        data = JSON.parse(data);
        generateTable(data)
    }

    function error(){
    }
}


//SHOW THE UPDATE WINDOW
function show_update_window(subject_id, subject_type, first_name, last_name, email){
    subject_id_input = $("#subject_id_section").children("input")[0];
    $(subject_id_input).val(subject_id);

    subject_type_input = $("#subject_type_section").children("input")[0];
    $(subject_type_input).val(subject_type);

    first_name_input = $("#subject_first_name_section").children("input")[0];
    $(first_name_input).val(first_name);

    last_name_input = $("#subject_last_name_section").children("input")[0];
    $(last_name_input).val(last_name);

    email_input = $("#subject_email_section").children("input")[0];
    $(email_input).val(email);
}



