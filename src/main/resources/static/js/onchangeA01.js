$("#idSelect_a").change(function ()  {
       // ////alert( $( "#idSelect_tc" ).val());   
      
    var arraySelectedValues = [];
    x = -1;
    var cu ='';
    var tu ='';
    var pu='';
    var au='';

    var cu_q ='';
    var tu_q ='';
    var pu_q='';
    var au_q='';

    var condition ='';
    var condition_q ='';
    var startCondition="(";
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

       var endCondition=")";
   //   console.log('concat'+startCondition.concat(cu,tu, pu,au,endCondition));

      condition=startCondition.concat(cu,tu, pu,au,endCondition).replace(/&&([^'&&']*)$/, '' + '$1');

      condition_q=startCondition.concat(cu_q,tu_q, pu_q,au_q,endCondition).replace(/&&([^'&&']*)$/, '' + '$1');

       var resultJson =[];    

 //console.log(condition);
  $.getJSON("json/rdf.json",
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
//21092020 //alert(condition_q);

//(element.cu.value  == 'http://ontologies.ef-ears.eu/ears2/1#ctg_55'  && element.au.value  == 'http://ontologies.ef-ears.eu/ears2/1#act_2'   )
   //(item.cu.value == $("#idSelect_tc").val()) 
   
   




   ////alert(condition_q);


  //   console.log('====--------------------->' + resultJson.length);
     $.each(resultJson, function (key, item) {            
        aa +=  'CU'+'--'+item.cl.value+'--'+ item.tl.value +'--'+item.pl.value  +'--'+item.al.value+ '</br>'
        select_option_data_PU += '<option value="' + item.pu.value + '">' + item.pl.value + '</option>'
        select_option_data_TU += '<option value="' + item.tu.value + '">' + item.tl.value + '</option>'        
        select_option_data_AU += '<option value="' + item.au.value + '">' + item.al.value + '</option>'    
        
       // 

        if ($('#idSelect_tc').attr('disabled') != 'disabled') {//mettre à jour donc avec ql condition
            //for this ToolCategory => set up Process
            if ( condition_q ) {
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
    
    
    
    
                 
    
                    if ($('#idSelect_a').attr('disabled') != 'disabled') {
    
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




     
     if ($('#idSelect_tc').attr('disabled') != 'disabled') {
        if ($('#idSelect_tc option').length == 2) {
            $("#idSelect_tc")
                .html($("#idSelect_tc").find('option').not(':empty()').first().attr('selected', true))
                .selectpicker('refresh');

            $('#idSelect_tc').prop('disabled', true);
            $('#idSelect_tc').selectpicker('refresh');

            $('#tc_lock').css('visibility', 'visible');
            $('#tc_unlock').css('visibility', 'hidden');

        }
    }



    if ($('#idSelect_t').attr('disabled') != 'disabled') {
        if ($('#idSelect_t option').length == 2) {
            $("#idSelect_t")
                .html($("#idSelect_t").find('option').not(':empty()').first().attr('selected', true))
                .selectpicker('refresh');

            $('#idSelect_t').prop('disabled', true);
            $('#idSelect_t').selectpicker('refresh');



            $('#t_lock').css('visibility', 'visible');
            $('#t_unlock').css('visibility', 'hidden');

        }

    }

    if ($('#idSelect_p').attr('disabled') != 'disabled') {
        if ($('#idSelect_p option').length == 2) {
            $("#idSelect_p")
                .html($("#idSelect_p").find('option').not(':empty()').first().attr('selected', true))
                .selectpicker('refresh');

            $('#idSelect_p').prop('disabled', true);
            $('#idSelect_p').selectpicker('refresh');



            $('#p_lock').css('visibility', 'visible');
            $('#p_unlock').css('visibility', 'hidden');

        }
    }









    if ($('#idSelect_a').attr('disabled') != 'disabled') {

        if ($('#idSelect_a option').length == 2) {
            $("#idSelect_a")
                .html($("#idSelect_a").find('option').not(':empty()').first().attr('selected', true))
                .selectpicker('refresh');

            $('#idSelect_a').prop('disabled', true);
            $('#idSelect_a').selectpicker('refresh');



            $('#a_lock').css('visibility', 'visible');
            $('#a_unlock').css('visibility', 'hidden');

        }

    }



   /*  console.log(select_option_data_PU);
     console.log(select_option_data_TU);
     console.log(select_option_data_AU);
     */
 }    
);
//console.log('====>' + resultJson.length);


    $('#idSelect_a').prop('disabled', true); 
    $('#idSelect_a').selectpicker('refresh');

      

    $('#a_lock').css('visibility', 'visible');
    $('#a_unlock').css('visibility', 'hidden');




});