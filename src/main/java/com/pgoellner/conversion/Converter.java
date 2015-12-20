package com.pgoellner.conversion;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class Converter
{
  public static void main(String[] s)
  {
    new Converter().convert(new File("test.py"));
  }
  
  /**converts the given .py file into a .java file*/
  public void convert(File source)
  {
    //creates two lists, one for the output the other for the processing
    ArrayList<String> lines = new ArrayList<String>();
    ArrayList<String> output = new ArrayList<String>();
    
    //tries to read the entire input file
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(source));
      
      String line = "";
      
      //while the current file, which is fetched right away, is not empty add it to the list
      while((line = br.readLine()) != null)
      {
        lines.add(line);
      }
      
      br.close();
    }
    //throw exception in case of a failure
    catch(Exception e)
    {
      System.out.println("Unable to read "+source.getName()+"!");
      e.printStackTrace();
      return;
    }
    
    //group the blocks
    lines = groupBlocks(lines);
    //parse all strings
    lines = parseStrings(lines);
    //parse all boolean relevant expressions
    lines = parseBools(lines);
    System.out.println(lines);
    
    //check every line
    for(String str : lines)
    {
      str = new Struct().checkStructs(str, true);
      str = new Bifs().checkBifs(str, true);
      
      //check necessity of ";" at the end of line for every non-empty line
      if(!str.trim().isEmpty())
      str = ConversionUtils.endLine(str);
      
      output.add(str);
    }
    
    //get all the blocks and all the code that remains
    ArrayList<ArrayList<String>> blocks = ConversionUtils.getBlocks(output);
    System.out.println(blocks);
    
    //insert a main() method for every sole statement
    output.add("");
    output.add("public static void main(String[] args)");
    output.add("{");
      for(String str : blocks.get(blocks.size()-1))
      {
        if(!str.trim().isEmpty())
        output.add("    ".concat(str));
      }
    output.add("}");
    
    //add additionalContent e.g. functional interfaces at the end of the class
    output.addAll(ConversionUtils.additionalContent);
    
    //add a new tab in every line and clean up
    for(int i = 0; i < output.size(); i++)
    {
      output.add(i, "    ".concat(output.remove(i)));
      output.add(i, ConversionUtils.cleanLine((output.remove(i))));
    }
    
    //insert a class structure with the filename as the class name
    String name = source.getName();
    output.add(0,"public class " + name.substring(0,name.length()-3));
    output.add(1,"{");
    output.add("}");
    
    //tries to write each line to a output file
    try
    {
      BufferedWriter br = new BufferedWriter(new FileWriter("test.java"));
      
      for(String str : output)
      {
        br.write(str);
        br.newLine();
      }
      
      br.close();
    }
    //throw an exception in case of a failure
    catch(Exception e)
    {
      System.out.println("Unable to write "+source.getName()+"!");
      e.printStackTrace();
      return;
    }
  }
  
  
  /*the character and keyword conversion part*/
  /**replaces every quotation and every double doublequotation with a doublequotation*/
  private ArrayList<String> parseStrings(ArrayList<String> lines)
  {
    for(int i = 0; i < lines.size(); i++)
    {
      lines.add(i,lines.remove(i).replace("'", "\"").replace("\"\"", "\""));
    }
    return lines;
  }
  
  /**replaces every boolean relevant keyword*/
  private ArrayList<String> parseBools(ArrayList<String> lines)
  {
    for(int i = 0; i < lines.size(); i++)
    {
      lines.add(i,lines.remove(i)
      .replace(" True", " true")
      .replace(" False", " false")
      .replace(" not ", " !")
      .replace(" is ", " == ")
      .replace(" and ", " && ")         
      .replace(" or ", " || ")
      .replace(" elif", " else if"));
    }
    return lines;
  }
  
  /**replaces every unbound keyword*/
  private ArrayList<String> parseUnbound(ArrayList<String> lines)
  {
    for(int i = 0; i < lines.size(); i++)
    {
      lines.add(i,lines.remove(i).replace(" None", " null").replace(" raise", " throw"));
    }
    return lines;
  }
  
  
  /*the control structure conversion part*/
  
  /**inserts braces at the top and the bottom of each block (not exactly top as they are included under the method head)
  !!only one block level is supported!!*/
  private ArrayList<String> groupBlocks(ArrayList<String> content)
  {
    //create an array to map the depths
    int[] levels = new int[content.size()];
    
    //determine each line's depth
    for(int i = 0; i < levels.length; i++)
    {
      if(!content.get(i).trim().isEmpty())
      levels[i] = ConversionUtils.getDepth(content.get(i));
      else
      levels[i] = 0;
    }
    
    //set the minimum depth
    int std = levels[0];
    //set the inBlock control
    boolean inBlock = false;
    
    int startBlock = 0;
    
    //runs through the entire list backwards
    for(int i = content.size()-1; i != -1; i--)
    {
      System.out.println(levels[i]);
      if(levels[i] != std)
      {
        if(!inBlock)
        {
          //if the current line has a non-standard depth and we are currently not in a block
          inBlock = true;
          //set the starting point for a recursive block building
          startBlock = i;
          //insert a brace after the current point and "-#-" to mark the end of a block
        content.add(i+1,"}-#-");
      }
    }
    else
    {
      if(inBlock)
      {
        //if the current line has a standard depth and we are currently in a block
        inBlock = false;
        //insert a brace after the current point
        content.add(i+1,"{");
        }
      }
    }
    return content;
  }
}