@main def sayHello(commands: String*) = 
  for s <- commands do
    println(s)
