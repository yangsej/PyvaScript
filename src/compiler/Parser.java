package compiler;

public class Parser {
	Token token; // current token from the input stream
	Lexer lexer;
	
	static int tabs = 0;
	Statement state_pre = null;

	public Parser(Lexer ts) { // Open the C++Lite source program
		lexer = ts; // as a token stream, and
		token = lexer.next(); // retrieve its first Token
	}

	private String match(TokenType t) { // * return the string of a token if it matches with t *
		String value = token.value();
		if (token.type() == t)
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
		// Block --> { Statement }
		Block b = new Block();
		// 탭 수를 확인해 계산
		while (token.type() != TokenType.Eof) {
//			System.out.println(lexer.getSpaceNum() + " " + token);
//			if(token.type() == TokenType.Space
//			|| token.type() == TokenType.Tab
//			|| token.type() == TokenType.Enter) {
//				token = lexer.next();
//				continue;
//			}
			Statement s = statement();
			if(s.getClass() == Statement_Skip.class) {
				continue;
			}
			if(b.members.size() > 0 && s.space < b.members.get(b.members.size()-1).space) {
//				System.out.println("End of Block : " + s);
				state_pre = s;
				break;
			}
				
			b.members.add(s);
			

			if(state_pre != null && state_pre.space == b.members.get(b.members.size()-1).space) {
				b.members.add(state_pre);
				state_pre = null;
			}
		}
		return b;
	}

	private Statement statement() {
		// Statement --> Skip | Assignment | IfStatement | 
		// WhileStatement | PrintStatement
//		System.out.println(lexer.getSpaceNum() + " " + token);
		Statement s = new Statement_Skip();
		int space_temp = lexer.getSpaceNum();
		if(token.type() == TokenType.Identifier)
			s = assignment();
		else if(token.type() == TokenType.If)
			s = ifStatement();
		else if(token.type() == TokenType.While)
			s = whileStatement();
		else if(token.type() == TokenType.Print)
			s = printStatement();
		if(token.type() == TokenType.Enter) token = lexer.next();
		s.space = space_temp;
		return s;
	}

	private Assignment assignment() {
		// Assignment --> Identifier = Expression
		Variable target = null;
		if (token.type() == TokenType.Identifier) {
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
		Expression test = expression();
		match(TokenType.Colon);
		match(TokenType.Enter);
		Block thenbranch = statements(), elsebranch = null;
		Conditional cond = null;
		if (token.type() == TokenType.Else) {
			token = lexer.next();
			elsebranch = statements();
			cond = new Conditional(test, thenbranch, elsebranch);
		} else
			cond = new Conditional(test, thenbranch);
		return cond;
	}

	private Loop whileStatement() {
		// WhileStatement --> while Expression : Statements
		match(TokenType.While);
		Expression test = expression();
		match(TokenType.Colon);
		match(TokenType.Enter);
		return new Loop(test, statements()); // student exercise
	}

	private Print printStatement() {
		// PrintStatement --> print '(' Expression ')'
		match(TokenType.Print);
		match(TokenType.LeftParen);
		Expression source = expression();
		match(TokenType.RightParen);
		return new Print(source);
	}

	private Expression expression() {
		// Expression --> Conjunction { || Conjunction }
		Expression e = conjunction();
		while (token.type() == TokenType.Or) {
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
		while (token.type() == TokenType.And) {
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
		if (token.type() == TokenType.Identifier) {
			e = new Variable(match(TokenType.Identifier));
		} else if (isLiteral()) {
			e = literal();
		} else if (token.type() == TokenType.LeftParen) {
			token = lexer.next();
			e = expression();
			match(TokenType.RightParen);
		} else
			error("Identifier | Literal | ( | Type");
		return e;
	}

	private Value literal() {
		Value v = null;
		if (token.type() == TokenType.IntLiteral)
			v = new IntValue(Integer.parseInt(token.value()));
		else if (isBooleanLiteral())
			v = new BoolValue(Boolean.parseBoolean(token.value()));
		else if (token.type() == TokenType.FloatLiteral)
			v = new FloatValue(Float.parseFloat(token.value()));
		else if (token.type() == TokenType.CharLiteral)
			v = new CharValue(token.value().charAt(token.value().length() - 1));
		else if (token.type() == TokenType.StrLiteral)
			v = new StrValue(token.value());
		token = lexer.next();
		return v; // student exercise
	}

	private boolean isAddOp() {
		return token.type() == TokenType.Plus || token.type() == TokenType.Minus;
	}

	private boolean isMultiplyOp() {
		return token.type() == TokenType.Multiply || token.type() == TokenType.Divide;
	}

	private boolean isUnaryOp() {
		return token.type() == TokenType.Not || token.type() == TokenType.Minus;
	}

	private boolean isEqualityOp() {
		return token.type() == TokenType.Equals  || token.type() == TokenType.NotEqual;
	}

	private boolean isRelationalOp() {
		return token.type() == TokenType.Less || token.type() == TokenType.LessEqual
				|| token.type() == TokenType.Greater || token.type() == TokenType.GreaterEqual;
	}

	private boolean isLiteral() {
		return token.type() == TokenType.IntLiteral || isBooleanLiteral()
				|| token.type() == TokenType.FloatLiteral || token.type() == TokenType.CharLiteral
				|| token.type() == TokenType.StrLiteral;
	}

	private boolean isBooleanLiteral() {
		return token.type() == TokenType.True || token.type() == TokenType.False;
	}

	public static void main(String args[]) {
		Parser parser = new Parser(new Lexer(args[0]));
		Program prog = parser.program();
		prog.display(); // display abstract syntax tree
	} // main

} // Parser
