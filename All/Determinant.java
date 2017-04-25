//http://stackoverflow.com/questions/16602350/calculating-matrix-determinant

import java.math.BigInteger;
public class Determinant {

    BigInteger A[][];
    BigInteger m[][];
    int N;
        int start;
        int last;

        public Determinant (BigInteger A[][], int N, int start, int last){
            this.A = A;
            this.N = N;
            this.start = start;
            this.last = last;
        }

        public BigInteger[][] generateSubArray (BigInteger A[][], int N, int j1){
            m = new BigInteger[N-1][];
            for (int k=0; k<(N-1); k++)
                    m[k] = new BigInteger[N-1];

            for (int i=1; i<N; i++)
            {
                    int j2=0;
                    for (int j=0; j<N; j++)
                    {
                      if(j == j1)
                              continue;
                      m[i-1][j2] = A[i][j];
                      j2++;
                    }
            }
            return m;
        }
    /*
     * Calculate determinant recursively
     */
    public BigInteger determinant(BigInteger A[][], int N)
    {
        BigInteger res, res1, res2;

        // Trivial 1x1 matrix
        if (N == 1){
            res = A[0][0];
        }
        // Trivial 2x2 matrix
        else if (N == 2){
            res1 = A[0][0].multiply(A[1][1]);
            res2 = A[1][0].multiply(A[0][1]);
            res = res1.subtract(res2);}
        // NxN matrix
        else
        {
            res=new BigInteger("0");
            for (int j1=0; j1<N; j1++)
            {
                            m = generateSubArray (A, N, j1);
                            res1 = A[0][j1].multiply(determinant(m, N-1));
                            if(j1%2==0){
                                res=res.add(res1);
                            }else{
                                res=res.subtract(res1);
                            }
                            System.out.println(res);
            }
        }
        return res;
    }
public static void main(String[] args){

    BigInteger [][]arr = {{new BigInteger("3"), new BigInteger("5"), new BigInteger("2") }, {new BigInteger("4"), new BigInteger("1"), new BigInteger("2") }, {new BigInteger("3"), new BigInteger("5"), new BigInteger("7") }};
    Determinant det = new Determinant(arr, 3, 0, 2);
    System.out.println(det.determinant(arr, 3));
}

}