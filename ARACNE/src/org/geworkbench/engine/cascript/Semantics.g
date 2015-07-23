header {package org.geworkbench.engine.cascript;}
//for some help - ":." means don't execute, just hold the reference of it
{
import antlr.MismatchedTokenException;
import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.collections.AST;
import antlr.collections.impl.BitSet;

import java.util.Vector;
}

class CASSemantics extends TreeParser;

options {
importVocab = CAStokens;
}
{
    CasPreInterpreter ipt = new CasPreInterpreter();
    AST mainbody = null;
}

/**
 * the root of the initial tree is going to be walkme, start here!
*/ 
walkme
: #(PROG (publicvar)* (function)+)
  {
    //make new symboltable for main
    ipt.psymt = new CasPreSymbolTable(ipt.psymt, ipt.psymt.getLevel()+1);
    fbody(mainbody);
    //get rid of main's symbol table
    ipt.psymt = ipt.psymt.Parent();
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
  ipt.makeFunction(id, argList, fbody, ipt.psymt, typereturn, brackets);
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
  CasPreData value = null;
  CasDataType typereturn;
  Vector<CasPreData> indices = null;}
: #(VARIABLE typereturn = type (#(IDENTIFIER ID {id = #ID.getText();} (indices = index)?) (#(EQUAL value = expr))?
{
if (ipt.psymt.existsinscope(id)) {
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
index returns [Vector<CasPreData> v]
{v = new Vector<CasPreData>();
CasPreData aindex = null;}
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
  else if (temp.equals("double"))
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
| "double"
| "boolean"
| "string"
;


fbody returns [CasPreData a]
{a = null;}
: #(FUNCTIONBODY (a = expr))
; 

/**
 * expr is a big deal, it deals with almost everything the language can throw at it
*/
expr returns [CasPreData r] 
{
r = null;
CasPreData a = null,b = null;
String id = "";
String id2 = "";
Vector<CasPreData> arglist = null;
}
: NUM_INT                     { r = new CasPreData(new CasInt(Integer.parseInt(#NUM_INT.getText())), false, true, true); } //literal integers
| NUM_DOUBLE                   { r = new CasPreData(new CasDouble(Double.parseDouble(#NUM_DOUBLE.getText())), false, true, true);} //literal doubles
| TRUE                        { r = new CasPreData(new CasBool(true), false, true, true); } //literal "true" value
| FALSE                       { r = new CasPreData(new CasBool(false), false, true, true); } //literal "false" value
| #(IDENTIFIER ID {id = #ID.getText();} (arglist = index)?)   //here we are just using arglist to gather indices
{
if (arglist == null)
  r = ipt.getVariable( id );  //identifier, AKA a variable
else
  r = ipt.dimensionAccess(id, arglist); //array access
}
| str:String                  { r = new CasPreData(new CasString( str.getText().toString()), false, true, true); } //literal string value
| variable                    { r = new CasPreData(new CasVariable("variable declaration"), false, false, false);} //variable declaration ex. int a = 5;
| #(ASSIGNMENT a=expr b=expr) //remember to extend ipt.assign(a,b) //assignment operation, more complex than it seems
  {
   r = ipt.assign(a,b); //for assign, remember to use b's flag to affect a's flags
  }
| #(OBJECT_VALUE ID {id = #ID.getText();} ID21:ID)
  { id2 = ID21.getText();
    if (ipt.psymt.findVar(id).getData() instanceof CasModule) {
      //should you be checking if id a CasModule in the firstplace?
      r = new CasPreData(new CasValue(id, id2, ((CasModule)ipt.psymt.findVar(id).getData())), true, true, false);
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
    r = new CasPreData(ipt.checkCasCallReturn(new CasCallReturn(ipt.MethodCall(id, id2, arglist))), true, true, false);
  }
  //object_call, like a function call in JAVA through a object ex. genePanel.createPanel(i++,10,true)
| #(PRINT a = expr)           { r = a; a.getData().print(); } //print statement
| #(IFSTR #(CONDITION a=expr) #(THEN thenif:.) (#(ELSE elseif:.))?) //we wanna change this completely
{
    if ( !( a.getData() instanceof CasBool ) )
        return new CasPreData(a.getData().error( "if: expression should be bool" ), false, false, false);
    if ( ((CasBool)a.getData()).var )
        r = expr( #thenif );
    else if ( null != elseif )
        r = expr( #elseif );
}
//conditional statement, pretty straightforward
| #(WHILESTR {ipt.loopInit();} #(CONDITION cond:.) rest:.) //we wanna change this completely
{
  a = expr(#cond);
  if ( !(a.getData() instanceof CasBool ))
    return new CasPreData(a.getData().error ( "while: expression should be bool" ), false, false, false);
  while (!ipt.breakSet() && ((CasBool)a.getData()).getvar()) {
    if (ipt.continueSet()) {
      ipt.tryResetFlowControl();
      continue;
    }
    r = expr (#rest);
    if (!ipt.breakSet())
      a = expr(#cond);
    if ( !(a.getData() instanceof CasBool ))
        return new CasPreData(a.getData().error ( "while: expression should be bool" ), false, false, false);
  }
  ipt.loopEnd();
}
//while loop, this probably has some bugs in it
| #(FORSTR {ipt.loopInit(); }#(FORLEFT r = expr) #(FORMID cond2:.) #(FORRIGHT after:.) #(FORBODY forbody:.)) //we wanna change this completely
{ a = expr(#cond2);
  if ( !(a.getData() instanceof CasBool ))
  return new CasPreData(a.getData().error ( "for: expression should be bool" ), false, false, false);
  while (!ipt.breakSet() && ((CasBool)a.getData()).getvar()) {
    if (ipt.continueSet()) {
      ipt.tryResetFlowControl();
      continue;
    }
    r = expr (#forbody);
    if (!ipt.breakSet()) {
      expr(#after);
      a = expr(#cond2);
    }
    if ( !(a.getData() instanceof CasBool ))
        return new CasPreData(a.getData().error ( "for: expression should be bool" ), false, false, false);
  }
  ipt.loopEnd();
}
//for loop, this probably has some bugs in it, the same bugs the while loop has
| #(STATEMENTS (statement:. { if ( ipt.canProceed() ) r = expr(#statement); } )*) //set of statements
| BREAK                       { r = new CasPreData(new CasBreak(), false, false, false); ipt.setBreak();} //break statement, changes control flow
| CONTINUE                    { r = new CasPreData(new CasContinue(), false, false, false); ipt.setContinue();} //continue statement, changes control flow
//should the returnstatement have its own DataType? like CasReturn? and you can check that it is that type in functioncall in the interpreter
| #(RETURNSTR a = expr)    { r = new CasPreData(new CasReturn(ipt.rvalue( a.getData() )), a.getDeclared(), a.getInitialized(), a.getKnown()); ipt.setReturn();} //return statement, changes control flow
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
    if ( a.getData() instanceof CasBool )
        r = ( ((CasBool)a.getData()).var ? ipt.PreDataMaker(a.getData().and(a.getData()),a,b) : ipt.PreDataMaker(a.getData().and( expr(#right_or).getData()),a,b) );
    else
        r = ipt.PreDataMaker(a.getData().or( expr(#right_or).getData()),a,b);
  }
//the extra complexity is required because if the first operand is true, there is no need to do the second operand.
| #(AND a = expr right_and:.)
  {
    if ( a.getData() instanceof CasBool )
        r = ( ((CasBool)a.getData()).var ? ipt.PreDataMaker(a.getData().and( expr(#right_and).getData()),a,b) : ipt.PreDataMaker(a.getData().and(a.getData()),a,b) );
    else
        r = ipt.PreDataMaker(a.getData().and( expr(#right_and).getData()),a,b);
  }
//the extra complexicty is required because if the first operand is false, there is no need to do the second operand.
//remember do switch all the a. to a.getData().
| #(NOT a = expr)                        {r = ipt.PreDataMaker(a.getData().not(),a,b);} //negation of a boolean value
| #(LESS a = expr b = expr)              {r = ipt.PreDataMaker(a.getData().lt(b.getData()),a,b);} //returns boolean value for a < b
| #(LESSEQUAL a = expr b = expr)         {r = ipt.PreDataMaker(a.getData().le(b.getData()),a,b);} //returns boolean value for a <=b
| #(MORE a = expr b = expr)              {r = ipt.PreDataMaker(a.getData().gt(b.getData()),a,b);} //returns boolean value for a > b
| #(MOREEQUAL a = expr b = expr)         {r = ipt.PreDataMaker(a.getData().ge(b.getData()),a,b);} //returns boolean value for a >=b
| #(EQUALTO a = expr b = expr)           {r = ipt.PreDataMaker(a.getData().eq(b.getData()),a,b);} //returns boolean value for a==b
| #(NOTEQUAL a = expr b = expr)          {r = ipt.PreDataMaker(a.getData().ne(b.getData()),a,b);} //returns boolean value for a!=b
| #(PLUS a = expr b = expr)              {r = ipt.PreDataMaker(a.getData().plus(b.getData()),a,b);} //addition of integers and doubles
| #(MINUS a = expr b = expr)             {r = ipt.PreDataMaker(a.getData().minus(b.getData()),a,b);} //subtraction of integers and doubles
| #(TIMES a = expr b = expr)             {r = ipt.PreDataMaker(a.getData().times(b.getData()),a,b);} //multiplication
| #(SLASH a = expr b = expr)             {r = ipt.PreDataMaker(a.getData().lfracts(b.getData()),a,b);} //division
| #(MODULO a = expr b = expr)            {r = ipt.PreDataMaker(a.getData().modulus(b.getData()),a,b);} //undefined
| #(NEGATION a = expr)                   {r = ipt.PreDataMaker(a.getData().uminus(),a,b);} //unary minus operation
| #(INCAFTER a = expr)                   {r = a.copy(); ipt.incOrDec(a, true);} //incrementation after operation, a++
| #(DECAFTER a = expr)                   {r = a.copy(); ipt.incOrDec(a, false);} //decrementation after operation, a--
| #(INCBEFORE a = expr)                  {r = ipt.incOrDec(a, true);} //incrementation before operation, ++a
| #(DECBEFORE a = expr)                  {r = ipt.incOrDec(a, false);} //incrementation after operation, --a
;

/**
 * list of parameters for a function call
*/
param returns [ Vector<CasPreData> arglist ]
{ arglist = null;
  CasPreData a;}
: #(ARGS { arglist = new Vector<CasPreData>(); } ( a=expr      { arglist.add( a ); })*)
;

