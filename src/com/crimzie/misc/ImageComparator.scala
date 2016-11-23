package com.crimzie.misc

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.{File, IOException}
import javax.imageio.ImageIO

import scala.annotation.tailrec

object ImageComparator {
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
    * @param step distance in pixels to be distinguishing for separate areas of differing pixels.
    * @return a <code>BufferedImage</code> with marked differences.
    * @throws IOException if an error occurs during reading the files.
    * */
  def getHighlightedDiffs(fileA: File, fileB: File, step: Int = 10): BufferedImage = {
    case class Edges(left: Int, right: Int, top: Int, bottom: Int) {
      def |+|(that: Edges) = Edges(
        math min(this.left, that.left),
        math max(this.right, that.right),
        math min(this.top, that.top),
        math max(this.bottom, that.bottom))
    }
    val picA = ImageIO read fileA
    val picB = ImageIO read fileB
    val height = picA getHeight()
    val width = picA getWidth()
    val diffs = (for (w <- 1 to width; h <- 1 to height) yield (w, h)) filter {
      case (w, h) =>
        val pA = picA getRGB(w - 1, h - 1)
        val pB = picB getRGB(w - 1, h - 1)
        (math abs (pA >> 24 & 0xff) - (pB >> 24 & 0xff)) + (math abs (pA >> 16 & 0xff) - (pB >> 16 & 0xff)) +
          (math abs (pA >> 8 & 0xff) - (pB >> 8 & 0xff)) + (math abs (pA & 0xff) - (pB & 0xff)) > 102
    }
    @tailrec
    def findClusters(pixels: Seq[(Int, Int)], clusters: Seq[Edges] = Seq()): Seq[Edges] = {
      if (pixels.isEmpty) return clusters
      val (hor, vrt) = pixels.head
      @tailrec
      def collectCluster(edges: Edges, seq: Seq[(Int, Int)], offPixels: Seq[(Int, Int)]): (Edges, Seq[(Int, Int)]) = {
        val cl = seq filter { case (x, y) =>
          x - edges.left >= -step && x - edges.right <= step && y - edges.top >= -step && y - edges.bottom <= step
        }
        if (cl.isEmpty) return (edges, offPixels)
        val hor = cl map { case (x, _) => x }
        val vrt = cl map { case (_, y) => y }
        val newEdges = Edges(hor.min, hor.max, vrt.min, vrt.max)
        collectCluster(edges |+| newEdges, seq diff cl, offPixels ++ cl)
      }
      val (newCluster, clPixels) = collectCluster(Edges(hor, hor, vrt, vrt), pixels, Nil)
      findClusters(pixels diff clPixels, clusters :+ newCluster)
    }
    val graph = picB.createGraphics()
    graph.setPaint(Color.RED)
    findClusters(diffs) foreach { case Edges(left, right, top, bottom) =>
      graph.drawRect(left - 1, top - 1, right - left, bottom - top)
    }
    picB
  }
}