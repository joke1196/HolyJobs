$(document).ready(function() {
    $('[data-toggle="tooltip"]').tooltip();

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
                $("#search-jobs-button").attr("data-original-title", "Search for jobs.");
                $("#search-jobs-button").tooltip('hide');
                $("#home-page-bottom").empty();

                // Checks if there is jobs for the given parameters.
                if (msg.jobs.length) {
                    $("#no-result-panel").removeClass("no-result-panel-visible");

                    for (var i = 0; i < msg.jobs.length; ++i) {
                        var currentRowNumber = Math.floor(i / 4) + 1;

                        if (i % 4 == 0) {
                            $("#home-page-bottom").append('<div class="job-row" id="job-row-' + currentRowNumber + '"></div>');
                        }

                        $("#job-row-" + currentRowNumber).append(
                            '<div class="job-element">\
                                <div class="job-element-content">\
                                    <img alt="job1" class="job-element-image" src="/assets/images/jobs/' + msg.jobs[i].image + '" />\
                                    <div class="job-element-title">\
                                        <strong>' + msg.jobs[i].name + '</strong>\
                                    </div>\
                                </div>\
                            </div>'
                        );
                    }

                    // Scroll the screen down.
                    $("html, body").animate({
                        "scrollTop": $("#home-bottom").position().top
                    }, 1000);
                // Otherwise shows an information message.
                } else {
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
                }
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

function hideNoResultPanel() {
    $("#no-result-panel").removeClass("no-result-panel-visible");
}
