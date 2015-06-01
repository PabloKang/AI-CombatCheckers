import java.util.Scanner;
import java.util.regex.Pattern;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		Scanner s = new Scanner(System.in);
		while(true) {
			String str = s.next(Pattern.compile("[a-zA-z_\\s]*[,]?"));
			System.out.println(str);
		}
	}

}
