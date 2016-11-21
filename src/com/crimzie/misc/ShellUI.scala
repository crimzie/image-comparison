package com.crimzie.misc

import java.io.File
import javax.imageio.ImageIO

object ShellUI extends App {
  ImageIO write (new ImageComparator(new File(args(0)), new File(args(1))).getHighlightedDiffs(), "PNG", new File("/new.png"))
}
