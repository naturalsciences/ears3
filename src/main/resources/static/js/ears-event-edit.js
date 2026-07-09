function initSelectpicker(selector) {
    $(document).ready(function () {
        $(selector).val($(selector).attr('value'));
        $(selector).selectpicker('refresh');
    })
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

    initDropdowns(rdfBindings, selectedValues);
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
});
