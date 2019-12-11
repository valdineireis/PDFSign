package br.com.valdineireis.pdfsign;

import com.itextpdf.text.log.LoggerFactory;
import com.itextpdf.text.log.SysoLogger;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author Valdinei
 */
public class PDFSignature {

    public PdfPKCS7 verifySignature(AcroFields fields, String name) throws GeneralSecurityException, IOException {
        System.out.println("Assinatura abrange todo o documento: " + fields.signatureCoversWholeDocument(name));
        System.out.println("Revisão de documentos: " + fields.getRevision(name) + " de " + fields.getTotalRevisions());
        
        PdfPKCS7 pkcs7 = fields.verifySignature(name);
        boolean valid  = pkcs7.verify();
        
        System.out.println("Verificação de integridade, está correta? " + valid);
        
        String reason       = pkcs7.getReason();
        Calendar signedAt   = pkcs7.getSignDate();
        X509Certificate signingCertificate = pkcs7.getSigningCertificate();
        Principal issuerDN  = signingCertificate.getIssuerDN();
        Principal subjectDN = signingCertificate.getSubjectDN();
        
        System.out.printf("válido (valid) = %s, data (date) = %s, razão (reason) = '%s'", valid, signedAt.getTime(), reason);
        System.out.println();
        System.out.printf("emissor (issuer) = '%s', sujeito (subject) = '%s'", issuerDN, subjectDN);
        
        return pkcs7;
    }

    public void verifySignatures(String path) throws IOException, GeneralSecurityException {
        System.out.println(path);
        
        PdfReader reader        = new PdfReader(path);
        AcroFields fields       = reader.getAcroFields();
        ArrayList<String> names = fields.getSignatureNames();
        
        for (String name : names) {
            System.out.println("::::: " + name + " :::::");
            verifySignature(fields, name);
            System.out.println();
            System.out.println("---------------------------------------------");
        }
        
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            throw new Exception("Arquivo de entrada inválido");
//        }
//        String filename = args[0];

        String filename = "C:\\dev\\test-sign\\AssinaturaDigital2_signed_signed.pdf";
        
        File f = new File(filename);
        if (!f.isFile() || !f.canRead()) {
            throw new Exception(String.format("Não é possível ler o arquivo: %s", filename));
        }

        LoggerFactory.getInstance().setLogger(new SysoLogger());
        
        // O iText exige que o BC analise assinaturas e certificados e calcule valores de hash.
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        
        PDFSignature app = new PDFSignature();
        app.verifySignatures(f.getAbsolutePath());
    }
}
