
public class TreeNode {
	public int level;
	public int code = -1;
	public String node;
	TreeNode(int _level, String _node){
		level = _level;
		node = _node;
	}
	TreeNode(int _level, int _code){
		level = _level;
		code = _code;
	}
}
