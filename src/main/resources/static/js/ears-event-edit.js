function initSelectpicker(selector) {
    $(document).ready(function () {
        $(selector).val($(selector).attr('value'));
        $(selector).selectpicker('refresh');
    })
}

function updateLocalTimeField() {
    const dateVal = $('#dateField').val();
    const timeVal = $('#timeField').val();

    if (!dateVal || !timeVal) {
        $('#localTimeField').val('');
        return;
    }

    // Build a proper UTC timestamp string and parse it
    const utcDate = new Date(dateVal + 'T' + timeVal + 'Z');

    if (isNaN(utcDate.getTime())) {
        $('#localTimeField').val('');
        return;
    }

    const formatter = new Intl.DateTimeFormat('en-GB', {
        timeZone: 'Europe/Brussels',
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
    });

    const parts = formatter.formatToParts(utcDate).reduce(function (acc, part) {
        acc[part.type] = part.value;
        return acc;
    }, {});

    const localString = parts.year + '-' + parts.month + '-' + parts.day + ' ' +
        parts.hour + ':' + parts.minute + ':' + parts.second;

    $('#localTimeField').val(localString);
}

$(document).ready(function () {
    const rdfBindings = getBindings(false); //asynchronous because synchronous messes up the order of the buttons
    const toolCategory = $('#idSelect_tc').attr('value'); //comes from thymeleaf
    const tool = $('#idSelect_t').attr('value');
    const process = $('#idSelect_p').attr('value');
    const action = $('#idSelect_a').attr('value');

    const selectedValues = {
        tc: toolCategory,
        t: tool,
        p: process,
        a: action
    };

    initDropdowns(rdfBindings, selectedValues, function () {
        window.location.href = '/ears3/events'; //after successfully submitting, go back to the main page.
    });
    initSelectpicker('#programField');
    initSelectpicker('#stationField');
    initSelectpicker('#labelField');

    $("#propertyPopup").dialog({
        autoOpen: false,
        modal: true,
        close: function () {
            $(this).find("input").empty(); //clear everything if we close it otherwise values are kept
            $(document).off('click', '#btnSubmitEventWithProperties'); //clear it else previous events are readded each time
        }
    });

    $('#deleteEvent').click(function () {
        const actor = JSON.parse(localStorage.actor);
        const actorFullName = (actor.firstName + " " + actor.lastName).trim();

        // Adjust this to however `actor` is actually shaped on currentEvent —
        // e.g. currentEvent.actor.firstName + " " + currentEvent.actor.lastName,
        // or currentEvent.actorEmail === actor.email if that's more reliable.
        const eventActorName = (currentEvent.actor.firstName + " " + currentEvent.actor.lastName).trim();

        if (eventActorName !== actorFullName) {
            alert("You can only delete your own events. The owner is " + eventActorName + ". Please don't mess with other persons' events.");
            return;
        }

        const confirmMsg = eventActorName + ', are you sure you want to delete this event "' +
            currentEvent.tool.term.name + '-' + currentEvent.process.name + '-' + currentEvent.action.name +
            ' at ' + currentEvent.timeStamp + '"?';

        if (confirm(confirmMsg)) {
            deleteEvent(currentEvent.identifier)
                .done(function () {
                    window.location.href = '/ears3/events';
                })
                .fail(function (jqXHR) {
                    alert('Delete failed (' + jqXHR.status + '): ' + (jqXHR.responseText || 'unknown error'));
                });
        }
    });


    $("#dropdownForm").validate({
        rules: {
            date: {
                required: true,
                pattern: /\d{4}\-\d{2}\-\d{2}/
            }
        },
        messages: {
            date: {
                required: 'Please provide a date',
                pattern: 'The date format is invalid'
            }
        },
        ignore: "",
        errorClass: 'fieldError',
        onkeyup: true,
        onblur: true,
        errorElement: 'label',
        submitHandler: function () {
            //alert("alert");
        }
    });

    updateLocalTimeField();
    $('#dateField, #timeField').on('change keyup', updateLocalTimeField);

});
