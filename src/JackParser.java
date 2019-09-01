import error_pack.JackCompilerException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

public class JackParser {
    public static void main(String args[]){
        // Later args will contain the filename
        // Documents/School/nand2tetris/nand2tetris/projects/10/ArrayTest/Main.jack
//        String fileName = "Documents/School/nand2tetris/nand2tetris/projects/10/ArrayTest/Main.jack";
        // win - E:\OneDrive - EFIF\H5\nand2tetris softwaresuite-20190805\nand2tetris\projects\10\ArrayTest
        String fileName = "E:\\OneDrive - EFIF\\H5\\nand2tetris softwaresuite-20190805\\nand2tetris\\projects\\10\\ArrayTest\\Main.jack";
        List<String> tokens;
        Tokenizer tok = new Tokenizer();
        tokens = tok.ReadDocument(fileName);
        ProgramStructure pro = new ProgramStructure();
        try {
            pro.ReadDocument(tokens);
        } catch (XMLStreamException | IOException | JackCompilerException e) {
            e.printStackTrace();
        }
    }
}
