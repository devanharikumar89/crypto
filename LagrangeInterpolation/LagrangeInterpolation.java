//Degree of the polynomial - n-1
//Number of solutions - n
//n points (x1, y1), (x2, y2) ... (xn, yn) where yi = f(xi) for all 1<= i <= n
import java.math.BigInteger;
import java.math.BigDecimal;

class LagrangeInterpolation{
	int n;
	int [] xvalues;
	BigInteger [] yvalues;
public LagrangeInterpolation(int n){
	this.n = n;
}

public LagrangeInterpolation(int n, int[] xvalues, BigInteger[] yvalues){
	this.n = n;
	this.xvalues = xvalues;
	this.yvalues = yvalues;
}

//returns the interpolated y value for this x
public BigInteger getInterpolatedValue(int x){
	BigDecimal val= new BigDecimal("0.0");
	double prod=1.0;
	for(int i=0; i<n; i++){
		prod=1.0;
		for(int j=0; j<n; j++){
			if(j!=i){
				prod*=(double)(x-xvalues[j]) / (xvalues[i] - xvalues[j]);
			}
		}

		BigDecimal product = new BigDecimal(prod);
		//System.out.println(product);
		BigDecimal temp = product.multiply(new BigDecimal(yvalues[i]));
		//System.out.println(temp);
		val = val.add(temp);
		//System.out.println(val);

	}
	return val.toBigInteger();
}
public static void main(String[] args) {
	int[]x = {1, 2, 3};
	BigInteger[]y = {new BigInteger("6"), new BigInteger("0"), new BigInteger("-4")};
	LagrangeInterpolation lag = new LagrangeInterpolation(3, x, y);
	System.out.println(lag.getInterpolatedValue(3290));
}
}