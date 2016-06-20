var numberMaxOfJobsPerLine = 4;

$(document).ready(function() {
    $('[data-toggle="tooltip"]').tooltip();

    // We used the nazar-pc's PickMeUp plugin (https://github.com/nazar-pc/PickMeUp)
    // in order to display a nice calendar.
    $('#search-when').pickmeup({
		position		: 'top',
		hide_on_select	: true
	});

    // Animate the home page when the user scrolls.
    $(window).on("scroll", function () {
        var homeBottomTop = $("#home-bottom").position().top - $("#home-title").height();
        var titleOpacity = ($(window).scrollTop() / homeBottomTop);
        var percentOfScrollingToBottom = ($(window).scrollTop() / $("#home-bottom").position().top);
        var homeTitleBottomMiddlePosition = ($("#home-page-top").height() - $("#home-bottom").position().top - $("#home-title-bottom").height()) / 2;

        // Move the main title to the top and make it transparent.
        $("#helper").css("height", "calc(25% + " + $(window).scrollTop() / 2.5 + "px)");
        $("#home-title").css("opacity", 1 - titleOpacity);
        // Move the second main-title to the top so it will apear to the screen.
        $("#home-title-bottom").css("bottom", "calc(" + homeTitleBottomMiddlePosition + "px - (150px + 3.8vh) * " + Math.max(1 - percentOfScrollingToBottom, 0) + ")");
    })

    // Occurs when the user clicked on the "Money, to me!" button.
    $("#search-jobs-button").click(function() {
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

        // If every field is valid send an AJAX request to get the jobs.
        if (valid) {
            $.ajax({
                method: "GET",
                url: $("#ajaxUrl").val(),
                data: {
                    region: whereValue,
                    startDate: whenValue,
                    jobType: whatValue
                }
            })
            .done(function(msg) {
                var oldHeight = $("#home-page-bottom").height();

                $("#search-jobs-button").attr("data-original-title", "Search for jobs.");
                $("#search-jobs-button").tooltip('hide');

                // Fades the bottom panel out in order to reload its content.
                $("#home-page-bottom").addClass("home-page-bottom-transparent");

                $("#home-page-bottom").one('transitionend webkitTransitionEnd oTransitionEnd otransitionend MSTransitionEnd', function() {
                    // The result panel keep the same size as before when it is
                    // cleaned, to avoid display bugs.
                    // The height is reset at the end of the process.
                    $("#home-page-bottom").css("height", oldHeight + "px");
                    $("#home-page-bottom").empty();

                    // Checks if there is jobs for the given parameters.
                    if (msg.jobs.length) {
                        $("#no-result-panel").removeClass("no-result-panel-visible");
                        $("#home-page-bottom").append('<div class="jobs-title">Woohoo, here are the jobs!</div>');

                        // Appends the jobs to the page.
                        for (var i = 0; i < msg.jobs.length; ++i) {
                            var currentRowNumber = Math.floor(i / numberMaxOfJobsPerLine) + 1;

                            // We have to add a new row each time there was three elements.
                            if (i % numberMaxOfJobsPerLine == 0) {
                                $("#home-page-bottom").append('<div class="job-row" id="job-row-' + currentRowNumber + '"></div>');
                            }

                            $("#job-row-" + currentRowNumber).append(
                                '<div class="job-element">\
                                    <a href="/details/' + msg.jobs[i].id + '">\
                                        <div class="job-element-content">\
                                                <img alt="job1" class="job-element-image" src="/assets/images/jobs/' + msg.jobs[i].image + '" />\
                                            <div class="job-element-title">\
                                                <strong>' + msg.jobs[i].name + '</strong>\
                                            </div>\
                                        </div>\
                                    </a>\
                                </div>'
                            );
                        }

                        // If there was no enough results to complete a whole line,
                        // we have to add "ghost jobs" in order to get a better
                        // rendering.
                        for (var i = 0; i < numberMaxOfJobsPerLine - msg.jobs.length; ++i) {
                            $("#job-row-1").append('<div class="job-element"></div>')
                        }

                        $("#home-page-bottom").css("min-height", "100vh");

                        // Scroll the screen down.
                        $("html, body").animate({
                            "scrollTop": $("#home-bottom").position().top
                        }, 1000);
                    // If there is no jobs, shows an information message.
                    } else {
                        $("#home-page-bottom").append('<h1 class="fill-fields-message">Sorry but there is no result for the given parameters 😢.</h1>');

                        // Shows the message if not already shown.
                        if (!$("#no-result-panel").hasClass("no-result-panel-visible")) {
                            $("#no-result-panel").addClass("no-result-panel-visible");
                        // Otherwise enhances the message to be sure the user saw it.
                        } else {
                            $("#no-result-panel-smiley").addClass("smiley-big");

                            $("#no-result-panel").one('transitionend webkitTransitionEnd oTransitionEnd otransitionend MSTransitionEnd', function() {
                                $("#no-result-panel-smiley").removeClass("smiley-big");
                            })
                        }

                        // Scroll the screen up.
                        $("html, body").animate({
                            "scrollTop": 0
                        }, 1000);

                        $("#home-page-bottom").css("min-height", "0");
                        $("#home-page-bottom").css("height", "auto");
                    }

                    $("#home-page-bottom").removeClass("home-page-bottom-transparent");
                });
            })
            // Shows an error message over the button in an bad request error occured.
            .fail(function(jqXHR, textStatus) {
                switch(jqXHR.responseText) {
                    case "errorField":
                        $("#search-jobs-button").attr("data-original-title", "Please select a value for all fields.");
                        break;
                    default:
                        $("#search-jobs-button").attr("data-original-title", "An error occured, please retry in a while 😢.");
                }

                $("#search-jobs-button").tooltip('show');
            });
        }
    })
})

// Hides the no result panel when the user clicked on the "Got it" button.
function hideNoResultPanel() {
    $("#no-result-panel").removeClass("no-result-panel-visible");
}
