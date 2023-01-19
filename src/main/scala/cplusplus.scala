import space.bird14.cp_tool.common.*
import scala.util.*
import java.io.*
import java.lang.Runtime

package space.bird14.cp_tool.lan:
  abstract class CPlusPlus extends Language:
    def beforeSumit(problem: String, answer: String): Try[File] = ???
    def run(runFile: File, inputFile: File, outputFile: File, timeLimit: Int = 1, 
      memoryLimit: Int = 512):  Try[Tuple2[Boolean, String]] = 
      val outId = java.util.UUID.randomUUID.toString() + System.currentTimeMillis()
        //todo limit memory
      val command: Array[String] = Array("/bin/sh", "-c", s"timeout $timeLimit ${runFile.getAbsolutePath()} < " +
        s"${inputFile.getAbsolutePath()} > /tmp/${outId}")
      println(s"/tmp/${outId}")
      println(command.mkString(" "))
      var process = Runtime.getRuntime().exec(command)
      var stdout: String = ""
      var errout: String = ""
      Using(new BufferedInputStream(process.getErrorStream())){bis => errout = String(bis.readAllBytes())}
      Using(new BufferedInputStream(process.getInputStream())){bis => stdout = String(bis.readAllBytes())}
      if !errout.isEmpty() then return Failure(RuntimeException(errout))
      process = Runtime.getRuntime().exec(s"diff ${outputFile.getAbsolutePath()} /tmp/${outId}")
      Using(new BufferedInputStream(process.getInputStream())){bis => stdout = String(bis.readAllBytes())}
      Using(new BufferedInputStream(process.getErrorStream())){bis => errout = String(bis.readAllBytes())}
      if !errout.isEmpty() then return Failure(RuntimeException(errout))
      if stdout.isEmpty then return Success(Tuple2(true, ""))
      else return Success(Tuple2(false, stdout))

    def support(srcFile: String): Boolean = 
      val fileName = srcFile.split(File.separator).last
      var result = false
      for s <- List("c", "cpp", "cc") do
        result |= fileName.endsWith(s)
      return result
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
    
