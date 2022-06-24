package ke.co.skyworld.domain.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * sls-api (ke.co.scedar.domain.beans)
 * Created by: elon
 * On: 03 Jul, 2018 7/3/18 12:59 AM
 **/
public class CommandLine {
    
    public static class Args {
        String flag, opt;
    public Args(String flag, String opt) { this.flag = flag.toLowerCase(); this.opt = opt; }

        @Override
        public String toString() {
            return "Option{" +
                    "flag='" + flag + '\'' +
                    ", opt='" + opt + '\'' +
                    '}';
        }
    }
    
    public static List<Args> args = new ArrayList<>();
    public static List<String> argzList = new ArrayList<String>();
    public static List<String> doubleOptsList = new ArrayList<String>();
    
    public static void populateArgs(String... argz){
        for (int i = 0; i < argz.length; i++) {
            switch (argz[i].charAt(0)) {
                case '-':
                    if (argz[i].length() < 2)
                        throw new IllegalArgumentException("Not a valid argument: "+argz[i]);
                    if (argz[i].charAt(1) == '-') {
                        if (argz[i].length() < 3)
                            throw new IllegalArgumentException("Not a valid argument: "+argz[i]);
                        // --opt
                        doubleOptsList.add(argz[i].substring(2, argz[i].length()));
                    } else {
                        if (argz.length-1 == i)
                            throw new IllegalArgumentException("Expected arg after: "+argz[i]);
                        // -opt
                        args.add(new Args(argz[i], argz[i+1]));
                        i++;
                    }
                    break;
                default:
                    // arg
                    argzList.add(argz[i]);
                    break;
            }
        }
    }
    
    public static String getArg(String flag){
        for (Args arg : args){
            if(arg.flag.equals(flag.toLowerCase()))
                return arg.opt;
        }
        return null;
    }
    
    public static void addArg(Args arg){
        args.add(arg);
    }

    public static boolean seedDatabase(){
        return true;
    }
    
}
