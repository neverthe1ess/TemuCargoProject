import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/*
 * 1차 코드
 * 2차원 dp(홀홀, 짝짝) + 재귀(짝이나 홀이 나올때까지 재귀, 나오면 DP)
 * 퐁당퐁당
 * n이 100000쯤 되면 stackOverflow나 OutOfMemory(Heap 부족) 가능성 있음
 * IDE 설정에서 늘려주기
 * */


public class Model2 {
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
        // 인덱스 맞추기 위한 1 더하기, 그리고 0번 시작아닌 1번 시작으로 1 더하기
        dp = new Integer[N][(K / 2) + 2];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            w[i] = Integer.parseInt(st.nextToken());
            v[i] = Integer.parseInt(st.nextToken());
        }
        System.out.println(knapsack(N - 1, K));

        // 메모리 측정 종료
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // 메모리 사용량 출력
        System.out.println("메모리 사용량: " + (memoryAfter - memoryBefore) + " bytes");
    }

    static int knapsack(int i, int k) {
        if (i < 0) return 0;

        if((i % 2 == 0 && k % 2 == 0) || (i % 2 == 1 && k % 2 == 1)){
            // i가 짝수면 짝수인 열만 데이터 유지하기
                if(dp[i][(k + 1) / 2] == null){   // 0, 2, 4, 6 -> 0, 1, 2, 3
                    if(w[i] > k){
                        dp[i][(k + 1) / 2] = knapsack(i  -1, k);
                    } else {
                        dp[i][(k + 1) / 2] = Math.max(knapsack(i - 1, k), knapsack(i - 1, k - w[i]) + v[i]);
                    }
                }
                return dp[i][(k + 1) / 2]; // 값이 있으면 주기
            } else {
                if(w[i] > k){
                    return knapsack(i  -1, k);
                } else {
                    return Math.max(knapsack(i - 1, k), knapsack(i - 1, k - w[i]) + v[i]);
                }
            }
    }
}