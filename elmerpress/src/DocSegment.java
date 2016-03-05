/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedWriter;

import java.io.File;

import java.io.FileInputStream;

import java.io.FileWriter;

import java.io.IOException;

import java.io.Writer;
import java.lang.String;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import java.util.logging.Level;

import java.util.logging.Logger;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

import org.apache.poi.hpsf.DocumentSummaryInformation;

import org.apache.poi.hwpf.*;

import org.apache.poi.hwpf.extractor.WordExtractor;

import org.apache.poi.hwpf.usermodel.*;

import org.apache.poi.poifs.filesystem.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;

import javax.xml.transform.*;

import javax.xml.transform.stream.*;

import javax.xml.transform.dom.*;

/**
 *
 * @author David Liu
 */
public class DocSegment {

    private String title = "";
    private String id = "";
    private Document doc = null;
    private Pattern pattern
            = Pattern.compile(""
                    + "((Fig.|Figure|Table)\\s+\\d+\\s{0,}-\\s{0,}\\d+\\s{0,}$)|"
                    + "((Fig.|Figure|Table)\\s+\\d+\\s{0,}-\\s{0,}\\d+\\s{0,}[^a-zA-Z0-9])|"
                    + "((Fig.|Figure|Table)\\s+\\d+[a-zA-Z]{0,1}(\\s{0,},\\s{0,}\\d{0,}[a-zA-Z]{0,1}[^a-zA-Z0-9]){0,}$)|"
                    + "((Fig.|Figure|Table)\\s+\\d+[a-zA-Z]{0,1}(\\s{0,},\\s{0,}\\d{0,}[a-zA-Z]{0,1}[^a-zA-Z0-9]){0,}[^a-zA-Z0-9])|"
                    + "(\\[\\d+\\])|"
                    + "(\\[\\d+-\\d+\\])|"
                    + "(\\[\\d+(,\\s*\\d+){1,}\\])|"
                    + "(\\[(\\d+|\\d+-\\d+)(,\\s*\\d+|,\\s*\\d+-\\d+){1,}\\])"
            );
    private Pattern listPattern = Pattern.compile("^\\d+\\)(.*)$");
    private ArrayList<DocSegment> children = new ArrayList<DocSegment>();
    private ArrayList<String> stringList = new ArrayList<String>();
    private Pattern sPattern = Pattern.compile("^\\[s\\d{0,}\\].+$");
    private String seletion = null;
    
    private boolean isAbc(String aaa) {
    	boolean bbb = false;
    	if(aaa!=null & (aaa.contains("Original Article") || aaa.contains("Short Communication"))) {
    		bbb = true;
    	}
    	return bbb;
    }

    public DocSegment() {
    	seletion = !("").equals(Main.isOriginal)? Main.isOriginal: 
        	!("").equals(RefDiviedMain.isOriginal) ? RefDiviedMain.isOriginal : 
        		!("").equals(RefSouceOnlyMain.isOriginal)?RefSouceOnlyMain.isOriginal:
        			"";
        
        System.out.println("seletion is:"+seletion);
    }

    public void setTitle(String a) {
        this.title = a;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDoc(Document a) {
        this.doc = a;
    }

    public Document getDoc() {
        return this.doc;
    }

    public void setList(ArrayList<String> a) {
        this.stringList = a;
    }

    public ArrayList<String> getList() {
        return this.stringList;
    }

    public void setId(String a) {
        this.id = a;
    }

    public String getId() {
        return this.id;
    }

    public void addChild(DocSegment aSegment) {
        children.add(aSegment);
    }

    public ArrayList<DocSegment> getChildren() {
        return this.children;
    }
    private String[] letterList = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w"};

    public static int secIndex = 0;

    public List<Element> getGeneral(ArrayList<String> stringList, String tag, String aaa) {

        List<Element> secList = new ArrayList();
      
        
        //String  = Main.isOriginal;

        for (int i = 0; i < stringList.size(); i++) {
            String oneString = stringList.get(i);

            while (!oneString.startsWith(tag) && i < stringList.size()) {

                ArrayList<Element> elemList = addTableFigure(oneString);

                secList.add(elemList.get(elemList.size() - 1));
                for (int x = 0; x < elemList.size() - 1; x++) {
                    secList.add(elemList.get(x));
                }
                i++;
                if (i >= stringList.size()) {
                    break;
                } else {
                    oneString = stringList.get(i);
                }
            }

            if (oneString.startsWith(tag)) {
                secIndex++;
                this.title = oneString.replace(tag, "");
                if (this.title.trim().length() < 1) {
                    if (Main.refs != null && Main.refs.size() > 0) {
                        Main.error(oneString + " must be followed by a title.");
                    } else if (RefDiviedMain.refs != null && RefDiviedMain.refs.size() > 0) {
                        RefDiviedMain.error(oneString + " must be followed by a title.");
                    } else if (RefSouceOnlyMain.refs != null && RefSouceOnlyMain.refs.size() > 0) {
                        RefSouceOnlyMain.error(oneString + " must be followed by a title.");
                    }
                }
                String theType = this.title.toLowerCase();
                if (title.equalsIgnoreCase("introduction") || title.equalsIgnoreCase("Synopsis")) {
                    theType = "intro";
                } else if (title.contains("Materials")) {
                    theType = "materials";
                } else if (title.contains("Cases") || title.equalsIgnoreCase("Cases Report") || title.equalsIgnoreCase("Cases Reports")) {
                    theType = "cases";
                } else if (title.equalsIgnoreCase("Disclusion") || title.equalsIgnoreCase("Comment")) {
                    theType = "discussion";
                } else if (title.equalsIgnoreCase("Disclosure Statement")) {
                    theType = "supplementary-material";
                } else if (title.equalsIgnoreCase("Abbreviations")) {
                    theType = "Abbreviations";
                }

                this.id = "s" + secIndex;
                Element sec = doc.createElement("sec");
                sec.setAttribute("id", this.id);
                if(secIndex==2 && isAbc(this.seletion)) {
                   sec.setAttribute("sec-type", "materials | methods");
                }
                Element secTitle = doc.createElement("title");
                sec.appendChild(secTitle);
                secTitle.appendChild(doc.createTextNode(title));

                i++;

                if (title.trim().equalsIgnoreCase("Abbreviations")) {
                    oneString = stringList.get(i);
                    Element abbr_def_list = doc.createElement("def-list");
                    String[] abbr_list = oneString.split(";");
                    for (String aString : abbr_list) {
                        String[] abbr_list_list = aString.split(":");
                        Element abbr_def_item = doc.createElement("def-item");
                        Element abbr_def_term = doc.createElement("term");
                        Element abbr_def_def = doc.createElement("def");
                        Element abbr_def_def_p = doc.createElement("p");
                        abbr_def_list.appendChild(abbr_def_item);
                        abbr_def_item.appendChild(abbr_def_term);
                        abbr_def_item.appendChild(abbr_def_def);
                        abbr_def_def.appendChild(abbr_def_def_p);
                        abbr_def_term.appendChild(doc.createTextNode(abbr_list_list[0].trim()));
                        abbr_def_def_p.appendChild(doc.createTextNode(abbr_list_list[1].trim()));
                    }
                    sec.appendChild(abbr_def_list);
                    i++;

                } else {
                    oneString = stringList.get(i);
                    ArrayList<String> stringArr = new ArrayList<String>();
                    while (!oneString.startsWith(tag) && i < stringList.size()) {
                        stringArr.add(oneString);
                        i++;
                        if (i >= stringList.size()) {
                            break;
                        } else {
                            oneString = stringList.get(i);
                        }
                    }
                    for (Element aElem : getSecGeneral(stringArr, secIndex)) {
                        sec.appendChild(aElem);
                    }
                }

                secList.add(sec);
                i--;
            }
        }
        return secList;
    }

    public List<Element> getSecGeneral(ArrayList<String> stringList, int theIndex) {

        List<Element> secList = new ArrayList();

        String tag = "[s1]";

        int inndex = 0;
        for (int i = 0; i < stringList.size(); i++) {
            String oneString = stringList.get(i).trim();

            while (!oneString.startsWith(tag) && i < stringList.size()) {
                if (oneString.trim().startsWith("1)")) {
                    Element listP = doc.createElement("p");

                    Element listP_order = doc.createElement("list");
                    listP_order.setAttribute("list-type", "order");
                    listP.appendChild(listP_order);
                    Matcher matcher
                            = listPattern.matcher(oneString.trim());
                    while (matcher.matches()) {

                        Element listP_item = doc.createElement("list-item");
                        listP_order.appendChild(listP_item);
                        Element listPP = doc.createElement("p");
                        listP_item.appendChild(listPP);
                        String[] oneStringStr = oneString.split("\\)");
                        listPP.appendChild(doc.createTextNode(oneStringStr[1].trim()));
                        i++;
                        if (i >= stringList.size()) {
                            break;
                        } else {
                            oneString = stringList.get(i);
                            matcher
                                    = listPattern.matcher(oneString.trim());
                        }
                    }
                    i--;
                    secList.add(listP);
                } else {

                    ArrayList<Element> elemList = addTableFigure(oneString);

                    secList.add(elemList.get(elemList.size() - 1));
                    for (int x = 0; x < elemList.size() - 1; x++) {
                        secList.add(elemList.get(x));
                    }

                }

                i++;
                if (i >= stringList.size()) {
                    break;
                } else {
                    oneString = stringList.get(i);
                }

            }

            if (oneString.startsWith(tag)) {
                inndex++;
                this.title = oneString.replace(tag, "");
                if (this.title.trim().length() < 1) {
                    if (this.title.trim().length() < 1) {
                        if (Main.refs != null && Main.refs.size() > 0) {
                            Main.error(oneString + " must be followed by a title.");
                        } else if (RefDiviedMain.refs != null && RefDiviedMain.refs.size() > 0) {
                            RefDiviedMain.error(oneString + " must be followed by a title.");
                        } else if (RefSouceOnlyMain.refs != null && RefSouceOnlyMain.refs.size() > 0) {
                            RefSouceOnlyMain.error(oneString + " must be followed by a title.");
                        }
                    }
                }
                String theType = this.title;
                this.id = "s" + theIndex + letterList[inndex - 1];
                Element sec = doc.createElement("sec");
                sec.setAttribute("id", this.id);
                if(secIndex==2 && isAbc(this.seletion)) {
                    sec.setAttribute("sec-type", "materials | methods");
                 }
                Element secTitle = doc.createElement("title");
                sec.appendChild(secTitle);
                secTitle.appendChild(doc.createTextNode(title));

                i++;
                oneString = stringList.get(i);
                while (!oneString.startsWith(tag) && i < stringList.size()) {
                    ArrayList<Element> elemList = null;
                    if (oneString.trim().startsWith("1)")) {
                        Element listP = doc.createElement("p");

                        Element listP_order = doc.createElement("list");
                        listP_order.setAttribute("list-type", "order");
                        listP.appendChild(listP_order);
                        Matcher matcher
                                = listPattern.matcher(oneString.trim());
                        while (matcher.matches()) {

                            Element listP_item = doc.createElement("list-item");
                            listP_order.appendChild(listP_item);
                            Element listPP = doc.createElement("p");
                            listP_item.appendChild(listPP);
                            String[] oneStringStr = oneString.split("\\)");
                            listPP.appendChild(doc.createTextNode(oneStringStr[1].trim()));
                            i++;
                            if (i >= stringList.size()) {
                                break;
                            } else {
                                oneString = stringList.get(i);
                                matcher
                                        = listPattern.matcher(oneString.trim());
                            }
                        }
                        i--;
                        sec.appendChild(listP);
                    } else {

                        ArrayList<String> stringArr = new ArrayList<String>();
                        while (!oneString.startsWith(tag) && i < stringList.size()) {
                            stringArr.add(oneString);
                            i++;
                            if (i >= stringList.size()) {
                                break;
                            } else {
                                oneString = stringList.get(i);
                            }
                        }
                        for (Element aElem : getThirdGeneral(stringArr, this.id)) {
                            sec.appendChild(aElem);
                        }

                    }

                }

                secList.add(sec);
                i--;
            }
        }
        return secList;
    }

    public List<Element> getThirdGeneral(ArrayList<String> stringList, String theIndex) {

        List<Element> secList = new ArrayList();

        String tag = "[s2]";
        int inndex = 0;
        for (int i = 0; i < stringList.size(); i++) {
            String oneString = stringList.get(i);

            while (!oneString.startsWith(tag) && i < stringList.size()) {

                if (oneString.trim().startsWith("1)")) {
                    Element listP = doc.createElement("p");

                    Element listP_order = doc.createElement("list");
                    listP_order.setAttribute("list-type", "order");
                    listP.appendChild(listP_order);
                    Matcher matcher
                            = listPattern.matcher(oneString.trim());
                    while (matcher.matches()) {

                        Element listP_item = doc.createElement("list-item");
                        listP_order.appendChild(listP_item);
                        Element listPP = doc.createElement("p");
                        listP_item.appendChild(listPP);
                        String[] oneStringStr = oneString.split("\\)");
                        listPP.appendChild(doc.createTextNode(oneStringStr[1].trim()));
                        i++;
                        if (i >= stringList.size()) {
                            break;
                        } else {
                            oneString = stringList.get(i);
                            matcher
                                    = listPattern.matcher(oneString.trim());
                        }
                    }
                    i--;
                    secList.add(listP);
                } else {

                    ArrayList<Element> elemList = addTableFigure(oneString);

                    secList.add(elemList.get(elemList.size() - 1));
                    for (int x = 0; x < elemList.size() - 1; x++) {
                        secList.add(elemList.get(x));
                    }

                }

                i++;
                if (i >= stringList.size()) {
                    break;
                } else {
                    oneString = stringList.get(i);
                }
            }

            if (oneString.startsWith(tag)) {
                inndex++;
                this.title = oneString.replace(tag, "");
                if (this.title.trim().length() < 1) {
                    if (Main.refs != null && Main.refs.size() > 0) {
                        Main.error(oneString + " must be followed by a title.");
                    } else if (RefDiviedMain.refs != null && RefDiviedMain.refs.size() > 0) {
                        RefDiviedMain.error(oneString + " must be followed by a title.");
                    } else if (RefSouceOnlyMain.refs != null && RefSouceOnlyMain.refs.size() > 0) {
                        RefSouceOnlyMain.error(oneString + " must be followed by a title.");
                    }
                }
                String theType = this.title;
                this.id = theIndex + inndex;
                Element sec = doc.createElement("sec");
                sec.setAttribute("id", this.id);
                if(secIndex==2 && isAbc(this.seletion)) {
                    sec.setAttribute("sec-type", "materials | methods");
                 }
                Element secTitle = doc.createElement("title");
                sec.appendChild(secTitle);
                secTitle.appendChild(doc.createTextNode(title));

                i++;
                oneString = stringList.get(i);
                while (!oneString.startsWith(tag) && i < stringList.size()) {
                    ArrayList<Element> elemList = null;
                    if (oneString.trim().startsWith("1)")) {
                        Element listP = doc.createElement("p");

                        Element listP_order = doc.createElement("list");
                        listP_order.setAttribute("list-type", "order");
                        listP.appendChild(listP_order);
                        Matcher matcher
                                = listPattern.matcher(oneString.trim());
                        while (matcher.matches()) {

                            Element listP_item = doc.createElement("list-item");
                            listP_order.appendChild(listP_item);
                            Element listPP = doc.createElement("p");
                            listP_item.appendChild(listPP);
                            String[] oneStringStr = oneString.split("\\)");
                            listPP.appendChild(doc.createTextNode(oneStringStr[1].trim()));
                            i++;
                            if (i >= stringList.size()) {
                                break;
                            } else {
                                oneString = stringList.get(i);
                                matcher
                                        = listPattern.matcher(oneString.trim());
                            }
                        }
                        i--;
                        sec.appendChild(listP);
                    } else {

                        elemList = addTableFigure(oneString);

                        sec.appendChild(elemList.get(elemList.size() - 1));
                        for (int x = 0; x < elemList.size() - 1; x++) {
                            sec.appendChild(elemList.get(x));
                        }

                        i++;
                        if (i >= stringList.size()) {
                            break;
                        } else {
                            oneString = stringList.get(i);
                        }

                    }

                }

                secList.add(sec);
                i--;
            }
        }
        return secList;
    }

    public List<Element> getAbstract(ArrayList<String> stringList) {

        List<Element> secList = new ArrayList();
        int secIndex = 0;

        String tag = "[s1]";
        for (int i = 0; i < stringList.size(); i++) {
            String oneString = stringList.get(i);

            while (!oneString.startsWith(tag) && i < stringList.size()) {

                ArrayList<Element> elemList = addTableFigure(oneString);
                secList.add(elemList.get(elemList.size() - 1));
                for (int x = 0; x < elemList.size() - 1; x++) {
                    secList.add(elemList.get(x));
                }
                i++;
                if (i >= stringList.size()) {
                    break;
                } else {
                    oneString = stringList.get(i);
                }
            }

            if (oneString.startsWith(tag)) {
                secIndex++;
                this.title = oneString.replace(tag, "");
                if (this.title.trim().length() < 1) {
                    if (Main.refs != null && Main.refs.size() > 0) {
                        Main.error(oneString + " must be followed by a title.");
                    } else if (RefDiviedMain.refs != null && RefDiviedMain.refs.size() > 0) {
                        RefDiviedMain.error(oneString + " must be followed by a title.");
                    } else if (RefSouceOnlyMain.refs != null && RefSouceOnlyMain.refs.size() > 0) {
                        RefSouceOnlyMain.error(oneString + " must be followed by a title.");
                    }
                }
                this.id = "a" + secIndex;
                Element sec = doc.createElement("sec");
                sec.setAttribute("id", this.id);
                if(secIndex==2 && isAbc(seletion)) {
                    sec.setAttribute("sec-type", "materials | methods");
                 }
                Element secTitle = doc.createElement("title");
                sec.appendChild(secTitle);
                secTitle.appendChild(doc.createTextNode(title));

                i++;
                if (i >= stringList.size()) {
                    break;
                }
                oneString = stringList.get(i);
                while (!oneString.startsWith(tag) && i < stringList.size()) {

                    ArrayList<Element> elemList = addTableFigure(oneString);
                    sec.appendChild(elemList.get(elemList.size() - 1));
                    for (int x = 0; x < elemList.size() - 1; x++) {
                        sec.appendChild(elemList.get(x));
                    }
                    i++;
                    if (i >= stringList.size()) {
                        break;
                    } else {
                        oneString = stringList.get(i);
                    }
                }

                secList.add(sec);
                i--;
            }
        }
        return secList;
    }

    public ArrayList<Node> addInsideTableFigure(String a) {

        System.out.println("add table or figure from:" + a);

        ArrayList<Node> elemArr = new ArrayList<Node>();

        //Element p = doc.createElement("title");
        Matcher matcher
                = pattern.matcher(a);

        boolean found = false;

        int lastIndex = 0;

        while (matcher.find()) {

            System.out.println("I found " + matcher.group() + " the start index is " + matcher.start() + " the last index is " + matcher.end());

            if ((matcher.start() > 0) && (lastIndex < matcher.start())) {
                elemArr.add(doc.createTextNode(a.substring(lastIndex, matcher.start())));
            }

            String matchString = matcher.group();

            String temp = "";

            String prefix = "";

            String profix = "";

            if (matchString.contains(")")) {

                profix = ")";

            }

            if (matchString.startsWith(" ")) {
                prefix = " " + prefix;
            }
            if (matchString.endsWith(" ")) {
                profix = profix + " ";
            } else if (matchString.endsWith(".")) {
                System.out.println("end with .");
                matchString = matchString.substring(0, matchString.length() - 1);
                profix = profix + ".";
            } else if (matchString.endsWith(",")) {
                System.out.println("end with ,");
                matchString = matchString.substring(0, matchString.length() - 1);
                profix = profix + ",";
            } else if (matchString.endsWith(";")) {
                System.out.println("end with ;");
                matchString = matchString.substring(0, matchString.length() - 1);
                profix = profix + ";";
            } else if (matchString.endsWith(":")) {
                System.out.println("end with :");
                matchString = matchString.substring(0, matchString.length() - 1);
                profix = profix + ":";
            }

            System.out.println("get converted string:" + matchString);
            matchString = matchString.trim();

            temp = matchString.replaceAll("\\)", "").replaceAll("\\(", "").trim();

            String[] tempArr = temp.split("\\s+");

            elemArr.add(doc.createTextNode(prefix));

            if (matchString.contains("[")) {

                String tempemp = matchString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "");

                elemArr.add(doc.createTextNode("["));

                String[] tempempArr = null;

                System.out.println("find out the reference index " + tempemp);

                if (tempemp.contains(",")) {

                    System.out.println("this index includes ,");

                    tempempArr = tempemp.split(",");
                    int index = 0;

                    for (String aTemp : tempempArr) {

                        System.out.println(index + ":" + aTemp);

                        if (index != 0) {
                            elemArr.add(doc.createTextNode(", "));
                        }

                        elemArr.add(addMultipleRefs(aTemp));

                        index++;
                    }
                } else if (tempemp.contains("-")) {

                    tempempArr = tempemp.split("-");

                    String tempStart = tempempArr[0];

                    Element tempE = doc.createElement("xref");

                    tempE.setAttribute("ref-type", "bibr");
                    if (tempStart.length() == 1) {
                        tempStart = "0" + tempStart;
                    }

                    tempE.setAttribute("rid", "R" + tempStart);

                    tempE.appendChild(doc.createTextNode(tempemp));

                    elemArr.add(tempE);

                } else {

                    tempempArr = new String[1];

                    tempempArr[0] = tempemp;

                    String tempStart = tempempArr[0];

                    Element tempE = doc.createElement("xref");

                    tempE.setAttribute("ref-type", "bibr");

                    String temptemptemp = tempStart;

                    if (tempStart.length() == 1) {
                        tempStart = "0" + tempStart;
                    }

                    tempE.setAttribute("rid", "R" + tempStart);

                    tempE.appendChild(doc.createTextNode(temptemptemp));

                    elemArr.add(tempE);

                }

                elemArr.add(doc.createTextNode("]"));

            }

            elemArr.add(doc.createTextNode(profix));

            lastIndex = matcher.end();

            found = true;

        }

        if (!found) {

            //p.appendChild();
            elemArr.add(doc.createTextNode(a));

        } else {

            if (lastIndex < (a.length() - 1)) {

                elemArr.add(doc.createTextNode(a.substring(lastIndex, a.length())));

            } else {

                // p.appendChild(doc.createTextNode(a.substring(a.length() - 1, a.length())));
                elemArr.add(doc.createTextNode(a.substring(lastIndex, a.length())));

            }

            //elemArr.add(p);
        }

        return elemArr;

    }

    public ArrayList<Element> addTableFigure(String a) {

        System.out.println("add table or figure from:" + a);

        ArrayList<Element> elemArr = new ArrayList<Element>();

        Element p = doc.createElement("p");

        Matcher matcher
                = pattern.matcher(a);

        boolean found = false;

        int lastIndex = 0;

        while (matcher.find()) {

            System.out.println("I found " + matcher.group() + " the start index is " + matcher.start() + " the last index is " + matcher.end());

            if ((matcher.start() > 0) && (lastIndex < matcher.start())) {
                p.appendChild(doc.createTextNode(a.substring(lastIndex, matcher.start())));
            }

            String matchString = matcher.group();

            String temp = "";

            String prefix = "";

            String profix = "";

            if (matchString.contains(")")) {

                profix = ")";

            }

            if (matchString.startsWith(" ")) {
                prefix = " " + prefix;
            }
            if (matchString.endsWith(" ")) {
                profix = profix + " ";
            } else if (matchString.endsWith(".")) {
                System.out.println("end with .");
                matchString = matchString.substring(0, matchString.length() - 1);
                profix = profix + ".";
            } else if (matchString.endsWith(",")) {
                System.out.println("end with ,");
                matchString = matchString.substring(0, matchString.length() - 1);
                profix = profix + ",";
            } else if (matchString.endsWith(";")) {
                System.out.println("end with ;");
                matchString = matchString.substring(0, matchString.length() - 1);
                profix = profix + ";";
            } else if (matchString.endsWith(":")) {
                System.out.println("end with :");
                matchString = matchString.substring(0, matchString.length() - 1);
                profix = profix + ":";
            }

            System.out.println("get converted string:" + matchString);
            matchString = matchString.trim();

            temp = matchString.replaceAll("\\)", "").replaceAll("\\(", "").trim();

            String[] tempArr = temp.split("\\s+");

            p.appendChild(doc.createTextNode(prefix));

            if (tempArr[0].contains("Table")) {

                tempArr[1] = tempArr[1].replaceAll("[^0-9a-zA-Z]", "");
                
                List<String> tableList = null;

                if (Main.refs != null && Main.refs.size() > 0) {
                    if (Main.table.size() == 0) {
                        Main.error("Don't find table list at the end of document.");
                    }
                     try {

                    //   System.out.println("try to convert "+(Integer.parseInt(tempArr[1].trim())-1)+" INTO table which has size:"+Main.table.size());
                    tableList = (List<String>) Main.table.get(Integer.valueOf(tempArr[1].trim()).intValue() - 1);
                } catch (NumberFormatException e) {
                    System.out.println(tempArr[1].trim() + " has issue.");
                    e.printStackTrace();
                }
                } else if (RefDiviedMain.refs != null && RefDiviedMain.refs.size() > 0) {
                    if (RefDiviedMain.table.size() == 0) {
                        RefDiviedMain.error("Don't find table list at the end of document.");
                    }
                     try {

                    //   System.out.println("try to convert "+(Integer.parseInt(tempArr[1].trim())-1)+" INTO table which has size:"+Main.table.size());
                    tableList = (List<String>) RefDiviedMain.table.get(Integer.valueOf(tempArr[1].trim()).intValue() - 1);
                } catch (NumberFormatException e) {
                    System.out.println(tempArr[1].trim() + " has issue.");
                    e.printStackTrace();
                }
                } else if (RefSouceOnlyMain.refs != null && RefSouceOnlyMain.refs.size() > 0) {
                    if (RefSouceOnlyMain.table.size() == 0) {
                        RefSouceOnlyMain.error("Don't find table list at the end of document.");
                    }
                     try {

                    //   System.out.println("try to convert "+(Integer.parseInt(tempArr[1].trim())-1)+" INTO table which has size:"+Main.table.size());
                    tableList = (List<String>) RefSouceOnlyMain.table.get(Integer.valueOf(tempArr[1].trim()).intValue() - 1);
                } catch (NumberFormatException e) {
                    System.out.println(tempArr[1].trim() + " has issue.");
                    e.printStackTrace();
                }
                }

                
               

                Element tempE = doc.createElement("xref");

                tempE.setAttribute("ref-type", "table");

                tempE.setAttribute("rid", "T" + tempArr[1]);

                tempE.appendChild(doc.createTextNode(temp));

                p.appendChild(tempE);

                if (Main.refs != null && Main.refs.size() > 0) {
                    if (!Main.tableAlready.contains(tempArr[1])) {

                        Main.tableAlready.add(tempArr[1]);

                        Element tableWrap = doc.createElement("table-wrap");

                        tableWrap.setAttribute("id", "T" + tempArr[1]);

                        tableWrap.setAttribute("position", "float");

                        Element tableTitle = doc.createElement("label");

                        tableTitle.appendChild(doc.createTextNode(temp));

                        tableWrap.appendChild(tableTitle);

                        Element tableCap = doc.createElement("caption");
                        Element tableCapTitle1 = doc.createElement("title");

                        ArrayList<Node> temptempElemArr = addInsideTableFigure(tableList.get(1));

                        for (Node aa : temptempElemArr) {
                            tableCapTitle1.appendChild(aa);
                        }
                        tableCap.appendChild(tableCapTitle1);

                        tableWrap.appendChild(tableCap);

                //Element tableFrame = doc.createElement("table");
                        Element tableFrame = Main.getTable(temp);
                        if (tableFrame != null) {
                            tableWrap.appendChild(tableFrame);
                        }

                //tableFrame.setAttribute("frame", "hsides");
                //tableFrame.setAttribute("rules", "groups");
                        Element tableWrapFoot = doc.createElement("table-wrap-foot");

                        Element tableWrapFn = doc.createElement("fn");

                        tableWrapFoot.appendChild(tableWrapFn);

                        boolean addFootnote = false;
                        if (tableList.get(2).contains("aaaaa")) {
                            String[] aaaaaList = tableList.get(2).split("aaaaa");
                            for (String aString : aaaaaList) {
                                Element tableWrapFnP = doc.createElement("p");
                                tableWrapFn.appendChild(tableWrapFnP);
                                tableWrapFnP.appendChild(doc.createTextNode(aString));
                                if (aString != null && !aString.trim().equals("")) {
                                    addFootnote = true;
                                }
                            }
                        } else {
                            Element tableWrapFnP = doc.createElement("p");

                            tableWrapFn.appendChild(tableWrapFnP);
                            tableWrapFnP.appendChild(doc.createTextNode(tableList.get(2)));
                            if (tableList.get(2) != null && !tableList.get(2).trim().equals("")) {
                                addFootnote = true;
                            }
                        }
                        if (addFootnote) {
                            tableWrap.appendChild(tableWrapFoot);
                        }
                        elemArr.add(tableWrap);
                    }
                } else if (RefDiviedMain.refs != null && RefDiviedMain.refs.size() > 0) {
                    if (!RefDiviedMain.tableAlready.contains(tempArr[1])) {

                        RefDiviedMain.tableAlready.add(tempArr[1]);

                        Element tableWrap = doc.createElement("table-wrap");

                        tableWrap.setAttribute("id", "T" + tempArr[1]);

                        tableWrap.setAttribute("position", "float");

                        Element tableTitle = doc.createElement("label");

                        tableTitle.appendChild(doc.createTextNode(temp));

                        tableWrap.appendChild(tableTitle);

                        Element tableCap = doc.createElement("caption");
                        Element tableCapTitle1 = doc.createElement("title");

                        ArrayList<Node> temptempElemArr = addInsideTableFigure(tableList.get(1));

                        for (Node aa : temptempElemArr) {
                            tableCapTitle1.appendChild(aa);
                        }
                        tableCap.appendChild(tableCapTitle1);

                        tableWrap.appendChild(tableCap);

                //Element tableFrame = doc.createElement("table");
                        Element tableFrame = RefDiviedMain.getTable(temp);
                        if (tableFrame != null) {
                            tableWrap.appendChild(tableFrame);
                        }

                //tableFrame.setAttribute("frame", "hsides");
                //tableFrame.setAttribute("rules", "groups");
                        Element tableWrapFoot = doc.createElement("table-wrap-foot");

                        Element tableWrapFn = doc.createElement("fn");

                        tableWrapFoot.appendChild(tableWrapFn);

                        boolean addFootnote = false;
                        if (tableList.get(2).contains("aaaaa")) {
                            String[] aaaaaList = tableList.get(2).split("aaaaa");
                            for (String aString : aaaaaList) {
                                Element tableWrapFnP = doc.createElement("p");
                                tableWrapFn.appendChild(tableWrapFnP);
                                tableWrapFnP.appendChild(doc.createTextNode(aString));
                                if (aString != null && !aString.trim().equals("")) {
                                    addFootnote = true;
                                }
                            }
                        } else {
                            Element tableWrapFnP = doc.createElement("p");

                            tableWrapFn.appendChild(tableWrapFnP);
                            tableWrapFnP.appendChild(doc.createTextNode(tableList.get(2)));
                            if (tableList.get(2) != null && !tableList.get(2).trim().equals("")) {
                                addFootnote = true;
                            }
                        }
                        if (addFootnote) {
                            tableWrap.appendChild(tableWrapFoot);
                        }
                        elemArr.add(tableWrap);
                    }
                } else if (RefSouceOnlyMain.refs != null && RefSouceOnlyMain.refs.size() > 0) {
                    if (!RefSouceOnlyMain.tableAlready.contains(tempArr[1])) {

                        RefSouceOnlyMain.tableAlready.add(tempArr[1]);

                        Element tableWrap = doc.createElement("table-wrap");

                        tableWrap.setAttribute("id", "T" + tempArr[1]);

                        tableWrap.setAttribute("position", "float");

                        Element tableTitle = doc.createElement("label");

                        tableTitle.appendChild(doc.createTextNode(temp));

                        tableWrap.appendChild(tableTitle);

                        Element tableCap = doc.createElement("caption");
                        Element tableCapTitle1 = doc.createElement("title");

                        ArrayList<Node> temptempElemArr = addInsideTableFigure(tableList.get(1));

                        for (Node aa : temptempElemArr) {
                            tableCapTitle1.appendChild(aa);
                        }
                        tableCap.appendChild(tableCapTitle1);

                        tableWrap.appendChild(tableCap);

                //Element tableFrame = doc.createElement("table");
                        Element tableFrame = RefSouceOnlyMain.getTable(temp);
                        if (tableFrame != null) {
                            tableWrap.appendChild(tableFrame);
                        }

                //tableFrame.setAttribute("frame", "hsides");
                //tableFrame.setAttribute("rules", "groups");
                        Element tableWrapFoot = doc.createElement("table-wrap-foot");

                        Element tableWrapFn = doc.createElement("fn");

                        tableWrapFoot.appendChild(tableWrapFn);

                        boolean addFootnote = false;
                        if (tableList.get(2).contains("aaaaa")) {
                            String[] aaaaaList = tableList.get(2).split("aaaaa");
                            for (String aString : aaaaaList) {
                                Element tableWrapFnP = doc.createElement("p");
                                tableWrapFn.appendChild(tableWrapFnP);
                                tableWrapFnP.appendChild(doc.createTextNode(aString));
                                if (aString != null && !aString.trim().equals("")) {
                                    addFootnote = true;
                                }
                            }
                        } else {
                            Element tableWrapFnP = doc.createElement("p");

                            tableWrapFn.appendChild(tableWrapFnP);
                            tableWrapFnP.appendChild(doc.createTextNode(tableList.get(2)));
                            if (tableList.get(2) != null && !tableList.get(2).trim().equals("")) {
                                addFootnote = true;
                            }
                        }
                        if (addFootnote) {
                            tableWrap.appendChild(tableWrapFoot);
                        }
                        elemArr.add(tableWrap);
                    }
                }

            } else if (tempArr[0].contains("Fig.") || tempArr[0].contains("Figure")) {

               
                
                if (Main.refs != null && Main.refs.size() > 0) {
                     if (Main.figure.size() == 0) {
                    Main.error("Don't find figure list at the end of document.");
                }
                } else if (RefDiviedMain.refs != null && RefDiviedMain.refs.size() > 0) {
                     if (RefDiviedMain.figure.size() == 0) {
                    RefDiviedMain.error("Don't find figure list at the end of document.");
                }
                } else if (RefSouceOnlyMain.refs != null && RefSouceOnlyMain.refs.size() > 0) {
                     if (RefSouceOnlyMain.figure.size() == 0) {
                    RefSouceOnlyMain.error("Don't find figure list at the end of document.");
                }
                }

                ArrayList<String> figArray = new ArrayList<String>();

                if (tempArr[1].contains("-")) {
                    String[] aaaaa = tempArr[1].split("-");
                    int bbbbbb = Integer.valueOf(aaaaa[0]).intValue();
                    int ccccc = Integer.valueOf(aaaaa[1]).intValue();
                    ArrayList<String> figListList = new ArrayList<String>();
                    for (int i = bbbbbb; i <= ccccc; i++) {
                        figListList.add(i + "");
                    }

                    figArray.addAll(figListList);

                } else if (tempArr[1].contains(",")) {
                    String[] b = tempArr[1].split(",");
                    figArray.addAll(Arrays.asList(b));
                } else {
                    figArray.add(tempArr[1]);
                }

                String preIndex = "";
                for (String aFig : figArray) {

                    String aFigTemp = aFig;
                    aFig = aFig.replaceAll("[a-z]", "").replaceAll("[A-Z]", "");
                    if (aFig.trim().length() > 0) {
                        preIndex = aFig;
                    } else {
                        aFig = preIndex;
                        aFigTemp = aFig + aFigTemp;
                    }
                    
                     List<String> tableList = new ArrayList<String>();
                      if (Main.refs != null && Main.refs.size() > 0) {
                     tableList = (List<String>) Main.figure.get(Integer.valueOf(aFig).intValue() - 1);
                }
                   else if (RefDiviedMain.refs != null && RefDiviedMain.refs.size() > 0) {
                     tableList = (List<String>) RefDiviedMain.figure.get(Integer.valueOf(aFig).intValue() - 1);
                } else if (RefSouceOnlyMain.refs != null && RefSouceOnlyMain.refs.size() > 0) {
                     tableList = (List<String>) RefSouceOnlyMain.figure.get(Integer.valueOf(aFig).intValue() - 1);
                }
                    
                   
                    Element tempE = doc.createElement("xref");

                    tempE.setAttribute("ref-type", "fig");

                    tempE.setAttribute("rid", "F" + aFig);

                    if (tempArr[0].contains("Figure")) {
                        tempE.appendChild(doc.createTextNode("Figure " + aFigTemp));
                    } else {
                        tempE.appendChild(doc.createTextNode("Fig. " + aFigTemp));
                    }

                    p.appendChild(tempE);
                    
                    
                    if (Main.refs != null && Main.refs.size() > 0 && !Main.figAlready.contains(aFig)) {

                        Main.figAlready.add(aFig);

                        Element tableWrap = doc.createElement("fig");

                        tableWrap.setAttribute("id", "F" + aFig);

                        tableWrap.setAttribute("position", "float");

                        Element tableTitle = doc.createElement("label");

                        tableTitle.appendChild(doc.createTextNode("Figure " + aFig));

                        tableWrap.appendChild(tableTitle);

                        Element tableCap = doc.createElement("caption");
                        Element tableCapTitle1 = doc.createElement("p");

                        tableCapTitle1.appendChild(doc.createTextNode(tableList.get(1)));
                        tableCap.appendChild(tableCapTitle1);

                        tableWrap.appendChild(tableCap);

                        Element tableCapGraphic = doc.createElement("graphic");

                        tableCapGraphic.setAttribute("xlink:href", tableList.get(2).trim());

                        tableWrap.appendChild(tableCapGraphic);

                        elemArr.add(tableWrap);

                    } else if (RefDiviedMain.refs != null && RefDiviedMain.refs.size() > 0 && !RefDiviedMain.figAlready.contains(aFig)) {

                        RefDiviedMain.figAlready.add(aFig);

                        Element tableWrap = doc.createElement("fig");

                        tableWrap.setAttribute("id", "F" + aFig);

                        tableWrap.setAttribute("position", "float");

                        Element tableTitle = doc.createElement("label");

                        tableTitle.appendChild(doc.createTextNode("Figure " + aFig));

                        tableWrap.appendChild(tableTitle);

                        Element tableCap = doc.createElement("caption");
                        Element tableCapTitle1 = doc.createElement("p");

                        tableCapTitle1.appendChild(doc.createTextNode(tableList.get(1)));
                        tableCap.appendChild(tableCapTitle1);

                        tableWrap.appendChild(tableCap);

                        Element tableCapGraphic = doc.createElement("graphic");

                        tableCapGraphic.setAttribute("xlink:href", tableList.get(2).trim());

                        tableWrap.appendChild(tableCapGraphic);

                        elemArr.add(tableWrap);

                    } else if (RefSouceOnlyMain.refs != null && RefSouceOnlyMain.refs.size() > 0 && !RefSouceOnlyMain.figAlready.contains(aFig)) {

                        RefSouceOnlyMain.figAlready.add(aFig);

                        Element tableWrap = doc.createElement("fig");

                        tableWrap.setAttribute("id", "F" + aFig);

                        tableWrap.setAttribute("position", "float");

                        Element tableTitle = doc.createElement("label");

                        tableTitle.appendChild(doc.createTextNode("Figure " + aFig));

                        tableWrap.appendChild(tableTitle);

                        Element tableCap = doc.createElement("caption");
                        Element tableCapTitle1 = doc.createElement("p");

                        tableCapTitle1.appendChild(doc.createTextNode(tableList.get(1)));
                        tableCap.appendChild(tableCapTitle1);

                        tableWrap.appendChild(tableCap);

                        Element tableCapGraphic = doc.createElement("graphic");

                        tableCapGraphic.setAttribute("xlink:href", tableList.get(2).trim());

                        tableWrap.appendChild(tableCapGraphic);

                        elemArr.add(tableWrap);

                    }
                }

            } else if (matchString.contains("[")) {

                String tempemp = matchString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "");

                p.appendChild(doc.createTextNode("["));

                String[] tempempArr = null;

                System.out.println("find out the reference index " + tempemp);

                if (tempemp.contains(",")) {

                    System.out.println("this index includes ,");

                    tempempArr = tempemp.split(",");
                    int index = 0;

                    for (String aTemp : tempempArr) {

                        System.out.println(index + ":" + aTemp);

                        if (index != 0) {
                            p.appendChild(doc.createTextNode(", "));
                        }

                        p.appendChild(addMultipleRefs(aTemp));

                        index++;
                    }
                } else if (tempemp.contains("-")) {

                    tempempArr = tempemp.split("-");

                    String tempStart = tempempArr[0];

                    Element tempE = doc.createElement("xref");

                    tempE.setAttribute("ref-type", "bibr");
                    if (tempStart.length() == 1) {
                        tempStart = "0" + tempStart;
                    }

                    tempE.setAttribute("rid", "R" + tempStart);

                    tempE.appendChild(doc.createTextNode(tempemp));

                    p.appendChild(tempE);

                } else {

                    tempempArr = new String[1];

                    tempempArr[0] = tempemp;

                    String tempStart = tempempArr[0];

                    Element tempE = doc.createElement("xref");

                    tempE.setAttribute("ref-type", "bibr");

                    String temptemptemp = tempStart;

                    if (tempStart.length() == 1) {
                        tempStart = "0" + tempStart;
                    }

                    tempE.setAttribute("rid", "R" + tempStart);

                    tempE.appendChild(doc.createTextNode(temptemptemp));

                    p.appendChild(tempE);

                }

                p.appendChild(doc.createTextNode("]"));

            }

            p.appendChild(doc.createTextNode(profix));

            lastIndex = matcher.end();

            found = true;

        }

        if (!found) {

            p.appendChild(doc.createTextNode(a));

            elemArr.add(p);

        } else {

            if (lastIndex < (a.length() - 1)) {

                p.appendChild(doc.createTextNode(a.substring(lastIndex, a.length())));

            } else {

                // p.appendChild(doc.createTextNode(a.substring(a.length() - 1, a.length())));
                p.appendChild(doc.createTextNode(a.substring(lastIndex, a.length())));

            }

            elemArr.add(p);

        }

        return elemArr;

    }

    private Element addMultipleRefs(String tempemp) {

        String[] tempempArr = null;

        Element tempE = null;

        if (tempemp.contains("-")) {

            tempempArr = tempemp.split("-");

            String tempStart = tempempArr[0];

            tempE = doc.createElement("xref");

            tempE.setAttribute("ref-type", "bibr");
            if (tempStart.length() == 1) {
                tempStart = "0" + tempStart;
            }

            tempE.setAttribute("rid", "R" + tempStart);

            tempE.appendChild(doc.createTextNode(tempemp));

                    //p.appendChild(tempE);
        } else {

            tempempArr = new String[1];

            tempempArr[0] = tempemp;

            String tempStart = tempempArr[0];

            tempE = doc.createElement("xref");

            tempE.setAttribute("ref-type", "bibr");

            String temptemptemp = tempStart;

            if (tempStart.length() == 1) {
                tempStart = "0" + tempStart;
            }

            tempE.setAttribute("rid", "R" + tempStart);

            tempE.appendChild(doc.createTextNode(temptemptemp));

                    //p.appendChild(tempE);
        }
        return tempE;
    }

}
