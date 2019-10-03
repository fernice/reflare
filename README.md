# reflare

reflare is a Look-and-Feel for Java Swing that introduces styling with CSS. reflare makes styling of components using stylesheets possible. Like in browser one
or more stylesheets can be defined to style the whole application. With the help of Kotlin, reflare integrates into any application even deeper.

reflare supports both being the primary Look-and-Feel as well as being a integrated into an already existing Look-and-Feel, making migrations even easier.
Support for integration is provided through the components under `fernice.reflare.light`.

# Example

```kotlin
// install the look-and-feel
FlareLookAndFeel.install()

// add your stylesheet
CSSEngine.addStylesheetResource("/index.css")

// create a frame
val frame = JFrame()
frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

val contentPane = JPanel()

val textField = JTextField()
contentPane.add(textField)

val button = Button()
contentPane.add(button)

// extension functions and properties
// css id
button.id = "submit"
// css class
button.classes.add("outlined-button")
// css inline style block
button.style = "background: white;"    

frame.contentPane = contentPane

// make the frame visible
frame.isVisible = true
```