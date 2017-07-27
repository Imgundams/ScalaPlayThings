package models

import java.security.MessageDigest
import java.util
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64

class Encryption {

  object Encryption {
    def encrypt(key: String, value: String): String = {
      val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
      cipher.init(Cipher.ENCRYPT_MODE, keyToSpec(key))
      Base64.encodeBase64String(cipher.doFinal(value.getBytes("UTF-8")))
    }
//  This code can't decrypt
    def decrypt(key: String, encryptedValue: String): String = {
      val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
      cipher.init(Cipher.DECRYPT_MODE, keyToSpec(key))
      new String(cipher.doFinal(Base64.decodeBase64(encryptedValue)))
    }

    def keyToSpec(key: String): SecretKeySpec = {
      var keyBytes: Array[Byte] = (key).getBytes("UTF-8")
      val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
      keyBytes = sha.digest(keyBytes)
      keyBytes = util.Arrays.copyOf(keyBytes, 16)
      new SecretKeySpec(keyBytes, "AES")
    }
//  Make the salt generated to make the Sha better
    private val SALT: String =
      "ThisIsSaltForTheSHAOne"
  }

}