## Image comparison software
<p>The com.crimzie.misc.ImageComparator object defines one public method that takes two java.io.File objects and compares them on the 
base of color-differing pixels, then searches for clusters of such 
pixels and returns a copy of the second image as java.awt.image.BufferedImage
with differing areas highlighted with red rectangles.</p>
<p>The ShellUI object is the main class for usage of the software as a command line tool.
The jar file takes two image files as arguments and overwrites the second 
file supplied with its copy with highlights.