$(document).ready(function(){
    $("button").click(function(){
        if(!form_validation_failed()){
            form_info = {
                "email": $("#email").val(),
                "password": $("#password").val()
            }
            request = {
                type: 'POST',
                url: '/',
                data: JSON.stringify(form_info),
                contentType: 'application/json',
                success: success
            }
            console.log("ajax");
            $.ajax(request);
        }
        else{
            $(".warning").html("<h4 style='color:red'> Please enter all values </h4>");
        }
    })
});

function success(data){
    console.log(data);
    if (data == "true"){
        window.location = "/dashboard"
    }
    else{
        $(".warning").html("<h5 style='color:red'> User/email combination does not exist </h5>");
    }
    //convert to some sort of error code instead of failed (more consistent)
}

function form_validation_failed(){
    failed = false;
    console.log($("#email").val());
    if ($("#email").val() == "" || $("#password").val() == ""){
        failed = true;
    }
    console.log(failed);
    return failed;
}
