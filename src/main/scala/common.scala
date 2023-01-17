import java.awt.*;
import java.awt.TrayIcon.MessageType;
import scala.util.Try;
import java.io.*;
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import scala.util.{Failure, Success, Using}
import scala.io.Source

package space.bird14.cp_tool:
  val PROJECT_NAME = "cp_tool"

  package common:
    trait Language:
      def support(srcFile: String): Boolean
      def beforeSumit(problem: String, answer: String): Try[File]
      def compile(srcFile: String, templatePath: String): Try[File]
      def run(runPath: String, inputFile: String, outputFile: String, timeLimit: Int = 1000, 
        memoryLimit: Int = 512):  Try[File]

    trait Contest:
      def getContestInfo(): Option[ContestInfo] = 
        try
          val f = File(s"${Config.dataPath}now_info")
          var content: String = Source.fromFile(f).getLines().mkString("")
          Some(ContestInfo.fromString(content).get.asInstanceOf[ContestInfo])
        catch
          case e: Exception => None
      protected def beforeSetContest(id : String) : Try[Seq[String]]
      def getCategory() : String
      def setContestInfo(id: String): Try[ContestInfo] =
        val info = beforeSetContest(id)
        if info.isSuccess then 
          val result = ContestInfo(info.get, id, getCategory())
          try
            val f = File(s"${Config.dataPath}now_info")
            Using(FileWriter(f)){fw => fw.write(compact(render(result.toJson)))}
          catch
            case e: Exception => Failure(e)
          Success(result)
        else
          Failure(info.failed.get)
      def downloadTestData(id: String): Try[File]
      def submit(id: String): Try[String]

    object Display:
      def displayTray(message: String): Unit =
        //Obtain only one instance of the SystemTray object
        var tray = SystemTray.getSystemTray();

        //If the icon is a file
        var image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        var trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);
        
        trayIcon.displayMessage(PROJECT_NAME, message, MessageType.INFO);

    trait Jsonable:
      def _fromJValue(json: JValue) : Any
      def fromString(s : String) : Try[Any] = 
        try
          Success(_fromJValue(parse(s)))
        catch
          case e : Exception => Failure(e)

    // case class CompetionSatus(name: String, id: String) extends Jsonable:
    //   override def toJson: JValue = ("name" -> name) ~ ("id" -> id)
    //   override def _fromJValue(json: JValue): CompetionSatus = 
    //     implicit val formats = DefaultFormats
    //     CompetionSatus((json \ "name").extract[String], (json \ "id").extract[String])

    case class ContestInfo(problems: Seq[String], id : String, category: String) :
      def toJson = ("problems" -> problems) ~~ ("id" -> id) ~~ ("category" -> category)
    object ContestInfo extends Jsonable :
      override def _fromJValue(json: JValue): ContestInfo = 
        implicit val formats = DefaultFormats
        ContestInfo((json \ "problems").extract[Seq[String]], (json \ "id").extract[String], 
        (json \ "category").extract[String])


    object Config:
      def dataPath = "data/"

    object CommonUtil:
      def relativePathToAbstractPathByRunPath(relativePath: String) : String =
        if relativePath.startsWith("/") then return relativePath
        val runtime = Runtime.getRuntime()
        val process = runtime.exec("pwd")
        var abstractPath = String(process.getInputStream().readAllBytes())
        abstractPath = abstractPath.replaceAllLiterally(System.lineSeparator(), "")
        for s <- relativePath.split(File.separator) if s != "." if s.length() > 0 do
          if ".." == s then 
            abstractPath = abstractPath.split(File.separator).dropRight(1).mkString(File.separator)
          else
            abstractPath += File.separator + s
        abstractPath
    

