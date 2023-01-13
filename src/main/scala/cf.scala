import space.bird14.cp_tool.*;
import java.io.*;
import scala.util.Using;
import space.bird14.cp_tool.common.Contest
import scala.util.Try
import okhttp3.*
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.jsoup.*

package space.bird14.cp_tool.contest:
  case class CfInfo(problems: Seq[String]):
    def toJson = ("problems" -> problems)

  object CodeForces extends Contest:
    override def getContestInfo(): Option[String] = ???
    override def downloadTestData(id: String): Try[File] = ???
    override def setContestInfo(id: String): Option[String] = 

      var f = File("data/cf", id)
      if !f.exists() then
        f.getParentFile().mkdirs()
        f.createNewFile()
      
      var doc = Jsoup.connect(s"https://codeforces.com/contest/${id}").get()
      var eles = doc.getElementsByClass("problems")
      if (eles.isEmpty()) then return None
      var ele = eles.get(0)
      var ids = ele.getElementsByClass("id")
      if (ids.isEmpty()) then return None
      var vec : Seq[String] = Vector()
      ids.forEach(id => vec = vec :+ id.text)

      Using(FileWriter(f)){fos => fos.write(compact(render(CfInfo(vec).toJson)))}
      

      return getContestInfo()
    override def submit(id: String): Try[String] = ???
      
