
public class Compiler {
	public String compile(String fileName) {
		String output = "";
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
				output = coder.generate(table);
				System.out.print("\nCode:\n" + output);
			}
		}
		table.printErrors();
		if (table.errors.size()==0) {
			return output;
		} else {
			return ";Error\n";
		}
	}
	public Compiler() {
	}
}
