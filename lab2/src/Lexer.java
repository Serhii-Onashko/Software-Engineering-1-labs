import java.io.FileReader;
import java.io.IOException;

public class Lexer {
	public void analize(Tables table, String fileName) {
		try(FileReader reader = new FileReader("src/"+fileName))
        {
            int digit= reader.read();
            int i = 1;//position in row
            int j = 1;//row number
        	String str;
            while((digit) != -1){
                switch(table.getAttrs(digit)) {
	                case 0: //whitespace
	                	if (digit == 10) {
	                		j++;
	                		i=1;
	                		digit = reader.read();//just read next symbol
	                	} else if (digit == 13) {
	                		digit = reader.read();//just read next symbol
	                	} else {
	                		i++;
	                		digit = reader.read();//just read next symbol
	                	}
	            		break;
	                case 1: //digit 
	                	str = "";
	                    while(table.getAttrs(digit) == 1) {
	                    	str+=(char)digit;
		                    i++;
		                	digit = reader.read();//read next symbol
		                	if  (digit == -1) {
		                		break;
		                	}
	                    }
		                table.addConst(str,j,i - str.length());
	            		break;
	            	case 2: //Letter
	                	str = "";
	                    while ((table.getAttrs(digit) == 1) || (table.getAttrs(digit) == 2)) {
	                    	str+=(char)digit;
		                    i++;
		                	digit = reader.read();//read next symbol
		                	if  (digit == -1) {
		                		break;
		                	}
	                    }
		                table.addIds(str,j,i - str.length());
	            		break;
	            	case 3: //Delimiter
	            		table.addLexeme(digit,j,i);
	                    i++;
	                	digit = reader.read();//read next symbol
	            		break;
	                case 5: //"(" - may be beginning of "(*" - comments
	                	digit = reader.read();//read next symbol
	                    i++;
	                	if (digit != '*') {
	                		table.addLexeme(((int)'('), j, i - 1);
		            		break;
	                	}
	                	Boolean previous=false;
	                	while (digit != -1) {
	    	               
	                		if (digit == 10) {
		                		j++;
		                		i=1;
		                		digit = reader.read();//just read next symbol
		                	} else if (digit == 13) {
		                		digit = reader.read();//just read next symbol
		                	} else {
		                		i++;
		                		digit = reader.read();//read next symbol
		                		if (digit == '*') {
		                			previous = true;
		                		} else if (previous) {
		                			if (digit == ')') {
		                				break;
		                			} else {
		                				previous = false;
		                			}
		                		}
		                	}
	                	}
	                	if (digit == -1) {
	                		table.addError("Lexer: Error (line "+j+", column "+i +"): expected '*)'  but found EOF");
	                	} else {
	                		if (previous) {
		    	                i++;
		    	                digit = reader.read();//read next symbol
	                		}
	                	}
	            		break;
	                case 6: // wrong character
		                table.addError("Lexer: Error (line "+j+", column "+i +"): wrong character { "+((char)digit)+" } ");
	                    i++;
	                	digit = reader.read();//read next symbol
	            		break;
	            	default:
	                    System.out.print("wrong number in attrs for " + digit);
	                    i++;
	                	digit = reader.read();//read next symbol
	            		break;
                }
            } 
            reader.close();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
	}
	public Lexer() {
	}
}
