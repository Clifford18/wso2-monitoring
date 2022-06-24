package ke.co.skyworld.utils;

import java.text.DecimalFormat;

public class ProgressIndicator {

    private long count;
    private long currentPosition;
    private char octothorp;
    private int progressIndicatorLength;
    private double percentage;
    private StringBuilder octothorps = new StringBuilder();
    private int difference;
    private DecimalFormat df;
    private long startTime;
    private long endTime;
    private long t1;
    private long t2;
    private double avgTimeTakenToDoUnitWork = 0d;
    private boolean printProgress;
    private String progress;
    private String strEta;
    private long eta;
    private String strElapsedTime;
    private long elapsedTime;

    public ProgressIndicator(){}

    public ProgressIndicator(long count) {
        this.count = count;
        this.octothorp = '#';
        this.progressIndicatorLength = 50;
        this.df = new DecimalFormat("0.00");
        this.printProgress = true;
    }

    public ProgressIndicator(long count, char octothorp, int progressIndicatorLength) {
        this.count = count;
        this.octothorp = octothorp;
        this.progressIndicatorLength = progressIndicatorLength;
        this.df = new DecimalFormat("0.00");
        this.printProgress = true;
    }

    public ProgressIndicator(long count, char octothorp, int progressIndicatorLength, String decimalFormat) {
        this.count = count;
        this.octothorp = octothorp;
        this.progressIndicatorLength = progressIndicatorLength;
        this.df = new DecimalFormat(decimalFormat);
        this.printProgress = true;
    }

    public ProgressIndicator(long count, char octothorp, int progressIndicatorLength, String decimalFormat,
                             boolean printProgress) {
        this.count = count;
        this.octothorp = octothorp;
        this.progressIndicatorLength = progressIndicatorLength;
        this.df = new DecimalFormat(decimalFormat);
        this.printProgress = printProgress;
    }

    public void indicateProgress(long currentPosition){
        this.currentPosition = currentPosition;
        percentage = (double)currentPosition/count;

        t2 = System.currentTimeMillis();

        double timeTakenToDoUnitWork = t2-t1;
        if(timeTakenToDoUnitWork == 0) timeTakenToDoUnitWork = 0.055;
        //avgTimeTakenToDoUnitWork = ((avgTimeTakenToDoUnitWork+timeTakenToDoUnitWork)/currentPosition);
        avgTimeTakenToDoUnitWork = timeTakenToDoUnitWork;
        //set 1 ms if system gives 0
        long remainingWork = count-currentPosition; //currentPosition => work done;
        long eta = (long) (remainingWork * avgTimeTakenToDoUnitWork);

        //How many octothorps to print
        octothorps.setLength(0);
        for (int i = 0; i < (int)(progressIndicatorLength*percentage) ; i++) {
            octothorps.append(octothorp);
        }

        difference = progressIndicatorLength - octothorps.toString().length();

        //Add spaces
        for (int i = 0; i < difference ; i++) {
            octothorps.append(" ");
        }

        progress = "["+octothorps+"] "+df.format(percentage*100) +" % ETA "+prettyTimeDifference(eta);
        this.strEta = prettyTimeDifference(eta);
        this.eta = eta;
        this.elapsedTime = System.currentTimeMillis() - startTime;
        this.strElapsedTime = prettyTimeDifference((System.currentTimeMillis() - startTime));
        if(printProgress)
            System.out.print(progress+"\r");

        t1 = System.currentTimeMillis();

        if(percentage == 1) end();
    }

    public void start(){
        startTime = System.currentTimeMillis();
        t1 = startTime;
    }

    private void end(){
        endTime = System.currentTimeMillis();
        System.out.println("\nTime taken: "+prettyTimeDifference(endTime-startTime));
    }

    private String prettyTimeDifference(long difference){
        String seconds = "second";
        String minutes = "minute";
        String hours = "hour";
        String days = "day";

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = difference / daysInMilli;
        difference = difference % daysInMilli;

        long elapsedHours = difference / hoursInMilli;
        difference = difference % hoursInMilli;

        long elapsedMinutes = difference / minutesInMilli;
        difference = difference % minutesInMilli;

        long elapsedSeconds = difference / secondsInMilli;

        if (elapsedSeconds>1) seconds = seconds+"s";
        if (elapsedMinutes>1) minutes = minutes+"s";
        if (elapsedHours>1) hours = hours+"s";
        if (elapsedDays>1) days = days+"s";

        if (elapsedDays<=0){
            if(elapsedHours<=0){
                if(elapsedMinutes<=0){
                    if(elapsedSeconds<=0){
                        return "0 seconds";
                    }else{
                        return String.format("%d "+seconds, elapsedSeconds);
                    }
                }else{
                    if(elapsedSeconds>0){
                        return String.format("%d "+minutes+", %d "+seconds,
                                elapsedMinutes, elapsedSeconds);
                    }else{
                        return String.format("%d "+minutes, elapsedMinutes);
                    }
                }
            }else{
                if(elapsedMinutes>0){
                    return String.format("%d "+hours+", %d "+minutes,
                            elapsedHours, elapsedMinutes);
                }else{
                    return String.format("%d "+hours, elapsedHours);
                }
            }
        }else{
            if(elapsedHours>0){
                try {
                    return String.format("%d "+days+", %d "+hours,
                            elapsedDays, elapsedHours);
                } catch (Exception e){
                    e.printStackTrace();
                    return "Error";
                }
            }else{
                try {
                    return String.format("%d "+days, elapsedDays);
                } catch (Exception e){
                    e.printStackTrace();
                    return "Error";
                }
            }
        }
    }

    public void gc(){
        octothorps = null;
        df = null;
    }

    public long getCount() {
        return count;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public char getOctothorp() {
        return octothorp;
    }

    public int getProgressIndicatorLength() {
        return progressIndicatorLength;
    }

    public double getPercentage() {
        return percentage*100;
    }

    public StringBuilder getOctothorps() {
        return octothorps;
    }

    public int getDifference() {
        return difference;
    }

    public DecimalFormat getDf() {
        return df;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public double getAvgTimeTakenToDoUnitWork() {
        return avgTimeTakenToDoUnitWork;
    }

    public boolean isPrintProgress() {
        return printProgress;
    }

    public String getProgress() {
        return progress;
    }

    public String getStrEta() {
        return strEta;
    }

    public long getEta() {
        return eta;
    }

    public String getStrElapsedTime() {
        return strElapsedTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setOctothorp(char octothorp) {
        this.octothorp = octothorp;
    }

    public void setProgressIndicatorLength(int progressIndicatorLength) {
        this.progressIndicatorLength = progressIndicatorLength;
    }

    public void setDf(DecimalFormat df) {
        this.df = df;
    }

    public void setPrintProgress(boolean printProgress) {
        this.printProgress = printProgress;
    }
}