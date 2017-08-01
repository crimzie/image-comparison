## Image comparison software
<p>The ImageComparator object defines one public method that takes two java.io.File objects and compares them on the 
basis of color-differing pixels, then searches for clusters of such pixels and returns a copy of the second image as 
Try[java.awt.image.BufferedImage] with differing areas highlighted with red rectangles. The method fails if either of 
the two files isn't a readable image or if images dimensions do not match.</p>
<p>The CLI object is the main class for usage of the software as a command line tool. The jar file takes two image 
files as arguments and writes a copy of the second file with differing areas highlighted by red rectangles to 
{path to second image}/diffs.png .</p>
