public class Parser {
	int index=0;
	Tables table;
	
	private boolean identifier(TreeNode root) {
		TreeNode node = new TreeNode(Tables.identifier);
		if (index >= table.lexemes.size()) {
			return false;
		}
		int buff = table.lexemes.get(index).code;
		if (buff < 1000) {
			return false;
		}
		node.add(index);
		root.add(node);
		index++;
		return true;
	}
	
	private boolean variableIdentifier(TreeNode root) {
		TreeNode node = new TreeNode(Tables.variableIdentifier);
		if (identifier(node) == false) {
			return false;
		}
		root.add(node);
		return true;
	}
	
	private boolean procedureIdentifier(TreeNode root) {
		TreeNode node = new TreeNode(Tables.procedureIdentifier);
		if (identifier(node) == false) {
			return false;
		}
		root.add(node);
		return true;
	}
	
	private boolean unsignedInteger(TreeNode root) {
		TreeNode node = new TreeNode(Tables.unsignedInteger);
		if (index >= table.lexemes.size()) {
			return false;
		}
		int buff = table.lexemes.get(index).code;
		if ((buff < 500) || (buff > 999)) {
			return false;
		}
		node.add(index);
		root.add(node);
		index++;
		return true;
	}
	
	private boolean actualArgumentsList(TreeNode root) {
		TreeNode node = new TreeNode(Tables.actualArguments);
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ',')) {
			node.add(Tables.empty);
			root.add(node);
			return true;
		}
		node.add(index);
		index++;
		if (unsignedInteger(node) == false) {
			return false;
		}
		
		if (actualArgumentsList(node) == false) {
			return false;
		}

		root.add(node);
		return true;
	}

	private boolean actualArguments(TreeNode root) {
		TreeNode node = new TreeNode(Tables.actualArguments);
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != '(')) {
			node.add(Tables.empty);
			root.add(node);
			return true;
		}
		node.add(index);
		index++;

		if (unsignedInteger(node) == false) {
			return false;
		}
		
		if (actualArgumentsList(node) == false) {
			return false;
		}
		
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ')')) {
			return false;
		}
		node.add(index);
		root.add(node);
		index++;
		return true;
	}

	private boolean statement(TreeNode root) {
		TreeNode node = new TreeNode(Tables.statement);
		int buff = index;
		if (procedureIdentifier(node) == false) {
			index = buff;
			if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != 403)) {
							
				return false;
			}
			node.add(index);
			index++;
			if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ';')) {
				return false;
			}
			node.add(index);
			root.add(node);
			index++;
			return true;
		}

		if (actualArguments(node) == false) {
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ';')) {
			return false;
		}
		node.add(index);
		root.add(node);
		index++;
		return true;
	}

	private boolean statementsList(TreeNode root) {
		TreeNode node = new TreeNode(Tables.statementsList);
		int buff = index;
		if (statement(node) == false) {
			index = buff;
			node.add(Tables.empty);
			root.add(node);
			return true;
		}
		if (statementsList(node) == false) {
			return false;
		}
		root.add(node);
		return true;
	}

	private boolean identifiersList(TreeNode root) {
		TreeNode node = new TreeNode(Tables.identifiersList);
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ',')) {
			node.add(Tables.empty);
			root.add(node);
			return true;
		}
		node.add(index);
		index++;
		if (variableIdentifier(node) == false) {
			return false;
		}
		if (identifiersList(node) == false) {
			return false;
		}
		root.add(node);
		return true;
	}
	
	private boolean paramatersList(TreeNode root) {
		TreeNode node = new TreeNode(Tables.paramatersList);
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != '(')) {
			node.add(Tables.empty);
			root.add(node);
			return true;
		}
		node.add(index);
		index++;
		if (variableIdentifier(node) == false) {
			table.addParserError("identifier",index);
			return false;
		}
		if (identifiersList(node) == false) {
			table.addParserError("<identifier>",index);
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ')')) {
			return false;
		}
		node.add(index);
		root.add(node);
		index++;
		return true;
	}

	private boolean procedure(TreeNode root) {
		TreeNode node = new TreeNode(Tables.procedure);
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != 400)) {
			return false;
		}
		node.add(index);
		index++;
		if (procedureIdentifier(node) == false) {
			return false;
		}
		if (paramatersList(node) == false) {
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ';')) {
			return false;
		}
		node.add(index);
		root.add(node);
		index++;
		return true;
	}

	private boolean procedureDeclarations(TreeNode root) {
		TreeNode node = new TreeNode(Tables.procedureDeclarations);
		int buff = index;
		if (procedure(node) == false) {
			index = buff;
			node.add(Tables.empty);
			root.add(node);
			return true;
		}
		if (procedureDeclarations(node) == false) {
			root.add(node);
			return false;
		}
		root.add(node);
		return true;
	}

	private boolean declarations(TreeNode root) {
		TreeNode node = new TreeNode(Tables.declarations);
		if (procedureDeclarations(node) == false) {
			root.add(node);
			return false;
		}
		root.add(node);
		return true;
	}
	
	private boolean block(TreeNode root) {
		TreeNode node = new TreeNode(Tables.block);
		if (declarations(node) == false) {
			root.add(node);
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != 401)) {
			table.addParserError("BEGIN",index);
			root.add(node);
			return false;
		}
		node.add(index);
		index++;
		if (statementsList(node) == false) {
			root.add(node);
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != 402)) {
			table.addParserError("END",index);
			root.add(node);
			return false;
		}
		node.add(index);
		root.add(node);
		index++;
		return true;
	}

	private boolean program(TreeNode root){
		TreeNode node = new TreeNode(Tables.program);
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != 400)) {
			table.addParserError("PROCEDURE",index);
			root.add(node);
			return false;
		}
		node.add(index);
		index++;
		if (procedureIdentifier(node) == false) {
			table.addParserError("<identifier>",index);
			root.add(node);
			return false;
		}
		if (paramatersList(node) == false) {
			root.add(node);
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ';')) {
			table.addParserError(";",index);
			root.add(node);
			return false;
		}
		node.add(index);
		index++;
		if (block(node) == false) {
			root.add(node);
			return false;
		}
		if ((index >= table.lexemes.size()) || (table.lexemes.get(index).code != ';')) {
			table.addParserError(";",index);
			root.add(node);
			return false;
		}
		node.add(index);
		root.add(node);
		index++;
		return true;
	}
	
	private boolean signalProgram(){
		table.treeRoot = new TreeNode(Tables.signalProgram);
		if (program(table.treeRoot) == false) {
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
