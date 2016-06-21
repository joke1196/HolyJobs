$(document).ready(function() {
    $('[data-toggle="tooltip"]').tooltip();

    // We used the nazar-pc's PickMeUp plugin (https://github.com/nazar-pc/PickMeUp)
    // in order to display a nice calendar.
    $('#search-when').pickmeup({
		position		: 'bottom',
		hide_on_select	: true
	});

    // Occurs during the header search-post's sending.
    // If fields are not valid, cancels the posting.
    $("#header-form").submit(function() {
         // Returns false to cancel form action.
        return validSearchFields().valid;
    });

    // Initializes the "apply" dialog in the details action.
    $("#dialog-apply").dialog({
        autoOpen: false,
        buttons: [
            {
                text: "Cancel",
                click: function() {
                    $(this).dialog("close");
                }
            },
            {
                text: "Apply!",
                click: function() {
                    $("#btn-submit-apply").click();
                }
            }
        ],
        closeOnEscape: true,
        dialogClass: "no-close",
        minWidth: 800,
        modal: true
    });

    // Shows the "apply" dialog when the user clicks on the "Apply!" button.
    $("#btn-apply").click(function() {
        $("#dialog-apply").dialog("open");
    });
})
