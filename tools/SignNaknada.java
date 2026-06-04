import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class SignNaknada {
    public static void main(String[] args) throws Exception {
        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: java SignNaknada <naknada.json> <private_key.pem> [output.sig]");
            System.exit(1);
        }

        Path jsonPath = Path.of(args[0]);
        Path privateKeyPath = Path.of(args[1]);
        Path signaturePath = args.length == 3 ? Path.of(args[2]) : Path.of(args[0] + ".sig");

        byte[] jsonBytes = Files.readAllBytes(jsonPath);
        PrivateKey privateKey = loadPrivateKey(privateKeyPath);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(jsonBytes);

        String signatureBase64 = Base64.getEncoder().encodeToString(signature.sign());
        Files.writeString(signaturePath, signatureBase64 + System.lineSeparator(), StandardCharsets.US_ASCII);
        System.out.println("Signature written to: " + signaturePath.toAbsolutePath());
    }

    private static PrivateKey loadPrivateKey(Path privateKeyPath) throws Exception {
        String pem = Files.readString(privateKeyPath, StandardCharsets.US_ASCII)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(pem);
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
    }
}
