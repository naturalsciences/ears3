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