package org.geworkbench.engine.cascript;

import antlr.collections.AST;

import java.util.Random;
import java.util.Vector;

/**
 * Interpreter routines that is called directly from the tree walker.
 *
 * @author Behrooz Badii - badiib@gmail.com
 * @version $Id: CasInterpreter.java,v 1.22 2005/12/07 15:47:09 manju Exp $
 */
class CasInterpreter {
    CasSymbolTable symt;
    CasDataTypeImport CDTI;

    final static int fc_none = 0;
    final static int fc_break = 1;
    final static int fc_continue = 2;
    final static int fc_return = 3;

    private int control = fc_none;
    private String label;

    private Random random = new Random();

    public CasInterpreter() {
        symt = new CasSymbolTable(null, -1); //-1 means that we're in a global scope
        CDTI = new CasDataTypeImport();
    }

    /**used for variable initialization in the symbol table
     *
    */
    public void putvar(String id, CasDataType type, Vector<CasDataType> indices, CasDataType value) {
        if (indices == null) {
            if (type instanceof CasModule) {
                symt.put(id, new CasModule(id, ((CasModule)type).getType()));
                /*Testing purposes
                System.out.println("put in casModule");
                System.out.println("Name: " + symt.findVar(id).name);
                System.out.println("Type: " + ((CasModule) symt.findVar(id)).type);*/
            }
            if (type instanceof CasDataPlug) {
                if (CDTI.containsKey(((CasDataPlug)type).getType())) {
                    symt.put(id,
                             new CasDataPlug(id, ((CasDataPlug) type).getType(), CDTI));
                    /*Testing purposes
                    System.out.println("put in casDataPlug");
                    System.out.println("Name: " + symt.findVar(id).name);
                    System.out.println("Type: " +
                                       ((CasDataPlug) symt.findVar(id)).type);*/
                }
                else
                    throw new CasException("The datatype type " + ((CasDataPlug) type).getType() + " for the variable " + id + " is not supported.");
            }
            else {
                if (type instanceof CasString) {
                    symt.put(id, type.copy());
                } else if (type instanceof CasDouble) {
                    symt.put(id, type.copy());
                } else if (type instanceof CasInt) {
                    symt.put(id, type.copy());
                } else if (type instanceof CasBool) {
                    symt.put(id, type.copy());
                }
                symt.findVar(id).setName(id);
                /*Testing purposes
                System.out.println("Name: " + symt.findVar(id).name);*/
            }
        }
        //time to deal with arrays and matrices
        else if (indices.size() == 1) {
            /*Testing purposes
            System.out.println("we are dealing with an array here, with dimensionality of one in assign() call");*/
            if (indices.elementAt(0) instanceof CasInt) {
                symt.put(id, new CasArray(((CasInt) indices.elementAt(0)).getvar(), type));
                symt.findVar(id).setName(id);
                (symt.findVar(id)).initializeArray(); //this is dependent on setName occurring first
                /*Testing purposes
                System.out.println("Name: " + symt.findVar(id).name);*/
            } else throw new CasException("index of array declaration for " + id + " must be an integer");
        } else if (indices.size() == 2) {
            /*Testing purposes
            System.out.println("we are dealing with an array here, with dimensionality of two in assign() call");*/
            if ((indices.elementAt(0) instanceof CasInt) && (indices.elementAt(1) instanceof CasInt)) {
                symt.put(id, new CasMatrix(((CasInt) indices.elementAt(0)).getvar(), ((CasInt) indices.elementAt(0)).getvar(), type));
                symt.findVar(id).setName(id);
                (symt.findVar(id)).initializeMatrix(); //this is dependent on setName occurring first
                /*Testing purposes
                System.out.println("Name: " + symt.findVar(id).name);*/
            } else throw new CasException("indices of two-dimensional array declaration (matrix declaration) for " + id + " must be integers");
        }

        if (value != null) {
            assign(symt.findVar(id), value);
        }
    }

    /**state CAN change in this method
     * this is used in the assignment token in the walker and when you have to initialize a variable
     * make the return type different for each one.  If it is successful, return a, if not, throw an exception.
    */
    public CasDataType assign(CasDataType a, CasDataType b) {
        /*Testing purposes
        System.out.println("in assignment");*/
        //if (b instanceof CasCallReturn) { //this is done before assign is called anyway
        //    b = checkCasCallReturn((CasCallReturn)b);
        //}
        if (b instanceof CasValue) {
            //This method is objectionable.  We are worried that a getValue() call in geWorkBench will affect the system and not just return a value.
            //are the get and set methods implemented correctly in geWorkBench so that we don't have changes to the system?
            b = checkCasValue((CasValue) b);
        }
        if (a.getPartOf() != null) {
            CasDataType x = null;
            /*Testing purposes
            System.out.println("in a array or matrix");*/
            if (a instanceof CasInt && b instanceof CasInt) {
                /*Testing purposes
                System.out.println("new value " + ((CasInt) b).getvar());*/
                x = ((CasInt) rvalue(b));
            }
            if (a instanceof CasDouble) {
                if (b instanceof CasDouble) {
                   /*Testing purposes
                   System.out.println("new value " + ((CasDouble) b).getvar());*/
                    x = ((CasDouble) rvalue(b));
                }
                if (b instanceof CasInt) {
                   /*Testing purposes
                   System.out.println("new value " + ((CasInt) b).getvar());*/
                   x = ((CasDouble) rvalue(b));
                }
            }
            if (a instanceof CasBool && b instanceof CasBool) {
                /*Testing purposes
                System.out.println("new value " + ((CasBool) b).getvar());*/
                x = ((CasBool) rvalue(b));
            }
            if (a instanceof CasString && b instanceof CasString) {
                /*Testing purposes
                System.out.println("new value " + ((CasString) b).getvar());*/
                x = ((CasString) rvalue(b));
            }
            //here, we have x, a will be replaced by x in the structure itself
            if (symt.findVar(a.getPartOf()) instanceof CasArray && x != null) {
                x.setPosition(a.getPosition());
                x.setPartOf(a.getPartOf());
                symt.findVar(x.getPartOf()).setArrayValue(x, x.getPosition());
            }
            if (symt.findVar(a.getPartOf()) instanceof CasMatrix && x != null) {
                x.setPositions(a.getPosition(), a.getPosition2());
                x.setPartOf(a.getPartOf());
                symt.findVar(x.getPartOf()).setMatrixValue(x, x.getPosition(), x.getPosition2());
            }
            /*Testing purposes
            System.out.println("substructure assignment success!");*/
            return new CasBool(true);
        }
        if (a instanceof CasValue) {
            if (b instanceof CasValue) {
                //lefthand side of assignment is set, righthand side is get
                //set is the value that is going to be assigned to
                CasValue set = new CasValue(((CasValue) a).formodule,
                                            "set" + ((CasValue) a).othername,
                                            ((CasValue) a).association);
                //get is the value that set is going to have after this procedure
                CasValue get = new CasValue(((CasValue) b).formodule,
                                            "get" + ((CasValue) b).othername,
                                            ((CasValue) b).association);
                try {
                    Object data = get.getm().invoke(get.getPlugin());
                    set.getm().invoke(set.getPlugin(), data);
                    return new CasBool(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CasException("error in assign for two CasValues");
                }
            }
            else if (b instanceof CasDataPlug) {
                CasValue set = new CasValue(((CasValue) a).formodule,
                            "set" + ((CasValue) a).othername,
                            ((CasValue) a).association);
                try {
                    Object data = ((CasDataPlug)b).getVar();
                    set.getm().invoke(set.getPlugin(),data);
                    return new CasBool(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new CasException("error in assigning a datatype to a CasValue");
                }
            }
            else if (b instanceof CasCallReturn) {
                CasValue set = new CasValue(((CasValue) a).formodule,
                            "set" + ((CasValue) a).othername,
                            ((CasValue) a).association);
                try {
                    Object data = ((CasCallReturn)b).getRetValue();
                    set.getm().invoke(set.getPlugin(),data);
                    return new CasBool(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new CasException("error in assigning a datatype to a CasValue");
                }
            }
        }
        if (null != a.getName()) {
            if (a instanceof CasInt && b instanceof CasInt) {
                CasDataType x = rvalue(b);
                x.setName(a.name);
                symt.setVar(a.name, (CasInt) x);
                return new CasBool(true);
            }
            if (a instanceof CasDouble) {
                if (b instanceof CasDouble) {
                    CasDataType x = rvalue(b);
                    x.setName(a.name);
                    symt.setVar(a.name, (CasDouble) x);
                }
                if (b instanceof CasInt) {
                    CasDataType x = rvalue(b);
                    x.setName(a.name);
                    symt.setVar(a.name, (CasInt) x);
                }
                return new CasBool(true);
            }
            if (a instanceof CasBool && b instanceof CasBool) {
                CasDataType x = rvalue(b);
                x.setName(a.name);
                symt.setVar(a.name, (CasBool) x);
                return new CasBool(true);
            }
            if (a instanceof CasString) {
                if (b instanceof CasBool) {
                    b = new CasString(Boolean.toString(((CasBool)b).getvar()));
                }
                else if (b instanceof CasInt) {
                    b = new CasString(Integer.toString(((CasInt)b).getvar()));
                }
                else if (b instanceof CasDouble) {
                    b = new CasString(Double.toString(((CasDouble)b).getvar()));
                }
                if (b instanceof CasString) {
                    CasDataType x = rvalue(b);
                    x.setName(a.name);
                    symt.setVar(a.name, (CasString) x);
                    return new CasBool(true);
                }
            }
            //should this be allowed?
            if (a instanceof CasModule && b instanceof CasModule) {
                if (((CasModule)a).getType().equals(((CasModule)b).getType())) {
                    //you have make sure the types are the same!
                    CasDataType x = rvalue(b);
                    x.setName(a.name);
                    symt.setVar(a.name, (CasModule) x);
                    return new CasBool(true);
                }
                else
                    throw new CasException("Assignment for modules " + a.getName() + " and " + b.getName() + "is invalid; they are not of the same type");
            }
            if (a instanceof CasDataPlug) {
                //if b is also a CasDataPlug
                //remember to check that anything being assigned to a CasDataPlug has to be assignable to the original interface
                if (b instanceof CasDataPlug) {
                    if (((CasDataPlug) a).getType().equals(((CasDataPlug) b).
                            getType())) {
                        //you have make sure the types are the same!
                        CasDataType x = rvalue(b);
                        x.setName(a.name);
                        symt.setVar(a.name, (CasDataPlug) x);
                        return new CasBool(true);
                    }
                }
                //fix this, this is both for a CasMethod and a CasDataMethod
                else if (b instanceof CasCallReturn) {
                    Object data = ((CasCallReturn) b).getRetValue();
                    try {
                        if (((CasDataPlug) a).getVar().getClass().isAssignableFrom(data.getClass())) {
                            CasDataType x = ((CasDataPlug) a).copy();
                            ((CasDataPlug) x).setVar(data);
                            symt.setVar(x.name, (CasDataPlug) x);
                            return new CasBool(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new CasException("The geWorkBench return value cannot be assigned to " + ((CasDataPlug) a).getName() +
                                               "; it is not compatible with " + ((CasDataPlug) a).getName() + ".");
                    }
                }
                else if (b instanceof CasValue) {
                    CasValue get = new CasValue(((CasValue) b).formodule, "get" + ((CasValue) b).othername, ((CasValue) b).association);
                    try {
                        Object data = get.getm().invoke(get.getPlugin());
                        if (((CasDataPlug)a).getVar().getClass().isAssignableFrom(data.getClass())) {
                            CasDataType x = ((CasDataPlug)a).copy();
                            ((CasDataPlug)x).setVar(data);
                            symt.setVar(x.name, (CasDataPlug) x);
                            return new CasBool(true);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        throw new CasException(((CasValue) b).formodule + "." + ((CasValue) b).othername + " cannot be assigned to " + ((CasDataPlug)a).getName() + " since the datatypes are not compatible.");
                    }
                    //make sure the object from get when invoked is good for the CasDataPlug
                }
                else
                    throw new CasException ("A datatype ("+a.getName()+") can only be assigned values from another datatype, a module's methods, or a module's variables");
                //make sure that the CasDataPlug type is equal to what is either being called using
                //make sure it's the right DataPlug! object var has to be assignable
                //a CasValue, CasMethod, or another CasDataPlug
            }
            if (null != b.getName()) return a.error(b, "=");
            else return a.error("=");
        }
        return a.error("=");
    }
    
    /**
     * helper method that accesses arrays and matrices
     * There is an error here, sometimes we get an array out of bounds exception in this method
     */
    public CasDataType dimensionAccess(String id, Vector<CasDataType> indices) {
        /*Testing purposes
        System.out.println("in dimensionAccess() call");*/
        CasDataType a = symt.findVar(id);
        CasDataType ret;
        if (indices.size() == 1 && a instanceof CasArray) {
            if (indices.elementAt(0) instanceof CasInt) {
                ret = ((CasArray) a).accessArray(((CasInt) indices.elementAt(0)).getvar());
                return ret;
            } else throw new CasException("index of array access for " + a.getName() + " must be an integer");
        } else if (indices.size() == 1 && a instanceof CasMatrix) {
            if (indices.elementAt(0) instanceof CasInt) {
                ret = ((CasMatrix) a).subArrayofMatrix(((CasInt) indices.elementAt(0)).getvar());
                return ret;
            } else throw new CasException("index of array access for " + a.getName() + " must be an integer");
        } else if (indices.size() == 2 && a instanceof CasMatrix) {
            if ((indices.elementAt(0) instanceof CasInt) && (indices.elementAt(1) instanceof CasInt)) {
                ret = ((CasMatrix) a).accessMatrix(((CasInt) indices.elementAt(0)).getvar(), ((CasInt) indices.elementAt(1)).getvar());
                return ret;
            } else throw new CasException("indices of two-dimensional array declaration (matrix declaration) for " + a.getName() + " must be integers");
        } else throw new CasException("there are too many indices, only two dimensional arrays are supported");
    }

    /**
     * this method checks the object within CasCallReturn and either changes it into a CasInt, CasString, CasBool, CasDouble, or keeps the object intact
    */
    public CasDataType checkCasCallReturn(CasCallReturn b) {
        if (b.getRetValue() instanceof Integer) {
            return new CasInt(((Integer)b.getRetValue()).intValue());
        }
        else if (b.getRetValue() instanceof String) {
            return new CasString(((String)b.getRetValue()));
        }
        else if (b.getRetValue() instanceof Boolean) {
            return new CasBool(((Boolean)b.getRetValue()).booleanValue());
        }
        else if (b.getRetValue() instanceof Double) {
            return new CasDouble(((Double)b.getRetValue()).doubleValue());
        }
        else if (b.getRetValue() instanceof Float) {
            return new CasDouble(((Float)b.getRetValue()).doubleValue());
        }
        else {
            return b;
        }
    }

    /**state CAN change in this method
    *This method is objectionable.  We are worried that a getValue() call in geWorkBench will affect the system and not just return a value.
    *This method calls a get<somedataset>() method in geWorkBench and return and object.  If it is an Integer, String, Boolean, Double, or Float
     *it is morphed into the corresponding Cas Class (CasInt, CasString, etc.).
    */
    public CasDataType checkCasValue(CasValue b) {
        CasValue get = new CasValue(((CasValue) b).formodule, "get" + ((CasValue) b).othername, ((CasValue) b).association);
        try {
            Object data = get.getm().invoke(get.getPlugin());
            if (data instanceof Integer) {
                return new CasInt(((Integer)data).intValue());
            }
            else if (data instanceof String) {
                return new CasString(((String)data));
            }
            else if (data instanceof Boolean) {
                return new CasBool(((Boolean)data).booleanValue());
            }
            else if (data instanceof Double) {
                return new CasDouble(((Double)data).doubleValue());
            }
            else if (data instanceof Float) {
                return new CasDouble(((Float)data).doubleValue());
            }
            else {
                return b;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new CasException("Error occurred calling method " + get.othername + " for module " + get.formodule + ".");
        }
    }

    /**
     * waiting is done in seconds, not milliseconds
     * stopme is called in a WAIT statement 
    */
    public CasDataType stopme(CasDataType a) {
        /*Testing purposes
        System.out.println("in stopme");*/
        if (a instanceof CasInt) {
            try {
                Thread.sleep(((CasInt) a).getvar() * 1000);
                return new CasBool(true);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CasException ("problem making thread sleep in ipt.stopme() call");
            }
        }
        else {
            throw new CasException("wait call needs an integer");
        }
    }

    /*state CAN change in this method
    *methodcall occurs when the CasMethod is used, so we will find it in OBJECT_CALL
    */
    public Object MethodCall(String casname, String casmethod, Vector<CasDataType> v) {
        Object ret = null;
        Object [] args = vectortoargs(v, casmethod);
        if (symt.exists(casname) && symt.findVar(casname) instanceof CasDataPlug) {
            //deal with it in another method
            ret = OtherMethodCall(casname, casmethod, args);
            return ret;
        }
        else {
            if (symt.exists(casname + " " + casmethod) &&
                symt.findVar(casname + " " + casmethod) instanceof CasMethod) {
                CasMethod callme = (CasMethod) symt.findVar(casname + " " +
                        casmethod);
                try {
                    ret = callme.m.invoke(callme.getPlugin(), args);
                    return ret;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CasException(
                            "Error occurred in MethodCall in interpreter for pre-existing method for a module");
                }
            } else {
                if (symt.findVar(casname) instanceof CasModule) {
                    Class[] p = argstoclasses(args);
                    symt.put(casname + " " + casmethod,
                             new CasMethod(casname, casmethod,
                                           (CasModule) symt.findVar(casname), p));
                    /*Testing purposes
                    System.out.println("made new method " + casmethod + " for " +
                                       casname);*/
                    CasMethod callme = (CasMethod) symt.findVar(casname + " " +
                            casmethod);
                    try {
                        ret = callme.m.invoke(callme.getPlugin(), args);
                        return ret;
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new CasException(
                                "Error occurred in MethodCall in interpreter for non-pre-existing method");
                    }
                } else {
                    throw new CasException(casname + " is not a module, " +
                                           casname + "." +
                                           casmethod + " cannot be called");
                }
            }
        }
    }

    /* state CAN change in this method buy only for DataPlugs, not Modules
     * this is called by MethodCall if what we're dealing with is a DataPlug and not a Module
    */
    public Object OtherMethodCall(String casname, String casmethod, Object[] args) {
        Object ret = null;
        if (symt.exists(casname + " " + casmethod) &&
            symt.findVar(casname + " " + casmethod) instanceof CasDataMethod) {
            CasDataMethod callme = (CasDataMethod) symt.findVar(casname + " " +
                    casmethod);
            try {
                ret = callme.m.invoke(callme.geta().getVar(), args);
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                throw new CasException(
                        "Error occurred in MethodCall in interpreter for pre-existing method for a datatype");
            }
        } else {
            if (symt.findVar(casname) instanceof CasDataPlug) {
                //you have to change args to the classtypes for that method!
                Class[] p = argstoclasses(args);
                symt.put(casname + " " + casmethod,
                         new CasDataMethod(casname, casmethod,
                                       (CasDataPlug) symt.findVar(casname), p));
                /*Testing purposes
                System.out.println("made new method " + casmethod + " for " +
                                   casname);*/
                CasDataMethod callme = (CasDataMethod) symt.findVar(casname + " " +
                        casmethod);
                try {
                    ret = callme.m.invoke(callme.geta().getVar(), args);
                    return ret;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CasException(
                            "Error occurred in MethodCall in interpreter for non-pre-existing method");
                }
            } else {
                throw new CasException(casname + " is not a module, " +
                                       casname + "." +
                                       casmethod + " cannot be called");
            }
        }
    }

    /**
     * helper function for CasInterpreter.OtherMethodCall
     */
    static Class[] argstoclasses(Object[] args) {
        Class[] p = new Class[args.length];
        for (int i = 0; i < p.length; i++) {
            if (args[i] instanceof Boolean) {
                p[i] = boolean.class;
            } else if (args[i] instanceof Integer) {
                p[i] = int.class;
            } else if (args[i] instanceof Double) {
                p[i] = double.class;
            } else {
                p[i] = args[i].getClass();
            }
        }
        return p;
    }

    /**
     * helper function for CasInterpreter.MethodCall
     */
    static Object[] vectortoargs(Vector<CasDataType> v, String id) {
        Object args[] = v.toArray();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof CasCallReturn) {
                args[i] = ((CasCallReturn)args[i]).getRetValue();
            }
            if (args[i] instanceof CasBool) {
                /*Testing purposes
                System.out.println("Boolean" + ((CasBool) args[i]).getvar());*/
                args[i] = new Boolean(((CasBool) args[i]).getvar());
            } else if (args[i] instanceof CasInt) {
                /*Testing purposes
                System.out.println("Int" + ((CasInt) args[i]).getvar());*/
                args[i] = new Integer(((CasInt) args[i]).getvar());
            } else if (args[i] instanceof CasDouble) {
                /*Testing purposes
                System.out.println("Double" + ((CasDouble) args[i]).getvar());*/
                args[i] = new Double(((CasDouble) args[i]).getvar());
            } else if (args[i] instanceof CasString) {
                /*Testing purposes
                System.out.println("String" + ((CasString) args[i]).getvar());*/
                args[i] = ((CasString) args[i]).getvar();
            } else if (args[i] instanceof CasModule) {
                args[i] = ((CasModule) args[i]).getPlugin();
                /*Testing purposes
                System.out.println("This should be an object");*/
            } else if (args[i] instanceof CasDataPlug) {
                args[i] = ((CasDataPlug) args[i]).getVar();
                /*Testing purposes
                System.out.println("Dataplug in argument to MethodCall");*/
            }
        }
        return args;
    }

    /**
    * gets the variable from the SymbolTable
     */
    public CasDataType getVariable(String s) {
        // default static scoping
        CasDataType x = symt.findVar(s);
        return x;
    }

    /**
     * you need to make a copy, so that when i = j, they don't become the same reference
     */
    public CasDataType rvalue(CasDataType a) {
        if (null == a.name) return a;
        return a.copy();
    }

    /**
     * used in control flow
     */
    public void setBreak() {
        control = fc_break;
    }
    /**
     * used in control flow
     */
    public boolean breakSet() {
        if (control == fc_break) {
            return true;
        } else return false;
    }
    /**
     * used in control flow
     */
    public void setContinue() {
        control = fc_continue;
    }
    /**
     * used in control flow
     */
    public boolean continueSet() {
        if (control == fc_continue) {
            return true;
        } else return false;
    }
    /**
     * used in control flow
     */
    public void setReturn() {
        control = fc_return;
    }
    /**
     * used in control flow
     */
    public void tryResetFlowControl() {
        control = fc_none;
    }
    /**
     * used in control flow
     */
    public void loopNext() {
        if (control == fc_continue) tryResetFlowControl();
    }
    /**
     * used in control flow
     */
    public void loopEnd() {
        if (control == fc_break) tryResetFlowControl();
        symt = symt.Parent();
    }
    /**
     * used in control flow
     */
    public boolean canProceed() {
        return control == fc_none;
    }
    /**
     * used in control flow
     */
    public void loopInit() {
        // create a new symbol table
        symt = new CasSymbolTable(symt, symt.getLevel() + 1);
    }
    /**
     * used in control flow
     */
    public boolean forCanProceed() {
        if (control != fc_none) return false;
        return true;
    }
    /**
     * used in control flow
     */
    public void forNext() {
        if (control == fc_continue) tryResetFlowControl();
    }

    /**
     * creates a user-defined function that can be called by the user later on
     */
    public void makeFunction(String id, Vector<CasArgument> args, AST body, CasSymbolTable s, CasDataType typereturn, int brackets) {
        //if we take the symboltable that was passed in, it is static
        CasFunction a = new CasFunction(id, args, body, s, typereturn, brackets);
        /*Testing purposes
        System.out.println("we're in function " + a.getName() + " with this many brackets:" + a.brackets);*/
        CasArgument temp;
        for (int i = 0; i < a.args.size(); i++) {
            temp = a.args.elementAt(i);
            /*Testing purposes
            System.out.println("Reading arguments: id = " + temp.getId());
            if (temp.getType() instanceof CasModule ) System.out.println("we have a module of type: " + ((CasModule)temp.getType()).getType());
            else System.out.println("the type is: " + temp.getType().getType());
            System.out.println("Number of brackets: " + temp.getBrackets());*/
        }
        if (symt.notexists(a.getName())) {
            symt.put(a.getName(), a);
        } else throw new CasException(a.getName() + " already exists as a function or variable");
    }

    /**
     * used for a function call
     */
    public CasDataType funcCall(CASWalker walker, String id, Vector<CasDataType> argList) {
        CasSymbolTable temp;
        CasDataType whatthefunc = symt.findVar(id);
        if (! (whatthefunc instanceof CasFunction)) return whatthefunc.error(id + "is not a function.");
        Vector<CasArgument> actualargs = ((CasFunction) whatthefunc).getArgs();
        if (actualargs.size() != argList.size()) return whatthefunc.error(id + " has the wrong number of parameters");
        temp = symt;
        //is this the right symbol table? should we be using the symbol that's part of the CasFunction object?
        symt = new CasSymbolTable(((CasFunction) whatthefunc).getParentSymbolTable(), ((CasFunction) whatthefunc).getParentSymbolTable().getLevel() + 1);

        //remember to check arguments against their correct type
        checkarguments(argList, actualargs, id);

        for (int i = 0; i < actualargs.size(); i++) {
            CasDataType a = rvalue(argList.elementAt(i));
            a.setName(actualargs.elementAt(i).getId());
            if (symt.existsinscope(actualargs.elementAt(i).getId())) throw new CasException(actualargs.elementAt(i).getId() + " already exists in " + id);
            else symt.put(actualargs.elementAt(i).getId(), a);
        }
        CasDataType ret = null;
        try {
            ret = walker.fbody(((CasFunction) whatthefunc).getBody());
        } catch (antlr.RecognitionException e) {
            e.printStackTrace();
            throw new CasException("we have a problem in funcCall");
        }
        // no break or continue can go through the function
        if (control == fc_break || control == fc_continue) throw new CasException("nowhere to break or continue");

        // if a return was called
        if (control == fc_return) tryResetFlowControl();

        // remove this symbol table and return
        symt = temp;
        //check the return type before passing it back, if it's not the same return type, throw an error
        //you still have to do this!
        if (((CasFunction)whatthefunc).getReturnType() instanceof CasVoid && ret instanceof CasReturn)
            throw new CasException("you're not supposed to return anything with the function " + id + " + because its return type is void");
        else if (((CasFunction)whatthefunc).getReturnType() instanceof CasVoid && !(ret instanceof CasReturn))
            return new CasVariable("return statement");
        else if (ret instanceof CasReturn) {
            if (checkreturntype(((CasReturn)ret).getRetValue(), (CasFunction) whatthefunc)) {
                return ((CasReturn)ret).getRetValue();
            }
            else
                throw new CasException("bad return type for function " + id + ", you are returning the wrong thing");
        }
        else
            throw new CasException("bad return type for function " + id);
    }

    /**
     * helper function for funcCall
     * must check if the arguments being sent in are matching up
     */
    public void checkarguments(Vector<CasDataType> argList, Vector<CasArgument> actualargs, String id) {
        CasDataType a;
        CasArgument b;
        CasDataType btype;
        for (int i = 0; i < argList.size(); i++){
            a = argList.elementAt(i);
            b = actualargs.elementAt(i);
            btype = b.getType();
            if (btype.getClass().equals(a.getClass())) {
                if (a instanceof CasModule) {
                    if (btype instanceof CasModule) {
                      if (!(((CasModule)a).getType().equals(((CasModule)btype).getType()))) {
                           throw new CasException("argument and parameter are both modules, but wrong module type for argument " + b.getId() + " for method " + id);
                      }
                    }
                }
                if (a instanceof CasDataPlug) {
                    if (btype instanceof CasDataPlug) {
                      if (!(((CasDataPlug)a).getType().equals(((CasDataPlug)btype).getType()))) {
                           throw new CasException("argument and parameter are both datatype, but wrong datatype type for argument " + b.getId() + " for method " + id);
                      }
                    }
                }
                if (a instanceof CasArray) {
                    if (btype instanceof CasArray) {
                      if (!(((CasArray)a).getelementType().equals(((CasArray)btype).getelementType()))) {
                           throw new CasException("argument and parameter are both arrays, but wrong array element type for argument " + b.getId() + " for method " + id);
                      }
                    }
                }
                if (a instanceof CasMatrix) {
                    if (btype instanceof CasMatrix) {
                      if (!(((CasMatrix)a).getelementType().equals(((CasMatrix)btype).getelementType()))) {
                           throw new CasException("argument and parameter are both matrices, but wrong matrix element type for argument " + b.getId() + " for method " + id);
                      }
                    }
                }
            }
            else
                throw new CasException("invalid type for arguments for method" + id);
        }
    }
    
    /**fix this stuff
     * checks the return type of a function during funcCall
     * i feel this code can be written much better than what is here now
     * maybe we can make checkreturntype based on the ret data, and instead of
     * passing in func, we can pass in it's returntype and its dimensions
     * then there is a possibility of recursion with arrays and matrices
    */
    public boolean checkreturntype(CasDataType ret, CasFunction func) {
        CasDataType type = func.getReturnType();
        int dimensions = func.getBrackets();
        if (dimensions == 0) {
            if (type instanceof CasModule) {
                if (ret instanceof CasModule) {
                    String t = ((CasModule) ret).getType();
                    if (((CasModule)type).getType().equals(t)) {
                        return true;
                    }
                }
            }
            if (type instanceof CasDataPlug) {
                if (ret instanceof CasDataPlug) {
                    String t = ((CasDataPlug) ret).getType();
                    if (((CasDataPlug)type).getType().equals(t)) {
                        return true;
                    }
                }
            }
            else if (type.getClass().equals(ret.getClass()))
                return true;
        }
        if (dimensions == 1) {
            if (ret instanceof CasArray) {
                CasDataType r = ((CasArray) ret).getelementType();
                if (type.getClass().equals(r.getClass()))
                    return true;
            }
        }
        if (dimensions == 2) {
            if (ret instanceof CasMatrix) {
                CasDataType r = ((CasMatrix)ret).getelementType();
                if (type.getClass().equals(r.getClass()))
                    return true;
            }
        }
        return false;
    }
    
    public CasDataType incOrDec(CasDataType a, boolean iod) {
        if (a.getName() == null && a.getPartOf() == null) {
            if (iod == true)
                throw new CasException ("Trying to increment a constant is not allowed");
            else
                throw new CasException ("Trying to decrement a constant is not allowed");
        }
        if (a.getPartOf() != null) {
            if (!(a instanceof CasInt || a instanceof CasDouble)) {
                if (iod == true)
                    throw new CasException("Trying to increment a variable inside " + a.getPartOf() + "that is not a double or integer is not allowed.");
                else
                    throw new CasException("Trying to decrement a variable inside " + a.getPartOf() + "that is not a double or integer is not allowed.");
            }
            if (iod == true)
                return a.ib();
            else  
                return a.db();
        }
        else if (a.getName() != null) {
            if (!(a instanceof CasInt || a instanceof CasDouble)) {
                if (iod == true)
                    throw new CasException("Trying to increment a variable " + a.getName() + "that is not a double or integer is not allowed.");
                else
                    throw new CasException("Trying to decrement a variable " + a.getName() + "that is not a double or integer is not allowed.");                
            }
            if (iod == true )
                return a.ib();
            else
                return a.db();
        }    
        if (iod == true)
            throw new CasException("Error occurred in incrementation");
        else
            throw new CasException("Error occurred in decrementation");
    }
}
