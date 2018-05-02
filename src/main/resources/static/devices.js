$(document).ready(function(){
    #("#device_info").hide();
    $("#edit").click(function()[
        id_number = $(this).siblings("#id");
        make_request(id_number);
        $("#device_info").show();
    });
});
