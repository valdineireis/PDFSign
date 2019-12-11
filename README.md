# PDFSign

Laboratório para realizar/validar assinaturas de documentos.

## Gerar certificado

Exemplo de geração de certificado no computador local.

```sh
$ keytool -genkeypair -storepass 123456 -storetype pkcs12 -alias test -validity 365 -v -keyalg RSA -keystore keystore.p12
```

## Bibliotecas

Principais bibliotecas utilizadas. Conferir o arquivo _pom.xml_.

- org.apache.pdfbox: [pdfbox](https://mvnrepository.com/artifact/org.apache.pdfbox/pdfbox)
- com.itextpdf: [itextpdf](https://mvnrepository.com/artifact/com.itextpdf/itextpdf)
- org.bouncycastle: [bcprov-jdk15on](https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on) e [bcpkix-jdk15on](https://mvnrepository.com/artifact/org.bouncycastle/bcpkix-jdk15on)

> O **iText** precisa do **BouncyCastle** para realizar análise das assinaturas e certificados e calcular valores de hash.

Exemplo de criação do _provider_ do BouncyCastle:

```java
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

// demais código ...

BouncyCastleProvider provider = new BouncyCastleProvider();
Security.addProvider(provider);
```
