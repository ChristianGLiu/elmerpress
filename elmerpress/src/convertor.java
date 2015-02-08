
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

//Java
import java.io.File;
import java.io.OutputStream;

//JAXP
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;

//FOP
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

/**
 * This class demonstrates the conversion of an XML file to PDF using
 * JAXP (XSLT) and FOP (XSL-FO).
 */
public class convertor {
	
	
	public static void myTransformer (String sourceID, String xslID)
			throws TransformerException, TransformerConfigurationException {

			        // Create a transform factory instance.
			        TransformerFactory tfactory = TransformerFactory.newInstance();

			 // Create a transformer for the stylesheet.
			 Transformer transformer = tfactory.newTransformer(new StreamSource(new File(xslID)));

			 // Transform the source XML to System.out.
			 transformer.transform(new StreamSource(new File(sourceID)),
			    new StreamResult(System.out));
			}

    /**
     * Main method.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("FOP ExampleXML2PDF\n");
            System.out.println("Preparing...");
            
            System.setProperty("javax.xml.transform.TransformerFactory",  
            		"net.sf.saxon.TransformerFactoryImpl");


            // Setup directories
            File baseDir = new File(".");
            File outDir = new File(baseDir, "out");
            outDir.mkdirs();
            
           // myTransformer("jocmr-04-87.xml","jpub3-APAcit-xslfo.xsl");

            // Setup input and output files
            File xmlfile = new File(baseDir, "jocmr-04-87.xml");
            File xsltfile = new File(baseDir, "jpub3-PMCcit-xslfo.xsl");
            File pdffile = new File(outDir, "ResultXML2PDF.pdf");

            System.out.println("Input: XML (" + xmlfile + ")");
            System.out.println("Stylesheet: " + xsltfile);
            System.out.println("Output: PDF (" + pdffile + ")");
            System.out.println();
            System.out.println("Transforming...");

            // configure fopFactory as desired
            FopFactory fopFactory = FopFactory.newInstance();

            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            // configure foUserAgent as desired

            // Setup output
            OutputStream out = new java.io.FileOutputStream(pdffile);
            out = new java.io.BufferedOutputStream(out);

            try {
                // Construct fop with desired output format
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

                // Setup XSLT
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(new StreamSource(xsltfile));

                // Set the value of a <param> in the stylesheet
                transformer.setParameter("versionParam", "2.0");

                // Setup input for XSLT transformation
                Source src = new StreamSource(xmlfile);

                // Resulting SAX events (the generated FO) must be piped through to FOP
                Result res = new SAXResult(fop.getDefaultHandler());

                // Start XSLT transformation and FOP processing
                transformer.transform(src, res);
            } finally {
                out.close();
            }

            System.out.println("Success!");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }
}
