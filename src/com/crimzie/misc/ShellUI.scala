package com.crimzie.misc

import java.io.File
import javax.imageio.ImageIO

object ShellUI extends App {
  val init = System.currentTimeMillis()
  ImageIO write(ImageComparator getHighlightedDiffs(new File(args(0)), new File(args(1))), "PNG", new File(args(1)))
  println(s"Done in ${System.currentTimeMillis - init}")
}