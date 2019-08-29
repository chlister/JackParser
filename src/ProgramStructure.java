import error_pack.JackCompilerException;

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


public class ProgramStructure  {
    private static final String intReg = "(\\d+)";
    private static final String typeReg = "(int|char|boolean)\\w*";
    private static final String symbolReg ="[{}()\\[\\].,;+\\-*/&|<>=~]";
    private static final String keywordReg =
            "(class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)";
    private static final String identifierReg = "([A-Za-z]\\w+|[A-Za-z])";
    private static final String unaryTermReg = "([-~])\\w*";
    private static final String opReg = "([+\\-*/&|<>=])\\w*";
    private static final String keywordConstantReg = "(true|false|null|this)\\w*";
    private static final String stringReg = "([\\\"]\\b.*[\\\"])";
    private List<String> xml;
    private int lineNum = 0;
    private StringWriter stringWriter;
    private XMLOutputFactory xMLOutputFactory;
    private XMLStreamWriter xMLStreamWriter;

    /**
     * Method will need a list of lines from the tokenizer
     * It runs via a linenumber, which will get incremented in each method - the next line will thereafter
     * get extracted by the method and a if/else statement will interpret the next line, deciding if the
     * next statement can be executed or not
     */
    public void ReadDocument(List<String> _xml) {
        try {
            xml=_xml; // TODO: put in ctor
            stringWriter = new StringWriter();

            xMLOutputFactory = XMLOutputFactory.newInstance();
            xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();


            // extract first three elements (class <className> '{')
            // --> The 3rd element should always match a classVarDec | subRoutineDec
            String line = xml.get(lineNum);

            if (line.matches("(class)")){
                lineNum++;
                xMLStreamWriter.writeStartElement("class");
                AddXMLTag("keyword", line);
                line = xml.get(lineNum);
                if (line.matches(identifierReg)){
                    lineNum++;
                    AddXMLTag("identifier", line);
                    line = xml.get(lineNum);
                    if (line.matches("\\{")){
                        lineNum++;
                        AddXMLTag("symbol", line);
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
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void subroutineDec(String line) throws XMLStreamException{
        lineNum++;
        xMLStreamWriter.writeStartElement("subroutineDec");
        AddXMLTag("keyword", line);// (constructor|function|method)
        line = xml.get(lineNum);
        if (line.matches(typeReg+"|(void)\\w*")){ // Needs type
            lineNum++;
            AddXMLTag("keyword", line); //(int|char|boolean|void)
            line = xml.get(lineNum);
            if (line.matches(identifierReg)){ // SubroutineName
                lineNum++;
                AddXMLTag("identifier", line); // subroutineName
                line = xml.get(lineNum);
                if (line.matches("\\(")){ // Adds params to subroutine
                    lineNum++;
                    AddXMLTag("symbol", line);
                    xMLStreamWriter.writeStartElement("parameterList"); // If no params are listed then is has to be empty
                    line = xml.get(lineNum);
                    if (line.matches(typeReg)) {// Has type --> must be a param
                        AddParam(line);
                    }
                    xMLStreamWriter.writeEndElement();
                    line = xml.get(lineNum);
                    if (line.matches("\\)")) {
                        lineNum++;
                        AddXMLTag("symbol", line);
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
        AddXMLTag("keyword", line);
        line=xml.get(lineNum);
        if(line.matches(identifierReg)) {
            lineNum++;
            AddXMLTag("identifier", line);
            line=xml.get(lineNum);
            if (line.matches(",")) {
                lineNum++;
                AddXMLTag("symbol", line);
                line = xml.get(lineNum);
                AddParam(line);
            }
        } // TODO: Throw identifier missing
    }

    private void AddSubroutineBody(String line) throws XMLStreamException {
        lineNum++;
        xMLStreamWriter.writeStartElement("subroutineBody");
        AddXMLTag("symbol", line);
        line=xml.get(lineNum);
        if (line.matches("(var)\\w*")){
            xMLStreamWriter.writeStartElement("varDec");
            AddVarDec(line); // 1 to many
            line = xml.get(lineNum);
            if (line.matches(";")){
                lineNum++;
                AddXMLTag("symbol", line);
                xMLStreamWriter.writeEndElement(); // ends varDec
            } // TODO: Throw closing tag missing
        } // TODO: Throw variable Declarations missing
        line = xml.get(lineNum);
        if(line.matches("(let|if|while|do|return)")){ // TODO: Statements!
            lineNum++;
            xMLStreamWriter.writeStartElement("statements");
            Statements(line);
            xMLStreamWriter.writeEndElement(); // Ends statements
        } // TODO: Throw missing statement declaration
    }

    private void Statements(String line) throws XMLStreamException {
        if (line.matches("let")) {
            lineNum++;
            xMLStreamWriter.writeStartElement("letStatement");
            AddXMLTag("keyword", line);
            line = xml.get(lineNum);
            if (line.matches(identifierReg)) { // varName
                lineNum++;
                AddXMLTag("identifier", line);
                line = xml.get(lineNum);
                if (line.matches(opReg)) { // operator found
                    lineNum++;
                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    xMLStreamWriter.writeStartElement("expression");

                    Expressions(line);

                    xMLStreamWriter.writeEndElement(); // Closing expression tag
                    line = xml.get(lineNum);
                    if (line.matches(";")) {
                        lineNum++;
                        AddXMLTag("symbol", line);
                    } // TODO: semicolon missing
                } // TODO: no operator found
                else if (line.matches("\\[")) { // TODO: Has [expression]
                    lineNum++;
                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    xMLStreamWriter.writeStartElement("expression");

                    Expressions(line);

                    xMLStreamWriter.writeEndElement(); // close expression tag

                    line = xml.get(lineNum);
                    AddXMLTag("symbol", line); // ]
                    line = xml.get(lineNum);
                    if (line.matches(opReg)) {
                        lineNum++;
                        Expressions(line);
                    } // TODO: Throw operator missing
                } // TODO: Throw operator or invalid expression signature
            } // TODO: Throw identifier missing
            xMLStreamWriter.writeEndElement(); // Ends letStatement
        }
        else if (line.matches("if")) { // TODO: if statement
            lineNum++;
            xMLStreamWriter.writeStartElement("ifStatement");
            AddXMLTag("keyword", line);
            line = xml.get(lineNum);
            if (line.matches("\\(")) {
                lineNum++;
                AddXMLTag("symbol", line);
                line = xml.get(lineNum);
                xMLStreamWriter.writeStartElement("expression");
                Expressions(line);
                xMLStreamWriter.writeEndElement();
                line = xml.get(lineNum);
                if (line.matches("\\)")) {
                    lineNum++;
                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    if (line.matches("\\{")) {
                        lineNum++;
                        AddXMLTag("symbol", line);
                        line = xml.get(lineNum);
                        xMLStreamWriter.writeStartElement("statements");
                        Statements(line);
                        xMLStreamWriter.writeEndElement();
                        line = xml.get(lineNum);
                        if (line.matches("}")) {
                            lineNum++;
                            AddXMLTag("symbol", line);
                            line = xml.get(lineNum);
                            if (line.matches("(else)\\w*")) {
                                lineNum++;
                                AddXMLTag("keyword", line);
                                line = xml.get(lineNum);
                                xMLStreamWriter.writeStartElement("statements");
                                Statements(line);
                                xMLStreamWriter.writeEndElement();
                                line = xml.get(lineNum);
                            }
                            if (line.matches("}")) {
                                lineNum++;
                                xMLStreamWriter.writeStartElement("whileStatement");
                                AddXMLTag("symbol", line);
                                line = xml.get(lineNum);

                            } // TODO: Throw missing closing tag
                        }
                    } // TODO: Throw missing closing tag
                } // TODO: Throw missing curly brackets
            } // TODO: Throw missing closing tag
        }
        else if (line.matches("while")) { // TODO: while statement
            lineNum++;
            xMLStreamWriter.writeStartElement("whileStatement");
            AddXMLTag("keyword", line);
            lineNum++;
            line = xml.get(lineNum);
            if (line.matches("\\(")){
                lineNum++;
                AddXMLTag("symbol", line);
                line=xml.get(lineNum);
                xMLStreamWriter.writeStartElement("expression");
                Expressions(line);
                xMLStreamWriter.writeEndElement(); // close expression
                line=xml.get(lineNum);

                if (line.matches("\\)")){
                    lineNum++;
                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    if (line.matches("\\{")){
                        lineNum++;
                        AddXMLTag("symbol", line);
                        line=xml.get(lineNum);
                        xMLStreamWriter.writeStartElement("statements");
                        Statements(line);
                        xMLStreamWriter.writeEndElement(); // close statements
                        line = xml.get(lineNum);
                        if (line.matches("}")) {
                            lineNum++;
                            AddXMLTag("symbol", line);
                        } // TODO: throw closing tag missing
                    } // TODO: throw opening tag missing
                } // TODO: Throw missing closing tag
            } // TODO: Opening tag missing
        }
        else if (line.matches("do")) {
            lineNum++;
            xMLStreamWriter.writeStartElement("doStatement");
            AddXMLTag("keyword", line);
            lineNum++;
            line = xml.get(lineNum);
            if(line.matches(identifierReg)){ // subroutineName
                lineNum++;
                AddXMLTag("identifier", line);
                line = xml.get(lineNum);
                if (line.matches(".")) { // SubroutineCall
                    SubroutineCall(line);
                } // TODO: dot notation missing
            } // TODO: no identifier found
        }
        else if (line.matches("return")) {
            lineNum++;
            xMLStreamWriter.writeStartElement("returnStatement");
            AddXMLTag("keyword", line);
            lineNum++;
            line = xml.get(lineNum);
            if (!line.matches(";")){ // Not the end tag -> must be expression
                xMLStreamWriter.writeStartElement("expression");
                Expressions(line);
                xMLStreamWriter.writeEndElement();
            }
            if (line.matches(";")){
                AddXMLTag("symbol", line);
                lineNum++;
            }
        }
        line = xml.get(lineNum);
        if (line.matches("(let|if|while|do|return)")) {
            Statements(line);
        }
    }


    private void Expressions(String line) throws XMLStreamException {
        xMLStreamWriter.writeStartElement("term");
        if (line.matches(intReg)){
            lineNum++;
            AddXMLTag("integerConstant", line);
            line=xml.get(lineNum);
            CheckEndOfExpression(line);
        }
        else if (line.matches(stringReg)){
            lineNum++;
            AddXMLTag("stringConstant", line);
            line=xml.get(lineNum);
            CheckEndOfExpression(line);
        }
        else if(line.matches(keywordConstantReg)){
            lineNum++;
            AddXMLTag("keywordConstant", line);
            line=xml.get(lineNum);
            CheckEndOfExpression(line);
        }
        else if(line.matches(unaryTermReg)){
            lineNum++;
            AddXMLTag("symbol", line);
            line=xml.get(lineNum);
            CheckEndOfExpression(line);
        }
        else if(line.matches(identifierReg)){ // VarName
            lineNum++;
            AddXMLTag("identifier", line);
            line = xml.get(lineNum);
            /*
              Next check the next line
              -> '.' (subroutineCall)       ((varName.subroutineCall))
              -> '[' (expression            ((varName[expression]))
              -> ; (Close the expression)   ((varName;))
             */
            if (line.matches(".")){ // SubroutineCall!
                SubroutineCall(line); }
            else if (line.matches("\\[")) { // expression
                lineNum++;
                AddXMLTag("symbol", line);
                line = xml.get(lineNum);
                xMLStreamWriter.writeStartElement("expression");

                Expressions(line);

                xMLStreamWriter.writeEndElement();
            }
            if (line.matches("]")){
                lineNum++;
            } // TODO: No subroutine call or end line found
        } // TODO: no identifier found
        else if (line.matches(opReg)){
            lineNum++;
            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            Expressions(line);
        }
        xMLStreamWriter.writeEndElement(); // Closing term tag
    }

    private void CheckEndOfExpression(String line) throws XMLStreamException {
        if (line.matches("\\(")){
            lineNum++;
            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            xMLStreamWriter.writeStartElement("expression"); // Start expression
            if (!line.matches("\\)")){ // Must be more expressions
                Expressions(line);
            }
            line = xml.get(lineNum);
            if (line.matches("\\)")){
                lineNum++;
                AddXMLTag("symbol", line);
            } // TODO: End param tag missing
            xMLStreamWriter.writeEndElement(); // End expression
        } // TODO: Throw start param tag missing
    }

    private void AddVarDec(String line) throws XMLStreamException {
        lineNum++;
        AddXMLTag("keyword", line);
        line=xml.get(lineNum);
        if(line.matches(typeReg)){
            lineNum++;
            AddXMLTag("keyword", line);
            line=xml.get(lineNum);
            if (line.matches(identifierReg)){
                lineNum++;
                AddXMLTag("identifier", line);
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
        AddXMLTag("keyword", line);// (static|field)
        line = xml.get(lineNum);
        if (line.matches("(int|char|boolean)\\w+|"+identifierReg)){ // Needs type TODO: extend with className
            lineNum++;
            AddXMLTag("keyword", line); //(int|char|boolean|className)
            line = xml.get(lineNum);
            if (line.matches(identifierReg)){ // Needs varName
                AddIdentifier(line); // Adds as many identifiers as needed
                // After building classVarDec only action to execute is: (more varNames) | (;)
                if (line.matches(";")){ // needs to close variable declaration
                    lineNum++;
                    AddXMLTag("symbol", line);
                    // VarDec is done
                } // TODO: Throw closing tag missing
            } // TODO: Throw identifier missing
        } // TODO: Throw type missing
        xMLStreamWriter.writeEndElement(); // Ending classVarDec
    }

    private void AddIdentifier(String line) throws XMLStreamException {
        lineNum++;
        AddXMLTag("identifier", line);
        line = xml.get(lineNum);
        if (line.matches(",")){
            lineNum++;
            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            AddIdentifier(line);
        }
    }

    private void SubroutineCall(String line) throws XMLStreamException {
        lineNum++;
        AddXMLTag("identifier", line);
        line = xml.get(lineNum);
        if (line.matches(identifierReg)){ // SubroutineName
            lineNum++;
            AddXMLTag("identifier", line);
            line = xml.get(lineNum);
            if (line.matches("\\(")){
                lineNum++;
                AddXMLTag("symbol", line);
                line = xml.get(lineNum);
                xMLStreamWriter.writeStartElement("expressionList");
                Expressions(line);
                xMLStreamWriter.writeEndElement();
                line = xml.get(lineNum);
                if (line.matches("\\)")){
                    lineNum++;
                    AddXMLTag("symbol", line);
                }
            } // TODO: Throw Missing parentheses
        } // TODO: Throw no subroutineName found
    }

    private void AddXMLTag(String tag, String chars) throws XMLStreamException {
        xMLStreamWriter.writeStartElement(tag);
        xMLStreamWriter.writeCharacters(chars);
        xMLStreamWriter.writeEndElement();
    }
}
