package compiler;

public class Parser {
	Token token; // current token from the input stream
	Lexer lexer;
	static int tabs = 0;

	public Parser(Lexer ts) { // Open the C++Lite source program
		lexer = ts; // as a token stream, and
		token = lexer.next(); // retrieve its first Token
	}

	private String match(TokenType t) { // * return the string of a token if it matches with t *
		String value = token.value();
		if (token.type().equals(t))
			token = lexer.next();
		else
			error(t);
		return value;
	}

	private void error(TokenType tok) {
		System.err.println("Syntax error: expecting: " + tok + "; saw: " + token);
		System.exit(1);
	}

	private void error(String tok) {
		System.err.println("Syntax error: expecting: " + tok + "; saw: " + token);
		System.exit(1);
	}

	public Program program() {
		// Program --> Statements
		return new Program(statements());
	}
	
	private Block statements() {
		// Block --> { Statements }
		Block b = new Block();
		// 탭 수를 확인해 계산
		while (!token.type().equals(TokenType.Eof))
			b.members.add(statement());
		return b;
	}

	private Statement statement() {
		// Statement --> Assignment | IfStatement | 
		// WhileStatement | PrintStatement
		
		Statement s = null;
		switch (token.type()) {
		case Identifier:
			s = assignment();
			break;
		case If:
			s = ifStatement();
			break;
		case While:
			s = whileStatement();
			break;
		case Print:
			s = printStatement();
		default:
			break;
		}

		return s;
	}

	private Assignment assignment() {
		// Assignment --> Identifier = Expression
		Variable target = null;
		if (token.type().equals(TokenType.Identifier)) {
			target = new Variable(token.value());
			token = lexer.next();
		}
		match(TokenType.Assign);
		Expression source = expression();
		return new Assignment(target, source); // student exercise
	}

	private Conditional ifStatement() {
		// IfStatement --> if Expression : Statements [ else Statements ]
		match(TokenType.If);
		match(TokenType.LeftParen);
		Expression test = expression();
		match(TokenType.RightParen);
		Block thenbranch = statements(), elsebranch = null;
		Conditional cond = null;
		if (token.type().equals(TokenType.Else)) {
			token = lexer.next();
			elsebranch = statements();
			cond = new Conditional(test, thenbranch, elsebranch);
		} else
			cond = new Conditional(test, thenbranch);
		return cond; // student exercise
	}

	private Loop whileStatement() {
		// WhileStatement --> while ( Expression ) Statement
		match(TokenType.While);
		match(TokenType.LeftParen);
		Expression test = expression();
		match(TokenType.RightParen);
		return new Loop(test, statement()); // student exercise
	}

	private Print printStatement() {
		// TODO Auto-generated method stub
		return null;
	}

	private Expression expression() {
		// Expression --> Conjunction { || Conjunction }
		Expression e = conjunction();
		while (token.type().equals(TokenType.Or)) {
			Operator op = new Operator(match(token.type()));
			Expression conjunction2 = conjunction();
			e = new Binary(op, e, conjunction2);
		}
//    	token = lexer.next();
		return e; // student exercise
	}

	private Expression conjunction() {
		// Conjunction --> Equality { && Equality }
		Expression e = equality();
		while (token.type().equals(TokenType.And)) {
			Operator op = new Operator(match(token.type()));
			Expression equality2 = equality();
			e = new Binary(op, e, equality2);
		}
		return e; // student exercise
	}

	private Expression equality() {
		// Equality --> Relation [ EquOp Relation ]
		Expression e = relation();
		if (isEqualityOp()) {
			Operator op = new Operator(match(token.type()));
			Expression relation2 = relation();
			e = new Binary(op, e, relation2);
		}
		return e; // student exercise
	}

	private Expression relation() {
		// Relation --> Addition [RelOp Addition]
		Expression e = addition();
		if (isRelationalOp()) {
			Operator op = new Operator(match(token.type()));
			Expression addition2 = addition();
			e = new Binary(op, e, addition2);
		}
		return e; // student exercise
	}

	private Expression addition() {
		// Addition --> Term { AddOp Term }
		Expression e = term();
		while (isAddOp()) {
			Operator op = new Operator(match(token.type()));
			Expression term2 = term();
			e = new Binary(op, e, term2);
		}
		return e;
	}

	private Expression term() {
		// Term --> Factor { MultiplyOp Factor }
		Expression e = factor();
		while (isMultiplyOp()) {
			Operator op = new Operator(match(token.type()));
			Expression term2 = factor();
			e = new Binary(op, e, term2);
		}
		return e;
	}

	private Expression factor() {
		// Factor --> [ UnaryOp ] Primary
		if (isUnaryOp()) {
			Operator op = new Operator(match(token.type()));
			Expression term = primary();
			return new Unary(op, term);
		} else
			return primary();
	}

	private Expression primary() {
		// Primary --> Identifier | Literal | ( Expression )
		Expression e = null;
		if (token.type().equals(TokenType.Identifier)) {
			e = new Variable(match(TokenType.Identifier));
		} else if (isLiteral()) {
			e = literal();
		} else if (token.type().equals(TokenType.LeftParen)) {
			token = lexer.next();
			e = expression();
			match(TokenType.RightParen);
		} else
			error("Identifier | Literal | ( | Type");
		return e;
	}

	private Value literal() {
		Value v = null;
		if (token.type().equals(TokenType.IntLiteral))
			v = new IntValue(Integer.parseInt(token.value()));
		else if (isBooleanLiteral())
			v = new BoolValue(Boolean.parseBoolean(token.value()));
		else if (token.type().equals(TokenType.FloatLiteral))
			v = new FloatValue(Float.parseFloat(token.value()));
		else if (token.type().equals(TokenType.CharLiteral))
			v = new CharValue(token.value().charAt(token.value().length() - 1));
		token = lexer.next();
		return v; // student exercise
	}

	private boolean isAddOp() {
		return token.type().equals(TokenType.Plus) || token.type().equals(TokenType.Minus);
	}

	private boolean isMultiplyOp() {
		return token.type().equals(TokenType.Multiply) || token.type().equals(TokenType.Divide);
	}

	private boolean isUnaryOp() {
		return token.type().equals(TokenType.Not) || token.type().equals(TokenType.Minus);
	}

	private boolean isEqualityOp() {
		return token.type().equals(TokenType.Equals) || token.type().equals(TokenType.NotEqual);
	}

	private boolean isRelationalOp() {
		return token.type().equals(TokenType.Less) || token.type().equals(TokenType.LessEqual)
				|| token.type().equals(TokenType.Greater) || token.type().equals(TokenType.GreaterEqual);
	}

	private boolean isLiteral() {
		return token.type().equals(TokenType.IntLiteral) || isBooleanLiteral()
				|| token.type().equals(TokenType.FloatLiteral) || token.type().equals(TokenType.CharLiteral);
	}

	private boolean isBooleanLiteral() {
		return token.type().equals(TokenType.True) || token.type().equals(TokenType.False);
	}

	public static void main(String args[]) {
		Parser parser = new Parser(new Lexer(args[0]));
		Program prog = parser.program();
		prog.display(); // display abstract syntax tree
	} // main

} // Parser
