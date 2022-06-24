package ke.co.skyworld.domain.beans;

import ke.co.skyworld.utils.Misc;
import ke.co.skyworld.utils.data_formatting.XmlObject;
import ke.co.skyworld.utils.data_formatting.XmlUtils;
import ke.co.skyworld.utils.memory.JvmManager;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;

public class WorkflowPayload {

    private long record_id;
    private long portal_type_id;
    private int app_code;
    private String record_content;
    private int approval_level;
    private String review_request_approval_level;
    private String approval_status;
    private String workflow_title;
    private String workflow_status;
    private String user_comment;
    private Document xml_doc;
    private XmlObject xml_object;

    public WorkflowPayload() {
    }

    public long getRecord_id() {
        return record_id;
    }

    public void setRecord_id(long record_id) {
        this.record_id = record_id;
    }

    public long getPortal_type_id() {
        return portal_type_id;
    }

    public void setPortal_type_id(long portal_type_id) {
        this.portal_type_id = portal_type_id;
    }

    public int getApp_code() {
        return app_code;
    }

    public void setApp_code(int app_code) {
        this.app_code = app_code;
    }

    public String getRecord_content() {
        return (record_content != null) ? Misc.removeNonUTF8Characters(record_content) : null;
    }

    public void setRecord_content(String record_content) {
        this.record_content = (record_content != null) ? Misc.removeNonUTF8Characters(record_content) : null;
    }

    public int getApproval_level() {
        return approval_level;
    }

    public void setApproval_level(int approval_level) {
        this.approval_level = approval_level;
    }

    public String getApproval_status() {
        return approval_status;
    }

    public void setApproval_status(String approval_status) {
        this.approval_status = approval_status;
    }

    public String getWorkflow_title() {
        return workflow_title;
    }

    public void setWorkflow_title(String workflow_title) {
        this.workflow_title = workflow_title;
    }

    public String getWorkflow_status() {
        return workflow_status;
    }

    public void setWorkflow_status(String workflow_status) {
        this.workflow_status = workflow_status;
    }

    public String getUser_comment() {
        return user_comment;
    }

    public void setUser_comment(String user_comment) {
        this.user_comment = user_comment;
    }

    public String getReview_request_approval_level() {
        return review_request_approval_level;
    }

    public void setReview_request_approval_level(String review_request_approval_level) {
        this.review_request_approval_level = review_request_approval_level;
    }

    public void resetRecordContent() {
        this.xml_doc = null;
        this.xml_object = null;
    }


    public boolean editXmlTagValue(String tagPath, String value){
        xml_doc = getXml_doc();
        if(xml_doc != null){
            xml_doc = XmlUtils.updateXMLTag(xml_doc, tagPath, value);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer;
            try {
                transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                StringWriter writer = new StringWriter();
                transformer.transform(new DOMSource(xml_doc), new StreamResult(writer));
                this.record_content = writer.getBuffer().toString()
                        .replaceAll("[\n\r]", "");
                JvmManager.gc(tf, transformer, writer);
                return true;
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getTagValue(String tagPath){
        xml_doc = getXml_doc();
        if(xml_doc != null){
            try{
                return XmlUtils.readXMLTag(xml_doc, tagPath);
            }catch (Exception e){
                e.printStackTrace();
                System.err.println(e.toString());
                return "";
            }
        }
        return "";
    }

    public String getXmlContentAsString(String tagPath){
        xml_doc = getXml_doc();
        NodeList nodeList = null;

        try {
            nodeList = (NodeList) XPathFactory.newInstance().newXPath().compile(tagPath)
                    .evaluate(xml_doc, XPathConstants.NODESET);

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        Transformer transformer = null;
        StreamResult result = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(Objects.requireNonNull(nodeList).item(0));
            transformer.transform(source, result);

            JvmManager.gc(nodeList, xml_doc, transformer);

            return result.getWriter().toString();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        JvmManager.gc(nodeList, xml_doc, transformer, result);
        return "";
    }

    public String getInnerXmlContentAsString(String tagPath, String parentTag){
        String xmlContentAsString = getXmlContentAsString(tagPath);
        xmlContentAsString = xmlContentAsString.replaceAll(
                "<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", "");
        xmlContentAsString = xmlContentAsString.replaceAll("<"+parentTag+">", "");
        return xmlContentAsString.replaceAll("</"+parentTag+">", "").trim();
    }

    public void recreateXmlDoc(){
        this.xml_doc = null;
        this.xml_doc = getXml_doc();
    }

    public void recreateXmlDoc(String recordContent){
        setRecord_content(recordContent);
        recreateXmlDoc();
    }

    public Document getXml_doc(){
        if(xml_doc == null){
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
                JvmManager.gc(factory);
                return builder.parse(new InputSource(new StringReader(getRecord_content())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            JvmManager.gc(factory, builder);
            return null;
        }
        return xml_doc;
    }

    public XmlObject getXml_object(){
        if(xml_object == null) {
            xml_object = new XmlObject(getRecord_content());
        }
        return xml_object;
    }

    public XmlObject recordContent(){
        return getXml_object();
    }

    @Override
    public String toString() {
        return "WorkflowPayload{" +
                "recordId='" + record_id + '\'' +
                ", appCode='" + app_code + '\'' +
                ", recordContent='" + record_content + '\'' +
                ", approvalLevel='" + approval_level + '\'' +
                ", approvalStatus='" + approval_status + '\'' +
                ", workflowTitle='" + workflow_title + '\'' +
                ", workflowStatus='" + workflow_status + '\'' +
                ", userComment='" + user_comment + '\'' +
                ", reviewRequestApprovalLevel='" + review_request_approval_level + '\'' +
                '}';
    }
}
