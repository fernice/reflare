# reflare

reflare is a Look&Feel for Java Swing that introduces styling with CSS. reflare enables the styling of components using stylesheets. Like in browsers one or
more stylesheets can be defined to style the whole application. This allows customizing the global appearance of an application without the need to write custom
code.

reflare is designed to both being employed as the primary Look&Feel in new projects and being integrated into existing projects with an already defined
Look&Feel.

### Look&Feel Mode

Like any other, reflare can be installed as a primary Look&Feel via the `UIManager`. When reflare is used as the primary Look&Feel, you can simply use all
regular component classes. However, it is recommended that you use the components classes provided by this Look&Feel instead, as they may provide additional
functionality. Refer to embedded mode for more information on the provided component classes.

```kotlin
FlareLookAndFeel.install()
// or
UIManager.setLookAndFeel(FlareLookAndFeel())
```

reflare should be used as the primary Look&Feel in new or very small existing projects. Introducing a new Look&Feel into an existing code base can be highly
disruptive for both the developer AND the user. Consider using reflare in embedded mode instead.

### Embedded Mode

reflare can also be embedded into an already installed Look&Feel. In this case no Look&Feel installation is required to any of the components. All component
classes provided by reflare will automatically initialize and use the embedded Look&Feel without interfering with the current Look&Feel outside their own scope.
All non-provided component classes will appear and behave according to the host Look&Feel.

The provided component classes can be found in the `fernice.reflare.light` package, and they all start with a capital `F` in place of the capital `J`.

The embedded mode is ideal for use in already existing code bases. This allows implementing new parts and reimplementing old portions gradually without
interrupting or breaking existing code and functionalities.

### Stylesheets

Stylesheet acts as global source for style rules. The whole application is comparable to a single browser tab. All stylesheets are managed by the `CSSEngine`.
It supports both stylesheet resources and files.

```kotlin
// adding and removing resources
CSSEngine.addStylesheetResource("/index.css")
CSSEngine.removeStylesheetResource("/index.css")
// adding and removing files
CSSEngine.addStylesheet(File("~/.application/index.css"))
CSSEngine.removeStylesheet(File("~/.application/index.css"))
```

Stylesheets can be reloaded from file while the application is running. This can be useful during development in order to minimize application restarts. This
generally works for all file-based stylesheets and might also work with resource-based stylesheets, if they're sourced from e.g. a target directory.

```kotlin
CSSEngine.reloadStylesheets()
```

Adding, removing or reloading stylesheets will automatically trigger restyle of the whole application and therefore will propagate all changes directly.

### Styling Components

Components can be affected through the following three mechanisms in order of precedence:

Style Attributes: Each component can be assigned a style rule directly through the `style` property. This will only affect the component itself. Direct and
indirect children may be affected too, in case of inheritable properties, but only properties applied to the component itself take precedence over the other
mechanisms.

Style Rules: Style rules defined in the stylesheets can define selectors to match one or more components by certain predefined attributes. The most important
attributes are namespaces, classes and ids. It is also possible to match special states of components like when the mouse hovering over the component. The style
rules take precedence by their specificity, and the order they appear in.

Inheritance: The second mechanism is inheritance. Components can inherit certain properties from their parents, if they're not specified by one of the other two
mechanisms. Which properties can be inherited, is defined by the CSS specification. Inheritance is a powerful tool for setting the general font globally by
applying it to the root element.

There are extension functions defined for accessing these additional attributes of a component.

```kotlin
val button = JButton()

// Style Attributes
button.style = "background: white;"

// Style Rules
// button.namespace is defined as 'button'
button.id = "submit"
button.classes.add("outlined-button")
```

### Usage

Declare the following dependency:

```
<dependency>
    <groupId>org.fernice</groupId>
    <artifactId>fernice-reflare</artifactId>
    <version>${fernice.version}</version>
</dependency>
```

reflare (sadly) has to make use of some internal jdk classes and is therefore dependent on the JDK version. In order to accommodate for this, reflare requires
an auxiliary dependency.

JDK 8:

```
<dependency>
    <groupId>org.fernice</groupId>
    <artifactId>fernice-reflare-access-jdk-8</artifactId>
    <version>${fernice.version}</version>
</dependency>
```

JDK 10 or higher:

```
<dependency>
    <groupId>org.fernice</groupId>
    <artifactId>fernice-reflare-access-jdk-10</artifactId>
    <version>${fernice.version}</version>
</dependency>
```

JDK 9 is not supported.

### Example

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

val button = JButton()
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

### Supported Properties

reflare supports nearly all the visual properties such as `font`, `background` or `border` in both parsing and rendering. A full list of all currently supported
properties can be found [here](SUPPORTED.md).