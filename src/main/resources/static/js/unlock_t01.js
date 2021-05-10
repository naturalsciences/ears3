//const jsonRdfLocation = "../ontology/vessel/sparql?q=PREFIX%20owl%3A%20%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0APREFIX%20dc%3A%20%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0APREFIX%20skos%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23%3E%0APREFIX%20ears2%3A%3Chttp%3A%2F%2Fontologies.ef-ears.eu%2Fears2%2F1%23%3E%0APREFIX%20xsd%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0ASELECT%20DISTINCT%20%3Feid%20%28str%28%3Fc%29%20as%20%3Fcu%29%20%28str%28%3Fcc%29%20as%20%3Fctu%29%20%3Fcl%20%28str%28%3Ft%29%20%20as%20%3Ftu%29%20%28str%28%3Ftc%29%20as%20%3Fttu%29%20%3Ftl%20%28str%28%3Fp%29%20as%20%3Fpu%29%20%3Fpl%20%28str%28%3Fa%29%20as%20%3Fau%29%20%3Fal%0AWHERE%20%7B%0A%7B%0AOPTIONAL%20%7B%0A%3Fc%20a%20ears2%3AToolCategory.%0A%3Ft%20a%20ears2%3ATool.%0A%3Fe%20ears2%3AhasProcess%20%3Fp.%0A%3Fe%20ears2%3AhasAction%20%3Fa.%20%0A%3Fe%20ears2%3AwithTool%20%3Ft.%20%0A%3Ft%20ears2%3AisMemberOf%20%3Fc.%0A%3Fe%20ears2%3AasConcept%20%3Fec.%0A%3Fec%20dc%3Aidentifier%20%3Feid.%0A%3Fc%20ears2%3AasConcept%20%3Fcc.%0A%3Fcc%20skos%3AprefLabel%20%3Fcl%20.%0A%3Ft%20ears2%3AasConcept%20%3Ftc.%0A%3Ftc%20skos%3AprefLabel%20%3Ftl%20.%0A%3Fp%20ears2%3AasConcept%20%3Fpc.%0A%3Fpc%20skos%3AprefLabel%20%3Fpl%20.%0A%3Fa%20ears2%3AasConcept%20%3Fac.%0A%3Fac%20skos%3AprefLabel%20%3Fal%20%20%7D%0A%20%7D%0A%0A%7D%0AORDER%20BY%20%3Fpl%20%3Fal";
        if (($('#idSelect_tc').attr('disabled') != 'disabled') && ($('#idSelect_p').attr('disabled') != 'disabled') && ($('#idSelect_a').attr('disabled') != 'disabled')) {

$.getScript("js/unlock_tc.js");
}
else
{

var arraySelectedValues = [];
        x = - 1;
        var cu = '';
        var tu = '';
        var pu = '';
        var au = '';
        var cu_q = '';
        var tu_q = '';
        var pu_q = '';
        var au_q = '';
        var condition = '';
        var condition_q = '';
        var startCondition = "(";
        if ($("#idSelect_tc").val() != 1) {
cu = "element.cu.value  == " + "'" + $("#idSelect_tc").val() + "'" + "  && ";
        //(element.cu.value  == 'http://ontologies.ef-ears.eu/ears2/1#ctg_55'  && element.au.value  == 'http://ontologies.ef-ears.eu/ears2/1#act_2'   )

        cu_q = "item.cu.value  == " + "'" + $("#idSelect_tc").val() + "'" + "  && ";
}


if ($("#idSelect_t").val() != 1) {
tu = "element.tu.value  == " + "'" + $("#idSelect_t").val() + "'" + "  && ";
        tu_q = "item.tu.value  == " + "'" + $("#idSelect_t").val() + "'" + "  && ";
}

if ($("#idSelect_p").val() != 1) {
pu = "element.pu.value  == " + "'" + $("#idSelect_p").val() + "'" + "  && ";
        pu_q = "item.pu.value  == " + "'" + $("#idSelect_p").val() + "'" + "  && ";
}


if ($("#idSelect_a").val() != 1) {
au = "element.au.value  == " + "'" + $("#idSelect_a").val() + "'" + "  && ";
        au_q = "item.au.value  == " + "'" + $("#idSelect_a").val() + "'" + "  && ";
}

var endCondition = ")";
        //   console.log('concat'+startCondition.concat(cu,tu, pu,au,endCondition));

        condition = startCondition.concat(cu, endCondition).replace(/&&([^'&&']*)$/, '' + '$1');
        condition_q = startCondition.concat(cu_q, endCondition).replace(/&&([^'&&']*)$/, '' + '$1');
        var resultJson = [];
        //console.log(condition);
        $.getJSON(jsonVesselRdfLocation,
                function (data) {

                var tc = [];
                        var t = [];
                        var p = [];
                        var a = [];
                        $(data.results.bindings).each(function (index, element) {
                if (eval(condition)) { //(element.tu.value  == 'http://ontologies.ef-ears.eu/ears2/1#ves_1792'   )
                //    console.log(element.cl.value + '-----' + element.tl.value + '-----' + element.pl.value + '-----' + element.al.value);
                resultJson.push(element);
                }


                });
                        var aa = '';
                        var select_option_data_PU = '';
                        select_option_data_PU += '<option value=1></option>';
                        var select_option_data_TU = '';
                        select_option_data_TU += '<option value=1></option>';
                        var select_option_data_AU = '';
                        select_option_data_AU += '<option value=1></option>';
                        //21092020   //alert ($("#idSelect_tc").val() +'  '+$("#idSelect_t").val() +'  '+$("#idSelect_p").val()+'   '+$("#idSelect_a").val())
//21092020 //
//alert(condition_q);

//(element.cu.value  == 'http://ontologies.ef-ears.eu/ears2/1#ctg_55'  && element.au.value  == 'http://ontologies.ef-ears.eu/ears2/1#act_2'   )
                        //(item.cu.value == $("#idSelect_tc").val()) 






                        ////alert(condition_q);


                        //   console.log('====--------------------->' + resultJson.length);
                        $.each(resultJson, function (key, item) {
                        aa += 'CU' + '--' + item.cl.value + '--' + item.tl.value + '--' + item.pl.value + '--' + item.al.value + '</br>'
                                select_option_data_PU += '<option value="' + item.pu.value + '">' + item.pl.value + '</option>'
                                select_option_data_TU += '<option value="' + item.tu.value + '">' + item.tl.value + '</option>'
                                select_option_data_AU += '<option value="' + item.au.value + '">' + item.al.value + '</option>'

                                // 

                                if ($('#idSelect_tc').attr('disabled') != 'disabled') {//mettre à jour donc avec ql condition
                        //for this ToolCategory => set up Process
                        if (condition_q) {
                        //alert('ACTION' + condition_q);
                        var events = $.grep(tc, function (e) {
                        return item.cu.value === e.cu.value &&
                                item.cl.value === e.cl.value;
                        });
                                if (events.length === 0) {
                        tc.push(item);
                        }
                        var select_option_data = '';
                                select_option_data += '<option value=1></option>'
                                $.each(tc, function (key, unique) {
                                select_option_data += '<option value="' + unique.cu.value + '">' + unique.cl.value + '</option>'

                                });
                                $("#idSelect_tc")
                                .html(select_option_data)
                                .selectpicker('refresh');
                        }
                        }



                        if ($('#idSelect_p').attr('disabled') != 'disabled') {
                        //for this ToolCategory => set up Process
                        if (condition_q) {
                        var events = $.grep(p, function (e) {
                        return item.pu.value === e.pu.value &&
                                item.pl.value === e.pl.value;
                        });
                                if (events.length === 0) {
                        p.push(item);
                        }
                        var select_option_data = '';
                                select_option_data += '<option value=1></option>'
                                $.each(p, function (key, unique) {
                                select_option_data += '<option value="' + unique.pu.value + '">' + unique.pl.value + '</option>'

                                });
                                $("#idSelect_p")
                                .html(select_option_data)
                                .selectpicker('refresh');
                        }
                        }




                        if ($('#idSelect_t').attr('disabled') != 'disabled') {//tc = cu

                        //for this ToolCategory  => set up  Tool
                        if (condition_q) {
                        var events = $.grep(t, function (e) { //cu = tc
                        return item.tu.value === e.tu.value &&
                                item.tl.value === e.tl.value;
                        });
                                if (events.length === 0) {
                        t.push(item);
                        }
                        var select_option_data = '';
                                select_option_data += '<option value=1></option>'
                                $.each(t, function (key, unique) {
                                select_option_data += '<option value="' + unique.tu.value + '">' + unique.tl.value + '</option>'

                                });
                                $("#idSelect_t")
                                .html(select_option_data)
                                .selectpicker('refresh');
                        }
                        }



                        if ($('#idSelect_a').attr('disabled') != 'disabled') {
                        // alert('unlock_aaaaaaaaa')

                        //for this ToolCategory => set up Action
                        if (condition_q) {
                        var events = $.grep(a, function (e) {
                        return item.au.value === e.au.value &&
                                item.al.value === e.al.value;
                        });
                                if (events.length === 0) {
                        a.push(item);
                        }
                        var select_option_data = '';
                                select_option_data += '<option value=1></option>'
                                $.each(a, function (key, unique) {
                                select_option_data += '<option value="' + unique.au.value + '">' + unique.al.value + '</option>'

                                });
                                $("#idSelect_a")
                                .html(select_option_data)
                                .selectpicker('refresh');
                        }
                        }
















                        });
                        /*  console.log(select_option_data_PU);
                         console.log(select_option_data_TU);
                         console.log(select_option_data_AU);
                         */
                }
        );
//console.log('====>' + resultJson.length);








}





/*
 
 
 
 
 */




