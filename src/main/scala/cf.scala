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
    override def downloadTestData(id: String): Try[File] = ???
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
    override def getCategory(): String = "CodeForces"
      
