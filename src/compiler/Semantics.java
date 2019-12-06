package compiler;

// Following is the semantics class: 
// The meaning M of a Statement is a State 
// The meaning M of a Expression is a Value 

 
public class Semantics { 

    State M (Program p) {
        return M (p.body, new State());  
    } 
    
//    State initialState (Block b) {
//        State state = new State(); 
////        Value intUndef = new IntValue();
//        for (Statement s: b.members){
//        	if(s.getClass() == Assignment.class){
//        		Assignment a = (Assignment)s;
//        		System.out.println(a);
//        		state.put(a.target, a.source);
//        	}
//        }
//         
//        return state; 
//    }
   
    State M (Statement s, State state) {
        if (s instanceof Skip) return M((Skip)s, state); 
        if (s instanceof Assignment)  return M((Assignment)s, state); 
        if (s instanceof Conditional)  return M((Conditional)s, state); 
        if (s instanceof Loop)  return M((Loop)s, state); 
        if (s instanceof Block)  return M((Block)s, state); 
        if (s instanceof Print)  return M((Print)s, state);
        if (s instanceof Input)  return M((Input)s, state);
        throw new IllegalArgumentException("should never reach here"); 
    } 
   
    State M (Skip s, State state) { 
        return state; 
    } 
   
    State M (Assignment a, State state) { 
        return state.onion(a.target, M (a.source, state)); 
    } 
   
    State M (Block b, State state) { 
        for (Statement s : b.members)
            state = M (s, state); 
        return state; 
    } 

    State M (Conditional c, State state) { 
        if (M(c.test, state).boolValue( )) 
            return M (c.thenbranch, state); 
        else 
            return M (c.elsebranch, state); 
    } 

	State M (Loop l, State state) { 
        if (M(l.test, state).boolValue())
            return M (l.body, state); 
        else return state; 
    } 
	
	State M (Print p, State state) {
		M(p.source, state);
        return state;
    }
	
	State M (Input i, State state) {
        return state.onion(i.id, M(i.source, state));
    }

    Value applyBinary (Operator op, Value v1, Value v2) { 
        if(v1 == null || v2 == null) {
        	System.err.println("reference to undeclared value");
            System.exit(1);
        }
        if( v1.type( ) != v2.type( )){
        	if(v1.type( ) == Type.INT&&v2.type( )==Type.FLOAT) {
        		return new FloatValue(v1.intValue() + v2.floatValue());
        	}else if(v1.type( )==Type.FLOAT&&v2.type( ) == Type.INT) {
        		return new FloatValue(v1.floatValue() + v2.intValue());
        	}else if(v1.type( ) == Type.CHAR&&v2.type( )==Type.INT) {
        		return new IntValue(v1.charValue() + v2.intValue());
        	}else if(v1.type( )==Type.INT&&v2.type( ) == Type.CHAR) {
        		return new IntValue(v1.intValue() + v2.charValue());
        	}
        	System.err.println("mismatched types");
            System.exit(1);
        }
    	
    	if (op.ArithmeticOp()) {
			if (v1.type() == Type.INT) {
				if (op.val.equals(Operator.PLUS))
					return new IntValue(v1.intValue() + v2.intValue());
				if (op.val.equals(Operator.MINUS))
					return new IntValue(v1.intValue() - v2.intValue());
				if (op.val.equals(Operator.TIMES))
					return new IntValue(v1.intValue() * v2.intValue());
				if (op.val.equals(Operator.DIV))
					return new IntValue(v1.intValue() / v2.intValue());
			}
			if (v1.type() == Type.FLOAT) {
				if (op.val.equals(Operator.PLUS))
					return new FloatValue(v1.floatValue() + v2.floatValue());
				if (op.val.equals(Operator.MINUS))
					return new FloatValue(v1.floatValue() - v2.floatValue());
				if (op.val.equals(Operator.TIMES))
					return new FloatValue(v1.floatValue() * v2.floatValue());
				if (op.val.equals(Operator.DIV))
					return new FloatValue(v1.floatValue() / v2.floatValue());
			}
			if (v1.type() == Type.STR) {
				if (op.val.equals(Operator.PLUS))
					return new StrValue(v1.strValue() + v2.strValue());
			}
		} else if (op.BooleanOp()) {
			if (v1.type() == Type.BOOL) {
				if (op.val.equals(Operator.AND))
					return new BoolValue(v1.boolValue() && v2.boolValue());
				if (op.val.equals(Operator.OR))
					return new BoolValue(v1.boolValue() || v2.boolValue());
			}
		} else if (op.RelationalOp()) {
			if (v1.type() == Type.INT) {
				if (op.val.equals(Operator.LT))
					return new BoolValue(v1.intValue() < v2.intValue());
				if (op.val.equals(Operator.LE))
					return new BoolValue(v1.intValue() <= v2.intValue());
				if (op.val.equals(Operator.EQ))
					return new BoolValue(v1.intValue() == v2.intValue());
				if (op.val.equals(Operator.NE))
					return new BoolValue(v1.intValue() != v2.intValue());
				if (op.val.equals(Operator.GT))
					return new BoolValue(v1.intValue() > v2.intValue());
				if (op.val.equals(Operator.GE))
					return new BoolValue(v1.intValue() >= v2.intValue());
			}
			if (v1.type() == Type.FLOAT) {
				if (op.val.equals(Operator.LT))
					return new BoolValue(v1.floatValue() < v2.floatValue());
				if (op.val.equals(Operator.LE))
					return new BoolValue(v1.floatValue() <= v2.floatValue());
				if (op.val.equals(Operator.EQ))
					return new BoolValue(v1.floatValue() == v2.floatValue());
				if (op.val.equals(Operator.NE))
					return new BoolValue(v1.floatValue() != v2.floatValue());
				if (op.val.equals(Operator.GT))
					return new BoolValue(v1.floatValue() > v2.floatValue());
				if (op.val.equals(Operator.GE))
					return new BoolValue(v1.floatValue() >= v2.floatValue());
			}
			if (v1.type() == Type.BOOL) {
				if (op.val.equals(Operator.EQ))
					return new BoolValue(v1.boolValue() == v2.boolValue());
				if (op.val.equals(Operator.NE))
					return new BoolValue(v1.boolValue() != v2.boolValue());
			}
			if (v1.type() == Type.CHAR) {
				if (op.val.equals(Operator.LT))
					return new BoolValue(v1.charValue() < v2.charValue());
				if (op.val.equals(Operator.LE))
					return new BoolValue(v1.charValue() <= v2.charValue());
				if (op.val.equals(Operator.EQ))
					return new BoolValue(v1.charValue() == v2.charValue());
				if (op.val.equals(Operator.NE))
					return new BoolValue(v1.charValue() != v2.charValue());
				if (op.val.equals(Operator.GT))
					return new BoolValue(v1.charValue() > v2.charValue());
				if (op.val.equals(Operator.GE))
					return new BoolValue(v1.charValue() >= v2.charValue());
			}
			if (v1.type() == Type.STR) {
				if (op.val.equals(Operator.EQ))
					return new BoolValue(v1.strValue() == v2.strValue());
				if (op.val.equals(Operator.NE))
					return new BoolValue(v1.strValue() != v2.strValue());
			}
		}
    	
    	
//        if (op.val.equals(Operator.INT_PLUS))  
//            return new IntValue(v1.intValue( ) + v2.intValue( )); 
//        if (op.val.equals(Operator.INT_MINUS))  
//            return new IntValue(v1.intValue( ) - v2.intValue( )); 
//        if (op.val.equals(Operator.INT_TIMES))  
//            return new IntValue(v1.intValue( ) * v2.intValue( )); 
//        if (op.val.equals(Operator.INT_DIV))  
//            return new IntValue(v1.intValue( ) / v2.intValue( ));         
// 
//        if (op.val.equals(Operator.FLOAT_PLUS))  
//            return new FloatValue(v1.floatValue( ) + v2.floatValue( )); 
//        if (op.val.equals(Operator.FLOAT_MINUS))  
//            return new FloatValue(v1.floatValue( ) - v2.floatValue( )); 
//        if (op.val.equals(Operator.FLOAT_TIMES))  
//            return new FloatValue(v1.floatValue( ) * v2.floatValue( )); 
//        if (op.val.equals(Operator.FLOAT_DIV))  
//            return new FloatValue(v1.floatValue( ) / v2.floatValue( )); 
//
//        if (op.val.equals(Operator.INT_LT)) 
//            return new BoolValue(v1.intValue( ) < v2.intValue( )); 
//        if (op.val.equals(Operator.INT_LE)) 
//            return new BoolValue(v1.intValue( ) <= v2.intValue( )); 
//        if (op.val.equals(Operator.INT_EQ)) 
//            return new BoolValue(v1.intValue( ) == v2.intValue( )); 
//        if (op.val.equals(Operator.INT_NE)) 
//            return new BoolValue(v1.intValue( ) != v2.intValue( )); 
//        if (op.val.equals(Operator.INT_GT)) 
//            return new BoolValue(v1.intValue( ) > v2.intValue( )); 
//        if (op.val.equals(Operator.INT_GE)) 
//            return new BoolValue(v1.intValue( ) >= v2.intValue( )); 
//
//        if (op.val.equals(Operator.FLOAT_LT)) 
//            return new BoolValue(v1.floatValue( ) <  v2.floatValue( )); 
//        if (op.val.equals(Operator.FLOAT_LE)) 
//            return new BoolValue(v1.floatValue( ) <= v2.floatValue( )); 
//        if (op.val.equals(Operator.FLOAT_EQ)) 
//            return new BoolValue(v1.floatValue( ) == v2.floatValue( )); 
//        if (op.val.equals(Operator.FLOAT_NE)) 
//            return new BoolValue(v1.floatValue( ) != v2.floatValue( )); 
//        if (op.val.equals(Operator.FLOAT_GT)) 
//           return new BoolValue(v1.floatValue( ) >  v2.floatValue( )); 
//        if (op.val.equals(Operator.FLOAT_GE)) 
//            return new BoolValue(v1.floatValue( ) >= v2.floatValue( )); 
//
//        if (op.val.equals(Operator.CHAR_LT)) 
//            return new BoolValue(v1.charValue( ) <  v2.charValue( )); 
//        if (op.val.equals(Operator.CHAR_LE)) 
//            return new BoolValue(v1.charValue( ) <= v2.charValue( )); 
//        if (op.val.equals(Operator.CHAR_EQ)) 
//            return new BoolValue(v1.charValue( ) == v2.charValue( )); 
//        if (op.val.equals(Operator.CHAR_NE)) 
//            return new BoolValue(v1.charValue( ) != v2.charValue( )); 
//        if (op.val.equals(Operator.CHAR_GT)) 
//            return new BoolValue(v1.charValue( ) >  v2.charValue( )); 
//        if (op.val.equals(Operator.CHAR_GE)) 
//            return new BoolValue(v1.charValue( ) >= v2.charValue( )); 
//
//        if (op.val.equals(Operator.BOOL_EQ)) 
//            return new BoolValue(v1.boolValue( ) == v2.boolValue( )); 
//        if (op.val.equals(Operator.BOOL_NE)) 
//            return new BoolValue(v1.boolValue( ) != v2.boolValue( )); 
//        if (op.val.equals(Operator.AND)) 
//            return new BoolValue(v1.boolValue( ) && v2.boolValue( )); 
//        if (op.val.equals(Operator.OR)) 
//            return new BoolValue(v1.boolValue( ) || v2.boolValue( )); 
        // student exercise 
        //add float , bool, and Char(maybe) 
        throw new IllegalArgumentException("should never reach here"); 
    }  
     
    Value applyUnary (Operator op, Value v) { 
    	if(v == null){
        	System.err.println("reference to undefined variable");
            System.exit(1);
        }
        if (op.val.equals(Operator.NOT)) 
            return new BoolValue(!v.boolValue( )); 
        else if (op.val.equals(Operator.NEG)) {
        	if (v.type() == Type.INT)
        		return new IntValue(-v.intValue( )); 
        	if (v.type() == Type.FLOAT)
        		return new FloatValue(-v.floatValue( )); 
        }
//        else if (op.val.equals(Operator.INT_NEG)) 
//            return new IntValue(-v.intValue( )); 
//        else if (op.val.equals(Operator.FLOAT_NEG)) 
//            return new FloatValue(-v.floatValue( )); 
//        else if (op.val.equals(Operator.I2F)) 
//            return new FloatValue((float)(v.intValue( )));  
//        else if (op.val.equals(Operator.F2I)) 
//            return new IntValue((int)(v.floatValue( ))); 
//        else if (op.val.equals(Operator.C2I)) 
//            return new IntValue((int)(v.charValue( ))); 
//        else if (op.val.equals(Operator.I2C)) 
//            return new CharValue((char)(v.intValue( ))); 
        throw new IllegalArgumentException("should never reach here"); 
    }  

    Value M (Expression e, State state) {
        if (e instanceof Value)  
            return (Value)e; 
        if (e instanceof Variable) {
          	if(!state.containsKey(e)){
            	System.err.println("reference to undeclared variable");
                System.exit(1);
            }
        	return (Value)(state.get(e));
        }
        if (e instanceof Binary) { 
            Binary b = (Binary)e; 
            return applyBinary (b.op,  
                                M(b.term1, state), M(b.term2, state)); 
        } 
        if (e instanceof Unary) { 
            Unary u = (Unary)e; 
            return applyUnary(u.op, M(u.term, state)); 
        } 
        throw new IllegalArgumentException("should never reach here"); 
    } 

    public static void main(String args[]) { 
        Parser parser  = new Parser(new Lexer(args[0])); 
        Program prog = parser.program(); 
//        prog.display();
        System.out.println("\nBegin type checking..."); 
//        System.out.println("Type map:"); 
//        TypeMap map = StaticTypeCheck.typing(prog.decpart); 
//        map.display(); 
//        StaticTypeCheck.V(prog); 
//        Program out = TypeTransformer.T(prog, map); 
//        System.out.println("Output AST"); 
//        out.display(); 
        Semantics semantics = new Semantics();
        State state = semantics.M(prog); 
        System.out.println("Final State"); 
        state.display( ); 
    } 
} 

