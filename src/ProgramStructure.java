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

import ProgramStructure.StructureState;

public class ProgramStructure {
    private static final String intReg = "(\\d+)";
    private static final String symbolReg ="[{}()\\[\\].,;+\\-*/&|<>=~]";
    private static final String keywordReg =
            "(class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)";
    private static final String stringCaptureReg = "([\\\"]\\b.*[\\\"])";
    private static final String identifierReg = "([A-Za-z]\\w+|[A-Za-z])";
    private static final String allSplitterReg = "(\\d+)|([;\\[{}(),.+\\-*/&|<>~=\\]])|([\\\"]\\b.*[\\\"])|([A-Za-z]\\w+|[A-Za-z])";

    // States
    public StructureState state = new StructureState();

    public void ReadDocument(String fileName) {
        try {
            StringWriter stringWriter = new StringWriter();

            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter =
                    xMLOutputFactory.createXMLStreamWriter(stringWriter);

            // get each line
            File file = new File(fileName);
            Scanner reader = new Scanner(file);


            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("class");

            while (reader.hasNextLine()) {
                String line = reader.nextLine().stripLeading(); // Strip first empty spaces - we don't need those

                if (!line.startsWith("//") && !line.startsWith("/**") && !line.isBlank()) { // filter comments and blank lines
//                    line = line.replaceFirst("//", ""); // comments on the same line should be removed
                    line = line.substring(0, line.indexOf("//"));

                    Pattern pat = Pattern.compile(allSplitterReg);
                    Matcher m = pat.matcher(line);
                    while (m.find()) {
                        // Send group to the state handle method
                        String match = m.group();
                        if (match.matches(keywordReg)) {
                            System.out.println("Keyword: " + match);
                            xMLStreamWriter.writeStartElement("keyword");
                            xMLStreamWriter.writeCharacters(match);
                            xMLStreamWriter.writeEndElement();
                        } else if (match.matches(symbolReg)) {
                            System.out.println("Symbol: " + match);
                            xMLStreamWriter.writeStartElement("symbol");
                            xMLStreamWriter.writeCharacters(match);
                            xMLStreamWriter.writeEndElement();

                        } else if (match.matches(intReg)) {
                            System.out.println("IntegerConstant: " + match);
                            xMLStreamWriter.writeStartElement("integerConstant");
                            xMLStreamWriter.writeCharacters(match);
                            xMLStreamWriter.writeEndElement();

                        } else if (match.matches(identifierReg)) { //
                            System.out.println("Identifier: " + match);
                            xMLStreamWriter.writeStartElement("identifier");
                            xMLStreamWriter.writeCharacters(match);
                            xMLStreamWriter.writeEndElement();

                        } else if (match.matches(stringCaptureReg)) {
                            System.out.println("StringConstant: " + match);
                            xMLStreamWriter.writeStartElement("stringConstant");
                            xMLStreamWriter.writeCharacters(match);
                            xMLStreamWriter.writeEndElement();
                        }
                    }
                }
            }


            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

