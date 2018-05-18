/**
 * This method changes the current uri. Depending on where this
 * window.location is set a different Java method in IOTController
 * is called
 */

$(document).ready(function(){
    //MAKE THE URI /subjects
    $("#subjects").click(function(){
        window.location = '/subjects';
    });
    //MAKE THE URI /objects
    $("#objects").click(function(){
        window.location = '/objects';
    });
    //MAKE THE URI /functions
    $("#relationship").click(function(){
        window.location = '/functions'
    })
})
