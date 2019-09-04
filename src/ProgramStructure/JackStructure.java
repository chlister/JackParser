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

    /**
     * Expects the first line to be 'class'
     * then begins to compile the class structure:
     * 'class' className '{' classVarDec* subroutineDec* '}'
     * @param line -> 'class'
     * @throws XMLStreamException
     */
    private void ClassStructure(String line) throws XMLStreamException {
        // check class keyword
            // Add the identifier
        // Check if next line is -> {
        // Check for classVarDec*
        // Check for subroutineDec*
        // Check for end char -> }
    }



    /**
     * Expects let|while|do|return|if as param
     * Adds the statements tag to the statements
     * @param line -> let|if|while|do|return
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void Statements(String line) throws XMLStreamException, JackCompilerException{
        xMLStreamWriter.writeStartElement("statement");

        Statement(line);

        xMLStreamWriter.writeEndElement();
    }

    /**
     * Expects let | while | do | return | if
     * Finds which statement will get executed
     * @param line -> let|if|while|do|return
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void Statement(String line) throws XMLStreamException, JackCompilerException{
        /*
        line should contain
        -> let
        -> if
        -> while
        -> do
        -> return
         */
        switch (line){
            case "let":
                LetStatement(line);
                break;
            case "while":
                WhileStatement(line);
                break;
            case "if":
                IfStatement(line);
                break;
            case "do":
                DoStatement(line);
                break;
            case "return":
                ReturnStatement(line);
                break;
        }
        line = xml.get(lineNum);
        if (line.matches("(let|while|do|return|if)")){
            // New statement
            Statement(line);
        }
    }

    /**
     * Expects the 'do' keyword as param
     * Will match the structure:
     * 'do' subroutineCall ';'
     * @param line -> 'do'
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void DoStatement(String line) throws XMLStreamException, JackCompilerException {
        xMLStreamWriter.writeStartElement("doStatement");

        lineNum++;
        AddXMLTag("keyword", line);     // do
        line = xml.get(lineNum);
        SubroutineCall(line);                // subroutine
        line = xml.get(lineNum);
        lineNum++;
        AddXMLTag("symbol", line);      // ;

        xMLStreamWriter.writeEndElement();

    }

    /**
     * Expects 'if' as param
     * Will match the structure:
     * 'if' '(' expression ')' '{' statement '}' ('else' '{' statements '}')?
     * If statement will end when the last '}' is encountered after statements
     * @param line -> if
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void IfStatement(String line) throws XMLStreamException, JackCompilerException {
        lineNum++;
        xMLStreamWriter.writeStartElement("ifStatement");
        AddXMLTag("keyword", line);
        line = xml.get(lineNum);
        if (line.matches("\\(")){
            lineNum++;
            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            Expression(line);
            line = xml.get(lineNum);
            if (line.matches("\\)")){
                lineNum++;
                AddXMLTag("symbol", line);
                line = xml.get(lineNum);
                if (line.matches("\\{")){
                    lineNum++;
                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    Statements(line);
                    line = xml.get(lineNum);
                    if (line.matches("}")){
                        lineNum++;
                        AddXMLTag("symbol", line);
                        line = xml.get(lineNum);
                        if (line.matches("(else)")){
                            lineNum++;
                            AddXMLTag("keyword", line);
                            line = xml.get(lineNum);
                            if (line.matches("\\{")) {
                                lineNum++;
                                AddXMLTag("symbol", line);
                                line = xml.get(lineNum);
                                Statements(line);
                                line = xml.get(lineNum);
                                if (line.matches("}")) {
                                    lineNum++;
                                    AddXMLTag("symbol", line);
                                }
                            }
                        }
                    }
                }
            }
        }
        xMLStreamWriter.writeEndElement(); // Ends if statement
    }

    /**
     * Expects the 'while' keyword
     * Will match the structure:
     * 'while' '('expression')' '{' statements '}'
     * while statement ends when '}' en encountered
     * @param line -> 'while'
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void WhileStatement(String line) throws XMLStreamException, JackCompilerException {
        lineNum++;
        xMLStreamWriter.writeStartElement("whileStatement");
        AddXMLTag("keyword", line);
        line = xml.get(lineNum);
        if  (line.matches("\\(")){
            lineNum++;
            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            Expression(line);
            line = xml.get(lineNum);
            if (line.matches("\\)")){
                lineNum++;
                AddXMLTag("symbol", line);
                line = xml.get(lineNum);
                if (line.matches("\\{")){
                    lineNum++;
                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    Statements(line);
                    line = xml.get(lineNum);
                    if (line.matches("}")){
                        lineNum++;
                        AddXMLTag("symbol", line);
                    }
                }
            }
        }
        xMLStreamWriter.writeEndElement(); // Ends whileStatement

    }

    /**
     * Expects to get 'return' as param
     * Will match the structure as:
     * 'return' expression ';'
     * @param line -> return
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void ReturnStatement(String line) throws XMLStreamException, JackCompilerException {
        lineNum++;
        xMLStreamWriter.writeStartElement("returnStatement");
        AddXMLTag("keyword", line);
        line = xml.get(lineNum);
        if (!line.matches(";")){
            Expression(line);
        }
        lineNum++;
        AddXMLTag("symbol", line);
    }

    /**
     * Method expects a let statement
     * Will then break up the let statement if it matches this structure:
     * 'let' varName ('['expression']')? '=' expression ';'
     * @param line -> 'let'
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void LetStatement(String line) throws XMLStreamException, JackCompilerException {
        if (line.matches("(let)")){
            lineNum++;
            xMLStreamWriter.writeStartElement("letStatement");
            AddXMLTag("keyword", line);
            line = xml.get(lineNum);
            if (line.matches(identifierReg)){ // varName
                lineNum++;
                AddXMLTag("identifier", line);
                line = xml.get(lineNum);
                if (line.matches("\\[")) { // expression
                    lineNum++;
                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    Expression(line);
                    line = xml.get(lineNum);
                    if (line.matches("]")){ // End [expression]
                        lineNum++;
                        AddXMLTag("symbol", line);
                    }
                }
                    // Last part of letStatement ( = expression; )
                line = xml.get(lineNum);
                if (line.matches("=")){
                    lineNum++;
                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    Expression(line);
                    line = xml.get(lineNum);
                    if (line.matches(";")){
                        lineNum++;
                        AddXMLTag("symbol", line);
                    }
                }
            }
        }
        xMLStreamWriter.writeEndElement(); // ends letStatement
    }

    /**
     * Method expects to get a 'term'
     * Term: IntegerConstant | stringConstant | keywordConstant | varName | varName'['expression']' | subroutineCall | '('expression')' | unaryOp Term
     * Will keep adding expressions if encountering a ','
     * @param line
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void Expression(String line) throws XMLStreamException, JackCompilerException {
        xMLStreamWriter.writeStartElement("expression");
        Term(line);
        xMLStreamWriter.writeEndElement();
        line = xml.get(lineNum);
        if (line.matches(",")){ // Checks if expression ends with a ',' -> then adds more
            lineNum++;
            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            Expression(line);
        }
    }

    /**
     * Method expects to get:
     * Integer|String|Keyword|UnaryOp|Identifier|'('
     * Term: IntegerConstant | stringConstant | keywordConstant | varName | varName'['expression']' | subroutineCall | '('expression')' | unaryOp Term
     * Will keep adding terms if encountering a operator
     * @param line
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void Term(String line) throws XMLStreamException, JackCompilerException {
        xMLStreamWriter.writeStartElement("term");
        if (line.matches(intReg)){
            lineNum++;
            AddXMLTag("integerConstant", line);
        }
        else if (line.matches(stringReg)){
            lineNum++;
            AddXMLTag("stringConstant", line);
        }
        else if(line.matches(keywordConstantReg)){
            lineNum++;
            AddXMLTag("keyword", line);
        }
        else if(line.matches(unaryTermReg)){
            lineNum++;
            AddXMLTag("symbol", line);
        }
        else if(line.matches(identifierReg)){ // VarName
//            lineNum++;
//            AddXMLTag("identifier", line);
//            line = xml.get(lineNum);
            String nextLine = xml.get(lineNum+1);
            /*
              Next check the next line
              -> '.' (subroutineCall)       ((varName.subroutineCall))
              -> '[' (expression            ((varName[expression]))
              -> if none match              ( just a varName )
             */
            if (nextLine.matches("[.]") || nextLine.matches("\\(")){ // SubroutineCall! ( varName. | varName( )
                SubroutineCall(line); // -> sending varNam|subroutineName to method
            }
            else if (nextLine.matches("[\\[]")) { // expression
                lineNum++;
                AddXMLTag("identifier", line); // varName
                line = xml.get(lineNum);

                lineNum++;
                AddXMLTag("symbol", line); // symbol
                line = xml.get(lineNum);

                Expression(line);

                line = xml.get(lineNum);
                if (line.matches("]")){
                    lineNum++;
                    AddXMLTag("symbol", line); // ]
                }
            }
            else {
                // just a varName
                lineNum++;
                AddXMLTag("identifier", line);
            }
        }
        else if(line.matches("\\(")){
            lineNum++;
            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            Expression(line);
            line = xml.get(lineNum);
            if (line.matches("\\)")){
                lineNum++;
                AddXMLTag("symbol", line);
            }
        }
        // Close Term tag
        xMLStreamWriter.writeEndElement();
        line = xml.get(lineNum);
        if (line.matches(opReg)){ // Checks if term ends with a operator -> then adds more
            lineNum++;
            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            Term(line);
        }
    }

    /**
     * Expects the 'identifier' as param
     * Will match the structure:
     * subroutineName '(' expressionList ')' | (className | varName)'.'subroutineName'('expressionList')'
     * @param line -> identifier
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void SubroutineCall(String line) throws XMLStreamException, JackCompilerException {
        lineNum++;
        AddXMLTag("identifier", line);          // subroutineName|className|varName
        line = xml.get(lineNum);
        if (line.matches("[.]")){          // has className|varName'.'subroutineName
            lineNum++;
            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            if (line.matches(identifierReg)){
                lineNum++;
                AddXMLTag("identifier", line);
                line = xml.get(lineNum);
            }
        }
        if (line.matches("\\(")){             // adding (expressionList)
            lineNum++;
            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            ExpressionList(line);
            line = xml.get(lineNum);
            if (line.matches("\\)")){
                lineNum++;
                AddXMLTag("symbol", line);
            }
        }
    }

    /**
     * Method expects a term as param
     * If the param is ')' then no expression or term tag will be called
     * Adds the expressionList tag to the XML
     * @param line -> term
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void ExpressionList(String line) throws XMLStreamException, JackCompilerException {
        xMLStreamWriter.writeStartElement("expressionList");
        if (!line.matches("\\)"))
            Expression(line);
        xMLStreamWriter.writeEndElement();
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
