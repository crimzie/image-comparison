package com
package crimzie
package imagecomparator

import java.io.{File, IOException}
import javax.imageio.ImageIO

import scala.util.{Failure, Success, Try}

object CLI extends App {
  private val init = System.currentTimeMillis()
  Try {
    val pic1 = new File(args(0))
    val pic2 = new File(args(1))
    (pic1, pic2)
  } match {
    case Failure(_) => println(s"Not enough parameters.")
    case Success((pic1, pic2)) =>
      val p2Path = pic2.getPath
      val out = new File(p2Path.take(p2Path.lastIndexOf('/') + 1) + "diffs.png")
      ImageComparator highlightedDiffs(pic1, pic2) match {
        case Failure(e: IOException) => println(s"Failed to read a file: ${e.getMessage}")
        case Failure(_: AssertionError) => println("Images dimensions do not match.")
        case Failure(e) => println(s"Unknown error: ${e.getMessage}")
        case Success(bi) =>
          ImageIO write(bi, "PNG", out)
          println(s"Done in ${System.currentTimeMillis - init} ms.")
      }
  }
}
