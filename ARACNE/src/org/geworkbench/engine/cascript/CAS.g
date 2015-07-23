header {package org.geworkbench.engine.cascript;}
//the above header must be changed accordingly when moving this file around and compiling it
//this file MUST be compiled using the antlr.jar file from geworkbench\lib
//java antlr.Tool CAS.g
class CASLexer extends Lexer;
options
{
k = 2;
charVocabulary = '\3' .. '\377';
exportVocab = CAStokens;
testLiterals = false;
}

/*These are keywords*/
tokens {
    INT = "int";
    FLOAT = "float";
    BOOLSTR = "bool";
    STRING = "string";
    MODULE = "module";
    DATATYPE = "datatype";
    IFSTR = "if";
    ELSE = "else";
    WHILESTR = "while";
    FORSTR = "for";
    RETURNSTR = "return";
    TRUE = "true";
    FALSE = "false";
    PUBLIC = "public";
    FUNC = "function";
    BREAK = "break";
    CONTINUE = "continue";
    LET = "let";
    VOID = "void";
    WAIT = "wait";
    NEW = "new";
    PRINT = "print";
    NUM_INT;
    NUM_FLOAT;
}
{
    int nr_error = 0;
    public void reportError( String s ) {
        super.reportError( s );
        nr_error++;
    }
    public void reportError( RecognitionException e ) {
        super.reportError( e );
        nr_error++;
    }
}
PERIOD : '.' ;
COMMA : ',' ;
COLON : ':' ;
SEMICOLON : ';' ;
POUND : '#' ;
PLUS : '+' ;
MINUS : '-' ;
SLASH : '/' ;
MODULO : '%';
TIMES : '*' ;
LESS : '<';
LESSEQUAL : "<=";
MORE : '>';
MOREEQUAL : ">=";
EQUAL : '=';
EQUALTO : "==";
NOT : '!';
OR : "||" ;
AND : "&&";
NOTEQUAL : "!=";
PLUSPLUS : "++";
MINUSMINUS : "--";
LEFTPAREN : '(';
RIGHTPAREN :')';
LEFTBRACKET : '[';
RIGHTBRACKET : ']';
LEFTBRACE: '{';
RIGHTBRACE: '}';
protected LETTER : 'a' .. 'z' | 'A' .. 'Z' |'_';
protected DIGIT : '0' .. '9';
ID options { testLiterals = true; } : LETTER (LETTER | DIGIT)* ;

// Numeric constants
/*
The two types of number are split to avoid a side-effect of LL(k) parsing:
If the two rules were combined, the lookahead set for Number would include
a period (e.g., from ".1") followed by end-of-token e.g., from "1" by
itself), which collides with the lookahead set for the single-period rule.
*/
NUM_FLOAT: '.' ('0'..'9')+ (Exponent)?;
Number :    ('0'..'9')+ ( '.' ('0'..'9')* (Exponent)? { $setType(NUM_FLOAT); }
            | Exponent { $setType(NUM_FLOAT); }
            | /* empty */ { $setType(NUM_INT); });

// a couple protected methods to assist in matching floating point numbers
protected
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

protected ESC : '\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '"' | '\'' | '\\' );

String : '"'! (ESC|~('"'|'\\'|'\n'|'\r'))* '"'! ;

//Adapted from antlr example java.g
/**
 *Whitespace -- ignored
*/
WS	:	(	' '
		|	'\t'
		|	'\f'
			// handle newlines
		|	(	options {generateAmbigWarnings=false;}
			:	"\r\n"	// Evil DOS
			|	'\r'	// Macintosh
			|	'\n'	// Unix (the right way)
			)
			{ newline(); }
		)+
		{ _ttype = Token.SKIP; }
	;

/**
 * Single-line comments
*/
SL_COMMENT
	:	"//"
		(~('\n'|'\r'))* ('\n'|'\r'('\n')?)
		{$setType(Token.SKIP); newline();}
	;

/**
 * Multiple-line comments
*/
ML_COMMENT
	:	"/*"
		(	/*	'\r' '\n' can be matched in one alternative or by matching
				'\r' in one iteration and '\n' in another. I am trying to
				handle any flavor of newline that comes in, but the language
				that allows both "\r\n" and "\r" and "\n" to all be valid
				newline is ambiguous. Consequently, the resulting grammar
				must be ambiguous. I'm shutting this warning off.
			 */
			options {
				generateAmbigWarnings=false;
			}
		:
			{ LA(2)!='/' }? '*'
		|	'\r' '\n'		{newline();}
		|	'\r'			{newline();}
		|	'\n'			{newline();}
		|	~('*'|'\n'|'\r')
		)*
		"*/"
		{$setType(Token.SKIP);}
	;

class CASParser extends Parser;
options {
    k = 4;
    buildAST = true;
    exportVocab = CAStokens;
    defaultErrorHandler = true;
}

//whenever a new token is defined in the parser, it has to be added to this list
tokens {
 PROG;
 PUBLICVAR;
 FUNCTION;
 VARIABLE;
 TYPE;
 INDEX;
 NULL;
 ASSIGNVALUE;
 ARGS;
 OBJECT_VALUE;
 OBJECT_CALL;
 NEGATION;
 CALL_FUNCTION;
 ASSIGNMENT;
 RETURN_VAL;
 THEN;
 FUNCTION_CALL;
 CONDITION;
 MODULECALL;
 FORLEFT;
 FORMID;
 FORRIGHT;
 FORBODY;
 INCAFTER;
 INCBEFORE;
 DECAFTER;
 DECBEFORE;
 DECLARETYPE;
 FUNCTIONBODY;
 ARGDEC;
 STATEMENTS;
 IDENTIFIER;
}
{
    int nr_error = 0;
    public void reportError( String s ) {
        super.reportError( s );
        nr_error++;
    }
    public void reportError( RecognitionException e ) {
        super.reportError( e );
        nr_error++;
    }
}

/**
 * program starts here
*/
program :
    (pubVarDecl)* (funcDecl)+ EOF!
    { #program = #([PROG, "PROG"], program); }
    ;

/**
 * public variable declaration
*/
pubVarDecl:
    PUBLIC! declareStmt
    { #pubVarDecl = #([PUBLICVAR, "PUBLICVAR"], pubVarDecl);}
    ;

/**
 * function declaration token
*/
funcDecl:
    (type(LEFTBRACKET RIGHTBRACKET)*| isvoid) ID argDeclarationList functionbody
    { #funcDecl = #([FUNCTION, "FUNCTION"], funcDecl); }
    ;

/**
 * function body; this is what you find in the braces
*/
functionbody:
    bracestatement
    { #functionbody = #([FUNCTIONBODY, "FUNCTIONBODY"], functionbody); }
    ;

/**
 * list of types of statements
*/
statement
    : evaluateStmt
    | breakStmt
    | continueStmt
    | whileStmt
    | ifStmt
    | declareStmt
    | returnStmt
    | forstatement
    | waitStmt
    | printStmt
    ;

/**
 * simple print statement
*/
printStmt
    : PRINT^ eval SEMICOLON!
    ;

/**
 * simple wait statement
*/
waitStmt
    : WAIT^ eval SEMICOLON!
    ;

/**
 * for loop composed of 4 pieces - the <1>;<2>;<3>; { <4> }
*/
forstatement
    : FORSTR^ forLeft forMid forRight forBody
    ;

/**
 * first part of a for loop, generally the variable initialization
*/
forLeft
    : LEFTPAREN! (declaration | evaluate)
    {#forLeft = #([FORLEFT, "FORLEFT"], forLeft); }
    ;

/**
 * second part of a for loop, the evaluation
*/
forMid
    : SEMICOLON! expression
    {#forMid = #([FORMID, "FORMID"], forMid); }
    ;

/**
 * third part of a for loop, generally the incrementation
*/
forRight
    : SEMICOLON! evaluate RIGHTPAREN!
    {#forRight = #([FORRIGHT, "FORRIGHT"], forRight); }
    ;

/**
 * fourth part of a for loop, the body
*/
forBody
    : bracestatement
    {#forBody = #([FORBODY, "FORBODY"], forBody); }
    ;

/**
 * evaluation statement, only returns booleans
*/
evaluateStmt
    : evaluate SEMICOLON!
    ;

/**
 * break statement
*/
breakStmt
    : BREAK^ SEMICOLON!
    ;

/**
 * continue statement
*/
continueStmt
    : CONTINUE^ SEMICOLON!
    ;

/**
 * brace statement: can be either a single statement of a set of statements in braces
*/
bracestatement
    : (LEFTBRACE! (statement)+ RIGHTBRACE!) {#bracestatement = #([STATEMENTS, "STATEMENTS"], bracestatement); }| statement
    ;

/**
 * while loop statements with the evaluation and the following brace statement
*/
whileStmt
    : WHILESTR^ expressionStmt bracestatement
    ;

/**
 * while loop statements with the evaluation, the if body, and an else statement
*/
ifStmt
    : IFSTR^ expressionStmt ifStmtBody elseStmt
    ;

/**
 * if body is just a brace statement
*/

ifStmtBody
    : bracestatement
    {#ifStmtBody = #([THEN, "THEN"], ifStmtBody); }
    ;

/**
 * else statement, if it exists, notice the use of options {greedy = true;}
*/
elseStmt
    : (options {greedy = true;}
        : ELSE^ bracestatement)?
    ;

/**
 * condition for the while loop and the if conditional
*/

expressionStmt
    : LEFTPAREN! expression RIGHTPAREN!
    {#expressionStmt = #([CONDITION, "CONDITION"], expressionStmt); }
    ;

/** 
 * variable declaration statement
*/
declareStmt
    : declaration SEMICOLON!
    ;

/**
 *  variable declaration including its type, its identifier, and a third token called declareType
*/
declaration
    : type id declareType
    {#declaration = #([VARIABLE, "VARIABLE"], declaration); }
    ;

/**
 * return statement with an evaluation
*/
returnStmt
    : RETURNSTR^ evaluate SEMICOLON!
    ;

/**
 * evaluation statement including assignment
*/
evaluate
    : expression (EQUAL! expression
    {#evaluate = #([ASSIGNMENT, "ASSIGNMENT"], evaluate); })?
    ;

/**
 * caObj, this can be either a caValue of a caCall
*/
caObj:
    ID PERIOD! ID
    (argList {#caObj = #([OBJECT_CALL, "OBJECT_CALL"], caObj);}
    | /*empty*/ {#caObj = #([OBJECT_VALUE, "OBJECT_VALUE"], caObj);})
    ;

/**
 * caValue, AKA CasValue; this is what you use when you want to call a getDataSet, or setDataSet call
 * the first ID is the module name, the second ID is the name of the "variable", in our case DataSet
*/
caValue
    : ID PERIOD! ID {#caValue = #([OBJECT_VALUE, "OBJECT_VALUE"], caValue);}
    ;

/**
 * caCall, AKA CasMethod; this is what you use to make a module call a method
 * the first ID is the module name, the second ID is the method name
*/
caCall
    : ID PERIOD! ID argList {#caCall = #([OBJECT_CALL, "OBJECT_CALL"], caCall);}
    ;

/**
 * id is an identifier followed by an index.  If the index has a value, we're dealing with an array.
 * If the index is empty, we're dealing with just a plain variable.
*/
id
    : ID index
    {#id = #([IDENTIFIER, "IDENTIFIER"], id); }
    ;

/**
 * index shows us in the tree if we have an array or not
*/
index
    : LEFTBRACKET! indexValue RIGHTBRACKET! indexTail
    {#index = #([INDEX, "INDEX"], index); }
    | /* nothing */
    ;

/**
 * if we have an indextail, then we have a matrix
*/
indexTail
    : (LEFTBRACKET! indexValue RIGHTBRACKET!)?
    ;

/**
 * an indexValue, value found inside brackets denoting placement in an array, must be an integer
*/
indexValue
    : eval 
    ;

/**
 * declareType, the third part of the rule declare, can assign values to a variable right off the bat, and
 * can list off more identifier with assignvalues aka int i = 0; j, k = 5;
*/
declareType
    : ( assignValue)? (COMMA! id ( assignValue)?)*
    ;

/**
 * rule found under declare type that holds the value being assigned to an identifier
*/
assignValue
    : EQUAL^ expression
    ;

/**
 * the types found in CAScript
*/ 
type:
    (INT|FLOAT|BOOLSTR|STRING|(MODULE ID)| (DATATYPE ID)) //you should have dynamic types based on the parsing of the registry here.
    { #type = #([TYPE, "TYPE"], type); }
    ;

/**
 * void is a possible type for methods
*/
isvoid:
    VOID
    { #isvoid = #([TYPE, "TYPE"], isvoid); }
    ;

/**
 * precedence is set all the way down to an atom, starting here:
 * expression: (compare || compare)*
*/
expression
    : compare (OR^ compare)*
    ;

/**
 * compare: (inverse && inverse)*
 * 
*/
compare
    : inverse (AND^ inverse)*
    ;

/**
 * inverse: !compareTo
*/
inverse
    : (NOT^)?
        compareTo
    ;

/**
 * compareTo: (eval [< or <= or > or >= or == or !=] eval)*
*/
compareTo
    : eval ( ( LESS^ | LESSEQUAL^ | MORE^ | MOREEQUAL^
        | EQUALTO^ | NOTEQUAL^) eval ) *
    ;
/**
 * eval: (evalTerm [+ or -] evalTerm)*
 * we have to be greedy to make sure pluses and minuses are not taken as positive and negative signs for numbers
 * numbers and integers start showing at eval
*/
eval
    : evalTerm ((options {greedy=true;}: ( PLUS^ | MINUS^)) evalTerm ) *
    ;

/**
 * evalTerm: (negate [* or / or &] negate)*
*/
evalTerm
    : negate ( ( TIMES^ | SLASH^ | MODULO^) negate) *
    | LEFTBRACKET! negate RIGHTBRACKET!
    ;

/**
 * negate: -afteratom
*/
negate
    : (MINUS! afteratom {#negate = #([NEGATION, "NEGATION"], negate); })
    |afteratom;

/**
 * afteratom: beforeatom or beforeatom++ or beforeatom--
 * incrementation and decrementation before and after a value was tricky
*/
afteratom
    : beforeatom (options {greedy=true;}:
      PLUSPLUS! {#afteratom = #([INCAFTER,"INCAFTER"],afteratom);}
    | MINUSMINUS! {#afteratom = #([DECAFTER,"DECAFTER"],afteratom);})?;

/**
 * before: atom or --beforeatom or ++beforeatom
 * incrementation and decrementation before and after a value was tricky
*/
beforeatom
    : PLUSPLUS! atom { #beforeatom = #([INCBEFORE,"INCBEFORE"], beforeatom);}
    | MINUSMINUS! atom { #beforeatom = #([DECBEFORE,"DECBEFORE"], beforeatom); }
    | atom;

/**
 * an atom can a lot of things: an identifier, a caCall, a caValue, a functioncall, a number (integer or double)
 * a string, true, false, it can even be an entire new expression in a set of parentheses, or a new declaration
*/
atom
    : id
    | caObj
    | callFunction
    | numberValue
    | String
    | TRUE
    | FALSE
    | LEFTPAREN! expression RIGHTPAREN!
    | newatom
    ;

/**
 * not supported yet: It's much like an instantiation in java - new identifier argList
*/
newatom
    : NEW^ ID argList
    ;

/**
 * integer or double
*/
numberValue
    : NUM_INT
    | NUM_FLOAT
    ;

/**
 * argList, or an argument list: 
 * you have a left parentheses, and then expressions separated by commas, then a right parantheses
 * example: (5, 9, true, "hello")
*/
argList
    : LEFTPAREN! ((expression) (COMMA! expression)*)? RIGHTPAREN!
    {#argList = #([ARGS, "ARGS"], argList); }
    ;

/**
 * calling a function entails an identifier (which will have to be tested as an identifier standing for a function)
 * and its argList
*/
callFunction
    : ID argList
    {#callFunction = #([FUNCTION_CALL, "FUNCTION_CALL"], callFunction); }
    ;

/**
 * argDeclarationList, or argument declaration list:
 * you have a left parenthesis, and then sets of types, IDs, and possible opening and closing left and right brackets
 * with sets separated by commas, then a right parenthesis
 * example: (int i[][], int j, module genePanel gpan, double k)
*/
argDeclarationList
    : LEFTPAREN! (type ID (LEFTBRACKET RIGHTBRACKET)* (COMMA! type ID (LEFTBRACKET RIGHTBRACKET)*)* )? RIGHTPAREN!
    {#argDeclarationList = #([ARGDEC, "ARGDEC"], argDeclarationList); }
    ;

//for some help - ":." means don't execute, just hold the reference of it
{
import antlr.MismatchedTokenException;
import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.collections.AST;
import antlr.collections.impl.BitSet;

import java.util.Vector;
}

class CASWalker extends TreeParser;

options {
importVocab = CAStokens;
}
{
    CasInterpreter ipt = new CasInterpreter();
    AST mainbody = null;
}

/**
 * the root of the initial tree is going to be walkme, start here!
*/ 
walkme
: #(PROG (publicvar)* (function)+)
  {
    //make new symboltable for main
    ipt.symt = new CasSymbolTable(ipt.symt, ipt.symt.getLevel()+1);
    fbody(mainbody);
    //get rid of main's symbol table
    ipt.symt = ipt.symt.Parent();
  }
;

/**
 * public variable declaration
*/
publicvar
: #(PUBLICVAR variable)
;

/**
 * defining a function
*/
function
{ int brackets = 0;
  String id = "";
  CasDataType typereturn = null;
  Vector<CasArgument> argList = null;
  CasArgument temp = null;}
: #(FUNCTION (typereturn = type (LEFTBRACKET RIGHTBRACKET {brackets++;})*) ID {id = #ID.getText();} argList = args fbody:.)
{
  if (id.equals("main")) {
    mainbody = #fbody;
  }
  ipt.makeFunction(id, argList, fbody, ipt.symt, typereturn, brackets);
}
;

/**
 * list of formal arguments in a function definition
*/
args returns [Vector<CasArgument> argList]
{argList = new Vector<CasArgument>();
CasArgument temp = null;
String id = "";
int brackets = 0;
CasDataType typereturn = null;}
: #(ARGDEC ((typereturn = type) ID {id = #ID.getText();} (LEFTBRACKET RIGHTBRACKET {brackets++;})?
{temp = new CasArgument(typereturn, id, brackets);
argList.add(temp);
id = "";
brackets = 0;
typereturn = null;
temp = null;}
)*);//notice the kleene closure.  We can keep finding more parameters and we want all of them

/**
 * this is a variable declaration, like int a = 5; 
*/
variable
{ String id = "";
  CasDataType value = null;
  CasDataType typereturn;
  Vector<CasDataType> indices = null;}
: #(VARIABLE typereturn = type (#(IDENTIFIER ID {id = #ID.getText();} (indices = index)?) (#(EQUAL value = expr))?
{
if (ipt.symt.existsinscope(id)) {
  throw new CasException(id + " already exists as a function or variable");
}
ipt.putvar(id, typereturn, indices, value);
indices = null;
value = null;
})+)
/*notice the plus sign here.  This means that we keep finding an id, an indices value, and a value from expr to
create more entries in the symbol table*/
;

/**
 *index keeps track of all the indices that are defined in an variable declaration
*/
index returns [Vector<CasDataType> v]
{v = new Vector<CasDataType>();
CasDataType aindex = null;}
: #(INDEX (aindex = expr {v.add(aindex);})*)
;


/** 
 * the type of a function or variable returns one, possibly two strings
 * if the type is module, there is a secondary string that defines the type of module
 * if not, then the type is an ordinary primitive
 * type is used for variables, functions, formal parameter lists, arrays, and matrices
*/
type returns [CasDataType typereturn]
{
typereturn = null;
int isdiff = 0;
String id = "";
}
: #(TYPE (n:primitives | (MODULE ID {isdiff = 1; id = #ID.getText();}) | (DATATYPE ID {isdiff = 2; id = #ID.getText();})))
{
if (isdiff == 1) {
  typereturn = new CasModule(id);
}
else if (isdiff == 2) {
  typereturn = new CasDataPlug(id);
}
else {
  String temp = n.getText();
  if (temp.equals("void"))
    typereturn = new CasVoid();
  else if (temp.equals("int"))
    typereturn = new CasInt(0);
  else if (temp.equals("float"))
    typereturn = new CasDouble(0);
  else if (temp.equals("boolean"))
    typereturn = new CasBool(false);
  else if (temp.equals("string"))
    typereturn = new CasString("");
}}
;

/**
 * primitive types, including void
*/
primitives
: "void"
| "int"
| "float"
| "boolean"
| "string"
;


fbody returns [CasDataType a]
{a = null;}
: #(FUNCTIONBODY (a = expr))
; 

/**
 * expr is a big deal, it deals with almost everything the language can throw at it
*/
expr returns [CasDataType r] 
{
r = null;
CasDataType a,b;
String id = "";
String id2 = "";
Vector<CasDataType> arglist = null;
}
: NUM_INT                     { r = new CasInt(Integer.parseInt(#NUM_INT.getText())); } //literal integers
| NUM_FLOAT                   { r = new CasDouble(Double.parseDouble(#NUM_FLOAT.getText()));} //literal doubles
| TRUE                        { r = new CasBool(true); } //literal "true" value
| FALSE                       { r = new CasBool(false); } //literal "false" value
| #(IDENTIFIER ID {id = #ID.getText();} (arglist = index)?)   //here we are just using arglist to gather indices
{
if (arglist == null)
  r = ipt.getVariable( id );  //identifier, AKA a variable
else
  r = ipt.dimensionAccess(id, arglist); //array access
}
| str:String                  { r = new CasString( str.getText().toString() ); } //literal string value
| variable                    { r = new CasBool(true);} //variable declaration ex. int a = 5;
| #(ASSIGNMENT a=expr b=expr) //remember to extend ipt.assign(a,b) //assignment operation, more complex than it seems
  {r = ipt.assign(a,b);}
| #(OBJECT_VALUE ID {id = #ID.getText();} ID21:ID)
  { id2 = ID21.getText();
    if (ipt.symt.findVar(id) instanceof CasModule) {
      //should you be checking if id a CasModule in the firstplace?
      r = new CasValue(id, id2, ((CasModule)ipt.symt.findVar(id)));
      /*Testing purposes System.out.println("we're in object_value");*/
    }
    else {
      throw new CasException(id + "is not a module, so it can't have any variables");
    }
  }
  //object_value, like a public variable in JAVA ex. genePanel.DataSet
| #(OBJECT_CALL ID {id = #ID.getText();} ID22:ID arglist = param) //modify all this for CasDataPlug, bring this into the interpreter
  { id2 = ID22.getText();
    //r has to be something different, it has to come from MethodCall
    //MethodCall should tell the difference between a CasModule and a CasDataPlug
    /*Testing purposes
    System.out.println("we're in object_call");*/
    r = ipt.checkCasCallReturn(new CasCallReturn(ipt.MethodCall(id, id2, arglist)));
  }
  //object_call, like a function call in JAVA through a object ex. genePanel.createPanel(i++,10,true)
| #(PRINT a = expr)           { r = a; a.print(); } //print statement
| #(IFSTR #(CONDITION a=expr) #(THEN thenif:.) (#(ELSE elseif:.))?)
{
    if ( !( a instanceof CasBool ) )
        return a.error( "if: expression should be bool" );
    if ( ((CasBool)a).var )
        r = expr( #thenif );
    else if ( null != elseif )
        r = expr( #elseif );
}
//conditional statement, pretty straightforward
| #(WHILESTR {ipt.loopInit();} #(CONDITION cond:.) rest:.)
{
  a = expr(#cond);
  if ( !(a instanceof CasBool ))
    return a.error ( "while: expression should be bool" );
  while (!ipt.breakSet() && ((CasBool)a).getvar()) {
    if (ipt.continueSet()) {
      ipt.tryResetFlowControl();
      continue;
    }
    r = expr (#rest);
    if (!ipt.breakSet())
      a = expr(#cond);
    if ( !(a instanceof CasBool ))
        return a.error ( "while: expression should be bool" );
  }
  ipt.loopEnd();
}
//while loop, this probably has some bugs in it
| #(FORSTR {ipt.loopInit(); }#(FORLEFT r = expr) #(FORMID cond2:.) #(FORRIGHT after:.) #(FORBODY forbody:.))
{ a = expr(#cond2);
  if ( !(a instanceof CasBool ))
  return a.error ( "for: expression should be bool" );
  while (!ipt.breakSet() && ((CasBool)a).getvar()) {
    if (ipt.continueSet()) {
      ipt.tryResetFlowControl();
      continue;
    }
    r = expr (#forbody);
    if (!ipt.breakSet()) {
      expr(#after);
      a = expr(#cond2);
    }
    if ( !(a instanceof CasBool ))
        return a.error ( "for: expression should be bool" );
  }
  ipt.loopEnd();
}
//for loop, this probably has some bugs in it, the same bugs the while loop has
| #(STATEMENTS (statement:. { if ( ipt.canProceed() ) r = expr(#statement); } )*) //set of statements
| BREAK                       { r = new CasBreak(); ipt.setBreak();} //break statement, changes control flow
| CONTINUE                    { r = new CasContinue(); ipt.setContinue();} //continue statement, changes control flow
//should the returnstatement have its own DataType? like CasReturn? and you can check that it is that type in functioncall in the interpreter
| #(RETURNSTR a = expr)    { r = new CasReturn(ipt.rvalue( a )); ipt.setReturn();} //return statement, changes control flow
| #(WAIT a = expr)         { r = ipt.stopme(a);} //pauses the program for a given number of seconds
| #(FUNCTION_CALL ID {id = #ID.getText();} arglist = param)
  { r = ipt.funcCall(this, id, arglist);}
  //call a function created by the user
/*
| #(NEW ID arglist = param)
  { id = #ID.getText();
    r = new CasString("new statement" + id);
    //for (int i = 0; i < arglist.size(); i++){
    //  r += arglist.elementAt(i) + " ";
    //}
  }*/

| #(OR a = expr right_or:.)
  {
    if ( a instanceof CasBool )
        r = ( ((CasBool)a).var ? a : expr(#right_or) );
    else
        r = a.or( expr(#right_or) );
  }
//the extra complexity is required because if the first operand is true, there is no need to do the second operand.
| #(AND a = expr right_and:.)
  {
    if ( a instanceof CasBool )
        r = ( ((CasBool)a).var ? expr(#right_and) : a );
    else
        r = a.and( expr(#right_and) );
  }
//the extra complexicty is required because if the first operand is false, there is no need to do the second operand.
| #(NOT a = expr)                        {r = a.not();} //negation of a boolean value
| #(LESS a = expr b = expr)              {r = a.lt(b);} //returns boolean value for a < b
| #(LESSEQUAL a = expr b = expr)         {r = a.le(b);} //returns boolean value for a <=b
| #(MORE a = expr b = expr)              {r = a.gt(b);} //returns boolean value for a > b
| #(MOREEQUAL a = expr b = expr)         {r = a.ge(b);} //returns boolean value for a >=b
| #(EQUALTO a = expr b = expr)           {r = a.eq(b);} //returns boolean value for a==b
| #(NOTEQUAL a = expr b = expr)          {r = a.ne(b);} //returns boolean value for a!=b
| #(PLUS a = expr b = expr)              {r = a.plus(b);} //addition of integers and doubles
| #(MINUS a = expr b = expr)             {r = a.minus(b);} //subtraction of integers and doubles
| #(TIMES a = expr b = expr)             {r = a.times(b);} //multiplication
| #(SLASH a = expr b = expr)             {r = a.lfracts(b);} //division
| #(MODULO a = expr b = expr)            {r = a.modulus(b);} //undefined
| #(NEGATION a = expr)                   {r = a.uminus();} //unary minus operation
| #(INCAFTER a = expr)                   {r = a.copy(); ipt.incOrDec(a, true);} //incrementation after operation, a++
| #(DECAFTER a = expr)                   {r = a.copy(); ipt.incOrDec(a, false);} //decrementation after operation, a--
| #(INCBEFORE a = expr)                  {r = ipt.incOrDec(a, true);} //incrementation before operation, ++a
| #(DECBEFORE a = expr)                  {r = ipt.incOrDec(a, false);} //incrementation after operation, --a
;

/**
 * list of parameters for a function call
*/
param returns [ Vector<CasDataType> arglist ]
{ arglist = null;
  CasDataType a;}
: #(ARGS { arglist = new Vector<CasDataType>(); } ( a=expr      { arglist.add( a ); })*)
;
