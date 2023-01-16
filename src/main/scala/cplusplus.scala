import space.bird14.cp_tool.common.*
import scala.util.*
import java.io.*

package space.bird14.cp_tool.lan:
  abstract class CPlusPlus extends Language:
    def beforeSumit(problem: String, answer: String): Try[File] = ???
    def run(runPath: String, inputFile: String, outputFile: String, timeLimit: Int = 1000, 
      memoryLimit: Int = 512):  Try[File] = ???
    def support(srcFile: String): Boolean = 
      srcFile.split(File.separator).tail.endsWith(List("c","cpp","cc"))

  class Clang extends CPlusPlus :
    def compile(srcFile: String, templatePath: String): Try[File] = ???
