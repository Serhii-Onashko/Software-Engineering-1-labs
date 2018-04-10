
public class Tests {
	public void lexer(String fileName) {
		Tables table = new Tables();
		Lexer lexer = new Lexer();
		lexer.analize(table, fileName);
		table.printTables();
		table.printErrors();
	}
	public void parser(String fileName) {
		Tables table = new Tables();
		Lexer lexer = new Lexer();
		lexer.analize(table, fileName);
		table.printTables();
		if (table.errors.size()==0) {
			Parser parser = new Parser();
			parser.analize(table);
			table.printTree();
			//for (int i=0; i<table.procedureDeclarations.size();i++) {
			//	System.out.println(table.procedureDeclarations.get(i).id);
			//	for (int j=0; j<table.procedureDeclarations.get(i).parameters.size();j++) {
			//		System.out.println("---"+table.procedureDeclarations.get(i).parameters.get(j));
			//	}
			//}
			//for (int i=0; i<table.statements.size();i++) {
			//	System.out.println(table.statements.get(i).id);
			//	for (int j=0; j<table.statements.get(i).parameters.size();j++) {
			//		System.out.println("---"+table.statements.get(i).parameters.get(j));
			//	}
			//}
		}
		table.printErrors();
	}
	public Tests() {
	}
}
