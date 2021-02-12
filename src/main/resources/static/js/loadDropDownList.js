var sortByString = function (a, b)
{
    var one = a.label;
    var two = b.label;
    // a and b will here be two objects from the array
    // thus a[1] and b[1] will equal the names

    // if they are equal, return 0 (no sorting)
    if (one == two) {
        return 0;
    }
    if (one > two)
    {
        // if a should come after b, return 1
        return 1;
    } else
    {
        // if b should come after a, return -1
        return -1;
    }
};

function getElement(what, item) {
    switch (what) {
        case 'TC':
            return {url: item.cu.value, label: item.cl.value};
            break;
        case 'T':
            return {url: item.tu.value, label: item.tl.value};
            break;
        case 'P':
            return {url: item.pu.value, label: item.pl.value};
            break;
        case 'A':
            return {url: item.au.value, label: item.al.value};
            break;
        default:
            return null;
    }
}

var loadDropdownList = function (data, entityName, dropdownId) {
    var bb = [];
    var select_option_data = '';
    $.each(data.results.bindings, function (key, item) {
        val = getElement(entityName, item);
        var events = $.grep(bb, function (e) {
            return val.url === e.url &&
                    val.label === e.label;
        });
        if (events.length === 0) {
            bb.push(val);
        }
    });
    if (/Android|webOS|iPhone|iPad|iPod|BlackBerry/i.test(navigator.userAgent)) {
        $(dropdownId).selectpicker('mobile');
    }
    if (bb.length <= 6) {
        console.log("bb<6");
        $(dropdownId).selectpicker({
            liveSearch: false
        }).selectpicker('refresh');
    }
    bb.sort(sortByString);
    var select_option_data = '';
    select_option_data += '<option value=1></option>'
    $.each(bb, function (key, unique) {
        select_option_data += '<option value="' + unique.url + '">' + unique.label + '</option>'
    });
    $(dropdownId)
            .html(select_option_data)
            .selectpicker('refresh');
}

function loadDropdownLists(jsonRdfLocation) {
    $.getJSON(jsonRdfLocation, function (data) {
        loadDropdownList(data, "TC", "#idSelect_tc");
        loadDropdownList(data, "T", "#idSelect_t");
        loadDropdownList(data, "P", "#idSelect_p");
        loadDropdownList(data, "A", "#idSelect_a");
    });

    $('#tc_lock').css('visibility', 'hidden');
    $('#t_lock').css('visibility', 'hidden');
    $('#p_lock').css('visibility', 'hidden');
    $('#a_lock').css('visibility', 'hidden');
}





