package br.com.valdineireis.pdfsign.exemplo;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.ByteBuffer;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 *
 * @author Valdinei
 */
public class AssinaEAdicionaRotuloPDF {

    /**
     * Documento a ser criado
     */
    static String fname = "C:\\dev\\test-sign\\AssinaturaDigital_Teste.pdf";

//    /**
//     * Documento Assinado
//     */
//    static String fnameS = "C:\\dev\\test-sign\\HelloWorld_sign.pdf";

    public static void main(String[] args) {
        try {
            AssinaEAdicionaRotuloPDF sign_pdf = new AssinaEAdicionaRotuloPDF();

            //sign_pdf.buildPDF();
            sign_pdf.signPdf();
        } catch (Exception e) {
        }
    }

    /**
     * Cria um simples PDF "Hello World"
     */
    public static void buildPDF() {

        // Creation du document
        Document document = new Document();

        try {
            // Creation du "writer" vers le doc
            // directement vers un fichier
            PdfWriter.getInstance(document,
                    new FileOutputStream(fname));
            // Ouverture du document
            document.open();

            // Ecriture des datas
            document.add(new Paragraph("Hello World"));

        } catch (DocumentException de) {
            System.err.println(de.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        // Fermeture du document
        document.close();

    }

    /**
     * Assina o PDF
     */
    public static final boolean signPdf()
            throws IOException, DocumentException, Exception {

        //caminho da certificação
        String fileKey = "C:\\dev\\test-sign\\valdinei.p12";
        //senha da certificação
        String fileKeyPassword = "123456";

        try {

            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(fileKey), fileKeyPassword.toCharArray());
            String alias = (String) ks.aliases().nextElement();

            // Recupera a chave privada
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, fileKeyPassword.toCharArray());

//            System.out.println("algoritmo = " + privateKey.getEncoded());

            // pega o certificado
            Certificate[] certificateChain = ks.getCertificateChain(alias);

//            for (int i = 0; i < certificateChain.length - 1; i++) {
//                System.out.print(certificateChain[i]);
//            }

            File inFile = new File(fname);
            String fileName = inFile.getName();
            String substring = fileName.substring(0, fileName.lastIndexOf('.'));
            
            // Le o Documento Fonte
            PdfReader pdfReader = new PdfReader(inFile.getAbsolutePath());
            File outputFile = new File(inFile.getParent(), substring + "_Assinado.pdf");
            
            // Criação do selo de assinatura
            PdfStamper pdfStamper = PdfStamper.createSignature(pdfReader, null, '\0', outputFile);
            PdfSignatureAppearance appearance = pdfStamper.getSignatureAppearance();
//            appearance.setSignatureCreator("Nome da empresa que criou a assinatura no documento.");
//            appearance.setCertificate(certificateChain[0]);
            
            int lastPage = pdfReader.getNumberOfPages();
            
            // This method has to be called as an alternative to 'com.itextpdf.text.pdf.PdfSignatureAppearance.setVisibleSignature'.
            //new Rectangle(<margem esquerda>, <margem do rodapé>, <largura>, <altura>)
//            setVisibleSignatureRotated(pdfStamper, appearance, new Rectangle(120, 650, 170, 770), lastPage, null);
            appearance.setVisibleSignature(new Rectangle(50, 20, 250, 100), lastPage, null);
            
            // Perform the signature.
            ExternalSignature externalSignature = new PrivateKeySignature(privateKey, "SHA-256", null);
            ExternalDigest externalDigest = new BouncyCastleDigest();
            MakeSignature.signDetached(appearance, externalDigest, externalSignature, certificateChain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);
            
            pdfStamper.close();

            return true;
        } catch (Exception key) {
            throw new Exception(key);
        }
    }
    
    /*
    private static void setVisibleSignatureRotated(PdfStamper stamper, PdfSignatureAppearance appearance, Rectangle pageRect, int page, String fieldName) throws DocumentException, IOException {
        float height = pageRect.getHeight();
        float width = pageRect.getWidth();
        float llx = pageRect.getLeft();
        float lly = pageRect.getBottom();
        // Visual signature is configured as if it were going to be a regular horizontal visual signature.
        appearance.setVisibleSignature(new Rectangle(llx, lly, llx + height, lly + width), page, null);
        // We trigger premature appearance creation, so independent parts of it can be modified right away.
        appearance.getAppearance();
        // Now we correct the width and height.
        appearance.setVisibleSignature(new Rectangle(llx, lly, llx + width, lly + height), page, fieldName);
        appearance.getTopLayer().setWidth(width);
        appearance.getTopLayer().setHeight(height);
        PdfTemplate n2Layer = appearance.getLayer(2);
        n2Layer.setWidth(width);
        n2Layer.setHeight(height);
        // Then we rotate the n2 layer. See http://developers.itextpdf.com/question/how-rotate-paragraph.
        PdfTemplate t = PdfTemplate.createTemplate(stamper.getWriter(), height, width);
        ByteBuffer internalBuffer = t.getInternalBuffer();
        internalBuffer.write(n2Layer.toString().getBytes());
        n2Layer.reset();
        Image textImg = Image.getInstance(t);
        textImg.setInterpolation(true);
        textImg.scaleAbsolute(height, width);
        textImg.setRotationDegrees((float) 90);
        textImg.setAbsolutePosition(0, 0);
        n2Layer.addImage(textImg);
    }
    */
}
