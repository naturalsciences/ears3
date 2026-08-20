// Shared utilities and event-key / local-storage helpers used by
// ears-events-api.js, ears-events-dropdown.js, and ears-events-scenarios.js.

$(document).ready(function () {
    populateProgramField();
    populateCruiseField();
    populateActorField();
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
 * Populates #programField with programs matching earsProgramRegex (a rolling
 * current-year +-1 window). Pass an explicit regex to override, e.g. for
 * testing. Call once from $(document).ready(...) on any page that includes
 * the program field.
 */
/**
 * True if the given actor's full name matches one of the program's PIs.
 */
function isPrincipalInvestigatorOf(program, actorFullName) {
    if (!actorFullName || !program.principalInvestigators || program.principalInvestigators.length === 0) {
        return false;
    }
    return program.principalInvestigators.some(function (pi) {
        return (pi.firstName + " " + pi.lastName) === actorFullName;
    });
}

/**
 * Sorts programs where the given actor is a PI to the front, alphabetically
 * by identifier within each group (mine, then everyone else's).
 */
function buildProgramSort(actorFullName) {
    return function (a, b) {
        const aIsMine = isPrincipalInvestigatorOf(a, actorFullName);
        const bIsMine = isPrincipalInvestigatorOf(b, actorFullName);
        if (aIsMine !== bIsMine) {
            return aIsMine ? -1 : 1;
        }
        return a.identifier.localeCompare(b.identifier);
    };
}

/**
 * Populates #programField with programs matching earsProgramRegex (a rolling
 * current-year +-1 window), with the current actor's own programs sorted to
 * the top. Pass an explicit regex to override, e.g. for testing. Call once
 * from $(document).ready(...) on any page that includes the program field.
 */
function populateProgramField() {
    const me = getCurrentActor()
    const actorFullName = me ? (me.firstName + " " + me.lastName) : null;

    const earsProgramRegex = buildYearWindowRegex();
    populateFilteredSelectField('#programField', programsGetLocation, 'programs', earsProgramRegex, function (item) {
        let names = "";
        if (item.principalInvestigators && item.principalInvestigators.length > 0) {
            names = " (" + item.principalInvestigators.map(function (pi) {
                return pi.firstName + " " + pi.lastName;
            }).join("/") + ")";
        }
        return item.identifier + names;
    }, buildProgramSort(actorFullName))
        .done(function () {
            const $field = $('#programField');
            $field.val($field.attr('value'));
            $field.selectpicker('refresh');
        });
}


let cruiseLookup = {};

function populateCruiseField(regex) {
    const earsCruiseRegex = buildYearWindowRegex();

    populateFilteredSelectField('#cruiseField', cruisesGetLocation, 'cruises', earsCruiseRegex, function (item) {
        cruiseLookup[item.identifier] = item; // keep the real object around
        return item.identifier; // adjust once you know what label cruises actually need
    });
}

let actorLookup = {};

function populateActorField(regex) {
    populateFilteredSelectField('#actorField', personsGetLocation, 'persons', null, function (item) {
        actorLookup[item.firstName + " " + item.lastName] = item;
        return item.firstName + " " + item.lastName;
    });
}


/**
 * Populates a <select> element (rendered via bootstrap-select) with options
 * built from a JSON list endpoint, keeping only items whose identifier
 * matches the given regex, sorted according to sortFn.
 *
 * selectId:     jQuery selector for the <select>, e.g. '#programField'
 * jsonLocation: URL returning JSON of the shape { [dataKey]: [ {identifier, ...}, ... ] }
 * dataKey:      the property on the JSON response holding the array of items
 * regex:        tested against each item's identifier; non-matching items are skipped.
 *               Pass null/undefined to skip filtering entirely.
 * labelBuilder: optional function(item) -> string for the option's display text.
 *               Defaults to the identifier alone if omitted.
 * sortFn:       optional Array.prototype.sort comparator, e.g. function(a, b) {...}.
 *               Defaults to alphabetical by identifier.
 */
function populateFilteredSelectField(selectId, jsonLocation, dataKey, regex, labelBuilder, sortFn) {
    if ($(selectId).length === 0) {
        return; // this page doesn't have this field - nothing to do
    }
    return $.getJSON(jsonLocation, function (data) {
        if (!regex) {
            regex = /.*/
        }
        const items = $.grep(data[dataKey], function (item) {
            return regex.test(item.identifier);
        });

        items.sort(sortFn || defaultIdentifierSort);

        $.each(items, function (key, item) {
            const label = labelBuilder ? labelBuilder(item) : item.identifier;
            $(selectId).append($("<option />").val(item.identifier).text(label));
        });
        $(selectId).selectpicker('refresh');
    });
}


function defaultIdentifierSort(a, b) {
    if (a.identifier && b.identifier) {
        return a.identifier.localeCompare(b.identifier);
    }
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

function getCurrentActor() {
    if (localStorage.actor && typeof localStorage.actor !== 'undefined' && localStorage.actor !== 'undefined') {
        return JSON.parse(localStorage.actor);
    } else return null;
}

function setCurrentActor(actor) {
    if (actor && typeof actor !== 'undefined' && actor !== 'undefined') {
        localStorage.actor = JSON.stringify(actor);
    }
}

