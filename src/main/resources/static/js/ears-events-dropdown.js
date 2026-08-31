const ENTITIES = [
    {key: 'tc', prefixLetter: 'c', bindingKey: 'TC', selectId: '#idSelect_tc', lockId: '#tc_lock', unlockId: '#tc_unlock'},
    {key: 't', prefixLetter: 't', bindingKey: 'T', selectId: '#idSelect_t', lockId: '#t_lock', unlockId: '#t_unlock'},
    {key: 'p', prefixLetter: 'p', bindingKey: 'P', selectId: '#idSelect_p', lockId: '#p_lock', unlockId: '#p_unlock'},
    {key: 'a', prefixLetter: 'a', bindingKey: 'A', selectId: '#idSelect_a', lockId: '#a_lock', unlockId: '#a_unlock'}
];

/**
 * Appends to the provided dropdown one provided row item of a SPARQL JSON result of provided type entityType, if it is not yet added.
 * dropdown: the jQuery element for the dropdown
 * entityType: a string representing the entity (c=toolcategory, t=tool, p=process,a=action)
 * item: the row in the SPARQL result
 * listElements: an array to keep track of what has been added already
 */
function populateDropdownBasedOnPrevious(dropdown, entityType, item, listElements) {
    if (dropdown.attr('disabled') !== 'disabled') {
        const urlField = entityType + 'u'; //the url field in the SPARQL JSON result, for the right entity
        const labelField = entityType + 'l'; //the label field in the SPARQL JSON result, for the right entity

        listElements.sort(function (a, b) {
            return a[labelField].value.localeCompare(b[labelField].value);
        });

        var matches = $.grep(listElements, function (e) {
            return item[urlField].value === e[urlField].value &&
                    item[labelField].value === e[labelField].value;
        });

        if (matches.length === 0) { //item has not yet been added
            const selectOptionData = dropdown.html() +
                    '<option value="' + item[urlField].value + '">' + item[labelField].value + '</option>';
            dropdown.html(selectOptionData).selectpicker('refresh');
            listElements.push(item);
        }
    }
}

function populateDropdownList(rdfBindings, entityName, dropdownId, selectedValue) {
    selectedValue = stripSlash(selectedValue);
    let ddmOptions = [];

    $.each(rdfBindings, function (key, item) {
        let val = getElement(entityName, item);
        let existing = $.grep(ddmOptions, function (e) {
            return (val.url === e.url && val.label === e.label) || (val.transitiveUrl === e.url && val.label === e.label);
        });
        if (existing.length === 0) {
            ddmOptions.push(val);
        }
    });

    if (/Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent)) {
        $(dropdownId).selectpicker('mobile');
    }
    if (ddmOptions.length <= 6) {
        $(dropdownId).selectpicker({liveSearch: false}).selectpicker('refresh');
    }

    ddmOptions.sort(function (a, b) {
        return a.label.localeCompare(b.label);
    });

    let selectOptionData = '<option value=1></option>';
    $.each(ddmOptions, function (key, unique) {
        if (unique.url === selectedValue) {
            selectOptionData += '<option value="' + unique.url + '">' + unique.label + '</option>';
        } else if (unique.transitiveUrl !== null && unique.transitiveUrl === selectedValue) {
            selectOptionData += '<option value="' + unique.transitiveUrl + '">' + unique.label + '</option>';
        } else {
            selectOptionData += '<option value="' + unique.url + '">' + unique.label + '</option>';
        }
    });

    $(dropdownId).html(selectOptionData).selectpicker('refresh');
    if (selectedValue !== null) {
        $(dropdownId).selectpicker('val', selectedValue);
        $(dropdownId).prop('disabled', true);
        $(dropdownId).selectpicker('refresh');
    }
}

/**
 * Autoselect a value in the given dropdown when there is only one choice: if there are only two options, one is the default 'Select a value', the other a true value.
 * dropdown: the jQuery element for the dropdown
 * lockElement: the jQuery element for the lock of this element
 * unlockElement: the jQuery element for the unlock of this element
 */
function autoselectDropdownWhenOnlyOneChoice(dropdown, lockElement, unlockElement, disableOnceSelected) {
    if (dropdown.attr('disabled') !== 'disabled') {
        if (dropdown.children('option').length === 2) {
            dropdown.html(dropdown.find('option').not(':empty()').first().attr('selected', true)).selectpicker('refresh');
            if (disableOnceSelected) {
                dropdown.prop('disabled', true);
                dropdown.selectpicker('refresh');
                lockElement.css('visibility', 'visible');
                unlockElement.css('visibility', 'hidden');
            }
        }
    }
}

/**
 * Reads the currently selected value (if any) for every entity. A value of
 * '1' or null means "nothing actually selected" and is normalized to null.
 */
function getSelectedValues() {
    let selected = {};
    ENTITIES.forEach(function (entity) {
        const val = $(entity.selectId).val();
        selected[entity.key] = (val !== '1' && val != null) ? val : null;
    });
    return selected;
}

/**
 * True if the given SPARQL binding row is compatible with the currently
 * selected values - i.e. for every entity that has an actual selection, the
 * row's url or transitiveUrl matches it. Entities with no selection impose no
 * constraint.
 */
function rowMatchesSelection(bindings, selectedValues) {
    return ENTITIES.every(function (entity) {
        let selected = selectedValues[entity.key];
        if (selected === null) {
            return true;
        }
        let b = bindings[entity.key];
        return b.url === selected || b.transitiveUrl === selected;
    });
}

function getBindingsForRow(element) {
    let bindings = {};
    ENTITIES.forEach(function (entity) {
        bindings[entity.key] = getElement(entity.bindingKey, element);
    });
    return bindings;
}

/**
 * Change a dropdown: repopulate it and others based on the ones currently selected/unlocked. set disableOnceSelected to true to lock it, false to keep it open.
 */
function dropdownChanged(dropdown, disableOnceSelected) {
    const lockElementId = dropdown.attr('id').split('_')[1] + "_lock";
    const lockElement = $("#" + lockElementId);
    const unlockElementId = dropdown.attr('id').split('_')[1] + "_unlock";
    const unlockElement = $("#" + unlockElementId);

    let selectedValues = getSelectedValues();
    const emptyOption = '<option value=1></option>';
    let accumulated = {};

    ENTITIES.forEach(function (entity) {
        accumulated[entity.key] = [];
        if ($(entity.selectId).attr('disabled') != 'disabled') {
            $(entity.selectId).html(emptyOption); //clear it when no choice is made
        }
    });

    let rdfBindings = getBindings(false);
    $(rdfBindings).each(function (index, element) {
        var bindings = getBindingsForRow(element);
        if (rowMatchesSelection(bindings, selectedValues)) {
            ENTITIES.forEach(function (entity) {
                populateDropdownBasedOnPrevious($(entity.selectId), entity.prefixLetter, element, accumulated[entity.key]);
            });
        }
    });

    ENTITIES.forEach(function (entity) {
        autoselectDropdownWhenOnlyOneChoice($(entity.selectId), $(entity.lockId), $(entity.unlockId), disableOnceSelected);
    });
}

function unlockTc(rdfBindings) {
    ENTITIES.forEach(function (entity) {
        populateDropdownList(rdfBindings, entity.bindingKey, entity.selectId, null);
        $(entity.selectId).prop('disabled', false);
        $(entity.selectId).selectpicker('refresh');
        $(entity.lockId).css('visibility', 'hidden');
        $(entity.unlockId).css('visibility', 'visible');
    });
    $('#id_form_process').hide();
}

/**
 * Handles clicking the unlock icon for a non-root entity (t, p, or a).
 * If every other entity is already unlocked, this is equivalent to a full
 * reset (unlockTc). Otherwise, this entity is reset and re-enabled, and
 * every entity downstream of it (later in the ENTITIES list) is unlocked too,
 * since their valid options depend on this one.
 */
function handleDependentUnlockClick(entity, rdfBindings) {
    let others = ENTITIES.filter(function (e) {
        return e.key !== entity.key;
    });
    let allOthersEnabled = others.every(function (e) {
        return $(e.selectId).attr('disabled') != 'disabled';
    });

    if (allOthersEnabled) {
        unlockTc(rdfBindings);
        return;
    }

    $(entity.selectId).val("1"); //clear the selection
    $(entity.selectId).prop('disabled', false);
    dropdownChanged($(entity.selectId), false);
    $(entity.selectId).selectpicker('refresh');
    $(entity.lockId).css('visibility', 'hidden');
    $(entity.unlockId).css('visibility', 'visible');

    const downstream = ENTITIES.slice(ENTITIES.indexOf(entity) + 1);
    downstream.forEach(function (e) {
        $(e.selectId).prop('disabled', false);
        $(e.selectId).selectpicker('refresh');
        $(e.lockId).css('visibility', 'hidden');
        $(e.unlockId).css('visibility', 'visible');
    });

    $('#id_eid').hide();
}

function initDropdowns(rdfBindings, selectedValues, onEventSubmitted) {
    populateDropdownLists(rdfBindings, selectedValues);

    $("#idSelect_tc, #idSelect_t, #idSelect_p, #idSelect_a").change(function () {
        dropdownChanged($(this), true);
    });

    $("#cell_tc_unlock").click(function () {
        unlockTc(rdfBindings);
    });
    $("#cell_t_unlock").click(function () {
        handleDependentUnlockClick(ENTITIES[1], rdfBindings);
    });
    $("#cell_p_unlock").click(function () {
        handleDependentUnlockClick(ENTITIES[2], rdfBindings);
    });
    $("#cell_a_unlock").click(function () {
        handleDependentUnlockClick(ENTITIES[3], rdfBindings);
    });

    $('#dropdownForm').submit(function (e) {
        e.preventDefault();

        const identifier = $('#dropdownForm').attr("data-identifier"); //in case of editing an event, this is set
        const date = $('#dateField').val(); //in case of editing an event, this is set
        const time = $('#timeField').val(); //in case of editing an event, this is set
        //const timeZone = $('#timeZoneField').val(); //in case of editing an event, this is set
        const timeStamp = (date !== undefined && time !== undefined) ? (date + 'T' + time + 'Z') : null;

        let allSelected = ENTITIES.every(function (entity) {
            return $(entity.selectId).val() !== '1';
        });

        if (!allSelected) {
            $("#btnSubmitDropdownChoice").removeClass("btn-success").addClass("btn-warning");
            toggleErrorMessage("Please select a category, tool, process and action.");
            return;
        }

        let selectedValues = {};
        ENTITIES.forEach(function (entity) {
            selectedValues[entity.key] = $(entity.selectId).val();
        });

        let rdfBindings = getBindings(false);
        $(rdfBindings).each(function (index, element) {
            let bindings = getBindingsForRow(element);

            let matches = ENTITIES.every(function (entity) {
                var b = bindings[entity.key];
                return b.url === selectedValues[entity.key] || b.transitiveUrl === selectedValues[entity.key];
            });

            if (matches) {
                let event = new EarsEvent(element);
                event.identifier = identifier;
                event.timeStamp = timeStamp;
                postEventInner(event, function () {
                    $("#btnSubmitDropdownChoice").removeClass("btn-warning").addClass("btn-success");
                    setTimeout(function () {
                        $("#btnSubmitDropdownChoice").removeClass("btn-success");
                    }, stayGreenForThisPeriod);
                    $("#collapseOne").addClass("show");
                    addEVtoLocalStorage(event);
                    populateAllScenarios(rdfBindings);
                    unlockTc(rdfBindings); // reset tc/t/p/a so the next event starts from a clean slate
                    if (typeof onEventSubmitted === 'function') {
                        onEventSubmitted(event);
                    }
                }, function () {
                    $("#btnSubmitDropdownChoice").removeClass("btn-success").addClass("btn-warning");
                });
                return false; // stop $.each, we found our match
            }
        });
    });
}

/***
 * Given one row of a JSON RDF SPARQL response binding, provide the right values, based on whether it is a TC, T, P or A. Return it as an array of value, url pairs.
 * @param {type} what
 * @param {type} item
 * @returns {getElement.ears-events-dropdownAnonym$2|getElement.ears-events-dropdownAnonym$3|getElement.ears-events-dropdownAnonym$1|getElement.ears-events-dropdownAnonym$4}
 */
function getElement(what, item) {
    switch (what) {
        case 'TC':
            return {url: stripSlash(item.cu.value), transitiveUrl: stripSlash(item.ctu.value), label: item.cl.value};
        case 'T':
            return {url: stripSlash(item.tu.value), transitiveUrl: stripSlash(item.ttu.value), label: item.tl.value};
        case 'P':
            return {url: stripSlash(item.pu.value), transitiveUrl: null, label: item.pl.value};
        case 'A':
            return {url: stripSlash(item.au.value), transitiveUrl: null, label: item.al.value};
        default:
            return null;
    }
}

function populateDropdownLists(rdfBindings, selectedValues) {
    const hasSelection = selectedValues !== undefined && selectedValues !== null;

    ENTITIES.forEach(function (entity) {
        const value = hasSelection ? selectedValues[entity.key] : null;
        populateDropdownList(rdfBindings, entity.bindingKey, entity.selectId, value);
        $(entity.lockId).css('visibility', hasSelection ? 'visible' : 'hidden');
    });

    // unlock icons are only touched (hidden) when
    // there IS a selection; with no selection their visibility is left as-is.
    if (hasSelection) {
        ENTITIES.forEach(function (entity) {
            $(entity.unlockId).css('visibility', 'hidden');
        });
    }
}
