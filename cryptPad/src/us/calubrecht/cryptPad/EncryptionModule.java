package us.calubrecht.cryptPad;

import java.nio.*;
import java.security.*;

public interface EncryptionModule
{
  public byte[] doDecrypt(ByteBuffer allBytes, String password) throws GeneralSecurityException;
  public byte[] doEncrypt(String data, String password) throws GeneralSecurityException;
  
  public int getVersion();
}
