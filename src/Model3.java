import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/*
 * 2차 코드
 * 2차원 dp bottom-up(for문)
 *  n이 100000쯤 되면 stackOverflow나 OutOfMemory(Heap 부족) 가능성 있음
 * IDE 설정에서 늘려주기
 * */
public class Model3 {
    static int[][] dp;
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
        w = new int[N + 1];
        v = new int[N + 1];
        dp = new int[N + 1][K + 1];

        for (int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine());
            w[i] = Integer.parseInt(st.nextToken());
            v[i] = Integer.parseInt(st.nextToken());
        }
        knapsack(N, K);
        System.out.println(dp[N][K]);

        // 메모리 측정 종료
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // 메모리 사용량 출력
        System.out.println("메모리 사용량: " + (memoryAfter - memoryBefore) + " bytes");
    }

    private static void knapsack(int N, int K) {
        for (int i = 1; i <= N; i++) {
            for(int j = 1; j <= K; j++) {
                if(w[i] > j) {
                    dp[i][j] = dp[i - 1][j];
                }
                else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i - 1][j - w[i]] + v[i]);
                }
            }
        }
    }
}
