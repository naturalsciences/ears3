/**
 * Appends to the provided dropdown one provided row item  of a SPARQL JSON result of provided type entityType, if it is not yet added.
 * dropdown: the jQuery element for the dropdown
 * entityType: a string representing the entity (c=toolcategory, t=tool, p=process,a=action)
 * row: the row in the SPARQL result
 * listElements: an array to keep track of what has been added already
 */
function populateDropdownBasedOnPrevious(dropdown, entityType, item, listElements) {
    if (dropdown.attr('disabled') != 'disabled') {
        var url = [entityType + 'u']; //the url field in the SPARQL JSON result, for the right entity
        var label = [entityType + 'l']; //the label field in the SPARQL JSON result, for the right entity
        var matches = $.grep(listElements, function (e) {
            return item[url].value === e[url].value &&
                    item[label].value === e[label].value;
        });
        select_option_data = dropdown.html();
        if (matches.length === 0) { //item has not yet been added
            select_option_data += '<option value="' + item[url].value + '">' + item[label].value + '</option>'
            dropdown.html(select_option_data).selectpicker('refresh');
            listElements.push(item);
            //console.log("Adding "+item[url].value+" to dropdown "+dropdown.attr('id')+" with value: "+ dropdown.val());
        }
    }
}
/**
 * Autoselect a value in the given dropdown when there is only one choice: if there are only two options, one is the defualt 'Select a value', the other a true value.
 * dropdown: the jQuery element for the dropdown
 * lockElement: the jQuery element for the lock of this element
 * unlock: Elementthe jQuery element for the unlock of this element
 */
function autoselectDropdownWhenOnlyOneChoice(dropdown, lockElement, unlockElement, disableOnceSelected) {
    if (dropdown.attr('disabled') != 'disabled') {
        if (dropdown.children('option').length == 2) { //
            dropdown.html(dropdown.find('option').not(':empty()').first().attr('selected', true)).selectpicker('refresh');
            if (disableOnceSelected) {
                dropdown.prop('disabled', true);
                dropdown.selectpicker('refresh');
                lockElement.css('visibility', 'visible');
                unlockElement.css('visibility', 'hidden');
            }
            //console.log("autoselected dropdown "+dropdown.attr('id')+" value: "+ dropdown.val());
        }
    }
}

/***
 * Change a dropdown: repopulate it and others based on the ones currently selected/unlocked. set disableOnceSelected to true to lock it, false to keep it open.
 */
function dropdownChanged(dropdown, disableOnceSelected) {
    var lockElementId = dropdown.attr('id').split('_')[1] + "_lock";
    var lockElement = $("#" + lockElementId);
    var unlockElementId = dropdown.attr('id').split('_')[1] + "_unlock";
    var unlockElement = $("#" + unlockElementId);
    var cu = '';
    var tu = '';
    var pu = '';
    var au = '';

    if ($("#idSelect_tc").val() != 1 && $("#idSelect_tc").val() != null) { //if an actual value is selected here others need to be constrained by it and the condition changes
        cu = "element.cu.value  == " + "'" + $("#idSelect_tc").val() + "'" + "  && ";
    }
    if ($("#idSelect_t").val() != 1 && $("#idSelect_t").val() != null) {
        tu = "element.tu.value  == " + "'" + $("#idSelect_t").val() + "'" + "  && ";
    }
    if ($("#idSelect_p").val() != 1 && $("#idSelect_p").val() != null) {
        pu = "element.pu.value  == " + "'" + $("#idSelect_p").val() + "'" + "  && ";
    }
    if ($("#idSelect_a").val() != 1 && $("#idSelect_a").val() != null) {
        au = "element.au.value  == " + "'" + $("#idSelect_a").val() + "'" + "  && ";
    }

    let condition = "(".concat(cu, tu, pu, au, ")").replace(/&&([^'&&']*)$/, '' + '$1');
    $.getJSON(jsonVesselRdfLocation,
            function (data) {
                var tc = [];
                var t = [];
                var p = [];
                var a = [];
                var select_option_data = '<option value=1></option>';
                if ($('#idSelect_tc').attr('disabled') != 'disabled') {
                    $('#idSelect_tc').html(select_option_data);
                } //clear it when no choice is made
                if ($('#idSelect_t').attr('disabled') != 'disabled') {
                    $('#idSelect_t').html(select_option_data);
                }
                if ($('#idSelect_p').attr('disabled') != 'disabled') {
                    $('#idSelect_p').html(select_option_data);
                }
                if ($('#idSelect_a').attr('disabled') != 'disabled') {
                    $('#idSelect_a').html(select_option_data);
                }
                $(data.results.bindings).each(function (index, element) {
                    if (eval(condition)) {
                        populateDropdownBasedOnPrevious($('#idSelect_tc'), 'c', element, tc);
                        populateDropdownBasedOnPrevious($('#idSelect_t'), 't', element, t);
                        populateDropdownBasedOnPrevious($('#idSelect_p'), 'p', element, p);
                        populateDropdownBasedOnPrevious($('#idSelect_a'), 'a', element, a);
                    }
                });
                autoselectDropdownWhenOnlyOneChoice($('#idSelect_tc'), $('#tc_lock'), $('#tc_unlock'), disableOnceSelected);
                autoselectDropdownWhenOnlyOneChoice($('#idSelect_t'), $('#t_lock'), $('#t_unlock'), disableOnceSelected);
                autoselectDropdownWhenOnlyOneChoice($('#idSelect_p'), $('#p_lock'), $('#p_unlock'), disableOnceSelected);
                autoselectDropdownWhenOnlyOneChoice($('#idSelect_a'), $('#a_lock'), $('#a_unlock'), disableOnceSelected);
            }
    );
}

function initDropdowns(rdfBindings, selectedValues) {
    populateDropdownLists(rdfBindings, selectedValues);
    //on change dropdown list
    $("#idSelect_tc, #idSelect_t, #idSelect_p, #idSelect_a").change(function () {
        dropdownChanged($(this), true);
    });
    //if reset tc
    $("#cell_tc_unlock").click(function () {
        $.getScript("/ears3/js/unlock_tc.js");
    });

    $("#cell_t_unlock").click(function () {
        if (($('#idSelect_tc').attr('disabled') != 'disabled') && ($('#idSelect_p').attr('disabled') != 'disabled') && ($('#idSelect_a').attr('disabled') != 'disabled')) {
            $.getScript("js/unlock_tc.js"); //if all others are not disabled, ie are selectable, unlock everything.
        } else {
            $("#idSelect_t").val("1"); //clear the selection
            dropdownChanged($("#idSelect_t"), false);

            $('#idSelect_t').prop('disabled', false);
            $('#idSelect_t').selectpicker('refresh');
            $('#t_lock').css('vissibility', 'hidden');
            $('#t_unlock').css('visibility', 'visible');

            $('#idSelect_p').prop('disabled', false);
            $('#idSelect_p').selectpicker('refresh');
            $('#p_lock').css('visibility', 'hidden');
            $('#p_unlock').css('visibility', 'visible');

            $('#idSelect_a').prop('disabled', false);
            $('#idSelect_a').selectpicker('refresh');
            $('#a_lock').css('visibility', 'hidden');
            $('#a_unlock').css('visibility', 'visible');

            $('#id_eid').hide();
        }
    });
    $("#cell_p_unlock").click(function () {
        if (($('#idSelect_tc').attr('disabled') != 'disabled') && ($('#idSelect_t').attr('disabled') != 'disabled') && ($('#idSelect_a').attr('disabled') != 'disabled')) {
            $.getScript("js/unlock_tc.js");
        } else {
            $("#idSelect_p").val("1"); //clear the selection
            dropdownChanged($("#idSelect_p"), false);

            $('#idSelect_p').prop('disabled', false);
            $('#idSelect_p').selectpicker('refresh');
            $('#p_lock').css('visibility', 'hidden');
            $('#p_unlock').css('visibility', 'visible');

            $('#idSelect_a').prop('disabled', false);
            $('#idSelect_a').selectpicker('refresh');
            $('#a_lock').css('visibility', 'hidden');
            $('#a_unlock').css('visibility', 'visible');

            $('#id_eid').hide();
        }
    });
    $("#cell_a_unlock").click(function () {
        if (($('#idSelect_tc').attr('disabled') != 'disabled') && ($('#idSelect_t').attr('disabled') != 'disabled') && ($('#idSelect_p').attr('disabled') != 'disabled')) {
            $.getScript("js/unlock_tc.js");
        } else {
            $("#idSelect_a").val("1"); //clear the selection
            dropdownChanged($("#idSelect_a"), false);

            $('#idSelect_a').prop('disabled', false);
            $('#idSelect_a').selectpicker('refresh');
            $('#a_lock').css('visibility', 'hidden');
            $('#a_unlock').css('visibility', 'visible');

            $('#id_eid').hide();
        }
    });
    $('#dropdownForm').submit(function (e) {
        e.preventDefault();
        var eventSubmitDate = new Date(Date.now());
        var identifier = $('#dropdownForm').attr("data-identifier"); //in case of editing an event, this is set
        var date = $('#dateField').val(); //in case of editing an event, this is set
        var time = $('#timeField').val(); //in case of editing an event, this is set
        var timeZone = $('#timeZoneField').val(); //in case of editing an event, this is set
        if(date !== undefined && time !== undefined){
            var timeStamp=date+'T'+time+timeZone;
        }
        else{
            var timeStamp=null;
        }
        if (($("#idSelect_tc").val() != 1) && ($("#idSelect_t").val() != 1) && ($("#idSelect_p").val() != 1) && ($("#idSelect_a").val() != 1)) {
            var condition = '';
            var startCondition = "(";
            if ($("#idSelect_tc").val() != 1) {
                cu = "element.cu.value  == " + "'" + $("#idSelect_tc").val() + "'" + "  && ";
            }
            if ($("#idSelect_t").val() != 1) {
                tu = "element.tu.value  == " + "'" + $("#idSelect_t").val() + "'" + "  && ";
            }
            if ($("#idSelect_p").val() != 1) {
                pu = "element.pu.value  == " + "'" + $("#idSelect_p").val() + "'" + "  && ";
            }
            if ($("#idSelect_a").val() != 1) {
                au = "element.au.value  == " + "'" + $("#idSelect_a").val() + "'" + "  && ";
            }
            var endCondition = ")";
            condition = startCondition.concat(cu, tu, pu, au, endCondition).replace(/&&([^'&&']*)$/, '' + '$1');
            $.getJSON(jsonVesselRdfLocation,
                    function (data) {
                        var rdfBindings = data.results.bindings;
                        $(rdfBindings).each(function (index, element) {
                            if (eval(condition)) {
                                let event = new EarsEvent(element);
                                event.identifier = identifier;
                                event.timeStamp = timeStamp;
                                postEventInner(event, function () {
                                    $("#btnSubmitDropdownChoice").removeClass("btn-warning").addClass("btn-success");
                                    setTimeout(function () {
                                        $("#btnSubmitDropdownChoice").removeClass("btn-success");
                                    }, stayGreenForThisPeriod);
                                    $("#btnSubmitDropdownChoice").removeClass("btn-warning").addClass("btn-success");
                                    $("#collapseOne").addClass("show");
                                    addEVtoLocalStorage(event);
                                    populateAllScenarios(rdfBindings);
                                },
                                        function () {
                                            $("#btnSubmitDropdownChoice").removeClass("btn-success").addClass("btn-warning");
                                        });

                            }
                        });
                    }
            );
        } else {
            $("#btnSubmitDropdownChoice").removeClass("btn-success").addClass("btn-warning");
        }
    });

    var sortByString = function (a, b)
    {
        var one = a.label;
        var two = b.label;
        // a and b will here be two objects from the array
        // thus a[1] and b[1] will equal the names

        // if they are equal, return 0 (no sorting)
        if (one == two) {
            return 0;
        }
        if (one > two)
        {
            // if a should come after b, return 1
            return 1;
        } else
        {
            // if b should come after a, return -1
            return -1;
        }
    };

    function getElement(what, item) {
        switch (what) {
            case 'TC':
                return {url: item.cu.value, label: item.cl.value};
                break;
            case 'T':
                return {url: item.tu.value, label: item.tl.value};
                break;
            case 'P':
                return {url: item.pu.value, label: item.pl.value};
                break;
            case 'A':
                return {url: item.au.value, label: item.al.value};
                break;
            default:
                return null;
        }
    }

    function populateDropdownList(rdfBindings, entityName, dropdownId, selectedValue) {
        var ddmOptions = [];
        var select_option_data = '';
        $.each(rdfBindings, function (key, item) {
            val = getElement(entityName, item);
            var events = $.grep(ddmOptions, function (e) {
                return val.url === e.url &&
                        val.label === e.label;
            });
            if (events.length === 0) {
                ddmOptions.push(val);
            }
        });
        if (/Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent)) {
            $(dropdownId).selectpicker('mobile');
        }
        if (ddmOptions.length <= 6) {
            $(dropdownId).selectpicker({
                liveSearch: false
            }).selectpicker('refresh');
        }
        ddmOptions.sort(sortByString);
        var select_option_data = '';
        select_option_data += '<option value=1></option>'
        $.each(ddmOptions, function (key, unique) {
            select_option_data += '<option value="' + unique.url + '">' + unique.label + '</option>';
        });

        $(dropdownId).html(select_option_data).selectpicker('refresh');
        if (selectedValue !== null) {
            $(dropdownId).selectpicker('val', selectedValue);
            $(dropdownId).prop('disabled', true);
            $(dropdownId).selectpicker('refresh');
        }
    }

    function populateDropdownLists(rdfBindings, selectedValues) {
        if (selectedValues !== undefined && selectedValues !== null) {
            populateDropdownList(rdfBindings, "TC", "#idSelect_tc", selectedValues.tc);
            populateDropdownList(rdfBindings, "T", "#idSelect_t", selectedValues.t);
            populateDropdownList(rdfBindings, "P", "#idSelect_p", selectedValues.p);
            populateDropdownList(rdfBindings, "A", "#idSelect_a", selectedValues.a);

            $('#tc_unlock').css('visibility', 'hidden');
            $('#t_unlock').css('visibility', 'hidden');
            $('#p_unlock').css('visibility', 'hidden');
            $('#a_unlock').css('visibility', 'hidden');

            $('#tc_lock').css('visibility', 'visible');
            $('#t_lock').css('visibility', 'visible');
            $('#p_lock').css('visibility', 'visible');
            $('#a_lock').css('visibility', 'visible');
        } else {
            populateDropdownList(rdfBindings, "TC", "#idSelect_tc", null);
            populateDropdownList(rdfBindings, "T", "#idSelect_t", null);
            populateDropdownList(rdfBindings, "P", "#idSelect_p", null);
            populateDropdownList(rdfBindings, "A", "#idSelect_a", null);

            $('#tc_lock').css('visibility', 'hidden');
            $('#t_lock').css('visibility', 'hidden');
            $('#p_lock').css('visibility', 'hidden');
            $('#a_lock').css('visibility', 'hidden');
        }

    }






}