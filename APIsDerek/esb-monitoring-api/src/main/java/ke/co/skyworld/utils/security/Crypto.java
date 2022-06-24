package ke.co.skyworld.utils.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Crypto {

	public String encrypt(String key, String cleartext) {
		IvParameterSpec iv = null;
		SecretKeySpec skeySpec = null;
		Cipher cipher = null;
		
        try {
        	String hashKey = hash("MD5", key);
        	String ivKey = hashKey.substring(0,16); 
        	String encKey = hashKey.substring(16,32); 
        	
            iv = new IvParameterSpec(ivKey.getBytes(StandardCharsets.UTF_8));

            skeySpec = new SecretKeySpec(encKey.getBytes(StandardCharsets.UTF_8),
                    "Rijndael");
            cipher = Cipher.getInstance("Rijndael/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(cleartext.getBytes());
            //System.out.println("Encrypted String: " + Base64.encodeBase64String(encrypted));
            iv = null;
    		skeySpec = null;
    		cipher = null;
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally
		{
        	iv = null;
    		skeySpec = null;
    		cipher = null;
		}
        return null;
    }
    
    public String decrypt(String key, String encrypted) {
    	IvParameterSpec iv = null;
		SecretKeySpec skeySpec = null;
		Cipher cipher = null;
		
        try {
        	String hashKey = hash("MD5", key);
        	String ivKey = hashKey.substring(0,16); 
        	String encKey = hashKey.substring(16,32);
        	
            iv = new IvParameterSpec(ivKey.getBytes(StandardCharsets.UTF_8));

            skeySpec = new SecretKeySpec(encKey.getBytes(StandardCharsets.UTF_8),
                    "Rijndael");
            cipher = Cipher.getInstance("Rijndael/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally
		{
        	iv = null;
    		skeySpec = null;
    		cipher = null;
		}
        return null;
    }  
    
    public static String hash(String algo, String data) {
    	MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(algo);
            messageDigest.reset();
            messageDigest.update(data.getBytes(StandardCharsets.UTF_8));
            final byte[] resultByte = messageDigest.digest();
            final String result = new String(Hex.encodeHex(resultByte));
            messageDigest = null;
            return result; 
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally
		{
        	messageDigest = null;
		}
        return null;
    }
}
