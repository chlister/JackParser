import java.util.List;

public class JackParser {
    public static void main(String args[]){
        // Later args will contain the filename
        // Documents/School/nand2tetris/nand2tetris/projects/10/ArrayTest/Main.jack
        List<String> tokens;
        Tokenizer tok = new Tokenizer();
        tokens = tok.ReadDocument("/home/zbcmakar/Documents/School/nand2tetris/nand2tetris/projects/10/ArrayTest/Main.jack");
        ProgramStructure pro = new ProgramStructure();
        pro.ReadDocument(tokens);
    }
}
