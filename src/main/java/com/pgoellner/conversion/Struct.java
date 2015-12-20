package com.pgoellner.conversion;

import java.util.ArrayList;
import java.io.File;

class Struct
{
  public static void main(String[] s)
  {
    new Converter().convert(new File("test.py"));
  }
  
  /**runs through every method thus performing any needed structure conversions*/
  public String checkStructs(String line, boolean check)
  {
    //initialize an Object Array to contain the argument
    Object[] args = new Object[1];
    
    //run through every method of Struct
    for(java.lang.reflect.Method m : Struct.class.getDeclaredMethods())
    {
      //set the line as argument
      args[0] = line;
      //try to call the current method with this object as and the given argument
      try
      {line = (String) m.invoke(this, args);}
      catch(Exception e) {}
    }
    return line;
  }
  
  
  /**parses the methos head by replacing "def" with "public void" and by deleting ":" at the end of the line*/
  private String parseFunction(String line)
  {
    //exit if no conversion is needed here
    if(!line.contains("def "))
    return line;
    //add "#-#" to mark a method head
    return line.replace("def ","public void ").replace(":", "#-#");
  }
  
  /**parses if statements by enclosing the check in brackets and by deleting ":" at the end of the line*/
  private String parseIf(String line)
  {
    //exit if no conversion is needed here
    if(!line.contains(" if "))
    return line;
    return line.replace(" if "," if(").replace(":", ")#-#");
  }
  
  /**parses elif statements by enclosing the check in brackets and by deleting ":" at the end of the line*/
  private String parseElif(String line)
  {
    //exit if no conversion is needed here
    if(!line.contains("elif "))
    return line;
    return line.replace("elif ","else if(").replace(":", ")#-#");
  }
  
  /**parses else statements by deleting ":" at the end of the line*/
  private String parseElse(String line)
  {
    //exit if no conversion is needed here
    if(!line.contains("else "))
    return line;
    return line.replace("else:","else#-#").replace("else ", "else").replace(":", "#-#");
  }
  
  /**parses while statements by enclosing the check in brackets and by deleting ":" at the end of the line*/
  private String parseWhile(String line)
  {
    //exit if no conversion is needed here
    if(!line.contains("while "))
    return line;
    return line.replace("while ","while(").replace(":", ")#-#");
  }
  
  /**parses for statements by enclosing the check in brackets, inserting "Object" before the vaiable replacing the "in" with ":" and by deleting ":" at the end of the line*/
  private String parseFor(String line)
  {
    //exit if no conversion is needed here
    if(!line.contains("for "))
    return line;
    line = line.replace("for ","for(Object ").replace(":", ")#-#");
    return line.replace(" in ", " : ");
  }
}