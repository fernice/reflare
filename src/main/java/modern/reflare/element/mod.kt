package modern.reflare.element

import de.krall.flare.std.Option
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
        replaceWith = ReplaceWith("CSSEngine.addStylesheetResource(resource)", "modern.reflare.element.CSSEngine")
)
fun JFrame.addStylesheetResource(resource: String) {
    CSSEngine.addStylesheetResource(resource)
}

@Deprecated(
        message = "Stylesheets are no longer bound to a Frame",
        replaceWith = ReplaceWith("CSSEngine.addStylesheet(file)", "modern.reflare.element.CSSEngine")
)
fun JFrame.addStylesheet(file: File) {
    CSSEngine.addStylesheet(file)
}

@Deprecated(
        message = "Stylesheets are no longer bound to a Frame",
        replaceWith = ReplaceWith("CSSEngine.removeStylesheet(file)", "modern.reflare.element.CSSEngine")
)
fun JFrame.removeStylesheet(file: File) {
    CSSEngine.removeStylesheet(file)
}
