import java.security.*;
import java.io.*;
import java.nio.file.*;
import javax.crypto.*;
import java.util.*;

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
