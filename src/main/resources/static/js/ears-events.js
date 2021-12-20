const eventPostLocation = "/ears3/api/event";
const jsonVesselRdfLocation = "/ears3/ontology/vessel/sparql?q=PREFIX%20owl%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0APREFIX%20dc%3A%20%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0APREFIX%20skos%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23%3E%0APREFIX%20ears2%3A%3Chttp%3A%2F%2Fontologies.ef-ears.eu%2Fears2%2F1%23%3E%0APREFIX%20xsd%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0ASELECT%20DISTINCT%20%3Feid%20(str(%3Fc)%20as%20%3Fcu)%20(str(%3Fcc)%20as%20%3Fctu)%20%3Fcl%20(str(%3Ft)%20%20as%20%3Ftu)%20(str(%3Ftc)%20as%20%3Fttu)%20%3Ftl%20(str(%3Fp)%20as%20%3Fpu)%20%3Fpl%20(str(%3Fa)%20as%20%3Fau)%20%3Fal%0AWHERE%20%7B%0A%7B%0AOPTIONAL%20%7B%0A%3Fc%20a%20ears2%3AToolCategory.%0A%3Ft%20a%20ears2%3ATool.%0A%3Fe%20ears2%3AhasProcess%20%3Fp.%0A%3Fe%20ears2%3AhasAction%20%3Fa.%20%0A%3Fe%20ears2%3AwithTool%20%3Ft.%20%0A%3Ft%20ears2%3AisMemberOf%20%3Fc.%0A%3Fe%20ears2%3AasConcept%20%3Fec.%0A%3Fec%20dc%3Aidentifier%20%3Feid.%0A%3Fc%20ears2%3AasConcept%20%3Fcc.%0A%3Fcc%20skos%3AprefLabel%20%3Fcl%20.%0A%3Ft%20ears2%3AasConcept%20%3Ftc.%0A%3Ftc%20skos%3AprefLabel%20%3Ftl%20.%0A%3Fp%20ears2%3AasConcept%20%3Fpc.%0A%3Fpc%20skos%3AprefLabel%20%3Fpl%20.%0A%3Fa%20ears2%3AasConcept%20%3Fac.%0A%3Fac%20skos%3AprefLabel%20%3Fal.%0A%3Fac%20ears2%3Astatus%20%3Fas%20%20%7D%0A%20%7D%0AFILTER%20(!REGEX(%20%3Fal%2C%20%22%5ENew%20Action%22%20)%20%26%26%20str(%3Fas)%20!%3D%20%27Deprecated%27%20)%0A%7D%0A%0AORDER%20BY%20%3Fpl%20%3Fal%0A";
const jsonProgramRdfLocation = "/ears3/ontology/program/sparql?q=PREFIX%20owl%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0APREFIX%20dc%3A%20%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0APREFIX%20skos%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23%3E%0APREFIX%20ears2%3A%3Chttp%3A%2F%2Fontologies.ef-ears.eu%2Fears2%2F1%23%3E%0APREFIX%20xsd%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0ASELECT%20DISTINCT%20%3Feid%20%28str%28%3Fc%29%20as%20%3Fcu%29%20%28str%28%3Fcc%29%20as%20%3Fctu%29%20%3Fcl%20%28str%28%3Ft%29%20%20as%20%3Ftu%29%20%28str%28%3Ftc%29%20as%20%3Fttu%29%20%3Ftl%20%28str%28%3Fp%29%20as%20%3Fpu%29%20%3Fpl%20%28str%28%3Fa%29%20as%20%3Fau%29%20%3Fal%0AWHERE%20%7B%0A%7B%0AOPTIONAL%20%7B%0A%3Fc%20a%20ears2%3AToolCategory.%0A%3Ft%20a%20ears2%3ATool.%0A%3Fe%20ears2%3AhasProcess%20%3Fp.%0A%3Fe%20ears2%3AhasAction%20%3Fa.%20%0A%3Fe%20ears2%3AwithTool%20%3Ft.%20%0A%3Ft%20ears2%3AisMemberOf%20%3Fc.%0A%3Fe%20ears2%3AasConcept%20%3Fec.%0A%3Fec%20dc%3Aidentifier%20%3Feid.%0A%3Fc%20ears2%3AasConcept%20%3Fcc.%0A%3Fcc%20skos%3AprefLabel%20%3Fcl%20.%0A%3Ft%20ears2%3AasConcept%20%3Ftc.%0A%3Ftc%20skos%3AprefLabel%20%3Ftl%20.%0A%3Fp%20ears2%3AasConcept%20%3Fpc.%0A%3Fpc%20skos%3AprefLabel%20%3Fpl%20.%0A%3Fa%20ears2%3AasConcept%20%3Fac.%0A%3Fac%20skos%3AprefLabel%20%3Fal%20%20%7D%0A%20%7D%0A%0A%7D%0AORDER%20BY%20%3Fpl%20%3Fal";

const eventPropertyRdfLocation = "/ears3/ontology/vessel/sparql?q=PREFIX%20owl%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%20PREFIX%20dc%3A%20%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%20PREFIX%20skos%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23%3E%20PREFIX%20ears2%3A%3Chttp%3A%2F%2Fontologies.ef-ears.eu%2Fears2%2F1%23%3E%20PREFIX%20xsd%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%20SELECT%20DISTINCT%20%3Feid%20(str(%3Fpr)%20as%20%3Fpru)%20%3Fprl%20%3Fmult%20%3Funit%20WHERE%20%7B%20%7B%20OPTIONAL%20%7B%20%3Fe%20ears2%3AhasProperty%20%3Fpr.%20%3Fe%20ears2%3AhasProcess%20%3Fp.%20%3Fe%20ears2%3AhasAction%20%3Fa.%20%3Fe%20ears2%3AwithTool%20%3Ft.%20%3Ft%20ears2%3AisMemberOf%20%3Fc.%20%3Fe%20ears2%3AasConcept%20%3Fec.%20%3Fec%20dc%3Aidentifier%20%3Feid.%20%3Fpr%20ears2%3AasConcept%20%3Fprc.%20%3Fprc%20skos%3AprefLabel%20%3Fprl.%20%3Fpr%20ears2%3Amultiple%20%3Fmult%20%7D%7D%20%7D%20ORDER%20BY%20%3Feid%20";

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
        this.properties=[];
        this.timeStamp=null;
    }
}

//just to give an idea on the structure
const eventData = {
    identifier: null,
    eventDefinitionId: null,
    timeStamp: null,
    properties:[
        {
            key: {
                identifier: 'http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653/',
                transitiveIdentifier: null,
                name: 'Van Veen grab'
            },
            value: '10',
            uom:'m'
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

function getBindings(async) {
    var rdfBindings;
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
    return rdfBindings;
}

function deleteEvent(identifier){
    $.ajax({
            type: "DELETE",
            async: true,
            url: eventPostLocation+"?identifier="+identifier,
            success: function (data) {
                console.log("event "+identifier+" deleted");
            },
            failure: function (data) {
                console.log("event "+identifier+" not deleted. exception");
            },
        });
}

function postEvent(recentEventButton) {
    recentEventId = recentEventButton.id;
    console.log('Pressed button for '+recentEventId);
    if(recentEventId !== recentlyDeletedEventId){
        postEventByEventDefinition(recentEventId, function() {
            $(recentEventButton).removeClass("btn-warning").addClass("btn-success");
            setTimeout(function() {
                $(recentEventButton).removeClass("btn-success");
            }, stayGreenForThisPeriod);
            $("#collapseOne").addClass("show");
        }, function() {
            $(recentEventButton).removeClass("btn-success").addClass("btn-warning");
        });
    }
}

function postEventByEventDefinition(eventDefinitionId, successFunction, errorFunction) {
    var eid11 = "element.eid.value  == " + "'" + eventDefinitionId + "'" + "  && ";
    var condition11 = "(".concat(eid11, ")").replace(/&&([^'&&']*)$/, '' + '$1');
    $.getJSON(jsonVesselRdfLocation,
        function(data) {
            $(data.results.bindings).each(function(index, element) {
                if (eval(condition11)) {
                    event = new EarsEvent(element);
                    postEventInner(event, successFunction, errorFunction);
                }
            })
        }
    );
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
        var eid=event.eventDefinitionId;
        
        event.program = programFieldVal;
        
        $.ajax({ //asynchronous because we need the properties populated first
                type: "GET",
                dataType: "json",
                async: false,
                url: eventPropertyRdfLocation,
                success: function(data) {
                    var urls = jsonPath(data, "results.bindings[?(@.eid.value =='"+eid+"')].pru.value");
                    var labels = jsonPath(data, "results.bindings[?(@.eid.value =='"+eid+"')].prl.value");
                    if(!urls || (urls && !urls.includes("http://ontologies.ef-ears.eu/ears2/1#pry_4"))){
                        if(!urls){
                            urls=[];
                            labels=[];
                        }
                        urls.push("http://ontologies.ef-ears.eu/ears2/1#pry_4");
                        labels.push("label");
                    }
                    urls.push("http://ontologies.ef-ears.eu/ears2/1#pry_station");
                    labels.push("station");
                    if(urls){
                        $("#propertyPopup ul").empty();
                        $("#propertyPopup").dialog("open");
                        urls.forEach(function (item, index) {
                            //console.log(item, labels[index]);
                            var input= $('<input>', {
                                type: 'text',
                                id: 'property_'+index,
                                name:  labels[index], 
                                class: 'form-control',
                                value: null
                            });
                            if(item === "http://ontologies.ef-ears.eu/ears2/1#pry_4"){
                                input.attr('value',$("#labelField").val()); //predefined entry coming from the labelField.
                            }
                            if(item === "http://ontologies.ef-ears.eu/ears2/1#pry_station"){
                                input.attr('value',$("#stationField").val()); //predefined entry coming from the labelField.
                            }
                            input.attr('data-url',item);
                            var label= $('<label>', {
                                class: 'txtBox',
                                for: 'property_'+index,
                                html: labels[index]
                            });
                            var unit= $('<label>', {
                                class: 'txtBox',
                                for: 'property_'+index,
                                html: labels[index]+": "
                            });
                            var li= $('<li>', {
                                html: '<div class="form-group"><p>'+input.get(0).outerHTML+label.get(0).outerHTML+'</p></div>'
                            });
                            $("#propertyPopup ul").append(li);
                        });
                    }else{ //eventPropertyRdfLocation was reached but yielded no url results, so we have no props
                        postEventInnerMost(event, successFunction, errorFunction);
                    }
                }
        });  
        
        $(document).on('click','#btnSubmitEventWithProperties',function(){
            $("#propertyPopup input").each(function( index ) {
                var url = $(this).attr('data-url');
                var name = $(this).attr('name');
                if(url !== "http://ontologies.ef-ears.eu/ears2/1#pry_4"){
                    var key = {
                        identifier : url, //eg http://ontologies.ef-ears.eu/ears2/1#pry_6546,
                        name : name //eg label
                    }
                    var property = {
                        key : key,
                        uom : '', //TODO
                        value : $(this).val()
                    }
                    event.properties.push(property);
                }else{                 
                    event.label = $(this).val();
                }
            });
            $(document).off('click', '#btnSubmitEventWithProperties'); //clear it else previous events are readded each time
            // console.log(event);
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
            success: function(result) {
               $('#serverBadFeedbackBox').css('visibility', 'hidden');
               // console.log("Success: Event created with id:" + result.id + ", identifier:" + result.identifier + " and timeStamp:" + result.timeStamp);
                successFunction();
            },
            error: function(result) {
                console.log("Failure: " + result.status + ": " + result.responseText);
                console.log(event);
                var message='';
                if(isJSON(result.responseText)){
                    message= JSON.parse(result.responseText).message;     
                }else{
                    message="HTTP "+result.status+": "+result.statusText;
                }
                $('#serverBadFeedbackBox').css('visibility', 'visible');
                $('#serverBadFeedbackBox').text(message);
                
                errorFunction();
            }
        });
}