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
  
  public void convert(File source)
  {
    ArrayList<String> lines = new ArrayList<String>();
    ArrayList<String> output = new ArrayList<String>();
    
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(source));
      
      String line = "";
      
      while((line = br.readLine()) != null)
      {
        lines.add(line);
      }
      
      br.close();
    }
    catch(Exception e)
    {
      System.out.println("Unable to read "+source.getName()+"!");
      e.printStackTrace();
      return;
    }
    
    lines = groupBlocks(lines);
    lines = parseStrings(lines);
    
    for(String str : lines)
    {
      if(str.contains("print"))
      {
        str = parsePrint(str);
      }
      else if(str.contains("def"))
      {
        str = parseFunction(str);
      }
      
      if(!str.trim().isEmpty())
      str = endLine(str);
      output.add(str);
    }
    
    
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
    catch(Exception e)
    {
      System.out.println("Unable to write "+source.getName()+"!");
      e.printStackTrace();
      return;
    }
  }
  
  private ArrayList<String> parseStrings(ArrayList<String> lines)
  {
    for(String str : lines)
    {
      str = str.replace("'", "\"").replace("\"\"", "\"");
    }
    return lines;
  }
  
  private String parsePrint(String line)
  {
    int beginning = line.indexOf("print(")+5;
    int end = getPairPosition(line, '(', beginning);
    
    char[] printContent = new char[(end-beginning)-1];
    
    for(int i = beginning+1; i < end; i++)
    {
      printContent[i-(beginning+1)] = line.charAt(i);
    }
    
    return line.replace(line.substring(beginning-5, end+1),"System.out.println(" + new String(printContent) + ")");
  }
  
  private String parseFunction(String line)
  {
    return line.replace("def ","public void ").replace(":", "");
  }
  
  private int getPairPosition(String line, char character, int startingPosition)
  {
    char pair;
    
    switch(character)
    {
      case '(': {pair = ')'; break;}
      case '[': {pair = ']'; break;}
        case '{': {pair = '}'; break;}
      default: {pair = character; break;}
    }
    
    for(int i = startingPosition+1; i < line.length(); i++)
    {
      if(pair == line.charAt(i))
      {
        return i;
      }
    }
    return -1;
  }
  
  private int getDepth(String line)
  {
    int level = 0;
    
    while(line.charAt(level) == ' ')
    {
      level++;
    }
    
    return level;
  }
  
  private ArrayList<String> groupBlocks(ArrayList<String> content)
  {
    int[] levels = new int[content.size()];
    
    for(int i = 0; i < levels.length; i++)
    {
      if(!content.get(i).trim().isEmpty())
      levels[i] = getDepth(content.get(i));
      else
      levels[i] = 0;
    }
    
    int std = levels[0];
    boolean inBlock = false;
    
    for(int i = content.size()-1; i != -1; i--)
    {
      if(levels[i] != std)
      {
        if(!inBlock)
        {
          inBlock = true;
        content.add(i+1,"}");
      }
    }
    else
    {
      if(inBlock)
      {
        inBlock = false;
        content.add(i,content.remove(i).concat("#-#"));
        content.add(i+1,"{");
        }
      }
    }
    return content;
  }
  
  private String endLine(String line)
  {
    if(line.endsWith("{") || line.endsWith("}"))
    return line;
    else if(line.endsWith("#-#"))
    return line.replace("#-#","");
    else
    return line.concat(";");
  }
}