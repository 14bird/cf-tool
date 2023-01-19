import space.bird14.cp_tool.*;
import space.bird14.cp_tool.common.Display
import space.bird14.cp_tool.contest.CodeForces
import org.json4s.*
import org.json4s.native.JsonMethods._
import space.bird14.cp_tool.common.CommonUtil
import space.bird14.cp_tool.lan.Clang
import space.bird14.cp_tool.common.*
import javax.naming.Context
package space.bird14.cp_tool:
  @main def sayHello(command: String, args: String*) = 
    // CodeForces.setContestInfo("566")
    //println(compact(render(CodeForces.getContestInfo().get.toJson)))
    // println(CommonUtil.relativePathToAbstractPathByRunPath("./././../cp-tool/data/../../."))
    // CodeForces.downloadTestData("B")
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
          val info = ContestInfo.getContestInfo()
          if info.isEmpty then return println("Please set contest first.")
          info.get.category match
            case ContestType.CF.name => 
              for id <- info.get.problems do
                CodeForces.downloadTestData(id)
            case _ => return println("Please set contest first.")
          
        case "test" =>
          if args.length > 2 then
            val src = CommonUtil.relativePathToAbstractPathByRunPath(args(0))
            val tem = CommonUtil.relativePathToAbstractPathByRunPath(args(1))
            val id = args(2)
            val lan: Language = common.judgeLanguage(src)
            val result = lan.compile(src, tem)
            if result.isFailure then
              return println(result.failed.get.getStackTrace().mkString(System.lineSeparator()))
            val info = ContestInfo.getContestInfo()
            if info.isEmpty then return println("Please set contest first.")
            info.get.category match
            case ContestType.CF.name => 
              val inputs = CodeForces.getInputFiles(id)
              for input <- inputs do
                val output = java.io.File(input.getParent(), input.getName().replaceAllLiterally("input", "output"))
                val res = lan.run(result.get, input, output)
                if res.isSuccess then
                  if res.get._1 then
                    println("pass case " + input.getName().split("-")(1))
                  else
                    println("fail case " + input.getName.split("-")(1))
                    println(res.get._2)
                else
                  println("fail case " + input.getName().split("-")(1))
                  println(res.failed.get.getStackTrace().mkString(System.lineSeparator()))
            case _ => return println("Please set contest first.")

          else
            println("please input the path of src and template as well as problem id")
        case _: String => 
          println("command not found, you can refer to help")
