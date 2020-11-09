
//TC
$.get("json/rdf.json",
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
//T

$.get("json/rdf.json",
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



$.get("json/rdf.json",
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



    
$.get("json/rdf.json",
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


$('#tc_lock').css('visibility', 'hidden');
$('#t_lock').css('visibility', 'hidden');
$('#p_lock').css('visibility', 'hidden');
$('#a_lock').css('visibility', 'hidden');



