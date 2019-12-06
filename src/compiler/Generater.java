package compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;

public class Generater {
	File jsFile = null, htmlFile = null;
	FileWriter writer = null;
	String result = "";
	
	private static int tabs = 0;
	
	Generater(String Fname) throws IOException {
		jsFile = new File(Fname + ".js");
		htmlFile = new File(Fname + ".html");
	}
	
	void G(Program p) throws IOException {
		for(Statement s : p.body.members) {
			G(s);
		}
		writer = new FileWriter(jsFile);
		writer.write(result);
		writer.flush();
		writer.close();
		
		writer = new FileWriter(htmlFile);
		writer.write("<html>\r\n" + 
				"    <body>\r\n" + 
				"        <script type=\"text/javascript\" src=\"" + jsFile.getName() + "\"></script>\r\n" + 
				"    </body>\r\n" + 
				"</html>");
		writer.flush();
		writer.close();
	}
	
	void G(Statement s) throws IOException {
		System.out.println(s);
        if (s instanceof Skip) G((Skip)s);
        else if (s instanceof Assignment)  G((Assignment)s); 
        else if (s instanceof Conditional)  G((Conditional)s); 
        else if (s instanceof Loop) G((Loop)s); 
        else if (s instanceof Block) G((Block)s);
        else if (s instanceof Print) G((Print)s); 
        else if (s instanceof Input) G((Input)s); 
        else throw new IllegalArgumentException("should never reach here"); 
	}
	
	void G(Skip s) {
		return;
	}
	
	void G(Assignment a) throws IOException {
		String temp = "";
		temp+=a.target;
		//System.out.println(temp+" check!");
		if(!temp.contains("[")) { 
			
			result += "var ";
		}
		G(a.target);
		result += " = ";
		G(a.source);
		result += ";\n";
	}
	
	void G(Conditional c) throws IOException {
		result += "if (";
		G(c.test);
		result += ")\n";
		G(c.thenbranch);
		if(!(c.elsebranch instanceof Skip)) {
			result += String.join("", Collections.nCopies(tabs, "\t")) + "else\n";
			G(c.elsebranch);
			result += "\n";
		}
	}
	
	void G(Loop l) throws IOException {
		result += "while (";
		G(l.test);
		result += ")\n";
		G(l.body);
		result += "\n";
	}

	void G(Block b) throws IOException {
		result += String.join("", Collections.nCopies(tabs, "\t")) + "{\n";
		tabs++;
		for(Statement s : b.members) {
			result += String.join("", Collections.nCopies(tabs, "\t"));
			G(s);
		}
		tabs--;
		result += String.join("", Collections.nCopies(tabs, "\t")) + "}\n";
	}
	
	void G(Print p) throws IOException {
		result += "alert(";
		G(p.source);
		result += ");\n";
	}
	
	void G(Input i) throws IOException {
		result += "var " + i.id + " = ";
		result += "prompt(";
		G(i.source);
		result += ");\n";
	}

	void G(Expression e) {
		if(e.hasParen) result += "(";
		if(e instanceof Variable) G((Variable)e);
		else if(e instanceof Value) G((Value)e);
		else if(e instanceof Binary) G((Binary)e);
		else if(e instanceof Unary) G((Unary)e);
        else throw new IllegalArgumentException("should never reach here");
		if(e.hasParen) result += ")";
	}
	
	void G(Variable v) {
		result += v;
		//System.out.println(v);
	}
	
	void G(Value v) {
		if(v instanceof IntValue) G((IntValue)v);
		else if(v instanceof BoolValue) G((BoolValue)v);
		else if(v instanceof CharValue) G((CharValue)v);
		else if(v instanceof FloatValue) G((FloatValue)v);
		else if(v instanceof StrValue) G((StrValue)v);
		else if(v instanceof List) G((List)v);
        else throw new IllegalArgumentException("should never reach here"); 
	}
	
	void G(Binary b) {
		G(b.term1);
		result += " " + b.op + " ";
		G(b.term2);
	}
	
	void G(Unary u) {
		result += u.op;
		G(u.term);
	}
	
	void G(IntValue v) {
		result += v;
	}
	
	void G(BoolValue v) {
		result += v;
	}
	
	void G(CharValue v) {
		result += "\'" + v + "\'";
	}
	
	void G(FloatValue v) {
		result += v;
	}
	
	void G(StrValue v) {
		result += "\"" + v + "\"";
	}
	void G(List l) {
		result += "[";
		for(Expression e : l.members) {
			G(e);
			if(e == l.members.get(l.members.size()-1)) break;
			result += ", ";
		}
		result += "]";
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        Parser parser  = new Parser(new Lexer(args[0])); 
        Program prog = parser.program(); 
        
        Semantics semantics = new Semantics();
        State state = semantics.M(prog);
        
        Generater generater;
		try {
			generater = new Generater(args[0].substring(0, args[0].lastIndexOf(".")));
	        generater.G(prog);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
