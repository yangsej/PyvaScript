package compiler;

public enum TokenType {
    Bool, Char, Else, False, Float,
     If, Int, True, While,
    Eof, LeftBracket, RightBracket,
    LeftParen, RightParen, Semicolon, Comma, Assign,
    Equals, Less, LessEqual, Greater, GreaterEqual,
    Not, NotEqual, Plus, Minus, Multiply,
    Divide, And, Or, Identifier, IntLiteral,
    FloatLiteral, CharLiteral,
    
    
    StrLiteral, Str, Colon, Tab, Space, Enter
}