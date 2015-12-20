package com.pgoellner.conversion;

import java.io.File;
import java.util.ArrayList;

public abstract class ConversionUtils
{
  static ArrayList<String> additionalContent = new ArrayList<String>();
  
  public static void main(String[] s)
  {
    new Converter().convert(new File("test.py"));
  }
  
  /**returns the position of the corresponding partner of a paired character*/
  public static int getPairPosition(String line, char character, int startingPosition)
  {
    char pair;
    
    //find the corresponding character
    switch(character)
    {
      case '(': {pair = ')'; break;}
      case '[': {pair = ']'; break;}
        case '{': {pair = '}'; break;}
      default: {pair = character; break;}
    }
    
    //find the position of the partnercharacter
    for(int i = startingPosition+1; i < line.length(); i++)
    {
      if(pair == line.charAt(i))
      {
        return i;
      }
    }
    return -1;
  }
  
  /**returns the depth of a line in spaces*/
  public static int getDepth(String line)
  {
    int level = 0;
    
    //increment the depth for every leading space
    while(line.charAt(level) == ' ')
    {
      level++;
    }
    
    return level;
  }
  
  /**returns a list of all the method blocks and the remaining rest of code*/
  public static ArrayList<ArrayList<String>> getBlocks(ArrayList<String> lines)
  {
    ArrayList<ArrayList<String>> blocks = new ArrayList<ArrayList<String>>();
    
    ArrayList<String> block = new ArrayList<String>();
    ArrayList<String> rest = new ArrayList<String>();
    boolean inBlock = false;
    
    for(String line : lines)
    {
      if(!inBlock)
      {
        if(line.contains("#-#"))
        {
          block.add(line);
          inBlock = true;
        }
        else
        {
          if(!line.trim().isEmpty())
          {
            rest.add(line);
          }
        }
      }
      else
      {
        block.add(line);
        
        if(line.contains("-#-"))
        {
          inBlock = false;
          blocks.add(block);
          block = new ArrayList<String>();
        }
      }
    }
    
    lines.removeAll(rest);
    
    blocks.add(rest);
    
    return blocks;
  }
  
  /**inserts ";" at the end of each line requiring it*/
  public static String endLine(String line)
  {
    //determine whether there is a semicolon needed and place it if need be
    if(line.endsWith("{") || line.endsWith("}") || line.contains(" if(") || line.contains(" else"))
    return line;
    else if(line.endsWith("#-#") || line.endsWith("-#-"))
    return line;
    else
    return line.concat(";");
  }
  
  /**delets "#-#" and "-#-" at the end of line*/
  public static String cleanLine(String line)
  {
    //clean up the help for the block building
    if(line.endsWith("#-#") || line.endsWith("-#-"))
    return line.replace("#-#", "").replace("-#-", "");
    else
    return line;
  }
  
  /**finds the specified character in the given line left of the startingPosition*/
  public static int getCharIndexBefore(String line, char character, int startingPosition)
  {
    while(startingPosition != -1)
    {
      if(character == line.charAt(startingPosition))
      {
        return startingPosition;
      }
      startingPosition--;
    }
    return startingPosition;
  }
}