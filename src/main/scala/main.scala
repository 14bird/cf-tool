import space.bird14.cp_tool.*;
import space.bird14.cp_tool.common.Display
import space.bird14.cp_tool.contest.CodeForces
import org.json4s.*
import org.json4s.native.JsonMethods._
package space.bird14.cp_tool:
  @main def sayHello(command: String, args: String*) = 
    // CodeForces.setContestInfo("566")
    println(compact(render(CodeForces.getContestInfo().get.toJson)))
    MainCommand.run(command, args)

  object MainCommand:
    def run(command: String, args: Seq[String]): Unit = 
      command match
        case "help" =>
          val info = f"""
          This program has several commands available.
          help print the summary
          """
          println(info)
        case _: String => 
          println("command not found, you can refer to help")
      
      

