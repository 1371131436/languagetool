package de.danielnaber.languagetool.dev.conversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;

import de.danielnaber.languagetool.JLanguageTool;

import morfologik.stemming.Dictionary;
import morfologik.stemming.DictionaryLookup;
import morfologik.stemming.IStemmer;
import morfologik.stemming.WordData;

public abstract class RuleConverter {

    protected static final String firstIndent = "  ";
    protected static final String secondIndent = "    ";
    protected static final String thirdIndent = "      ";
    protected static final String fourthIndent = "        ";
    protected static final int firstIndentInt = 2;
    protected static final int secondIndentInt = 4;
    protected static final int thirdIndentInt = 6;
    protected static final int fourthIndentInt = 8;
    
    protected String inFileName;
    protected String outFileName;
    protected String ruleType;
    
    protected static DictionaryLookup dictLookup = (DictionaryLookup) loadDictionary();
    
    protected static final Pattern wordReference = Pattern.compile("\\\\\\d+");
    protected static final Pattern wordReferenceTransform = Pattern.compile("\\\\\\d+:[^:]+");
    protected static final Pattern uppercase = Pattern.compile("[A-Z]");
    
    private static Pattern regex = Pattern.compile("[\\.\\^\\$\\*\\+\\?\\{\\}\\[\\]\\|\\(\\)]");
    public static String xmlHeader = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<?xml-stylesheet type=\"text/xsl\" href=\"../print.xsl\" ?>\n" +
        "<?xml-stylesheet type=\"text/css\" href=\"../rules.css\"\n" + 
        "title=\"Easy editing stylesheet\" ?>\n" +
        "<!--\n" +
        "English Grammar and Typo Rules for LanguageTool\n" +
        "See tagset.txt for the meaning of the POS tags\n" +
        "Copyright (C) 2001-2007 Daniel Naber (http://www.danielnaber.de)\n" +
        "$Id: grammar.xml,v 1.129 2010-11-13 23:24:21 dnaber Exp $\n" +
        "-->\n" +
        "<!--suppress CheckTagEmptyBody -->\n" +
        "<rules lang=\"en\" xsi:noNamespaceSchemaLocation=\"../rules.xsd\" xmlns:xsi=\"http://\n" +
        "www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n";
    
    // basic constructor
    public RuleConverter() {    
    }
    
    // constructor with input and output rule files
    public RuleConverter(String inFileName, String outFileName, String ruleType) {
        this.inFileName = inFileName;
        this.outFileName = outFileName;
        if (ruleType == null)     {
            this.ruleType = "default";
        } else {
            this.ruleType = ruleType;
        }
    }
    
  /**
   * Reads rules in from rule file specified by inFileName
   * 
   * @return a List<String> containing the rule strings from the infile
   */
    public abstract List<String> getRulesAsString() throws IOException;
    
    // parses a rule, returning a HashMap of relevant values ( a sketchy part of the implementation)
    // the "pattern" key will always point to the thing to match, be it a regexp, some tokens, just a term,
    // or any combination thereof
    public abstract HashMap<String,String> parseRule(String rule);
    
    // takes the result of parseRule() and returns a rule in LT format (i.e. XML)
    // this is the most difficult part: actually finding meaning in the rules
//    public abstract String ltRule(HashMap<String,String> rule);
    
//    public abstract List<String> ltRuleAsList(HashMap<String,String> rule);
    public abstract List<String> ltRuleAsList(HashMap<String,String> rule, String id, String name, String type);
    
    public String getInFile() {
        return inFileName;
    }
    
    public String getOutFile() {
        return outFileName;
    }
    
    public String getFileType() {
        return ruleType;
    }
    
    public void setInFile(String filename) {
        this.inFileName = filename;
    }
    
    public void setOutFile(String filename) {
        this.outFileName = filename;
    }
    
    public void setFileType(String fileType) {
        this.ruleType = fileType;
    }
    
    /**
     * 
     * @param orig LT rule ArrayList
     * @param token to add
     * @param postag to add
     * @param type: how to add the token, postag 
     * @return
     */
    protected static ArrayList<String> addToken(ArrayList<String> orig, String token, String postag, String type, int indent) {
        String space = getSpace(indent);
        
        if (type.equals("token")) {
            orig.add(space + "<token>" + token + "</token>");
        } else if (type.equals("regexandpostag")) {
            orig.add(space + "<token postag=\"" + postag + "\" regexp=\"yes\">" + token + "</token>");
        } else if (type.equals("tokenandpostag")) {
            orig.add(space + "<token postag=\"" + postag + "\">" + token + "</token>");
        } else if (type.equals("tokenandregexpostag")) {
            orig.add(space + "<token postag=\"" + postag + "\" postag_regexp=\"yes\">" + token + "</token>");
        } else if (type.equals("regexandregexpostag")) {
            orig.add(space + "<token postag=\"" + postag + "\" postag_regexp=\"yes\" regexp=\"yes\">" + token + "</token>");
        } else if (type.equals("postag")) {
            orig.add(space + "<token postag=\"" + postag + "\"></token>");
        } else if (type.equals("regextoken")) {
            orig.add(space + "<token regexp=\"yes\">" + token + "</token>");
        } else if (type.equals("regexpostag")) {
            orig.add(space + "<token postag=\"" + postag + "\" postag_regexp=\"yes\"></token>");
        } else if (type.equals("sentstart")) {
            orig.add(space + "<token postag=\"SENT_START\"></token>");
        }
        return orig;
    }
    
    /**
     * 
     * @param filename
     * @return ArrayList of lines of the file
     */
    public static ArrayList<String> fileToList(String filename) {
        ArrayList<String> returnList = new ArrayList<String>();
        Scanner in = null;
        try {
            in = new Scanner(new FileInputStream(filename));
            while (in.hasNextLine()) {
                returnList.add(in.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        return returnList;
    }
    
    public static ArrayList<String> fileToListNoBlanks(String filename) {
        ArrayList<String> returnList = new ArrayList<String>();
        Scanner in = null;
        try {
            in = new Scanner(new FileInputStream(filename));
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (!line.equals("") && !line.equals("\n")) {
                    returnList.add(line);
                }
            } 
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
        return returnList;
    }
    
    // pretty kludgy methods, here
    // seems necessary to have individual names for the rules, as opposed
    // to just putting them in a <rulegroup>, because then you can turn them on/off 
    // individually
    
    //TODO: rework this method to return acceptable names
    // (ones that don't start with an underscore, ones that don't include the regex symbols)
    public static String getSuitableID(HashMap<String,String> rule) {
        return rule.get("pattern").replaceAll("[\\ &|.*/]", "_");       
    }
    
    public static String getSuitableName(HashMap<String,String> rule) {
        return rule.get("pattern").replaceAll("[&|.*/]", "_");
    }
    
    /**
     * @param e: AtD token
     * @return if the token contains a "/", indicating that it contains POS tag information
     */
    protected static boolean hasPosTag(String e) {
        return e.contains("/");
    }
    
    /**
     * returns if the string contains a character that might indicate it's a regex
     */
    protected static boolean isRegex(String e) {
        Matcher m = regex.matcher(e);
        return m.find();        
    }
    
    protected static String getSpace(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<indent;i++) {
            sb.append(' ');
        }
        return sb.toString();
    }
    
    // if this is done in a non-static way it might save some time or be cleaner
    // changes for later
    public static boolean inDictionary(String word) {
        if (dictLookup == null) {
            dictLookup = (DictionaryLookup) loadDictionary();
        }
        return !dictLookup.lookup(word).isEmpty();
    }
    
    public static IStemmer loadDictionary() {
        IStemmer dictLookup = null;
        String fileName = "." + JLanguageTool.getDataBroker().getResourceDir() + "/en/english.dict";
        File dictFile = new File(fileName);
        try {
            dictLookup = new DictionaryLookup(Dictionary.read(dictFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dictLookup;
    }
    
}
