
public class Tests {
	public void lexer(String fileName) {
		Tables table = new Tables();
		Lexer lexer = new Lexer();
		lexer.analize(table, fileName);
		table.printTables();
		table.printErrors();
	}
	public Tests() {
	}
}
