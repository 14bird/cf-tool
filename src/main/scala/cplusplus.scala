import space.bird14.cp_tool.common.*
import scala.util.*
import java.io.*
import java.lang.Runtime

package space.bird14.cp_tool.lan:
  abstract class CPlusPlus extends Language:
    def beforeSumit(problem: String, answer: String): Try[File] = ???
    def run(runPath: String, inputFile: String, outputFile: String, timeLimit: Int = 1000, 
      memoryLimit: Int = 512):  Try[File] = ???
    def support(srcFile: String): Boolean = 
      srcFile.split(File.separator).tail.endsWith(List("c","cpp","cc"))

  object Clang extends CPlusPlus :
    def compile(srcFile: String, templatePath: String): Try[File] = 
      val run = Runtime.getRuntime()
      val target = srcFile.split(File.separator).dropRight(1).mkString(File.separator)
      var stdout: String = ""
      val findLinkFiles = s"find $templatePath -name *.o"
      var process = run.exec(findLinkFiles.split(" "))
      var errout: String = ""
      Using(new BufferedInputStream(process.getErrorStream())){bis => errout = String(bis.readAllBytes())}
      Using(new BufferedInputStream(process.getInputStream())){bis => stdout = String(bis.readAllBytes())}
      val linkFiles = stdout.replaceAllLiterally(System.lineSeparator(), " ")
      val command = s"clang++ ${linkFiles} -I ${templatePath} ${srcFile} -o ${target}/main"
      process = run.exec(command)
      
      Using(new BufferedInputStream(process.getInputStream())){bis => stdout = String(bis.readAllBytes())}
      Using(new BufferedInputStream(process.getErrorStream())){bis => errout = String(bis.readAllBytes())}
      
      if stdout.length > 0 then 
        return Failure(java.lang.RuntimeException(s"fail to compile, because $stdout"))
      else if errout.length > 0 then
        return Failure(java.lang.RuntimeException(s"fail to compile, because $errout"))
      try
        val src = File(srcFile)
        Success(File(src.getParent(), "main"))
      catch
        case e: Exception => Failure(e)
    
