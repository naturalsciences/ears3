//const jsonRdfLocation = "../ontology/vessel/sparql?q=PREFIX%20owl%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0APREFIX%20dc%3A%20%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0APREFIX%20skos%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23%3E%0APREFIX%20ears2%3A%3Chttp%3A%2F%2Fontologies.ef-ears.eu%2Fears2%2F1%23%3E%0APREFIX%20xsd%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0ASELECT%20DISTINCT%20%3Feid%20%28str%28%3Fc%29%20as%20%3Fcu%29%20%28str%28%3Fcc%29%20as%20%3Fctu%29%20%3Fcl%20%28str%28%3Ft%29%20%20as%20%3Ftu%29%20%28str%28%3Ftc%29%20as%20%3Fttu%29%20%3Ftl%20%28str%28%3Fp%29%20as%20%3Fpu%29%20%3Fpl%20%28str%28%3Fa%29%20as%20%3Fau%29%20%3Fal%0AWHERE%20%7B%0A%7B%0AOPTIONAL%20%7B%0A%3Fc%20a%20ears2%3AToolCategory.%0A%3Ft%20a%20ears2%3ATool.%0A%3Fe%20ears2%3AhasProcess%20%3Fp.%0A%3Fe%20ears2%3AhasAction%20%3Fa.%20%0A%3Fe%20ears2%3AwithTool%20%3Ft.%20%0A%3Ft%20ears2%3AisMemberOf%20%3Fc.%0A%3Fe%20ears2%3AasConcept%20%3Fec.%0A%3Fec%20dc%3Aidentifier%20%3Feid.%0A%3Fc%20ears2%3AasConcept%20%3Fcc.%0A%3Fcc%20skos%3AprefLabel%20%3Fcl%20.%0A%3Ft%20ears2%3AasConcept%20%3Ftc.%0A%3Ftc%20skos%3AprefLabel%20%3Ftl%20.%0A%3Fp%20ears2%3AasConcept%20%3Fpc.%0A%3Fpc%20skos%3AprefLabel%20%3Fpl%20.%0A%3Fa%20ears2%3AasConcept%20%3Fac.%0A%3Fac%20skos%3AprefLabel%20%3Fal%20%20%7D%0A%20%7D%0A%0A%7D%0AORDER%20BY%20%3Fpl%20%3Fal";
//this crazyness needs refactoring
$.getJSON(jsonVesselRdfLocation,
                function (data) {
                var bb = [];
                        $.each(data.results.bindings, function (key, item) {

                        var events = $.grep(bb, function (e) {
                        return item.cu.value === e.cu.value &&
                                item.cl.value === e.cl.value;
                        });
                                if (events.length === 0) {
                        bb.push(item);
                        }
                        //  $("#result").append(item.cu.value+'</br>');
                        });
                        var select_option_data = '';
                        select_option_data += '<option value=1></option>'
                        $.each(bb, function (key, unique) {
                        select_option_data += '<option value="' + unique.cu.value + '">' + unique.cl.value + '</option>'
                        });
                        // $("#idSelect_tc").append(select_option_data);
                        $("#idSelect_tc")
                        .html(select_option_data)
                        .selectpicker('refresh');
                }
        );
        $.getJSON(jsonVesselRdfLocation,
                function (data) {
                var bb = [];
                        $.each(data.results.bindings, function (key, item) {

                        var events = $.grep(bb, function (e) {
                        return item.tu.value === e.tu.value &&
                                item.tl.value === e.tl.value;
                        });
                                if (events.length === 0) {
                        bb.push(item);
                        }
                        //  $("#result").append(item.cu.value+'</br>');


                        });
                        var select_option_data = '';
                        select_option_data += '<option value=1></option>'
                        $.each(bb, function (key, unique) {
                        select_option_data += '<option value="' + unique.tu.value + '">' + unique.tl.value + '</option>'


                        });
                        //  $("#idSelect_t").append(select_option_data);
                        $("#idSelect_t")
                        .html(select_option_data)
                        .selectpicker('refresh');
                }
        );
        $.getJSON(jsonVesselRdfLocation,
                function (data) {
                var bb = [];
                        $.each(data.results.bindings, function (key, item) {

                        var events = $.grep(bb, function (e) {
                        return item.pu.value === e.pu.value &&
                                item.pl.value === e.pl.value;
                        });
                                if (events.length === 0) {
                        bb.push(item);
                        }
                        //  $("#result").append(item.cu.value+'</br>');


                        });
                        var select_option_data = '';
                        select_option_data += '<option value=1></option>'
                        $.each(bb, function (key, unique) {
                        select_option_data += '<option value="' + unique.pu.value + '">' + unique.pl.value + '</option>'


                        });
                        // $("#idSelect_p").append(select_option_data); //add news to
                        $("#idSelect_p")
                        .html(select_option_data) ///replace
                        .selectpicker('refresh');
                }
        );
        $.getJSON(jsonVesselRdfLocation,
                function (data) {

                var bb = [];
                        $.each(data.results.bindings, function (key, item) {

                        var events = $.grep(bb, function (e) {
                        return item.au.value === e.au.value &&
                                item.al.value === e.al.value;
                        });
                                if (events.length === 0) {
                        bb.push(item);
                        }
                        //  $("#result").append(item.cu.value+'</br>');
                        });
                        var select_option_data = '';
                        select_option_data += '<option value=1></option>'
                        $.each(bb, function (key, unique) {
                        select_option_data += '<option value="' + unique.au.value + '">' + unique.al.value + '</option>'

                        });
                        //  $("#idSelect").append(select_option_data);
                        $("#idSelect_a")
                        .html(select_option_data)
                        .selectpicker('refresh');
                }
        );
        $('#idSelect_tc').prop('disabled', false);
        $('#idSelect_tc').selectpicker('refresh');
        $('#idSelect_t').prop('disabled', false);
        $('#idSelect_t').selectpicker('refresh');
        $('#idSelect_p').prop('disabled', false);
        $('#idSelect_p').selectpicker('refresh');
        $('#idSelect_a').prop('disabled', false);
        $('#idSelect_a').selectpicker('refresh');
        $('#id_form_process').hide();
        $('#id_eid').hide();
        $('#tc_lock').css('visibility', 'hidden');
        $('#t_lock').css('visibility', 'hidden');
        $('#p_lock').css('visibility', 'hidden');
        $('#a_lock').css('visibility', 'hidden');
        $('#tc_unlock').css('visibility', 'visible');
        $('#t_unlock').css('visibility', 'visible');
        $('#p_unlock').css('visibility', 'visible');
        $('#a_unlock').css('visibility', 'visible');





