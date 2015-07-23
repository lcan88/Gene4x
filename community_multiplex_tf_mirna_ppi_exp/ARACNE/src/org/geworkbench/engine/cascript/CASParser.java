// $ANTLR 2.7.5 (20050128): "CAS.g" -> "CASParser.java"$
package org.geworkbench.engine.cascript;import antlr.*;
import antlr.collections.AST;
import antlr.collections.impl.ASTArray;
import antlr.collections.impl.BitSet;

public class CASParser extends antlr.LLkParser       implements CAStokensTokenTypes
 {

    int nr_error = 0;
    StringBuilder sbe = new StringBuilder();
    /** Parser error-reporting function can be overridden in subclass */
    public void reportError(RecognitionException ex) {
        sbe.append(ex.toString() + "\n");
        nr_error++;
    }

    /** Parser error-reporting function can be overridden in subclass */
    public void reportError(String s) {
        //if (nr_error == 0) {
        //  setPs();
        //}
        if (getFilename() == null) {
            sbe.append(s + "\n");
        }
        else {
            sbe.append(s + "\n");
        }
        nr_error++;
    }

protected CASParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public CASParser(TokenBuffer tokenBuf) {
  this(tokenBuf,4);
}

protected CASParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public CASParser(TokenStream lexer) {
  this(lexer,4);
}

public CASParser(ParserSharedInputState state) {
  super(state,4);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

/**
 * program starts here
*/
	public final void program() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST program_AST = null;
		
		try {      // for error handling
			{
			_loop72:
			do {
				if ((LA(1)==PUBLIC)) {
					pubVarDecl();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop72;
				}
				
			} while (true);
			}
			{
			int _cnt74=0;
			_loop74:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					funcDecl();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt74>=1 ) { break _loop74; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt74++;
			} while (true);
			}
			match(Token.EOF_TYPE);
			program_AST = (AST)currentAST.root;
			program_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PROG,"PROG")).add(program_AST));
			currentAST.root = program_AST;
			currentAST.child = program_AST!=null &&program_AST.getFirstChild()!=null ?
				program_AST.getFirstChild() : program_AST;
			currentAST.advanceChildToEnd();
			program_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = program_AST;
	}
	
/**
 * public variable declaration
*/
	public final void pubVarDecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pubVarDecl_AST = null;
		
		try {      // for error handling
			match(PUBLIC);
			declareStmt();
			astFactory.addASTChild(currentAST, returnAST);
			pubVarDecl_AST = (AST)currentAST.root;
			pubVarDecl_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PUBLICVAR,"PUBLICVAR")).add(pubVarDecl_AST));
			currentAST.root = pubVarDecl_AST;
			currentAST.child = pubVarDecl_AST!=null &&pubVarDecl_AST.getFirstChild()!=null ?
				pubVarDecl_AST.getFirstChild() : pubVarDecl_AST;
			currentAST.advanceChildToEnd();
			pubVarDecl_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		returnAST = pubVarDecl_AST;
	}
	
/**
 * function declaration token
*/
	public final void funcDecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST funcDecl_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case INT:
			case FLOAT:
			case BOOLSTR:
			case STRING:
			case MODULE:
			case DATATYPE:
			{
				type();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop79:
				do {
					if ((LA(1)==LEFTBRACKET)) {
						AST tmp79_AST = null;
						tmp79_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp79_AST);
						match(LEFTBRACKET);
						AST tmp80_AST = null;
						tmp80_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp80_AST);
						match(RIGHTBRACKET);
					}
					else {
						break _loop79;
					}
					
				} while (true);
				}
				break;
			}
			case VOID:
			{
				isvoid();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp81_AST = null;
			tmp81_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp81_AST);
			match(ID);
			argDeclarationList();
			astFactory.addASTChild(currentAST, returnAST);
			functionbody();
			astFactory.addASTChild(currentAST, returnAST);
			funcDecl_AST = (AST)currentAST.root;
			funcDecl_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FUNCTION,"FUNCTION")).add(funcDecl_AST));
			currentAST.root = funcDecl_AST;
			currentAST.child = funcDecl_AST!=null &&funcDecl_AST.getFirstChild()!=null ?
				funcDecl_AST.getFirstChild() : funcDecl_AST;
			currentAST.advanceChildToEnd();
			funcDecl_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = funcDecl_AST;
	}
	
/** 
 * variable declaration statement
*/
	public final void declareStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declareStmt_AST = null;
		
		try {      // for error handling
			declaration();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			declareStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		returnAST = declareStmt_AST;
	}
	
/**
 * the types found in CAScript
*/
	public final void type() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case INT:
			{
				AST tmp83_AST = null;
				tmp83_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp83_AST);
				match(INT);
				break;
			}
			case FLOAT:
			{
				AST tmp84_AST = null;
				tmp84_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp84_AST);
				match(FLOAT);
				break;
			}
			case BOOLSTR:
			{
				AST tmp85_AST = null;
				tmp85_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp85_AST);
				match(BOOLSTR);
				break;
			}
			case STRING:
			{
				AST tmp86_AST = null;
				tmp86_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp86_AST);
				match(STRING);
				break;
			}
			case MODULE:
			{
				{
				AST tmp87_AST = null;
				tmp87_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp87_AST);
				match(MODULE);
				AST tmp88_AST = null;
				tmp88_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp88_AST);
				match(ID);
				}
				break;
			}
			case DATATYPE:
			{
				{
				AST tmp89_AST = null;
				tmp89_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp89_AST);
				match(DATATYPE);
				AST tmp90_AST = null;
				tmp90_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp90_AST);
				match(ID);
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			type_AST = (AST)currentAST.root;
			type_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(TYPE,"TYPE")).add(type_AST));
			currentAST.root = type_AST;
			currentAST.child = type_AST!=null &&type_AST.getFirstChild()!=null ?
				type_AST.getFirstChild() : type_AST;
			currentAST.advanceChildToEnd();
			type_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		returnAST = type_AST;
	}
	
/**
 * void is a possible type for methods
*/
	public final void isvoid() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST isvoid_AST = null;
		
		try {      // for error handling
			AST tmp91_AST = null;
			tmp91_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp91_AST);
			match(VOID);
			isvoid_AST = (AST)currentAST.root;
			isvoid_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(TYPE,"TYPE")).add(isvoid_AST));
			currentAST.root = isvoid_AST;
			currentAST.child = isvoid_AST!=null &&isvoid_AST.getFirstChild()!=null ?
				isvoid_AST.getFirstChild() : isvoid_AST;
			currentAST.advanceChildToEnd();
			isvoid_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		returnAST = isvoid_AST;
	}
	
/**
 * argDeclarationList, or argument declaration list:
 * you have a left parenthesis, and then sets of types, IDs, and possible opening and closing left and right brackets
 * with sets separated by commas, then a right parenthesis
 * example: (int i[][], int j, module genePanel gpan, double k)
*/
	public final void argDeclarationList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argDeclarationList_AST = null;
		
		try {      // for error handling
			match(LEFTPAREN);
			{
			switch ( LA(1)) {
			case INT:
			case FLOAT:
			case BOOLSTR:
			case STRING:
			case MODULE:
			case DATATYPE:
			{
				type();
				astFactory.addASTChild(currentAST, returnAST);
				AST tmp93_AST = null;
				tmp93_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp93_AST);
				match(ID);
				{
				_loop166:
				do {
					if ((LA(1)==LEFTBRACKET)) {
						AST tmp94_AST = null;
						tmp94_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp94_AST);
						match(LEFTBRACKET);
						AST tmp95_AST = null;
						tmp95_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp95_AST);
						match(RIGHTBRACKET);
					}
					else {
						break _loop166;
					}
					
				} while (true);
				}
				{
				_loop170:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						type();
						astFactory.addASTChild(currentAST, returnAST);
						AST tmp97_AST = null;
						tmp97_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp97_AST);
						match(ID);
						{
						_loop169:
						do {
							if ((LA(1)==LEFTBRACKET)) {
								AST tmp98_AST = null;
								tmp98_AST = astFactory.create(LT(1));
								astFactory.addASTChild(currentAST, tmp98_AST);
								match(LEFTBRACKET);
								AST tmp99_AST = null;
								tmp99_AST = astFactory.create(LT(1));
								astFactory.addASTChild(currentAST, tmp99_AST);
								match(RIGHTBRACKET);
							}
							else {
								break _loop169;
							}
							
						} while (true);
						}
					}
					else {
						break _loop170;
					}
					
				} while (true);
				}
				break;
			}
			case RIGHTPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RIGHTPAREN);
			argDeclarationList_AST = (AST)currentAST.root;
			argDeclarationList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGDEC,"ARGDEC")).add(argDeclarationList_AST));
			currentAST.root = argDeclarationList_AST;
			currentAST.child = argDeclarationList_AST!=null &&argDeclarationList_AST.getFirstChild()!=null ?
				argDeclarationList_AST.getFirstChild() : argDeclarationList_AST;
			currentAST.advanceChildToEnd();
			argDeclarationList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = argDeclarationList_AST;
	}
	
/**
 * function body; this is what you find in the braces
*/
	public final void functionbody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionbody_AST = null;
		
		try {      // for error handling
			bracestatement();
			astFactory.addASTChild(currentAST, returnAST);
			functionbody_AST = (AST)currentAST.root;
			functionbody_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FUNCTIONBODY,"FUNCTIONBODY")).add(functionbody_AST));
			currentAST.root = functionbody_AST;
			currentAST.child = functionbody_AST!=null &&functionbody_AST.getFirstChild()!=null ?
				functionbody_AST.getFirstChild() : functionbody_AST;
			currentAST.advanceChildToEnd();
			functionbody_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = functionbody_AST;
	}
	
/**
 * brace statement: can be either a single statement of a set of statements in braces
*/
	public final void bracestatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bracestatement_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LEFTBRACE:
			{
				{
				match(LEFTBRACE);
				{
				int _cnt96=0;
				_loop96:
				do {
					if ((_tokenSet_8.member(LA(1)))) {
						statement();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt96>=1 ) { break _loop96; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt96++;
				} while (true);
				}
				match(RIGHTBRACE);
				}
				bracestatement_AST = (AST)currentAST.root;
				bracestatement_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(STATEMENTS,"STATEMENTS")).add(bracestatement_AST));
				currentAST.root = bracestatement_AST;
				currentAST.child = bracestatement_AST!=null &&bracestatement_AST.getFirstChild()!=null ?
					bracestatement_AST.getFirstChild() : bracestatement_AST;
				currentAST.advanceChildToEnd();
				bracestatement_AST = (AST)currentAST.root;
				break;
			}
			case INT:
			case FLOAT:
			case BOOLSTR:
			case STRING:
			case MODULE:
			case DATATYPE:
			case IFSTR:
			case WHILESTR:
			case FORSTR:
			case RETURNSTR:
			case TRUE:
			case FALSE:
			case BREAK:
			case CONTINUE:
			case WAIT:
			case NEW:
			case PRINT:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case NOT:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case LEFTBRACKET:
			case ID:
			case String:
			{
				statement();
				astFactory.addASTChild(currentAST, returnAST);
				bracestatement_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = bracestatement_AST;
	}
	
/**
 * list of types of statements
*/
	public final void statement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case NOT:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case LEFTBRACKET:
			case ID:
			case String:
			{
				evaluateStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case BREAK:
			{
				breakStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case CONTINUE:
			{
				continueStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case WHILESTR:
			{
				whileStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case IFSTR:
			{
				ifStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case INT:
			case FLOAT:
			case BOOLSTR:
			case STRING:
			case MODULE:
			case DATATYPE:
			{
				declareStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case RETURNSTR:
			{
				returnStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case FORSTR:
			{
				forstatement();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case WAIT:
			{
				waitStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case PRINT:
			{
				printStmt();
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = statement_AST;
	}
	
/**
 * evaluation statement, only returns booleans
*/
	public final void evaluateStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST evaluateStmt_AST = null;
		
		try {      // for error handling
			evaluate();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			evaluateStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = evaluateStmt_AST;
	}
	
/**
 * break statement
*/
	public final void breakStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST breakStmt_AST = null;
		
		try {      // for error handling
			AST tmp104_AST = null;
			tmp104_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp104_AST);
			match(BREAK);
			match(SEMICOLON);
			breakStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = breakStmt_AST;
	}
	
/**
 * continue statement
*/
	public final void continueStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST continueStmt_AST = null;
		
		try {      // for error handling
			AST tmp106_AST = null;
			tmp106_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp106_AST);
			match(CONTINUE);
			match(SEMICOLON);
			continueStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = continueStmt_AST;
	}
	
/**
 * while loop statements with the evaluation and the following brace statement
*/
	public final void whileStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whileStmt_AST = null;
		
		try {      // for error handling
			AST tmp108_AST = null;
			tmp108_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp108_AST);
			match(WHILESTR);
			expressionStmt();
			astFactory.addASTChild(currentAST, returnAST);
			bracestatement();
			astFactory.addASTChild(currentAST, returnAST);
			whileStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = whileStmt_AST;
	}
	
/**
 * while loop statements with the evaluation, the if body, and an else statement
*/
	public final void ifStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ifStmt_AST = null;
		
		try {      // for error handling
			AST tmp109_AST = null;
			tmp109_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp109_AST);
			match(IFSTR);
			expressionStmt();
			astFactory.addASTChild(currentAST, returnAST);
			ifStmtBody();
			astFactory.addASTChild(currentAST, returnAST);
			elseStmt();
			astFactory.addASTChild(currentAST, returnAST);
			ifStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = ifStmt_AST;
	}
	
/**
 * return statement with an evaluation
*/
	public final void returnStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST returnStmt_AST = null;
		
		try {      // for error handling
			AST tmp110_AST = null;
			tmp110_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp110_AST);
			match(RETURNSTR);
			evaluate();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			returnStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = returnStmt_AST;
	}
	
/**
 * for loop composed of 4 pieces - the <1>;<2>;<3>; { <4> }
*/
	public final void forstatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forstatement_AST = null;
		
		try {      // for error handling
			AST tmp112_AST = null;
			tmp112_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp112_AST);
			match(FORSTR);
			forLeft();
			astFactory.addASTChild(currentAST, returnAST);
			forMid();
			astFactory.addASTChild(currentAST, returnAST);
			forRight();
			astFactory.addASTChild(currentAST, returnAST);
			forBody();
			astFactory.addASTChild(currentAST, returnAST);
			forstatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = forstatement_AST;
	}
	
/**
 * simple wait statement
*/
	public final void waitStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST waitStmt_AST = null;
		
		try {      // for error handling
			AST tmp113_AST = null;
			tmp113_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp113_AST);
			match(WAIT);
			eval();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			waitStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = waitStmt_AST;
	}
	
/**
 * simple print statement
*/
	public final void printStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST printStmt_AST = null;
		
		try {      // for error handling
			AST tmp115_AST = null;
			tmp115_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp115_AST);
			match(PRINT);
			eval();
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			printStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = printStmt_AST;
	}
	
/**
 * eval: (evalTerm [+ or -] evalTerm)*
 * we have to be greedy to make sure pluses and minuses are not taken as positive and negative signs for numbers
 * numbers and integers start showing at eval
*/
	public final void eval() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST eval_AST = null;
		
		try {      // for error handling
			evalTerm();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop144:
			do {
				if ((LA(1)==PLUS||LA(1)==MINUS)) {
					{
					{
					switch ( LA(1)) {
					case PLUS:
					{
						AST tmp117_AST = null;
						tmp117_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp117_AST);
						match(PLUS);
						break;
					}
					case MINUS:
					{
						AST tmp118_AST = null;
						tmp118_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp118_AST);
						match(MINUS);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					}
					evalTerm();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop144;
				}
				
			} while (true);
			}
			eval_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		returnAST = eval_AST;
	}
	
/**
 * first part of a for loop, generally the variable initialization
*/
	public final void forLeft() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forLeft_AST = null;
		
		try {      // for error handling
			match(LEFTPAREN);
			{
			switch ( LA(1)) {
			case INT:
			case FLOAT:
			case BOOLSTR:
			case STRING:
			case MODULE:
			case DATATYPE:
			{
				declaration();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case NOT:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case LEFTBRACKET:
			case ID:
			case String:
			{
				evaluate();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			forLeft_AST = (AST)currentAST.root;
			forLeft_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FORLEFT,"FORLEFT")).add(forLeft_AST));
			currentAST.root = forLeft_AST;
			currentAST.child = forLeft_AST!=null &&forLeft_AST.getFirstChild()!=null ?
				forLeft_AST.getFirstChild() : forLeft_AST;
			currentAST.advanceChildToEnd();
			forLeft_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = forLeft_AST;
	}
	
/**
 * second part of a for loop, the evaluation
*/
	public final void forMid() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forMid_AST = null;
		
		try {      // for error handling
			match(SEMICOLON);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			forMid_AST = (AST)currentAST.root;
			forMid_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FORMID,"FORMID")).add(forMid_AST));
			currentAST.root = forMid_AST;
			currentAST.child = forMid_AST!=null &&forMid_AST.getFirstChild()!=null ?
				forMid_AST.getFirstChild() : forMid_AST;
			currentAST.advanceChildToEnd();
			forMid_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = forMid_AST;
	}
	
/**
 * third part of a for loop, generally the incrementation
*/
	public final void forRight() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forRight_AST = null;
		
		try {      // for error handling
			match(SEMICOLON);
			evaluate();
			astFactory.addASTChild(currentAST, returnAST);
			match(RIGHTPAREN);
			forRight_AST = (AST)currentAST.root;
			forRight_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FORRIGHT,"FORRIGHT")).add(forRight_AST));
			currentAST.root = forRight_AST;
			currentAST.child = forRight_AST!=null &&forRight_AST.getFirstChild()!=null ?
				forRight_AST.getFirstChild() : forRight_AST;
			currentAST.advanceChildToEnd();
			forRight_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = forRight_AST;
	}
	
/**
 * fourth part of a for loop, the body
*/
	public final void forBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST forBody_AST = null;
		
		try {      // for error handling
			bracestatement();
			astFactory.addASTChild(currentAST, returnAST);
			forBody_AST = (AST)currentAST.root;
			forBody_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FORBODY,"FORBODY")).add(forBody_AST));
			currentAST.root = forBody_AST;
			currentAST.child = forBody_AST!=null &&forBody_AST.getFirstChild()!=null ?
				forBody_AST.getFirstChild() : forBody_AST;
			currentAST.advanceChildToEnd();
			forBody_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = forBody_AST;
	}
	
/**
 *  variable declaration including its type, its identifier, and a third token called declareType
*/
	public final void declaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaration_AST = null;
		
		try {      // for error handling
			type();
			astFactory.addASTChild(currentAST, returnAST);
			id();
			astFactory.addASTChild(currentAST, returnAST);
			declareType();
			astFactory.addASTChild(currentAST, returnAST);
			declaration_AST = (AST)currentAST.root;
			declaration_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(VARIABLE,"VARIABLE")).add(declaration_AST));
			currentAST.root = declaration_AST;
			currentAST.child = declaration_AST!=null &&declaration_AST.getFirstChild()!=null ?
				declaration_AST.getFirstChild() : declaration_AST;
			currentAST.advanceChildToEnd();
			declaration_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = declaration_AST;
	}
	
/**
 * evaluation statement including assignment
*/
	public final void evaluate() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST evaluate_AST = null;
		
		try {      // for error handling
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case EQUAL:
			{
				match(EQUAL);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				evaluate_AST = (AST)currentAST.root;
				evaluate_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ASSIGNMENT,"ASSIGNMENT")).add(evaluate_AST));
				currentAST.root = evaluate_AST;
				currentAST.child = evaluate_AST!=null &&evaluate_AST.getFirstChild()!=null ?
					evaluate_AST.getFirstChild() : evaluate_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case SEMICOLON:
			case RIGHTPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			evaluate_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = evaluate_AST;
	}
	
/**
 * precedence is set all the way down to an atom, starting here:
 * expression: (compare || compare)*
*/
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		try {      // for error handling
			compare();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop130:
			do {
				if ((LA(1)==OR)) {
					AST tmp124_AST = null;
					tmp124_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp124_AST);
					match(OR);
					compare();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop130;
				}
				
			} while (true);
			}
			expression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
		}
		returnAST = expression_AST;
	}
	
/**
 * condition for the while loop and the if conditional
*/
	public final void expressionStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expressionStmt_AST = null;
		
		try {      // for error handling
			match(LEFTPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RIGHTPAREN);
			expressionStmt_AST = (AST)currentAST.root;
			expressionStmt_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CONDITION,"CONDITION")).add(expressionStmt_AST));
			currentAST.root = expressionStmt_AST;
			currentAST.child = expressionStmt_AST!=null &&expressionStmt_AST.getFirstChild()!=null ?
				expressionStmt_AST.getFirstChild() : expressionStmt_AST;
			currentAST.advanceChildToEnd();
			expressionStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = expressionStmt_AST;
	}
	
/**
 * if body is just a brace statement
*/
	public final void ifStmtBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ifStmtBody_AST = null;
		
		try {      // for error handling
			bracestatement();
			astFactory.addASTChild(currentAST, returnAST);
			ifStmtBody_AST = (AST)currentAST.root;
			ifStmtBody_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(THEN,"THEN")).add(ifStmtBody_AST));
			currentAST.root = ifStmtBody_AST;
			currentAST.child = ifStmtBody_AST!=null &&ifStmtBody_AST.getFirstChild()!=null ?
				ifStmtBody_AST.getFirstChild() : ifStmtBody_AST;
			currentAST.advanceChildToEnd();
			ifStmtBody_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = ifStmtBody_AST;
	}
	
/**
 * else statement, if it exists, notice the use of options {greedy = true;}
*/
	public final void elseStmt() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST elseStmt_AST = null;
		
		try {      // for error handling
			{
			if ((LA(1)==ELSE) && (_tokenSet_7.member(LA(2))) && (_tokenSet_14.member(LA(3))) && (_tokenSet_15.member(LA(4)))) {
				AST tmp127_AST = null;
				tmp127_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp127_AST);
				match(ELSE);
				bracestatement();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((_tokenSet_9.member(LA(1))) && (_tokenSet_16.member(LA(2))) && (_tokenSet_17.member(LA(3))) && (_tokenSet_17.member(LA(4)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			elseStmt_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = elseStmt_AST;
	}
	
/**
 * id is an identifier followed by an index.  If the index has a value, we're dealing with an array.
 * If the index is empty, we're dealing with just a plain variable.
*/
	public final void id() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST id_AST = null;
		
		try {      // for error handling
			AST tmp128_AST = null;
			tmp128_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp128_AST);
			match(ID);
			index();
			astFactory.addASTChild(currentAST, returnAST);
			id_AST = (AST)currentAST.root;
			id_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(IDENTIFIER,"IDENTIFIER")).add(id_AST));
			currentAST.root = id_AST;
			currentAST.child = id_AST!=null &&id_AST.getFirstChild()!=null ?
				id_AST.getFirstChild() : id_AST;
			currentAST.advanceChildToEnd();
			id_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = id_AST;
	}
	
/**
 * declareType, the third part of the rule declare, can assign values to a variable right off the bat, and
 * can list off more identifier with assignvalues aka int i = 0; j, k = 5;
*/
	public final void declareType() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declareType_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case EQUAL:
			{
				assignValue();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case COMMA:
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			_loop121:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					id();
					astFactory.addASTChild(currentAST, returnAST);
					{
					switch ( LA(1)) {
					case EQUAL:
					{
						assignValue();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case COMMA:
					case SEMICOLON:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
				}
				else {
					break _loop121;
				}
				
			} while (true);
			}
			declareType_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = declareType_AST;
	}
	
/**
 * caObj, this can be either a caValue of a caCall
*/
	public final void caObj() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caObj_AST = null;
		
		try {      // for error handling
			AST tmp130_AST = null;
			tmp130_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp130_AST);
			match(ID);
			match(PERIOD);
			AST tmp132_AST = null;
			tmp132_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp132_AST);
			match(ID);
			{
			switch ( LA(1)) {
			case LEFTPAREN:
			{
				argList();
				astFactory.addASTChild(currentAST, returnAST);
				caObj_AST = (AST)currentAST.root;
				caObj_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OBJECT_CALL,"OBJECT_CALL")).add(caObj_AST));
				currentAST.root = caObj_AST;
				currentAST.child = caObj_AST!=null &&caObj_AST.getFirstChild()!=null ?
					caObj_AST.getFirstChild() : caObj_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case COMMA:
			case SEMICOLON:
			case PLUS:
			case MINUS:
			case SLASH:
			case MODULO:
			case TIMES:
			case LESS:
			case LESSEQUAL:
			case MORE:
			case MOREEQUAL:
			case EQUAL:
			case EQUALTO:
			case OR:
			case AND:
			case NOTEQUAL:
			case PLUSPLUS:
			case MINUSMINUS:
			case RIGHTPAREN:
			case RIGHTBRACKET:
			{
				caObj_AST = (AST)currentAST.root;
				caObj_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OBJECT_VALUE,"OBJECT_VALUE")).add(caObj_AST));
				currentAST.root = caObj_AST;
				currentAST.child = caObj_AST!=null &&caObj_AST.getFirstChild()!=null ?
					caObj_AST.getFirstChild() : caObj_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			caObj_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = caObj_AST;
	}
	
/**
 * argList, or an argument list: 
 * you have a left parentheses, and then expressions separated by commas, then a right parantheses
 * example: (5, 9, true, "hello")
*/
	public final void argList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST argList_AST = null;
		
		try {      // for error handling
			match(LEFTPAREN);
			{
			switch ( LA(1)) {
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case NOT:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case LEFTBRACKET:
			case ID:
			case String:
			{
				{
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				}
				{
				_loop161:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						expression();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop161;
					}
					
				} while (true);
				}
				break;
			}
			case RIGHTPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RIGHTPAREN);
			argList_AST = (AST)currentAST.root;
			argList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ARGS,"ARGS")).add(argList_AST));
			currentAST.root = argList_AST;
			currentAST.child = argList_AST!=null &&argList_AST.getFirstChild()!=null ?
				argList_AST.getFirstChild() : argList_AST;
			currentAST.advanceChildToEnd();
			argList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_19);
		}
		returnAST = argList_AST;
	}
	
/**
 * caValue, AKA CasValue; this is what you use when you want to call a getDataSet, or setDataSet call
 * the first ID is the module name, the second ID is the name of the "variable", in our case DataSet
*/
	public final void caValue() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caValue_AST = null;
		
		try {      // for error handling
			AST tmp136_AST = null;
			tmp136_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp136_AST);
			match(ID);
			match(PERIOD);
			AST tmp138_AST = null;
			tmp138_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp138_AST);
			match(ID);
			caValue_AST = (AST)currentAST.root;
			caValue_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OBJECT_VALUE,"OBJECT_VALUE")).add(caValue_AST));
			currentAST.root = caValue_AST;
			currentAST.child = caValue_AST!=null &&caValue_AST.getFirstChild()!=null ?
				caValue_AST.getFirstChild() : caValue_AST;
			currentAST.advanceChildToEnd();
			caValue_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = caValue_AST;
	}
	
/**
 * caCall, AKA CasMethod; this is what you use to make a module call a method
 * the first ID is the module name, the second ID is the method name
*/
	public final void caCall() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caCall_AST = null;
		
		try {      // for error handling
			AST tmp139_AST = null;
			tmp139_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp139_AST);
			match(ID);
			match(PERIOD);
			AST tmp141_AST = null;
			tmp141_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp141_AST);
			match(ID);
			argList();
			astFactory.addASTChild(currentAST, returnAST);
			caCall_AST = (AST)currentAST.root;
			caCall_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OBJECT_CALL,"OBJECT_CALL")).add(caCall_AST));
			currentAST.root = caCall_AST;
			currentAST.child = caCall_AST!=null &&caCall_AST.getFirstChild()!=null ?
				caCall_AST.getFirstChild() : caCall_AST;
			currentAST.advanceChildToEnd();
			caCall_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = caCall_AST;
	}
	
/**
 * index shows us in the tree if we have an array or not
*/
	public final void index() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST index_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LEFTBRACKET:
			{
				match(LEFTBRACKET);
				indexValue();
				astFactory.addASTChild(currentAST, returnAST);
				match(RIGHTBRACKET);
				indexTail();
				astFactory.addASTChild(currentAST, returnAST);
				index_AST = (AST)currentAST.root;
				index_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INDEX,"INDEX")).add(index_AST));
				currentAST.root = index_AST;
				currentAST.child = index_AST!=null &&index_AST.getFirstChild()!=null ?
					index_AST.getFirstChild() : index_AST;
				currentAST.advanceChildToEnd();
				index_AST = (AST)currentAST.root;
				break;
			}
			case COMMA:
			case SEMICOLON:
			case PLUS:
			case MINUS:
			case SLASH:
			case MODULO:
			case TIMES:
			case LESS:
			case LESSEQUAL:
			case MORE:
			case MOREEQUAL:
			case EQUAL:
			case EQUALTO:
			case OR:
			case AND:
			case NOTEQUAL:
			case PLUSPLUS:
			case MINUSMINUS:
			case RIGHTPAREN:
			case RIGHTBRACKET:
			{
				index_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = index_AST;
	}
	
/**
 * an indexValue, value found inside brackets denoting placement in an array, must be an integer
*/
	public final void indexValue() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST indexValue_AST = null;
		
		try {      // for error handling
			eval();
			astFactory.addASTChild(currentAST, returnAST);
			indexValue_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		returnAST = indexValue_AST;
	}
	
/**
 * if we have an indextail, then we have a matrix
*/
	public final void indexTail() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST indexTail_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LEFTBRACKET:
			{
				match(LEFTBRACKET);
				indexValue();
				astFactory.addASTChild(currentAST, returnAST);
				match(RIGHTBRACKET);
				break;
			}
			case COMMA:
			case SEMICOLON:
			case PLUS:
			case MINUS:
			case SLASH:
			case MODULO:
			case TIMES:
			case LESS:
			case LESSEQUAL:
			case MORE:
			case MOREEQUAL:
			case EQUAL:
			case EQUALTO:
			case OR:
			case AND:
			case NOTEQUAL:
			case PLUSPLUS:
			case MINUSMINUS:
			case RIGHTPAREN:
			case RIGHTBRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			indexTail_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = indexTail_AST;
	}
	
/**
 * rule found under declare type that holds the value being assigned to an identifier
*/
	public final void assignValue() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignValue_AST = null;
		
		try {      // for error handling
			AST tmp146_AST = null;
			tmp146_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp146_AST);
			match(EQUAL);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			assignValue_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_21);
		}
		returnAST = assignValue_AST;
	}
	
/**
 * compare: (inverse && inverse)*
 * 
*/
	public final void compare() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compare_AST = null;
		
		try {      // for error handling
			inverse();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop133:
			do {
				if ((LA(1)==AND)) {
					AST tmp147_AST = null;
					tmp147_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp147_AST);
					match(AND);
					inverse();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop133;
				}
				
			} while (true);
			}
			compare_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_22);
		}
		returnAST = compare_AST;
	}
	
/**
 * inverse: !compareTo
*/
	public final void inverse() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inverse_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case NOT:
			{
				AST tmp148_AST = null;
				tmp148_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp148_AST);
				match(NOT);
				break;
			}
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case LEFTBRACKET:
			case ID:
			case String:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			compareTo();
			astFactory.addASTChild(currentAST, returnAST);
			inverse_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
		returnAST = inverse_AST;
	}
	
/**
 * compareTo: (eval [< or <= or > or >= or == or !=] eval)*
*/
	public final void compareTo() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compareTo_AST = null;
		
		try {      // for error handling
			eval();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop139:
			do {
				if ((_tokenSet_24.member(LA(1)))) {
					{
					switch ( LA(1)) {
					case LESS:
					{
						AST tmp149_AST = null;
						tmp149_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp149_AST);
						match(LESS);
						break;
					}
					case LESSEQUAL:
					{
						AST tmp150_AST = null;
						tmp150_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp150_AST);
						match(LESSEQUAL);
						break;
					}
					case MORE:
					{
						AST tmp151_AST = null;
						tmp151_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp151_AST);
						match(MORE);
						break;
					}
					case MOREEQUAL:
					{
						AST tmp152_AST = null;
						tmp152_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp152_AST);
						match(MOREEQUAL);
						break;
					}
					case EQUALTO:
					{
						AST tmp153_AST = null;
						tmp153_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp153_AST);
						match(EQUALTO);
						break;
					}
					case NOTEQUAL:
					{
						AST tmp154_AST = null;
						tmp154_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp154_AST);
						match(NOTEQUAL);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					eval();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop139;
				}
				
			} while (true);
			}
			compareTo_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
		returnAST = compareTo_AST;
	}
	
/**
 * evalTerm: (negate [* or / or &] negate)*
*/
	public final void evalTerm() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST evalTerm_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case MINUS:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case ID:
			case String:
			{
				negate();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop148:
				do {
					if (((LA(1) >= SLASH && LA(1) <= TIMES))) {
						{
						switch ( LA(1)) {
						case TIMES:
						{
							AST tmp155_AST = null;
							tmp155_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp155_AST);
							match(TIMES);
							break;
						}
						case SLASH:
						{
							AST tmp156_AST = null;
							tmp156_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp156_AST);
							match(SLASH);
							break;
						}
						case MODULO:
						{
							AST tmp157_AST = null;
							tmp157_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp157_AST);
							match(MODULO);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						negate();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop148;
					}
					
				} while (true);
				}
				evalTerm_AST = (AST)currentAST.root;
				break;
			}
			case LEFTBRACKET:
			{
				match(LEFTBRACKET);
				negate();
				astFactory.addASTChild(currentAST, returnAST);
				match(RIGHTBRACKET);
				evalTerm_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_25);
		}
		returnAST = evalTerm_AST;
	}
	
/**
 * negate: -afteratom
*/
	public final void negate() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST negate_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MINUS:
			{
				{
				match(MINUS);
				afteratom();
				astFactory.addASTChild(currentAST, returnAST);
				negate_AST = (AST)currentAST.root;
				negate_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(NEGATION,"NEGATION")).add(negate_AST));
				currentAST.root = negate_AST;
				currentAST.child = negate_AST!=null &&negate_AST.getFirstChild()!=null ?
					negate_AST.getFirstChild() : negate_AST;
				currentAST.advanceChildToEnd();
				}
				negate_AST = (AST)currentAST.root;
				break;
			}
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case PLUSPLUS:
			case MINUSMINUS:
			case LEFTPAREN:
			case ID:
			case String:
			{
				afteratom();
				astFactory.addASTChild(currentAST, returnAST);
				negate_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_26);
		}
		returnAST = negate_AST;
	}
	
/**
 * afteratom: beforeatom or beforeatom++ or beforeatom--
 * incrementation and decrementation before and after a value was tricky
*/
	public final void afteratom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST afteratom_AST = null;
		
		try {      // for error handling
			beforeatom();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case PLUSPLUS:
			{
				match(PLUSPLUS);
				afteratom_AST = (AST)currentAST.root;
				afteratom_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INCAFTER,"INCAFTER")).add(afteratom_AST));
				currentAST.root = afteratom_AST;
				currentAST.child = afteratom_AST!=null &&afteratom_AST.getFirstChild()!=null ?
					afteratom_AST.getFirstChild() : afteratom_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case MINUSMINUS:
			{
				match(MINUSMINUS);
				afteratom_AST = (AST)currentAST.root;
				afteratom_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(DECAFTER,"DECAFTER")).add(afteratom_AST));
				currentAST.root = afteratom_AST;
				currentAST.child = afteratom_AST!=null &&afteratom_AST.getFirstChild()!=null ?
					afteratom_AST.getFirstChild() : afteratom_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case COMMA:
			case SEMICOLON:
			case PLUS:
			case MINUS:
			case SLASH:
			case MODULO:
			case TIMES:
			case LESS:
			case LESSEQUAL:
			case MORE:
			case MOREEQUAL:
			case EQUAL:
			case EQUALTO:
			case OR:
			case AND:
			case NOTEQUAL:
			case RIGHTPAREN:
			case RIGHTBRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			afteratom_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_26);
		}
		returnAST = afteratom_AST;
	}
	
/**
 * before: atom or --beforeatom or ++beforeatom
 * incrementation and decrementation before and after a value was tricky
*/
	public final void beforeatom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST beforeatom_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case PLUSPLUS:
			{
				match(PLUSPLUS);
				atom();
				astFactory.addASTChild(currentAST, returnAST);
				beforeatom_AST = (AST)currentAST.root;
				beforeatom_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INCBEFORE,"INCBEFORE")).add(beforeatom_AST));
				currentAST.root = beforeatom_AST;
				currentAST.child = beforeatom_AST!=null &&beforeatom_AST.getFirstChild()!=null ?
					beforeatom_AST.getFirstChild() : beforeatom_AST;
				currentAST.advanceChildToEnd();
				beforeatom_AST = (AST)currentAST.root;
				break;
			}
			case MINUSMINUS:
			{
				match(MINUSMINUS);
				atom();
				astFactory.addASTChild(currentAST, returnAST);
				beforeatom_AST = (AST)currentAST.root;
				beforeatom_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(DECBEFORE,"DECBEFORE")).add(beforeatom_AST));
				currentAST.root = beforeatom_AST;
				currentAST.child = beforeatom_AST!=null &&beforeatom_AST.getFirstChild()!=null ?
					beforeatom_AST.getFirstChild() : beforeatom_AST;
				currentAST.advanceChildToEnd();
				beforeatom_AST = (AST)currentAST.root;
				break;
			}
			case TRUE:
			case FALSE:
			case NEW:
			case NUM_INT:
			case NUM_FLOAT:
			case LEFTPAREN:
			case ID:
			case String:
			{
				atom();
				astFactory.addASTChild(currentAST, returnAST);
				beforeatom_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = beforeatom_AST;
	}
	
/**
 * an atom can a lot of things: an identifier, a caCall, a caValue, a functioncall, a number (integer or double)
 * a string, true, false, it can even be an entire new expression in a set of parentheses, or a new declaration
*/
	public final void atom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atom_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NUM_INT:
			case NUM_FLOAT:
			{
				numberValue();
				astFactory.addASTChild(currentAST, returnAST);
				atom_AST = (AST)currentAST.root;
				break;
			}
			case String:
			{
				AST tmp165_AST = null;
				tmp165_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp165_AST);
				match(String);
				atom_AST = (AST)currentAST.root;
				break;
			}
			case TRUE:
			{
				AST tmp166_AST = null;
				tmp166_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp166_AST);
				match(TRUE);
				atom_AST = (AST)currentAST.root;
				break;
			}
			case FALSE:
			{
				AST tmp167_AST = null;
				tmp167_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp167_AST);
				match(FALSE);
				atom_AST = (AST)currentAST.root;
				break;
			}
			case LEFTPAREN:
			{
				match(LEFTPAREN);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				match(RIGHTPAREN);
				atom_AST = (AST)currentAST.root;
				break;
			}
			case NEW:
			{
				newatom();
				astFactory.addASTChild(currentAST, returnAST);
				atom_AST = (AST)currentAST.root;
				break;
			}
			default:
				if ((LA(1)==ID) && (_tokenSet_27.member(LA(2)))) {
					id();
					astFactory.addASTChild(currentAST, returnAST);
					atom_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==ID) && (LA(2)==PERIOD)) {
					caObj();
					astFactory.addASTChild(currentAST, returnAST);
					atom_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==ID) && (LA(2)==LEFTPAREN)) {
					callFunction();
					astFactory.addASTChild(currentAST, returnAST);
					atom_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = atom_AST;
	}
	
/**
 * calling a function entails an identifier (which will have to be tested as an identifier standing for a function)
 * and its argList
*/
	public final void callFunction() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST callFunction_AST = null;
		
		try {      // for error handling
			AST tmp170_AST = null;
			tmp170_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp170_AST);
			match(ID);
			argList();
			astFactory.addASTChild(currentAST, returnAST);
			callFunction_AST = (AST)currentAST.root;
			callFunction_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FUNCTION_CALL,"FUNCTION_CALL")).add(callFunction_AST));
			currentAST.root = callFunction_AST;
			currentAST.child = callFunction_AST!=null &&callFunction_AST.getFirstChild()!=null ?
				callFunction_AST.getFirstChild() : callFunction_AST;
			currentAST.advanceChildToEnd();
			callFunction_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = callFunction_AST;
	}
	
/**
 * integer or double
*/
	public final void numberValue() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST numberValue_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NUM_INT:
			{
				AST tmp171_AST = null;
				tmp171_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp171_AST);
				match(NUM_INT);
				numberValue_AST = (AST)currentAST.root;
				break;
			}
			case NUM_FLOAT:
			{
				AST tmp172_AST = null;
				tmp172_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp172_AST);
				match(NUM_FLOAT);
				numberValue_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = numberValue_AST;
	}
	
/**
 * not supported yet: It's much like an instantiation in java - new identifier argList
*/
	public final void newatom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST newatom_AST = null;
		
		try {      // for error handling
			AST tmp173_AST = null;
			tmp173_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp173_AST);
			match(NEW);
			AST tmp174_AST = null;
			tmp174_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp174_AST);
			match(ID);
			argList();
			astFactory.addASTChild(currentAST, returnAST);
			newatom_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = newatom_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"int\"",
		"\"float\"",
		"\"bool\"",
		"\"string\"",
		"\"module\"",
		"\"datatype\"",
		"\"if\"",
		"\"else\"",
		"\"while\"",
		"\"for\"",
		"\"return\"",
		"\"true\"",
		"\"false\"",
		"\"public\"",
		"\"function\"",
		"\"break\"",
		"\"continue\"",
		"\"let\"",
		"\"void\"",
		"\"wait\"",
		"\"new\"",
		"\"print\"",
		"NUM_INT",
		"NUM_FLOAT",
		"PERIOD",
		"COMMA",
		"COLON",
		"SEMICOLON",
		"POUND",
		"PLUS",
		"MINUS",
		"SLASH",
		"MODULO",
		"TIMES",
		"LESS",
		"LESSEQUAL",
		"MORE",
		"MOREEQUAL",
		"EQUAL",
		"EQUALTO",
		"NOT",
		"OR",
		"AND",
		"NOTEQUAL",
		"PLUSPLUS",
		"MINUSMINUS",
		"LEFTPAREN",
		"RIGHTPAREN",
		"LEFTBRACKET",
		"RIGHTBRACKET",
		"LEFTBRACE",
		"RIGHTBRACE",
		"LETTER",
		"DIGIT",
		"ID",
		"Number",
		"Exponent",
		"ESC",
		"String",
		"WS",
		"SL_COMMENT",
		"ML_COMMENT",
		"PROG",
		"PUBLICVAR",
		"FUNCTION",
		"VARIABLE",
		"TYPE",
		"INDEX",
		"NULL",
		"ASSIGNVALUE",
		"ARGS",
		"OBJECT_VALUE",
		"OBJECT_CALL",
		"NEGATION",
		"CALL_FUNCTION",
		"ASSIGNMENT",
		"RETURN_VAL",
		"THEN",
		"FUNCTION_CALL",
		"CONDITION",
		"MODULECALL",
		"FORLEFT",
		"FORMID",
		"FORRIGHT",
		"FORBODY",
		"INCAFTER",
		"INCBEFORE",
		"DECAFTER",
		"DECBEFORE",
		"DECLARETYPE",
		"FUNCTIONBODY",
		"ARGDEC",
		"STATEMENTS",
		"IDENTIFIER"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 4195312L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 4326384L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 4195314L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 4942436725694398450L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 292733975779082240L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 288230376151711744L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 4924422327180589040L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 4906407928671107056L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 4942436725694267378L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 11522609665540096L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 2147483648L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 2251801961168896L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 2256200544550912L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 4906671788107888624L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 4953959584736346098L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 4960714983640530930L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 4971973983245828082L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 12367300883644416L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 12367300883644418L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 9007199254740992L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 2684354560L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 2291384916639744L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 2361753660817408L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 153656749981696L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 11522635435343872L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { 11522875953512448L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 16870900511014912L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	
	}
