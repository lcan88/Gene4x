// $ANTLR 2.7.5 (20050128): "Semantics.g" -> "CASSemantics.java"$
package org.geworkbench.engine.cascript;
import antlr.MismatchedTokenException;
import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.collections.AST;
import antlr.collections.impl.BitSet;

import java.util.Vector;


public class CASSemantics extends antlr.TreeParser       implements CASSemanticsTokenTypes
 {

    CasPreInterpreter ipt = new CasPreInterpreter();
    AST mainbody = null;
public CASSemantics() {
	tokenNames = _tokenNames;
}

/**
 * the root of the initial tree is going to be walkme, start here!
*/
	public final void walkme(AST _t) throws RecognitionException {
		
		AST walkme_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t2 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,PROG);
			_t = _t.getFirstChild();
			{
			_loop4:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==PUBLICVAR)) {
					publicvar(_t);
					_t = _retTree;
				}
				else {
					break _loop4;
				}
				
			} while (true);
			}
			{
			int _cnt6=0;
			_loop6:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==FUNCTION)) {
					function(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt6>=1 ) { break _loop6; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt6++;
			} while (true);
			}
			_t = __t2;
			_t = _t.getNextSibling();
			
			//make new symboltable for main
			ipt.psymt = new CasPreSymbolTable(ipt.psymt, ipt.psymt.getLevel()+1);
			fbody(mainbody);
			//get rid of main's symbol table
			ipt.psymt = ipt.psymt.Parent();
			
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
			AST __t8 = _t;
			AST tmp2_AST_in = (AST)_t;
			match(_t,PUBLICVAR);
			_t = _t.getFirstChild();
			variable(_t);
			_t = _retTree;
			_t = __t8;
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
			AST __t10 = _t;
			AST tmp3_AST_in = (AST)_t;
			match(_t,FUNCTION);
			_t = _t.getFirstChild();
			{
			typereturn=type(_t);
			_t = _retTree;
			{
			_loop13:
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
					break _loop13;
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
			_t = __t10;
			_t = _t.getNextSibling();
			
			if (id.equals("main")) {
			mainbody = fbody;
			}
			ipt.makeFunction(id, argList, fbody, ipt.psymt, typereturn, brackets);
			
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
		CasPreData value = null;
		CasDataType typereturn;
		Vector<CasPreData> indices = null;
		
		try {      // for error handling
			AST __t21 = _t;
			AST tmp7_AST_in = (AST)_t;
			match(_t,VARIABLE);
			_t = _t.getFirstChild();
			typereturn=type(_t);
			_t = _retTree;
			{
			int _cnt27=0;
			_loop27:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==IDENTIFIER)) {
					AST __t23 = _t;
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
					_t = __t23;
					_t = _t.getNextSibling();
					{
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case EQUAL:
					{
						AST __t26 = _t;
						AST tmp10_AST_in = (AST)_t;
						match(_t,EQUAL);
						_t = _t.getFirstChild();
						value=expr(_t);
						_t = _retTree;
						_t = __t26;
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
					
					if (ipt.psymt.existsinscope(id)) {
					throw new CasException(id + " already exists as a function or variable");
					}
					ipt.putvar(id, typereturn, indices, value);
					indices = null;
					value = null;
					
				}
				else {
					if ( _cnt27>=1 ) { break _loop27; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt27++;
			} while (true);
			}
			_t = __t21;
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
			AST __t33 = _t;
			AST tmp11_AST_in = (AST)_t;
			match(_t,TYPE);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INT:
			case DOUBLE:
			case BOOLSTR:
			case STRING:
			case VOID:
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
			_t = __t33;
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
			else if (temp.equals("double"))
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
			AST __t15 = _t;
			AST tmp16_AST_in = (AST)_t;
			match(_t,ARGDEC);
			_t = _t.getFirstChild();
			{
			_loop19:
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
					break _loop19;
				}
				
			} while (true);
			}
			_t = __t15;
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
	public final Vector<CasPreData>  index(AST _t) throws RecognitionException {
		Vector<CasPreData> v;
		
		AST index_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		v = new Vector<CasPreData>();
		CasPreData aindex = null;
		
		try {      // for error handling
			AST __t29 = _t;
			AST tmp20_AST_in = (AST)_t;
			match(_t,INDEX);
			_t = _t.getFirstChild();
			{
			_loop31:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					aindex=expr(_t);
					_t = _retTree;
					v.add(aindex);
				}
				else {
					break _loop31;
				}
				
			} while (true);
			}
			_t = __t29;
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
	public final CasPreData  expr(AST _t) throws RecognitionException {
		CasPreData r;
		
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
		CasPreData a = null,b = null;
		String id = "";
		String id2 = "";
		Vector<CasPreData> arglist = null;
		
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NUM_INT:
			{
				AST tmp21_AST_in = (AST)_t;
				match(_t,NUM_INT);
				_t = _t.getNextSibling();
				r = new CasPreData(new CasInt(Integer.parseInt(tmp21_AST_in.getText())), false, true, true);
				break;
			}
			case NUM_DOUBLE:
			{
				AST tmp22_AST_in = (AST)_t;
				match(_t,NUM_DOUBLE);
				_t = _t.getNextSibling();
				r = new CasPreData(new CasDouble(Double.parseDouble(tmp22_AST_in.getText())), false, true, true);
				break;
			}
			case TRUE:
			{
				AST tmp23_AST_in = (AST)_t;
				match(_t,TRUE);
				_t = _t.getNextSibling();
				r = new CasPreData(new CasBool(true), false, true, true);
				break;
			}
			case FALSE:
			{
				AST tmp24_AST_in = (AST)_t;
				match(_t,FALSE);
				_t = _t.getNextSibling();
				r = new CasPreData(new CasBool(false), false, true, true);
				break;
			}
			case IDENTIFIER:
			{
				AST __t42 = _t;
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
				_t = __t42;
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
				r = new CasPreData(new CasString( str.getText().toString()), false, true, true);
				break;
			}
			case VARIABLE:
			{
				variable(_t);
				_t = _retTree;
				r = new CasPreData(new CasVariable("variable declaration"), false, false, false);
				break;
			}
			case ASSIGNMENT:
			{
				AST __t44 = _t;
				AST tmp27_AST_in = (AST)_t;
				match(_t,ASSIGNMENT);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t44;
				_t = _t.getNextSibling();
				
				r = ipt.assign(a,b); //for assign, remember to use b's flag to affect a's flags
				
				break;
			}
			case OBJECT_VALUE:
			{
				AST __t45 = _t;
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
				_t = __t45;
				_t = _t.getNextSibling();
				id2 = ID21.getText();
				if (ipt.psymt.findVar(id).getData() instanceof CasModule) {
				//should you be checking if id a CasModule in the firstplace?
				r = new CasPreData(new CasValue(id, id2, ((CasModule)ipt.psymt.findVar(id).getData())), true, true, false);
				/*Testing purposes System.out.println("we're in object_value");*/
				}
				else {
				throw new CasException(id + "is not a module, so it can't have any variables");
				}
				
				break;
			}
			case OBJECT_CALL:
			{
				AST __t46 = _t;
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
				_t = __t46;
				_t = _t.getNextSibling();
				id2 = ID22.getText();
				//r has to be something different, it has to come from MethodCall
				//MethodCall should tell the difference between a CasModule and a CasDataPlug
				/*Testing purposes
				System.out.println("we're in object_call");*/
				r = new CasPreData(ipt.checkCasCallReturn(new CasCallReturn(ipt.MethodCall(id, id2, arglist))), true, true, false);
				
				break;
			}
			case PRINT:
			{
				AST __t47 = _t;
				AST tmp32_AST_in = (AST)_t;
				match(_t,PRINT);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t47;
				_t = _t.getNextSibling();
				r = a; a.getData().print();
				break;
			}
			case IFSTR:
			{
				AST __t48 = _t;
				AST tmp33_AST_in = (AST)_t;
				match(_t,IFSTR);
				_t = _t.getFirstChild();
				AST __t49 = _t;
				AST tmp34_AST_in = (AST)_t;
				match(_t,CONDITION);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t49;
				_t = _t.getNextSibling();
				AST __t50 = _t;
				AST tmp35_AST_in = (AST)_t;
				match(_t,THEN);
				_t = _t.getFirstChild();
				thenif = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t50;
				_t = _t.getNextSibling();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ELSE:
				{
					AST __t52 = _t;
					AST tmp36_AST_in = (AST)_t;
					match(_t,ELSE);
					_t = _t.getFirstChild();
					elseif = (AST)_t;
					if ( _t==null ) throw new MismatchedTokenException();
					_t = _t.getNextSibling();
					_t = __t52;
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
				_t = __t48;
				_t = _t.getNextSibling();
				
				if ( !( a.getData() instanceof CasBool ) )
				return new CasPreData(a.getData().error( "if: expression should be bool" ), false, false, false);
				if ( ((CasBool)a.getData()).var )
				r = expr( thenif );
				else if ( null != elseif )
				r = expr( elseif );
				
				break;
			}
			case WHILESTR:
			{
				AST __t53 = _t;
				AST tmp37_AST_in = (AST)_t;
				match(_t,WHILESTR);
				_t = _t.getFirstChild();
				ipt.loopInit();
				AST __t54 = _t;
				AST tmp38_AST_in = (AST)_t;
				match(_t,CONDITION);
				_t = _t.getFirstChild();
				cond = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t54;
				_t = _t.getNextSibling();
				rest = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t53;
				_t = _t.getNextSibling();
				
				a = expr(cond);
				if ( !(a.getData() instanceof CasBool ))
				return new CasPreData(a.getData().error ( "while: expression should be bool" ), false, false, false);
				while (!ipt.breakSet() && ((CasBool)a.getData()).getvar()) {
				if (ipt.continueSet()) {
				ipt.tryResetFlowControl();
				continue;
				}
				r = expr (rest);
				if (!ipt.breakSet())
				a = expr(cond);
				if ( !(a.getData() instanceof CasBool ))
				return new CasPreData(a.getData().error ( "while: expression should be bool" ), false, false, false);
				}
				ipt.loopEnd();
				
				break;
			}
			case FORSTR:
			{
				AST __t55 = _t;
				AST tmp39_AST_in = (AST)_t;
				match(_t,FORSTR);
				_t = _t.getFirstChild();
				ipt.loopInit();
				AST __t56 = _t;
				AST tmp40_AST_in = (AST)_t;
				match(_t,FORLEFT);
				_t = _t.getFirstChild();
				r=expr(_t);
				_t = _retTree;
				_t = __t56;
				_t = _t.getNextSibling();
				AST __t57 = _t;
				AST tmp41_AST_in = (AST)_t;
				match(_t,FORMID);
				_t = _t.getFirstChild();
				cond2 = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t57;
				_t = _t.getNextSibling();
				AST __t58 = _t;
				AST tmp42_AST_in = (AST)_t;
				match(_t,FORRIGHT);
				_t = _t.getFirstChild();
				after = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t58;
				_t = _t.getNextSibling();
				AST __t59 = _t;
				AST tmp43_AST_in = (AST)_t;
				match(_t,FORBODY);
				_t = _t.getFirstChild();
				forbody = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t59;
				_t = _t.getNextSibling();
				_t = __t55;
				_t = _t.getNextSibling();
				a = expr(cond2);
				if ( !(a.getData() instanceof CasBool ))
				return new CasPreData(a.getData().error ( "for: expression should be bool" ), false, false, false);
				while (!ipt.breakSet() && ((CasBool)a.getData()).getvar()) {
				if (ipt.continueSet()) {
				ipt.tryResetFlowControl();
				continue;
				}
				r = expr (forbody);
				if (!ipt.breakSet()) {
				expr(after);
				a = expr(cond2);
				}
				if ( !(a.getData() instanceof CasBool ))
				return new CasPreData(a.getData().error ( "for: expression should be bool" ), false, false, false);
				}
				ipt.loopEnd();
				
				break;
			}
			case STATEMENTS:
			{
				AST __t60 = _t;
				AST tmp44_AST_in = (AST)_t;
				match(_t,STATEMENTS);
				_t = _t.getFirstChild();
				{
				_loop62:
				do {
					if (_t==null) _t=ASTNULL;
					if (((_t.getType() >= INT && _t.getType() <= IDENTIFIER))) {
						statement = (AST)_t;
						if ( _t==null ) throw new MismatchedTokenException();
						_t = _t.getNextSibling();
						if ( ipt.canProceed() ) r = expr(statement);
					}
					else {
						break _loop62;
					}
					
				} while (true);
				}
				_t = __t60;
				_t = _t.getNextSibling();
				break;
			}
			case BREAK:
			{
				AST tmp45_AST_in = (AST)_t;
				match(_t,BREAK);
				_t = _t.getNextSibling();
				r = new CasPreData(new CasBreak(), false, false, false); ipt.setBreak();
				break;
			}
			case CONTINUE:
			{
				AST tmp46_AST_in = (AST)_t;
				match(_t,CONTINUE);
				_t = _t.getNextSibling();
				r = new CasPreData(new CasContinue(), false, false, false); ipt.setContinue();
				break;
			}
			case RETURNSTR:
			{
				AST __t63 = _t;
				AST tmp47_AST_in = (AST)_t;
				match(_t,RETURNSTR);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t63;
				_t = _t.getNextSibling();
				r = new CasPreData(new CasReturn(ipt.rvalue( a.getData() )), a.getDeclared(), a.getInitialized(), a.getKnown()); ipt.setReturn();
				break;
			}
			case WAIT:
			{
				AST __t64 = _t;
				AST tmp48_AST_in = (AST)_t;
				match(_t,WAIT);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t64;
				_t = _t.getNextSibling();
				r = ipt.stopme(a);
				break;
			}
			case FUNCTION_CALL:
			{
				AST __t65 = _t;
				AST tmp49_AST_in = (AST)_t;
				match(_t,FUNCTION_CALL);
				_t = _t.getFirstChild();
				AST tmp50_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				id = tmp50_AST_in.getText();
				arglist=param(_t);
				_t = _retTree;
				_t = __t65;
				_t = _t.getNextSibling();
				r = ipt.funcCall(this, id, arglist);
				break;
			}
			case OR:
			{
				AST __t66 = _t;
				AST tmp51_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				right_or = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t66;
				_t = _t.getNextSibling();
				
				if ( a.getData() instanceof CasBool )
				r = ( ((CasBool)a.getData()).var ? ipt.PreDataMaker(a.getData().and(a.getData()),a,b) : ipt.PreDataMaker(a.getData().and( expr(right_or).getData()),a,b) );
				else
				r = ipt.PreDataMaker(a.getData().or( expr(right_or).getData()),a,b);
				
				break;
			}
			case AND:
			{
				AST __t67 = _t;
				AST tmp52_AST_in = (AST)_t;
				match(_t,AND);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				right_and = (AST)_t;
				if ( _t==null ) throw new MismatchedTokenException();
				_t = _t.getNextSibling();
				_t = __t67;
				_t = _t.getNextSibling();
				
				if ( a.getData() instanceof CasBool )
				r = ( ((CasBool)a.getData()).var ? ipt.PreDataMaker(a.getData().and( expr(right_and).getData()),a,b) : ipt.PreDataMaker(a.getData().and(a.getData()),a,b) );
				else
				r = ipt.PreDataMaker(a.getData().and( expr(right_and).getData()),a,b);
				
				break;
			}
			case NOT:
			{
				AST __t68 = _t;
				AST tmp53_AST_in = (AST)_t;
				match(_t,NOT);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t68;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().not(),a,b);
				break;
			}
			case LESS:
			{
				AST __t69 = _t;
				AST tmp54_AST_in = (AST)_t;
				match(_t,LESS);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t69;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().lt(b.getData()),a,b);
				break;
			}
			case LESSEQUAL:
			{
				AST __t70 = _t;
				AST tmp55_AST_in = (AST)_t;
				match(_t,LESSEQUAL);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t70;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().le(b.getData()),a,b);
				break;
			}
			case MORE:
			{
				AST __t71 = _t;
				AST tmp56_AST_in = (AST)_t;
				match(_t,MORE);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t71;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().gt(b.getData()),a,b);
				break;
			}
			case MOREEQUAL:
			{
				AST __t72 = _t;
				AST tmp57_AST_in = (AST)_t;
				match(_t,MOREEQUAL);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t72;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().ge(b.getData()),a,b);
				break;
			}
			case EQUALTO:
			{
				AST __t73 = _t;
				AST tmp58_AST_in = (AST)_t;
				match(_t,EQUALTO);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t73;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().eq(b.getData()),a,b);
				break;
			}
			case NOTEQUAL:
			{
				AST __t74 = _t;
				AST tmp59_AST_in = (AST)_t;
				match(_t,NOTEQUAL);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t74;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().ne(b.getData()),a,b);
				break;
			}
			case PLUS:
			{
				AST __t75 = _t;
				AST tmp60_AST_in = (AST)_t;
				match(_t,PLUS);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t75;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().plus(b.getData()),a,b);
				break;
			}
			case MINUS:
			{
				AST __t76 = _t;
				AST tmp61_AST_in = (AST)_t;
				match(_t,MINUS);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t76;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().minus(b.getData()),a,b);
				break;
			}
			case TIMES:
			{
				AST __t77 = _t;
				AST tmp62_AST_in = (AST)_t;
				match(_t,TIMES);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t77;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().times(b.getData()),a,b);
				break;
			}
			case SLASH:
			{
				AST __t78 = _t;
				AST tmp63_AST_in = (AST)_t;
				match(_t,SLASH);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t78;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().lfracts(b.getData()),a,b);
				break;
			}
			case MODULO:
			{
				AST __t79 = _t;
				AST tmp64_AST_in = (AST)_t;
				match(_t,MODULO);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t79;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().modulus(b.getData()),a,b);
				break;
			}
			case NEGATION:
			{
				AST __t80 = _t;
				AST tmp65_AST_in = (AST)_t;
				match(_t,NEGATION);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t80;
				_t = _t.getNextSibling();
				r = ipt.PreDataMaker(a.getData().uminus(),a,b);
				break;
			}
			case INCAFTER:
			{
				AST __t81 = _t;
				AST tmp66_AST_in = (AST)_t;
				match(_t,INCAFTER);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t81;
				_t = _t.getNextSibling();
				r = a.copy(); ipt.incOrDec(a, true);
				break;
			}
			case DECAFTER:
			{
				AST __t82 = _t;
				AST tmp67_AST_in = (AST)_t;
				match(_t,DECAFTER);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t82;
				_t = _t.getNextSibling();
				r = a.copy(); ipt.incOrDec(a, false);
				break;
			}
			case INCBEFORE:
			{
				AST __t83 = _t;
				AST tmp68_AST_in = (AST)_t;
				match(_t,INCBEFORE);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t83;
				_t = _t.getNextSibling();
				r = ipt.incOrDec(a, true);
				break;
			}
			case DECBEFORE:
			{
				AST __t84 = _t;
				AST tmp69_AST_in = (AST)_t;
				match(_t,DECBEFORE);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				_t = __t84;
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
			case DOUBLE:
			{
				AST tmp72_AST_in = (AST)_t;
				match(_t,DOUBLE);
				_t = _t.getNextSibling();
				break;
			}
			case BOOLSTR:
			{
				AST tmp73_AST_in = (AST)_t;
				match(_t,BOOLSTR);
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
	
	public final CasPreData  fbody(AST _t) throws RecognitionException {
		CasPreData a;
		
		AST fbody_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		a = null;
		
		try {      // for error handling
			AST __t39 = _t;
			AST tmp75_AST_in = (AST)_t;
			match(_t,FUNCTIONBODY);
			_t = _t.getFirstChild();
			{
			a=expr(_t);
			_t = _retTree;
			}
			_t = __t39;
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
	public final  Vector<CasPreData>  param(AST _t) throws RecognitionException {
		 Vector<CasPreData> arglist ;
		
		AST param_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		arglist = null;
		CasPreData a;
		
		try {      // for error handling
			AST __t86 = _t;
			AST tmp76_AST_in = (AST)_t;
			match(_t,ARGS);
			_t = _t.getFirstChild();
			arglist = new Vector<CasPreData>();
			{
			_loop88:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					a=expr(_t);
					_t = _retTree;
					arglist.add( a );
				}
				else {
					break _loop88;
				}
				
			} while (true);
			}
			_t = __t86;
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
		"\"double\"",
		"\"boolean\"",
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
		"NUM_DOUBLE",
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
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 4611963087012623360L, 13388527648L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	}
	
