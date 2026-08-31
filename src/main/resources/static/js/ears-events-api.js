const eventPostLocation = "/ears3/api/event";

const conceptHierarchySPARQL = `PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
PREFIX ears2:<http://ontologies.ef-ears.eu/ears2/1#>
PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>
SELECT DISTINCT (replace(replace(str(?e),".+?(gev?_)","ears:gev::","i"),".+?(sev?_)","ears:sev::","i") as ?eid)  (str(?c) as ?cu) (str(?cc) as ?ctu) ?cl (str(?t)  as ?tu) (str(?tc) as ?ttu) ?tl (str(?p) as ?pu) ?pl (str(?a) as ?au) ?al
WHERE {
  {
    OPTIONAL {
      ?c a ears2:ToolCategory.
      ?t a ears2:Tool.
      ?t ears2:isMemberOf ?c.
      ?e ears2:hasProcess ?p.
      ?e ears2:hasAction ?a. 
      {?e ears2:withTool ?t.} UNION {?e ears2:withTool ?c.}

      ?c ears2:asConcept ?cc.
      ?cc skos:prefLabel ?cl .
      
      ?t ears2:asConcept ?tc.
      ?tc skos:prefLabel ?tl_ .
      
      OPTIONAL {
        ?t ears2:toolIdentifier ?ti .
        BIND(CONCAT(": ", ?ti) AS ?ti_) .
      }
      ?p ears2:asConcept ?pc.
      ?pc skos:prefLabel ?pl .
      ?a ears2:asConcept ?ac.
      ?ac skos:prefLabel ?al.
      ?ac ears2:status ?as .
      
      BIND(COALESCE(?ti_, "") AS ?ti__) .
      BIND(CONCAT(?tl_, ?ti__) AS ?tl) .
    }
  }
  FILTER (!REGEX( ?al, "^New Action" ) && str(?as) != 'Deprecated' )
}
ORDER BY DESC(?eid) ?pl ?al`;

const eventPropertySPARQL = `PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX dc: <http://purl.org/dc/elements/1.1/> 
PREFIX skos:<http://www.w3.org/2004/02/skos/core#> 
PREFIX ears2:<http://ontologies.ef-ears.eu/ears2/1#> 
PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> 
SELECT DISTINCT (replace(replace(str(?e),".+?(gev?_)","ears:gev::","i"),".+?(sev?_)","ears:sev::","i") as ?eid) (str(?pr) as ?pru) ?prl ?mult
WHERE { 
  { 
    OPTIONAL { 
      ?e ears2:hasProperty ?pr .
      ?c a ears2:ToolCategory.
      ?t a ears2:Tool.
      ?t ears2:isMemberOf ?c.
      ?e ears2:hasProcess ?p.
      ?e ears2:hasAction ?a. 
      {?e ears2:withTool ?t.} UNION {?e ears2:withTool ?c.}

      ?pr ears2:asConcept ?prc. 
      ?prc skos:prefLabel ?prl. 
      ?pr ears2:multiple ?mult }
  }
} 
ORDER BY DESC(?eid)`;

const jsonVesselRdfLocation = "/ears3/api/ontology/sparql?q=" + encodeURIComponent(conceptHierarchySPARQL);
const eventPropertyRdfLocation = "/ears3/api/ontology/sparql?q=" + encodeURIComponent(eventPropertySPARQL); //TODO modify to use eventPropertyIncludeGEVSPARQL
const stayGreenForThisPeriod = 5000;

class EarsEvent {
    constructor(sparqlResultElement) {
        // New, in-the-moment events get no timestamp here - the server assigns it.
        this.eventDefinitionId = sparqlResultElement.eid.value;
        this.label = null;
        const me = getCurrentActor()
        if (me !== null) {
            this.actor = {
                firstName: me.firstName,
                lastName: me.lastName,
                email: me.email,
                organisation: me.organisation
            };
        }

        this.toolCategory = {
            identifier: sparqlResultElement.cu.value,
            transitiveIdentifier: sparqlResultElement.ctu.value,
            name: sparqlResultElement.cl.value
        };
        this.tool = {
            tool: {
                identifier: sparqlResultElement.tu.value,
                transitiveIdentifier: sparqlResultElement.ttu.value,
                name: sparqlResultElement.tl.value
            }
        };
        this.process = {
            identifier: sparqlResultElement.pu.value,
            name: sparqlResultElement.pl.value
        };
        this.action = {
            identifier: sparqlResultElement.au.value,
            name: sparqlResultElement.al.value
        };
        this.subject = {
            identifier: 'http://vocab.nerc.ac.uk/collection/C77/current/M06/',
            name: 'Routine standard measurements'
        };
        this.program = null;
        this.properties = [];
        this.timeStamp = null;
    }
}

//just to give an idea on the structure
const eventData = {
    identifier: null,
    eventDefinitionId: null,
    timeStamp: null,
    properties: [
        {
            key: {
                identifier: 'http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653/',
                transitiveIdentifier: null,
                name: 'Van Veen grab'
            },
            value: '10',
            uom: 'm'
        }
    ],
    actor: {
        firstName: null,
        lastName: null,
        organisation: null,
        phoneNumber: null,
        faxNumber: null,
        email: null
    },
    subject: {
        identifier: 'http://vocab.nerc.ac.uk/collection/C77/current/M06/',
        transitiveIdentifier: null,
        name: 'Routine standard measurements'
    },
    tool: {
        tool: {
            identifier: 'http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653/',
            transitiveIdentifier: null,
            name: 'Van Veen grab'
        },
        parentTool: null
    },
    toolCategory: {
        identifier: 'http://vocab.nerc.ac.uk/collection/L05/current/50/',
        transitiveIdentifier: null,
        name: 'sediment grabs'
    },
    process: {
        identifier: 'http://ontologies.ef-ears.eu/ears2/1#pro_1',
        transitiveIdentifier: null,
        name: 'Sampling'
    },
    action: {
        identifier: 'http://ontologies.ef-ears.eu/ears2/1#act_2',
        transitiveIdentifier: null,
        name: 'End'
    },
    program: null,
    platform: null
};

// isJSON now lives in ears-events-common.js

var rdfBindings; //global
function getBindings(async) {
    if (rdfBindings == null) {
        if (async) {
            // TODO: async loading path is not implemented yet - currently a no-op.
        } else {
            $.ajax({ //synchronous, deliberately: async here would mess up the ordering of the generated buttons
                type: "GET",
                dataType: "json",
                async: false,
                url: jsonVesselRdfLocation,
                success: function (data) {
                    rdfBindings = data.results.bindings;
                }
            });
        }
    }
    return rdfBindings;
}

function deleteEvent(identifier) {
    return $.ajax({
        type: "DELETE",
        async: true,
        url: eventPostLocation + "?identifier=" + identifier,
        success: function (data) {
            console.log("event " + identifier + " deleted");
        },
        error: function (data) {
            console.log("event " + identifier + " not deleted. exception");
        }
    });
}

function postEvent(recentEventButton) {
    let recentEventId = objectKeyFromButton(recentEventButton);
    console.log('Pressed button for ' + recentEventId);
    if (recentEventId !== recentlyDeletedEventId) {
        postEventByEventDefinition(recentEventId, function () {
            $(recentEventButton).removeClass("btn-warning").addClass("btn-success");
            setTimeout(function () {
                $(recentEventButton).removeClass("btn-success");
            }, stayGreenForThisPeriod);
            $("#collapseOne").addClass("show");
        }, function () {
            $(recentEventButton).removeClass("btn-success").addClass("btn-warning");
        });
    }
}

function rdfBindingElementHasEid(element, eventDefinitionId) {
    return compareObjectKeyWithElement(eventDefinitionId, element);
}

function postEventByEventDefinition(eventDefinitionId, successFunction, errorFunction) {
    let rdfBindings = getBindings(false);
    $(rdfBindings).each(function (index, element) {
        if (rdfBindingElementHasEid(element, eventDefinitionId)) {
            let event = new EarsEvent(element);
            postEventInner(event, successFunction, errorFunction);
        }
    });
}

/***
 * Post a provided event to the API, but populate it and display the property popup first
 */
function postEventInner(event, successFunction, errorFunction) {
    const programFieldVal = $("#programField").val() === "" ? null : $("#programField").val();
    if (programFieldVal === null) {
        window.scrollTo(0, 0);
        $('#programField').selectpicker('setStyle', 'btn-warning');
        $("#btnSubmitDropdownChoice").removeClass("btn-success").addClass("btn-warning");
        toggleErrorMessage("Please select a program first.")
        return;
    }

    $('#programField').selectpicker('setStyle', 'btn-warning', 'remove');
    $('#programField').selectpicker('setStyle', 'btn-light', 'add');

    const eid = event.eventDefinitionId;
    event.program = programFieldVal;

    $.ajax({ //asynchronous because we need the properties populated first
        type: "GET",
        dataType: "json",
        async: false,
        url: eventPropertyRdfLocation,
        success: function (data) {
            // JSONPath note: exact-match and "contains" queries against @.eid.value
            // don't work here because the id we have is a URL fragment, not the full
            // stored value. Regex matching (via /i.test(...)) is the one approach that
            // works with both the goessner and jsonpath-plus libraries - keep using it.
            var eid2 = eid.substring(eid.lastIndexOf('_') + 1); //url -> uuid for comparison

            if (window.location.href.includes("event/new")) {
                $("input#fixed_property_2").val(""); //description should not be kept the same over each scenario button press.
                $("input#fixed_property_2").attr('value', ""); //description should not be kept the same over each scenario button press.
                $("#propertyPopup ul.non-fixed-properties").empty(); //clear all true (non-fixed) properties so they don't repeat on each scenario button press. When editing an existing event, this is skipped so the existing values stay.
            }

            let eventPropertyUrls = JSONPath.JSONPath("results.bindings[?(/" + eid2 + "/i.test(@.eid.value))].pru.value", data);
            let eventPropertyLabels = JSONPath.JSONPath("results.bindings[?(/" + eid2 + "/i.test(@.eid.value))].prl.value", data);
            if (!eventPropertyUrls) {
                eventPropertyUrls = [];
                eventPropertyLabels = [];
            }

            //remove label as a property from the list coming from the event itself.
            //(older trees sometimes still define label as a property; we remove and reattach it so it displays in the right order)
            if (eventPropertyUrls.includes("http://ontologies.ef-ears.eu/ears2/1#pry_4")) {
                eventPropertyUrls = eventPropertyUrls.filter(function (value) {
                    return value != "http://ontologies.ef-ears.eu/ears2/1#pry_4";
                });
                eventPropertyLabels = eventPropertyLabels.filter(function (value) {
                    return value != "label";
                });
            }

            //fixed properties, ie. station and label, get prefilled
            $("input#fixed_property_0").attr('value', $("#stationField").val()); //predefined entry coming from the stationField.
            $("input#fixed_property_1").attr('value', $("#labelField").val()); //predefined entry coming from the labelField.
            $("input#fixed_property_2").attr('value', $("#descriptionField").val()); //predefined entry coming from the labelField.

            if (window.location.href.includes("event/new") || eventPropertyUrls.length > 0) { //we are newly creating events or there are properties
                $("#propertyPopup").dialog("open");
                eventPropertyUrls.forEach(function (eventPropertyUrl, index) {
                    //needed as searches in the rdf return e.g. http://ontologies.ef-ears.eu/ears2/1/#pry_18 whereas they could be stored as http://ontologies.ef-ears.eu/ears2/1#pry_18
                    eventPropertyUrl = eventPropertyUrl.replace('/#', '#');

                    if ($("input[data-url='" + eventPropertyUrl + "']").length == 0) { //only add if not yet added - relevant for new events; editing an existing event already has values from the backend via the thymeleaf template
                        var li = buildPropertyListItem(index, eventPropertyUrl, eventPropertyLabels[index]);
                        $("#propertyPopup ul.non-fixed-properties").append(li);
                    }
                });
            } else { //eventPropertyRdfLocation was reached but yielded no url results, so we have no props
                postEventInnerMost(event, successFunction, errorFunction);
            }
        }
    });

    const fakePropsEnum = {
        "label": "http://ontologies.ef-ears.eu/ears2/1#pry_4",
        "station": "http://ontologies.ef-ears.eu/ears2/1#pry_station",
        "description": "http://ontologies.ef-ears.eu/ears2/1#pry_description"
    };

    // .one() instead of .on()+.off(): this handler only ever needs to fire once per
    // popup, and unbinds itself automatically instead of relying on a manual .off() call.
    $(document).one('click', '#btnSubmitEventWithProperties', function () {
        $("#propertyPopup input").each(function (index) {
            const url = $(this).attr('data-url');
            const name = $(this).attr('name');
            if (!Object.values(fakePropsEnum).includes(url)) { //label, station and description hide as properties but are not saved as properties
                const key = {
                    identifier: url, //eg http://ontologies.ef-ears.eu/ears2/1#pry_6546
                    name: name //eg label
                };
                const property = {
                    key: key,
                    uom: '', //TODO
                    value: $(this).val()
                };
                event.properties.push(property);
            } else {
                if (url === fakePropsEnum.label) {
                    event.label = $(this).val();
                }
                if (url === fakePropsEnum.station) {
                    event.station = $(this).val();
                }
                if (url === fakePropsEnum.description) {
                    event.description = $(this).val();
                }
            }
        });
        postEventInnerMost(event, successFunction, errorFunction);
        $("#propertyPopup").dialog("close");
    });
}

/**
 * Builds a single <li> containing the input+label pair for one non-fixed
 * event property, in the shape postEventInner's popup expects.
 */
function buildPropertyListItem(index, eventPropertyUrl, eventPropertyLabel) {
    const input = $('<input>', {
        type: 'text',
        id: 'property_' + index + 3,
        name: eventPropertyLabel,
        class: 'form-control',
        value: null
    });
    input.attr('data-url', eventPropertyUrl);

    const label = $('<label>', {
        class: 'txtBox',
        for: 'property_' + index + 3,
        html: eventPropertyLabel
    });

    return $('<li>', {
        html: '<div class="form-group"><p>' + input.get(0).outerHTML + label.get(0).outerHTML + '</p></div>'
    });
}

/***
 * Post a provided event to the API provided a successFunction and errorFunction
 */
function postEventInnerMost(event, successFunction, errorFunction) {
    $.ajax({
        type: "POST",
        dataType: "json",
        contentType: "application/json;charset=utf-8",
        url: eventPostLocation,
        data: JSON.stringify(event),
        success: function (result) {
            toggleErrorMessage(null);
            successFunction();
        },
        error: function (result) {
            console.log("Failure: " + result.status + ": " + result.responseText);
            console.log(event);
            let message;
            if (isJSON(result.responseText)) {
                message = JSON.parse(result.responseText).message;
            } else {
                message = "HTTP " + result.status + ": " + result.statusText;
            }
            toggleErrorMessage(message);

            errorFunction();
        }
    });
}
