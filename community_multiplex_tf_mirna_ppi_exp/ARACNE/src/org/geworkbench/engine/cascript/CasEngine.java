package org.geworkbench.engine.cascript;

import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.debug.misc.ASTFrame;

import java.io.DataInputStream;
import java.io.StringBufferInputStream;

/**
 * @author Behrooz Badii
 * @author John Watkinson
 */
public class CasEngine {

    public static void runScript(String scriptText) throws TokenStreamException, RecognitionException {
        DataInputStream input = new DataInputStream(new StringBufferInputStream(scriptText));
        // Create the lexer and parser and feed them the input
        CASLexer lexer = new CASLexer(input);
        if (lexer.nr_error > 0) {
            lexer.sbe.deleteCharAt(lexer.sbe.length()-1);
            throw new CasException("\nLexing errors:\n" + lexer.sbe.toString());
        } 
        CASParser parser = new CASParser(lexer);
        parser.program(); // "file" is the main rule in the parser
        //if there are parsing errors, do not interpret
        if ( parser.nr_error > 0 ) {
            parser.sbe.deleteCharAt(parser.sbe.length()-1);
            throw new CasException("\nParsing errors:\n" +parser.sbe.toString());
        }
        // Get the AST from the parser
        CommonAST parseTree = (CommonAST) parser.getAST();

        // Print the AST in a human-readable format
        //System.out.println(parseTree.toStringList());

        // Open a window in which the AST is displayed graphically
        ASTFrame frame = new ASTFrame("AST from the CAS parser", parseTree);
        frame.setVisible(false);

        //walk this tree for interpretation
        CASWalker walk = new CASWalker();
        walk.walkme(parseTree);

    }
}
