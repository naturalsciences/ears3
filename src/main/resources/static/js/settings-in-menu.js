function initSelectpicker(selector) {
    $(document).ready(function() {
    $(selector).val($(selector).attr('value'));
    $(selector).selectpicker('refresh');
})
}

if (window.localStorage && typeof window.localStorage !== 'undefined') {
    $(document).ready(function () {
        if (localStorage.actor == null) {
            window.location.href = '/ears3/settings';
        } else {
            var me = JSON.parse(localStorage.actor);
            $("#settings-link").html(me.firstName + " " + me.lastName);
        }
    });
} else {
    alert("Sorry, your browser does not support local storage.");
}