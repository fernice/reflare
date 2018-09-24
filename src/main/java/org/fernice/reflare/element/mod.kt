package org.fernice.reflare.element

import javax.swing.SwingUtilities

fun invokeLater(runnable: () -> Unit) {
    if (SwingUtilities.isEventDispatchThread()) {
        runnable()
    } else {
        SwingUtilities.invokeLater(runnable)
    }
}

fun invokeAndWait(runnable: () -> Unit) {
    if (SwingUtilities.isEventDispatchThread()) {
        runnable()
    } else {
        SwingUtilities.invokeAndWait(runnable)
    }
}