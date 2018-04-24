import java.util.ArrayList;

public class Coder {
	Tables table;
	ArrayList <Integer> proceduresNamesCodeList = new ArrayList<Integer>();
	ArrayList <Integer> procedureArgsNumList = new ArrayList<Integer>();
	String beforeCall = "";
	String afterCall = "";
	ArrayList <Integer> procedureArgsList = new ArrayList<Integer>();

	private Lexeme identifier(TreeNode root) {
		return table.lexemes.get(root.get(0).value);
	}
	
	private Lexeme variableIdentifier(TreeNode root) {
		return identifier(root.get(0));
	}

	private Lexeme procedureIdentifier(TreeNode root) {
		return identifier(root.get(0));
	}

	private Integer unsignedInteger(TreeNode root) {
		return root.get(0).value;
	}
	
	private void actualArgumentsList(TreeNode root, ArrayList<Integer> list) {
		if (root.get(0).value == Tables.empty) {
			return;
		}
		list.add(unsignedInteger(root.get(1)));
		actualArgumentsList(root.get(2), list);
	}
	
	private void actualArguments(TreeNode root, ArrayList<Integer> list) {
		if (root.get(0).value == Tables.empty) {
			return;
		}
		list.add(unsignedInteger(root.get(1)));
		actualArgumentsList(root.get(2), list);
	}

	private String statement(TreeNode root) {
		if (root.get(0).value != Tables.procedureIdentifier) {
			return "RET\n";
		}
		
		ArrayList<Integer> list = new ArrayList<Integer>(); 
		actualArguments(root.get(1),list);
		Lexeme id = procedureIdentifier(root.get(0));
		
		if (proceduresNamesCodeList.indexOf(id.code) == -1) {
			table.addCodeError(id, table.getTokken(id.code) + " - unknown procedure identifier");
			return "NOP\n";
		}
		
		String code = "";
		int j = 0;
		int n = procedureArgsNumList.get(proceduresNamesCodeList.indexOf(id.code));
		for (int i = 0; i < list.size(); i++) {
			if (j < n) {
				code +="MOV EAX, " + table.getTokken(table.lexemes.get(list.get(i)).code) + "\nPUSH EAX\n";
				j++;
			} else {
				table.addCodeError(table.lexemes.get(list.get(i)), table.getTokken(id.code) + " expected "+n+" arguments, given "+list.size());
				return "NOP\n";
			}
		}
		
		if (n > list.size()) {
			if (list.size() > 0) {
			table.addCodeError(table.lexemes.get(list.get(list.size()-1)), table.getTokken(id.code) +" expected "+n+" arguments, given "+list.size());
			return "NOP\n";
			}
			table.addCodeError(id, table.getTokken(id.code) +" expected "+n+" arguments, given 0");
			return "NOP\n";
		}
		
		code = beforeCall + code + "CALL " + table.getTokken(id.code) + "\n" + afterCall;
		return  code;
	}

	private String statementsList(TreeNode root) {
		if (root.get(0).value == Tables.empty) {
			return "NOP\n";
		}
		return statement(root.get(0)) + statementsList(root.get(1));
	}

	private ArrayList<Lexeme> identifiersList(TreeNode root, ArrayList<Lexeme> list) {
		if (root.get(0).value == Tables.empty) {
			return list;
		}
		list.add(variableIdentifier(root.get(1)));
		return identifiersList(root.get(2), list);
	}

	private ArrayList<Lexeme> parametersList(TreeNode root) {
		ArrayList <Lexeme> list = new ArrayList<Lexeme>();
		if (root.get(0).value == Tables.empty) {
			return list;
		}
		list.add(variableIdentifier(root.get(1)));
		return identifiersList(root.get(2), list);
	}
	
	private String procedure(TreeNode root) {
		Lexeme buff = procedureIdentifier(root.get(1));
		ArrayList <Lexeme> list = parametersList(root.get(2));
		ArrayList <Integer> parameters = new ArrayList<Integer>(list.size());
		int j = 0;
		
		int id = proceduresNamesCodeList.indexOf(buff.code);
		if (id != -1) {
			table.addCodeError(buff, table.getTokken(buff.code) + " has already been used as procedure name");
			return "";
		}
		if (procedureArgsList.indexOf(buff.code) != -1){
			table.addCodeError(buff, table.getTokken(buff.code) + " has already been used as procedure argument");
			return "";
			
		}
		
		
		for (int i = 0; i < list.size(); i++) {
			id = parameters.indexOf(list.get(i).code);
			if (id != -1) {
				table.addCodeError(list.get(i), table.getTokken(list.get(i).code) + " has been used twice in this procedure");
				j++;
				return "";
			} else {
				parameters.add(list.get(i).code);
				j++;
			}
		}
		proceduresNamesCodeList.add(buff.code);
		procedureArgsNumList.add(j);
		
		return "";
	}

	private String procedureDeclarations(TreeNode root) {
		if (root.get(0).value!=Tables.procedure) {
			return "";
		}
		return procedure(root.get(0)) + procedureDeclarations(root.get(1));
	}
	
	private String declarations(TreeNode root) {
		return procedureDeclarations(root.get(0));
	}
	
	private String block(TreeNode root) {
		return declarations(root.get(0)) + statementsList(root.get(2));
	}
	
	private String program(TreeNode root) {
		Lexeme buff = procedureIdentifier(root.get(1));
		ArrayList <Lexeme> list = parametersList(root.get(2));
		String code = "";
		String code1;
		int j = 0;
		
		proceduresNamesCodeList.add(buff.code);
		code1 = table.getTokken(buff.code) + " PROC\n";
		
		for (int i = list.size() - 1; i >= 0; i--) {
			int id = procedureArgsList.indexOf(list.get(i).code);
			if (id != -1) {
				table.addCodeError(list.get(i), table.getTokken(list.get(i).code) + " has already been used as argument in this procedure");
				code += "POP EAX\n";
				code += "NOP; " + table.getTokken(list.get(i).code) + "\n";
				j++;
			} else if (buff.code == list.get(i).code) {
				table.addCodeError(list.get(i), table.getTokken(list.get(i).code) + " has already been used as name of this procedure");
				code += "POP EAX\n";
				code += "NOP; " + table.getTokken(list.get(i).code) + "\n";
				j++;
			} else {
				code += "POP EAX\n";
				code += "MOV " + table.getTokken(list.get(i).code) + ", EAX\n";
				procedureArgsList.add(list.get(i).code);
				j++;
			}
		}
		if (j>0) {
			code = code1 + "POP EBX\n" + code + "PUSH EBX\n";
		} else {
			code = code1 + code;
		}
		String vars = "";
		for (int i = 0; i < procedureArgsList.size(); i++) {
			vars += table.getTokken(procedureArgsList.get(i)) + "	dword	?\n";
			beforeCall += "MOV EAX, " + table.getTokken(procedureArgsList.get(i)) + "\n";
			beforeCall += "PUSH EAX\n";
			afterCall = "MOV " + table.getTokken(procedureArgsList.get(i)) + ", EAX\n" + afterCall;
			afterCall = "POP EAX\n" + afterCall;
		}
		procedureArgsNumList.add(j);
		
		return vars + code + block(root.get(4)) + "RET;end\n" +table.getTokken(buff.code) + " ENDP\n";
	}
	
	private String signalProgram(TreeNode root) {
		return program(root.get(0));
	}
	
	public String generate(Tables inTable) {
		table = inTable;
		return signalProgram(table.treeRoot);
	}
	public Coder() {
	}
}
