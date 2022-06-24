package ke.co.skyworld;

import static org.junit.Assert.assertTrue;

import ke.co.skyworld.utils.security.HashUtils;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Unit test for simple App.
 */
public class Security
{
    @Test
    public void encrypto(){
        System.out.println("johnkarani@skyworld.co.ke -> "+
                HashUtils.base64Encode(BCrypt.hashpw("johnkarani@skyworld.co.ke::"+HashUtils.SHA256("Esb2022$"),
                        BCrypt.gensalt(4))));
        //JDJhJDA0JEpCU1lQL3RTWDJZeG5xODkwN3B5cC5qa2tpOWdhWVhFenVHamhUdkFNaGtaS0lGYW4xeXhH

        System.out.println("Esb2020$ -> "+
                HashUtils.base64Encode(BCrypt.hashpw(HashUtils.SHA256("Esb@2022$"),
                        BCrypt.gensalt(4))));
        //JDJhJDA0JEswdGZXMzguT2JSZGlyTHhleFp3YU9USEYuVXdteFN3Y3JZRm5OSFlFVFV2LjhHcFBXNlBp

    }
}
