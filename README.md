cryptPad is a simple note-pad like application that loads and saves files in an encrypted format.

The encrypted files uses PBKDF2 to create an AES key from a password, and encrypt the data from that. Keys will be unique,
even if created from the same password, but still vulnerable to brute forcing the password.
