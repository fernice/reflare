package fernice.reflare

import fernice.std.Option
import org.fernice.reflare.element.into
import java.awt.Component
import java.io.File
import javax.swing.JFrame

val Component.classes: MutableList<String>
    get() = this.into().classes

var Component.id: Option<String>
    get() = this.into().id
    set(value) {
        this.into().id = value
    }

fun <E> MutableList<E>.addAll(vararg values: E) {
    for (value in values) {
        this.add(value)
    }
}

@Deprecated(
        message = "Stylesheets are no longer bound to a Frame",
        replaceWith = ReplaceWith("CSSEngine.addStylesheetResource(resource)", "fernice.reflare.CSSEngine")
)
fun JFrame.addStylesheetResource(resource: String) {
    CSSEngine.addStylesheetResource(resource)
}

@Deprecated(
        message = "Stylesheets are no longer bound to a Frame",
        replaceWith = ReplaceWith("CSSEngine.addStylesheet(file)", "fernice.reflare.CSSEngine")
)
fun JFrame.addStylesheet(file: File) {
    CSSEngine.addStylesheet(file)
}

@Deprecated(
        message = "Stylesheets are no longer bound to a Frame",
        replaceWith = ReplaceWith("CSSEngine.removeStylesheet(file)", "fernice.reflare.CSSEngine")
)
fun JFrame.removeStylesheet(file: File) {
    CSSEngine.removeStylesheet(file)
}