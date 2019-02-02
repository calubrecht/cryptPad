package us.calubrecht.cryptPad;

import java.io.*;
import java.nio.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

public class FileIO
{
  private static final byte[] keyBytes = new byte[]
  { -104, 19, -117, -5, -8, -63, 31, -3, -55, 60, 23, -33, -50, -8, 72, -104 };
  
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

  public String loadText(File f, String password) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException,
      InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
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
      int ivLength = allBytes.getInt();
      if (ivLength < 12 || ivLength >= 16)
      { // check input parameter
        throw new IllegalArgumentException("invalid iv length");
      }
      byte withPassword = allBytes.get();
      byte[] iv = new byte[ivLength];
      allBytes.get(iv);
      byte[] cipherText = new byte[allBytes.remaining()];
      allBytes.get(cipherText);
      SecretKey key = getKey(withPassword == (byte)1 ? password : "");

      final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); // 128 bit auth tag length
      cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
      byte[] plainText = cipher.doFinal(cipherText);
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

  public SecretKey getKey(String password)
  {
    if (password.isEmpty())
    {
      return new SecretKeySpec(keyBytes, "AES");
    }
    byte[] thisKey = Arrays.copyOf(keyBytes, keyBytes.length);
    byte[] passwordBytes = password.getBytes();
    for (int i = 0; i < password.length(); i++)
    {
      thisKey[i] = passwordBytes[i];
    }
    return new SecretKeySpec(thisKey, "AES");
  }

  public File saveText(File f, String text, String password) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException,
      InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
  {
    if (isEncryptedFile(f))
    {
      boolean withPassword = !password.isEmpty();
      SecretKey key = getKey(password);
      byte[] iv = new byte[12]; // NEVER REUSE THIS IV WITH SAME KEY
      SecureRandom secureRandom = new SecureRandom();
      secureRandom.nextBytes(iv);
      final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); // 128 bit auth tag length
      cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
      byte[] cipherText = cipher.doFinal(text.getBytes());

      ByteBuffer byteBuffer = ByteBuffer.allocate(5 + iv.length + cipherText.length);
      byteBuffer.putInt(iv.length);
      byteBuffer.put(withPassword ? (byte)1 : (byte)0);
      byteBuffer.put(iv);
      byteBuffer.put(cipherText);
      byte[] cipherMessage = byteBuffer.array();
      FileOutputStream fos = new FileOutputStream(f);
      fos.write(cipherMessage);
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
