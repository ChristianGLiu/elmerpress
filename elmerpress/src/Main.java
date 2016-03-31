
import java.io.BufferedInputStream;
import java.io.BufferedWriter;

import java.io.File;

import java.io.FileInputStream;

import java.io.FileWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.io.Writer;
import java.lang.Character.UnicodeBlock;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import java.util.*;

import java.util.logging.Level;

import java.util.logging.Logger;

import java.util.regex.Matcher;

import java.util.regex.Pattern;



import javax.swing.JOptionPane;
import javax.swing.JTextArea;

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
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellReference;

public class Main {

    public static javax.swing.JTextArea ta;
    public static String title = "";
    public static ArrayList<String> refs = new ArrayList<String>();
    public static String shortTitle = "";
    public static String doi = "";
    public static String ppub = "";
    public static String epub = "";
    public static String volume = "";
    public static String issue = "";
    public static String fpage = "";
    public static String lpage = "";
    public static String manuscriptDateAccepted = "";
    public static String dateAccepted = "";
    public static String copyrightStat = "";
    public static String copyrightYear = "";
    public static String isOriginal = "";
    public static ArrayList<String> abstractArr = new ArrayList<String>();
    public static ArrayList<String> keywordArr = new ArrayList<String>();
    public static ArrayList<String> discussion = new ArrayList<String>();
    public static ArrayList<String> acknowledgement = new ArrayList<String>();
    public static ArrayList<String> disclosure = new ArrayList<String>();
    public static ArrayList<String> figAlready = new ArrayList<String>();
    public static ArrayList<String> tableAlready = new ArrayList<String>();
    public static HashMap<String,String> articleType = new HashMap<String, String>();                                      
    public static ArrayList table = new ArrayList();
    public static ArrayList figure = new ArrayList();
    public static ArrayList<String> authors = new ArrayList<String>();
    public static ArrayList<String> references = new ArrayList<String>();
    public static DocSegment aDoc;
    //Matcher matcher =
    //       pattern.matcher(console.readLine("Enter input string to search: "));
    public static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    public static Document doc;
    public static String journalTitle = "";
    public static boolean isValidBoday;
    public static boolean isValidBack;
    public static boolean isValidRefs;
    public static String magzineTitle;
    public static String magzineShortTitle;
    public static String issn_ppub;
    public static String issn_epub;
    public static String textNote="This is an open-access article distributed under the terms of the Creative Commons Attribution-NonCommercial 4.0 International License, which permits unrestricted non-commercial use, distribution, and reproduction in any medium, provided the original work is properly cited.";

    public static void cleanArrs() {

    	if(ta!=null) {
        ta.setText("");
        ta.append("cleaning arrays \n");}

        isValidBoday = false;
        isValidBack = false;
        isValidRefs = false;

        
    if(refs!=null)
        refs.clear();
if(figAlready!=null)
        figAlready.clear();
if(abstractArr!=null)
        abstractArr.clear();
        if(keywordArr!=null)
        keywordArr.clear();

if(discussion!=null)
        discussion.clear();

if(acknowledgement!=null)
        acknowledgement.clear();
if(disclosure!=null)
        disclosure.clear();
if(table!=null)
        table.clear();
if(figure!=null)
        figure.clear();
if(authors!=null)
        authors.clear();
if(references!=null)
        references.clear();
        if(articleType!=null)
        articleType.clear();



    }
    public static void init(String fileName) {



        ta.append("reading doc......\n");

        Main.readMyDocument(fileName);



        articleType.put("Original Article","research-article");
        articleType.put("Case Report","case-report");
        articleType.put("Review","review-article");
        articleType.put("Letter to the Editor","letter");
        articleType.put("Short Communication","rapid-communication");
        articleType.put("Meeting Report","meeting-report");
        articleType.put("News","news");
        articleType.put("Editorial","editorial");
        articleType.put("Book Review","book-review");
        articleType.put("Abstract","abstract");
        articleType.put("Announcement","announcement");
        articleType.put("Addendum","addendum");
        articleType.put("Article Commentary","article-commentary");
        articleType.put("Brief Report","brief-report");
        articleType.put("Calendar","calendar");
        articleType.put("Discussion","discussion");
        articleType.put("In Brief","in-brief");
        articleType.put("Introduction","Introduction");
        articleType.put("Partial Retraction","partial-retraction");
        articleType.put("Product Review","product-review");
        articleType.put("Retraction","retraction");
        articleType.put("Retraction","retraction");
        articleType.put("Translation","translation");
        articleType.put("Reprint","reprint");
        articleType.put("Oration","oration");
        articleType.put("Correction","correction");
        articleType.put("Obituary","obituary");
        articleType.put("Books Received","books-received");
        articleType.put("Collection","collection");
        articleType.put("Translation","translation");
        articleType.put("Dissertation","dissertation");
        articleType.put("Reply","reply");
        
        
        
        



        ta.append("trim the array elements....\n");

        abstractArr = Main.arrayCleaner(abstractArr);
        if (abstractArr.size() > 0) {
            abstractArr.remove(0);
        }
        
        keywordArr = Main.arrayCleaner(keywordArr);
        if (keywordArr.size() > 0) {
            keywordArr.remove(0);
        }

        discussion = Main.arrayCleaner(discussion);
        if (discussion.size() > 0) {
            discussion.remove(0);
        }

        acknowledgement = Main.arrayCleaner(acknowledgement);

        disclosure = Main.arrayCleaner(disclosure);
        if (disclosure.size() > 0) {
            disclosure.remove(0);
        }

        //  table = Main.arrayCleaner(table);

        //figure = Main.arrayCleaner(figure);

        authors = Main.arrayCleaner(authors);
        //  authors.remove(0);

        references = Main.arrayCleaner(references);

        DocumentBuilder db = null;

        try {

            db = dbf.newDocumentBuilder();

        } catch (ParserConfigurationException ex) {
        	ex.printStackTrace();

            ta.append("\nerrors happen:\n"+ex.getMessage());
            ta.append(ex.getMessage() + "\n");

        }



        doc = db.newDocument();
        

        Element root = doc.createElement("article");

        root.setAttribute("xmlns:mml", "http://www.w3.org/1998/Math/MathML");

        root.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        
        if(articleType.get(Main.isOriginal) == null ) {

            root.setAttribute("article-type", Main.isOriginal);
        } else {
            root.setAttribute("article-type", articleType.get(Main.isOriginal));
        }
        
        root.appendChild(generateFront());

        root.appendChild(generateBody());

        root.appendChild(generateBack());



        doc.appendChild(root);



        DOMSource domSource = new DOMSource(doc);

        TransformerFactory tf = TransformerFactory.newInstance();


        Transformer transformer = null;

        try {

            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//NLM//DTD Journal Publishing DTD v3.0 20080202//EN");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "journalpublishing3.dtd");

        } catch (TransformerConfigurationException ex) {
        	ex.printStackTrace();

            ta.append("\nerrors happen:\n"+ex.getMessage());
            ta.append(ex.getMessage() + "\n");

        }





        java.io.StringWriter sw = new java.io.StringWriter();

        StreamResult sr = new StreamResult(sw);


        try {

            transformer.transform(domSource, sr);

        } catch (TransformerException ex) {
        	ex.printStackTrace();

            ta.append("\nerrors happen:\n"+ex.getMessage());
            ta.append(ex.getMessage() + "\n");

        }



        String xml = sw.toString();

        //xml = xml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>", "");

        ta.append("\n" + xml);
        // finalXMLString = "<!DOCTYPE article PUBLIC \"-//NLM//DTD Journal Publishing DTD v3.0 20080202//EN\" \"http://dtd.nlm.nih.gov/publishing/3.0/journalpublishing3.dtd\">";
        finalXMLString += xml;



    }

    private static Element appendChilds(Element a, ArrayList<Element> b) {

        a.appendChild(b.get(b.size() - 1));

        for (int i = 0; i < b.size() - 1; i++) {

            a.appendChild(b.get(i));

        }

        return a;

    }

    public static Element generateBack() {

        Element back = doc.createElement("back");
        if (Main.acknowledgement.size() > 0) {

            Element ack = doc.createElement("ack");

            back.appendChild(ack);

            for (String aString : acknowledgement) {

                aDoc = new DocSegment();
                aDoc.setDoc(doc);
                aDoc.setList(Main.acknowledgement);
                ack = Main.appendChilds(ack, aDoc.addTableFigure(aString));

            }
        }

        aDoc = new DocSegment();
        aDoc.setDoc(doc);
        aDoc.setList(Main.disclosure);
        List<Element> tempString = aDoc.getGeneral(disclosure, "[s]", "isBack");
        for (Element a : tempString) {
            back.appendChild(a);
        }


        Element refList = doc.createElement("ref-list");

        back.appendChild(refList);

        Element refListTitle = doc.createElement("title");

        refListTitle.appendChild(doc.createTextNode("References"));

        refList.appendChild(refListTitle);


        //Humphris G, Blinkhorn A, Freeman R, Gorter R, Hoad-Reddick G, Murtomaa H, O'Sullivan R, et al. Psychological stress in undergraduate dental students: baseline results from seven European dental schools. Eur J Dent Educ. 2002;6(1):22-29. 11872070
        //
        for (int i = 0; i < references.size(); i++) {

            String temp = references.get(i).trim();

            ta.append("\n" + "dealing with the " + i + " ref:" + temp);
            
            System.out.println("\n" + "dealing with the " + i + " ref:" + temp);



            Element tempOneRef = doRefs(temp, i);

            if (tempOneRef != null) {

                refList.appendChild(tempOneRef);

            }

        }

        return back;

    }

    public static Element doRefs(String refs, int i) {

        String[] newRefs = refs.split("httphttp");
        
        refs = newRefs[0];
        
        String souceString =  newRefs[0].trim();
                
        
        String sourcePattern = "\\d{1,}\\s*[\\.,]\\s*";
        souceString=souceString.replaceFirst(sourcePattern, "");
        
        System.out.println("souceString;"+souceString);
        
        refs = refs.trim();
      //  refs = refs.replaceFirst("^[0-9]+\\.", "");
        
       System.out.println("what is before:"+refs);
        refs = refs.replaceAll("\\.,",",");
       
        refs = refs.replaceAll("\\?", ".");
        
        System.out.println("what is before:"+refs);

        String[] refInfo = refs.split("\\.");
        
        System.out.println("refingo length:"+refInfo.length);

        if (refInfo.length < 2) {

            return null;

        }

        if (refInfo.length < 5) {

            ta.append("\ncurrent ref format is not correct, please check it.");

            return null;

        }
        
        System.out.println("1,"+refInfo[0]+" 2,"+refInfo[1]+" 3,"+refInfo[2]+" 4,"+refInfo[3]+" 5,"+refInfo[4]);

        if (refInfo.length > 4) {
            String[] refInfoTemp = new String[4];
            refInfoTemp[3] = refInfo[refInfo.length - 1];
            refInfoTemp[2] = refInfo[refInfo.length - 2];
            //refInfoTemp[1] = refInfo[refInfo.length - 3];
            refInfoTemp[0] = refInfo[1];
            String titleTemp = refInfo[2];
            ;
            for (int x = 2; x < refInfo.length - 2; x++) {
                titleTemp = titleTemp + "." + refInfo[x];
            }
            refInfoTemp[1] = titleTemp;
            refInfo = refInfoTemp;
        }

        Element e1 = doc.createElement("ref");

        Element refLabel = doc.createElement("label");
        
        Element e200 = doc.createElement("element-citation");
        
        e200.setAttribute("publication-type", "journal");

        Element e2 = doc.createElement("source");
        
        e200.appendChild(e2);

        String idString = (i + 1) + "";

        e1.appendChild(refLabel);

        refLabel.appendChild(doc.createTextNode(idString));

        e1.appendChild(e200);
        

        if (i < 9) {

            idString = "0" + (i + 1);

        }

        e1.setAttribute("id", "R" + idString);



        System.out.println(i + " idstring is " + idString);





        //e2.setAttribute("publication-type", "journal");

        e2.appendChild(doc.createTextNode(souceString));
/**
        String[] names = refInfo[0].split(",");


        for (int j = 0; j < names.length; j++) {

            names[j] = names[j].trim();
            
            System.out.println("one ref name:"+names[j]);

            if (j == (names.length - 1) && (names[j].equals("et al") || names[j].equals("etal"))) {
                Element e3 = doc.createElement("etal");
                e3.appendChild(doc.createTextNode("et al"));
               // e2.appendChild(e3);
            } else {

                String surname = "";

                String givenName = "";

                if (names[j].contains(" ")) {

                    String[] name = names[j].split(" ");

                    surname = name[0];
                    givenName = name[1];
                    
                    if(name.length>2) {
                        for(int iii=2; iii< name.length;iii++) {

                           givenName += " "+name[iii];
                        }
                    }

                } else {

                    surname = names[j];

                }

                Element e3 = doc.createElement("name");

                Element e4 = doc.createElement("surname");

                Element e5 = doc.createElement("given-names");

                e4.appendChild(doc.createTextNode(surname.trim()));

                e5.appendChild(doc.createTextNode(givenName.trim()));

                e3.appendChild(e4);

                e3.appendChild(e5);

               // e2.appendChild(e3);
            }

        }



        System.out.println("done with name ");

        Element e6 = doc.createElement("article-title");

        e6.appendChild(doc.createTextNode(refInfo[1].trim()));

        //e2.appendChild(e6);



        System.out.println("done with title ");

        Element e7 = doc.createElement("source");

        String source = "";

        String supply = "";

        // 2005;43(3):235-243

        if (refInfo[2].contains("(")) {

            String[] sourceSupply = refInfo[2].split("\\(");

            source = sourceSupply[0];

            supply = sourceSupply[1].replace(")", "");

            e7.appendChild(doc.createTextNode(source.trim()));

           // e2.appendChild(e7);

            Element e8 = doc.createElement("supplement");

            e8.appendChild(doc.createTextNode(supply.trim()));

           // e2.appendChild(e8);

        } else {

            source = refInfo[2];

            e7.appendChild(doc.createTextNode(source.trim()));

          //  e2.appendChild(e7);

        }



        System.out.println("done with source ");

        Pattern pattern = Pattern.compile("[0-9]{4};");

        Matcher matcher = pattern.matcher(refInfo[3]);

        while (matcher.find()) {

            String yearInfo = matcher.group();

            yearInfo = yearInfo.replace(";", "");



            Element e9 = doc.createElement("year");

            e9.appendChild(doc.createTextNode(yearInfo.trim()));

          //  e2.appendChild(e9);

        }



        System.out.println("done with year ");

        pattern = Pattern.compile(";[0-9]{1,}");

        matcher = pattern.matcher(refInfo[3]);

        while (matcher.find()) {

            String volume = matcher.group();

            volume = volume.replace(";", "");



            Element e10 = doc.createElement("volume");

            e10.appendChild(doc.createTextNode(volume.trim()));

          //  e2.appendChild(e10);

        }



        System.out.println("done with volume ");



        pattern = Pattern.compile(";[0-9]{1,}\\(([0-9]{1,}(-[0-9]{1,}){0,1}|suppl\\s[0-9]{1,})\\)");

        matcher = pattern.matcher(refInfo[3]);

        while (matcher.find()) {

            String issue = matcher.group();

            String[] issueArray = issue.split("\\(");

            issue = issueArray[1].replace(")", "");

            Element e11 = doc.createElement("issue");

            e11.appendChild(doc.createTextNode(issue.trim()));

          //  e2.appendChild(e11);

        }



        System.out.println("done with issue ");



        pattern = Pattern.compile(":[0-9a-zA-Z]{0,}-[0-9a-zA-Z]{0,}");

        matcher = pattern.matcher(refInfo[3]);

        while (matcher.find()) {

            String pageInfo = matcher.group();

            pageInfo = pageInfo.replace(":", "");

            String[] pageInfoArr = pageInfo.split("-");

            String fpage = pageInfoArr[0];

            String lpage = pageInfoArr[1];


            if (!fpage.equals("000")) {

                Element e12 = doc.createElement("fpage");

                e12.appendChild(doc.createTextNode(fpage.trim()));

              //  e2.appendChild(e12);
            }



            if (!lpage.equals("000")) {
                Element e13 = doc.createElement("lpage");

                e13.appendChild(doc.createTextNode(lpage.trim()));

              //  e2.appendChild(e13);
            }

        }

        pattern = Pattern.compile("discussion\\s[0-9]{1,}");

        matcher = pattern.matcher(refInfo[3]);

        while (matcher.find()) {
            String pageInfo = matcher.group();
            Element e13 = doc.createElement("comment");
            e13.appendChild(doc.createTextNode(pageInfo.trim()));
         //   e2.appendChild(e13);
        }



        System.out.println("done with lpage ");
        
        **/

        if (newRefs.length>1 && newRefs[1]!=null && !newRefs[1].trim().equals("000")) {
            
            String result = newRefs[1].trim();
            System.out.println(result);
            
            String doiString = "";
            String pmidString = "";
            
            Pattern pattern = Pattern.compile("aaaaapmid(.{1,})(aaaaa.{1,}){0,1}");
            
            Matcher matcher = pattern.matcher(result);
             while (matcher.find()) {
                //doiString = matcher.group(1);
                pmidString = matcher.group(1);
                if(doiString.contains("aaaaadoi")) {
                    String[] pmidStringArr = doiString.split("aaaaadoi"); 
                    pmidString = pmidStringArr[0];
                }
               // pmidString=pmidString.replaceAll("aaaaapmid", "");
                System.out.println("find pmid match:"+pmidString);
            }
             
            
            pattern = Pattern.compile("aaaaadoi(.{1,})(aaaaa.{1,}){0,1}");

            matcher = pattern.matcher(result);

            
            while (matcher.find()) {
                doiString = matcher.group(1);
                if(doiString.contains("aaaaapmid")) {
                    String[] doiStringArr = doiString.split("aaaaapmid"); 
                    doiString = doiStringArr[0];
                }
                //doiString=doiString.replaceAll("aaaaadoi", "");
                System.out.println("find doi match:"+doiString);
               // pmidString = matcher.group(3);
                
            }
            
            
            
            System.out.println("doi and pmid:"+doiString + " "+pmidString);
            
             if(!doiString.trim().equals("")) {
                Element e14 = doc.createElement("pub-id");

                e14.appendChild(doc.createTextNode(doiString));

                e200.appendChild(e14);

                e14.setAttribute("pub-id-type", "doi");
            }
            
            if(!pmidString.trim().equals("")) {
                Element e14 = doc.createElement("pub-id");

                e14.appendChild(doc.createTextNode(pmidString));

                e200.appendChild(e14);

                e14.setAttribute("pub-id-type", "pmid");
            }
            
           

        }



        System.out.println("done with pmid ");

        return e1;



    }

    public static Element generateBody() {

        Element body = doc.createElement("body");

        aDoc = new DocSegment();
        aDoc.setDoc(doc);
        aDoc.setList(Main.discussion);
        List<Element> tempString = aDoc.getGeneral(discussion, "[s]","isBody");
        for (Element a : tempString) {
            body.appendChild(a);
        }

        return body;

    }

    public static Element generateFront() {




        Element front = doc.createElement("front");

        Element journalMeta = doc.createElement("journal-meta");

        Element journalTa = doc.createElement("journal-id");

        journalTa.setAttribute("journal-id-type", "nlm-ta");

        journalTa.appendChild(doc.createTextNode(Main.magzineShortTitle));

        journalMeta.appendChild(journalTa);

        Element journalId = doc.createElement("journal-id");

        journalId.setAttribute("journal-id-type", "publisher-id");

        journalId.appendChild(doc.createTextNode("Elmer Press"));

        journalMeta.appendChild(journalId);

        Element journalTitleGroup = doc.createElement("journal-title-group");

        journalMeta.appendChild(journalTitleGroup);

        Element journalTitle = doc.createElement("journal-title");

        journalTitle.appendChild(doc.createTextNode(Main.magzineTitle));

        journalTitleGroup.appendChild(journalTitle);



        Element issnP = doc.createElement("issn");

        issnP.setAttribute("pub-type", "ppub");

        issnP.appendChild(doc.createTextNode(Main.issn_ppub));

        Element issnE = doc.createElement("issn");

        issnE.setAttribute("pub-type", "epub");

        issnE.appendChild(doc.createTextNode(Main.issn_epub));

        journalMeta.appendChild(issnP);

        journalMeta.appendChild(issnE);



        Element publisher = doc.createElement("publisher");

        Element publisherName = doc.createElement("publisher-name");

        journalMeta.appendChild(publisher);

        publisher.appendChild(publisherName);

        publisherName.appendChild(doc.createTextNode("Elmer Press"));





        Element articleMeta = doc.createElement("article-meta");

        Element articleId = doc.createElement("article-id");

        articleId.setAttribute("pub-id-type", "doi");

        articleId.appendChild(doc.createTextNode(Main.doi.trim()));

        articleMeta.appendChild(articleId);



        Element articleCategories = doc.createElement("article-categories");

        articleMeta.appendChild(articleCategories);

        Element subjGroup = doc.createElement("subj-group");

        subjGroup.setAttribute("subj-group-type", "heading");

        articleCategories.appendChild(subjGroup);

        Element subject = doc.createElement("subject");

        subjGroup.appendChild(subject);

        subject.appendChild(doc.createTextNode(Main.isOriginal.trim()));



        Element titleGroup = doc.createElement("title-group");

        articleMeta.appendChild(titleGroup);



        Element articleTitle = doc.createElement("article-title");

        titleGroup.appendChild(articleTitle);

        articleTitle.appendChild(doc.createTextNode(Main.title.trim()));

        if (Main.shortTitle != null) {

            Element altTitle = doc.createElement("alt-title");

            titleGroup.appendChild(altTitle);

            altTitle.appendChild(doc.createTextNode(Main.shortTitle.trim()));

            altTitle.setAttribute("alt-title-type", "short");

        }


        //Element contribGroup = doc.createElement("contrib-group");

        // Element contrib = doc.createElement("contrib");

        articleMeta = doAuthor(articleMeta, authors);

        String[] ppubArr = Main.ppub.split("/");

        String[] epubArr = Main.epub.split("/");

        Element pubDatePP = doc.createElement("pub-date");

        pubDatePP.setAttribute("pub-type", "ppub");

        Element pubDatePP_month = doc.createElement("month");

        pubDatePP_month.appendChild(doc.createTextNode(ppubArr[0].trim()));

        Element pubDatePP_year = doc.createElement("year");

        pubDatePP_year.appendChild(doc.createTextNode(ppubArr[1].trim()));

        articleMeta.appendChild(pubDatePP);

        pubDatePP.appendChild(pubDatePP_month);

        pubDatePP.appendChild(pubDatePP_year);

        Element pubDateEP = doc.createElement("pub-date");

        pubDateEP.setAttribute("pub-type", "epub");

        Element pubDateEP_day = doc.createElement("day");

        pubDateEP_day.appendChild(doc.createTextNode(epubArr[0].trim()));

        Element pubDateEP_month = doc.createElement("month");

        pubDateEP_month.appendChild(doc.createTextNode(epubArr[1].trim()));

        Element pubDateEP_year = doc.createElement("year");

        pubDateEP_year.appendChild(doc.createTextNode(epubArr[2].trim()));

        articleMeta.appendChild(pubDateEP);

        pubDateEP.appendChild(pubDateEP_day);

        pubDateEP.appendChild(pubDateEP_month);

        pubDateEP.appendChild(pubDateEP_year);





        Element volume = doc.createElement("volume");

        volume.appendChild(doc.createTextNode(Main.volume == null ? "" : Main.volume.trim()));

        articleMeta.appendChild(volume);



        Element issue = doc.createElement("issue");

        issue.appendChild(doc.createTextNode(Main.issue == null ? "" : Main.issue.trim()));

        articleMeta.appendChild(issue);



        Element fpage = doc.createElement("fpage");

        fpage.appendChild(doc.createTextNode(Main.fpage == null ? "" : Main.fpage.trim()));

        articleMeta.appendChild(fpage);



        Element lpage = doc.createElement("lpage");

        lpage.appendChild(doc.createTextNode(Main.lpage == null ? "" : Main.lpage.trim()));

        articleMeta.appendChild(lpage);





        Element history = doc.createElement("history");

        Element historyDate = doc.createElement("date");

        historyDate.setAttribute("date-type", "accepted");

        String[] historyDateArr = Main.manuscriptDateAccepted.split("/");

        Element history_day = doc.createElement("day");

        history_day.appendChild(doc.createTextNode(historyDateArr[0].trim()));

        Element history_month = doc.createElement("month");

        history_month.appendChild(doc.createTextNode(historyDateArr[1].trim()));

        Element history_year = doc.createElement("year");

        history_year.appendChild(doc.createTextNode(historyDateArr[2].trim()));

        historyDate.appendChild(history_day);

        historyDate.appendChild(history_month);

        historyDate.appendChild(history_year);

        history.appendChild(historyDate);

        articleMeta.appendChild(history);



        Element permissions = doc.createElement("permissions");

        articleMeta.appendChild(permissions);

        Element copyright_statement = doc.createElement("copyright-statement");
        copyright_statement.appendChild(doc.createTextNode(Main.copyrightStat.trim()));

        Element copyright_year = doc.createElement("copyright-year");
        copyright_year.appendChild(doc.createTextNode(Main.copyrightYear.trim()));

        Element license = doc.createElement("license");

        license.setAttribute("license-type", "open access");

        license.setAttribute("xlink:href", "http://creativecommons.org/licenses/by-nc/4.0/");

        Element license_p = doc.createElement("license-p");



        license_p.appendChild(doc.createTextNode(textNote));



        license.appendChild(license_p);



        permissions.appendChild(copyright_statement);

        permissions.appendChild(copyright_year);

        permissions.appendChild(license);


        aDoc = new DocSegment();
        aDoc.setDoc(doc);

        if (abstractArr.size() > 0) {
            Element abstractElem = doc.createElement("abstract");
            articleMeta.appendChild(abstractElem);
            List<Element> tempString = aDoc.getAbstract(abstractArr);
            for (Element a : tempString) {
                abstractElem.appendChild(a);
            }

        }
        
        if(keywordArr.size()>0 ) {
            Element keywordElem = doc.createElement("kwd-group");
            keywordElem.setAttribute("kwd-group-type", "author");
            articleMeta.appendChild(keywordElem);
            for(String aKeywordLine:keywordArr) {
                if(!aKeywordLine.startsWith("[s1]")) {
                    if(aKeywordLine.contains(";")) {
                        for(String aKeyword:aKeywordLine.split(";")) {
                            Element aKeywordElem = doc.createElement("kwd");
                            keywordElem.appendChild(aKeywordElem);
                            aKeywordElem.appendChild(doc.createTextNode(aKeyword.trim()));
                        }
                    }
                }
            }
        }



        front.appendChild(journalMeta);

        front.appendChild(articleMeta);



        return front;

    }

    public static ArrayList<String> arrayCleaner(ArrayList<String> a) {

        ArrayList<String> temp = new ArrayList<String>();

        for (String aString : a) {

            aString = aString.trim();

            if (aString != null && !aString.equals("")) {

                temp.add(aString);

            }

        }

        return temp;

    }

    public static Element doAuthor(Element e0, ArrayList<String> refs) {





        String[] authors = refs.get(1).split(",");
        
        List<String> authorsInitList = new ArrayList<String>();
        
        for(String aAuthString: authors) {
            if (aAuthString.trim().length() == 1) {
                authorsInitList.add(aAuthString.trim());
            }
        }
        
        Collections.sort(authorsInitList);

        Element e3 = doc.createElement("contrib-group");



        Element e11 = null;

        e0.appendChild(e3);







        for (int i = 0; i < authors.length; i++) {

            authors[i] = authors[i].trim();
            
            System.out.println("author name:"+authors[i]);

            if (authors[i] == null || authors[i].equals("")) {

                continue;

            }

            if(authors[i].trim().length()==1) {
                NodeList contribArr = e3.getElementsByTagName("contrib");

                Node lastContrib = contribArr.item(contribArr.getLength() - 1);
                Element e10 = doc.createElement("xref");

                lastContrib.appendChild(e10);
                if (authors[i].trim().equalsIgnoreCase(authorsInitList.get(authorsInitList.size()-1).trim())) {

                    

                    

                    e10.setAttribute("ref-type", "corresp");

                    e10.setAttribute("rid", "COR" + authors[i]);

                    e10.appendChild(doc.createTextNode(authors[i].trim()));

                } else {
                    e10.setAttribute("ref-type", "aff");

                    e10.setAttribute("rid", "A" + authors[i].trim());

                     e10.appendChild(doc.createTextNode(authors[i].trim()));
                }

                continue;
            }









            String authorName = authors[i].substring(0, authors[i].length()-1);

            String[] authorNameArr = authorName.split(" ");

            String surname = authorNameArr[authorNameArr.length - 1];

            String given_names = authorNameArr[0];

            int j = 1;

            while (j < (authorNameArr.length - 1)) {

                given_names += " " + authorNameArr[j];

                j++;

            }



            String refIndex = authors[i].substring(authors[i].length() - 1);

            Element e4 = doc.createElement("contrib");

            e3.appendChild(e4);

            e4.setAttribute("contrib-type", "author");

            Element e5 = doc.createElement("name");

            e4.appendChild(e5);

            Element e6 = doc.createElement("surname");

            Element e7 = doc.createElement("given-names");

            e5.appendChild(e6);

            e5.appendChild(e7);

            e6.appendChild(doc.createTextNode(surname.trim()));

            e7.appendChild(doc.createTextNode(given_names.trim()));



            Element e8 = doc.createElement("xref");

            e4.appendChild(e8);

            e8.setAttribute("ref-type", "aff");

            e8.setAttribute("rid", "A" + refIndex);

            e8.appendChild(doc.createTextNode(refIndex.trim()));

        }





        ArrayList<String> authorNotes = new ArrayList<String>();

        for (int x = 2; x < refs.size(); x++) {

            char index = refs.get(x).charAt(0);

            String context = refs.get(x).substring(1);

            if (context.startsWith("Corresponding")) {

                authorNotes.add(refs.get(x));

                continue;

            }

            e11 = doc.createElement("aff");

            Element e12 = doc.createElement("label");



            e11.appendChild(e12);

            e0.appendChild(e11);

            e11.setAttribute("id", "A" + index);

            e12.appendChild(doc.createTextNode("" + index));

            e11.appendChild(doc.createTextNode(context));

        }



        if (authorNotes.size() > 0) {

            Element e13 = doc.createElement("author-notes");

            e0.appendChild(e13);

            for (int x = 0; x < authorNotes.size(); x++) {

                char index = authorNotes.get(x).charAt(0);

                String context = authorNotes.get(x).substring(1);

                Element e14 = doc.createElement("corresp");

                Element e15 = doc.createElement("label");

                e13.appendChild(e14);

                e14.appendChild(e15);

                e14.setAttribute("id", "COR" + index);

                e15.appendChild(doc.createTextNode(index + ""));



                if (context.contains("Email:")) {
                    String[] aaa = context.split("Email:");
                    e14.appendChild(doc.createTextNode(aaa[0].trim() + " Email: "));
                    Element e20 = doc.createElement("email");
                    e14.appendChild(e20);
                    aaa[1]=aaa[1]==null?"":aaa[1].trim();
                    e20.appendChild(doc.createTextNode(aaa[1]));
                } else {
                	context=context==null?"":context.trim();
                    e14.appendChild(doc.createTextNode(context));
                }

            }

        }



        return e0;

    }

    public static Element getTable(String name) {
         DataFormatter formatter = new DataFormatter(Locale.US);
        if (name == null) {
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
            	ex.printStackTrace();
                ta.append("\nerrors happen:\n"+ex.getMessage());
                ta.append(ex.getMessage() + "\n");
            }
            doc = db.newDocument();
        }


        if (name == null) {
            name = "C:\\Users\\DLiu1\\Documents\\NetBeansProjects\\Simon\\dist\\Table 1";
        }
        String fileName = name + ".xls";

        File aaa = new File(fileName);
        if (!aaa.exists()) {
            Main.error(fileName + " doesn't exist, please copy the " + fileName + " into the same folder!");
            return null;
        }
        Element tableFrame = null;
        try {

            tableFrame = doc.createElement("table");

            tableFrame.setAttribute("frame", "hsides");

            tableFrame.setAttribute("rules", "groups");
            Element thead = doc.createElement("thead");
            Element tbody = doc.createElement("tbody");
            tableFrame.appendChild(thead);
            tableFrame.appendChild(tbody);
            /** Creating Input Stream**/
            //InputStream myInput= ReadExcelFile.class.getResourceAsStream( fileName );
            FileInputStream myInput = new FileInputStream(aaa);

            /** Create a POIFSFileSystem object**/
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            /** Create a workbook using the File System**/
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            /** Get the first sheet from workbook**/
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            int theRow = 0;
            int theadRows = 1;
            while (rowIter.hasNext()) {
                theRow++;
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                //Vector cellStoreVector=new Vector();
                System.out.println("\nprinting " + theRow);
                Element tr = doc.createElement("tr");


                System.out.println("\nprinting " + theRow);
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    CellProperties cp = new CellProperties(myCell);

                    Element td = null;
                    int colspan = cp.getColspan();
                    int rowspan = cp.getRowspan();
                    CellReference ref = new CellReference(myCell);
                    System.out.println("The value of " + ref.formatAsString() + " is " + formatter.formatCellValue(myCell));
                   // String myCellValue = myCell.toString();
                  //  myCell.setCellType(Cell.CELL_TYPE_STRING);
                   // String myCellValue = myCell.getRichStringCellValue().toString();
                   String myCellValue = formatter.formatCellValue(myCell);
                    if(myCellValue !=null && myCellValue.trim().endsWith(".0")) {
                        System.out.println(myCellValue + " have 0");
                        myCellValue = myCellValue.replace(".0", "");
                    }
                    System.out.println(myCellValue + ": colspan:" + cp.getColspan() + " rowspan:" + cp.getRowspan());
                    if (rowspan > 1) {
                        if (theRow == 1) {
                            theadRows = rowspan;
                        }
                    }
                    if (theRow <= theadRows) {
                       td= doc.createElement("th");
                       td.setAttribute("align", "left");
                    } else {
                       td=  doc.createElement("td");
                       td.setAttribute("align", "left");
                       td.setAttribute("valign", "top");
                    }
                    if (colspan > 1) {
                        td.setAttribute("colspan", colspan + "");
                    }
                    if(rowspan > 1) {
                        td.setAttribute("rowspan", rowspan + "");
                    }
                    if ((colspan > 1 || rowspan > 1) && myCellValue.trim().equals("")) {
                        continue;
                    }


                    Element bold = doc.createElement("bold");
                   
                    tr.appendChild(td);


                        td.appendChild(doc.createTextNode(myCellValue.trim()));

                    //  cellStoreVector.addElement(myCell);
                }

                if (theRow <= theadRows) {
                    thead.appendChild(tr);
                } else {
                    tbody.appendChild(tr);
                }
                // cellVectorHolder.addElement(cellStoreVector);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            ta.append("\nerrors happen:\n"+e.getMessage());
            ta.append(e.getMessage() + "\n");
        }

        return tableFrame;
    }
    
    public static String filterString(String a) {
    	String result = "";
    	if(a!=null && !a.equals("")) {
    	   a.replaceAll("&#160;", " ");
    	}
    	return result;
    }

    public static void readMyDocument(String fileName) {

        POIFSFileSystem fs = null;

        try {

            fs = new POIFSFileSystem(new FileInputStream(fileName));

            HWPFDocument doc = new HWPFDocument(fs);





            /** Read the content **/
            readParagraphs(doc,Main.ta,Main.refs);



            int realIndex = 0;

            try {

                for (int i = 0; i < refs.size(); i++) {

                    String temp = refs.get(i).trim();

                    System.out.println(i+":"+temp);

                    refs.set(i, temp);

                    if (temp == null || temp.length() < 3) {

                        continue;

                    }

                    realIndex++;

                    ta.append("\n" + "dealing with the " + i + " ref:" + refs.get(i));



                    if (realIndex == 1) {
                        Main.isOriginal = temp;
                        continue;
                    }

                    if (realIndex == 2) {

                        title = (temp == null ? "Unknown Title" : temp);
                        continue;
                    }


                    if (temp.startsWith("[a]")) {
                        if (realIndex < 3) {
                            Main.error("author tag is located at wrong place.");
                        }
                        if (temp.equals("[a]")) {

                            int j = i;

                            while (!refs.get(j).equals("[/a]")) {

                                temp = refs.get(j).trim();
                                refs.set(j, temp);

                                if (temp == null || temp.length() < 3) {
                                    j++;

                                    continue;

                                }

                                authors.add(refs.get(j));

                                j++;

                            }

                            i = j;
                            continue;

                        } else {
                            Main.error("[a] must not be followed by anything.\n" + temp);
                        }
                    }

                    Pattern p = Pattern.compile("^\\s{0,}\\d+/\\d+/\\d+\\s{0,}$");


                    if (temp.startsWith("Manuscript accepted")) {
                        manuscriptDateAccepted = temp.replace("Manuscript accepted:", "");
                        Matcher m = p.matcher(manuscriptDateAccepted);
                        if (m.find()) {
                            continue;
                        } else {
                            Main.error("manuscriptDateAccepted must follow by data format:dd/dd/dddd.\n" + temp);
                        }

                    }



                    if (temp.startsWith("Short title")) {

                        shortTitle = temp.replace("Short title:", "");
                        continue;
                    }



                    if (temp.startsWith("doi:")) {

                        doi = temp.replace("doi:", "");
                        continue;
                    }



                    if (temp.startsWith("ppub:")) {

                        ppub = temp.replace("ppub:", "");
                        Pattern ppub_p = Pattern.compile("^\\s{0,}\\d+/\\d+\\s{0,}$");
                        Matcher m = ppub_p.matcher(ppub);
                        if (m.find()) {
                            continue;
                        } else {
                            Main.error("ppub data format must be:dd/dd/dddd.\n" + temp);
                        }
                    }



                    if (temp.startsWith("epub:")) {

                        epub = temp.replace("epub:", "");
                        Matcher m = p.matcher(epub);
                        if (m.find()) {
                            continue;
                        } else {
                            Main.error("epub data format must be:dd/dd/dddd.\n" + temp);
                        }
                    }



                    if (temp.startsWith("volume:")) {

                        volume = temp.replace("volume:", "");
                        Pattern pc = Pattern.compile("^\\s{0,}\\d+\\s{0,}$");
                        Matcher m = pc.matcher(volume);
                        if (m.find()) {
                            continue;
                        } else {
                            Main.error("must only be digit number:\n" + temp);
                        }
                    }



                    if (temp.startsWith("issue")) {

                        issue = temp.replace("issue:", "");
                        Pattern pc = Pattern.compile("^\\s{0,}\\d+\\s{0,}$");
                        Matcher m = pc.matcher(issue);
                        if (m.find()) {
                            continue;
                        } else {
                            Main.error("must only be digit number:\n" + temp);
                        }
                    }



                    if (temp.startsWith("fpage")) {

                        fpage = temp.replace("fpage:", "");
                        Pattern pc = Pattern.compile("^\\s{0,}\\d+\\s{0,}$");
                        Matcher m = pc.matcher(fpage);
                        if (m.find()) {
                            continue;
                        } else {
                            Main.error("must only be digit number:\n" + temp);
                        }
                    }



                    if (temp.startsWith("lpage")) {

                        lpage = temp.replace("lpage:", "");
                        Pattern pc = Pattern.compile("^\\s{0,}\\d+\\s{0,}$");
                        Matcher m = pc.matcher(lpage);
                        if (m.find()) {
                            continue;
                        } else {
                            Main.error("must only be digit number:\n" + temp);
                        }
                    }



                    if (temp.startsWith("date accepted")) {

                        dateAccepted = temp.replace("date accepted:", "");
                        Matcher m = p.matcher(dateAccepted);
                        if (m.find()) {
                            continue;
                        } else {
                            Main.error("data format must be:dd/dd/dddd.\n" + temp);
                        }
                    }


                    if (temp.startsWith("copyright-statement")) {

                        copyrightStat = temp.replace("copyright-statement:", "");
                        continue;
                    }


                    if (temp.startsWith("copyright-year")) {

                        copyrightYear = temp.replace("copyright-year:", "");
                        Pattern pc = Pattern.compile("^\\s{0,}\\d{4}\\s{0,}$");
                        Matcher m = pc.matcher(copyrightYear);
                        if (m.find()) {
                            continue;
                        } else {
                            Main.error("must only be 4 digit numbers:\n" + temp);
                        }
                    }


                    if (temp.equalsIgnoreCase("Abstract")) {
                        int j = i;
                        while (!refs.get(j).trim().equals("[s1]Keywords")) {

                            ta.append("try find out introduction " + refs.get(j) + "\n");

                            temp = refs.get(j).trim();
                            refs.set(j, temp);

                            if (temp == null || temp.length() < 3) {
                                j++;

                                continue;

                            }

                            abstractArr.add(refs.get(j));

                            j++;

                        }

                        i = j - 1;
                        continue;
                    }
                    
                    if (temp.trim().equalsIgnoreCase("[s1]Keywords")) {
                        int j = i;
                        while (!refs.get(j).trim().equals("[body]")) {

                            ta.append("try find out introduction " + refs.get(j) + "\n");

                            temp = refs.get(j).trim();
                            refs.set(j, temp);

                            if (temp == null || temp.length() < 3) {
                                j++;

                                continue;

                            }

                            keywordArr.add(refs.get(j));

                            j++;

                        }

                        i = j - 1;
                        continue;
                    }
                    
                   if (temp.equals("[body]")) {
                        Main.isValidBoday = true;
                        int j = i;
                        while (!refs.get(j).equals("Acknowledgement") && !refs.get(j).equals("[back]")) {

                            ta.append("try find out introduction " + refs.get(j) + "\n");

                            temp = refs.get(j).trim();
                            refs.set(j, temp);

                            if (temp == null || temp.length() < 3) {
                                j++;

                                continue;

                            }

                            Main.discussion.add(refs.get(j));

                            j++;

                        }

                        i = j - 1;
                        continue;
                    }


                    if (temp.equals("Acknowledgement")) {

                        int j = i + 1;

                        while (!refs.get(j).equals("[back]")) {

                            temp = refs.get(j).trim();
                            refs.set(j, temp);

                            if (temp == null || temp.length() < 3) {
                                j++;

                                continue;

                            }

                            Main.acknowledgement.add(refs.get(j));

                            j++;

                        }

                        i = j - 1;
                        continue;
                    }

                    if (temp.equals("[back]")) {
                        Main.isValidBack = true;

                        int j = i;

                        while (!refs.get(j).equals("Table") && !refs.get(j).equals("Figure legends") && !refs.get(j).equals("References")) {

                            temp = refs.get(j).trim();
                            refs.set(j, temp);

                            if (temp == null || temp.length() < 3) {
                                j++;

                                continue;

                            }

                            Main.disclosure.add(refs.get(j));

                            j++;

                        }

                        i = j - 1;

                        continue;

                    }


                    if (temp.equalsIgnoreCase("Table")) {

                        int j = i + 1;

                        while (!refs.get(j).startsWith("Figure legends") && !refs.get(j).equals("References")) {

                            List<String> aTable = new ArrayList<String>();

                            refs.set(j, refs.get(j).trim());


                            String tempTemp = refs.get(j).trim();
                            System.out.println("setup table:"+tempTemp);

                            if (refs.get(j) == null || tempTemp.length() < 3) {
                                j++;

                                continue;

                            }

                            if (!tempTemp.contains("[title]")) {
                                Main.error("problem happened around " + tempTemp);
                                //System.exit(-1);
                            }

                            String[] a = refs.get(j).split("\\[title\\]");

                            aTable.add(a[0]);

                            String[] b = new String[2];

                            if (a[1].contains("footnotes")) {
                                b = a[1].split("\\[footnotes\\]");
                                j++;
                                while (!refs.get(j).startsWith("Figure legends") && !refs.get(j).equals("References") && !refs.get(j).startsWith("Table")) {
                                    b[1] += "aaaaa" + refs.get(j);
                                    j++;
                                }
                                j--;
                            } else {
                                b[0] = a[1];
                                b[1] = "";
                            }


                            aTable.add(b[0]);

                            aTable.add(b[1]);

                            Main.table.add(aTable);

                            j++;

                        }

                        i = j - 1;
                        continue;
                    }



                    if (temp.equals("Figure legends")) {

                        int j = i + 1;

                        while (!refs.get(j).startsWith("References")) {

                            List<String> aTable = new ArrayList<String>();

                            refs.set(j, refs.get(j).trim());

                            String tempTemp = refs.get(j).trim();

                            if (tempTemp == null || tempTemp.length() < 3) {
                                j++;

                                continue;

                            }

                            if (!tempTemp.contains("[legend]")) {
                                Main.error("problem happened around " + tempTemp);
                                // System.exit(-1);
                            }

                            String[] a = refs.get(j).split("\\[legend\\]");

                            aTable.add(a[0]);

                            String[] b = a[1].split("\\[file\\]");

                            aTable.add(b[0]);

                            aTable.add(b[1]);

                            Main.figure.add(aTable);


                            j++;
                        }

                        i = j - 1;
                        continue;
                    }



                    if (temp.equals("References")) {
                        Main.isValidRefs = true;

                        i++;

                        while (i < refs.size()) {

                            System.out.println("adding reference:" + refs.get(i));
                            String result = refs.get(i);
                            result += "httphttp";
                            String secondString = "";
                            if((i+1)<refs.size())
                            	secondString = refs.get(i+1);
                            System.out.println("new string 1:"+secondString);
                            String thirdString = "";
                            if((i+2)<refs.size())
                                 thirdString = refs.get(i+2);
                            System.out.println("new string 2:"+thirdString);
                            if(secondString!=null && (secondString.trim().startsWith("http://") || secondString.trim().startsWith("Http://")) ) {
                                result += secondString.trim();
                                i++;
                                
                                 if(thirdString!=null && (thirdString.trim().startsWith("http://") || thirdString.trim().startsWith("Http://")) ) {
                                    result += thirdString.trim();
                                    i++;
                                }
                            }
                            
                           
                             
                            result = result.replaceAll("http://dx.doi.org/", "aaaaadoi");
                            result = result.replaceAll("Http://dx.doi.org/", "aaaaadoi");
                            result = result.replaceAll("http://www.ncbi.nlm.nih.gov/pubmed/", "aaaaapmid");
                            result = result.replaceAll("Http://www.ncbi.nlm.nih.gov/pubmed/", "aaaaapmid");

                            Main.references.add(result);

                            i++;

                        }

                    }





                    //doit(refs.get(i), i);

                }

            } catch (Exception e) {

// TODO Auto-generated catch block
            	e.printStackTrace();

                ta.append("\nerrors happen:\n"+e.getMessage());
                ta.append(e.getMessage() + "\n");

            }



            int pageNumber = 1;



            /** We will try reading the header for page 1**/
            //readHeader(doc, pageNumber);
            /** Let's try reading the footer for page 1**/
            //readFooter(doc, pageNumber);
            /** Read the document summary**/
            //readDocumentSummary(doc);
        } catch (Exception e) {

            ta.append(e.getMessage());

        }

    }

    public static void readParagraphs(HWPFDocument doc,JTextArea taa, ArrayList<String> tbb) throws Exception {

    	WordExtractor we = new WordExtractor(doc);



        /**Get the total number of paragraphs**/
        String[] paragraphs = we.getParagraphText();

        taa.append("\n" + "Total Paragraphs: " + paragraphs.length);



        for (int i = 0; i < paragraphs.length; i++) {



            taa.append("\n" + "Length of paragraph " + (i + 1) + ": " + paragraphs[i].toString());



            if (paragraphs[i].toString() != null && paragraphs[i].toString().trim().length() > 1) {
                String temp = paragraphs[i].toString().trim();

                tbb.add(temp);

            }




        }



    }
    

    public static void readHeader(HWPFDocument doc, int pageNumber) {

        HeaderStories headerStore = new HeaderStories(doc);

        String header = headerStore.getHeader(pageNumber);

        ta.append("\n" + "Header Is: " + header);



    }

    public static void readFooter(HWPFDocument doc, int pageNumber) {

        HeaderStories headerStore = new HeaderStories(doc);

        String footer = headerStore.getFooter(pageNumber);

        ta.append("\n" + "Footer Is: " + footer);



    }

    public static void readDocumentSummary(HWPFDocument doc) {

        DocumentSummaryInformation summaryInfo = doc.getDocumentSummaryInformation();

        String category = summaryInfo.getCategory();

        String company = summaryInfo.getCompany();

        int lineCount = summaryInfo.getLineCount();

        int sectionCount = summaryInfo.getSectionCount();

        int slideCount = summaryInfo.getSlideCount();



        ta.append("\n" + "---------------------------");

        ta.append("\n" + "Category: " + category);

        ta.append("\n" + "Company: " + company);

        ta.append("\n" + "Line Count: " + lineCount);

        ta.append("\n" + "Section Count: " + sectionCount);

        ta.append("\n" + "Slide Count: " + slideCount);



    }
    public final static String prefixFileName = "c:\\";

    public static void fixEncoding(String[] aaaaa,JTextArea taa,Writer output) {
      

        String errorString = "";


        //System.out.println("");

        UnicodeBlock myBlock = null;
        
        for (String queryString : aaaaa) {
        	
        	 String result = "";
            taa.append("\ndealing with string:" + queryString);
            for (int i = 0; i < queryString.length(); i++) {
                char theChar = queryString.charAt(i);
                byte bValue = (byte) theChar;
                // System.out.println("[" + i + " => '" + (char) data[i]
                //     + "'] Is defined: "
                //     + Character.isDefined(new Byte(data[i]).intValue()));
                try {
                    myBlock = Character.UnicodeBlock.of(new Byte(bValue).intValue());
                    result += theChar;
                } catch (IllegalArgumentException e) {
                    int aaa = (int) theChar;
                    if(aaa==160) {
                    	result+=" ";
                    } else {
                    	result = result + "&#" + aaa + ";";
                    }
                    errorString+="ERROR HAPPENING: found ilegal char at index " + i + " : "
                            + theChar + "\nPLESE verfiy it in generated XML file.\nSystem already automatically convert it into &#" + aaa + ";\n";
                }
            }

            
            try {
				output.write(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        
        if(!errorString.equals("")) {
        	errorString = "special characters list:\n" + errorString + "Finished";
        	taa.append(errorString);
            //RefSouceOnlyMain.error(errorString);
            
        }
        
    }

    private static void replaceChar(String[] aaa) {
    }

    public static void generateXML(String theString, String fileName) throws IOException {



        Writer output = null;

        String prefix = "out/";

        File file = new File("out");

        if (!file.exists()) {
            file.mkdir();
        }

        file = new File(prefix + fileName);


        output = new BufferedWriter(new FileWriter(file));

        theString = theString.replaceAll("‘", "&#8216;").replaceAll("’", "&#8217;").replaceAll("”", "&#8221;").replaceAll("“", "&#8220;").replaceAll("≥", "&#8805;").replaceAll("≤", "&#8804;").replace("•", "&#8226;");
//theString = theString.replaceAll("‘","'").replaceAll("’","'").replaceAll("”", "\"").replaceAll("“", "\"");
        String[] aaaaa = theString.split("\n");
        Main.fixEncoding(aaaaa,Main.ta,output);

        output.close();

        ta.append("\n" + "the final xml file has been written to " + file.getAbsolutePath());

    }

    public static void error(String a) {

        JOptionPane.showMessageDialog(null, a, "Error happening", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(String a) {

        JOptionPane.showMessageDialog(null, a, "Important info", JOptionPane.INFORMATION_MESSAGE);
    }
    public static String finalXMLString = "";

    public static void main(String[] args) {
        Main.getTable(null);
    }
}
