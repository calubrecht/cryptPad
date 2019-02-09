package us.calubrecht.cryptPad;

import java.nio.*;
import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.*;

public class PBKDF2_AESEncModule extends AESEncModule
{
  private static byte[] keyBytes = new byte[] {74,5,33,-124,-80,-31,-127,80,-115,-48,-126,21,-45,-18,-121,-1};

  @Override
  public SecretKey getKey(String password, byte[] salt) throws GeneralSecurityException
  {
    // TODO Auto-generated method stub  
    if (password.equals(""))
    {
      return new SecretKeySpec(keyBytes, "AES");
    }
    
    int iterations = 500000;
    char[] chars = password.toCharArray();
     
    PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 16 * 8);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    SecretKey PBKDFkey = skf.generateSecret(spec);
    SecretKey AESKey = new SecretKeySpec(PBKDFkey.getEncoded(), "AES");
    return AESKey;
  }

  @Override
  public int getVersion()
  {
    return 2;
  }
  
  @Override
  public byte[] getSaltFromBuffer(ByteBuffer buffer)
  {
    byte[] salt = new byte[16];
    buffer.get(salt);
    return salt;
  }
  @Override
  public byte[] newSalt()
  {
    byte[] salt = new byte[16];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(salt);
    return salt;
  }
  
  public static void main(String[] args) throws GeneralSecurityException
  {
    PBKDF2_AESEncModule m = new PBKDF2_AESEncModule();
    byte[] salt = m.newSalt();
    SecretKey key = m.getKey("afkjsalkdflasdf234324", salt);
    
    long start = System.currentTimeMillis();
    int runs = 50;
    for (int i = 1; i < runs; i++)
    {
      salt = m.newSalt();
      key = m.getKey("afkjsalkdflasdf234324", salt);
    }
    long end = System.currentTimeMillis(); 
    System.out.println(""  + runs + " runs took " + (end -start)/1000.0/runs + "s each");
  }
}
