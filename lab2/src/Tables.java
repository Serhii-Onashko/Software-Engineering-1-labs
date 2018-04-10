import java.util.ArrayList;
public class Tables {
	//lexer
	ArrayList<String> keys = new ArrayList<String>(4);//   400  -  499
	ArrayList<String> consts = new ArrayList<String>();//  500  -  999
	ArrayList<String> ids = new ArrayList<String>();//    1000  -  ...
	ArrayList<Integer> attrs = new ArrayList<Integer>(256);
	ArrayList<Lexeme> lexemes = new ArrayList<Lexeme>();
	ArrayList<String> errors = new ArrayList<String>();
	//parser
	ArrayList<ProcedureDeclaration> procedureDeclarations = new ArrayList<ProcedureDeclaration>();
	ArrayList<ProcedureDeclaration> statements = new ArrayList<ProcedureDeclaration>();
	ArrayList<TreeNode> tree = new ArrayList<TreeNode>();
	//lexer
	public String getTokken(int code){
		if (code < 128) {
			return String.valueOf((char)code);
		}
		if (code < 500) {
			return keys.get(code-400);
		}
		if (code < 1000) {
			return consts.get(code-500);
		}
		return ids.get(code-1000);
	}
	public void printTables(){
		int i;
		Lexeme lexeme;
		System.out.println("\nLexemes:");
		for(i=0; i<lexemes.size(); i++) {
			lexeme = lexemes.get(i);
			System.out.format("%04d %04d %05d "+ getTokken(lexeme.code)+"%n", lexeme.row, lexeme.pos, lexeme.code);
		}
		System.out.println("\nKeys:");
		for(i=0; i<keys.size(); i++) {
			System.out.format("%05d "+ keys.get(i)+"%n", i+400);
		}
		System.out.println("\nConsts:");
		for(i=0; i<consts.size(); i++) {
			System.out.format("%05d "+ consts.get(i)+"%n", i+500);
		}
		System.out.println("\nIdentifier:");
		for(i=0; i<ids.size(); i++) {
			System.out.format("%05d "+ ids.get(i)+"%n", i+1000);
		}
	}
	public void printErrors(){
		int i;
		if (errors.size()>0) {
			System.out.println("\nErrors:");
			for(i=0; i<errors.size(); i++) {
				System.out.format(errors.get(i)+"%n");
			}
		} else {
			System.out.println("\nNo errors");
		}
	}
	public int getConstsId(String str){
		int id = consts.indexOf(str);
		if (id == -1) {
			if(consts.size()<=500) {
				id = consts.size();
				consts.add(str);
			} else {
				return -1;
			}
		}
		return id + 500;
	}
	private int getKeyId(String str) {
		//return index of element in keys(+400) or ids(+1000)
		int id = keys.indexOf(str);
		if (id == -1) {
			id = ids.indexOf(str);
			if (id == -1) {
				id = ids.size();
				ids.add(str);
			}
			return id + 1000;	
		}
		return id + 400;
	}
	public void addConst(String str, int j, int i) {
		int constsId = getConstsId(str);
		if (constsId == -1){
			addError("Lexer: Error (line "+j+", column "+i +"): more than 500 consts");
		} else {
			addLexeme(constsId, j, i);
		}
		
	}
	public void addIds(String str, int j, int i) {
		addLexeme(getKeyId(str), j, i);
	}
	public void addError(String str) {
		errors.add(str);
	}
	public void addLexeme(int code, int j, int i) {
		Lexeme lexeme = new Lexeme(code, j, i);
		lexemes.add(lexeme);
	}
	public int getAttrs(int digit) {
    	if ((digit >= 0)&&(digit <=127)){
    		return attrs.get(digit);
    	}
		return 6;
	}
	public Tables() {
		for (int i = 0; i <= 127; i++) {
			attrs.add(6);
		}
		//white space
		attrs.set(8, 0);//backspace
		attrs.set(9, 0);//tab
		attrs.set(10, 0);//next line
		attrs.set(11, 0);//tab
		attrs.set(13, 0);//carriage return
		attrs.set(32, 0);//space
		for (int i = 48; i <= 57; i++) {
			attrs.set(i, 1);//digits
		}
		for (int i = 65; i <= 90; i++) {
			attrs.set(i, 2);//letters
		}
		attrs.set(59, 3);//;
		attrs.set(40, 5);//(   ==> (*
		attrs.set(41, 3);//)
		attrs.set(44, 3);//,
		keys.add("PROCEDURE");//0
		keys.add("BEGIN");//1
		keys.add("END");//2
		keys.add("RETURN");//3
	}
	//parser
	public void addParserError(String str, int index) {
		if (index<lexemes.size()){
			Lexeme buff = lexemes.get(index);
			addError("Parser: Error (line "+buff.row+", column "+buff.pos +"): expected {"+str+"} but found {"+getTokken(buff.code)+"}");
		} else {
			addError("Parser: Error (on eof): expected {#} but found EOF");
		}
		
	}
	void addTreeNode(int _level, String _node) {
		TreeNode buff = new TreeNode(_level,_node);
		tree.add(buff);
	}
	void addTreeNode(int _level, int _code) {
		TreeNode buff = new TreeNode(_level,_code);
		tree.add(buff);
	}
	String getTreeNode(int id) {
		if (id<tree.size()) {
			TreeNode node = tree.get(id);
			String buff = "";
			for (int i=0;i<node.level*2;i++) {
				buff += '.';
			}
			if (node.code>0) {
				buff += node.code +" "+ getTokken(node.code) +"\n";
			} else {
				buff += node.node +"\n";
			}
			return buff;
		}
		return "OutOfRange";
	}
	void printTree() {
		System.out.println("\nTree:\n");
		for (int i = 0; i < tree.size(); i++) {
			System.out.print(getTreeNode(i));
		}
	}
}
