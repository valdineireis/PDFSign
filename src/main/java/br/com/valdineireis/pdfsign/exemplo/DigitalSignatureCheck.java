package br.com.valdineireis.pdfsign.exemplo;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class DigitalSignatureCheck {

    public static final boolean verifySignature(PdfReader pdfReader)
            throws GeneralSecurityException, IOException {
        boolean valid = false;
        AcroFields acroFields = pdfReader.getAcroFields();
        List<String> signatureNames = acroFields.getSignatureNames();
        
        if (!signatureNames.isEmpty()) {
            for (String name : signatureNames) {
                if (acroFields.signatureCoversWholeDocument(name)) {
                    PdfPKCS7 pkcs7 = acroFields.verifySignature(name);
                    valid = pkcs7.verify();
                    String reason = pkcs7.getReason();
                    Calendar signedAt = pkcs7.getSignDate();
                    X509Certificate signingCertificate = pkcs7.getSigningCertificate();
                    Principal issuerDN = signingCertificate.getIssuerDN();
                    Principal subjectDN = signingCertificate.getSubjectDN();

                    System.out.printf("valid = %s, date = %s, reason = '%s', issuer = '%s', subject = '%s'",
                            valid, signedAt.getTime(), reason, issuerDN, subjectDN);

                    break;
                }
            }
        }
        return valid;
    }

    private static void validate(String name)
            throws IOException, GeneralSecurityException {
        InputStream is = DigitalSignatureCheck.class.getClassLoader()
                .getResourceAsStream(name);
        
        PdfReader reader = new PdfReader(is);
        boolean ok = verifySignature(reader);

        System.out.println("");
        System.out.printf("'%s' is %ssigned", name, ok ? "" : "NOT ");
        //LOGGER.info("'{}' is {}signed", name, ok ? "" : "NOT ");
    }

    public static void main(String[] args) throws Exception {
        // O iText exige que o BC analise assinaturas e certificados e calcule valores de hash.
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        provider.clone();
        
        // if placed in resources' root
        validate("AssinaturaDigital_Teste_Assinado.pdf");
//        validate("AssinaturaDigital.pdf");
    }
}
