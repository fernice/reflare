package modern.reflare.util

import java.awt.Image
import javax.imageio.ImageIO
import javax.swing.ImageIcon

internal fun getScaledIconResource(resource: String, width: Int, height: Int): ImageIcon {
    val image = ImageIO.read(ImageHelper::class.java.getResourceAsStream(resource))
    val scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH)

    return ImageIcon(scaledImage)
}