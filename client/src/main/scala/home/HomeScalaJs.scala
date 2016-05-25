package home

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom
import org.scalajs.jquery.jQuery
import shared.SharedMessages

object HomeScalaJs extends js.JSApp {
    // This function is triggered when the user clicked the Search button of
    // the main page.
    @JSExport
    def searchForJobs(): Unit = {
        //val tmp = jQuery("#home-page-bottom").position().top;
        val tmp = dom.document.getElementById("home-page-bottom").getBoundingClientRect().top
        println(tmp)

        /*val x = jQuery("html, body").animate({
            scrollTop: @tmp
        }, 1000);*/
    }

    // Occurs on each page loading.
    def main(): Unit = {
        jQuery("body").append("<p>blablabla</p>")
        //dom.document.getElementById("scalajsShoutOut").textContent = SharedMessages.itWorks
    }
}
