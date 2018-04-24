
public class Lexeme {
	public int code = -1;
	public int row = -1;
	public int pos = -1;

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
