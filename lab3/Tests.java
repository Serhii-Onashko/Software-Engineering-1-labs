
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
		}
		table.printErrors();
	}
	public void coder(String fileName) {
		Tables table = new Tables();
		Lexer lexer = new Lexer();
		lexer.analize(table, fileName);
		table.printTables();
		if (table.errors.size()==0) {
			Parser parser = new Parser();
			parser.analize(table);
			table.printTree();
			if (table.errors.size()==0) {
				Coder coder = new Coder();
				System.out.print("\nCode:\n" + coder.generate(table));
			}
		}
		table.printErrors();
	}
	public Tests() {
	}
}
