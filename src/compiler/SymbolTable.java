package compiler;

import java.util.*;

public class SymbolTable extends HashMap<Variable, Value> {

	public SymbolTable() {
	}

	public SymbolTable(Variable key, Value val) {
		put(key, val);
	}

	public SymbolTable onion(Variable key, Value val) {
		put(key, val);
		return this;
	}

	public SymbolTable onion(SymbolTable t) {
		for (Variable key : t.keySet())
			put(key, t.get(key));
		return this;
	}

	public void display() {
		System.out.println(this.toString());
	}
}
