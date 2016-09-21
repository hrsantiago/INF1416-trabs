import java.security.*;
import java.io.*;
import java.nio.file.*;
import javax.crypto.*;
import java.util.*;

class DigestFile {

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
    String str = new String();
    str += m_filename + " ";
    str += m_realDigestType + " ";
    str += m_realDigest + " ";
    str += m_status;
    return str;
  }

  private String m_filename;
  private String m_realDigestType;
  private String m_realDigest;
  private String m_digest1Type;
  private String m_digest1;
  private String m_digest2Type;
  private String m_digest2;
  private String m_status;

}

public class DigestCalculator {

  public static void saveDigestFile(String filename, Vector<DigestFile> digestFiles) throws Exception {
    PrintWriter writer = new PrintWriter(filename, "US-ASCII");
    for(DigestFile df : digestFiles) {
      if(df.getDigest1Type() != null && df.getDigest1() != null) {
        writer.printf("%s %s %s", df.getFilename(), df.getDigest1Type(), df.getDigest1());
        if(df.getDigest2Type() != null && df.getDigest2() != null)
          writer.printf(" %s %s", df.getDigest2Type(), df.getDigest2());
        writer.printf("\n");
      }
    }
    writer.close();
  }

  public static Vector<DigestFile> loadDigestFile(String filename) throws Exception {
    Vector<DigestFile> digestFiles = new Vector<>();
    BufferedReader br = new BufferedReader(new FileReader(filename));
    try {
      String line = br.readLine();
      while(line != null) {
        String[] parts = line.split(" ");
        DigestFile digestFile = new DigestFile();
        if(parts.length >= 3) {
          digestFile.setFilename(parts[0]);
          digestFile.setDigest1Type(parts[1]);
          digestFile.setDigest1(parts[2]);
          digestFiles.add(digestFile);
        }
        if(parts.length >= 5) {
          digestFile.setDigest2Type(parts[3]);
          digestFile.setDigest2(parts[4]);
        }
        line = br.readLine();
      }
    } finally {
      br.close();
    }
    return digestFiles;
  }

  public static void main(String[] args) throws Exception {
    if(args.length < 3) {
      System.err.println("Usage: java DigestCalculator <SP> Tipo_Digest <SP>Caminho_ArqListaDigest <SP>Caminho_Arq1... <SP>Caminho_ArqN");
      System.exit(1);
    }

    String digestType = args[0];
    if(!digestType.equals("MD5") && !digestType.equals("SHA1")) {
      System.err.println("Digest type must be MD5 or SHA1");
      System.exit(1);
    }

    String digestFilename = args[1];
    Vector<DigestFile> digestFiles;
    Map<String, Integer> digestFilesMap = new HashMap<>();
    try {
      digestFiles = loadDigestFile(digestFilename);
      for(int i = 0; i < digestFiles.size(); i++)
        digestFilesMap.put(digestFiles.get(i).getFilename(), i);

    } catch(Exception e) {
      digestFiles = new Vector<>();
    }

    for(int i = 2; i < args.length; i++) {
      String filename = args[i];

      File f = new File(filename);
      if(!f.exists() || f.isDirectory())
        continue;

      DigestFile digestFile;
      Integer index = digestFilesMap.get(filename);
      if(index == null) {
        digestFile = new DigestFile();
        digestFile.setFilename(filename);
        digestFiles.add(digestFile);
        digestFilesMap.put(filename, digestFiles.size());
      }
      else
        digestFile = digestFiles.get(index);

      digestFile.calculateDigest(digestType);
      digestFile.calculateStatus(digestFiles);
      digestFile.updateDigests();

      System.out.println(digestFile.info());
    }

    saveDigestFile(digestFilename, digestFiles);
  }

  public static String byteToHex(byte[] data) {
    StringBuffer buf = new StringBuffer();
    for(int i = 0; i < data.length; i++) {
      String hex = Integer.toHexString(0x0100 + (data[i] & 0x00FF)).substring(1);
      buf.append((hex.length() < 2 ? "0" : "") + hex);
    }
    return buf.toString();
  }
}
