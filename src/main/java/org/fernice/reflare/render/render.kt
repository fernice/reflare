package org.fernice.reflare.render

import java.awt.Image


internal val Image.height: Int
    get() = this.getHeight(null)


internal val Image.width: Int
    get() = this.getWidth(null)