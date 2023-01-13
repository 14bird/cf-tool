import space.bird14.cp_tool.*;
import space.bird14.cp_tool.common.Display
import space.bird14.cp_tool.contest.CodeForces
package space.bird14.cp_tool:
  @main def sayHello(command: String, args: String*) = 
    CodeForces.setContestInfo("123")
    MainCommand.run(command, args)

  object MainCommand:
    def run(command: String, args: Seq[String]): Unit = 
      command match
        case "help" =>
          println("help")
        case _: String => 
          println("command not found, you can refer to help")
      
      

