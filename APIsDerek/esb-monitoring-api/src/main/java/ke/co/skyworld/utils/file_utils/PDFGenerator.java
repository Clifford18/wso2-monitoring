package ke.co.skyworld.utils.file_utils;

import com.openhtmltopdf.pdfboxout.PdfBoxRenderer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * skyworld-api (elon)
 * Created by: elon
 * On: 06 May, 2021. 13:55
 **/
public class PDFGenerator {

    public void generateThePDF(String htmlString, String fileName) {
        PDDocument doc = new PDDocument();
        FileOutputStream fos = null;
        try {
            String baseUrl = this.getClass()
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toString();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlString, baseUrl);
            builder.usePDDocument(doc);
            builder.useSVGDrawer(new BatikSVGDrawer());
            PdfBoxRenderer renderer = builder.buildPdfRenderer();
            renderer.createPDFWithoutClosing();
            renderer.close();

            //UNCOMMENT IF YOU WANT TO PASSWORD PROTECT THE DOCUMENT.
            /*
                AccessPermission ap = new AccessPermission();
                StandardProtectionPolicy spp = new StandardProtectionPolicy("PASSWORD_HERE", "PASSWORD_HERE", ap);
                doc.protect(spp);*/


            fos = new FileOutputStream(fileName, false);
            doc.save(fos);
            fos.close();
            doc.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        finally {
            try {
                doc.close();
            }
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }


    public void generateThePDF(List<String> allPayslipsHTML) {
        PDDocument doc = new PDDocument();
        FileOutputStream fos = null;
        try {
            String baseUrl = this.getClass()
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toString();

            for (String payslip : allPayslipsHTML) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(payslip, baseUrl);
                builder.usePDDocument(doc);
                builder.useSVGDrawer(new BatikSVGDrawer());
                PdfBoxRenderer renderer = builder.buildPdfRenderer();
                renderer.createPDFWithoutClosing();
                renderer.close();
            }

            //UNCOMMENT IF YOU WANT TO PASSWORD PROTECT THE DOCUMENT.
            /*
                AccessPermission ap = new AccessPermission();
                StandardProtectionPolicy spp = new StandardProtectionPolicy("PASSWORD_HERE", "PASSWORD_HERE", ap);
                doc.protect(spp);*/

            fos = new FileOutputStream("receipt2.pdf", false);
            doc.save(fos);
            fos.close();
            doc.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        finally {
            try {
                doc.close();
            }
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
