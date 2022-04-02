package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileNameXml = "data.xml";
        String jsonCSVFileName = "dataCSV.json";
        String jsonXMLFileName = "dataXML.json";
        List<Employee> listCSV = parseCSV(columnMapping, fileName);
        String jsonCSV = listToJson(listCSV);
        writeString(jsonCSV, jsonCSVFileName);

        List<Employee> listXML = parseXML(fileNameXml);
        String jsonXML = listToJson(listXML);
        writeString(jsonXML, jsonXMLFileName);
    }

    static List<Employee> parseCSV(String[] columnMapping, String filename) {
        try (CSVReader csvReader = new CSVReader(new FileReader(filename))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List>() {}.getType();
        String json  = gson.toJson(list, listType);
        return json;
    }

    static void writeString(String json,String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    static List<Employee> parseXML(String fileName) {
        List<Employee> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fileName);

            NodeList nodeList = doc.getElementsByTagName("employee");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String id = element.getElementsByTagName("id").item(0).getTextContent();
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    String age = element.getElementsByTagName("age").item(0).getTextContent();

                    Employee employee = new Employee();
                    try {
                        employee.id = Long.parseLong(id);
                    } catch (NumberFormatException exception) {
                        exception.printStackTrace();
                    }
                    employee.firstName = firstName;
                    employee.lastName = lastName;
                    employee.country = country;
                    try {
                        employee.age = Integer.parseInt(age);
                    } catch (NumberFormatException exception) {
                        exception.printStackTrace();
                    }
                    list.add(employee);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException exception) {
            exception.printStackTrace();
        }
        return list;
    }
}
