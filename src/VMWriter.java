import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class VMWriter {
    private FileWriter fw;

    /**
     * Creates a new file and prepares it for writing
     * @param outPutFile -> File
     */
    public VMWriter(File outPutFile){
        try {
            fw = new FileWriter(outPutFile);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM push command
     * @param segment
     */
    public void writePush(String segment){

    }

    /**
     * Writes a VM pop command
     * @param segment
     */
    public void writePop(String segment) {

    }

    /**
     * Writes a VM arithmatic command
     * @param command
     */
    public void writeArithmetic(String command) {

    }

    /**
     * Writes a VM label
     * @param label
     */
    public void writeLabel(String label) {

    }

    /**
     * Writes a VM goto command
     * @param label
     */
    public void writeGoto(String label) {

    }

    /**
     * Writes a VM if-goto command
     * @param label
     */
    public void writeIf(String label) {

    }

    /**
     * Writes a VM call command
     * @param name
     * @param nLocals
     */
    public void writeCall(String name, int nLocals){

    }

    /**
     * Writes a VM function command
     * @param name
     * @param nLocals
     */
    public void writeFunction(String name, int nLocals) {

    }

    /**
     * Writes a VM return command
     */
    public void writeReturn() {

    }

    /**
     * Closes the output file
     */
    public void close(){

    }
}
