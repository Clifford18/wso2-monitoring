package ke.co.skyworld.utils.multi_processing;

public class Threader {

    public static void thread(Runnable runnable){
        thread(runnable, 0);
    }

    public static void thread(Runnable runnable, long delay){
        new Thread(runnable).start();
    }

}
