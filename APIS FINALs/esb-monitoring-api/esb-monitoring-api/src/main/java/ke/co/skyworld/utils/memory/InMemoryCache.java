package ke.co.skyworld.utils.memory;

import ke.co.skyworld.utils.logging.Log;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("Duplicates")
public class InMemoryCache {

    private static HashMap<String, Object[]> cache = new HashMap<>();

    /**
     *
     * @param key
     * @param value
     * @param ttl - In SECONDS
     */
    public static void store(String key, Object value, long ttl) {

        //Prevent null or empty key identifiers
        if(key == null || key.equals(""))
            throw new NullPointerException("Identifier - key cannot be null or empty");

        //Convert time to live to seconds
        if(ttl != -1) ttl = ttl*1000;

        //Store object in map (ttl stored too in case scheduler fails
        try{
            cache.put(key, new Object[]{value, (ttl != -1) ? (System.currentTimeMillis() + ttl) : ttl});
            Log.info(InMemoryCache.class, "store",
                    "Stored Entry -> key: "+key+" successfully");
        }catch (Exception e){
            Log.error(InMemoryCache.class, "store",
                    "Error storing Entry -> key"+key+" ("+e.getMessage()+")");
        }

        //Schedule Map entry for deletion after ttl seconds
        if(ttl != -1){
            Timer t = new Timer();
            t.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            remove(key);
                            t.cancel();
                        }
                    }, ttl
            );
        }
    }

    public static Object retrieve(String key) {

        try{
            //Check if key exists. Else return null
            if (exists(key)) {

                Long expires = (Long) cache.get(key)[1];
                if(expires != -1){

                    //Check if entry has expired. If so, delete it. Else retrieve entry and return
                    if (expires - System.currentTimeMillis() > 0) {
                        return cache.get(key)[0];
                    } else {
                        remove(key);
                    }
                }else{
                    return cache.get(key)[0];
                }
            }
        }catch (Exception e){
            Log.error(InMemoryCache.class, "retrieve", "Error retrieving Entry -> key: "+key);
        }

        return null;
    }

    public static Object retrieveAndRemove(String key) {

        //Check if key exists. Else return null
        if (exists(key)) {
            //Retrieve entry and delete it
            Object obj = retrieve(key);
            remove(key);
            return obj;
        }

        return null;
    }

    public static void remove(String key) {
        try{
            cache.remove(key);
            Log.info(InMemoryCache.class, "remove",
                    "Removed Entry -> key: "+key+" successfully");
        } catch (NullPointerException ignore){}
        catch (Exception e){
            Log.error(InMemoryCache.class, "remove",
                    "Error removing Entry -> key: "+key+" ("+e.getMessage()+")");
        }
    }

    public static boolean exists(String  key){
        return cache.containsKey(key);
    }

    public static boolean existsAndRemove(String  key){
        if(cache.containsKey(key)){
            remove(key);
            return true;
        } else return false;
    }

    public static void dump() {
        Log.debug(InMemoryCache.class, "dump", "\n*****************************************************");
        Log.debug(InMemoryCache.class, "dump", "        START InMemoryCache.dump()                   ");
        Log.debug(InMemoryCache.class, "dump", "*****************************************************\n");
        cache.forEach((key, value)
                -> Log.info(InMemoryCache.class, "dump", "key -> "+key+" \t value -> "+value[0])
        );
        Log.debug(InMemoryCache.class, "dump", "\n\n*****************************************************");
        Log.debug(InMemoryCache.class, "dump", "          END InMemoryCache.dump()                   ");
        Log.debug(InMemoryCache.class, "dump", "*****************************************************\n");
    }


}
