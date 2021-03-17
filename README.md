cryptPad is a simple note-pad like application that loads and saves files in an encrypted format.

The encrypted files uses PBKDF2 to create an AES key from a password, and encrypt the data from that. Keys will be unique,
even if created from the same password, but still vulnerable to brute forcing the password.

To associate this program with .cpf files in windows, compile the app to a jar and run the follow commands from an administrator command shell.

> assoc .cpf="CryptPad File"
> 
> ftype "CryptPad File"=<PATH TO JAVA EXECUTABLES>/javaw.exe -jar <PATH TO JAR>\CryptPad.jar %1
