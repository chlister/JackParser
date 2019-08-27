import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.File;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class ProgramStructure {
    private static final String intReg = "(\\d+)";
    private static final String symbolReg ="[{}()\\[\\].,;+\\-*/&|<>=~]";
    private static final String keywordReg =
            "(class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)";
    private static final String stringCaptureReg = "([\\\"]\\b.*[\\\"])";
    private static final String identifierReg = "([A-Za-z]\\w+|[A-Za-z])";
    private static final String allSplitterReg = "(\\d+)|([;\\[{}(),.+\\-*/&|<>~=\\]])|([\\\"]\\b.*[\\\"])|([A-Za-z]\\w+|[A-Za-z])";
    private List<String> xml;
    private int lineNum = 0;
    // States

    public void ReadDocument(List<String> _xml) {
        try {
            xml=_xml; // TODO: put in ctor
            StringWriter stringWriter = new StringWriter();

            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter xMLStreamWriter =
                    xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();
            xMLStreamWriter.writeStartElement("class");

            /**
             * Method will need a list of lines from the tokenizer
             * It runs via a linenumber, which will get incremented in each method - the next line will thereafter
             * get extracted by the method and a if/else statement will interpret the next line, deciding if the
             * next statement can be executed or not
             */

            // extract first three elements (class <className> '{')
            // --> The 3rd element should always match a classVarDec | subRoutineDec
            String line = xml.get(lineNum);

            if (line.matches("(class)")){
                lineNum++;
                xMLStreamWriter.writeStartElement("class");
                xMLStreamWriter.writeStartElement("keyword");
                xMLStreamWriter.writeEmptyElement(line);
                xMLStreamWriter.writeEndElement();
                line = xml.get(lineNum);
                if (line.matches(identifierReg)){
                    lineNum++;
                    xMLStreamWriter.writeStartElement("identifier");
                    xMLStreamWriter.writeEmptyElement(line);
                    xMLStreamWriter.writeEndElement();
                    line = xml.get(lineNum);
                    if (line.matches("\\{")){
                        lineNum++;
                        xMLStreamWriter.writeStartElement("symbol");
                        xMLStreamWriter.writeEmptyElement(line);
                        xMLStreamWriter.writeEndElement();
                        line = xml.get(lineNum);
                        if (line.matches("(static|field)\\w+")){ // classVarDec
                            classVarDec(line, xMLStreamWriter);
                            // TODO: when we're done

                        } else if (line.matches("(constructor|function|method)\\w+")){ // subroutineDec
                            subroutineDec(line);
                        }

                    }
                }
            }
//                    xml.remove(line);
//                    if (line.matches(keywordReg)) {
//                        xMLStreamWriter.writeStartElement("class");
//                        ClassStructure(xml, xMLStreamWriter);
//                    } else {
//                        // TODO: throw exception
//                    }



//                    Pattern pat = Pattern.compile(allSplitterReg);
//                    Matcher m = pat.matcher(line);
//                    while (m.find()) {
//                        // Send group to the state handle method
//                        String match = m.group();
//                        if (match.matches(keywordReg)) {
//                            System.out.println("Keyword: " + match);
//                            xMLStreamWriter.writeStartElement("keyword");
//                            xMLStreamWriter.writeCharacters(match);
//                            xMLStreamWriter.writeEndElement();
//                        } else if (match.matches(symbolReg)) {
//                            System.out.println("Symbol: " + match);
//                            xMLStreamWriter.writeStartElement("symbol");
//                            xMLStreamWriter.writeCharacters(match);
//                            xMLStreamWriter.writeEndElement();
//
//                        } else if (match.matches(intReg)) {
//                            System.out.println("IntegerConstant: " + match);
//                            xMLStreamWriter.writeStartElement("integerConstant");
//                            xMLStreamWriter.writeCharacters(match);
//                            xMLStreamWriter.writeEndElement();
//
//                        } else if (match.matches(identifierReg)) { //
//                            System.out.println("Identifier: " + match);
//                            xMLStreamWriter.writeStartElement("identifier");
//                            xMLStreamWriter.writeCharacters(match);
//                            xMLStreamWriter.writeEndElement();
//
//                        } else if (match.matches(stringCaptureReg)) {
//                            System.out.println("StringConstant: " + match);
//                            xMLStreamWriter.writeStartElement("stringConstant");
//                            xMLStreamWriter.writeCharacters(match);
//                            xMLStreamWriter.writeEndElement();
//                        }
//                }
//
//
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void classVarDec(String line, XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
        lineNum++;
        xMLStreamWriter.writeStartElement("classVarDec");
        xMLStreamWriter.writeStartElement("keyword");
        xMLStreamWriter.writeEmptyElement(line); // (static|field)
        xMLStreamWriter.writeEndElement();
        line = xml.get(lineNum);
        if (line.matches("(int|char|boolean)\\w+"+identifierReg)){ // TODO: extend with className
            lineNum++;
            xMLStreamWriter.writeStartElement("keyword");
            xMLStreamWriter.writeEmptyElement(line); //(int|char|boolean|className)
            xMLStreamWriter.writeEndElement();
            line = xml.get(lineNum);
            if (line.matches(identifierReg)){
                AddIdentifier(line, xMLStreamWriter); // Adds as many identifiers as needed
                // After building classVarDec only action to execute is: (more varNames) | (;)
                if (line.matches(";")){
                    lineNum++;
                    xMLStreamWriter.writeStartElement("symbol");
                    xMLStreamWriter.writeEmptyElement(line);
                    xMLStreamWriter.writeEndElement();
                    AddIdentifier(line, xMLStreamWriter);
//                    line = xml.get(lineNum);
                }
            } // Else fail
        }
        xMLStreamWriter.writeEndElement(); // Ending classVarDec
    }

    private void AddIdentifier(String line, XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
        lineNum++;
        xMLStreamWriter.writeStartElement("identifier");
        xMLStreamWriter.writeEmptyElement(line);
        xMLStreamWriter.writeEndElement();
        line = xml.get(lineNum);
        if (line.matches(",")){
            xMLStreamWriter.writeStartElement("symbol");
            xMLStreamWriter.writeEmptyElement(line);
            xMLStreamWriter.writeEndElement();
            AddIdentifier(line, xMLStreamWriter);}
    }


    private void ClassStructure(List<String> xml, XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
        String line = xml.get(0);
        xml.remove(line);
        if (line.matches(identifierReg)){
            xMLStreamWriter.writeStartElement("identifier");
            xMLStreamWriter.writeEmptyElement(line);
            xMLStreamWriter.writeEndElement();
        } else if (line.matches())
    }
}

