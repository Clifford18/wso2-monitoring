package ke.co.skyworld.repository;

import ke.co.skyworld.domain.enums.PortalTypes;
import ke.co.skyworld.repository.beans.FlexicoreArrayList;
import ke.co.skyworld.repository.beans.FlexicoreHashMap;
import ke.co.skyworld.repository.beans.TransactionWrapper;
import ke.co.skyworld.repository.query.FilterPredicate;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.data_formatting.XmlObject;
import ke.co.skyworld.utils.logging.Log;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * skyworld-api (ke.co.skyworld.repository)
 * Created by: elon
 * On: 16 Jan, 2021. 18:14
 **/
public class SystemSettingsRepository {

    public static FlexicoreHashMap getGlobalPasswordPolicyRegex() throws Exception {

        FilterPredicate filterPredicate = new FilterPredicate(
                "system_settings_type = :system_settings_type AND system_settings_scope = :system_settings_scope");
        FlexicoreHashMap queryVariables = new FlexicoreHashMap();
        queryVariables.addQueryArgument(":system_settings_type", Constants.SST_PASSWORD_POLICY);

        TransactionWrapper wrapper = Repository.selectWhere(Constants.Table.SYSTEM_SETTINGS, "system_settings_xml",
                filterPredicate, queryVariables);

        if(wrapper.hasErrors()){
            throw new Exception("Unable to determine GLOBAL password policy. ("+wrapper.getErrors()+")");
        }

        if(wrapper.getSingleRecord() == null){
            throw new Exception("Unable to determine GLOBAL password policy.");
        }

        String passwordPolicyXml = wrapper.getSingleRecord().getValue("system_settings_xml");

        if(passwordPolicyXml == null || passwordPolicyXml.isEmpty()){
            throw new Exception("Unable to determine GLOBAL password policy");
        }

        XmlObject passwordPolicyXmlObject = new XmlObject(passwordPolicyXml, Constants.PASSWORD_POLICY_XSD);
        if(passwordPolicyXmlObject.isValid()){
            FlexicoreHashMap passwordPolicy = new FlexicoreHashMap();
            passwordPolicy.putValue("password_policy_regex", passwordPolicyXmlObject.read("/PASSWORD_POLICY/REGEX"));
            passwordPolicy.putValue("password_policy_description", passwordPolicyXmlObject.read("/PASSWORD_POLICY/DESCRIPTION"));
            return passwordPolicy;
        } else {
            throw new Exception("Invalid GLOBAL password policy XML. ("+passwordPolicyXmlObject.getXmlValidationError()+")");
        }
    }

    public static FlexicoreHashMap getPortalTypePasswordPolicyRegex() throws Exception {

        FilterPredicate filterPredicate = new FilterPredicate(
                "system_settings_type = :system_settings_type");
        FlexicoreHashMap queryVariables = new FlexicoreHashMap();
        queryVariables.addQueryArgument(":system_settings_type", Constants.SST_PASSWORD_POLICY);

        TransactionWrapper wrapper = Repository.selectWhere(Constants.Table.SYSTEM_SETTINGS, "system_settings_xml",
                filterPredicate, queryVariables);

        if(wrapper.hasErrors()){
            throw new Exception("Unable to determine password policy. ("+wrapper.getErrors()+")");
        }

        if(wrapper.getSingleRecord() == null){
            throw new Exception("Unable to determine password policy.");
        }

        String passwordPolicyXml = wrapper.getSingleRecord().getValue("system_settings_xml");

        if(passwordPolicyXml == null || passwordPolicyXml.isEmpty()){
            throw new Exception("Unable to determine password policy");
        }

        XmlObject passwordPolicyXmlObject = new XmlObject(passwordPolicyXml, Constants.PASSWORD_POLICY_XSD);
        if(passwordPolicyXmlObject.isValid()){
            FlexicoreHashMap passwordPolicy = new FlexicoreHashMap();
            passwordPolicy.putValue("password_policy_regex", passwordPolicyXmlObject.read("/PASSWORD_POLICY/REGEX"));
            passwordPolicy.putValue("password_policy_description", passwordPolicyXmlObject.read("/PASSWORD_POLICY/DESCRIPTION"));
            return passwordPolicy;
        } else {
            throw new Exception("Invalid password policy XML. ("+passwordPolicyXmlObject.getXmlValidationError()+")");
        }
    }

    public static FlexicoreHashMap getPasswordPolicyRegex() throws Exception {
        FlexicoreHashMap passwordPolicy = null;
        try {
            passwordPolicy = getPortalTypePasswordPolicyRegex();
            return passwordPolicy;
        } catch (Exception e){
            Log.error(SystemSettingsRepository.class, "getPasswordPolicy", "Error: "+e.getMessage()+". Trying to get Global Password Policy...");
            try {
                passwordPolicy = getGlobalPasswordPolicyRegex();
                return passwordPolicy;
            } catch (Exception ex){
                throw new Exception(ex);
            }
        }
    }


    public static XmlObject getGlobalCreditTopUpNotificationXmlObject() throws Exception {

        FilterPredicate filterPredicate = new FilterPredicate(
                "system_settings_type = :system_settings_type AND system_settings_scope = :system_settings_scope " +
                        "AND system_settings_identifier = :system_settings_identifier");
        FlexicoreHashMap queryVariables = new FlexicoreHashMap();
        queryVariables.addQueryArgument(":system_settings_type", Constants.SST_CREDIT_TOP_UP_NOTIFICATION);
        queryVariables.addQueryArgument(":system_settings_scope", Constants.SSS_GLOBAL);
        queryVariables.addQueryArgument(":system_settings_identifier", "0");

        TransactionWrapper wrapper = Repository.selectWhere(Constants.Table.SYSTEM_SETTINGS, "system_settings_xml",
                filterPredicate, queryVariables);

        if(wrapper.hasErrors()){
            throw new Exception("Unable to determine GLOBAL password policy. ("+wrapper.getErrors()+")");
        }

        if(wrapper.getSingleRecord() == null){
            throw new Exception("Unable to determine GLOBAL password policy.");
        }

        String creditTopUpNotificationXml = wrapper.getSingleRecord().getValue("system_settings_xml");

        if(creditTopUpNotificationXml == null || creditTopUpNotificationXml.isEmpty()){
            throw new Exception("Unable to determine GLOBAL Credit Top Up Notification XML");
        }

        //TODO: Create XSD to validate Credit TopUp Notification XML
        XmlObject creditTopUpNotificationXmlObject = new XmlObject(creditTopUpNotificationXml);
        /*if(creditTopUpNotificationXmlObject.isValid()){
            return creditTopUpNotificationXmlObject;
        } else {
            throw new Exception("Invalid GLOBAL password Credit Top Up Notification XML. ("+creditTopUpNotificationXmlObject.getXmlValidationError()+")");
        }*/
        return creditTopUpNotificationXmlObject;
    }

    public static XmlObject getPortalTypeCreditTopUpNotificationXmlObject(PortalTypes portal, String portalTypeId) throws Exception {

        FilterPredicate filterPredicate = new FilterPredicate(
                "system_settings_type = :system_settings_type AND system_settings_scope = :system_settings_scope " +
                        "AND system_settings_identifier = :system_settings_identifier");
        FlexicoreHashMap queryVariables = new FlexicoreHashMap();
        queryVariables.addQueryArgument(":system_settings_type", Constants.SST_CREDIT_TOP_UP_NOTIFICATION);
        queryVariables.addQueryArgument(":system_settings_scope", portal.value());
        queryVariables.addQueryArgument(":system_settings_identifier", portalTypeId);

        TransactionWrapper wrapper = Repository.selectWhere(Constants.Table.SYSTEM_SETTINGS, "system_settings_xml",
                filterPredicate, queryVariables);

        if(wrapper.hasErrors()){
            throw new Exception("Unable to determine "+portal.value()+" Credit Top Up Notification XML. ("+wrapper.getErrors()+")");
        }

        if(wrapper.getSingleRecord() == null){
            return null;
        }

        String creditTopUpNotificationXml = wrapper.getSingleRecord().getValue("system_settings_xml");

        if(creditTopUpNotificationXml == null || creditTopUpNotificationXml.isEmpty()){
            return null;
        }

        //TODO: Create XSD to validate Credit TopUp Notification XML
        XmlObject creditTopUpNotificationXmlObject = new XmlObject(creditTopUpNotificationXml);
        /*if(creditTopUpNotificationXmlObject.isValid()){
            return creditTopUpNotificationXmlObject;
        } else {
            throw new Exception("Invalid GLOBAL password Credit Top Up Notification XML. ("+creditTopUpNotificationXmlObject.getXmlValidationError()+")");
        }*/
        return creditTopUpNotificationXmlObject;
    }

    public static XmlObject getCreditTopUpNotificationXmlObject(PortalTypes portal, String portalTypeId) throws Exception {
        XmlObject creditTopUpNotificationXmlObject;
        try {
            creditTopUpNotificationXmlObject = getPortalTypeCreditTopUpNotificationXmlObject(portal, portalTypeId);
            return creditTopUpNotificationXmlObject;
        } catch (Exception e){
            Log.error(SystemSettingsRepository.class, "getCreditTopUpNotificationXML", "Error: "+e.getMessage()+". Trying to get Global Credit Top Up Notification XML...");
            try {
                creditTopUpNotificationXmlObject = getGlobalCreditTopUpNotificationXmlObject();
                return creditTopUpNotificationXmlObject;
            } catch (Exception ex){
                throw new Exception(ex);
            }
        }
    }

    public static FlexicoreArrayList getGlobalMFAModes() throws Exception {

        FilterPredicate filterPredicate = new FilterPredicate(
                "system_settings_type = :system_settings_type AND system_settings_scope = :system_settings_scope");
        FlexicoreHashMap queryVariables = new FlexicoreHashMap();
        queryVariables.addQueryArgument(":system_settings_type", Constants.SST_MFA_MODES);
        queryVariables.addQueryArgument(":system_settings_scope", Constants.SSS_GLOBAL);

        TransactionWrapper wrapper = Repository.selectWhere(Constants.Table.SYSTEM_SETTINGS, "system_settings_xml",
                filterPredicate, queryVariables);

        if(wrapper.hasErrors()){
            throw new Exception("Unable to determine GLOBAL MFA Modes. ("+wrapper.getErrors()+")");
        }

        if(wrapper.getSingleRecord() == null){
            throw new Exception("Unable to determine GLOBAL MFA Modes.");
        }

        String mfaModesXML = wrapper.getSingleRecord().getValue("system_settings_xml");

        if(mfaModesXML == null || mfaModesXML.isEmpty()){
            throw new Exception("Unable to determine GLOBAL MFA Modes.");
        }

        XmlObject mfaModesXMLObject = new XmlObject(mfaModesXML);
        if(mfaModesXMLObject.isValid()){
            FlexicoreArrayList mfaModes = new FlexicoreArrayList();
            FlexicoreHashMap mfaMode;

            NodeList mfaModesNodeList = mfaModesXMLObject.getSelfElementByName("/MFA/MODES/MODE");
            int mfaModesSize = mfaModesNodeList.getLength();
            for (int i = 0; i < mfaModesSize; i++) {
                if(mfaModesNodeList.item(i).getNodeType() != Node.ELEMENT_NODE){
                    continue;
                }

                mfaMode = new FlexicoreHashMap();
                String mfaModeName = mfaModesNodeList.item(i).getAttributes().getNamedItem("NAME").getTextContent();
                mfaMode.putValue("name", mfaModeName);
                mfaMode.putValue("status", mfaModesNodeList.item(i).getAttributes().getNamedItem("STATUS").getTextContent());

                if(mfaModeName.equals("OTP")){
                    NodeList otpNodeList = mfaModesNodeList.item(i).getChildNodes();
                    int otpNodeSize = otpNodeList.getLength();
                    for (int j = 0; j < otpNodeSize; j++) {
                        if(otpNodeList.item(j).getNodeType() != Node.ELEMENT_NODE){
                            continue;
                        }
                        if(otpNodeList.item(j).getNodeName().equals("OTP")){
                            mfaMode.putValue("otp_length", otpNodeList.item(j).getAttributes().getNamedItem("LENGTH").getTextContent());
                            mfaMode.putValue("otp_ttl", otpNodeList.item(j).getAttributes().getNamedItem("TTL").getTextContent());
                            mfaMode.putValue("otp_message", otpNodeList.item(j).getTextContent());
                            break;
                        }
                    }
                }
                mfaModes.add(mfaMode);
            }
            return mfaModes;
        } else {
            throw new Exception("Invalid GLOBAL MFA Modes XML. ("+mfaModesXMLObject.getXmlValidationError()+")");
        }
    }

    public static FlexicoreArrayList getMFAModes(PortalTypes portal, String portalTypeId) throws Exception {

        FilterPredicate filterPredicate = new FilterPredicate(
                "system_settings_identifier = :system_settings_identifier AND system_settings_type = :system_settings_type " +
                        "AND system_settings_scope = :system_settings_scope");
        FlexicoreHashMap queryVariables = new FlexicoreHashMap();
        queryVariables.addQueryArgument(":system_settings_identifier", portalTypeId);
        queryVariables.addQueryArgument(":system_settings_type", Constants.SST_MFA_MODES);
        queryVariables.addQueryArgument(":system_settings_scope", portal.value());

        TransactionWrapper wrapper = Repository.selectWhere(Constants.Table.SYSTEM_SETTINGS, "system_settings_xml",
                filterPredicate, queryVariables);

        if(wrapper.hasErrors()){
            throw new Exception("Unable to determine PORTAL Type ID MFA Modes. ("+wrapper.getErrors()+")");
        }

        if(wrapper.getSingleRecord() == null){
            return new FlexicoreArrayList();
        }

        String mfaModesXML = wrapper.getSingleRecord().getValue("system_settings_xml");

        if(mfaModesXML == null || mfaModesXML.isEmpty()){
            return new FlexicoreArrayList();
        }

        XmlObject mfaModesXMLObject = new XmlObject(mfaModesXML);
        if(mfaModesXMLObject.isValid()){
            FlexicoreArrayList mfaModes = new FlexicoreArrayList();
            FlexicoreHashMap mfaMode;

            NodeList mfaModesNodeList = mfaModesXMLObject.getSelfElementByName("/MFA/MODES/MODE");
            int mfaModesSize = mfaModesNodeList.getLength();
            for (int i = 0; i < mfaModesSize; i++) {
                if(mfaModesNodeList.item(i).getNodeType() != Node.ELEMENT_NODE){
                    continue;
                }

                mfaMode = new FlexicoreHashMap();
                String mfaModeName = mfaModesNodeList.item(i).getAttributes().getNamedItem("NAME").getTextContent();
                mfaMode.putValue("name", mfaModeName);
                mfaMode.putValue("status", mfaModesNodeList.item(i).getAttributes().getNamedItem("STATUS").getTextContent());

                if(mfaModeName.equals("OTP")){
                    NodeList otpNodeList = mfaModesNodeList.item(i).getChildNodes();
                    int otpNodeSize = otpNodeList.getLength();
                    for (int j = 0; j < otpNodeSize; j++) {
                        if(otpNodeList.item(j).getNodeType() != Node.ELEMENT_NODE){
                            continue;
                        }
                        if(otpNodeList.item(j).getNodeName().equals("OTP")){
                            mfaMode.putValue("otp_length", otpNodeList.item(j).getAttributes().getNamedItem("LENGTH").getTextContent());
                            mfaMode.putValue("otp_ttl", otpNodeList.item(j).getAttributes().getNamedItem("TTL").getTextContent());
                            mfaMode.putValue("otp_message", otpNodeList.item(j).getTextContent());
                            break;
                        }
                    }
                }
                mfaModes.add(mfaMode);
            }
            return mfaModes;
        } else {
            throw new Exception("Invalid PORTAL Type ID MFA Modes XML. ("+mfaModesXMLObject.getXmlValidationError()+")");
        }
    }

    public static FlexicoreArrayList getActiveMFAModes(PortalTypes portal, String portalTypeId) throws Exception {
        FlexicoreArrayList mfaModes = getMFAModes(portal, portalTypeId);
        if(mfaModes.isEmpty()) mfaModes = getGlobalMFAModes();

        mfaModes.removeIf(flexicoreHashMap -> flexicoreHashMap.getStringValue("status").equals(Constants.STATUS_INACTIVE));
        return mfaModes;
    }

    public static FlexicoreHashMap getOTPParameters(PortalTypes portal, String portalTypeId) throws Exception {
        FlexicoreArrayList mfaModes = getMFAModes(portal, portalTypeId);
        if(mfaModes.isEmpty()) mfaModes = getGlobalMFAModes();
        for (FlexicoreHashMap mfaMode : mfaModes) {
            if(mfaMode.getStringValue("name").equals("OTP")) return mfaMode;
        }
        return new FlexicoreHashMap();
    }

    public static boolean OTPEnabled(PortalTypes portal, String portalTypeId) throws Exception {
        return getOTPParameters(portal, portalTypeId).getStringValue("status").equals("ACTIVE");
    }

    public static FlexicoreHashMap getSystemMSGLength() throws Exception {
        FlexicoreHashMap systemMSGLength = new FlexicoreHashMap();

        XmlObject systemMSGLengthXMLObject = getSystemMSGParametersXMLObject();

        if(systemMSGLengthXMLObject != null){
            systemMSGLength.putValue("min_system_msg_length", systemMSGLengthXMLObject.read("/PARAMETERS/MSG/LENGTH/@MIN"));
            systemMSGLength.putValue("max_system_msg_length", systemMSGLengthXMLObject.read("/PARAMETERS/MSG/LENGTH/@MAX"));
        } else {
            throw new Exception("Error getting System MSG Length XML!");
        }

        return systemMSGLength;
    }

    public static XmlObject getSystemMSGParametersXMLObject() throws Exception {

        FilterPredicate filterPredicate = new FilterPredicate(
                "system_settings_type = :system_settings_type AND system_settings_scope = :system_settings_scope " +
                        "AND system_settings_identifier = :system_settings_identifier");
        FlexicoreHashMap queryVariables = new FlexicoreHashMap();
        queryVariables.addQueryArgument(":system_settings_type", Constants.SST_SYSTEM_MSG_PARAMETERS);
        queryVariables.addQueryArgument(":system_settings_scope", Constants.SSS_GLOBAL);
        queryVariables.addQueryArgument(":system_settings_identifier", "0");

        TransactionWrapper wrapper = Repository.selectWhere(Constants.Table.SYSTEM_SETTINGS, "system_settings_xml",
                filterPredicate, queryVariables);

        if(wrapper.hasErrors()){
            throw new Exception("Unable to determine System MSG Length XML. ("+wrapper.getErrors()+")");
        }

        if(wrapper.getSingleRecord() == null){
            return null;
        }

        String systemMSGLengthXML = wrapper.getSingleRecord().getValue("system_settings_xml");

        if(systemMSGLengthXML == null || systemMSGLengthXML.isEmpty()){
            return null;
        }

        return new XmlObject(systemMSGLengthXML);
    }

    public static XmlObject getSystemSettingXml(String systemSettingScope, String systemSettingIdentifier, String systemSettingType) throws Exception {

        FilterPredicate filterPredicate = new FilterPredicate(
                "system_settings_identifier = :system_settings_identifier AND system_settings_type = :system_settings_type AND " +
                        "system_settings_scope = :system_settings_scope");
        FlexicoreHashMap queryVariables = new FlexicoreHashMap();
        queryVariables.addQueryArgument(":system_settings_identifier", systemSettingIdentifier);
        queryVariables.addQueryArgument(":system_settings_type", systemSettingType);
        queryVariables.addQueryArgument(":system_settings_scope", systemSettingScope);

        TransactionWrapper wrapper = Repository.selectWhere(Constants.Table.SYSTEM_SETTINGS, "system_settings_xml",
                filterPredicate, queryVariables);

        if(wrapper.hasErrors()){
            throw new Exception("Unable to determine "+systemSettingScope+" "+systemSettingType+". ("+wrapper.getErrors()+")");
        }

        if(wrapper.getSingleRecord() == null){
            throw new Exception("Unable to determine "+systemSettingScope+" "+systemSettingType+".");
        }

        String systemSettingXML = wrapper.getSingleRecord().getValue("system_settings_xml");

        if(systemSettingXML == null || systemSettingXML.isEmpty()){
            throw new Exception("Unable to determine "+systemSettingScope+" "+systemSettingType);
        }

        XmlObject systemSettingXMLObject = new XmlObject(systemSettingXML);
        if(systemSettingXMLObject.isValid()){
            return systemSettingXMLObject;
        } else {
            throw new Exception("Invalid "+systemSettingScope+" "+systemSettingType+" XML. ("+systemSettingXMLObject.getXmlValidationError()+")");
        }
    }

}
