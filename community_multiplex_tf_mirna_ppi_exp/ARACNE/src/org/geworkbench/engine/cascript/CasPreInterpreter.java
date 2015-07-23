//to do - remember to set the name of CasPreData and the datatype it is holding, these must ALWAYS occur!
//during assignment, remember to check the cas call return, anything touching it is unknown!
//during assignment, make sure anything on the right hand assigned is already initialized!
//if it has a name, it must be initialized! if it is not, then we have a problem!
package org.geworkbench.engine.cascript;

import antlr.collections.AST;

import java.util.Random;
import java.util.Vector;

/**
 * Interpreter routines that is called directly from the tree walkerfor purely semantic analysis checks.
 *
 * @author Behrooz Badii - badiib@gmail.com
 */
class CasPreInterpreter {
    CasPreSymbolTable psymt;
    CasDataTypeImport CDTI;

    final static int fc_none = 0;
    final static int fc_break = 1;
    final static int fc_continue = 2;
    final static int fc_return = 3;

    private int control = fc_none;
    private String label;

    private Random random = new Random();

    public CasPreInterpreter() {
        psymt = new CasPreSymbolTable(null, -1); //-1 means that we're in a global scope
        CDTI = new CasDataTypeImport();
    }

    /**used for variable initialization in the symbol table
     *
    */
    //to do - set declared variable to true for EVERYTHING in arrays and matrices
    
//indices was changed from CasDataType to CasPreData, check if it has been initialized before creating the variable!
    public void putvar(String id, CasDataType type, Vector<CasPreData> indices, CasPreData value) {
        if (indices == null) {
            if (type instanceof CasModule) {
                psymt.putVar(id, new CasPreData(new CasModule(id, ((CasModule)type).getType()), true, true, false));
                psymt.findVar(id).setName(id);
                psymt.findVar(id).getData().setName(id);
                /*Testing purposes
                System.out.println("put in casModule");
                System.out.println("Name: " + symt.findVar(id).name);
                System.out.println("Type: " + ((CasModule) symt.findVar(id)).type);*/
            }
            if (type instanceof CasDataPlug) {
                if (CDTI.containsKey(((CasDataPlug)type).getType())) {
                    psymt.putVar(id, new CasPreData(new CasDataPlug(id, ((CasDataPlug)type).getType(), CDTI), true, false, true));
                    psymt.findVar(id).setName(id);
                    psymt.findVar(id).getData().setName(id);
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
                    psymt.putVar(id, new CasPreData(type.copy(), true, false, true));
                } else if (type instanceof CasDouble) {
                    psymt.putVar(id, new CasPreData(type.copy(), true, false, true));
                } else if (type instanceof CasInt) {
                    psymt.putVar(id, new CasPreData(type.copy(), true, false, true));
                } else if (type instanceof CasBool) {
                    psymt.putVar(id, new CasPreData(type.copy(), true, false, true));
                }
                psymt.findVar(id).getData().setName(id);
                psymt.findVar(id).setName(id);
                /*Testing purposes
                System.out.println("Name: " + symt.findVar(id).name);*/
            }
        }
        //time to deal with arrays and matrices
        else if (indices.size() == 1) {
            /*Testing purposes
            System.out.println("we are dealing with an array here, with dimensionality of one in assign() call");*/
            if (indices.elementAt(0).getData() instanceof CasInt) {
                psymt.putVar(id, new CasPreData(new CasArray(((CasInt) indices.elementAt(0).getData()).getvar(), type), true, true, true));
                psymt.findVar(id).setName(id);
                psymt.findVar(id).getData().setName(id);
                (psymt.findVar(id).getData()).initializeArray(); //this is dependent on setName occurring first
//put a helper function here for preanalysis to initialize EVERY array value!
                /*Testing purposes
                System.out.println("Name: " + symt.findVar(id).name);*/
            } else throw new CasException("index of array declaration for " + id + " must be an integer");
        } else if (indices.size() == 2) {
            /*Testing purposes
            System.out.println("we are dealing with an array here, with dimensionality of two in assign() call");*/
            if ((indices.elementAt(0).getData() instanceof CasInt) && (indices.elementAt(1).getData() instanceof CasInt)) {
                psymt.putVar(id, new CasPreData(new CasMatrix(((CasInt) indices.elementAt(0).getData()).getvar(), ((CasInt) indices.elementAt(1).getData()).getvar(), type), true, true, true));
                psymt.findVar(id).setName(id);
                psymt.findVar(id).getData().setName(id);
                (psymt.findVar(id).getData()).initializeMatrix(); //this is dependent on setName occurring first
//put a helper function here for preanalysis to initialize EVERY MATRIX value!
                /*Testing purposes
                System.out.println("Name: " + symt.findVar(id).name);*/
            } else throw new CasException("indices of two-dimensional array declaration (matrix declaration) for " + id + " must be integers");
        }

        if (value != null) {
            assign(psymt.findVar(id), value);
        }
    }

    /**state CAN change in this method
     * this is used in the assignment token in the walker and when you have to initialize a variable
     * make the return type different for each one.  If it is successful, return a, if not, throw an exception.
    */
    public CasPreData assign(CasPreData a, CasPreData b) {
        CasDataType aD = a.getData();
        CasDataType bD = b.getData();
        /*Testing purposes
        System.out.println("in assignment");*/
        //if (b instanceof CasCallReturn) { //this is done before assign is called anyway
        //    b = checkCasCallReturn((CasCallReturn)b);
        //}
        if (bD instanceof CasValue) {
            //This method is objectionable.  We are worried that a getValue() call in geWorkBench will affect the system and not just return a value.
            //are the get and set methods implemented correctly in geWorkBench so that we don't have changes to the system?
            bD = checkCasValue((CasValue) bD);
            b.setData(bD);
        }
//this means we have to find do more fussing with arrays and matrices here
        if (aD.getPartOf() != null) {
            CasDataType x = null;
            /*Testing purposes
            System.out.println("in a array or matrix");*/
            if (aD instanceof CasInt && bD instanceof CasInt) {
                /*Testing purposes
                System.out.println("new value " + ((CasInt) b).getvar());*/
                x = ((CasInt) rvalue(bD));
            }
            if (aD instanceof CasDouble) {
                if (bD instanceof CasDouble) {
                   /*Testing purposes
                   System.out.println("new value " + ((CasDouble) b).getvar());*/
                    x = ((CasDouble) rvalue(bD));
                }
                if (bD instanceof CasInt) {
                   /*Testing purposes
                   System.out.println("new value " + ((CasInt) b).getvar());*/
                   x = ((CasDouble) rvalue(bD));
                }
            }
            if (aD instanceof CasBool && bD instanceof CasBool) {
                /*Testing purposes
                System.out.println("new value " + ((CasBool) b).getvar());*/
                x = ((CasBool) rvalue(bD));
            }
            if (aD instanceof CasString && bD instanceof CasString) {
                /*Testing purposes
                System.out.println("new value " + ((CasString) b).getvar());*/
                x = ((CasString) rvalue(bD));
            }
            if (aD instanceof CasArray && bD instanceof CasArray) {
                /*Testing purposes
                System.out.println("new value " + ((CasString) b).getvar());*/
                x = ((CasArray) rvalue(bD));
            }
            //here, we have x, a will be replaced by x in the structure itself
//fuss with this line
            if ((psymt.findVar(aD.getPartOf())).getData() instanceof CasArray && x != null) {
//mess with this following line a bit - still has to happen to change initialization values inside array
                ((CasPreArray)psymt.findVar(aD.getPartOf())).setArrayValue(a, aD.getPosition());
            }
//fuss with this line
            if ((psymt.findVar(aD.getPartOf())).getData() instanceof CasMatrix && x != null) {
                if (bD instanceof CasArray) {
                    throw new CasException("assigning an array to a row in a matrix is not allowed.");
                }
                else {
//mess with this following line a bit - still has to happen to change initialization values inside matrix
                  ((CasPreMatrix)psymt.findVar(aD.getPartOf())).setMatrixValue(a, aD.getPosition(), aD.getPosition2());
                }
            }
            /*Testing purposes
            System.out.println("substructure assignment success!");*/
            return new CasPreData(new CasBool(true), false, true, true);
        }
//we do NOT want this to occur! no invocation allowed!
        if (aD instanceof CasValue) {
            if (bD instanceof CasValue) {
                //lefthand side of assignment is set, righthand side is get
                //set is the value that is going to be assigned to
                CasValue set = new CasValue(((CasValue) aD).formodule,
                                            "set" + ((CasValue) aD).othername,
                                            ((CasValue) aD).association);
                //get is the value that set is going to have after this procedure
                CasValue get = new CasValue(((CasValue) bD).formodule,
                                            "get" + ((CasValue) bD).othername,
                                            ((CasValue) bD).association);
//DO TYPE CHECKING HERE!                
                /*try {
                    Object data = get.getm().invoke(get.getPlugin());
                    set.getm().invoke(set.getPlugin(), data);
                    return new CasBool(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CasException("error in assign for two CasValues");
                }*/
            }
            else if (bD instanceof CasDataPlug) {
                CasValue set = new CasValue(((CasValue) aD).formodule,
                            "set" + ((CasValue) aD).othername,
                            ((CasValue) aD).association);
//DO TYPE CHECKING HERE!
                /*try {
                    Object data = ((CasDataPlug)bD).getVar();
                    set.getm().invoke(set.getPlugin(),data);
                    return new CasBool(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new CasException("error in assigning a datatype to a CasValue");
                }*/
            }
            else if (bD instanceof CasCallReturn) {
                CasValue set = new CasValue(((CasValue) aD).formodule,
                            "set" + ((CasValue) aD).othername,
                            ((CasValue) aD).association);
//DO TYPE CHECKING HERE!
                /*try {
                    Object data = ((CasCallReturn)bD).getRetValue();
                    set.getm().invoke(set.getPlugin(),data);
                    return new CasBool(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    throw new CasException("error in assigning a datatype to a CasValue");
                }*/
            }
        }
        if (null != a.getName()) {
            if (aD instanceof CasInt && bD instanceof CasInt) {
//no more variable changing until we get to research part of the semantic analysis
                a.setInitialized(true);
                //symt.setVar(aD.name, (CasInt) x);
                return new CasPreData(new CasBool(true), false, true, true);
            }
            if (aD instanceof CasDouble) {
                if (bD instanceof CasDouble) {
                    a.setInitialized(true);
                    //symt.setVar(aD.name, (CasDouble) x);
//no more variable changing until we get to research part of the semantic analysis
                }
                if (bD instanceof CasInt) {
                    a.setInitialized(true);
                    //symt.setVar(a.name, (CasInt) x);
//no more variable changing until we get to research part of the semantic analysis
                }
                return new CasPreData(new CasBool(true), false, true, true);
            }
            if (aD instanceof CasBool && bD instanceof CasBool) {
                a.setInitialized(true);
                //symt.setVar(aD.name, (CasBool) x);
//no more variable changing until we get to research part of the semantic analysis
                return new CasPreData(new CasBool(true), false, true, true);
            }
            if (aD instanceof CasArray && bD instanceof CasArray) {
                if (((CasArray)aD).getvar().length == ((CasArray)bD).getvar().length) {    
                    a.setInitialized(true);
                    //symt.setVar(aD.name, (CasArray) x);
//no more variable changing until we get to research part of the semantic analysis
                    return new CasPreData(new CasBool(true), false, true, true);
                }
                else {
                    throw new CasException("An array of size " + ((CasArray)bD).getvar().length + " is being assigned to an array of size " + ((CasArray)aD).getvar().length + ".  This is not allowed.");
                }
            }
            if (aD instanceof CasMatrix && bD instanceof CasMatrix) {
                if ((((CasMatrix)aD).getvar().length == ((CasMatrix)bD).getvar().length) && (((CasMatrix)aD).getvar()[0].length == ((CasMatrix)bD).getvar()[0].length)) {    
                    a.setInitialized(true);
                    //symt.setVar(aD.name, (CasMatrix) x);
//no more variable changing until we get to research part of the semantic analysis
                    return new CasPreData(new CasBool(true), false, true, true);
                }
                else {
                    throw new CasException("A matrix of dimensions " + ((CasMatrix)bD).getvar().length + "x" + ((CasMatrix)bD).getvar()[0].length + " is being assigned to a matrix of dimensions " + ((CasMatrix)aD).getvar().length + "x" + ((CasMatrix)aD).getvar()[0].length + ".  This is not allowed.");
                }
            }
            if (aD instanceof CasString) {
                CasDataType x = bD.copy();
                if (x instanceof CasBool) {
                    x = new CasString(Boolean.toString(((CasBool)bD).getvar()));
                }
                else if (x instanceof CasInt) {
                    x = new CasString(Integer.toString(((CasInt)bD).getvar()));
                }
                else if (x instanceof CasDouble) {
                    x = new CasString(Double.toString(((CasDouble)bD).getvar()));
                }
                if (x instanceof CasString) {
// no more variable changing until we get to research part of the semantic analysis
                    a.setInitialized(true);
                    //symt.setVar(aD.name, (CasString) x);
                    return new CasPreData(new CasBool(true), false, true, true);
                }
            }
            //should this be allowed?
            if (aD instanceof CasModule && bD instanceof CasModule) {
                if (((CasModule)aD).getType().equals(((CasModule)bD).getType())) {
                    //you have make sure the types are the same!
                    a.setInitialized(true);
// no more variable changing until we get to research part of the semantic analysis
                    //symt.setVar(a.name, (CasModule) x);
                    return new CasPreData(new CasBool(true), false, true, true);
                }
                else
                    throw new CasException("Assignment for modules " + a.getName() + " and " + b.getName() + "is invalid; they are not of the same type");
            }
            if (aD instanceof CasDataPlug) {
                //if b is also a CasDataPlug
                //remember to check that anything being assigned to a CasDataPlug has to be assignable to the original interface
                if (bD instanceof CasDataPlug) {
                    if (((CasDataPlug) aD).getType().equals(((CasDataPlug) bD).
                            getType())) {
                        //you have make sure the types are the same
                        a.setInitialized(true);
// no more variable changing until we get to research part of the semantic analysis                        
                        //symt.setVar(a.name, (CasDataPlug) x);
                        return new CasPreData(new CasBool(true), false, true, true);
                    }
                    else
                    throw new CasException("Assignment for datatypes " + a.getName() + " and " + b.getName() + "is invalid; they are not of the same type");
                }
                //fix this, this is both for a CasMethod and a CasDataMethod
                else if (bD instanceof CasCallReturn) {
                    Object data = ((CasCallReturn) bD).getRetValue();
                    try {
                        if (((CasDataPlug) aD).getVar().getClass().isAssignableFrom(data.getClass())) {
                            a.setInitialized(true);
// no more variable changing until we get to research part of the semantic analysis                        
                            //symt.setVar(x.name, (CasDataPlug) x);
                            return new CasPreData(new CasBool(true), false, true, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new CasException("The geWorkBench return value cannot be assigned to " + ((CasDataPlug) aD).getName() +
                                               "; it is not compatible with " + ((CasDataPlug) aD).getName() + ".");
                    }
                }
                else if (bD instanceof CasValue) {
                    CasValue get = new CasValue(((CasValue) bD).formodule, "get" + ((CasValue) bD).othername, ((CasValue) bD).association);
                    try {
                        Object data = get.getm().invoke(get.getPlugin());
                        if (((CasDataPlug)aD).getVar().getClass().isAssignableFrom(data.getClass())) {
                            a.setInitialized(true);
// no more variable changing until we get to research part of the semantic analysis                        
                            //symt.setVar(x.name, (CasDataPlug) x);
                            return new CasPreData(new CasBool(true), false, true, true);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        throw new CasException(((CasValue) bD).formodule + "." + ((CasValue) bD).othername + " cannot be assigned to " + ((CasDataPlug)aD).getName() + " since the datatypes are not compatible.");
                    }
                    //make sure the object from get when invoked is good for the CasDataPlug
                }
                else
                    throw new CasException ("A datatype ("+aD.getName()+") can only be assigned values from another datatype, a module's methods, or a module's variables");
                //make sure that the CasDataPlug type is equal to what is either being called using
                //make sure it's the right DataPlug! object var has to be assignable
                //a CasValue, CasMethod, or another CasDataPlug
            }
            if (null != bD.getName()) return new CasPreData(aD.error(bD, "="), false, true, true);
            else return new CasPreData(aD.error("="), false, true, true);
        }
        return new CasPreData(aD.error("="), false, true, true);
    }
    
    /**
     * helper method that accesses arrays and matrices
     * There is an error here, sometimes we get an array out of bounds exception in this method
     */
    
//this method should just return the type which the array holds, given the length is correct
    public CasPreData dimensionAccess(String id, Vector<CasPreData> indices) {
        /*Testing purposes
        System.out.println("in dimensionAccess() call");*/
        CasPreData aP = psymt.findVar(id);
        CasDataType a = psymt.findVar(id).getData();
        CasPreData ret;
        if (indices.size() == 1 && a instanceof CasArray) {
            if (indices.elementAt(0).getData() instanceof CasInt) {
                ret = new CasPreData(((CasArray) a).accessArray(((CasInt) indices.elementAt(0).getData()).getvar()), aP.getDeclared(), aP.getInitialized(), aP.getKnown());
                return ret;
            } else throw new CasException("index of array access for " + a.getName() + " must be an integer");
        } else if (indices.size() == 1 && a instanceof CasMatrix) {
            if (indices.elementAt(0).getData() instanceof CasInt) {
                ret = new CasPreData(((CasMatrix) a).subArrayofMatrix(((CasInt) indices.elementAt(0).getData()).getvar()), aP.getDeclared(), aP.getInitialized(), aP.getKnown());
                ret.getData().setPartOf(a.name);
                ret.getData().setPosition(((CasInt)indices.elementAt(0).getData()).getvar());
                return ret;
            } else throw new CasException("index of array access for " + a.getName() + " must be an integer");
        } else if (indices.size() == 2 && a instanceof CasMatrix) {
            if ((indices.elementAt(0).getData() instanceof CasInt) && (indices.elementAt(1).getData() instanceof CasInt)) {
                ret = new CasPreData(((CasMatrix) a).accessMatrix(((CasInt) indices.elementAt(0).getData()).getvar(), ((CasInt) indices.elementAt(1).getData()).getvar()),aP.getDeclared(), aP.getInitialized(), aP.getKnown());
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
//in this method, we want NO invocation, just return an empty return value!
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
    
//in this method, check for the integer, and if it is an integer, just jump over it, no waiting necessary
    public CasPreData stopme(CasPreData a) {
        /*Testing purposes
        System.out.println("in stopme");*/
        if (a.getData() instanceof CasInt) {
            try {
                Thread.sleep(((CasInt) a.getData()).getvar() * 1000);
                return new CasPreData(new CasVariable("wait statement"), false, false, false);
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
    
//check the type of the method, do not invoke it, just return the type
    public Object MethodCall(String casname, String casmethod, Vector<CasPreData> b) {
        Object ret = null;
        Vector<CasDataType> v = null;
        CasDataType t = null;
        for (int i = 0; i < b.size(); i++) {
            if (b.elementAt(i).getInitialized())  {
              v.add(b.elementAt(i).getData());  
            }
            else if (b.elementAt(i).getDeclared()){
                throw new CasException ("Error, caWorkBench call cannot be completed, variable " + b.elementAt(i).getName() + " is not initialized");
            }
        }
        Object [] args = vectortoargs(v, casmethod);
        if (psymt.exists(casname) && (psymt.findVar(casname)).getData() instanceof CasDataPlug) {
            //deal with it in another method
            ret = OtherMethodCall(casname, casmethod, args);
            return ret;
        }
        else {
            if (psymt.exists(casname + " " + casmethod) &&
                (psymt.findVar(casname + " " + casmethod)).getData() instanceof CasMethod) {
                CasMethod callme = (CasMethod) (psymt.findVar(casname + " " +
                        casmethod)).getData();
//do not do this, just return valid return type!
                try {
                    //ret = callme.m.invoke(callme.getPlugin(), args);
                    return ret;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CasException(
                            "Error occurred in MethodCall in interpreter for pre-existing method for a module");
                }                
            } else {
                if ((psymt.findVar(casname)).getData() instanceof CasModule) {
                    Class[] p = argstoclasses(args);
                    psymt.putVar(casname + " " + casmethod, new CasPreData(
                             new CasMethod(casname, casmethod,
                                           (CasModule) (psymt.findVar(casname)).getData(), p), true, true, true));
                    /*Testing purposes
                    System.out.println("made new method " + casmethod + " for " +
                                       casname);*/
                    CasMethod callme = (CasMethod) (psymt.findVar(casname + " " +
                            casmethod)).getData();
//do not do this, just return valid return type!
                    try {
                        //ret = callme.m.invoke(callme.getPlugin(), args);
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
    
//check the type of the method, do not invoke it, just return the type
    public Object OtherMethodCall(String casname, String casmethod, Object[] args) {
        Object ret = null;
        if (psymt.exists(casname + " " + casmethod) &&
            (psymt.findVar(casname + " " + casmethod)).getData() instanceof CasDataMethod) {
            CasDataMethod callme = (CasDataMethod) (psymt.findVar(casname + " " +
                    casmethod)).getData();
//do not do this, just return valid return type!
            try {
                //ret = callme.m.invoke(callme.geta().getVar(), args);
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                throw new CasException(
                        "Error occurred in MethodCall in interpreter for pre-existing method for a datatype");
            }
        } else {
            if ((psymt.findVar(casname)).getData() instanceof CasDataPlug) {
                //you have to change args to the classtypes for that method!
                Class[] p = argstoclasses(args);
                psymt.putVar(casname + " " + casmethod, new CasPreData(
                         new CasDataMethod(casname, casmethod,
                                       (CasDataPlug) (psymt.findVar(casname).getData()), p), true, true, true));
                /*Testing purposes
                System.out.println("made new method " + casmethod + " for " +
                                   casname);*/
                CasDataMethod callme = (CasDataMethod) psymt.findVar(casname + " " +
                        casmethod).getData();
//do not do this, just return valid return type!
                try {
                    //ret = callme.m.invoke(callme.geta().getVar(), args);
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
    public CasPreData getVariable(String s) {
        // default static scoping
        CasPreData x = psymt.findVar(s);
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
        psymt = psymt.Parent();
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
        psymt = new CasPreSymbolTable(psymt, psymt.getLevel() + 1);
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
    public void makeFunction(String id, Vector<CasArgument> args, AST body, CasPreSymbolTable s, CasDataType typereturn, int brackets) {
        //if we take the symboltable that was passed in, it is static
        CasPreFunction a = new CasPreFunction(id, args, body, s, typereturn, brackets);
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
        if (psymt.notexists(a.getName())) {
            psymt.putVar(a.getName(),new CasPreData (a, true, true, true));
        } else throw new CasException(a.getName() + " already exists as a function or variable");
    }

    /**
     * used for a function call
     */
    public CasPreData funcCall(CASSemantics walker, String id, Vector<CasPreData> argList) {
        CasPreSymbolTable temp;
        CasDataType whatthefunc = psymt.findVar(id).getData();
        if (! (whatthefunc instanceof CasFunction)) throw new CasException(id + "is not a function.");
        Vector<CasArgument> actualargs = ((CasFunction) whatthefunc).getArgs();
        if (actualargs.size() != argList.size()) throw new CasException(id + " has the wrong number of parameters");
        temp = psymt;
        //is this the right symbol table? should we be using the symbol that's part of the CasFunction object?
        psymt = new CasPreSymbolTable(((CasPreFunction) whatthefunc).getParentSymbolTable(), ((CasFunction) whatthefunc).getParentSymbolTable().getLevel() + 1);
        //remember to check arguments against their correct type
        checkarguments(argList, actualargs, id);

        for (int i = 0; i < actualargs.size(); i++) {
            CasDataType a = rvalue(argList.elementAt(i).getData());
            a.setName(actualargs.elementAt(i).getId());
            if (psymt.existsinscope(actualargs.elementAt(i).getId())) throw new CasException(actualargs.elementAt(i).getId() + " already exists in " + id);
            else psymt.putVar(actualargs.elementAt(i).getId(), new CasPreData (a, true, true, argList.elementAt(i).getKnown()));
        }
        CasPreData ret = null;
        try {
            ret = walker.fbody(((CasPreFunction) whatthefunc).getBody());
        } catch (antlr.RecognitionException e) {
            e.printStackTrace();
            throw new CasException("we have a problem in funcCall");
        }
        // no break or continue can go through the function
        if (control == fc_break || control == fc_continue) throw new CasException("nowhere to break or continue");

        // if a return was called
        if (control == fc_return) tryResetFlowControl();

        // remove this symbol table and return
        psymt = temp;
        //check the return type before passing it back, if it's not the same return type, throw an error
        //you still have to do this!
        if (((CasFunction)whatthefunc).getReturnType() instanceof CasVoid && ret.getData() instanceof CasReturn)
            throw new CasException("you're not supposed to return anything with the function " + id + " + because its return type is void");
        else if (((CasFunction)whatthefunc).getReturnType() instanceof CasVoid && !(ret.getData() instanceof CasReturn))
            return new CasPreData(new CasVariable("return statement"), true, true, true);
        else if (ret.getData() instanceof CasReturn) {
            if (checkreturntype(((CasReturn)ret.getData()).getRetValue(), (CasPreFunction) whatthefunc)) {
                return new CasPreData(((CasReturn)ret.getData()).getRetValue(), ret.getDeclared(), ret.getInitialized(), ret.getKnown());
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
    public void checkarguments(Vector<CasPreData> argList, Vector<CasArgument> actualargs, String id) {
        CasDataType a;
        CasArgument b;
        CasDataType btype;
        for (int i = 0; i < argList.size(); i++){
            a = argList.elementAt(i).getData();
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
    public boolean checkreturntype(CasDataType ret, CasPreFunction func) {
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
    
    public CasPreData incOrDec(CasPreData a, boolean iod) {
        if (a.getData().getName() == null && a.getData().getPartOf() == null) {
            if (iod == true)
                throw new CasException ("Trying to increment a constant is not allowed");
            else
                throw new CasException ("Trying to decrement a constant is not allowed");
        }
        if (a.getData().getPartOf() != null) {
            if (!(a.getData() instanceof CasInt || a.getData() instanceof CasDouble)) {
                if (iod == true)
                    throw new CasException("Trying to increment a variable inside " + a.getData().getPartOf() + "that is not a double or integer is not allowed.");
                else
                    throw new CasException("Trying to decrement a variable inside " + a.getData().getPartOf() + "that is not a double or integer is not allowed.");
            }
            if (iod == true) {
                a.setData(a.getData().ib());
                return a;
            }
            else {  
                a.setData(a.getData().db());
                return a;
            }
        }
        else if (a.getData().getName() != null) {
            if (!(a.getData() instanceof CasInt || a.getData() instanceof CasDouble)) {
                if (iod == true)
                    throw new CasException("Trying to increment a variable " + a.getData().getName() + "that is not a double or integer is not allowed.");
                else
                    throw new CasException("Trying to decrement a variable " + a.getData().getName() + "that is not a double or integer is not allowed.");                
            }
            if (iod == true ) {
                a.setData(a.getData().ib());
                return a;
            }
            else {
                a.setData(a.getData().db());
                return a;
            }
        }    
        if (iod == true)
            throw new CasException("Error occurred in incrementation");
        else
            throw new CasException("Error occurred in decrementation");
    }
    
    public CasPreData PreDataMaker(CasDataType cdt, CasPreData a, CasPreData b) {
        CasPreData ret = new CasPreData(cdt, false, true, false);
        if (a.getName() != null && a.getInitialized() == false) {
            throw new CasException (a.getName() + " cannot be used, it is not initialized.");
        }
        if (b != null) {
            if (b.getName() != null && b.getInitialized() == false) {
                throw new CasException (b.getName() + " cannot be used, it is not initialized.");
            }
            if (a.getKnown() == true && b.getKnown() == true) {
                ret.setKnown(true);
            }
        }
        else {
            if (a.getKnown() == true)
                ret.setKnown(true);
        }
        return ret;
    }
}

