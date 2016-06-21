var numberMaxOfJobsPerLine = 4;

// Validates search fields and returns their values as a JSON object.
// Also returns the validation's status.
function validSearchFields() {
    var whereValue = $("#search-where").val();
    var whenValue = $("#search-when").val();
    var whatValue = $("#search-what").val();
    var valid = true;

    // Hides the error tooltip if the user selected a location.
    if (whereValue) {
        $("#search-where").tooltip('hide');
    // Otherwise shows it and indicates the system the fields are
    // not valid.
    } else {
        $("#search-where").tooltip('show');
        valid = false;
    }

    if (whenValue) {
        $("#search-when").tooltip('hide');
    } else {
        $("#search-when").tooltip('show');
        valid = false;
    }

    if (whatValue) {
        $("#search-what").tooltip('hide');
    } else {
        $("#search-what").tooltip('show');
        valid = false;
    }

    return {
        "valid": valid,
        "whereValue": whereValue,
        "whenValue": whenValue,
        "whatValue": whatValue
    };
}
