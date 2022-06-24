package ke.co.skyworld.utils.data_formatting;

import ke.co.skyworld.Main;
import ke.co.skyworld.utils.Constants;
import ke.co.skyworld.utils.logging.Log;
import ke.co.skyworld.utils.memory.JvmManager;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.*;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Objects;

/**
 * sls-api (ke.co.scedar.utils.xml)
 * Created by: elon
 * On: 14 Sep, 2018 9/14/18 6:31 PM
 **/
public class XmlObject {

    private boolean isValid;
    private String xmlString;
    private String oldXmlString;
    private String xsdString;
    private String oldXsdString;
    private String xmlValidationError = "";
    private Document xmlDoc;
    private Document oldXmlDoc;

    public XmlObject() {

    }

    public XmlObject(String xmlString) {
        this.isValid = false;
        this.xmlString = xmlString;
        this.oldXmlString = xmlString;
        parseXml();
        this.oldXmlDoc = xmlDoc;
    }

    public XmlObject(String xmlString, String xsdString) {
        this.isValid = false;
        this.xmlString = xmlString;
        this.oldXmlString = xmlString;
        this.xsdString = xsdString;
        this.oldXsdString = xsdString;

        this.isValid = validate();
        if (this.isValid) {
            parseXml();
            this.oldXmlDoc = xmlDoc;
        }
    }

    public void reconstruct(String xmlString) {
        this.isValid = true;
        this.oldXmlString = this.xmlString;
        this.xmlString = xmlString;
        oldXmlDoc = xmlDoc;
        parseXml();
    }

    public void reconstruct(String xmlString, String xsdString) {
        this.isValid = false;
        this.oldXmlString = this.xmlString;
        this.oldXsdString = this.xsdString;
        this.xmlString = xmlString;
        this.xsdString = xsdString;
        oldXmlDoc = xmlDoc;

        this.isValid = validate();

        if (this.isValid) {
            parseXml();
        }
    }

    public void reCreateXmlDoc() {
        this.xmlDoc = null;
        parseXml();
    }

    public boolean isValid() {
        return isValid;
    }

    public String getXmlString() {
        return xmlString;
    }

    public String getOldXmlString() {
        return oldXmlString;
    }

    public String getXsdString() {
        return xsdString;
    }

    public String getOldXsdString() {
        return oldXsdString;
    }

    public String getXmlValidationError() {
        return xmlValidationError;
    }

    public Document getXmlDoc() {
        return xmlDoc;
    }

    public Document getOldXmlDoc() {
        return oldXmlDoc;
    }

    public boolean validate() {
        DocumentBuilderFactory documentBuilderFactory = null;
        DocumentBuilder parser = null;
        InputSource xmlSource = null;
        Document xmlDocument = null;
        Source schemaSource = null;
        Schema schema = null;
        Validator validator = null;
        boolean validXml = false;

        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(false);

            parser = documentBuilderFactory.newDocumentBuilder();
            parser.setEntityResolver((publicId, systemId) -> new InputSource(xsdString));

            xmlSource = new InputSource(new StringReader(xmlString));
            xmlDocument = parser.parse(xmlSource);

            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            schemaSource = new StreamSource(new StringReader(xsdString));
            schema = schemaFactory.newSchema(schemaSource);

            validator = schema.newValidator();
            validator.validate(new DOMSource(xmlDocument));
            validXml = true;

            JvmManager.gc(documentBuilderFactory, parser, xmlSource, xmlDocument, schemaSource, schema, validator);

        } catch (Exception e) {
            xmlValidationError = "XML not valid. Error: " + e.getMessage();
            Log.error(XmlObject.class, "validate", xmlValidationError);
        } finally {
            JvmManager.gc(documentBuilderFactory, parser, xmlSource, xmlDocument, schemaSource, schema, validator);
        }

        return validXml;
    }

    public boolean exists(String path) {
        boolean exists = false;
        if (xmlDoc == null) reCreateXmlDoc();

        XPathExpression xp;
        try {
            xp = XPathFactory.newInstance().newXPath().compile(path);
            NodeList nodes = (NodeList) xp.evaluate(xmlDoc, XPathConstants.NODESET);
            if (nodes != null && nodes.getLength() > 0) {
                exists = true;
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return exists;
    }

    public void update(String path, String value) {
        editXmlTagValue(path, value);
    }

    public void update(String path, String value, Integer... index) {
        path = String.format(path, (Object[]) index);
        editXmlTagValue(path, value);
    }

    public void editXmlTagValue(String tagPath, String value) {

        if (xmlDoc == null) reCreateXmlDoc();

        try {
            NodeList nodeList = (NodeList) XPathFactory.newInstance().newXPath().compile(tagPath)
                    .evaluate(xmlDoc, XPathConstants.NODESET);

            nodeList.item(0).setTextContent(value);

            updateXmlString();

        } catch (Exception e) {
            e.printStackTrace();
            Log.error(XmlObject.class, "editXmlTagValue",
                    "Error. Failed to editing XML object - " + e.getMessage());
        }
    }

    public String read(String path) {
        return getTagValue(path).trim();
    }

    public String getTagValue(String tagPath) {

        if (xmlDoc == null) reCreateXmlDoc();

        XPathExpression xp;
        try {
            xp = XPathFactory.newInstance().newXPath().compile(tagPath);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }

        try {
            return Objects.requireNonNull(xp).evaluate(xmlDoc);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTagValue(String tagPath, Integer... index) {
        tagPath = String.format(tagPath, (Object[]) index);
        return getTagValue(tagPath);
    }

    public String read(String tagPath, Integer... index) {
        tagPath = String.format(tagPath, (Object[]) index);
        return getTagValue(tagPath);
    }

    public String getXmlContentAsString(String tagPath) {
        return getXmlContentAsString(tagPath, false);
    }

    public String getXmlContentAsString(String tagPath, boolean stripXmlHeader) {
        if (xmlDoc == null) parseXml();
        NodeList nodeList = null;

        try {
            nodeList = (NodeList) XPathFactory.newInstance().newXPath().compile(tagPath)
                    .evaluate(xmlDoc, XPathConstants.NODESET);

        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        Transformer transformer = null;
        StreamResult result = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer(
                    new StreamSource(Main.class.getClassLoader().getResourceAsStream(Constants.REMOVE_BLANK_LINES_XSLT)));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(Objects.requireNonNull(nodeList).item(0));
            transformer.transform(source, result);

            JvmManager.gc(nodeList, xmlDoc, transformer);

            String xmlString = result.getWriter().toString();

            if (stripXmlHeader) {
                xmlString = xmlString.replaceAll(
                        "<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", "");
                xmlString = xmlString.replaceAll(
                        "<\\?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"\\?>", "");
                return xmlString;
            } else {
                return xmlString;
            }

        } catch (TransformerException e) {
            e.printStackTrace();
        }
        JvmManager.gc(nodeList, xmlDoc, transformer, result);
        return "";
    }

    public String getInnerXmlContentAsString(String tagPath, String parentTag) {
        String xmlContentAsString = getXmlContentAsString(tagPath);
        xmlContentAsString = xmlContentAsString.replaceAll(
                "<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", "");
        xmlContentAsString = xmlContentAsString.replaceAll("<" + parentTag + ">", "");
        return xmlContentAsString.replaceAll("</" + parentTag + ">", "").trim();
    }

    public int countTags(String tagPath) throws Exception {
        try {

            if (xmlDoc == null) reCreateXmlDoc();

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            XPathExpression expr = xpath.compile("count(/" + tagPath + ")");
            Number result = (Number) expr.evaluate(xmlDoc, XPathConstants.NUMBER);

            return result.intValue();

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public int countTags(String tagPath, Integer... index) throws Exception {
        tagPath = String.format(tagPath, (Object[]) index);
        return countTags(tagPath);
    }

    @Deprecated
    public int countInnerTags(String tagPath) throws Exception {
        try {

            if (xmlDoc == null) reCreateXmlDoc();

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            XPathExpression expr = xpath.compile("count(/" + tagPath + ")");
            Number result = (Number) expr.evaluate(xmlDoc, XPathConstants.NUMBER);

            return result.intValue();

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public void create(String rootElement) throws Exception {
        create(rootElement, null);
    }

    public void create(String rootElement, HashMap<String, String> attributes) throws Exception {

        DocumentBuilderFactory dbFactory = null;
        DocumentBuilder dBuilder = null;

        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            xmlDoc = dBuilder.newDocument();

            Element ROOT = xmlDoc.createElement(rootElement);
            xmlDoc.appendChild(ROOT);

            if (attributes != null && !attributes.isEmpty()) {
                for (String key : attributes.keySet()) {
                    Attr nameAttr = xmlDoc.createAttribute(key);
                    nameAttr.setValue(attributes.get(key));
                    ROOT.setAttributeNode(nameAttr);
                }
            }

            updateXmlString();

            JvmManager.gc(dbFactory, dBuilder);

        } catch (Exception e) {
            e.printStackTrace();
            Log.error(XmlObject.class, "create", "ERROR Creating XML Document: " + e.getMessage());
            throw new Exception(e);
        } finally {
            JvmManager.gc(dbFactory, dBuilder);
        }
    }

    public String addElement(String parentElement, String elementName, String value) throws Exception {
        return addElement(parentElement, elementName, value, null);
    }

    public String addElement(String parentElement, String elementName,
                             String value, HashMap<String, String> attributes) throws Exception {
        try {
            if (xmlDoc == null) reCreateXmlDoc();

            NodeList nodeList = getElementByName(xmlDoc, parentElement);
            if (nodeList.getLength() < 1) {
                throw new Exception("Parent element '" + parentElement + "' not found.");
            }

            Element ELEMENT = xmlDoc.createElement(elementName);
            ELEMENT.setTextContent(value);

            if (attributes != null && !attributes.isEmpty()) {
                for (String key : attributes.keySet()) {
                    Attr nameAttr = xmlDoc.createAttribute(key);
                    nameAttr.setValue(attributes.get(key));
                    ELEMENT.setAttributeNode(nameAttr);
                }
            }

            Element PARENT = (Element) nodeList.item(0);
            PARENT.appendChild(ELEMENT);

            updateXmlString();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR Adding Element to XML Document: " + e.getMessage());
            throw new Exception(e);
        }
        return getXmlContentAsString("/", true);
    }

    public String addNodeElement(String parentElement, String elementName, String value) throws Exception {
        return addNodeElement(parentElement, elementName, value, null);
    }

    public String addNodeElement(String parentElement, String elementName,
                                 String value, HashMap<String, String> attributes) throws Exception {
        try {

            if (xmlDoc == null) reCreateXmlDoc();

            NodeList nodeList = getElementByName(xmlDoc, parentElement);
            if (nodeList.getLength() < 1) {
                throw new Exception("Parent element '" + parentElement + "' not found.");
            }

            Element ELEMENT = xmlDoc.createElement(elementName);

            Document document = ELEMENT.getOwnerDocument();
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Node fragmentNode = documentBuilder.parse(new InputSource(new StringReader(value))).getDocumentElement();
            fragmentNode = document.importNode(fragmentNode, true);
            ELEMENT.appendChild(fragmentNode);

            if (attributes != null && !attributes.isEmpty()) {
                for (String key : attributes.keySet()) {
                    Attr nameAttr = xmlDoc.createAttribute(key);
                    nameAttr.setValue(attributes.get(key));
                    ELEMENT.setAttributeNode(nameAttr);
                }
            }

            Element PARENT = (Element) nodeList.item(0);
            PARENT.appendChild(ELEMENT);
            //xmlDoc.appendChild(ELEMENT);

            updateXmlString();
            JvmManager.gc(document, documentBuilder, fragmentNode);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR Adding Element to XML Document: " + e.getMessage());
            throw new Exception(e);
        }
        return getXmlContentAsString("/", true);
    }

    public void removeNodeElement(String elementName) throws Exception {
        try {

            if (xmlDoc == null) reCreateXmlDoc();

            NodeList nodeList = getElementByName(xmlDoc, elementName);
            if (nodeList.getLength() < 1) {
                //throw new Exception("Element '" + elementName + "' not found.");
                return;
            }

            Node nodeToRemove = nodeList.item(0);
            Node parentNode = nodeToRemove.getParentNode();
            if(parentNode != null) parentNode.removeChild(nodeToRemove);

            updateXmlString();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR Adding Element to XML Document: " + e.getMessage());
            throw new Exception(e);
        }
    }

    public void removeNodeElementLite(String elementName) throws Exception {
        try {

            if (xmlDoc == null) reCreateXmlDoc();

            NodeList nodeList = getElementByName(xmlDoc, elementName);
            if (nodeList.getLength() < 1) {
                //throw new Exception("Element '" + elementName + "' not found.");
                return;
            }

            for (int i = 0; i < nodeList.getLength(); i++) {
                if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Node nodeToRemove = nodeList.item(i);
                    Node parentNode = nodeToRemove.getParentNode();
                    if(parentNode != null) parentNode.removeChild(nodeToRemove);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR Adding Element to XML Document: " + e.getMessage());
            throw new Exception(e);
        }
    }

    public void removeNodeAttributeLite(String elementName, String attributeName) throws Exception {
        try {

            if (xmlDoc == null) reCreateXmlDoc();

            NodeList nodeList = getElementByName(xmlDoc, elementName);
            if (nodeList.getLength() < 1) {
                //throw new Exception("Element '" + elementName + "' not found.");
                return;
            }

            for (int i = 0; i < nodeList.getLength(); i++) {
                if(nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Node nodeWithAttribute = nodeList.item(i);
                    Node parentNode = nodeWithAttribute.getParentNode();
                    if(parentNode != null) {
                        ((Element) nodeWithAttribute).removeAttribute(attributeName);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR Adding Element to XML Document: " + e.getMessage());
            throw new Exception(e);
        }
    }

    /*public void removeNodeElement(Node node) throws Exception {
        try {

            if (xmlDoc == null) reCreateXmlDoc();

            NodeList nodeList = getElementByName(xmlDoc, elementName);
            if (nodeList.getLength() < 1) {
                //throw new Exception("Element '" + elementName + "' not found.");
                return;
            }

            Node nodeToRemove = nodeList.item(0);
            Node parentNode = nodeToRemove.getParentNode();
            if(parentNode != null) parentNode.removeChild(nodeToRemove);

            updateXmlString();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR Adding Element to XML Document: " + e.getMessage());
            throw new Exception(e);
        }
    }*/

    public static NodeList getElementByName(Document xmlDoc, String elementName) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList;

        try {
            nodeList = (NodeList) xPath.compile(elementName).evaluate(xmlDoc, XPathConstants.NODESET);

            JvmManager.gc(xPath);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR Getting element from XML Document: " + e.getMessage());
            throw new Exception(e);
        }

        return nodeList;
    }

    public NodeList getSelfElementByName(String elementName) throws Exception {
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList;

        try {
            nodeList = (NodeList) xPath.compile(elementName).evaluate(xmlDoc, XPathConstants.NODESET);

            JvmManager.gc(xPath);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR Getting element from XML Document: " + e.getMessage());
            throw new Exception(e);
        }

        return nodeList;
    }

    public void updateXmlString() {
        TransformerFactory transformerFactory = null;
        Transformer transformer = null;
        DOMSource source = null;
        Writer out = null;
        StreamResult streamResult = null;

        try {
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer(
                    new StreamSource(Main.class.getClassLoader().getResourceAsStream(Constants.REMOVE_BLANK_LINES_XSLT)));
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            source = new DOMSource(xmlDoc);
            out = new StringWriter();
            streamResult = new StreamResult(out);
            transformer.transform(source, streamResult);
            this.xmlString = out.toString();
            JvmManager.gc(transformerFactory, transformer, source, streamResult, out);
        } catch (TransformerException e) {
            Log.error(XmlObject.class, "save",
                    "Error. Failed to format XML - " + e.getMessageAndLocation());
        } finally {
            JvmManager.gc(transformerFactory, transformer, source, streamResult, out);
        }
    }

    public String format(boolean omitXmlDeclaration) {
        TransformerFactory transformerFactory = null;
        Transformer transformer = null;
        DOMSource source = null;
        Writer out = null;
        StreamResult streamResult = null;

        String formattedXml;
        try {
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer(
                    new StreamSource(Main.class.getClassLoader().getResourceAsStream(Constants.REMOVE_BLANK_LINES_XSLT)));
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, (omitXmlDeclaration) ? "yes" : "no");
            source = new DOMSource(xmlDoc);
            out = new StringWriter();
            streamResult = new StreamResult(out);
            transformer.transform(source, streamResult);
            formattedXml = out.toString();
            JvmManager.gc(transformerFactory, transformer, source, streamResult, out);
            return formattedXml;
        } catch (TransformerException e) {
            Log.error(XmlObject.class, "format",
                    "Error. Failed to format XML - " + e.getMessageAndLocation());
        } finally {
            JvmManager.gc(transformerFactory, transformer, source, streamResult, out);
        }
        return "";
    }

    public void save(String filePath, boolean omitXmlDeclaration) throws Exception {
        TransformerFactory transformerFactory = null;
        Transformer transformer = null;
        DOMSource source = null;
        StreamResult streamResult = null;
        try {
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer(
                    new StreamSource(Main.class.getClassLoader().getResourceAsStream(Constants.REMOVE_BLANK_LINES_XSLT)));
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, (omitXmlDeclaration) ? "yes" : "no");
            source = new DOMSource(xmlDoc);
            streamResult = new StreamResult(new File(filePath));
            transformer.transform(source, streamResult);
            JvmManager.gc(transformerFactory, transformer, source, streamResult);
        } catch (TransformerException e) {
            Log.error(XmlObject.class, "save",
                    "Error. Failed to save XML file (" + filePath + ") - " + e.getMessageAndLocation());
            throw new Exception(e);
        } finally {
            JvmManager.gc(transformerFactory, transformer, source, streamResult);
        }
    }

    private void parseXml() {
        Document doc;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(new InputSource(new StringReader(xmlString)));
            doc.getDocumentElement().normalize();
            xmlDoc = doc;
            isValid = true;
        } catch (Exception e) {
            //e.printStackTrace();
            Log.error(XmlObject.class, "parseXml",
                    "Error. Failed to parse XML file - " + e.getMessage());
            xmlValidationError = e.getMessage();
        }
    }

    public String toString() {
        return getXmlString();
    }

    public String serialize(boolean omitXmlDeclaration) {
        TransformerFactory transformerFactory = null;
        Transformer transformer = null;
        DOMSource source = null;
        Writer out = null;
        StreamResult streamResult = null;

        String formattedXml;
        try {
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer(
                    new StreamSource(Main.class.getClassLoader().getResourceAsStream(Constants.REMOVE_BLANK_LINES_XSLT)));
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, (omitXmlDeclaration) ? "yes" : "no");
            source = new DOMSource(xmlDoc);
            out = new StringWriter();
            streamResult = new StreamResult(out);
            transformer.transform(source, streamResult);
            formattedXml = out.toString();
            JvmManager.gc(transformerFactory, transformer, source, streamResult, out);
            return formattedXml;
        } catch (TransformerException e) {
            Log.error(XmlObject.class, "serialize",
                    "Error. Failed to format XML - " + e.getMessageAndLocation());
        } finally {
            JvmManager.gc(transformerFactory, transformer, source, streamResult, out);
        }
        return "";
    }

    /*public String serialize() throws Exception {

        OutputFormat format = null;
        Writer writer = null;
        XMLSerializer serializer = null;
        String xml = null;

        try {
            format = new OutputFormat(xmlDoc);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(4);
            format.setOmitXMLDeclaration(true);
            writer = new StringWriter();
            serializer = new XMLSerializer(writer, format);
            serializer.serialize(xmlDoc);

            xml = writer.toString();

            writer.flush();writer.close();
            JvmManager.gc(format, writer, serializer);

        } catch (Exception e){
            e.printStackTrace();
            Log.error(XmlObject.class, "serialize", "ERROR Serializing XML Document: "+e.getMessage());
        } finally {
            if(writer != null){ writer.flush(); writer.close(); }
            JvmManager.gc(format, writer, serializer);
        }

        return xml;
    }*/

}
