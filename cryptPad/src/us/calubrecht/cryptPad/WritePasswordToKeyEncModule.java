package us.calubrecht.cryptPad;

import java.nio.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

public class WritePasswordToKeyEncModule extends AESEncModule
{
  private static final byte[] keyBytes = new byte[]
  { -104, 19, -117, -5, -8, -63, 31, -3, -55, 60, 23, -33, -50, -8, 72, -104 };
  
  public int getVersion()
  {
    return 1;
  }
  
  @Override
  public byte[] getSaltFromBuffer(ByteBuffer buffer)
  {
    return new byte[] {};
  }
  @Override
  public byte[] newSalt()
  {
    return new byte[] {};
  }
  
  public SecretKey getKey(String password, byte[] salt)
  {
    /**
     * This module does not use salt
     */
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

}
