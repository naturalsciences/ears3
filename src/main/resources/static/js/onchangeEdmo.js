$("#id_Organisation").change(function () {
    var edmo = '';
    var condition = '';
    var condition_q = '';
    var startCondition = "(";
    if ($("#id_Organisation").val() !== 1) {
        edmo = "element.rpOrgName.SDNIdent  == " + "'" + $("#id_Organisation").val() + "'" + "  && ";
    }
    var endCondition = ")";
    condition = startCondition.concat(edmo, endCondition).replace(/&&([^'&&']*)$/, '' + '$1');

    //console.log(condition);
    $.getJSON("json/edmo.json",
            function (data) {
                $(data.vocabulary.citRespParty).each(function (index, element) {
                    if (eval(condition)) {
                        //alert(element.rpOrgName.text + '  ' + element.rpOrgName.SDNIdent+'  '+element.rpCntInfo.cntPhone.voiceNum + '  ' + element.rpCntInfo.cntPhone.faxNum+'  '+element.rpCntInfo.cntAddress.eMailAdd);   

                        $("#id_PhoneNumber")
                                .val(element.rpCntInfo.cntPhone.voiceNum);
                        $("#id_FaxNumber")
                                .val(element.rpCntInfo.cntPhone.faxNum);
                        $('#id_Email').val(element.rpCntInfo.cntAddress.eMailAdd);
                    }
                });
            }
    );
});
$(".edmoOrganisation").change(function () {
    $("#id_Organisationp").val('');
    $("#id_PhoneNumberp").val('');
    $("#id_FaxNumberp").val('');
    $('#id_Emailp').val('');
});
/*
 $(".otherOrganisation").change(function () {
 $.getJSON("http://www.bmdc.be/NODC/html/Eurofleet/edmo.json",
 function (data) {
 var select_option_data = '';
 select_option_data += '<option value=1></option>'
 $.each(data.vocabulary.citRespParty, function (key, item) {     //": [        
 select_option_data += '<option value="' + item.rpOrgName.SDNIdent + '">' + item.rpOrgName.text + '</option>'
 
 });
 $("#id_Organisation")
 .html(select_option_data)
 .selectpicker('refresh');
 
 }
 );
 $("#id_PhoneNumber").val('');
 $("#id_FaxNumber").val('');
 $('#id_Email').val('');
 });*/