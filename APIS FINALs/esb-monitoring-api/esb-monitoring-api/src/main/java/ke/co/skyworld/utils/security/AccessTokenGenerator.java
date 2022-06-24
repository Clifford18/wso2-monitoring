package ke.co.skyworld.utils.security;

public class AccessTokenGenerator {
    public static String getAccessToken(){
        return ScedarUID.generateUid();
    }

    public static String getAccessToken(int length){
        return ScedarUID.generateUid(length);
    }

}
