import java.awt.*;
import java.awt.TrayIcon.MessageType;
import scala.util.Try;
import java.io.*;
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import scala.util.{Failure, Success}

package space.bird14.cp_tool:
  val PROJECT_NAME = "cp_tool"

  package common:
    trait Language:
      def beforeSumit(problem: String, answer: String): Try[File]
      def compile(srcFile: String, templatePath: String): Try[File]
      def run(runPath: String, inputFile: String, outputFile: String, timeLimit: Int = 1000, 
      memoryLimit: Int = 512):  Try[File]

    trait Contest:
      def getContestInfo(): Option[String]
      def setContestInfo(id: String): Option[String]
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
      def toJson : JValue
      def _fromJValue(json: JValue) : Any
      def fromString(s : String) : Try[Any] = 
        try
          Success(_fromJValue(parse(s)))
        catch
          case e : Exception => Failure(e)

    case class CompetionSatus(name: String, id: String) extends Jsonable:
      override def toJson: JValue = ("name" -> name) ~ ("id" -> id)
      override def _fromJValue(json: JValue): CompetionSatus = 
        implicit val formats = DefaultFormats
        CompetionSatus((json \ "name").extract[String], (json \ "id").extract[String])
    

