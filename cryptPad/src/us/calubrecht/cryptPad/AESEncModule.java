package us.calubrecht.cryptPad;

import java.nio.*;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.*;

public abstract class AESEncModule implements EncryptionModule
{
  public abstract SecretKey getKey(String password, byte[] salt)  throws GeneralSecurityException;

  @Override
  public byte[] doDecrypt(ByteBuffer allBytes, String password) throws GeneralSecurityException
  {
    byte[] salt = getSaltFromBuffer(allBytes);
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
    SecretKey key = getKey(withPassword == (byte)1 ? password : "", salt);

    final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); // 128 bit auth tag length
    cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
    return cipher.doFinal(cipherText);
  }
  
  public abstract int getVersion();
  
  protected int getIVLength()
  {
    return 12;
  }
  
  public abstract byte[] getSaltFromBuffer(ByteBuffer buffer);
  public abstract byte[] newSalt();
  
  @Override
  public byte[] doEncrypt(String text, String password) throws GeneralSecurityException
  {
    boolean withPassword = !password.isEmpty();
    byte[] salt = newSalt();
    SecretKey key = getKey(password, salt);
    byte[] iv = new byte[getIVLength()]; // NEVER REUSE THIS IV WITH SAME KEY
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(iv);
    final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); // 128 bit auth tag length
    cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
    byte[] cipherText = cipher.doFinal(text.getBytes());

    ByteBuffer byteBuffer = ByteBuffer.allocate(5 + salt.length + iv.length + cipherText.length);
    byteBuffer.put(salt);
    byteBuffer.putInt(iv.length);
    byteBuffer.put(withPassword ? (byte)1 : (byte)0);
    byteBuffer.put(iv);
    byteBuffer.put(cipherText);
    return byteBuffer.array();
  }

}
