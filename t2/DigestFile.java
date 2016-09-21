import java.security.*;
import java.io.*;
import java.nio.file.*;
import javax.crypto.*;
import java.util.*;

public class DigestFile {

  private String m_filename;
  private String m_realDigestType;
  private String m_realDigest;
  private String m_digest1Type;
  private String m_digest1;
  private String m_digest2Type;
  private String m_digest2;
  private String m_status;

  /* Getters and Setters */
  public String getFilename() { return m_filename; }
  public String getDigest1Type() { return m_digest1Type; }
  public String getDigest1() { return m_digest1; }
  public String getDigest2Type() { return m_digest2Type; }
  public String getDigest2() { return m_digest2; }

  public void setFilename(String filename) { m_filename = filename; }
  public void setDigest1Type(String digest1Type) { m_digest1Type = digest1Type; }
  public void setDigest1(String digest1) { m_digest1 = digest1.toLowerCase(); }
  public void setDigest2Type(String digest2Type) { m_digest2Type = digest2Type; }
  public void setDigest2(String digest2) { m_digest2 = digest2.toLowerCase(); }

  public void calculateDigest(String digestType) throws Exception {
    Path path = Paths.get(m_filename);
    byte[] data = Files.readAllBytes(path);

    MessageDigest messageDigest = MessageDigest.getInstance(digestType);
    messageDigest.update(data, 0, data.length);
    byte[] digest = messageDigest.digest();
    m_realDigest = DigestCalculator.byteToHex(digest).toLowerCase();
    m_realDigestType = digestType;
  }

  public void calculateStatus(Vector<DigestFile> digestFiles) {
    for(DigestFile digestFile : digestFiles) {
      if(digestFile.getFilename() != m_filename && digestFile.matchDigest(m_realDigestType, m_realDigest)) {
        m_status = "COLLISION";
        return;
      }
    }

    if(matchDigest(m_realDigestType, m_realDigest)) {
      m_status = "OK";
      return;
    }

    if(m_digest1 == null || m_digest2 == null) {
      m_status = "NOT FOUND";
      return;
    }

    m_status = "NOT OK";
  }

  public void updateDigests() {
    if(m_status == "NOT FOUND") {
      if(m_digest1 == null) {
        m_digest1Type = m_realDigestType;
        m_digest1 = m_realDigest;
      }
      else if(m_digest2 == null) {
        m_digest2Type = m_realDigestType;
        m_digest2 = m_realDigest;
      }
    }
  }

  public boolean matchDigest(String digestType, String digest) {
    if(digestType.equals(m_digest1Type) && digest.equals(m_digest1))
      return true;
    if(digestType.equals(m_digest2Type) && digest.equals(m_digest2))
      return true;
    return false;
  }

  public String info() {
    String str = "";
    str += m_filename + " ";
    str += m_realDigestType + " ";
    str += m_realDigest + " ";
    str += m_status;
    return str;
  }

}