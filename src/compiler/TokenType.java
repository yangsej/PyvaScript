package compiler;

public enum TokenType {
	// Keywords
    Else, False,
	If, True, While,
    Print, Input,
    
    // Others
    Eof, LeftBracket, RightBracket,
    LeftParen, RightParen, Semicolon, Comma, Assign,
    Equals, Less, LessEqual, Greater, GreaterEqual,
    Not, NotEqual, Plus, Minus, Multiply,
    Divide, And, Or, Identifier, IntLiteral,
    FloatLiteral, CharLiteral, StrLiteral,
    Colon, Tab, Space, Enter,
}