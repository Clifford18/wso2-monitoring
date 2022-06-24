package ke.co.skyworld.utils.file_utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DelimitedFileUtils {
    private static DecimalFormat dfNormal = new DecimalFormat("#");

    public static long getRowCount(File dataFile){

        String fileExtension = getFileExtension(dataFile.getAbsolutePath());
        if(fileExtension == null) return 0;

        switch (fileExtension) {
            case "csv":
            case "txt":
                try (InputStream is =
                             new BufferedInputStream(
                                     new FileInputStream(dataFile))) {
                    byte[] c = new byte[1024];
                    int count = 0;
                    int readChars = 0;
                    boolean empty = true;
                    while ((readChars = is.read(c)) != -1) {
                        empty = false;
                        for (int i = 0; i < readChars; ++i) {
                            if (c[i] == '\n') {
                                ++count;
                            }
                        }
                    }
                    return (count == 0 && !empty) ? 1 : count;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case "xls":

                try {
                    FileInputStream excelFile = new FileInputStream(dataFile);
                    Workbook workbook = new HSSFWorkbook(excelFile);
                    Sheet dataSheet = workbook.getSheetAt(0);

                    return dataSheet.getPhysicalNumberOfRows();

                } catch (Exception ignored) {

                }

                break;
            case "xlsx":
                try {
                    FileInputStream excelFile = new FileInputStream(dataFile);
                    Workbook workbook = new XSSFWorkbook(excelFile);
                    Sheet dataSheet = workbook.getSheetAt(0);

                    return dataSheet.getPhysicalNumberOfRows();

                } catch (Exception ignored) {

                }
                break;
            default:
                return 0;
        }

        return 0;
    }

    @SuppressWarnings("Duplicates")
    public static List<String> getColumnList(File dataFile, int columnIndex,
                                             String delimiter) {
        List<String> columnList;

        String fileExtension = getFileExtension(dataFile.getAbsolutePath());
        if(fileExtension == null) return null;

        switch (fileExtension) {
            case "csv":
            case "txt":
                Function<String, String> columnMap = (line) -> {
                    String[] p = line.split(delimiter);
                    if (p[columnIndex] == null) {
                        return "";
                    } else {
                        return p[columnIndex].trim();
                    }
                };
                try {
                    InputStream inputFS = new FileInputStream(dataFile);
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(inputFS));
                    columnList = br.lines().map(columnMap).collect(
                            Collectors.toList());
                    br.close();
                    return columnList;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            case "xlsx":

                try {
                    FileInputStream excelFile = new FileInputStream(dataFile);
                    Workbook workbook;
                    workbook = new XSSFWorkbook(excelFile);
                    Sheet dataSheet = workbook.getSheetAt(0);
                    return rowIterator(dataSheet, columnIndex);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            case "xls":

                try {
                    FileInputStream excelFile = new FileInputStream(dataFile);
                    Workbook workbook;
                    workbook = new HSSFWorkbook(excelFile);
                    Sheet dataSheet = workbook.getSheetAt(0);
                    return rowIterator(dataSheet, columnIndex);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;

            default:
                return null;

        }
    }

    private static List<String> rowIterator(Sheet dataSheet, int columnIndex){
        Cell currentCell;
        List<String> columnList = new ArrayList<>();
        for (Row currentRow : dataSheet) {
            currentCell = currentRow.getCell(columnIndex);
            if (currentCell.getCellTypeEnum() == CellType.STRING) {
                columnList.add(currentCell.getStringCellValue());
            } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                columnList.add(dfNormal.format(currentCell.getNumericCellValue()));
            }
        }
        return columnList;
    }

    @SuppressWarnings("Duplicates")
    public static List<List<String>> getAllDataList(File dataFile,
                                                    String delimiter){

        String fileExtension = getFileExtension(dataFile.getAbsolutePath());
        if(fileExtension == null) return null;

        switch (fileExtension){
            case "txt":
            case "csv":
                List<List<String>> dataList;
                Function<String, List<String>> allDataMap = (line) -> {
                    String[] p = line.split(delimiter, 2);
                    List<String> item = new ArrayList<>();
                    for (String data : p) {
                        if(data == null){
                            item.add("");
                        }else{
                            item.add(data.trim());
                        }
                    }
                    return item;
                };
                try{
                    InputStream inputFS = new FileInputStream(dataFile);
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
                    dataList = br.lines().map(allDataMap).collect(Collectors.toList());
                    br.close();
                    return dataList;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            case "xlsx":
                try{
                    FileInputStream excelFile = new FileInputStream(dataFile);
                    Workbook workbook;
                    workbook = new XSSFWorkbook(excelFile);
                    Sheet dataSheet = workbook.getSheetAt(0);
                    return rowIterator(dataSheet);
                }catch (Exception ignore){

                }
            case "xls":
                try{
                    FileInputStream excelFile = new FileInputStream(dataFile);
                    Workbook workbook;
                    workbook = new HSSFWorkbook(excelFile);
                    Sheet dataSheet = workbook.getSheetAt(0);
                    return rowIterator(dataSheet);
                }catch (Exception ignore){

                }
        }

        return null;
    }

    private static List<List<String>> rowIterator(Sheet dataSheet){
        List<List<String>> allData = new ArrayList<>();
        List<String> row;
        for (Row currentRow : dataSheet) {
            row = new ArrayList<>();
            for(Cell currentCell: currentRow){
                if (currentCell.getCellTypeEnum() == CellType.STRING) {
                    row.add(currentCell.getStringCellValue());
                } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                    row.add(dfNormal.format(currentCell.getNumericCellValue()));
                }
            }
            allData.add(row);
        }
        return allData;
    }

    public static List<List<String>> fetchDataFromExcel(String filePath) throws Exception {
        List<List<String>> result = new ArrayList<>();
        XMLEventReader reader = null;
        XMLEvent event = null;
        Attribute attribute = null;
        StartElement startElement = null;
        EndElement endElement = null;
        String characters = null;

        StringBuilder stringValue = new StringBuilder(); //for collecting the characters to complete values
        List<String> sharedStrings = new ArrayList<String>(); //list of shared strings
        Map<String, String> numberFormats = new HashMap<String, String>(); //map of number formats
        List<String> cellNumberFormats = new ArrayList<String>(); //list of cell number formats
        Path source = Paths.get(filePath); //path to the Excel file
        FileSystem fs = FileSystems.newFileSystem(source, null); //get filesystem of Excel file

        //get shared strings ==============================================================================
        Path sharedStringsTable = fs.getPath("/xl/sharedStrings.xml");
        reader = XMLInputFactory.newInstance().createXMLEventReader(Files.newInputStream(sharedStringsTable));
        boolean siFound = false;
        while (reader.hasNext()) {
            event = (XMLEvent) reader.next();
            if (event.isStartElement()) {
                startElement = (StartElement) event;
                if (startElement.getName().getLocalPart().equalsIgnoreCase("si")) {
                    //start element of shared string item
                    siFound = true;
                    stringValue = new StringBuilder();
                }
            } else if (event.isCharacters() && siFound) {
                //chars of the shared string item
                characters = event.asCharacters().getData();
                stringValue.append(characters);
            } else if (event.isEndElement()) {
                endElement = (EndElement) event;
                if (endElement.getName().getLocalPart().equalsIgnoreCase("si")) {
                    //end element of shared string item
                    siFound = false;
                    sharedStrings.add(stringValue.toString());
                }
            }
        }
        reader.close();
        //shared strings ==================================================================================

        //get styles, number formats are essential for detecting date / time values =======================
        Path styles = fs.getPath("/xl/styles.xml");
        reader = XMLInputFactory.newInstance().createXMLEventReader(Files.newInputStream(styles));
        boolean cellXfsFound = false;
        while (reader.hasNext()) {
            event = (XMLEvent) reader.next();
            if (event.isStartElement()) {
                startElement = (StartElement) event;
                if (startElement.getName().getLocalPart().equalsIgnoreCase("numFmt")) {
                    //start element of number format
                    attribute = startElement.getAttributeByName(new QName("numFmtId"));
                    String numFmtId = attribute.getValue();
                    attribute = startElement.getAttributeByName(new QName("formatCode"));
                    numberFormats.put(numFmtId, ((attribute != null) ? attribute.getValue() : "null"));
                } else if (startElement.getName().getLocalPart().equalsIgnoreCase("cellXfs")) {
                    //start element of cell format setting
                    cellXfsFound = true;
                } else if (startElement.getName().getLocalPart().equalsIgnoreCase("xf") && cellXfsFound) {
                    //start element of format setting in cell format setting
                    attribute = startElement.getAttributeByName(new QName("numFmtId"));
                    cellNumberFormats.add(((attribute != null) ? attribute.getValue() : "null"));
                }
            } else if (event.isEndElement()) {
                endElement = (EndElement) event;
                if (endElement.getName().getLocalPart().equalsIgnoreCase("cellXfs")) {
                    //end element of cell format setting
                    cellXfsFound = false;
                }
            }
        }
        reader.close();
        //styles ==========================================================================================

        //get sheet data of first sheet ===================================================================
        Path sheet1 = fs.getPath("/xl/worksheets/sheet1.xml");
        reader = XMLInputFactory.newInstance().createXMLEventReader(Files.newInputStream(sheet1));
        boolean rowFound = false;
        boolean cellFound = false;
        boolean cellValueFound = false;
        boolean inlineStringFound = false;
        String cellStyle = null;
        String cellType = null;
        List<String> row = null;
        while (reader.hasNext()) {
            event = (XMLEvent) reader.next();
            if (event.isStartElement()) {
                startElement = (StartElement) event;
                if (startElement.getName().getLocalPart().equalsIgnoreCase("row")) {
                    //start element of row
                    rowFound = true;
                    if (row != null) {
                        result.add(row);
                    }
                    row = new ArrayList<>();
                } else if (startElement.getName().getLocalPart().equalsIgnoreCase("c") && rowFound) {
                    //start element of cell in row
                    cellFound = true;
                    attribute = startElement.getAttributeByName(new QName("t"));
                    cellType = ((attribute != null) ? attribute.getValue() : null);
                    attribute = startElement.getAttributeByName(new QName("s"));
                    cellStyle = ((attribute != null) ? attribute.getValue() : null);
                } else if (startElement.getName().getLocalPart().equalsIgnoreCase("v") && cellFound) {
                    //start element of value in cell
                    cellValueFound = true;
                    stringValue = new StringBuilder();
                } else if (startElement.getName().getLocalPart().equalsIgnoreCase("is") && cellFound) {
                    //start element of inline string in cell
                    inlineStringFound = true;
                    stringValue = new StringBuilder();
                }
            } else if (event.isCharacters() && cellFound && (cellValueFound || inlineStringFound)) {
                //chars of the cell value or the inline string
                characters = event.asCharacters().getData();
                stringValue.append(characters);
            } else if (event.isEndElement()) {
                endElement = (EndElement) event;
                if (endElement.getName().getLocalPart().equalsIgnoreCase("row")) {
                    //end element of row
                    rowFound = false;
                } else if (endElement.getName().getLocalPart().equalsIgnoreCase("c")) {
                    //end element of cell
                    cellFound = false;
                } else if (endElement.getName().getLocalPart().equalsIgnoreCase("v")) {
                    //end element of value
                    cellValueFound = false;
                    String cellValue = stringValue.toString();
                    if ("s".equals(cellType)) {
                        cellValue = sharedStrings.get(Integer.valueOf(cellValue));
                    }
                    row.add(cellValue);
                } else if (endElement.getName().getLocalPart().equalsIgnoreCase("is")) {
                    //end element of inline string
                    inlineStringFound = false;
                    String cellValue = stringValue.toString();
                    row.add(cellValue);
                }
            }
        }
        reader.close();
        //sheet data ======================================================================================
        fs.close();
        return result;
    }

    public static String getFileExtension(String fileName){
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        }else{
            return null;
        }
    }
}
