import java.util.Arrays;
import java.security.*;
import javax.crypto.*;

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

  public static KeyPair generateKeyPair() throws Exception {
    System.out.println( "\nStart generating RSA key" );
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(1024);
    KeyPair key = keyGen.generateKeyPair();
    System.out.println( "Finish generating RSA key" );
    return key;
  }
}
