import java.util.Arrays;
import java.security.*;
import javax.crypto.*;

class MySignature {

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
    t1.printHex(cipherText);
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
    t1.printHex(cipherText);

    return Arrays.equals(cipherText, digest);
  }

  private PublicKey m_publicKey;
  private PrivateKey m_privateKey;
  private byte[] m_data;
}

public class t1 {

  public static void main(String[] args) throws Exception {
    if(args.length !=1) {
      System.err.println("Usage: java t1 text");
      System.exit(1);
    }

    byte[] plainText = args[0].getBytes("UTF8");

    KeyPair key = generateKeyPair();

    MySignature sig = new MySignature();
    sig.initSign(key.getPrivate());
    sig.update(plainText);
    byte[] signature = sig.sign();

    System.out.println("\nStart signature verification");
    sig.initVerify(key.getPublic());
    sig.update(plainText);
    try {
      if(sig.verify(signature))
        System.out.println("Signature verified");
      else
        System.out.println("Signature failed");
    }
    catch(Exception se) {
      System.out.println( "Singature failed" );
    }
  }

  public static void printHex(byte[] data) {
    StringBuffer buf = new StringBuffer();
    for(int i = 0; i < data.length; i++) {
      String hex = Integer.toHexString(0x0100 + (data[i] & 0x00FF)).substring(1);
      buf.append((hex.length() < 2 ? "0" : "") + hex);
    }
    System.out.println( buf.toString() );
  }

  public static KeyPair generateKeyPair() throws Exception {
    System.out.println( "\nStart generating RSA key" );
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(1024);
    KeyPair key = keyGen.generateKeyPair();
    System.out.println( "Finish generating RSA key" );
    return key;
  }
}
