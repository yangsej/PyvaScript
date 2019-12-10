package compiler;

import java.util.*;


class Program {
	// Program = Block body
	Block body;

	static int tabs = 0;

	Program(Block b) {
		body = b;
	}

	public void display() {
		// TODO Auto-generated method stub
		System.out.println("Program");
		tabs++;
		System.out.println(body);
	}

	public static String tab() {
		return String.join("", Collections.nCopies(Program.tabs, "|\t"));
	}
}

class Type {
	// Type = int | bool | char | float | str
	final static Type INT = new Type("int");
	final static Type BOOL = new Type("bool");
	final static Type CHAR = new Type("char");
	final static Type FLOAT = new Type("float");
	final static Type STR = new Type("str");
	final static Type LIST = new Type("list");
	// final static Type UNDEFINED = new Type("undef");

	private String id;

	private Type(String t) {
		id = t;
	}

	public String toString() {
		return id;
	}
}

//class Block {
//	// Block = Statement*
//	// (a Vector of members)
//	public ArrayList<Statement> members = new ArrayList<Statement>();
//
//	public String toString() {
//		String str = Program.tab() + ":";
//		Program.tabs++;
//		for (Statement sta : members) {
//			str += "\n" + sta;
//		}
//		Program.tabs--;
//		return str;
//	}
//}

//class Skip extends Block {
//	public String toString() {
//		return Program.tab() + "Block_Skip";
//	}
//}

abstract class Statement {
	public int space = -1;
	// Statement = Skip | Assignment | Conditional | Loop | Print
}


//class Statement_Skip extends Statement {
//	public String toString() {
//		return Program.tab() + "Statement_Skip";
//	}
//}

class Skip extends Statement {
	public String toString() {
		return Program.tab() + "Skip";
	}
}

class Block extends Statement{
	// Block = Statement*
	// (a Vector of members)
	public ArrayList<Statement> members = new ArrayList<Statement>();
	
	
	public String toString() {
		String str = Program.tab() + ":";
		Program.tabs++;
		for (Statement sta : members) {
			str += "\n" + sta;
		}
		Program.tabs--;
		return str;
	}
}



class Assignment extends Statement {
	// Assignment = Variable target; Expression source
//	List DeclCheck = new ArrayList<String>();
	
	Variable target;
	Expression source;

	Assignment(Variable t, Expression e) {
		target = t;
		source = e;
	}

	public String toString() {
		String str = Program.tab() + "=";
		Program.tabs++;
		str += "\n" + Program.tab() + target + "\n" + Program.tab() + source;
		Program.tabs--;
		return str;
	}
}

class Conditional extends Statement {
//Conditional = Expression test; Block thenbranch, elsebranch
	Expression test;
	Statement thenbranch, elsebranch;
	// elsebranch == null means "if... then"

	Conditional(Expression t, Statement tp) {
		test = t;
		thenbranch = tp;
		elsebranch = new Skip();
	}

	Conditional(Expression t, Statement tp, Statement ep) {
		test = t;
		thenbranch = tp;
		elsebranch = ep;
	}

	public String toString() {
		String str = Program.tab() + "if";
		Program.tabs++;
		str += "\n" + Program.tab() + test + "\n" + thenbranch + "\n" + elsebranch;
		Program.tabs--;
		return str;
	}
}

class Loop extends Statement {
//Loop = Expression test; Statement body
	Expression test;
	Statement body;

	Loop(Expression t, Statement b) {
		test = t;
		body = b;
	}

	public String toString() {
		String str = Program.tab() + "while";
		Program.tabs++;
		str += "\n" + Program.tab() + test + "\n" + body;
		Program.tabs--;
		return str;
	}
}

class Print extends Statement {
	// Print = Expression source
	Expression source;

	Print(Expression string) {
		source = string;
	}

	public String toString() {
		String str = Program.tab() + "print";
		Program.tabs++;
		str += "\n" + Program.tab() + source;
		Program.tabs--;
		return str;
	}
}

class Input extends Statement {
	// Print = Expression source
	Variable id;
	Expression source;

	Input(Variable i, Expression s) {
		id = i;
		source = s;
	}

	public String toString() {
		String str = Program.tab() + "input";
		Program.tabs++;
		str += "\n" + Program.tab() + id;
		str += "\n" + Program.tab() + source;
		Program.tabs--;
		return str;
	}
}

abstract class Expression {
	// Expression = Variable | Value | Binary | Unary
	public boolean hasParen = false;

}

class Variable extends Expression {
	// Variable = String id
	protected String id;

	Variable(String s) {
		id = s;
	}

	public String toString() {
		return id;
	}

	public boolean equals(Object obj) {
		String s = ((Variable) obj).id;
		return id.equals(s); // case-sensitive identifiers
	}

	public int hashCode() {
		return id.hashCode();
	}

}

class ListItem extends Variable {
	// ListItem = String id | ArrayList<Expression> index
	public ArrayList<Expression> index = new ArrayList<Expression>();
	
	ListItem(String s) {
		super(s);
	}
	
	ListItem(String s, ArrayList<Expression> i) {
		super(s);
		index = i;
	}

	public String toString() {
		String str = id;
		for(Expression e : index) {
			str += "[" + e + "]";
		}
		return str;
	}
}

//class InputVariable extends Variable {
//	// Variable = String id
//	private String id;
//	Expression source;
//
//	InputVariable(Expression string) {
//		id = "input";
//		source = string;
//	}
//
//	public boolean equals(Object obj) {
//		String s = ((Variable) obj).id;
//		return id.equals(s); // case-sensitive identifiers
//	}
//
//	public String toString() {
//		String str = id;
//		Program.tabs++;
//		str += "\n" + Program.tab() + source;
//		Program.tabs--;
//		return str;
//	}
//}

abstract class Value extends Expression {
	// Value = IntValue | BoolValue | CharValue | FloatValue
	protected Type type;
	protected boolean undef = true;

	int intValue() {
		assert false : "should never reach here";
		return 0;
	} // implementation of this function is unnecessary can can be removed.

	boolean boolValue() {
		assert false : "should never reach here";
		return false;
	}

	char charValue() {
		assert false : "should never reach here";
		return ' ';
	}

	float floatValue() {
		assert false : "should never reach here";
		return 0.0f;
	}

	String strValue() {
		assert false : "should never reach here";
		return " ";
	}
	
	ArrayList<Expression> list() {
		assert false : "should never reach here";
		return new ArrayList<Expression>();
	}

	boolean isUndef() {
		return undef;
	}

	Type type() {
		return type;
	}

	static Value mkValue(Type type) {
		if (type == Type.INT)
			return new IntValue();
		if (type == Type.BOOL)
			return new BoolValue();
		if (type == Type.CHAR)
			return new CharValue();
		if (type == Type.FLOAT)
			return new FloatValue();
		if (type == Type.STR)
			return new StrValue();
		if (type == Type.LIST)
			return new List();
		throw new IllegalArgumentException("Illegal type in mkValue");
	}
}

class List extends Value{
	public ArrayList<Expression> members = new ArrayList<Expression>();
	
	List(){
		type = Type.LIST;
	}
	
	List(ArrayList<Expression> a){
		this();
		members = a;
	}
	
	ArrayList<Expression> list() {
		assert !undef : "reference to undefined list";
		return members;
	}
	
	public String toString(){
		return members.toString();
	}
}

class IntValue extends Value {
	private int value = 0;

	IntValue() {
		type = Type.INT;
	}

	IntValue(int v) {
		this();
		value = v;
		undef = false;
	}

	int intValue() {
		assert !undef : "reference to undefined int value";
		return value;
	}

	public String toString() {
		if (undef)
			return "undef";
		return "" + value;
	}

}

class BoolValue extends Value {
	private boolean value = false;

	BoolValue() {
		type = Type.BOOL;
	}

	BoolValue(boolean v) {
		this();
		value = v;
		undef = false;
	}

	boolean boolValue() {
		assert !undef : "reference to undefined bool value";
		return value;
	}

	int intValue() {
		assert !undef : "reference to undefined bool value";
		return value ? 1 : 0;
	}

	public String toString() {
		if (undef)
			return "undef";
		return "" + value;
	}

}

class CharValue extends Value {
	private char value = ' ';

	CharValue() {
		type = Type.CHAR;
	}

	CharValue(char v) {
		this();
		value = v;
		undef = false;
	}

	char charValue() {
		assert !undef : "reference to undefined char value";
		return value;
	}

	public String toString() {
		if (undef)
			return "undef";
		return "" + value;
	}

}

class FloatValue extends Value {
	private float value = 0;

	FloatValue() {
		type = Type.FLOAT;
	}

	FloatValue(float v) {
		this();
		value = v;
		undef = false;
	}

	float floatValue() {
		assert !undef : "reference to undefined float value";
		return value;
	}

	public String toString() {
		if (undef)
			return "undef";
		return "" + value;
	}

}

class StrValue extends Value {
	private String value = " ";

	StrValue() {
		type = Type.STR;
	}

	StrValue(String v) {
		this();
		value = v;
		undef = false;
	}

	String strValue() {
		assert !undef : "reference to undefined char value";
		return value;
	}

	public String toString() {
		if (undef)
			return "undef";
		return "" + value;
	}

}

class Binary extends Expression {
//Binary = Operator op; Expression term1, term2
	Operator op;
	Expression term1, term2;

	Binary(Operator o, Expression l, Expression r) {
		op = o;
		term1 = l;
		term2 = r;
	} // binary

	public String toString() {
		String str = op.toString();
		Program.tabs++;
		str += "\n" + Program.tab() + term1 + "\n" + Program.tab() + term2;
		Program.tabs--;
		return str;
	}
}

class Unary extends Expression {
	// Unary = Operator op; Expression term
	Operator op;
	Expression term;

	Unary(Operator o, Expression e) {
		op = o;
		term = e;
	} // unary

	public String toString() {
		String str = "Unary";
		Program.tabs++;
		str += "\n" + Program.tab() + op + "\n" + Program.tab() + term;
		Program.tabs--;
		return str;
	}
}

class Operator {
	// Operator = BooleanOp | RelationalOp | ArithmeticOp | UnaryOp
	// BooleanOp = && | ||
	final static String AND = "&&";
	final static String OR = "||";
	// RelationalOp = < | <= | == | != | >= | >
	final static String LT = "<";
	final static String LE = "<=";
	final static String EQ = "==";
	final static String NE = "!=";
	final static String GT = ">";
	final static String GE = ">=";
	// ArithmeticOp = + | - | * | /
	final static String PLUS = "+";
	final static String MINUS = "-";
	final static String TIMES = "*";
	final static String DIV = "/";
	// UnaryOp = !
	final static String NOT = "!";
	final static String NEG = "-";
	// CastOp = int | float | char
	final static String INT = "int";
	final static String FLOAT = "float";
	final static String CHAR = "char";
	final static String STR = "str";
	// Typed Operators
	// RelationalOp = < | <= | == | != | >= | >
	final static String INT_LT = "INT<";
	final static String INT_LE = "INT<=";
	final static String INT_EQ = "INT==";
	final static String INT_NE = "INT!=";
	final static String INT_GT = "INT>";
	final static String INT_GE = "INT>=";
	// ArithmeticOp = + | - | * | /
	final static String INT_PLUS = "INT+";
	final static String INT_MINUS = "INT-";
	final static String INT_TIMES = "INT*";
	final static String INT_DIV = "INT/";
	// UnaryOp = !
	final static String INT_NEG = "-";
	// RelationalOp = < | <= | == | != | >= | >
	final static String FLOAT_LT = "FLOAT<";
	final static String FLOAT_LE = "FLOAT<=";
	final static String FLOAT_EQ = "FLOAT==";
	final static String FLOAT_NE = "FLOAT!=";
	final static String FLOAT_GT = "FLOAT>";
	final static String FLOAT_GE = "FLOAT>=";
	// ArithmeticOp = + | - | * | /
	final static String FLOAT_PLUS = "FLOAT+";
	final static String FLOAT_MINUS = "FLOAT-";
	final static String FLOAT_TIMES = "FLOAT*";
	final static String FLOAT_DIV = "FLOAT/";
	// UnaryOp = !
	final static String FLOAT_NEG = "-";
	// RelationalOp = < | <= | == | != | >= | >
	final static String CHAR_LT = "CHAR<";
	final static String CHAR_LE = "CHAR<=";
	final static String CHAR_EQ = "CHAR==";
	final static String CHAR_NE = "CHAR!=";
	final static String CHAR_GT = "CHAR>";
	final static String CHAR_GE = "CHAR>=";
	// RelationalOp = < | <= | == | != | >= | >
	final static String BOOL_LT = "BOOL<";
	final static String BOOL_LE = "BOOL<=";
	final static String BOOL_EQ = "BOOL==";
	final static String BOOL_NE = "BOOL!=";
	final static String BOOL_GT = "BOOL>";
	final static String BOOL_GE = "BOOL>=";
	// Type specific cast
	final static String I2F = "I2F";
	final static String F2I = "F2I";
	final static String C2I = "C2I";
	final static String I2C = "I2C";

	String val;

	Operator(String s) {
		val = s;
	}

	public String toString() {
		return val;
	}

	public boolean equals(Object obj) {
		return val == obj;
	}

	boolean BooleanOp() {
		return val == AND || val == OR;
	}

	boolean RelationalOp() {
		return val == LT || val == LE || val == EQ || val == NE || val == GT || val == GE;
	}

	boolean ArithmeticOp() {
		return val == PLUS || val == MINUS || val == TIMES || val == DIV;
	}

	boolean NotOp() {
		return val == NOT;
	}

	boolean NegateOp() {
		return val == NEG;
	}

	boolean intOp() {
		return val == INT;
	}

	boolean floatOp() {
		return val == FLOAT;
	}

	boolean charOp() {
		return val == CHAR;
	}

	final static String intMap[][] = { { PLUS, INT_PLUS }, { MINUS, INT_MINUS }, { TIMES, INT_TIMES }, { DIV, INT_DIV },
			{ EQ, INT_EQ }, { NE, INT_NE }, { LT, INT_LT }, { LE, INT_LE }, { GT, INT_GT }, { GE, INT_GE },
			{ NEG, INT_NEG }, { FLOAT, I2F }, { CHAR, I2C } };

	final static String floatMap[][] = { { PLUS, FLOAT_PLUS }, { MINUS, FLOAT_MINUS }, { TIMES, FLOAT_TIMES },
			{ DIV, FLOAT_DIV }, { EQ, FLOAT_EQ }, { NE, FLOAT_NE }, { LT, FLOAT_LT }, { LE, FLOAT_LE },
			{ GT, FLOAT_GT }, { GE, FLOAT_GE }, { NEG, FLOAT_NEG }, { INT, F2I } };

	final static String charMap[][] = { { EQ, CHAR_EQ }, { NE, CHAR_NE }, { LT, CHAR_LT }, { LE, CHAR_LE },
			{ GT, CHAR_GT }, { GE, CHAR_GE }, { INT, C2I } };

	final static String boolMap[][] = { { EQ, BOOL_EQ }, { NE, BOOL_NE }, { LT, BOOL_LT }, { LE, BOOL_LE },
			{ GT, BOOL_GT }, { GE, BOOL_GE }, };
	
//	final static String strMap[][] = { { EQ, CHAR_EQ }, { NE, CHAR_NE }, { LT, CHAR_LT }, { LE, CHAR_LE },
//			{ GT, CHAR_GT }, { GE, CHAR_GE }, { INT, C2I } };

	final static private Operator map(String[][] tmap, String op) {
		for (int i = 0; i < tmap.length; i++)
			if (tmap[i][0] == op)
				return new Operator(tmap[i][1]);
		assert false : "should never reach here";
		return null;
	}

	final static public Operator intMap(String op) {
		return map(intMap, op);
	}

	final static public Operator floatMap(String op) {
		return map(floatMap, op);
	}

	final static public Operator charMap(String op) {
		return map(charMap, op);
	}

	final static public Operator boolMap(String op) {
		return map(boolMap, op);
	}

}
