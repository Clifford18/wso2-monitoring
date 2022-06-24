package ke.co.skyworld.utils.budgeting;

import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.data_formatting.XmlUtils;
import ke.co.skyworld.utils.logging.Log;

@SuppressWarnings("Duplicates")
public class CostCalculator {

    /**
     * @param message
     * @return
     */

    //FIXME: Get Actual Cost
    public static double calculateCost(String message){
        long characters = message.length();
        int singleSMSLength = Constants.readIntFromConf(Constants.XML_PATH_TO_SMS_LENGTH_SINGLE);
        int concatenatedSMSLength = Constants.readIntFromConf(Constants.XML_PATH_TO_SMS_LENGTH_CONCATENATED);
        double costPerSMS = 1.00;

        try {
            costPerSMS = Constants.readDoubleFromConf(Constants.XML_PATH_TO_SMS_COST_PER_SMS);
        } catch (NumberFormatException e){
            Log.debug(CostCalculator.class, "calculateCost", "Cought number format exception.");
        }

        if(characters <= singleSMSLength){
            return costPerSMS;
        }else{
            int smsCount = (int)characters/concatenatedSMSLength;
            if(characters%concatenatedSMSLength > 0){
                smsCount+=1;
            }
            return smsCount*costPerSMS;
        }
    }
}
