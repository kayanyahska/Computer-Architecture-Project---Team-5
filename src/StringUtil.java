import java.math.BigInteger;


public class StringUtil {

	public static int binaryToDecimal(String binary) {
		return new BigInteger(binary, 2).intValue();
	}

}
