
public class Lexeme {
	public int code;
	public int row;
	public int pos;

	public void set(int new_code, int new_row, int new_pos) {
		code = new_code;
		row = new_row;
		pos = new_pos;
	}
	public Lexeme() {
	}
	public Lexeme(int new_code, int new_row, int new_pos) {
		code = new_code;
		row = new_row;
		pos = new_pos;
	}
}
