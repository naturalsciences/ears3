// Recent-event "scenario" buttons UI, plus page bootstrap.
// Depends on ears-events-common.js being loaded first (objectKeyFromButton,
// localStorageEventDefsToMap, removeEVfromLocalStorage, etc.)

function forgetButton(recentEventButtonX) {
    const button = recentEventButtonX.parentNode.parentNode;
    button.disabled = true;
    let eventKeyToBeDeleted = objectKeyFromButton(button);
    removeEVfromLocalStorage(eventKeyToBeDeleted);
    button.removeAttribute("onclick");
    button.remove();
    populateAllScenarios();
}

function populateScenarioButton(eventDefinitionId, rdfBindings) {
    $(rdfBindings).each(function (index, element) {
        if (rdfBindingElementHasEid(element, eventDefinitionId)) {
            var lastButtonCell = $("#recentEventDefinitionTable td#cell" + $('#recentEventDefinitionTable button').length);
            lastButtonCell.append("<button data-tool=" + element.tu.value + " id=" + element.eid.value + " data-process=" + element.pu.value + " data-action=" + element.au.value + " type='button' onclick='postEvent(this);' class='btn btn-default text-left'>" +
                "<p class='close-btn'><a href='#' onclick='forgetButton(this);'>x</a></p><p>" + element.cl.value + "</p><p><strong>" + element.tl.value + "</strong></p><p>" + element.pl.value + "<br>" + element.al.value + "</p></button>");
        }
    });
}

function populateAllScenarios(rdfBindings) {
    let map = localStorageEventDefsToMap(); //eventdefinitionid (eid): event
    if (map !== null) {
        if (rdfBindings == null) {
            rdfBindings = getBindings(false); //asynchronous because synchronous messes up the order of the buttons
        }
        $("#recentEventDefinitionTable td").empty(); //remove all buttons
        for (let [key, value] of map) {
            populateScenarioButton(key, rdfBindings);
        }
    }
}

$(document).ready(function () {
    $('#id_collapse_in').css('visibility', 'hidden');
    $('#id_collapse_out').css('visibility', 'visible');
    $(".collapse").on('show.bs.collapse', function () {
        $('#id_collapse_in').css('visibility', 'visible');
        $('#id_collapse_out').css('visibility', 'hidden');
    });
    $(".collapse").on('hide.bs.collapse', function () {
        $('#id_collapse_in').css('visibility', 'hidden');
        $('#id_collapse_out').css('visibility', 'visible');
    });

    var rdfBindings = getBindings(false); //asynchronous because synchronous messes up the order of the buttons
    populateAllScenarios(rdfBindings);

    initDropdowns(rdfBindings, null);

    $("#propertyPopup").dialog({
        autoOpen: false,
        modal: true,
        close: function () {
            $(this).find("input").empty(); //clear everything if we close it otherwise values are kept
            $(document).off('click', '#btnSubmitEventWithProperties'); //clear it else previous events are readded each time
        }
    });
});
