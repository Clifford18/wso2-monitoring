package ke.co.skyworld.domain.enums;

/**
 * kukuza-api (ke.co.scedar.domain.enums)
 * Created by: elon
 * On: 16 Aug, 2018 8/16/18 12:50 PM
 **/
public enum FileWriters {

    FileWriter("fw"),
    BufferedWriter("bw");

    private String value;

    FileWriters(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
