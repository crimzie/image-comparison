package com
package crimzie
package imagecomparator

import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D}
import java.io.File
import javax.imageio.ImageIO

import scala.annotation.tailrec
import scala.util.Try

object ImageComparator {

  private case class Edges(left: Int, right: Int, top: Int, bottom: Int) {
    def |+|(that: Edges): Edges = Edges(
      math min(this.left, that.left),
      math max(this.right, that.right),
      math min(this.top, that.top),
      math max(this.bottom, that.bottom))
  }

  /* Aggregate pixels into groups by distances between them: */
  @tailrec
  private def findClustersEdges(pixels: Seq[(Int, Int)], step: Int, clusters: Seq[Edges] = Seq()): Seq[Edges] = {
    if (pixels.isEmpty) return clusters
    val (hor, vrt) = pixels.head

    @tailrec
    def collectCluster(
                        edges: Edges,
                        seq: Seq[(Int, Int)],
                        offPixels: Seq[(Int, Int)] = Nil): (Edges, Seq[(Int, Int)]) = {
      val cl = seq filter { case (x, y) =>
        x - edges.left >= -step && x - edges.right <= step && y - edges.top >= -step && y - edges.bottom <= step
      }
      if (cl.isEmpty) return (edges, offPixels)
      val (hor, vrt) = cl.unzip
      val newEdges = Edges(hor.min, hor.max, vrt.min, vrt.max)
      collectCluster(edges |+| newEdges, seq diff cl, offPixels ++ cl)
    }

    val (newCluster, clPixels) = collectCluster(Edges(hor, hor, vrt, vrt), pixels)
    findClustersEdges(pixels diff clPixels, step, clusters :+ newCluster)
  }

  /**
    * Method takes two <code>File</code> containing pictures of same dimensions
    * and returns a copy of <code>fileB</code> as <code>BufferedImage</code> with differing
    * areas of at least 10% color difference highlighted
    * with red rectangles. The <code>step</code> value defines maximum distance
    * between differing pixels to be considered in the same area (higher value
    * increases processing speed, default value is 10).
    *
    * @param fileA first image file.
    * @param fileB second image file, which will be returned marked.
    * @param step  distance in pixels to be distinguishing for separate areas of differing pixels.
    * @return a <code>BufferedImage</code> with marked differences.
    **/
  def highlightedDiffs(fileA: File, fileB: File, step: Int = 10): Try[BufferedImage] = Try {
    val picA = ImageIO read fileA
    val picB = ImageIO read fileB
    val picAw = picA.getWidth
    val picAh = picA.getHeight
    assert(picAw == picB.getWidth && picAh == picB.getHeight)
    /* Compare images pixel by pixel and collect the coordinates of pixels differing by more than 10%: */
    val diffs: Seq[(Int, Int)] = for {
      w <- 0 until picAw
      h <- 0 until picAh
      pA = picA getRGB(w, h)
      pB = picB getRGB(w, h)
      if (math abs (pA >> 24 & 0xff) - (pB >> 24 & 0xff)) + (math abs (pA >> 16 & 0xff) - (pB >> 16 & 0xff)) +
        (math abs (pA >> 8 & 0xff) - (pB >> 8 & 0xff)) + (math abs (pA & 0xff) - (pB & 0xff)) > 102
    } yield (w, h)
    val graph: Graphics2D = picB createGraphics()
    graph setPaint Color.RED
    for {Edges(left, right, top, bottom) <- findClustersEdges(diffs, step)}
      graph drawRect(left - 1, top - 1, right - left, bottom - top)
    picB
  }
}
