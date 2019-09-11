package ProgramStructure;

import error_pack.JackCompilerException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class JackStructure {
    private static final String intReg = "(\\d+)";
    private static final String typeReg = "(int|char|boolean|[A-Za-z]\\w+)\\w*";
    private static final String symbolReg = "[{}()\\[\\].,;+\\-*/&|<>=~]";
    private static final String keywordReg =
            "(class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)";
    private static final String identifierReg = "([A-Za-z]\\w+|[A-Za-z])";
    private static final String unaryTermReg = "([-~])\\w*";
    private static final String opReg = "([+\\-*/&|<>=])\\w*";
    private static final String keywordConstantReg = "(true|false|null|this)\\w*";
    private static final String stringReg = "([\\\"]\\b.*[\\\"])";
    private List<String> xml;
    private String outPutFile;
    private int lineNum = 0;
    private StringWriter stringWriter;
    private XMLOutputFactory xMLOutputFactory;
//    private XMLStreamWriter xMLStreamWriter;

    private String className, strSubroutine;
    private int nLabelIndex, nParams;
    private VMWriter vmWriter;
    private SymbolTable symbolTable;

    /**
     * Method will need a list of lines from the tokenizer
     * It runs via a linenumber, which will get incremented in each method - the next line will thereafter
     * get extracted by the method and a if/else statement will interpret the next line, deciding if the
     * next statement can be executed or not
     */
    public JackStructure(List<String> _xml, String _outPutFile) {
        xml = _xml;
        outPutFile = _outPutFile;
        vmWriter = new VMWriter(new File(_outPutFile));
        symbolTable = new SymbolTable();
    }

    /**
     * Starts reading the List<String> provided
     * Saves the output in the outPutFile specified
     *
     * @throws XMLStreamException
     * @throws IOException
     * @throws JackCompilerException
     */
    public void ReadDocument() throws XMLStreamException, IOException, JackCompilerException {
        try {
//            stringWriter = new StringWriter();
//            xMLOutputFactory = XMLOutputFactory.newInstance();
//////            xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);
////            xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(new FileWriter(outPutFile));
            String line = xml.get(lineNum);

            ClassStructure(line);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Expects the first line to be 'class'
     * then begins to compile the class structure:
     * 'class' className '{' classVarDec* subroutineDec* '}'
     *
     * @param line -> 'class'
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void ClassStructure(String line) throws XMLStreamException, JackCompilerException {
        // check class keyword
        // Add the identifier
        // Check if next line is -> {
        // Check for classVarDec*
        // Check for subroutineDec*
        // Check for end char -> }
        // first line should be class
        lineNum++;
        line = xml.get(lineNum);
        // Class name
        lineNum++;
        className = line;
        line = xml.get(lineNum);

        // symbol {
        lineNum++;
        line = xml.get(lineNum);


        if (line.matches("(static|field)")) {
            ClassVarDec(line);
        }
        line = xml.get(lineNum);
        if (line.matches("(constructor|function|method)")) {
            SubroutineDec(line);
        }
        line = xml.get(lineNum);
        // symbol }

        vmWriter.close();
    }

    /**
     * Expects constructor|function|method as param
     * Will match the structure:
     * ('constructor'|'function'|'method') ('void'|type)subroutineName'('parameterList')'subroutineBody
     *
     * @param line -> constructor|function|method
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void SubroutineDec(String line) throws XMLStreamException, JackCompilerException {
        String keyword, type, subroutineName;
        // new subroutine - reset symboltable
        symbolTable.startSubroutine();

        lineNum++;
        // constructor|function|method
        keyword = line;
        line = xml.get(lineNum);

        lineNum++;
        // void | type
        type = line;
        line = xml.get(lineNum);

        lineNum++;
        // subroutineName
        strSubroutine = className + "." + line;
        symbolTable.define("this", keyword, "argument");
        line = xml.get(lineNum);    // (

        lineNum++;
        line = xml.get(lineNum);
        // TODO Sub Param
        ParameterList(line);

        line = xml.get(lineNum);
        // )
        lineNum++;
        line = xml.get(lineNum);

        SubroutineBody(line);

        line = xml.get(lineNum);
        if (line.matches("(constructor|function|method)"))
            SubroutineDec(line);
    }

    /**
     * Expects a type as param
     * Will match the structure:
     * ((type varName) (',' type varName)*)?
     * Will add as many as needed or a empty parameterList tag
     *
     * @param line -> type
     * @throws XMLStreamException
     */
    private void ParameterList(String line) throws XMLStreamException {
////        xMLStreamWriter.writeStartElement("parameterList");

        if (line.matches(typeReg)) {
            AddParam(line);
        }

////        xMLStreamWriter.writeEndElement(); // end parameterList tag
    }

    /**
     * Expects a type as param
     * Will add as many params as needed
     *
     * @param line -> type
     * @throws XMLStreamException
     */
    private void AddParam(String line) throws XMLStreamException {
        String type;
        String paramName;
        lineNum++;

        // type
        type = line;
        line = xml.get(lineNum);
        lineNum++;

        // paramName
        paramName = line;
        symbolTable.define(paramName, type, "argument");
        vmWriter.writePush("argument", symbolTable.varCount("argument"));
        //TODO Param
        line = xml.get(lineNum);
        if (line.matches(",")) {
            lineNum++;
            line = xml.get(lineNum);
            AddParam(line);
        }
    }

    /**
     * Expects the symbol '{'
     * Will match the structure:
     * '{'varDec* statements'}'
     *
     * @param line -> '{'
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void SubroutineBody(String line) throws XMLStreamException, JackCompilerException {

        lineNum++;
        // {
        line = xml.get(lineNum);
        if (line.matches("(var)")) {
            VarDec(line);
        }
        vmWriter.writeFunction(strSubroutine, symbolTable.varCount("var"));
        line = xml.get(lineNum);
        if (!line.matches("}")) {
            Statements(line);
        }

        line = xml.get(lineNum);
//        AddXMLTag("symbol", line); // }
        lineNum++;


    }

    /**
     * Expects to get static|field as param
     * <p>
     * Statics and fields will be saved for later use by the class
     *
     * @param line -> static|field
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void ClassVarDec(String line) throws XMLStreamException, JackCompilerException {
        String kind, type, varName;
        lineNum++;
        kind = line;                         // static | field

        line = xml.get(lineNum);
        lineNum++;
        type = line;                         // type

        line = xml.get(lineNum);
        lineNum++;
        varName = line;
        symbolTable.define(varName, type, kind);

        line = xml.get(lineNum);
        while (line.matches(",")) {
            lineNum++;
            line = xml.get(lineNum);        // varName
            varName = line;
            symbolTable.define(varName, type, kind);
            lineNum++;
            line = xml.get(lineNum);
        }
        line = xml.get(lineNum);
        if (line.matches(";")) {
            lineNum++;
        }
        line = xml.get(lineNum);
        if (line.matches("(static|field)")) {
            ClassVarDec(line);
        }
    }

    /**
     * Expects let|while|do|return|if as param
     *
     * @param line -> let|if|while|do|return
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void Statements(String line) throws XMLStreamException, JackCompilerException {
////        xMLStreamWriter.writeStartElement("statements");

        Statement(line);

////        xMLStreamWriter.writeEndElement();
    }

    /**
     * Expects let | while | do | return | if
     * Finds which statement will get executed
     *
     * @param line -> let|if|while|do|return
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void Statement(String line) throws XMLStreamException, JackCompilerException {
        /*
        line should contain
        -> let
        -> if
        -> while
        -> do
        -> return
         */
        switch (line) {
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
        if (line.matches("(let|while|do|return|if)")) {
            // New statement
            Statement(line);
        }
    }

    /**
     * Expects the 'do' keyword as param
     * Will match the structure:
     * 'do' subroutineCall ';'
     *
     * @param line -> 'do'
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void DoStatement(String line) throws XMLStreamException, JackCompilerException {
//        xMLStreamWriter.writeStartElement("doStatement");

        lineNum++;
//        AddXMLTag("keyword", line);     // do
        line = xml.get(lineNum);
        SubroutineCall(line);                // subroutine
        line = xml.get(lineNum);
        lineNum++;
//        AddXMLTag("symbol", line);      // ;

//        xMLStreamWriter.writeEndElement();

    }

    /**
     * Expects 'if' as param
     * Will match the structure:
     * 'if' '(' expression ')' '{' statement '}' ('else' '{' statements '}')?
     * If statement will end when the last '}' is encountered after statements
     *
     * @param line -> if
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void IfStatement(String line) throws XMLStreamException, JackCompilerException {
        lineNum++;
//        xMLStreamWriter.writeStartElement("ifStatement");
//        AddXMLTag("keyword", line);
        line = xml.get(lineNum);
        if (line.matches("\\(")) {
            lineNum++;
//            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            nParams = 0;
            Expression(line);
            line = xml.get(lineNum);
            if (line.matches("\\)")) {
                lineNum++;
//                AddXMLTag("symbol", line);
                line = xml.get(lineNum);
                if (line.matches("\\{")) {
                    lineNum++;
//                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    Statements(line);
                    line = xml.get(lineNum);
                    if (line.matches("}")) {
                        lineNum++;
//                        AddXMLTag("symbol", line);
                        line = xml.get(lineNum);
                        if (line.matches("(else)")) {
                            lineNum++;
//                            AddXMLTag("keyword", line);
                            line = xml.get(lineNum);
                            if (line.matches("\\{")) {
                                lineNum++;
//                                AddXMLTag("symbol", line);
                                line = xml.get(lineNum);
                                Statements(line);
                                line = xml.get(lineNum);
                                if (line.matches("}")) {
                                    lineNum++;
//                                    AddXMLTag("symbol", line);
                                }
                            }
                        }
                    }
                }
            }
        }
//        xMLStreamWriter.writeEndElement(); // Ends if statement
    }

    /**
     * Expects the 'while' keyword
     * Will match the structure:
     * 'while' '('expression')' '{' statements '}'
     * while statement ends when '}' en encountered
     *
     * @param line -> 'while'
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void WhileStatement(String line) throws XMLStreamException, JackCompilerException {
        String secondLabel = "LABEL_" + nLabelIndex++;
        String firstLabel = "LABEL_" + nLabelIndex++;
        vmWriter.writeLabel(firstLabel);
        lineNum++;
//        xMLStreamWriter.writeStartElement("whileStatement");
//        AddXMLTag("keyword", line);
        line = xml.get(lineNum);
        if (line.matches("\\(")) {
            lineNum++;
//            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            nParams = 0;
            Expression(line);
            vmWriter.writeArithmetic("not");
            vmWriter.writeIf(secondLabel);
            line = xml.get(lineNum);
            if (line.matches("\\)")) {
                lineNum++;
//                AddXMLTag("symbol", line);
                line = xml.get(lineNum);
                if (line.matches("\\{")) {
                    lineNum++;
//                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    Statements(line);
                    line = xml.get(lineNum);
                    if (line.matches("}")) {
                        lineNum++;
//                        AddXMLTag("symbol", line);
                    }
                    // if condition go to first label
                    vmWriter.writeGoto(firstLabel);
                    // otherwise go to next label
                    vmWriter.writeLabel(secondLabel);
                }
            }
        }
//        xMLStreamWriter.writeEndElement(); // Ends whileStatement

    }

    /**
     * Expects to get 'return' as param
     * Will match the structure as:
     * 'return' expression ';'
     *
     * @param line -> return
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void ReturnStatement(String line) throws XMLStreamException, JackCompilerException {
        lineNum++;
        line = xml.get(lineNum);
        if (!line.matches(";")) {
            Expression(line);
        } else {
            vmWriter.writePush("constant", 0);
        }
        line = xml.get(lineNum);
        lineNum++;
        vmWriter.writeReturn();
    }

    /**
     * Method expects a let statement
     * Will then break up the let statement if it matches this structure:
     * 'let' varName ('['expression']')? '=' expression ';'
     *
     * @param line -> 'let'
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void LetStatement(String line) throws XMLStreamException, JackCompilerException {
        String varName = xml.get(lineNum + 1);

////        xMLStreamWriter.writeStartElement("letStatement");

        if (line.matches("(let)")) {
            lineNum++;
//            AddXMLTag("keyword", line);
            line = xml.get(lineNum);
            if (line.matches(identifierReg)) { // varName
                varName = line;
//                vmWriter.writePush(symbolTable.kindOf(varName), symbolTable.indexOf(varName));

                lineNum++;
//                AddXMLTag("identifier", line);
                line = xml.get(lineNum);
                if (line.matches("\\[")) { // expression
                    lineNum++;
//                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    nParams = 0;
                    Expression(line);
                    line = xml.get(lineNum);
                    if (line.matches("]")) { // End [expression]
                        lineNum++;
//                        AddXMLTag("symbol", line);
                    }
                }
                // Last part of letStatement ( = expression; )
                line = xml.get(lineNum);
                if (line.matches("=")) {
                    lineNum++;
//                    AddXMLTag("symbol", line);
                    line = xml.get(lineNum);
                    nParams = 0;
                    Expression(line);
                    line = xml.get(lineNum);
                    if (line.matches(";")) {
                        lineNum++;
//                        AddXMLTag("symbol", line);
                    }
                }
            }
            // TODO: pop
            vmWriter.writePop(symbolTable.kindOf(varName), symbolTable.indexOf(varName));
        }
//        xMLStreamWriter.writeEndElement(); // ends letStatement
    }

    /**
     * Method expects to get a 'term'
     * Term: IntegerConstant | stringConstant | keywordConstant | varName | varName'['expression']' | subroutineCall | '('expression')' | unaryOp Term
     * Will keep adding expressions if encountering a ','
     *
     * @param line
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void Expression(String line) throws XMLStreamException, JackCompilerException {
//        xMLStreamWriter.writeStartElement("expression");
        Term(line);
//        xMLStreamWriter.writeEndElement();
        line = xml.get(lineNum);
        nParams++;
        if (line.matches(",")) { // Checks if expression ends with a ',' -> then adds more
            lineNum++;
//            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            Expression(line);
        }
    }

    /**
     * Method expects to get:
     * Integer|String|Keyword|UnaryOp|Identifier|'('
     * Term: IntegerConstant | stringConstant | keywordConstant | varName | varName'['expression']' | subroutineCall | '('expression')' | unaryOp Term
     * Will keep adding terms if encountering a operator
     *
     * @param line
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void Term(String line) throws XMLStreamException, JackCompilerException {
//        xMLStreamWriter.writeStartElement("term");
        if (line.matches(intReg)) {
            lineNum++;
//            AddXMLTag("integerConstant", line);
            vmWriter.writePush("constant", Integer.parseInt(line));
        } else if (line.matches(stringReg)) {
            lineNum++;
            line = line.substring((line.indexOf("\"") + 1), line.lastIndexOf("\""));
            vmWriter.writePush("constant", line.length());
            vmWriter.writeCall("String.new", 1);
            for (int i = 0; i < line.length(); i++) {
                vmWriter.writePush("constant", line.charAt(i));
                vmWriter.writeCall("String.appendChar", 2);
            }
        } else if (line.matches(keywordConstantReg)) {
            lineNum++;
//            AddXMLTag("keyword", line);
            // TODO: find keyword

        } else if (line.matches(unaryTermReg)) {
            lineNum++;
//            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            Term(line);
        } else if (line.matches(identifierReg)) { // VarName
//            lineNum++;
////            AddXMLTag("identifier", line);
//            line = xml.get(lineNum);
            String nextLine = xml.get(lineNum + 1);
            /*
              Next check the next line
              -> '.' (subroutineCall)       ((varName.subroutineCall))
              -> '[' (expression            ((varName[expression]))
              -> if none match              ( just a varName )
             */
            if (nextLine.matches("[.]") || nextLine.matches("\\(")) { // SubroutineCall! ( varName. | varName( )
                String varName = xml.get(lineNum + 2);

                SubroutineCall(line); // -> sending varNam|subroutineName to method+
                if (nextLine.matches("[.]")) {
                    String strCall = line + "." + varName;
                    vmWriter.writeCall(strCall, nParams);
                }
            } else if (nextLine.matches("[\\[]")) { // expression
                lineNum++;
//                AddXMLTag("identifier", line); // varName
                line = xml.get(lineNum);

                lineNum++;
//                AddXMLTag("symbol", line); // symbol
                line = xml.get(lineNum);

                Expression(line);

                line = xml.get(lineNum);
                if (line.matches("]")) {
                    lineNum++;
//                    AddXMLTag("symbol", line); // ]
                }
            } else {
                // just a varName
                vmWriter.writePush(symbolTable.kindOf(line), symbolTable.indexOf(line));
                lineNum++;
//                AddXMLTag("identifier", line);
            }
        } else if (line.matches("\\(")) {
            lineNum++;
//            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            Expression(line);
            line = xml.get(lineNum);
            if (line.matches("\\)")) {
                lineNum++;
//                AddXMLTag("symbol", line);
            }
        }
        // Close Term tag
//        xMLStreamWriter.writeEndElement();
        line = xml.get(lineNum);
        if (line.matches(opReg)) { // Checks if term ends with a operator -> then adds more
            String op = line;
            lineNum++;
//            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            Term(line);
            if (op.matches("<")) {
                vmWriter.writeArithmetic("lt");
            } else if (op.matches(">")) {
                vmWriter.writeArithmetic("gt");
            } else if (op.matches("&")) {
                vmWriter.writeArithmetic("and");
            } else if (op.matches("\\+")) {
                vmWriter.writeArithmetic("add");
            } else if (op.matches("-")) {
                vmWriter.writeArithmetic("sub");
            } else if (op.matches("\\*")) {
                vmWriter.writeCall("Math.multiply", 2);
            } else if (op.matches("/")) {
                vmWriter.writeCall("Math.divide", 2);
            } else if (op.matches("=")) {
                vmWriter.writeArithmetic("eq");
            } else if (op.matches("|")) {
                vmWriter.writeArithmetic("or");

            }
        }
    }

    /**
     * Expects the 'identifier' as param
     * Will match the structure:
     * subroutineName '(' expressionList ')' | (className | varName)'.'subroutineName'('expressionList')'
     *
     * @param line -> identifier
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void SubroutineCall(String line) throws XMLStreamException, JackCompilerException {
        lineNum++;
//        AddXMLTag("identifier", line);          // subroutineName|className|varName
        line = xml.get(lineNum);
        if (line.matches("[.]")) {          // has className|varName'.'subroutineName
            lineNum++;
//            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            if (line.matches(identifierReg)) {
                lineNum++;
//                AddXMLTag("identifier", line);
                line = xml.get(lineNum);
            }
        }
        if (line.matches("\\(")) {             // adding (expressionList)
            lineNum++;
//            AddXMLTag("symbol", line);
            line = xml.get(lineNum);
            ExpressionList(line);
            line = xml.get(lineNum);
            if (line.matches("\\)")) {
                lineNum++;
//                AddXMLTag("symbol", line);
            }
        }
    }

    /**
     * Method expects a term as param
     * If the param is ')' then no expression or term tag will be called
     * Adds the expressionList tag to the XML
     *
     * @param line -> term
     * @throws XMLStreamException
     * @throws JackCompilerException
     */
    private void ExpressionList(String line) throws XMLStreamException, JackCompilerException {
//        xMLStreamWriter.writeStartElement("expressionList");
        if (!line.matches("\\)"))
            Expression(line);
//        xMLStreamWriter.writeEndElement();
    }

    /**
     * Expects to get the 'var' keyword as param
     * Will run more than once if it encounters a ','
     * Will stop a varDec when encountering a ';'
     * Will start another varDec if the next line after ';' is var
     *
     * @param line keyword
     * @throws XMLStreamException
     */
    private void VarDec(String line) throws XMLStreamException, JackCompilerException {
        String type, varName;

        lineNum++;
////        xMLStreamWriter.writeStartElement("varDec");
////        AddXMLTag("keyword", line);
        line = xml.get(lineNum);
        if (line.matches(typeReg)) { // needs type
            lineNum++;
////            AddXMLTag("keyword", line); // type
            type = line;
            line = xml.get(lineNum);

            lineNum++;
////            AddXMLTag("identifier",line); // varName
            varName = line;

            symbolTable.define(varName, type, "var");
//            vmWriter.writePush("var", symbolTable.varCount("var"));

            int index = 0;
            line = xml.get(lineNum);
            while (line.matches(",")) {
                index++;
                lineNum++;
                line = xml.get(lineNum);        // varName
                varName = line;
                symbolTable.define(varName, type, "var");
//                vmWriter.writePush("var", symbolTable.varCount("var"));
                lineNum++;
                line = xml.get(lineNum);
            }

//            if (line.matches(",")){
//                lineNum++;
////                AddXMLTag("symbol",line); // symbol
//                line=xml.get(lineNum);
//                AddIdentifier(line); // Sends the identifier to be added to the XML
//            }
            line = xml.get(lineNum);
            if (line.matches("\\;")) { // End of varDec
                lineNum++;
////                AddXMLTag("symbol",line);
                // ;
                line = xml.get(lineNum);
////                xMLStreamWriter.writeEndElement();
                if (line.matches("(var)\\w*")) // if next line is a var then add another
                    VarDec(line);
            }
        }
    }

    /**
     * Expects to get an identifier as param
     * Adds as many identifiers as necessary
     *
     * @param line identifier
     * @throws XMLStreamException
     */
    private void AddIdentifier(String line) throws XMLStreamException {
        lineNum++;
//        AddXMLTag("identifier", line);
        line = xml.get(lineNum);
        if (line.matches(" , ")) {
            lineNum++;
//            AddXMLTag(" symbol ", line);
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
//    private void AddXMLTag(String tag, String chars) throws XMLStreamException {
//        xMLStreamWriter.writeStartElement(tag);
//        xMLStreamWriter.writeCharacters(chars);
//        xMLStreamWriter.writeEndElement();
//    }
}
