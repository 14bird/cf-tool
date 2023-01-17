import space.bird14.cp_tool.*;
import java.io.*;
import scala.util.*;
import space.bird14.cp_tool.common.*
import okhttp3.*
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.jsoup.*
import scala.util.Failure

package space.bird14.cp_tool.contest:
  object CodeForces extends Contest:
    override def downloadTestData(id: String): Try[Int] = 
      val info = getContestInfo()
      if info.isEmpty then return Failure(RuntimeException("can't get contextInfo"))
      if info.get.category != getCategory() then 
        return Failure(RuntimeException("Now isn't codeforces, please reset conset info"))
      try
        val doc = Jsoup.connect(s"https://codeforces.com/contest/${info.get.id}/problem/${id}").get()
        // val doc = Jsoup.parse(File("test-data/B.html"))
        val sample = doc.getElementsByClass("sample-tests").get(0)
        val inputs = doc.getElementsByClass("input")
        val outputs = doc.getElementsByClass("output")
        val dir = File(s"${Config.dataPath}/cf/problem/${info.get.id}_${id}")
        if dir.exists() then
          for f<- dir.listFiles() do
            f.delete()
        else
          dir.mkdirs()
        for (i <-  0 until inputs.size()) do
          val input = File(dir, s"input-$i")
          Using(FileWriter(input)){fw =>
            fw.write(inputs.get(i).getElementsByTag("pre").html().replaceAllLiterally("<br>", 
              System.lineSeparator()))
          }
          val output = File(dir, s"output-$i")
          Using(FileWriter(output)){fw =>
            fw.write(outputs.get(i).getElementsByTag("pre").html().replaceAllLiterally("<br>", 
              System.lineSeparator()))
          }
        Success(inputs.size)
      catch
        case e: Exception => Failure(e)
    override def beforeSetContest(id: String): Try[Seq[String]] = 
      try
        var f = File(s"${Config.dataPath}cf", id)
        if !f.exists() then
          f.getParentFile().mkdirs()
          f.createNewFile()
        var doc = Jsoup.connect(s"https://codeforces.com/contest/${id}").get()
        var eles = doc.getElementsByClass("problems")
        var ele = eles.get(0)
        var ids = ele.getElementsByClass("id")
        var vec : Seq[String] = Vector()
        ids.forEach(id => vec = vec :+ id.text)
        Using(FileWriter(f)){fos => fos.write(compact(render(ContestInfo(vec, id, "codeforces").toJson)))}
        Success(vec)
      catch
        case e: Exception => Failure(e)

    override def submit(id: String): Try[String] = ???
    def getCategory() : String = ContestType.CF.name
      
