package error_pack;

/**
 * Custom exceptions for the Jack compiler
 */
public class JackCompilerException extends Exception {
    public JackCompilerException(String msg, Throwable e){super(msg, e);}
}
