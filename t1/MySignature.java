import java.util.Arrays;
import java.security.*;
import javax.crypto.*;

public class MySignature {

  private PublicKey m_publicKey;
  private PrivateKey m_privateKey;
  private byte[] m_data;

  public void initSign(PrivateKey privateKey) {
    m_privateKey = privateKey;
  }

  public byte[] sign() throws Exception {
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    messageDigest.update(m_data);
    byte[] digest = messageDigest.digest();

    System.out.println("\nDigest:");
    t1.printHex(digest);

    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.ENCRYPT_MODE, m_privateKey);
    byte[] cipherText = cipher.doFinal(digest);

    System.out.println("\nEncrypted digest (signature):");
    printHex(cipherText);
    return cipherText;
  }

  public void update(byte[] data) {
    m_data = data;
  }

  public void initVerify(PublicKey publicKey) {
    m_publicKey = publicKey;
  }

  public boolean verify(byte[] signature) throws Exception {
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    messageDigest.update(m_data);
    byte[] digest = messageDigest.digest();
    System.out.println("\nDigest:");
    t1.printHex(digest);

    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.DECRYPT_MODE, m_publicKey);
    byte[] cipherText = cipher.doFinal(signature);

    System.out.println("\nDecrypted signature:");
    printHex(cipherText);

    return Arrays.equals(cipherText, digest);
  }

  public static void printHex(byte[] data) {
    StringBuffer buf = new StringBuffer();
    for(int i = 0; i < data.length; i++) {
      String hex = Integer.toHexString(0x0100 + (data[i] & 0x00FF)).substring(1);
      buf.append((hex.length() < 2 ? "0" : "") + hex);
    }
    System.out.println( buf.toString() );
  }
  
}