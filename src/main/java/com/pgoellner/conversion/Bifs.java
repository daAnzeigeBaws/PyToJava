package com.pgoellner.conversion;

import java.util.ArrayList;
import java.io.File;

public class Bifs
{
  public static void main(String[] s)
  {
    new Converter().convert(new File("test.py"));
  }
  
  /**runs through every method thus performing any needed structure conversions*/
  public String checkBifs(String line, boolean check)
  {
    Object[] args = new Object[1];
    for(java.lang.reflect.Method m : Bifs.class.getDeclaredMethods())
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
  
  
  /**transforms the BIF abs into a Java equivalent*/
  public String abs(String line)
  {
    //determine the boundries of the abs function
    int beginning = line.indexOf("abs(");
    int end = beginning + 3;
    
    line = line.replace(line.substring(beginning, end),"Math.abs");
    
    return line;
  }
  
  /**transforms the BIF isinstance into a Java equivalent*/
  public String isinstance(String line)
  {
    //determine the boundries of the abs function
    int beginning = line.indexOf("isinstance(") + 11;
    int end = ConversionUtils.getPairPosition(line, '(', beginning);
    
    //create a char[] for all the content chars
    String content = line.substring(beginning, end);
    
    String[] args = content.split(",");
    
    line = line.replace("isinstance(" + content + ")",args[0] + " instanceof " + args[1]);
    
    return line;
  }
  
  /**transforms the BIF print into a Java equivalent*/
  public String print(String line)
  {
    //determine the boundries of the printed content
    int beginning = line.indexOf("print(")+5;
    int end = ConversionUtils.getPairPosition(line, '(', beginning);
    
    //create a char[] for all the content chars
    char[] printContent = new char[(end-beginning)-1];
    
    //insert the content into the array
    for(int i = beginning+1; i < end; i++)
    {
      printContent[i-(beginning+1)] = line.charAt(i);
    }
    
    String content = new String(printContent);
    boolean println = true;
    
    if(content.contains("end="))
    {
      int indexStart = content.indexOf("end=")+4;
      char stringQuote = printContent[indexStart];
      int indexEnd = ConversionUtils.getPairPosition(content, stringQuote, indexStart);
      
      //determine the content of the "end" variable
      String endValue = content.substring(indexStart+1,indexEnd);
      //determine the start of the substring ", end='[...]' or so"
      int replacementSubstringStartIndex = ConversionUtils.getCharIndexBefore(line,',',line.indexOf("end="));
      //build the string to be substituted
      String toBeReplaced = line.substring(replacementSubstringStartIndex,line.indexOf("end=")+4);
      
      //append the content of the "end" variable (plus the quotes)
      toBeReplaced += stringQuote;
      toBeReplaced += endValue;
      toBeReplaced += stringQuote;
      
      content = content.replace(toBeReplaced, "+\""+endValue+"\"");
      
      println = false;
    }
    if(content.contains("file="))
    {
      
    }
    
    //replaces the print statement, keeping the printed content
    if(println)
    return line.replace(line.substring(beginning-5, end+1),"System.out.println(" + content + ")");
    else
    return line.replace(line.substring(beginning-5, end+1),"System.out.print(" + content + ")");
  }
  
  /**transforms the BIF range into a Java equivalent*/
  public String range(String line)
  {
    //exit if no conversion is needed here
    if(!line.contains("range("))
    return line;
    //determine the boundries of the range function
    int beginning = line.indexOf("range(");
    int end = beginning + 5;
    
    line = line.replace(line.substring(beginning, end),"getRange");
    
    //add a help method getRange() that is called to build a range
    ConversionUtils.additionalContent.add("");
    ConversionUtils.additionalContent.add("private java.util.ArrayList<Integer> getRange(int i)#-#");
    ConversionUtils.additionalContent.add("{");
      ConversionUtils.additionalContent.add("    if(i == 0)");
      ConversionUtils.additionalContent.add("    return new java.util.ArrayList<Integer>();");
      ConversionUtils.additionalContent.add("    java.util.ArrayList<Integer> list = new java.util.ArrayList<Integer>();");
      ConversionUtils.additionalContent.add("    list.add(i);");
      ConversionUtils.additionalContent.add("    list.addAll(0,getRange(i-1));");
      ConversionUtils.additionalContent.add("    return list;");
    ConversionUtils.additionalContent.add("}-#-");
    
    return line;
  }
  
  /**transforms the BIF round into a Java equivalent*/
  public String round(String line)
  {
    //determine the boundries of the round function
    int beginning = line.indexOf("round(");
    int end = beginning + 5;
    
    line = line.replace(line.substring(beginning, end),"Math.round");
    
    return line;
  }
  
  /**transforms the BIF str into a Java equivalent*/
  public String str(String line)
  {
    //determine the boundries of the str content
    int beginning = line.indexOf("str(")+3;
    int end = ConversionUtils.getPairPosition(line, '(', beginning);
    
    //create a char[] for all the content chars
    char[] printContent = new char[(end-beginning)-1];
    
    //insert the content into the array
    for(int i = beginning+1; i < end; i++)
    {
      printContent[i-(beginning+1)] = line.charAt(i);
    }
    
    String content = new String(printContent);
    
    return line.replace(line.substring(beginning-5, end+1),"(\"\" + " + content + ")");
  }
}