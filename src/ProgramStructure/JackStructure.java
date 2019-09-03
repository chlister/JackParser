package ProgramStructure;

import error_pack.JackCompilerException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class JackStructure {
    private static final String intReg = "(\\d+)";
    private static final String typeReg = "(int|char|boolean|[A-Za-z]\\w+)\\w*";
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
    public void ReadDocument(List<String> _xml) throws XMLStreamException, IOException, JackCompilerException {
        try {
            xml=_xml; // TODO: put in ctor
            stringWriter = new StringWriter();

            xMLOutputFactory = XMLOutputFactory.newInstance();
            xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

            xMLStreamWriter.writeStartDocument();

            String line = xml.get(lineNum);

            ClassStructure(line);
        }catch(Exception e){
            e.printStackTrace();
        }
        xMLStreamWriter.writeEndDocument();

        xMLStreamWriter.flush();
        xMLStreamWriter.close();

        String xml = stringWriter.getBuffer().toString();
        stringWriter.close();
        System.out.println(xml);
    }

    private void ClassStructure(String line) throws XMLStreamException {

    }

    /**
     * Method expects to get:
     * Integer|String|Keyword|UnaryOp|Identifier|'('
     * @param line
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void Term(String line) throws XMLStreamException, JackCompilerException {
        if (line.matches(intReg)){
            lineNum++;
            AddXMLTag("integerConstant", line);
            line=xml.get(lineNum);
//            CheckEndOfExpression(line);
        }
        else if (line.matches(stringReg)){
            lineNum++;
            AddXMLTag("stringConstant", line);
            line=xml.get(lineNum);
//            CheckEndOfExpression(line);
        }
        else if(line.matches(keywordConstantReg)){
            lineNum++;
            AddXMLTag("keyword", line);
            line=xml.get(lineNum);
//            CheckEndOfExpression(line);
        }
        else if(line.matches(unaryTermReg)){
            lineNum++;
            AddXMLTag("symbol", line);
            line=xml.get(lineNum);

//            Expressions(line);
            line=xml.get(lineNum);
//            CheckEndOfExpression(line);
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
            if (line.matches("[.]") || line.matches("\\(")){ // SubroutineCall! ( varName. | varName( )
//                SubroutineCall(line);
            }
            else if (line.matches("[\\[]")) { // expression
                lineNum++;
                AddXMLTag("symbol", line);
                line = xml.get(lineNum);
                xMLStreamWriter.writeStartElement("expression");

//                Expressions(line);

                xMLStreamWriter.writeEndElement();
                line = xml.get(lineNum);
                AddXMLTag("symbol", line); // ]
                lineNum++;
            }
        }
        else if(line.matches("\\(")){
            lineNum++;
            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            xMLStreamWriter.writeStartElement("expression");
            xMLStreamWriter.writeStartElement("term");
            Term(line);
            xMLStreamWriter.writeEndElement();
            xMLStreamWriter.writeEndElement();
            line = xml.get(lineNum);
            if (line.matches("\\)")){
                lineNum++;
                AddXMLTag("symbol", line);
            }
        }
    }


    /**
     * Expects to get the 'var' keyword as param
     * Will run more than once if it encounters a ','
     * Will stop a varDec when encountering a ';'
     * Will start another varDec if the next line after ';' is var
     * @param line keyword
     * @throws XMLStreamException
     */
    private void VarDec(String line) throws XMLStreamException, JackCompilerException {
        lineNum++;
        xMLStreamWriter.writeStartElement(" varDec ");
        AddXMLTag(" keyword ", line);
        line=xml.get(lineNum);
        if (line.matches(typeReg)){ // needs type
            lineNum++;
            AddXMLTag(" keyword ", line); // type

            line=xml.get(lineNum);
            AddXMLTag(" identifier ",line); // varName
            line = xml.get(lineNum);
            if (line.matches(",")){
                lineNum++;
                AddXMLTag(" symbol ",line); // symbol
                line=xml.get(lineNum);
                AddIdentifier(line); // Sends the identifier to be added to the XML
            }
            line = xml.get(lineNum);
            if (line.matches("\\;")){ // End of varDec
                lineNum++;
                AddXMLTag(" symbol ",line); // ;
                line=xml.get(lineNum);
                xMLStreamWriter.writeEndElement();
                if (line.matches("(var)\\w*")) // if next line is a var then add another
                    VarDec(line);
            }
        }
    }

    /**
     * Can add more identifiers to the XML
     * Adds as many as necessary
     * @param line identifier
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void AddIdentifier(String line) throws XMLStreamException, JackCompilerException {
        lineNum++;
        AddXMLTag(" identifier ", line);
        line = xml.get(lineNum);
        if (line.matches(" , ")){
            lineNum++;
            AddXMLTag(" symbol ", line);
            line = xml.get(lineNum);
            AddIdentifier(line);
        }
    }

    /**
     * Adds a tag, and a nested element to the XML stream
     * @param tag <tag></tag>
     * @param chars <tag>chars</tag>
     * @throws XMLStreamException
     */
    private void AddXMLTag(String tag, String chars) throws XMLStreamException {
        xMLStreamWriter.writeStartElement(tag);
        xMLStreamWriter.writeCharacters(chars);
        xMLStreamWriter.writeEndElement();
    }
}
