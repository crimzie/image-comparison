package com.crimzie.misc

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import scala.annotation.tailrec


class ImageComparator(fileA: File, fileB: File) {

  case class Edges(top: Int, bottom: Int, left: Int, right: Int) {
    def |+|(that: Edges) = Edges(
      math min(this.top, that.top),
      math max(this.bottom, that.bottom),
      math min(this.left, that.left),
      math max(this.right, that.right))
  }

  val picA = ImageIO read fileA
  val picB = ImageIO read fileB
  val height = picA getHeight()
  val width = picA getWidth()
  if (height != picB.getHeight() || width != picB.getWidth()) ()

  // TODO: exit point

  def getHighlightedDiffs(): BufferedImage = {
    val diffs = (for (h <- 1 to height; w <- 1 to width) yield (h, w)) filter {
      case (h, w) =>
        val pA = new Color(picA getRGB(h, w))
        val pB = new Color(picB getRGB(h, w))
        (math abs pA.getAlpha + pB.getAlpha) + (math abs pA.getRed + pB.getRed) +
          (math abs pA.getGreen + pB.getGreen) + (math abs pA.getBlue + pB.getBlue) > 102 // TODO: bitwise
    }
    @tailrec
    def findClusters(pixels: Seq[(Int, Int)], clusters: Seq[Edges] = Seq()): Seq[Edges] = {
      if (pixels.isEmpty) return clusters
      val (hor, vrt) = pixels.head
      val (newCluster, clPixels) = collectCluster(Edges(hor, hor, vrt, vrt), pixels, Nil, Edges(height, 0, width, 0))
      @tailrec
      def collectCluster(edges: Edges, seq: Seq[(Int, Int)], offPixels: Seq[(Int, Int)], cluster: Edges): (Edges, Seq[(Int, Int)]) = {
        val step = 4
        val cl = seq filter {
          case (x, y) =>
            (x - edges.top > -step || x + edges.bottom < step) && (y - edges.left > -step || y + edges.right < step)
        }
        if (cl.isEmpty) return (cluster, offPixels)
        val hor = cl.map(_._1)
        val vrt = cl.map(_._2)
        val newEdges = Edges(hor.min, hor.max, vrt.min, vrt.max)
        collectCluster(newEdges, seq diff cl, offPixels ++ cl, cluster |+| newEdges)
      }
      findClusters(pixels diff clPixels, clusters :+ newCluster)
    }
    val graph = picA.createGraphics()
    graph.setPaint(Color.RED)
    findClusters(diffs) foreach { case Edges(top, bottom, left, right) =>
      graph.drawRect(left, top, right - left, bottom - top)
    }
    picA
  }
}