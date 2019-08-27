import javafx.css.Match;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.File;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Tokenizer {
    //
    private static final String intReg = "(\\d+)";
    private static final String symbolReg ="[{}()\\[\\].,;+\\-*/&|<>=~]";
    private static final String keywordReg =
            "(class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)";
    private static final String stringCaptureReg = "([\\\"]\\b.*[\\\"])";
    private static final String identifierReg = "([A-Za-z]\\w+|[A-Za-z])";


    public Tokenizer() {

    }

    /*

     */
    public void ReadDocument(String fileName) {
        // expects a .jack file
//        System.out.println(symbolReg);
//        System.out.println(keywordReg);
        try {
            StringWriter stringWriter = new StringWriter();

            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter =
                    xMLOutputFactory.createXMLStreamWriter(stringWriter);

            // get each line
            File file = new File(fileName);
            Scanner reader = new Scanner(file);
            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("tokens");
            while (reader.hasNextLine()) {
                String line = reader.nextLine().stripLeading(); // Strip first empty spaces - we don't need those
//                System.out.println(line);

                if (!line.startsWith("//") && !line.startsWith("/**") && !line.isBlank()) { // filter comments and blank lines
                    String allSplitter = "(\\d+)|([;\\[{}(),.+\\-*/&|<>~=\\]])|([\\\"]\\b.*[\\\"])|([A-Za-z]\\w+|[A-Za-z])";

                    Pattern pat = Pattern.compile(allSplitter);
                    Matcher m = pat.matcher(line);
                    while (m.find()){
                        String match = m.group();
                        if(match.matches(keywordReg)){
                            System.out.println("Keyword: " + match);
                            xMLStreamWriter.writeStartElement("keyword");
                            xMLStreamWriter.writeCharacters(match);
                            xMLStreamWriter.writeEndElement();
                        }
                        else if (match.matches(symbolReg)) {
                            System.out.println("Symbol: " + match);
                            xMLStreamWriter.writeStartElement("symbol");
                            xMLStreamWriter.writeCharacters(match);
                            xMLStreamWriter.writeEndElement();

                        } else if (match.matches(intReg)){
                            System.out.println("IntegerConstant: " + match);
                            xMLStreamWriter.writeStartElement("integerConstant");
                            xMLStreamWriter.writeCharacters(match);
                            xMLStreamWriter.writeEndElement();

                        } else if (match.matches(identifierReg)){ //
                            System.out.println("Identifier: " + match);
                            xMLStreamWriter.writeStartElement("identifier");
                            xMLStreamWriter.writeCharacters(match);
                            xMLStreamWriter.writeEndElement();

                        } else if (match.matches(stringCaptureReg)){
                            System.out.println("StringConstant: " + match);
                            xMLStreamWriter.writeStartElement("stringConstant");
                            xMLStreamWriter.writeCharacters(match);
                            xMLStreamWriter.writeEndElement();
                        }
                    }
                }
            }
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndDocument();

            xMLStreamWriter.flush();
            xMLStreamWriter.close();

            String xml = stringWriter.getBuffer().toString();

            stringWriter.close();
            System.out.println(xml);


        } catch (XMLStreamException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}