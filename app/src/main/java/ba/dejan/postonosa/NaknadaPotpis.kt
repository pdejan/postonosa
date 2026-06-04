package ba.dejan.postonosa

import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

object NaknadaPotpis {
    private const val JAVNI_KLJUC_BASE64 =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnA75EufrKIHj48lwmufb32C20+JTOIunYmv6kvnCXinZFJko9OMX0phvftwfG5tcNhJ0Fmy690XoEDG57f8IAn66PZMwkV1QFQq/+ZhJBJnKF+3FT1ypXbYSChCyjD87VNUeuG0kR11XWP1SGFQONE92k6SLk8q7iFk9hNtwKnKSBIhPdjP+dUs2am3hgyMlQpr6/oNkX2Z7lM7Ki4mYnDHuWN+3hyKf2wKKeSDMkIC5Lur3lFoMWY7eIFYRP3EvtYn8I1KsoIU2s2LSNTHicLx+o8yuLN/OTNpx0wDlaDoAaR+/lTyAoQLP9iX2U6gFepxFcVeGfOd1JmZRFAgh1wIDAQAB"

    fun validan(jsonBytes: ByteArray, potpisBase64: String): Boolean {
        return try {
            val javniKljucBytes = Base64.getDecoder().decode(JAVNI_KLJUC_BASE64)
            val potpisBytes = Base64.getMimeDecoder().decode(potpisBase64)
            val javniKljuc = KeyFactory.getInstance("RSA")
                .generatePublic(X509EncodedKeySpec(javniKljucBytes))

            Signature.getInstance("SHA256withRSA").run {
                initVerify(javniKljuc)
                update(jsonBytes)
                verify(potpisBytes)
            }
        } catch (e: Exception) {
            false
        }
    }
}
