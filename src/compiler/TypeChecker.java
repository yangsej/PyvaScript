package compiler;

import java.util.ArrayList;

// Following is the semantics class: 
// The meaning M of a Statement is a State 
// The meaning M of a Expression is a Value 

public class TypeChecker {

	SymbolTable M(Program p) {
		return M(p.body, new SymbolTable());
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

	SymbolTable M(Statement s, SymbolTable state) {
		if (s instanceof Skip)
			return M((Skip) s, state);
		if (s instanceof Assignment)
			return M((Assignment) s, state);
		if (s instanceof Conditional)
			return M((Conditional) s, state);
		if (s instanceof Loop)
			return M((Loop) s, state);
		if (s instanceof Block)
			return M((Block) s, state);
		if (s instanceof Print)
			return M((Print) s, state);
		if (s instanceof Input)
			return M((Input) s, state);
		throw new IllegalArgumentException("should never reach here");
	}

	SymbolTable M(Skip s, SymbolTable state) {
		return state;
	}

	SymbolTable M(Assignment a, SymbolTable state) {
		if (a.target instanceof ListItem) {
			ListItem li = (ListItem) a.target;
			Expression item = state.get(a.target);
			List l = null;
			int index = 0;
			for (Expression i : li.index) {
				index = M(i, state).intValue();

				try {
					l = (List) item;
					item = l.members.get(index);
				} catch (ClassCastException error) {
					System.err.println("object is not subscriptable");
					System.exit(1);
				}
			}
			l.members.set(index, M(a.source, state));
			return state;
		}
		return state.onion(a.target, M(a.source, state));
	}

	SymbolTable M(Block b, SymbolTable state) {
		for (Statement s : b.members)
			state = M(s, state);
		return state;
	}

	SymbolTable M(Conditional c, SymbolTable state) {
		if (M(c.test, state).boolValue())
			return M(c.thenbranch, state);
		else
			return M(c.elsebranch, state);
	}

	SymbolTable M(Loop l, SymbolTable state) {
		if (M(l.test, state).boolValue())
			return M(l.body, state);
		else
			return state;
	}

	SymbolTable M(Print p, SymbolTable state) {
		M(p.source, state);
		return state;
	}

	SymbolTable M(Input i, SymbolTable state) {
		return state.onion(i.id, M(i.source, state));
	}

	Value applyBinary(Operator op, Value v1, Value v2) {
		if (v1 == null || v2 == null) {
			System.err.println("reference to undeclared value");
			System.exit(1);
		}
		if (v1.type() != v2.type()) {
			if (v1.type() == Type.INT && v2.type() == Type.FLOAT) {
				return new FloatValue(v1.intValue() + v2.floatValue());
			} else if (v1.type() == Type.FLOAT && v2.type() == Type.INT) {
				return new FloatValue(v1.floatValue() + v2.intValue());
			} else if (v1.type() == Type.CHAR && v2.type() == Type.INT) {
				return new IntValue(v1.charValue() + v2.intValue());
			} else if (v1.type() == Type.INT && v2.type() == Type.CHAR) {
				return new IntValue(v1.intValue() + v2.charValue());
			} else if (v1.type() == Type.INT && v2.type() == Type.STR) {
				return new StrValue(v1.intValue() + v2.strValue());
			} else if (v1.type() == Type.STR && v2.type() == Type.INT) {
				return new StrValue(v1.strValue() + v2.intValue());
			} else if (v1.type() == Type.FLOAT && v2.type() == Type.STR) {
				return new StrValue(v1.floatValue() + v2.strValue());
			} else if (v1.type() == Type.STR && v2.type() == Type.FLOAT) {
				return new StrValue(v1.strValue() + v2.floatValue());
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
		// add float , bool, and Char(maybe)
		throw new IllegalArgumentException("should never reach here");
	}

	Value applyUnary(Operator op, Value v) {
		if (v == null) {
			System.err.println("reference to undefined variable");
			System.exit(1);
		}
		if (op.val.equals(Operator.NOT))
			return new BoolValue(!v.boolValue());
		else if (op.val.equals(Operator.NEG)) {
			if (v.type() == Type.INT)
				return new IntValue(-v.intValue());
			if (v.type() == Type.FLOAT)
				return new FloatValue(-v.floatValue());
		} else if (op.val.equals(Operator.INT)) {
			if (v.type() == Type.FLOAT)
				return new IntValue(v.intValue());
			else if (v.type() == Type.STR)
				return new IntValue(v.intValue());
		} else if (op.val.equals(Operator.FLOAT)) {
			if (v.type() == Type.INT)
				return new FloatValue(v.floatValue());
			else if (v.type() == Type.STR)
				return new FloatValue(v.floatValue());
		} else if (op.val.equals(Operator.STR)) {
			if (v.type() == Type.INT)
				return new StrValue(v.strValue());
			else if (v.type() == Type.FLOAT)
				return new StrValue(v.strValue());
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

	Value M(Expression e, SymbolTable state) {
		System.out.println(e);
		if (e instanceof Value) {
//            if (e instanceof List) {
//            	List l = (List)e;
//            }
			return (Value) e;
		}
		if (e instanceof Variable) {
			if (!state.containsKey(e)) {
				System.err.println("reference to undeclared variable");
				System.exit(1);
			}

			Expression item = state.get(e);
			if (e instanceof ListItem) {
				ListItem li = (ListItem) e;
				for (Expression i : li.index) {
					int index = M(i, state).intValue();

					try {
						List l = (List) item;
						item = l.members.get(index);
					} catch (ClassCastException error) {
						System.err.println("object is not subscriptable");
						System.exit(1);
					}
				}
			}

			return (Value) item;
		}

		if (e instanceof Binary) {
			Binary b = (Binary) e;
			return applyBinary(b.op, M(b.term1, state), M(b.term2, state));
		}
		if (e instanceof Unary) {
			Unary u = (Unary) e;
			return applyUnary(u.op, M(u.term, state));
		}
		throw new IllegalArgumentException("should never reach here");
	}

	List M(List l, SymbolTable state) {

		return l;
	}

	public static void main(String args[]) {
		Parser parser = new Parser(new Lexer(args[0]));
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
		TypeChecker typechecker = new TypeChecker();
		SymbolTable table = typechecker.M(prog);
		System.out.println("Final State");
		state.display();
	}
}
