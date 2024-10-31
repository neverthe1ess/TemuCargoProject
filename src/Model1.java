import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
/*
* 베이스 코드
* 2차원 top-down(재귀) dp
*  n이 100000쯤 되면 stackOverflow나 OutOfMemory(Heap 부족) 가능성 있음
 * IDE 설정에서 늘려주기
* */

public class Model1 {
    static Integer[][] dp;
    static int[] w;
    static int[] v;
    public static void main(String[] args) throws IOException {
        System.gc();
        // 메모리 측정 시작
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int N = Integer.parseInt(st.nextToken());
        int K = Integer.parseInt(st.nextToken());
        w = new int[N];
        v = new int[N];
        dp = new Integer[N][K + 1];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            w[i] = Integer.parseInt(st.nextToken());
            v[i] = Integer.parseInt(st.nextToken());
        }
        System.out.println(knapsack(N- 1, K));
        // 메모리 측정 종료
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // 메모리 사용량 출력
        System.out.println("메모리 사용량: " + (memoryAfter - memoryBefore) + " bytes");

    }
    static int knapsack(int i, int k){
        if(i < 0) return 0;

        if(dp[i][k] == null){
            if(w[i] > k){
                dp[i][k] = knapsack(i - 1, k);
            } else {
                dp[i][k] = Math.max(knapsack(i - 1, k), knapsack(i - 1, k - w[i]) + v[i]);
            }
        }
        return  dp[i][k];
    }
}
