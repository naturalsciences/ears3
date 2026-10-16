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
                "<p class='drag-handle'><a href='#' title='Drag to move this scenario to another cell' onclick='event.stopPropagation();'>&#9776;</a></p>" +
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
        enableScenarioReordering();
    }
}

/*
 * Reordering. A button sits in the cell matching its position in the local
 * storage map, so dragging one button onto another cell is a matter of
 * swapping their entries in that map (trading places) and repainting the
 * whole table.
 */

function cellIndexOf(cell) {
    return parseInt(cell.id.replace("cell", ""), 10);
}

function dropScenarioButtonInCell(button, cell) {
    let targetIndex = cellIndexOf(cell);
    if (isNaN(targetIndex)) {
        return;
    }
    if (swapEVinLocalStorage(objectKeyFromButton(button), targetIndex)) {
        //repaint once jquery-ui is done with the dragged button, else it
        //cleans up an element we already replaced
        setTimeout(populateAllScenarios, 0);
    }
}

/**
 * Makes every scenario button draggable by its handle and every cell of the
 * table a drop target. Called after each repaint, as the buttons are
 * recreated from scratch every time; the cells themselves survive, so they
 * only get their droppable once.
 */
function enableScenarioReordering() {
    $("#recentEventDefinitionTable button").draggable({
        handle: ".drag-handle",
        cancel: "", //draggable's default cancels on button/input/select/option, which would veto dragging the button itself
        helper: function () {
            return $(this).clone().removeAttr("id").width($(this).width());
        },
        appendTo: "body", //else the helper is clipped by the cell
        revert: "invalid",
        cursor: "move",
        zIndex: 1000
    });

    let cells = $("#recentEventDefinitionTable td.scenario-cell");
    if (!cells.hasClass("ui-droppable")) {
        cells.droppable({
            accept: "#recentEventDefinitionTable button",
            hoverClass: "scenario-cell-hover",
            tolerance: "pointer",
            drop: function (event, ui) {
                dropScenarioButtonInCell(ui.draggable.get(0), this);
            }
        });
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
