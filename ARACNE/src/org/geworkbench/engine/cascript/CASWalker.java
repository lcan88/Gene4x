// $ANTLR 2.7.5 (20050128): "CAS.g" -> "CASWalker.java"$
package org.geworkbench.engine.cascript;import antlr.MismatchedTokenException;
import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.collections.AST;
import antlr.collections.impl.BitSet;

import java.util.Vector;


public class CASWalker extends antlr.TreeParser       implements CASWalkerTokenTypes
 {

    CasInterpreter ipt = new CasInterpreter();
    AST mainbody = null;
public CASWalker() {
	tokenNames = _tokenNames;
}

/**
 * the root of the initial tree is going to be walkme, start here!
*/
	public final void walkme(AST _t) throws RecognitionException {
		
		AST walkme_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t172 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,PROG);
			_t = _t.getFirstChild();
			{
			_loop174:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==PUBLICVAR)) {
					publicvar(_t);
					_t = _retTree;
				}
				else {
					break _loop174;
				}
				
			} while (true);
			}
			{
			int _cnt176=0;
			_loop176:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==FUNCTION)) {
					function(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt176>=1 ) { break _loop176; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt176++;
			} while (true);
			}
			_t = __t172;
			_t = _t.getNextSibling();
			
			//make new symboltable for main
			ipt.symt = new CasSymbolTable(ipt.symt, ipt.symt.getLevel()+1);
			fbody(mainbody);
			//get rid of main's symbol table
			ipt.symt = ipt.symt.Parent();
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
/**
 * public variable declaration
*/
	public final void publicvar(AST _t) throws RecognitionException {
		
		AST publicvar_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t178 = _t;
			AST tmp2_AST_in = (AST)_t;
			match(_t,PUBLICVAR);
			_t = _t.getFirstChild();
			variable(_t);
			_t = _retTree;
			_t = __t178;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
/**
 * defining a function
*/
	public final void function(AST _t) throws RecognitionException {
		
		AST function_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST fbody = null;
		int brackets = 0;
		String id = "";
		CasDataType typereturn = null;
		Vector<CasArgument> argList = null;
		CasArgument temp = null;
		
		try {      // for error handling
			AST __t180 = _t;
			AST tmp3_AST_in = (AST)_t;
			match(_t,FUNCTION);
			_t = _t.getFirstChild();
			{
			typereturn=type(_t);
			_t = _retTree;
			{
			_loop183:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==LEFTBRACKET)) {
					AST tmp4_AST_in = (AST)_t;
					match(_t,LEFTBRACKET);
					_t = _t.getNextSibling();
					AST tmp5_AST_in = (AST)_t;
					match(_t,RIGHTBRACKET);
					_t = _t.getNextSibling();
					brackets++;
				}
				else {
					break _loop183;
				}
				
			} while (true);
			}
			}
			AST tmp6_AST_in = (AST)_t;
			match(_t,ID);
			_t = _t.getNextSibling();
			id = tmp6_AST_in.getText();
			argList=args(_t);
			_t = _retTree;
			fbody = (AST)_t;
			if ( _t==null ) throw new MismatchedTokenException();
			_t = _t.getNextSibling();
			_t = __t180;
			_t = _t.getNextSibling();
			
			if (id.equals("main")) {
			mainbody = fbody;
			}
			ipt.makeFunction(id, argList, fbody, ipt.symt, typereturn, brackets);
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
/**
 * this is a variable declaration, like int a = 5; 
*/
	public final void variable(AST _t) throws RecognitionException {
		
		AST variable_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		String id = "";
		CasDataType value = null;
		CasDataType typereturn;
		Vector<CasDataType> indices = null;
		
		try {      // for error handling
			AST __t191 = _t;
			AST tmp7_AST_in = (AST)_t;
			match(_t,VARIABLE);
			_t = _t.getFirstChild();
			typereturn=type(_t);
			_t = _retTree;
			{
			int _cnt197=0;
			_loop197:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==IDENTIFIER)) {
					AST __t193 = _t;
					AST tmp8_AST_in = (AST)_t;
					match(_t,IDENTIFIER);
					_t = _t.getFirstChild();
					AST tmp9_AST_in = (AST)_t;
					match(_t,ID);
					_t = _t.getNextSibling();
					id = tmp9_AST_in.getText();
					{
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case INDEX:
					{
						indices=index(_t);
						_t = _retTree;
						break;
					}
					case 3:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(_t);
					}
					}
					}
					_t = __t193;
					_t = _t.getNextSibling();
					{
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case EQUAL:
					{
						AST __t196 = _t;
						AST tmp10_AST_in = (AST)_t;
						match(_t,EQUAL);
						_t = _t.getFirstChild();
						value=expr(_t);
						_t = _retTree;
						_t = __t196;
						_t = _t.getNextSibling();
						break;
					}
					case 3:
					case IDENTIFIER:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(_t);
					}
					}
					}
					
					if (ipt.symt.existsinscope(id)) {
					throw new CasException(id + " already exists as a function or variable");
					}
					ipt.putvar(id, typereturn, indices, value);
					indices = null;
					value = null;
					
				}
				else {
					if ( _cnt197>=1 ) { break _loop197; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt197++;
			} while (true);
			}
			_t = __t191;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
/** 
 * the type of a function or variable returns one, possibly two strings
 * if the type is module, there is a secondary string that defines the type of module
 * if not, then the type is an ordinary primitive
 * type is used for variables, functions, formal parameter lists, arrays, and matrices
*/
	public final CasDataType  type(AST _t) throws RecognitionException {
		CasDataType typereturn;
		
		AST type_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		
		typereturn = null;
		int isdiff = 0;
		String id = "";
		
		
		try {      // for error handling
			AST __t203 = _t;
			AST tmp11_AST_in = (AST)_t;
			match(_t,TYPE);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INT:
			case FLOAT:
			case STRING:
			case VOID:
			case LITERAL_boolean:
			{
				n = _t==ASTNULL ? null : (AST)_t;
				primitives(_t);
				_t = _retTree;
				break;
			}
			case MODULE:
			{
				{
				AST tmp12_AST_in = (AST)_t;
				match(_t,MODULE);
				_t = _t.getNextSibling();
				AST tmp13_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				isdiff = 1; id = tmp13_AST_in.getText();
				}
				break;
			}
			case DATATYPE:
			{
				{
				AST tmp14_AST_in = (AST)_t;
				match(_t,DATATYPE);
				_t = _t.getNextSibling();
				AST tmp15_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				isdiff = 2; id = tmp15_AST_in.getText();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t203;
			_t = _t.getNextSibling();
			
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
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return typereturn;
	}
	
/**
 * list of formal arguments in a function definition
*/
	public final Vector<CasArgument>  args(AST _t) throws RecognitionException {
		Vector<CasArgument> argList;
		
		AST args_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		argList = new Vector<CasArgument>();
		CasArgument temp = null;
		String id = "";
		int brackets = 0;
		CasDataType typereturn = null;
		
		try {      // for error handling
			AST __t185 = _t;
			AST tmp16_AST_in = (AST)_t;
			match(_t,ARGDEC);
			_t = _t.getFirstChild();
			{
			_loop189:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==TYPE)) {
					{
					typereturn=type(_t);
					_t = _retTree;
					}
					AST tmp17_AST_in = (AST)_t;
					match(_t,ID);
					_t = _t.getNextSibling();
					id = tmp17_AST_in.getText();
					{
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case LEFTBRACKET:
					{
						AST tmp18_AST_in = (AST)_t;
						match(_t,LEFTBRACKET);
						_t = _t.getNextSibling();
						AST tmp19_AST_in = (AST)_t;
						match(_t,RIGHTBRACKET);
						_t = _t.getNextSibling();
						brackets++;
						break;
					}
					case 3:
					case TYPE:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(_t);
					}
					}
					}
					temp = new CasArgument(typereturn, id, brackets);
					argList.add(temp);
					id = "";
					brackets = 0;
					typereturn = null;
					temp = null;
				}
				else {
					break _loop189;
				}
				
			} while (true);
			}
			_t = __t185;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return argList;
	}
	
/**
 *index keeps track of all the indices that are defined in an variable declaration
*/
	public final Vector<CasDataType>  index(AST _t) throws RecognitionException {
		Vector<CasDataType> v;
		
		AST index_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		v = new Vector<CasDataType>();
		CasDataType aindex = null;
		
		try {      // for error handling
			AST __t199 = _t;
			AST tmp20_AST_in = (AST)_t;
			match(_t,INDEX);
			_t = _t.getFirstChild();
			{
			_loop201:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					aindex=expr(_t);
					_t = _retTree;
					v.add(aindex);
				}
				else {
					break _loop201;
				}
				
			} while (true);
			}
			_t = __t199;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return v;
	}
	
/**
 * expr is a big deal, it deals with almost everything the language can throw at it
*/
	public final CasDataType  expr(AST _t) throws RecognitionException {
		CasDataType r;
		
		AST expr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST str = null;
		AST ID21 = null;
		AST ID22 = null;
		AST thenif = null;
		AST elseif = null;
		AST cond = null;
		AST rest = null;
		AST cond2 = null;
		AST after = null;
		AST forbody = null;
		AST statement = null;
		AST right_or = null;
		AST right_and = null;
		
		r = null;
		CasDataType a,b;
		String id = "";
		String id2 = "";
		Vector<CasDataType> arglist = null;
		
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NUM_INT:
			{
				AST tmp21_AST_in = (AST)_t;
				match(_t,NUM_INT);
				_t = _t.getNextSibling();
				r = new CasInt(Integer.parseInt(tmp21_AST_in.getText()));
				break;
			}
			case NUM_FLOAT:
			{
				AST tmp22_AST_in = (AST)_t;
				match(_t,NUM_FLOAT);
				_t = _t.getNextSibling();
				r = new CasDouble(Double.parseDouble(tmp22_AST_in.getText()));
				break;
			}
			case TRUE:
			{
				AST tmp23_AST_in = (AST)_t;
				match(_t,TRUE);
				_t = _t.getNextSibling();
				r = new CasBool(true);
				break;
			}
			case FALSE:
			{
				AST tmp24_AST_in = (AST)_t;
				match(_t,FALSE);
				_t = _t.getNextSibling();
				r = new CasBool(false);
				break;
			}
			case IDENTIFIER:
			{
				AST __t212 = _t;
				AST tmp25_AST_in = (AST)_t;
				match(_t,IDENTIFIER);
				_t = _t.getFirstChild();
				AST tmp26_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				id = tmp26_AST_in.getText();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case INDEX:
				{
					arglist=index(_t);
					_t = _retTree;
					break;
				}
				case 3:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				_t = __t212;
				_t = _t.getNextSibling();
				
				if (arglist == null)
				r = ipt.getVariable( id );  //identifier, AKA a variable
				else
				r = ipt.dimensionAccess(id, arglist); //array access
				
				break;
			}
			case String:
			{
				str = (AST)_t;
				match(_t,String);
				_t = _t.getNextSibling();
				r = new CasString( str.getText().toString() );
				break;
			}
			case VARIABLE:
			{
				variable(_t);
				_t = _retTree;
				r = new CasBool(true);
				break;
			}
			case ASSIGNMENT:
			{
				AST __t214 = _t;
				AST tmp27_AST_in = (AST)_t;
				match(_t,ASSIGNMENT);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t214;
				_t = _t.getNextSibling();
				r = ipt.assign(a,b);
				break;
			}
			case OBJECT_VALUE:
			{
				AST __t215 = _t;
				AST tmp28_AST_in = (AST)_t;
				match(_t,OBJECT_VALUE);
				_t = _t.getFirstChild();
				AST tmp29_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				id = tmp29_AST_in.getText();
				ID21 = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				_t = __t215;
				_t = _t.getNextSibling();
				id2 = ID21.getText();
				if (ipt.symt.findVar(id) instanceof CasModule) {
				//should you be checking if id a CasModule in the firstplace?
				r = new CasValue(id, id2, ((CasModule)ipt.symt.findVar(id)));
				/*Testing purposes System.out.println("we're in object_value");*/
				}
				else {
				throw new CasException(id + "is not a module, so it can't have any variables");
				}
				
				break;
			}
			case OBJECT_CALL:
			{
				AST __t216 = _t;
				AST tmp30_AST_in = (AST)_t;
				match(_t,OBJECT_CALL);
				_t = _t.getFirstChild();
				AST tmp31_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				id = tmp31_AST_in.getText();
				ID22 = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				arglist=param(_t);
				_t = _retTree;
				_t = __t216;
				_t = _t.getNextSibling();
				id2 = ID22.getText();
				//r has to be something different, it has to come from MethodCall
				//MethodCall should tell the difference between a CasModule and a CasDataPlug
				/*Testing purposes
				System.out.println("we're in object_call");*/
				r = ipt.checkCasCallReturn(new CasCallReturn(ipt.MethodCall(id, id2, arglist)));
				
				break;
			}
			case PRINT:
			{
				AST __t217 = _t;
				AST tmp32_AST_in = (AST)_t;
				match(_t,PRINT);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t217;
				_t = _t.getNextSibling();
				r = a; a.print();
				break;
			}
			case IFSTR:
			{
				AST __t218 = _t;
				AST tmp33_AST_in = (AST)_t;
				match(_t,IFSTR);
				_t = _t.getFirstChild();
				AST __t219 = _t;
				AST tmp34_AST_in = (AST)_t;
				match(_t,CONDITION);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t219;
				_t = _t.getNextSibling();
				AST __t220 = _t;
				AST tmp35_AST_in = (AST)_t;
				match(_t,THEN);
				_t = _t.getFirstChild();
				thenif = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t220;
				_t = _t.getNextSibling();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ELSE:
				{
					AST __t222 = _t;
					AST tmp36_AST_in = (AST)_t;
					match(_t,ELSE);
					_t = _t.getFirstChild();
					elseif = (AST)_t;
					if ( _t==null ) throw new MismatchedTokenException();
					_t = _t.getNextSibling();
					_t = __t222;
					_t = _t.getNextSibling();
					break;
				}
				case 3:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				_t = __t218;
				_t = _t.getNextSibling();
				
				if ( !( a instanceof CasBool ) )
				return a.error( "if: expression should be bool" );
				if ( ((CasBool)a).var )
				r = expr( thenif );
				else if ( null != elseif )
				r = expr( elseif );
				
				break;
			}
			case WHILESTR:
			{
				AST __t223 = _t;
				AST tmp37_AST_in = (AST)_t;
				match(_t,WHILESTR);
				_t = _t.getFirstChild();
				ipt.loopInit();
				AST __t224 = _t;
				AST tmp38_AST_in = (AST)_t;
				match(_t,CONDITION);
				_t = _t.getFirstChild();
				cond = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t224;
				_t = _t.getNextSibling();
				rest = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t223;
				_t = _t.getNextSibling();
				
				a = expr(cond);
				if ( !(a instanceof CasBool ))
				return a.error ( "while: expression should be bool" );
				while (!ipt.breakSet() && ((CasBool)a).getvar()) {
				if (ipt.continueSet()) {
				ipt.tryResetFlowControl();
				continue;
				}
				r = expr (rest);
				if (!ipt.breakSet())
				a = expr(cond);
				if ( !(a instanceof CasBool ))
				return a.error ( "while: expression should be bool" );
				}
				ipt.loopEnd();
				
				break;
			}
			case FORSTR:
			{
				AST __t225 = _t;
				AST tmp39_AST_in = (AST)_t;
				match(_t,FORSTR);
				_t = _t.getFirstChild();
				ipt.loopInit();
				AST __t226 = _t;
				AST tmp40_AST_in = (AST)_t;
				match(_t,FORLEFT);
				_t = _t.getFirstChild();
				r=expr(_t);
				_t = _retTree;
				_t = __t226;
				_t = _t.getNextSibling();
				AST __t227 = _t;
				AST tmp41_AST_in = (AST)_t;
				match(_t,FORMID);
				_t = _t.getFirstChild();
				cond2 = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t227;
				_t = _t.getNextSibling();
				AST __t228 = _t;
				AST tmp42_AST_in = (AST)_t;
				match(_t,FORRIGHT);
				_t = _t.getFirstChild();
				after = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t228;
				_t = _t.getNextSibling();
				AST __t229 = _t;
				AST tmp43_AST_in = (AST)_t;
				match(_t,FORBODY);
				_t = _t.getFirstChild();
				forbody = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t229;
				_t = _t.getNextSibling();
				_t = __t225;
				_t = _t.getNextSibling();
				a = expr(cond2);
				if ( !(a instanceof CasBool ))
				return a.error ( "for: expression should be bool" );
				while (!ipt.breakSet() && ((CasBool)a).getvar()) {
				if (ipt.continueSet()) {
				ipt.tryResetFlowControl();
				continue;
				}
				r = expr (forbody);
				if (!ipt.breakSet()) {
				expr(after);
				a = expr(cond2);
				}
				if ( !(a instanceof CasBool ))
				return a.error ( "for: expression should be bool" );
				}
				ipt.loopEnd();
				
				break;
			}
			case STATEMENTS:
			{
				AST __t230 = _t;
				AST tmp44_AST_in = (AST)_t;
				match(_t,STATEMENTS);
				_t = _t.getFirstChild();
				{
				_loop232:
				do {
					if (_t==null) _t=ASTNULL;
					if (((_t.getType() >= INT && _t.getType() <= LITERAL_boolean))) {
						statement = (AST)_t;
						if ( _t==null ) throw new MismatchedTokenException();
						_t = _t.getNextSibling();
						if ( ipt.canProceed() ) r = expr(statement);
					}
					else {
						break _loop232;
					}
					
				} while (true);
				}
				_t = __t230;
				_t = _t.getNextSibling();
				break;
			}
			case BREAK:
			{
				AST tmp45_AST_in = (AST)_t;
				match(_t,BREAK);
				_t = _t.getNextSibling();
				r = new CasBreak(); ipt.setBreak();
				break;
			}
			case CONTINUE:
			{
				AST tmp46_AST_in = (AST)_t;
				match(_t,CONTINUE);
				_t = _t.getNextSibling();
				r = new CasContinue(); ipt.setContinue();
				break;
			}
			case RETURNSTR:
			{
				AST __t233 = _t;
				AST tmp47_AST_in = (AST)_t;
				match(_t,RETURNSTR);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t233;
				_t = _t.getNextSibling();
				r = new CasReturn(ipt.rvalue( a )); ipt.setReturn();
				break;
			}
			case WAIT:
			{
				AST __t234 = _t;
				AST tmp48_AST_in = (AST)_t;
				match(_t,WAIT);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t234;
				_t = _t.getNextSibling();
				r = ipt.stopme(a);
				break;
			}
			case FUNCTION_CALL:
			{
				AST __t235 = _t;
				AST tmp49_AST_in = (AST)_t;
				match(_t,FUNCTION_CALL);
				_t = _t.getFirstChild();
				AST tmp50_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				id = tmp50_AST_in.getText();
				arglist=param(_t);
				_t = _retTree;
				_t = __t235;
				_t = _t.getNextSibling();
				r = ipt.funcCall(this, id, arglist);
				break;
			}
			case OR:
			{
				AST __t236 = _t;
				AST tmp51_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				right_or = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t236;
				_t = _t.getNextSibling();
				
				if ( a instanceof CasBool )
				r = ( ((CasBool)a).var ? a : expr(right_or) );
				else
				r = a.or( expr(right_or) );
				
				break;
			}
			case AND:
			{
				AST __t237 = _t;
				AST tmp52_AST_in = (AST)_t;
				match(_t,AND);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				right_and = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t237;
				_t = _t.getNextSibling();
				
				if ( a instanceof CasBool )
				r = ( ((CasBool)a).var ? expr(right_and) : a );
				else
				r = a.and( expr(right_and) );
				
				break;
			}
			case NOT:
			{
				AST __t238 = _t;
				AST tmp53_AST_in = (AST)_t;
				match(_t,NOT);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t238;
				_t = _t.getNextSibling();
				r = a.not();
				break;
			}
			case LESS:
			{
				AST __t239 = _t;
				AST tmp54_AST_in = (AST)_t;
				match(_t,LESS);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t239;
				_t = _t.getNextSibling();
				r = a.lt(b);
				break;
			}
			case LESSEQUAL:
			{
				AST __t240 = _t;
				AST tmp55_AST_in = (AST)_t;
				match(_t,LESSEQUAL);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t240;
				_t = _t.getNextSibling();
				r = a.le(b);
				break;
			}
			case MORE:
			{
				AST __t241 = _t;
				AST tmp56_AST_in = (AST)_t;
				match(_t,MORE);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t241;
				_t = _t.getNextSibling();
				r = a.gt(b);
				break;
			}
			case MOREEQUAL:
			{
				AST __t242 = _t;
				AST tmp57_AST_in = (AST)_t;
				match(_t,MOREEQUAL);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t242;
				_t = _t.getNextSibling();
				r = a.ge(b);
				break;
			}
			case EQUALTO:
			{
				AST __t243 = _t;
				AST tmp58_AST_in = (AST)_t;
				match(_t,EQUALTO);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t243;
				_t = _t.getNextSibling();
				r = a.eq(b);
				break;
			}
			case NOTEQUAL:
			{
				AST __t244 = _t;
				AST tmp59_AST_in = (AST)_t;
				match(_t,NOTEQUAL);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t244;
				_t = _t.getNextSibling();
				r = a.ne(b);
				break;
			}
			case PLUS:
			{
				AST __t245 = _t;
				AST tmp60_AST_in = (AST)_t;
				match(_t,PLUS);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t245;
				_t = _t.getNextSibling();
				r = a.plus(b);
				break;
			}
			case MINUS:
			{
				AST __t246 = _t;
				AST tmp61_AST_in = (AST)_t;
				match(_t,MINUS);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t246;
				_t = _t.getNextSibling();
				r = a.minus(b);
				break;
			}
			case TIMES:
			{
				AST __t247 = _t;
				AST tmp62_AST_in = (AST)_t;
				match(_t,TIMES);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t247;
				_t = _t.getNextSibling();
				r = a.times(b);
				break;
			}
			case SLASH:
			{
				AST __t248 = _t;
				AST tmp63_AST_in = (AST)_t;
				match(_t,SLASH);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t248;
				_t = _t.getNextSibling();
				r = a.lfracts(b);
				break;
			}
			case MODULO:
			{
				AST __t249 = _t;
				AST tmp64_AST_in = (AST)_t;
				match(_t,MODULO);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t249;
				_t = _t.getNextSibling();
				r = a.modulus(b);
				break;
			}
			case NEGATION:
			{
				AST __t250 = _t;
				AST tmp65_AST_in = (AST)_t;
				match(_t,NEGATION);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t250;
				_t = _t.getNextSibling();
				r = a.uminus();
				break;
			}
			case INCAFTER:
			{
				AST __t251 = _t;
				AST tmp66_AST_in = (AST)_t;
				match(_t,INCAFTER);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t251;
				_t = _t.getNextSibling();
				r = a.copy(); ipt.incOrDec(a, true);
				break;
			}
			case DECAFTER:
			{
				AST __t252 = _t;
				AST tmp67_AST_in = (AST)_t;
				match(_t,DECAFTER);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t252;
				_t = _t.getNextSibling();
				r = a.copy(); ipt.incOrDec(a, false);
				break;
			}
			case INCBEFORE:
			{
				AST __t253 = _t;
				AST tmp68_AST_in = (AST)_t;
				match(_t,INCBEFORE);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t253;
				_t = _t.getNextSibling();
				r = ipt.incOrDec(a, true);
				break;
			}
			case DECBEFORE:
			{
				AST __t254 = _t;
				AST tmp69_AST_in = (AST)_t;
				match(_t,DECBEFORE);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t254;
				_t = _t.getNextSibling();
				r = ipt.incOrDec(a, false);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return r;
	}
	
/**
 * primitive types, including void
*/
	public final void primitives(AST _t) throws RecognitionException {
		
		AST primitives_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VOID:
			{
				AST tmp70_AST_in = (AST)_t;
				match(_t,VOID);
				_t = _t.getNextSibling();
				break;
			}
			case INT:
			{
				AST tmp71_AST_in = (AST)_t;
				match(_t,INT);
				_t = _t.getNextSibling();
				break;
			}
			case FLOAT:
			{
				AST tmp72_AST_in = (AST)_t;
				match(_t,FLOAT);
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_boolean:
			{
				AST tmp73_AST_in = (AST)_t;
				match(_t,LITERAL_boolean);
				_t = _t.getNextSibling();
				break;
			}
			case STRING:
			{
				AST tmp74_AST_in = (AST)_t;
				match(_t,STRING);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final CasDataType  fbody(AST _t) throws RecognitionException {
		CasDataType a;
		
		AST fbody_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		a = null;
		
		try {      // for error handling
			AST __t209 = _t;
			AST tmp75_AST_in = (AST)_t;
			match(_t,FUNCTIONBODY);
			_t = _t.getFirstChild();
			{
			a=expr(_t);
			_t = _retTree;
			}
			_t = __t209;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return a;
	}
	
/**
 * list of parameters for a function call
*/
	public final  Vector<CasDataType>  param(AST _t) throws RecognitionException {
		 Vector<CasDataType> arglist ;
		
		AST param_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		arglist = null;
		CasDataType a;
		
		try {      // for error handling
			AST __t256 = _t;
			AST tmp76_AST_in = (AST)_t;
			match(_t,ARGS);
			_t = _t.getFirstChild();
			arglist = new Vector<CasDataType>();
			{
			_loop258:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					a=expr(_t);
					_t = _retTree;
					arglist.add( a );
				}
				else {
					break _loop258;
				}
				
			} while (true);
			}
			_t = __t256;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return arglist ;
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
		"IDENTIFIER",
		"\"boolean\""
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 4611963087012623360L, 13388527648L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	}
	
