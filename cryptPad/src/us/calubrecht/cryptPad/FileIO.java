package us.calubrecht.cryptPad;

import java.io.*;
import java.nio.*;
import java.security.*;
import java.util.*;


public class FileIO
{
  static final int DEFAULT_ENCRYPTION_VERSION = 2;
  
  Map<Integer, EncryptionModule> encryptionModules_;
  {
    EncryptionModule[] modules =  new EncryptionModule[] {new PBKDF2_AESEncModule(), new WritePasswordToKeyEncModule()};
    encryptionModules_ = new HashMap<Integer, EncryptionModule>();
    for (EncryptionModule module : modules)
    {
      assert !encryptionModules_.containsKey(module.getVersion());
      encryptionModules_.put(module.getVersion(), module);
    }
  }
  
  public static boolean isEncryptedFile(File f)
  {
    String fileExtension = getFileExtension(f);
    return fileExtension.toLowerCase().contentEquals(CryptPadApp.FILE_EXTENSION);
  }
  
  public static File correctFileName(File f)
  {
    String fileExtension = getFileExtension(f);
    return fileExtension.isEmpty() ? new File(f.getPath() + "." + CryptPadApp.FILE_EXTENSION) : f;
  }
  
  private EncryptionModule getEncryptionModule(int encryptionVersion)
  {
    return encryptionModules_.get(encryptionVersion);
  }

  public String loadText(File f, String password) throws GeneralSecurityException, IOException
  {
    if (!f.exists())
    {
      throw new IOException("The file \"" + f.getPath() + "\" does not exist");
    }
    if (isEncryptedFile(f))
    {
      FileInputStream fis = new FileInputStream(f);
      ByteBuffer allBytes = ByteBuffer.wrap(fis.readAllBytes());
      fis.close();
      int encryptionVersion = allBytes.getInt();
      EncryptionModule encModule = getEncryptionModule(encryptionVersion);
      if (encModule == null)
      {
        throw new IOException("Unknown Encryption Version");
      }
      byte[] plainText = encModule.doDecrypt(allBytes, password);
      return new String(plainText);
    }
    Scanner sc = new Scanner(f);
    StringBuilder builder = new StringBuilder((int) f.length());
    while (sc.hasNextLine())
    {
      builder.append(sc.nextLine()).append(System.lineSeparator());
    }
    sc.close();
    return builder.toString();
  }

  public static String generateKey()
  {
    SecureRandom secureRandom = new SecureRandom();
    byte[] key = new byte[16];
    secureRandom.nextBytes(key);

    List<String> stringBytes = new ArrayList<String>(16);
    for (byte k : key)
    {
      stringBytes.add(Byte.toString(k));
    }
    return "new byte[] {" + String.join(",", stringBytes) + "};";
  }

  public File saveText(File f, String text, String password) throws IOException, GeneralSecurityException
  {
    if (isEncryptedFile(f))
    {

      int version = DEFAULT_ENCRYPTION_VERSION;
      EncryptionModule module = getEncryptionModule(version);
      byte[] cipherMessage = module.doEncrypt(text,password);
      ByteBuffer byteBuffer = ByteBuffer.allocate(4 +cipherMessage.length);
      byteBuffer.putInt(version);
      byteBuffer.put(cipherMessage);
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(byteBuffer.array());
      fos.close();
      return f;
    }
    FileWriter writer = new FileWriter(f);
    writer.write(text);
    writer.close();
    return f;
  }

  private static String getFileExtension(File file)
  {
    String name = file.getName();
    int lastIndexOf = name.lastIndexOf(".");
    if (lastIndexOf == -1)
    {
      return ""; // empty extension
    }
    return name.substring(lastIndexOf + 1);
  }
}
