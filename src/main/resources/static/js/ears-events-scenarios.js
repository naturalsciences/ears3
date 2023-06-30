function objectKeyFromEvent(event) {
    let key = {
        eid: event.eventDefinitionId, 
        tool: event.tool.tool.identifier,
        process: event.process.identifier,
        action: event.action.identifier,
    }
    return JSON.stringify(key)
}

function objectKeyFromButton(button) {
    let key = {
        eid: button.id, 
        tool: button.getAttribute('data-tool'),
        process: button.getAttribute('data-process'),
        action: button.getAttribute('data-action'),
    }
    return JSON.stringify(key)
}

function objectKeyFromElement(element) {
    let key = {
        eid: element.eid.value, 
        tool: element.tu.value,
        process: element.pu.value,
        action: element.au.value,
    }
    return JSON.stringify(key)
}

function compareObjectKeyWithElement(objectKey, element) {
    let objectKeyJson = JSON.parse(objectKey)
    if (objectKeyJson.id == element.id) {
        return objectKey == objectKeyFromElement(element)
    } else {
        return false
    }
}

function addEVtoLocalStorage(event) {
    let map = localStorageEventDefsToMap(); //maps preserve insertion order and have unique entries by default
    size = map.size;
    map.set(objectKeyFromEvent(event), event);
    if (map.size != size) { //addition happened, meaning it is new
        localStorage.eventDefinitions = JSON.stringify(Array.from(map.entries()));
    }
}

/*
 * map.delete(object) does not work as intended as the key is an object and the internal method checks for object equality (reference equality). It can't check individual fields.
 */
function deleteObjectKeyFromMap(map, objectKey) {

    map.forEach((value, key) => {
        console.log(value, key);
        // if (key.eid == objectKey.eid && key.tool == objectKey.tool) {
        if (key == objectKey) {
            delete map[key];
            map.delete(key);
        }
    })
}

function removeEVfromLocalStorage(objectKey) {
    let map = localStorageEventDefsToMap(); //maps preserve insertion order and have unique entries by default
    //map.delete(eventDefinitionId);
    deleteObjectKeyFromMap(map, objectKey);
    localStorage.eventDefinitions = JSON.stringify(Array.from(map.entries()));
}

function localStorageEventDefsToMap() {
    eventDefs = localStorage.eventDefinitions;

    if (isJSON(eventDefs)) {
        let map = new Map(JSON.parse(eventDefs));
        return map;
    } else {
        return new Map();
    }
}

var recentlyDeletedEventId;

function forgetButton(recentEventButtonX) {
    const button = recentEventButtonX.parentNode.parentNode;
    button.disabled = true;
    //EventIdToBeDeleted = button.id;
    EventKeyToBeDeleted = objectKeyFromButton(button);
    //EventIdToBeDeleted = { eid: button.id, tool: button.getAttribute('data-tool') };
    removeEVfromLocalStorage(EventKeyToBeDeleted);
    button.removeAttribute("onclick");
    button.remove();
    populateAllScenarios();
}


function populateScenarioButton(eventDefinitionId, rdfBindings) {
    $(rdfBindings).each(function (index, element) {
        if (rdfBindingElementHasEid(element, eventDefinitionId)) {
            var lastButtonCell = $("#recentEventDefinitionTable td#cell" + $('#recentEventDefinitionTable button').length);
            lastButtonCell.append("<button data-tool=" + element.tu.value + " id=" + element.eid.value + " data-process=" + element.pu.value + " data-action=" + element.au.value  + " type='button' onclick='postEvent(this);' class='btn btn-default text-left'>" +
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

if (window.localStorage && typeof window.localStorage !== 'undefined') {
    $(document).ready(function () {
        if (localStorage.actor == null) {
            window.location.href = '/ears3/settings';
        } else {
            var me = JSON.parse(localStorage.actor);
            $("#settings-link").html(me.firstName + " " + me.lastName);
        }
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
        $('#id_eid').hide();

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

} else {
    alert("Sorry, your browser does not support local storage.");
}