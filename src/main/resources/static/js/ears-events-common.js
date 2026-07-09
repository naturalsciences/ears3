// Shared utilities and event-key / local-storage helpers used by
// ears-events-api.js, ears-events-dropdown.js, and ears-events-scenarios.js.

const earsProgramRegex = buildYearWindowRegex();
const earsCruiseRegex = buildYearWindowRegex();

$(document).ready(function () {
    populateProgramField(earsProgramRegex);
    populateCruiseField(earsCruiseRegex);
});

function stripSlash(url) {
    if (url === null) {
        return null;
    }
    return url.endsWith('/') ?
        url.slice(0, -1) :
        url;
}

function isJSON(str) {
    try {
        return (JSON.parse(str) && !!str);
    } catch (e) {
        return false;
    }
}

function toggleErrorMessage(message) {
    if (!message) {
        $('#serverBadFeedbackBox').css('visibility', 'hidden').text("");
    } else {
        $('#serverBadFeedbackBox').css('visibility', 'visible').text(message);
    }
}

function objectKeyFromEvent(event) {
    let key = {
        eid: event.eventDefinitionId,
        tool: event.tool.tool.identifier,
        process: event.process.identifier,
        action: event.action.identifier
    };
    return JSON.stringify(key);
}

function objectKeyFromButton(button) {
    let key = {
        eid: button.id,
        tool: button.getAttribute('data-tool'),
        process: button.getAttribute('data-process'),
        action: button.getAttribute('data-action')
    };
    return JSON.stringify(key);
}

function objectKeyFromElement(element) {
    let key = {
        eid: element.eid.value,
        tool: element.tu.value,
        process: element.pu.value,
        action: element.au.value
    };
    return JSON.stringify(key);
}

function compareObjectKeyWithElement(objectKey, element) {
    return objectKey === objectKeyFromElement(element);
}

var recentlyDeletedEventId;

/*
 * map.delete(object) does not work as intended as the key is an object and the internal method checks for object equality (reference equality). It can't check individual fields.
 */
function deleteObjectKeyFromMap(map, objectKey) {
    map.forEach((value, key) => {
        if (key === objectKey) {
            map.delete(key);
        }
    });
}

function localStorageEventDefsToMap() {
    let eventDefs = localStorage.eventDefinitions;

    if (isJSON(eventDefs)) {
        return new Map(JSON.parse(eventDefs));
    } else {
        return new Map();
    }
}

function addEVtoLocalStorage(event) {
    let map = localStorageEventDefsToMap(); //maps preserve insertion order and have unique entries by default
    let size = map.size;
    map.set(objectKeyFromEvent(event), event);
    if (map.size !== size) { //addition happened, meaning it is new
        localStorage.eventDefinitions = JSON.stringify(Array.from(map.entries()));
    }
}

function removeEVfromLocalStorage(objectKey) {
    let map = localStorageEventDefsToMap(); //maps preserve insertion order and have unique entries by default
    deleteObjectKeyFromMap(map, objectKey);
    localStorage.eventDefinitions = JSON.stringify(Array.from(map.entries()));
}

/**
 * Populates #programField with the programs matching the given regex
 * (typically injected server-side, e.g. from the ears.program.regex
 * environment property - the regex itself must stay in the Thymeleaf
 * template since it needs server-side templating; only the call is here).
 * Call once from $(document).ready(...) on any page that includes the
 * program field.
 */
function populateProgramField(regex) {
    populateFilteredSelectField('#programField', programsGetLocation, 'programs', regex, function (item) {
        let names = "";
        if (item.principalInvestigators && item.principalInvestigators.length > 0) {
            names = " (" + item.principalInvestigators.map(function (pi) {
                return pi.firstName + " " + pi.lastName;
            }).join("/") + ")";
        }
        return item.identifier + names;
    });
}

let cruiseLookup = {};

function populateCruiseField(regex) {
    populateFilteredSelectField('#cruiseField', cruisesGetLocation, 'cruises', regex, function (item) {
        cruiseLookup[item.identifier] = item; // keep the real object around
        return item.identifier; // adjust once you know what label cruises actually need
    });
}


/**
 * Populates a <select> element (rendered via bootstrap-select) with options
 * built from a JSON list endpoint, keeping only items whose identifier
 * matches the given regex.
 *
 * selectId:     jQuery selector for the <select>, e.g. '#programField'
 * jsonLocation: URL returning JSON of the shape { [dataKey]: [ {identifier, ...}, ... ] }
 * dataKey:      the property on the JSON response holding the array of items
 * regex:        tested against each item's identifier; non-matching items are skipped
 * labelBuilder: optional function(item) -> string for the option's display text.
 *               Defaults to the identifier alone if omitted.
 */
function populateFilteredSelectField(selectId, jsonLocation, dataKey, regex, labelBuilder) {
    if ($(selectId).length === 0) {
        return; // this page doesn't have this field - nothing to do
    }
    $.getJSON(jsonLocation, function (data) {
        $.each(data[dataKey], function (key, item) {
            if (!regex || regex.test(item.identifier)) {
                const label = labelBuilder ? labelBuilder(item) : item.identifier;
                $(selectId).append($("<option />").val(item.identifier).text(label));
            }
        });
        $(selectId).selectpicker('refresh');
    });
}


/**
 * Builds a regex matching any of (year-1, year, year+1) around the given
 * date (defaults to now). Uses a compact character class - e.g. "202[5-7]" -
 * when all three years share the same prefix, which is the common case.
 * Falls back to a plain alternation - e.g. "(2028|2029|2030)" - at
 * decade/century boundaries, where the years don't share a prefix and the
 * compact form isn't valid.
 */
function buildYearWindowRegex(baseDate) {
    const year = (baseDate || new Date()).getFullYear();
    const years = [year - 1, year, year + 1].map(String);

    let prefix = '';
    for (let i = 0; i < years[0].length; i++) {
        const ch = years[0][i];
        if (years.every(y => y[i] === ch)) {
            prefix += ch;
        } else {
            break;
        }
    }

    const suffixes = years.map(y => y.slice(prefix.length));
    const isCompressibleRun = suffixes.every(s => s.length === 1) &&
        Number(suffixes[1]) === Number(suffixes[0]) + 1 &&
        Number(suffixes[2]) === Number(suffixes[1]) + 1;

    if (prefix.length > 0 && isCompressibleRun) {
        return new RegExp(prefix + '[' + suffixes[0] + '-' + suffixes[2] + ']');
    }

    return new RegExp('(' + years.join('|') + ')');
}



