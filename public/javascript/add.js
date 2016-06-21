$(document).ready(function() {
    $('[data-toggle="tooltip"]').tooltip();

    // We used the nazar-pc's PickMeUp plugin (https://github.com/nazar-pc/PickMeUp)
    // in order to display a nice calendar.
    $('#search-when').pickmeup({
		position		: 'bottom',
		hide_on_select	: true
	});
    
    $('#startDate, #endDate').pickmeup({
        format          : 'Y-m-d',
		hide_on_select	: true,
		position		: 'bottom'
	});

    // Occurs during the header search-post's sending.
    // If fields are not valid, cancels the posting.
    $("#header-form").submit(function() {
         // Returns false to cancel form action.
        return validSearchFields().valid;
    });
})
