package compiler;

import java.util.ArrayList;

public class Parser {
	Token token; // current token from the input stream
	Lexer lexer;

	private Statement s_pre = null;

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

	private void error() {
		System.err.println("Syntax error: expected an indented block");
		System.exit(1);
	}

	public Program program() {
		// Program --> Statements

		return new Program(statements(-1));
	}

	private Statement statement() {
		// Statement --> Skip | Block | Assignment | IfStatement |
		// WhileStatement | PrintStatement | InputStatement

		Statement s = new Skip();
		int space = lexer.getSpaceNum();
		if (token.type() == TokenType.Identifier)
			s = assignment();
//		else if(token.type() == TokenType.Colon)
//			s = statements(space);
		else if (token.type() == TokenType.If)
			s = ifStatement();
		else if (token.type() == TokenType.Else)
			error();
		else if (token.type() == TokenType.While)
			s = whileStatement();
		else if (token.type() == TokenType.Print)
			s = printStatement();
		else if (token.type() == TokenType.Input)
			s = inputStatement();

		if (token.type() == TokenType.Enter)
			token = lexer.next();

		s.space = space;
		return s;
	}

	private Assignment assignment() {
		// Assignment --> Identifier = Expression
		Variable target = null;
		if (token.type() == TokenType.Identifier) {
			target = new Variable(token.value());
			token = lexer.next();

			ArrayList<Expression> index = new ArrayList<Expression>();
			while (token.type() == TokenType.LeftBracket) {
				match(TokenType.LeftBracket);
				index.add(expression());
				match(TokenType.RightBracket);
			}
			if (!index.isEmpty())
				target = new ListItem(target.id, index);
		}
		match(TokenType.Assign);
		Expression source = expression();
		return new Assignment(target, source); // student exercise
	}

	private Block statements(int space) {
		// Block --> Statement { Statement }
		Block b = new Block();

		while (token.type() == TokenType.Enter)
			token = lexer.next();
		b.space = lexer.getSpaceNum();
		if (b.space <= space)
			error();

		Statement s_cur = statement();
		b.members.add(s_cur);

		while (token.type() != TokenType.Eof) {
			if (token.type() == TokenType.Enter) {
				token = lexer.next();
				continue;
			}
			if (lexer.getSpaceNum() != b.members.get(0).space) {
				break;
			}
			s_cur = statement();

			if (s_cur.space == b.members.get(0).space) {
				b.members.add(s_cur);
			} else {
				s_pre = s_cur;
				break;
			}

			if (s_pre != null) {
				if (s_pre.space == b.members.get(0).space) {
					b.members.add(s_pre);
					s_pre = null;
				}
			}
		}
		return b;
	}

	private Conditional ifStatement() {
		// IfStatement --> if Expression : Statements [ else Statements ]
		int if_space = lexer.getSpaceNum();
		match(TokenType.If);
		Expression test = expression();
		match(TokenType.Colon);
		Statement thenbranch = statements(if_space), elsebranch = null;
		Conditional cond = null;
		if (token.type() == TokenType.Else && lexer.getSpaceNum() == if_space) {
			token = lexer.next();
			match(TokenType.Colon);
			elsebranch = statements(if_space);
			cond = new Conditional(test, thenbranch, elsebranch);
		} else
			cond = new Conditional(test, thenbranch);
		cond.space = if_space;
		return cond;
	}

	private Loop whileStatement() {
		// WhileStatement --> while Expression : Statements
		int space = lexer.getSpaceNum();
		match(TokenType.While);
		Expression test = expression();
		match(TokenType.Colon);
		return new Loop(test, statements(space)); // student exercise
	}

	private Print printStatement() {
		// PrintStatement --> print '(' Expression ')'
		match(TokenType.Print);
		match(TokenType.LeftParen);
		Expression source = expression();
		match(TokenType.RightParen);
		return new Print(source);
	}

	private Input inputStatement() {
		// InputStatement --> input '(' Expression ')'
		match(TokenType.Input);
		match(TokenType.LeftParen);
		Variable id = new Variable(match(TokenType.Identifier));
		match(TokenType.Comma);
		Expression source = expression();
		match(TokenType.RightParen);
		return new Input(id, source);
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
		// Primary --> Identifier{'['Expression']'} | Literal | ( Expression ) | Input
		Expression e = null;
		if (token.type() == TokenType.Identifier) {
			Variable target = new Variable(token.value());
			token = lexer.next();
			ArrayList<Expression> index = new ArrayList<Expression>();
			while (token.type() == TokenType.LeftBracket) {
				match(TokenType.LeftBracket);
				index.add(expression());
				match(TokenType.RightBracket);
			}
			if (!index.isEmpty())
				target = new ListItem(target.id, index);
			e = target;
		} else if (isLiteral()) {
			e = literal();
		} else if (token.type() == TokenType.LeftParen) {
			token = lexer.next();
//			Boolean hasParen = true;
			e = expression();
			e.hasParen = true;
			match(TokenType.RightParen);
		} else if (token.type() == TokenType.LeftBracket) {
			token = lexer.next();
			List l = new List();
			while (token.type() != TokenType.RightBracket) {
				l.members.add(expression());
				if (token.type() == TokenType.Comma)
					token = lexer.next();
			}
			token = lexer.next();
			return l;
		} else if (token.type() == TokenType.Int || token.type() == TokenType.Float || token.type() == TokenType.Str
				|| token.type() == TokenType.Char) {
			Operator op = new Operator(match(token.type()));
			match(TokenType.LeftParen);
			Expression term = expression();
			match(TokenType.RightParen);
			e = new Unary(op, term);
		} else
			error("Identifier | Literal | ( | Type");
		return e;
	}

//	private Expression inputExpression() {
//		// InputStatement --> input '(' Expression ')'
//		match(TokenType.Input);
//		match(TokenType.LeftParen);
//		Expression source = expression();
//		match(TokenType.RightParen);
//		return new InputExpression(source);
//	}

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
		return token.type() == TokenType.Equals || token.type() == TokenType.NotEqual;
	}

	private boolean isRelationalOp() {
		return token.type() == TokenType.Less || token.type() == TokenType.LessEqual
				|| token.type() == TokenType.Greater || token.type() == TokenType.GreaterEqual;
	}

	private boolean isLiteral() {
		return token.type() == TokenType.IntLiteral || isBooleanLiteral() || token.type() == TokenType.FloatLiteral
				|| token.type() == TokenType.CharLiteral || token.type() == TokenType.StrLiteral;
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
