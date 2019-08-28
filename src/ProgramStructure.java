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
    private static final String typeReg = "(int|char|boolean)\\w*";
    private static final String symbolReg ="[{}()\\[\\].,;+\\-*/&|<>=~]";
    private static final String keywordReg =
            "(class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)";
    private static final String identifierReg = "([A-Za-z]\\w+|[A-Za-z])";
    private static final String unaryTermReg = "([-~])\\w*";
    private static final String opReg = "([+\\-*/&|<>=])\\w*";
    private static final String keywordConstantReg = "(true|false|null|this)\\w*";
    private List<String> xml;
    private int lineNum = 0;
    private StringWriter stringWriter;
    private XMLOutputFactory xMLOutputFactory;
    private XMLStreamWriter xMLStreamWriter;
    // States

    public void ReadDocument(List<String> _xml) {
        try {
            xml=_xml; // TODO: put in ctor
            stringWriter = new StringWriter();

            xMLOutputFactory = XMLOutputFactory.newInstance();
            xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();
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
                xMLStreamWriter.writeCharacters(line);
                xMLStreamWriter.writeEndElement();
                line = xml.get(lineNum);
                if (line.matches(identifierReg)){
                    lineNum++;
                    xMLStreamWriter.writeStartElement("identifier");
                    xMLStreamWriter.writeCharacters(line);
                    xMLStreamWriter.writeEndElement();
                    line = xml.get(lineNum);
                    if (line.matches("\\{")){
                        lineNum++;
                        xMLStreamWriter.writeStartElement("symbol");
                        xMLStreamWriter.writeCharacters(line);
                        xMLStreamWriter.writeEndElement();
                        line = xml.get(lineNum);
                        if (line.matches("(static|field)\\w*")) { // classVarDec
                            classVarDec(line);
                            line = xml.get(lineNum);
                            if (line.matches("(constructor|function|method)\\w*")) { // subroutineDec
                                subroutineDec(line);
                            } // TODO: throw exception
                        } // TODO: Throw Exception
                    }// TODO: throw exception
                }// TODO: throw exception
            }// TODO: throw exception

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

    private void subroutineDec(String line) throws XMLStreamException{
        lineNum++;
        xMLStreamWriter.writeStartElement("subroutineDec");
        xMLStreamWriter.writeStartElement("keyword");
        xMLStreamWriter.writeCharacters(line); // (constructor|function|method)
        xMLStreamWriter.writeEndElement();
        line = xml.get(lineNum);
        if (line.matches(typeReg+"|(void)\\w*")){ // Needs type
            lineNum++;
            xMLStreamWriter.writeStartElement("keyword");
            xMLStreamWriter.writeCharacters(line); //(int|char|boolean|void)
            xMLStreamWriter.writeEndElement();
            line = xml.get(lineNum);
            if (line.matches(identifierReg)){ // SubroutineName
                lineNum++;
                xMLStreamWriter.writeStartElement("identifier");
                xMLStreamWriter.writeCharacters(line); // subroutineName
                xMLStreamWriter.writeEndElement();
                line = xml.get(lineNum);
                if (line.matches("\\(")){ // Adds params to subroutine
                    lineNum++;
                    xMLStreamWriter.writeStartElement("symbol");
                    xMLStreamWriter.writeCharacters(line); // (
                    xMLStreamWriter.writeEndElement();
                    xMLStreamWriter.writeStartElement("parameterList"); // If no params are listed then is has to be empty
                    line = xml.get(lineNum);
                    if (line.matches(typeReg)) {// Has type --> must be a param
                        AddParam(line);
                    }
                    xMLStreamWriter.writeEndElement();
                    line = xml.get(lineNum);
                    if (line.matches("\\)")) {
                        lineNum++;
                        xMLStreamWriter.writeStartElement("symbol");
                        xMLStreamWriter.writeCharacters(line); // (
                        xMLStreamWriter.writeEndElement();
                        line = xml.get(lineNum);
                        if (line.matches("\\{")){ // Adds subroutineBody
                            AddSubroutineBody(line);
                        } // TODO: throw subroutine start body tag missing
                    } // TODO: throw exception
                } // TODO: Throw subroutine start param tag missing
            }
        }
        xMLStreamWriter.writeEndElement();
    }

    private void AddParam(String line) throws XMLStreamException {
        lineNum++;
        xMLStreamWriter.writeStartElement("keyword");
        xMLStreamWriter.writeCharacters(line);
        xMLStreamWriter.writeEndElement();
        line=xml.get(lineNum);
        if(line.matches(identifierReg)) {
            lineNum++;
            xMLStreamWriter.writeStartElement("identifier");
            xMLStreamWriter.writeCharacters(line);
            xMLStreamWriter.writeEndElement();
            line=xml.get(lineNum);
            if (line.matches(",")) {
                lineNum++;
                xMLStreamWriter.writeStartElement("symbol");
                xMLStreamWriter.writeCharacters(line);
                xMLStreamWriter.writeEndElement();
                line = xml.get(lineNum);
                AddParam(line);
            }
        } // TODO: Throw identifier missing
    }

    private void AddSubroutineBody(String line) throws XMLStreamException {
        lineNum++;
        xMLStreamWriter.writeStartElement("subroutineBody");
        xMLStreamWriter.writeStartElement("symbol");
        xMLStreamWriter.writeStartElement(line);
        xMLStreamWriter.writeEndElement();
        line=xml.get(lineNum);
        if (line.matches("(var)\\w*")){
            xMLStreamWriter.writeStartElement("varDec");
            AddVarDec(line); // 1 to many
            line = xml.get(lineNum);
            if (line.matches(";")){
                lineNum++;
                xMLStreamWriter.writeStartElement("symbol");
                xMLStreamWriter.writeStartElement(line);
                xMLStreamWriter.writeEndElement();
                xMLStreamWriter.writeEndElement(); // ends varDec
            } // TODO: Throw closing tag missing
            lineNum++;
            line = xml.get(lineNum);
            if(line.matches("(let|if|while|do|return)")){ // TODO: Statements!
                lineNum++;
                xMLStreamWriter.writeStartElement("statements");
                // TODO Find which statement
                Statements(line);
                xMLStreamWriter.writeEndElement(); // Ends statements
            }
        } // TODO: Throw variable Declarations missing
    }

    private void Statements(String line) throws XMLStreamException {
        if (line.matches("let")) { // TODO: let statement
            lineNum++;
            xMLStreamWriter.writeStartElement("letStatement");
            xMLStreamWriter.writeStartElement("keyword");
            xMLStreamWriter.writeStartElement(line);
            xMLStreamWriter.writeEndElement();
            line = xml.get(lineNum);
            if (line.matches(identifierReg)){
                lineNum++;
                xMLStreamWriter.writeStartElement("identifier");
                xMLStreamWriter.writeStartElement(line);
                xMLStreamWriter.writeEndElement();
                line = xml.get(lineNum);
                if (line.matches("\\[")){ // Has [expression]
                    Expressions(line);
                }
            }

            xMLStreamWriter.writeEndElement(); // Ends letStatement
        } else if  (line.matches("if")) { // TODO: if statement

        } else if  (line.matches("while")) { // TODO: while statement

        } else if  (line.matches("do")) { // TODO: do statement

        } else if  (line.matches("return")) { // TODO: return statement

        }
    }

    private void Expressions(String line) throws XMLStreamException {
        lineNum++;
        xMLStreamWriter.writeStartElement("symbol");
        xMLStreamWriter.writeStartElement(line);
        xMLStreamWriter.writeEndElement();
        xMLStreamWriter.writeStartElement("expression");
        line = xml.get(lineNum);
        if (line.matches(intReg+"|"+identifierReg+"|"+unaryTermReg+"|"+keywordConstantReg+"|"+opReg+"|"))


    }

    private void AddVarDec(String line) throws XMLStreamException {
        lineNum++;
        xMLStreamWriter.writeStartElement("keyword");
        xMLStreamWriter.writeStartElement(line);
        xMLStreamWriter.writeEndElement();
        line=xml.get(lineNum);
        if(line.matches(typeReg)){
            lineNum++;
            xMLStreamWriter.writeStartElement("keyword");
            xMLStreamWriter.writeStartElement(line);
            xMLStreamWriter.writeEndElement();
            line=xml.get(lineNum);
            if (line.matches(identifierReg)){
                lineNum++;
                xMLStreamWriter.writeStartElement("identifier");
                xMLStreamWriter.writeStartElement(line);
                xMLStreamWriter.writeEndElement();
                line=xml.get(lineNum);
                if (line.matches(",")){
                    AddIdentifier(line);
                }
            } // TODO: Throw identifier needed
        } // TODO: Throw type error
    }

    private void classVarDec(String line) throws XMLStreamException {
        lineNum++;
        xMLStreamWriter.writeStartElement("classVarDec");
        xMLStreamWriter.writeStartElement("keyword");
        xMLStreamWriter.writeCharacters(line); // (static|field)
        xMLStreamWriter.writeEndElement();
        line = xml.get(lineNum);
        if (line.matches("(int|char|boolean)\\w+|"+identifierReg)){ // Needs type TODO: extend with className
            lineNum++;
            xMLStreamWriter.writeStartElement("keyword");
            xMLStreamWriter.writeCharacters(line); //(int|char|boolean|className)
            xMLStreamWriter.writeEndElement();
            line = xml.get(lineNum);
            if (line.matches(identifierReg)){ // Needs varName
                AddIdentifier(line); // Adds as many identifiers as needed
                // After building classVarDec only action to execute is: (more varNames) | (;)
                if (line.matches(";")){ // needs to close variable declaration
                    lineNum++;
                    xMLStreamWriter.writeStartElement("symbol");
                    xMLStreamWriter.writeCharacters(line);
                    xMLStreamWriter.writeEndElement();
                    // VarDec is done
                } // TODO: Throw closing tag missing
            } // TODO: Throw identifier missing
        } // TODO: Throw type missing
        xMLStreamWriter.writeEndElement(); // Ending classVarDec
    }

    private void AddIdentifier(String line) throws XMLStreamException {
        lineNum++;
        xMLStreamWriter.writeStartElement("identifier");
        xMLStreamWriter.writeCharacters(line);
        xMLStreamWriter.writeEndElement();
        line = xml.get(lineNum);
        if (line.matches(",")){
            lineNum++;
            xMLStreamWriter.writeStartElement("symbol");
            xMLStreamWriter.writeCharacters(line);
            xMLStreamWriter.writeEndElement();
            line = xml.get(lineNum);
            AddIdentifier(line);
        }
    }
}