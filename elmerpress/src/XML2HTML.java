
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author DLiu1
 */
/*
 *  Use the TraX interface to perform a transformation in the simplest manner possible
 *  (3 statements).
 */
public class XML2HTML {

    public static javax.swing.JTextArea ta;
    public static javax.swing.JButton j2;
    public static javax.swing.JButton j3;
    public static javax.swing.JButton j4;
    public static javax.swing.JTextField t2;
    public static javax.swing.JTextField t3;
    public static javax.swing.JTextField t4;
    public static javax.swing.JFrame f;
    private static String str;

    private static void preProcess(String srcFile, String targetFile) {
         String str;
try{
FileInputStream	fis2=new FileInputStream(srcFile);
DataInputStream   input = new DataInputStream (fis2);
FileOutputStream fos2=new FileOutputStream(targetFile);
DataOutputStream   output = new DataOutputStream (fos2);

while (null != ((str = input.readLine())))
{



String s2="fo:wrapper";
String s3="fo:block";

int x=0;
int y=0;
String result="";
while ((x=str.indexOf(s2, y))>-1) {
  result+=str.substring(y,x);
  result+=s3;
  y=x+s2.length();
 }
result+=str.substring(y);
str=result;

if(str.indexOf("'',") != -1){
	continue;
}
else{
str=str+"\n";

output.writeBytes(str);
}
        }
       }
        catch (IOException ioe)
        {
            System.err.println ("I/O Error - " + ioe);
        }

    }
    
    /**
     * Converts an XML file to an XSL-FO file using JAXP (XSLT).
     * @param xml the XML file
     * @param xslt the stylesheet file
     * @param fo the target XSL-FO file
     * @throws IOException In case of an I/O problem
     * @throws TransformerException In case of a XSL transformation problem
     */
    public static void convertXML2HTML(File xml, File xslt, File fo) {
    	OutputStream out = null;
    	  try {
        //Setup output
        out = new java.io.FileOutputStream(fo);
      
            //Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xslt));

            //Setup input for XSLT transformation
            Source src = new StreamSource(xml);

            //Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new StreamResult(out);

            //Start XSLT transformation and FOP processing
            transformer.transform(src, res);
        } catch(Exception e) {
        	System.out.println(e.getMessage());
        }finally {
            try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public StreamWrapper getStreamWrapper(InputStream is, String type){
            return new StreamWrapper(is, type);
}
private class StreamWrapper extends Thread {
    InputStream is = null;
    String type = null;
    String message = null;
 
    public String getMessage() {
            return message;
    }
 
    StreamWrapper(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }
 
    public void runExec() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ( (line = br.readLine()) != null) {
                buffer.append(line);//.append("\n");
            }
            message = buffer.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
    
    public static void run(String jarFile, String xsltFile, String sourceXML, String destHTML) {
    	
    	File theJarFile = new File(jarFile);
    	
    	jarFile = theJarFile.getAbsolutePath();
        // java -jar "c:\path\to\saxon9.jar" -o mydocument.html mydocument.xml NLM3-APAcit-html.xsl 
    	
    	File theXsl = new File(xsltFile);
    	File theXml = new File(sourceXML);
    	File theHtml = new File(destHTML);
    	
    	//convertXML2HTML(theXml,theXsl,theHtml);
        /**
         Runtime rt = Runtime.getRuntime();
            XML2HTML rte = new XML2HTML();
            StreamWrapper error, output;
 
            try {
                        Process proc = rt.exec("ping localhost");
                        error = rte.getStreamWrapper(proc.getErrorStream(), "ERROR");
                        output = rte.getStreamWrapper(proc.getInputStream(), "OUTPUT");
                        int exitVal = 0;
 
                        error.start();
                        output.start();
                        error.join(3000);
                        output.join(3000);
                        exitVal = proc.waitFor();
                        System.out.println("Output: "+output.message+"\nError: "+error.message);
            } catch (IOException e) {
                        e.printStackTrace();
            } catch (InterruptedException e) {
                        e.printStackTrace();
            }
**/

        String foName = null;
        try {
            // Execute a command with an argument that contains a space
            String commands = "";
            if (destHTML.contains(".htm")) {
                commands = "java -jar ";
                commands += jarFile;
                commands += " -o:";
                commands += destHTML;
                commands += " ";
                commands += sourceXML;
                commands += " ";
                commands += xsltFile;
            } else {
                //java -jar /opt/Programs/saxon9/saxon9.jar -s:index.xml -xsl:/opt/docbook/fo/docbook.xsl -o:book.fo

                String[] tempArr = destHTML.split("\\.");
                foName = tempArr[0] + ".fo";
                commands = "java -jar ";
                commands += jarFile;
                commands += " -o:";
                commands += foName;
                commands += " -s:";
                commands += sourceXML;
                commands += " -xsl:";
                commands += xsltFile;

            }
            System.out.println("\nrunning command " + commands + "\n");
            ta.append("\nrunning command " + commands + "\n");
            MainWindow.jProgressBar1.setValue(10);

            f.setCursor(Cursor.WAIT_CURSOR);
            if(Main.refs!=null && Main.refs.size()>0) {
                 Main.info("execute command:"+commands);
            } else if(RefDiviedMain.refs!=null && RefDiviedMain.refs.size()>0) {
                 RefDiviedMain.info("execute command:"+commands);
            } else if(RefSouceOnlyMain.refs!=null && RefSouceOnlyMain.refs.size()>0) {
                 RefSouceOnlyMain.info("execute command:"+commands);
            }
            Process child = Runtime.getRuntime().exec(commands);

            if (destHTML.contains(".htm")) {
            MainWindow.jProgressBar1.setValue(90);
            } else {
                MainWindow.jProgressBar1.setValue(40);
            }

         
            InputStream pStdOut = child.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(pStdOut, "UTF-8"));
            String n;
            while ((n = reader.readLine()) != null) {
            	System.out.println("\n" + n + "\n");
                ta.append("\n" + n + "\n");
            }
            
               if (destHTML.contains(".pdf")) {
                   String[] tempArr = destHTML.split("\\.");
                   String foNameName = tempArr[0] + "_FO.fo";
                   if(Main.refs!=null && Main.refs.size()>0) {
                        Main.info("prepare generate FO file:"+foName);
                   } else if(RefDiviedMain.refs!=null && RefDiviedMain.refs.size()>0) {
                        RefDiviedMain.info("prepare generate FO file:"+foName);
                   } else if(RefSouceOnlyMain.refs!=null && RefSouceOnlyMain.refs.size()>0) {
                        RefSouceOnlyMain.info("prepare generate FO file:"+foName);
                   }
                   MainWindow.jProgressBar1.setValue(60);
                   //preProcess(foName,foNameName);
                XML2PDF.ta =XML2HTML.ta;
                if(Main.refs!=null && Main.refs.size()>0) {
                        Main.info("prepare generate PDF file:"+destHTML);
                   } else if(RefDiviedMain.refs!=null && RefDiviedMain.refs.size()>0) {
                        RefDiviedMain.info("prepare generate PDF file:"+destHTML);
                   } else if(RefSouceOnlyMain.refs!=null && RefSouceOnlyMain.refs.size()>0) {
                        RefSouceOnlyMain.info("prepare generate PDF file:"+destHTML);
                   }
                XML2PDF.run(foName, destHTML);
                MainWindow.jProgressBar1.setValue(90);
            }

            f.setCursor(Cursor.DEFAULT_CURSOR);

            MainWindow.jProgressBar1.setValue(100);
            MainWindow.jProgressBar1.setString("finished");

        } catch (IOException e) {
            ta.append("\n" + e.getMessage() + "\n");
        }
        // XML2HTML         rgObject = new XML2HTML();

        //   rgObject.buildReport("C:\\Users\\DLiu1\\Documents\\NetBeansProjects\\Simon\\dist\\aaa.xml", "C:\\Users\\DLiu1\\Documents\\NetBeansProjects\\Simon\\dist\\jpub3-APAcit-html.xsl");
    }
}
