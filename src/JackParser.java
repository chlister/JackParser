import ProgramStructure.Tokenizer;
import error_pack.JackCompilerException;
import ProgramStructure.JackStructure;


import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;



public class JackParser {

    public static void main(String args[]){
        // Later args will contain the filename
        // Documents/School/nand2tetris/nand2tetris/projects/10/ArrayTest/Main.jack
        String path = "/home/zbcmakar/Documents/School/nand2tetris/nand2tetris/projects/11/Average/Main.jack";
        // win - E:\OneDrive - EFIF\H5\nand2tetris softwaresuite-20190805\nand2tetris\projects\10\ArrayTest
//        String path = "E:\\OneDrive - EFIF\\H5\\nand2tetris softwaresuite-20190805\\nand2tetris\\projects\\10\\Square\\SquareGame.jack";
        String filePath;
        String fileName;
        if (System.getProperty("os.name").contains("Windows")){
             filePath = path.substring(0,path.lastIndexOf("\\"));
             fileName = path.substring(path.lastIndexOf("\\")+1, path.lastIndexOf("."));
        } else {
             filePath = path.substring(0,path.lastIndexOf("/"));
             fileName = path.substring(path.lastIndexOf("/")+1, path.lastIndexOf("."));
        }

        List<String> tokens;
        Tokenizer tok = new Tokenizer();
        tokens = tok.ReadDocument(path);
        JackStructure pro = new JackStructure(tokens, filePath+fileName+".xml");
        try {
            pro.ReadDocument();
        } catch (XMLStreamException | IOException | JackCompilerException e) {
            e.printStackTrace();
        }
    }
}
