package compiler;

import java.util.*;

public class SymbolTable extends HashMap<Variable, Value> {
	// Defines the set of variables and their associated values
	// that are active during interpretation

	public SymbolTable() {
	}

	public SymbolTable(Variable key, Value val) {
		put(key, val);
	}

	public SymbolTable onion(Variable key, Value val) {
		put(key, val);
		System.out.println("Onion! " + key + " " + val);
		return this;
	}

	public SymbolTable onion(SymbolTable t) {
		for (Variable key : t.keySet())
			put(key, t.get(key));
		return this;
	}

	public void display() {
		// TODO Auto-generated method stub
		System.out.println(this.toString());
	}
}
