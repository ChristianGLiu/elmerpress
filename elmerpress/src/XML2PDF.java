

//Java
import java.awt.Cursor;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

//JAXP
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;

//FOP
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FormattingResults;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.apps.PageSequenceResults;

/**
 * This class demonstrates the conversion of an XML file to PDF using
 * JAXP (XSLT) and FOP (XSL-FO).
 */
public class XML2PDF {
    
    // configure fopFactory as desired
    private FopFactory fopFactory = FopFactory.newInstance();

    /**
     * Converts an FO file to a PDF file using FOP
     * @param fo the FO file
     * @param pdf the target PDF file
     * @throws IOException In case of an I/O problem
     * @throws FOPException In case of a FOP problem
     */
    public void convertFO2PDF(File fo, File pdf) throws IOException, FOPException {

        OutputStream out = null;

        try {
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            // configure foUserAgent as desired

            // Setup output stream.  Note: Using BufferedOutputStream
            // for performance reasons (helpful with FileOutputStreams).
            out = new FileOutputStream(pdf);
            out = new BufferedOutputStream(out);

            // Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Setup JAXP using identity transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(); // identity transformer

            // Setup input stream
            Source src = new StreamSource(fo);

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            transformer.transform(src, res);

            // Result processing
            FormattingResults foResults = fop.getResults();
            java.util.List pageSequences = foResults.getPageSequences();
            for (java.util.Iterator it = pageSequences.iterator(); it.hasNext();) {
                PageSequenceResults pageSequenceResults = (PageSequenceResults)it.next();
                ta.append("\nPageSequence "
                        + (String.valueOf(pageSequenceResults.getID()).length() > 0
                                ? pageSequenceResults.getID() : "<no id>")
                        + " generated " + pageSequenceResults.getPageCount() + " pages.");
            }
            ta.append("\nGenerated " + foResults.getPageCount() + " pages in total.");

        } catch (Exception e) {
            if(Main.refs!=null && Main.refs.size()>0) {
                        Main.info(e.getMessage());
                   } else if(RefDiviedMain.refs!=null && RefDiviedMain.refs.size()>0) {
                        RefDiviedMain.info(e.getMessage());
                   } else if(RefSouceOnlyMain.refs!=null && RefSouceOnlyMain.refs.size()>0) {
                        RefSouceOnlyMain.info(e.getMessage());
                   }
            System.exit(-1);
        } finally {
            out.close();
        }
    }


    /**
     * Main method.
     * @param args command-line arguments
     */
    public static void run(String foName, String pdfName) {
        try {
            ta.append("\nFOP ExampleFO2PDF\n");
            ta.append("\nPreparing...");
        
        
            ta.append("\nFOP ExampleXML2PDF\n");
            ta.append("\nPreparing...");


            //Setup input and output files
            File fofile = new File(foName);
            //File fofile = new File(baseDir, "../fo/pagination/franklin_2pageseqs.fo");
            File pdffile = new File(pdfName);

            ta.append("\nInput: XSL-FO (" + fofile + ")");
            ta.append("\nOutput: PDF (" + pdffile + ")");
            System.out.println();
            ta.append("\nTransforming...");

            new XML2PDF().convertFO2PDF(fofile, pdffile);
       

            ta.append("\nSuccess!");
        } catch (Exception e) {
            if(Main.refs!=null && Main.refs.size()>0) {
                        Main.info(e.getMessage());
                   } else if(RefDiviedMain.refs!=null && RefDiviedMain.refs.size()>0) {
                        RefDiviedMain.info(e.getMessage());
                   } else if(RefSouceOnlyMain.refs!=null && RefSouceOnlyMain.refs.size()>0) {
                        RefSouceOnlyMain.info(e.getMessage());
                   }
            System.exit(-1);
        }
    }


    
/**
     * Main method.
     * @param args command-line arguments
     */
    
    public static javax.swing.JTextArea ta;
    
    
}
