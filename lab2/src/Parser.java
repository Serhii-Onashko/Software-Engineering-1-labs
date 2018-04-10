public class Parser {
	int index=0;
	Tables table;
	ProcedureDeclaration buffer;
	
	private boolean identifier(int level) {
		table.addTreeNode(level, "<identifier>");
		if (index >= table.lexemes.size()) {
			table.addTreeNode(level + 1, "<empty>");
			return false;
		}
		int buff = table.lexemes.get(index).code;
		if (buff < 1000) {
			return false;
		}
		table.addTreeNode(level + 1, buff);
		index++;
		return true;
	}
	
	private boolean variableIdentifier(int level) {
		table.addTreeNode(level, "<variable-identifier>");
		if (identifier(level+1) == false) {
			return false;
		}
		buffer.parameters.add(table.lexemes.get(index-1).code);
		return true;
	}
	
	private boolean procedureIdentifier(int level) {
		table.addTreeNode(level, "<procedure-identifier>");
		if (identifier(level+1) == false) {
			return false;
		}
		buffer.id = table.lexemes.get(index-1).code;
		return true;
	}
	
	private boolean unsignedInteger(int level) {
		table.addTreeNode(level, "<unsigned-integer>");
		if (index >= table.lexemes.size()) {
			return false;
		}
		int buff = table.lexemes.get(index).code;
		if ((buff < 500) || (buff > 999)) {
			return false;
		}
		buffer.parameters.add(buff);
		table.addTreeNode(level + 1, buff);
		index++;
		return true;
	}
	
	private boolean actualArgumentsList(int level) {
		table.addTreeNode(level, "<actual-arguments-list>");
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ',')) {
			table.addTreeNode(level + 1, "<empty>");
			return true;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		if (unsignedInteger(level+1) == false) {
			return false;
		}
		
		if (actualArgumentsList(level+1) == false) {
			return false;
		}
		
		return true;
	}

	private boolean actualArguments(int level) {
		table.addTreeNode(level, "<actual-arguments>");
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != '(')) {
			table.addTreeNode(level + 1, "<empty>");
			return true;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;

		if (unsignedInteger(level+1) == false) {
			return false;
		}
		
		if (actualArgumentsList(level+1) == false) {
			return false;
		}
		
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ')')) {
			return false;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		return true;
	}

	private boolean statement(int level) {
		table.addTreeNode(level, "<statement>");
		int oldSize = table.tree.size();
		int buff = index;
		buffer = new ProcedureDeclaration();
		if (procedureIdentifier(level+1) == false) {
			index = buff;
			for (int i = table.tree.size() - 1; i >= oldSize; i--) {
				table.tree.remove(i);
			}
			if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != 403)) {
							
				return false;
			}
			table.addTreeNode(level + 1, table.lexemes.get(index).code);
			index++;
			if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ';')) {
				index = buff;
				return false;
			}
			table.addTreeNode(level + 1, table.lexemes.get(index).code);
			buffer.id = 403;
			table.statements.add(buffer);
			index++;
			return true;
		}

		if (actualArguments(level+1) == false) {
			index = buff;
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ';')) {
			index = buff;
			return false;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		table.statements.add(buffer);
		return true;
	}

	private boolean statementsList(int level) {
		table.addTreeNode(level, "<statements-list>");
		int oldSize = table.tree.size();
		if (statement(level+1) == false) {
			for (int i = table.tree.size() - 1; i >= oldSize; i--) {
				table.tree.remove(i);
			}
			table.addTreeNode(level + 1, "<empty>");
			return true;
		}
		if (statementsList(level+1) == false) {
			return false;
		}
		return true;
	}

	private boolean identifiersList(int level) {
		table.addTreeNode(level, "<identifiers-list>");
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ',')) {
			table.addTreeNode(level + 1, "<empty>");
			return true;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		if (variableIdentifier(level+1) == false) {
			return false;
		}
		if (identifiersList(level+1) == false) {
			return false;
		}
		return true;
	}
	
	private boolean paramatersList(int level) {
		table.addTreeNode(level, "<paramaters-list>");
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != '(')) {
			table.addTreeNode(level + 1, "<empty>");
			return true;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		if (variableIdentifier(level+1) == false) {
			table.addParserError("identifier",index);
			//err expected id
			return false;
		}
		if (identifiersList(level+1) == false) {
			table.addParserError("<identifier>",index);
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ')')) {
			return false;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		return true;
	}

	private boolean procedure(int level) {
		table.addTreeNode(level, "<procedure>");
		int buff = index;
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != 400)) {
			return false;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		buffer = new ProcedureDeclaration();
		if (procedureIdentifier(level+1) == false) {
			index = buff;
			return false;
		}
		if (paramatersList(level+1) == false) {
			index = buff;
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ';')) {
			index = buff;
			return false;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		table.procedureDeclarations.add(buffer);
		return true;
	}

	private boolean procedureDeclarations(int level) {
		table.addTreeNode(level, "<procedure-declarations>");
		int oldSize = table.tree.size();
		if (procedure(level+1) == false) {
			for (int i = table.tree.size() - 1; i >= oldSize; i--) {
				table.tree.remove(i);
			}
			table.addTreeNode(level + 1, "<empty>");
			return true;
		}
		if (procedureDeclarations(level+1) == false) {
			return false;
		}
		return true;
	}

	private boolean declarations(int level) {
		table.addTreeNode(level, "<declarations>");
		if (procedureDeclarations(level+1) == false) {
			return false;
		}
		return true;
	}
	
	private boolean block(int level) {
		table.addTreeNode(level, "<block>");
		if (declarations(level+1) == false) {
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != 401)) {
			table.addParserError("BEGIN",index);
			return false;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		if (statementsList(level+1) == false) {
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != 402)) {
			table.addParserError("END",index);
			return false;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		return true;
	}

	private boolean program(int level){
		table.addTreeNode(level, "<program>");
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != 400)) {
			table.addParserError("PROCEDURE",index);
			return false;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		buffer = new ProcedureDeclaration();
		if (procedureIdentifier(level+1) == false) {
			table.addParserError("<identifier>",index);
			return false;
		}
		if (paramatersList(level+1) == false) {
			return false;
		}
		table.procedureDeclarations.add(buffer);
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ';')) {
			table.addParserError(";",index);
			return false;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		if (block(level+1) == false) {
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ';')) {
			table.addParserError(";",index);
			return false;
		}
		table.addTreeNode(level + 1, table.lexemes.get(index).code);
		index++;
		return true;
	}
	
	private boolean signalProgram(){
		int level = 0;
		table.addTreeNode(level, "<signal-program>");
		if (program(level+1) == false) {
			return false;
		}
		if ((index < table.lexemes.size()) && (table.lexemes.get(index).code == '#')) {
			return true;
		}
		table.addParserError("#",index);
		return false;
	}
	
	public void analize(Tables inTable) {
		table = inTable;
		if (table.lexemes.size()>1) {
			Lexeme buff = table.lexemes.get(table.lexemes.size()-1);
			Lexeme lexeme = new Lexeme('#',buff.row,buff.pos+1);
			table.lexemes.add(lexeme);
			signalProgram();
		}
	}
	
	public Parser() {
	}
}
