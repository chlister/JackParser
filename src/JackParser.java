public class JackParser {
    public static void main(String args[]){
        // Later args will contain the filename
        // Documents/School/nand2tetris/nand2tetris/projects/10/ArrayTest/Main.jack
        StringBuilder tokens;
        Tokenizer tok = new Tokenizer();
        tokens = tok.ReadDocument("/home/zbcmakar/Documents/School/nand2tetris/nand2tetris/projects/10/ArrayTest/Main.jack");

    }
}
