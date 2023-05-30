const eventPostLocation = "/ears3/api/event";

const conceptHierarchySPARQL = `PREFIX owl: <http://www.w3.org/2002/07/owl#>
PREFIX dc: <http://purl.org/dc/elements/1.1/>
PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
PREFIX ears2:<http://ontologies.ef-ears.eu/ears2/1#>
PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>
SELECT DISTINCT  (replace(replace(str(?e),"http://ontologies.ef-ears.eu/ears2/1/#gev_","ears:gev::","i"),"http://ontologies.ef-ears.eu/ears2/1/#sev_","ears:sev::","i") as ?eid)  (str(?c) as ?cu) (str(?cc) as ?ctu) ?cl (str(?t)  as ?tu) (str(?tc) as ?ttu) ?tl (str(?p) as ?pu) ?pl (str(?a) as ?au) ?al
WHERE {
{
OPTIONAL {
?c a ears2:ToolCategory.
?t a ears2:Tool.
?e ears2:hasProcess ?p.
?e ears2:hasAction ?a. 
{?e ears2:withTool ?t.} UNION {?e ears2:withTool ?c.}
?t ears2:isMemberOf ?c.
#?e ears2:asConcept ?ec.
#?ec dc:identifier ?eid.
?c ears2:asConcept ?cc.
?cc skos:prefLabel ?cl .
?t ears2:asConcept ?tc.
?tc skos:prefLabel ?tl .
?p ears2:asConcept ?pc.
?pc skos:prefLabel ?pl .
?a ears2:asConcept ?ac.
?ac skos:prefLabel ?al.
?ac ears2:status ?as  
}
 }
FILTER (!REGEX( ?al, "^New Action" ) && str(?as) != 'Deprecated' )
}
ORDER BY ?pl ?al`;

const eventPropertySPARQL = `PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX dc: <http://purl.org/dc/elements/1.1/> 
PREFIX skos:<http://www.w3.org/2004/02/skos/core#> 
PREFIX ears2:<http://ontologies.ef-ears.eu/ears2/1#> 
PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> 
SELECT DISTINCT ?eid (str(?pr) as ?pru) ?prl ?mult ?unit 
WHERE { 
    { 
        OPTIONAL { 
            ?e ears2:hasProperty ?pr. 
            ?e ears2:hasProcess ?p. 
            ?e ears2:hasAction ?a. 
            ?e ears2:withTool ?t. 
            ?t ears2:isMemberOf ?c. 
            ?e ears2:asConcept ?ec. 
            ?ec dc:identifier ?eid. 
            ?pr ears2:asConcept ?prc. 
            ?prc skos:prefLabel ?prl. 
            ?pr ears2:multiple ?mult }
        } 
    } 
ORDER BY ?eid `

const eventPropertyIncludeGEVSPARQL = `PREFIX owl: <http://www.w3.org/2002/07/owl#> 
PREFIX dc: <http://purl.org/dc/elements/1.1/> 
PREFIX skos:<http://www.w3.org/2004/02/skos/core#> 
PREFIX ears2:<http://ontologies.ef-ears.eu/ears2/1#> 
PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> 
SELECT DISTINCT ?eid (str(?pr) as ?pru) ?prl ?mult ?unit 
WHERE { 
    { 
        OPTIONAL { 
            {?e a ears2:GenericEventDefinition} UNION {?e a ears2:SpecificEventDefinition}
            ?e ears2:asConcept ?ec. 
            ?ec dc:identifier ?eid. 
            ?pr ears2:asConcept ?prc. 
            ?prc skos:prefLabel ?prl. 
            ?pr ears2:multiple ?mult }
        } 
    } 
ORDER BY ?eid`
const jsonVesselRdfLocation = "/ears3/ontology/vessel/sparql?q=" + encodeURIComponent(conceptHierarchySPARQL);
const jsonProgramRdfLocation = "/ears3/ontology/program/sparql?q=" + encodeURIComponent(conceptHierarchySPARQL);

const eventPropertyRdfLocation = "/ears3/ontology/vessel/sparql?q=" + encodeURIComponent(eventPropertySPARQL); //TODO modify to use eventPropertyIncludeGEVSPARQL

const stayGreenForThisPeriod = 5000;
class EarsEvent {
    constructor(sparqlResultElement) {
        //var ts = new Date();
        //event.timeStamp = ts.toISOString();
        //for brand new in-the-moment events no timestamp should be provided.
        this.eventDefinitionId = sparqlResultElement.eid.value;
        this.label = null;
        if (localStorage.actor !== null) {
            let actor = JSON.parse(localStorage.actor);
            this.actor = new Object();
            this.actor.firstName = actor.firstName;
            this.actor.lastName = actor.lastName;
            this.actor.email = actor.email;
            this.actor.organisation = actor.organisation;
        }
        this.toolCategory = new Object();
        this.toolCategory.identifier = sparqlResultElement.cu.value;
        this.toolCategory.transitiveIdentifier = sparqlResultElement.ctu.value;
        this.toolCategory.name = sparqlResultElement.cl.value;
        this.tool = new Object();
        this.tool.tool = new Object();
        this.tool.tool.identifier = sparqlResultElement.tu.value;
        this.tool.tool.transitiveIdentifier = sparqlResultElement.ttu.value;
        this.tool.tool.name = sparqlResultElement.tl.value;
        this.process = new Object();
        this.process.identifier = sparqlResultElement.pu.value;
        this.process.name = sparqlResultElement.pl.value;
        this.action = new Object();
        this.action.identifier = sparqlResultElement.au.value;
        this.action.name = sparqlResultElement.al.value;
        this.subject = new Object();
        this.subject.identifier = 'http://vocab.nerc.ac.uk/collection/C77/current/M06/';
        this.subject.name = 'Routine standard measurements';
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

function isJSON(str) {
    try {
        return (JSON.parse(str) && !!str);
    } catch (e) {
        return false;
    }
}


var rdfBindings; //global
function getBindings(async) {
    if (rdfBindings == null) {
        if (async) {

        } else {
            $.ajax({//asynchronous because synchronous messes up the order of the buttons
                type: "GET",
                dataType: "json",
                async: false,
                url: jsonVesselRdfLocation,
                success: function (data) {
                    rdfBindings = data.results.bindings;
                }
            });
            /*let programFieldVal = $("#programField").val() === "" ? null : $("#programField").val();
            if (programFieldVal !== null) {
                $.ajax({//asynchronous because synchronous messes up the order of the buttons
                    type: "GET",
                    dataType: "json",
                    async: false,
                    url: jsonProgramRdfLocation + "&programIdentifier=" + programFieldVal,
                    success: function (data) {
                        rdfBindings = rdfBindings.concat(data.results.bindings);
                    }
                });
            }*/
        }
    }
    return rdfBindings;
}

function deleteEvent(identifier) {
    $.ajax({
        type: "DELETE",
        async: true,
        url: eventPostLocation + "?identifier=" + identifier,
        success: function (data) {
            console.log("event " + identifier + " deleted");
        },
        failure: function (data) {
            console.log("event " + identifier + " not deleted. exception");
        },
    });
}

function postEvent(recentEventButton) {
    recentEventId = { eid: recentEventButton.id, tool: recentEventButton.getAttribute('data-tool') };
    console.log('Pressed button for ' + recentEventId);
    if (recentEventId.eid !== recentlyDeletedEventId) {
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
    return element.eid.value == eventDefinitionId.eid && element.tu.value == eventDefinitionId.tool
}

function postEventByEventDefinition(eventDefinitionId, successFunction, errorFunction) {
    var rdfBindings = getBindings(false);
    $(rdfBindings).each(function (index, element) {
        if (rdfBindingElementHasEid(element, eventDefinitionId)) {
            event = new EarsEvent(element);
            postEventInner(event, successFunction, errorFunction);
        }
    })
}

/***
 * Post a provided event to the API, but populate it and display the property popup first
 */
function postEventInner(event, successFunction, errorFunction) {
    var programFieldVal = $("#programField").val() === "" ? null : $("#programField").val();
    if (programFieldVal === null) {
        window.scrollTo(0, 0);
        $('#programField').selectpicker('setStyle', 'btn-warning');
        $("#btnSubmitDropdownChoice").removeClass("btn-success").addClass("btn-warning");
    } else {
        $('#programField').selectpicker('setStyle', 'btn-warning', 'remove');
        $('#programField').selectpicker('setStyle', 'btn-light', 'add');
        var eid = event.eventDefinitionId;

        event.program = programFieldVal;

        $.ajax({ //asynchronous because we need the properties populated first
            type: "GET",
            dataType: "json",
            async: false,
            url: eventPropertyRdfLocation,
            success: function (data) {
                var eid2 = eid.substring(eid.lastIndexOf('_') + 1); //needed as what we get is an url but we need to compare to an uuid
                //original used the original library, https://goessner.net/articles/JsonPath/
                //uses exact matches, wouldn't work for the above reason
                //doesn't work with contains
                //var urls = jsonPath(data, "results.bindings[?(@.eid.value =='"+eid2+"')].pru.value");
                //var labels = jsonPath(data, "results.bindings[?(@.eid.value =='"+eid2+"')].prl.value");

                //use a more advanced jsonpath library, https://jsonpath-plus.github.io/JSONPath/docs/ts/
                //contains doesn't work with this either
                //var urls = JSONPath.JSONPath("results.bindings[?(@.eid.value contains '"+eid2+"')].pru.value",data);
                //var labels = JSONPath.JSONPath("results.bindings[?(@.eid.value contains '"+eid2+"')].prl.value",data); 

                //regexp matching does work with both libraries
                //But both libraries do not return zero or one hit element, but always all elements of the match

                //solution
                //jsonpath regexes do work as described in https://support.smartbear.com/alertsite/docs/monitors/api/endpoint/jsonpath.html do not work, and should be written like this: /cat.*$/i.test(@.eid.value), see https://stackoverflow.com/a/59661213

                //ie remove  <script src="/ears3/js/index-browser-umd.cjs" type="text/javascript"></script>
                //and go back to simplest library https://goessner.net/articles/JsonPath/ ie <script src="/ears3/js/jsonpath-0.8.0.js" type="text/javascript"></script>

                if (window.location.href.includes("event/new")) {
                    $("input#fixed_property_2").val(""); //description should not be kept the same over each scenario button press. 
                    $("input#fixed_property_2").attr('value', ""); //description should not be kept the same over each scenario button press. 
                    $("#propertyPopup ul.non-fixed-properties").empty(); //clear all true properties, ie. those non-fixed. Otherwise they repeat each time again for each scenario button press. However, when editing an existing event, it needs to stay. 
                }

                var eventPropertyUrls = JSONPath.JSONPath("results.bindings[?(/" + eid2 + "/i.test(@.eid.value))].pru.value", data);
                var eventPropertyLabels = JSONPath.JSONPath("results.bindings[?(/" + eid2 + "/i.test(@.eid.value))].prl.value", data);
                if (!eventPropertyUrls) {
                    eventPropertyUrls = [];
                    eventPropertyLabels = [];
                }

                //remove label as a property from the list coming from the event itself.
                if (eventPropertyUrls.includes("http://ontologies.ef-ears.eu/ears2/1#pry_4")) { //this because sometimes label is still defined as a property in older trees. We remove and reattach it so it displays in the right order
                    eventPropertyUrls = eventPropertyUrls.filter(function (value, index, arr) {
                        return value != "http://ontologies.ef-ears.eu/ears2/1#pry_4"
                    });

                    eventPropertyLabels = eventPropertyLabels.filter(function (value, index, arr) {
                        return value != "label"
                    });
                }

                //fixed properties, ie. station and field, get prefilled
                // $("input#fixed_property_0").val($("#stationField").val()); //predefined entry coming from the stationField.
                // $("input#fixed_property_1").val($("#labelField").val()); //predefined entry coming from the labelField.

                $("input#fixed_property_0").attr('value', $("#stationField").val()); //predefined entry coming from the stationField.
                $("input#fixed_property_1").attr('value', $("#labelField").val()); //predefined entry coming from the labelField.
                if (eventPropertyUrls) { //we found matches                    
                    $("#propertyPopup").dialog("open");
                    eventPropertyUrls.forEach(function (eventPropertyUrl, index) {
                        eventPropertyUrl = eventPropertyUrl.replace('/#', '#') //needed as searches in th rdf return ie. http://ontologies.ef-ears.eu/ears2/1/#pry_18 whereas they could be stored as http://ontologies.ef-ears.eu/ears2/1#pry_18 
                        var eid2 = eid.substring(eid.lastIndexOf('_') + 1); //needed as what we get is an url but we need to compare to an uuid
                        if ($("input[data-url='" + eventPropertyUrl + "']").length == 0) { //only add if the same one was not yet added. We need this if we create new events. When editing existing events, we have the value from the back end via the thymeleaf template.
                            var input = $('<input>', {
                                type: 'text',
                                id: 'property_' + index + 3,
                                name: eventPropertyLabels[index],
                                class: 'form-control',
                                value: null
                            });

                            input.attr('data-url', eventPropertyUrl);
                            var label = $('<label>', {
                                class: 'txtBox',
                                for: 'property_' + index + 3,
                                html: eventPropertyLabels[index]
                            });
                            /*var unit= $('<label>', {
                                class: 'txtBox',
                                for: 'property_'+index,
                                html: labels[index]+": "
                            });*/
                            var li = $('<li>', {
                                html: '<div class="form-group"><p>' + input.get(0).outerHTML + label.get(0).outerHTML + '</p></div>'
                            });
                            $("#propertyPopup ul.non-fixed-properties").append(li);
                        }
                    });
                } else { //eventPropertyRdfLocation was reached but yielded no url results, so we have no props
                    postEventInnerMost(event, successFunction, errorFunction);
                }
            }
        });

        const fakePropsEnum = { "label": "http://ontologies.ef-ears.eu/ears2/1#pry_4", "station": "http://ontologies.ef-ears.eu/ears2/1#pry_station", "description": "http://ontologies.ef-ears.eu/ears2/1#pry_description" }
        // const fakeProps = ["http://ontologies.ef-ears.eu/ears2/1#pry_4","http://ontologies.ef-ears.eu/ears2/1#pry_station","http://ontologies.ef-ears.eu/ears2/1#pry_description"]; //label, station and description hide as properties but are not saved as properties

        $(document).on('click', '#btnSubmitEventWithProperties', function () {
            $("#propertyPopup input").each(function (index) {
                var url = $(this).attr('data-url');
                var name = $(this).attr('name');
                if (!Object.values(fakePropsEnum).includes(url)) { //label, station and description hide as properties but are not saved as properties
                    var key = {
                        identifier: url, //eg http://ontologies.ef-ears.eu/ears2/1#pry_6546
                        name: name //eg label
                    }
                    var property = {
                        key: key,
                        uom: '', //TODO
                        value: $(this).val()
                    }
                    event.properties.push(property);
                } else {
                    if (url === fakePropsEnum.label) { event.label = $(this).val(); }
                    if (url === fakePropsEnum.station) { event.station = $(this).val(); }
                    if (url === fakePropsEnum.description) { event.description = $(this).val(); }
                }
            });
            $(document).off('click', '#btnSubmitEventWithProperties'); //clear it else previous events are readded each time
            postEventInnerMost(event, successFunction, errorFunction);
            $("#propertyPopup").dialog("close");
        });
    }
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
            $('#serverBadFeedbackBox').css('visibility', 'hidden');
            // console.log("Success: Event created with id:" + result.id + ", identifier:" + result.identifier + " and timeStamp:" + result.timeStamp);
            successFunction();
        },
        error: function (result) {
            console.log("Failure: " + result.status + ": " + result.responseText);
            console.log(event);
            var message = '';
            if (isJSON(result.responseText)) {
                message = JSON.parse(result.responseText).message;
            } else {
                message = "HTTP " + result.status + ": " + result.statusText;
            }
            $('#serverBadFeedbackBox').css('visibility', 'visible');
            $('#serverBadFeedbackBox').text(message);

            errorFunction();
        }
    });
}