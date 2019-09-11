package ProgramStructure;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class VMWriter {
    private FileWriter fw;

    /**
     * Creates a new file and prepares it for writing
     *
     * @param outPutFile -> File
     */
    public VMWriter(File outPutFile) {
        try {
            fw = new FileWriter(outPutFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a VM push command
     *
     * @param segment
     */
    public void writePush(String segment, int nIndex) {
        if (segment.equals("var")) {
            segment = "local";
        }
        if (segment.equals("field")) {
            segment = "this";
        }
        try {
            fw.write("push " + segment + " " + nIndex + "\n");
            System.out.print("push " + segment + " " + nIndex + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes a VM pop command
     *
     * @param segment
     */
    public void writePop(String segment, int nIndex) {
        if (segment.equals("var")) {
            segment = "local";
        }
        if (segment.equals("field")) {
            segment = "this";
        }
        try {
            fw.write("pop " + segment + " " + nIndex + "\n");
            System.out.print("pop " + segment + " " + nIndex + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes a VM arithmatic command
     *
     * @param command
     */
    public void writeArithmetic(String command) {
        try {
            fw.write(command + "\n");
            System.out.print(command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes a VM label
     *
     * @param label
     */
    public void writeLabel(String label) {
        try {
            fw.write("label " + label + "\n");
            System.out.print("label " + label + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes a VM goto command
     *
     * @param label
     */
    public void writeGoto(String label) {
        try {
            fw.write("goto " + label + "\n");
            System.out.print("goto " + label + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes a VM if-goto command
     *
     * @param label
     */
    public void writeIf(String label) {
        try {
            fw.write("if-goto " + label + "\n");
            System.out.print("if-goto " + label + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes a VM call command
     *
     * @param name
     * @param nArgs
     */
    public void writeCall(String name, int nArgs) {
        try {
            fw.write("call " + name + " " + nArgs + "\n");
            System.out.print("call " + name + " " + nArgs + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes a VM function command
     *
     * @param name
     * @param nLocals
     */
    public void writeFunction(String name, int nLocals) {
        try {
            fw.write("function " + name + " " + nLocals + "\n");
            System.out.print("function " + name + " " + nLocals + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Writes a VM return command
     */
    public void writeReturn() {
        try {
            fw.write("return\n");
            System.out.print("return\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Closes the output file
     */
    public void close() {
        try {
            fw.close();
            System.out.print("Closing...");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
