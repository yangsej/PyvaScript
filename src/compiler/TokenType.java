package compiler;

public enum TokenType {
//    Bool, Char, Float, Int, Str,
	True, False,
    If, Else, While,
    Print, Input,
     
    Eof, LeftBracket, RightBracket,
    LeftParen, RightParen, Semicolon, Comma, Assign,
    Equals, Less, LessEqual, Greater, GreaterEqual,
    Not, NotEqual, Plus, Minus, Multiply,
    Divide, And, Or, Identifier, IntLiteral,
    FloatLiteral, CharLiteral,
    
    
    StrLiteral, Colon, Tab, Space, Enter
}