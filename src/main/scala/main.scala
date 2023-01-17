import space.bird14.cp_tool.*;
import space.bird14.cp_tool.common.Display
import space.bird14.cp_tool.contest.CodeForces
import org.json4s.*
import org.json4s.native.JsonMethods._
import space.bird14.cp_tool.common.CommonUtil
import space.bird14.cp_tool.lan.Clang
import space.bird14.cp_tool.common.ContestInfo
import space.bird14.cp_tool.common.{ContestHelper,ContestType}
import javax.naming.Context
package space.bird14.cp_tool:
  @main def sayHello(command: String, args: String*) = 
    // CodeForces.setContestInfo("566")
    //println(compact(render(CodeForces.getContestInfo().get.toJson)))
    // println(CommonUtil.relativePathToAbstractPathByRunPath("./././../cp-tool/data/../../."))
    CodeForces.downloadTestData("B")
    MainCommand.run(command, args)

  object MainCommand:
    def run(command: String, args: Seq[String]): Unit = 
      command match
        case "help" =>
          val info = f"""
          This program has several commands available.
          help Print the summary.
          cf {id} Set information of CodeForces. 
          down Download test data for now contest, please set contest info before run.
          """
          println(info)
        case "cf" =>
          if (args.length > 0) then
            val result = CodeForces.setContestInfo(args.head)
            if result.isSuccess then
              println(compact(render(result.get.toJson)))
            else
              println(result.failed)
          else
            println("please input the id of contest")
        case "down" =>
          val info = ContestHelper.getContestInfo()
          if info.isEmpty then return println("Please set contest first.")
          info.get.category match
            case ContestType.CF.name => 
              for id <- info.get.problems do
                CodeForces.downloadTestData(id)
            case _ => return println("Please set contest first.")
          
        case "test" =>
          if args.length > 1 then
            val src = CommonUtil.relativePathToAbstractPathByRunPath(args(0))
            val tem = CommonUtil.relativePathToAbstractPathByRunPath(args(1))
            val result = Clang.compile(src, tem)
            println(result)
          else
            println("please input the path of src and template")
        case _: String => 
          println("command not found, you can refer to help")
      
      

