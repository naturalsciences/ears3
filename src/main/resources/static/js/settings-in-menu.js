function initSelectpicker(selector) {
    $(document).ready(function() {
    $(selector).val($(selector).attr('value'));
    $(selector).selectpicker('refresh');
})
}