import java.util.ArrayList;

public class TreeNode {
	ArrayList<TreeNode> kids= new ArrayList<TreeNode>();
	public int value;
	TreeNode(int _code){
		value = _code;
	}
	TreeNode(){
		value = -1;
	}
	void add(TreeNode kid) {
		kids.add(kid);
	}
	void add(int code) {
		TreeNode kid = new TreeNode(code);
		kids.add(kid);
	}
	TreeNode get(int id) {
		if (kids.size() < id) {
			TreeNode kid = new TreeNode();
			System.out.println("TreeNode error: empty Node");
			return kid;
		}
		return kids.get(id);
	}
}
