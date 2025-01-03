package dummymodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/*
 * 1차 코드
 * 2차원 dp(홀홀, 짝짝) + 재귀(짝이나 홀이 나올때까지 재귀, 나오면 DP)
 * 퐁당퐁당
 * n이 100000쯤 되면 stackOverflow나 OutOfMemory(Heap 부족) 가능성 있음
 * IDE 설정에서 늘려주기
 * -Xms4096m -Xmx26624m -Xss1024m
 * */


public class Model2 {
    static Integer[][] maxValueTable;
    static int[] w;
    static int[] v;
    static List<Integer> selectedItems;

    public static void main(String[] args) throws IOException {
        System.gc();
        // 메모리 측정 시작
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int K = Integer.parseInt(st.nextToken());
        int N = Integer.parseInt(st.nextToken());

        w = new int[K];
        v = new int[K];
        int i;
        // 인덱스 맞추기 위한 1 더하기, 그리고 0번 시작아닌 1번 시작으로 1 더하기
        maxValueTable = new Integer[K][(N / 2) + 2];
        selectedItems = new ArrayList<>();

        for (i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            w[i] = Integer.parseInt(st.nextToken());
            v[i] = Integer.parseInt(st.nextToken());
        }
        System.out.println(calcMaxCargoValue(K - 1, N));
        int sum = 0;
        for (Integer selectedItem : selectedItems) {
            System.out.println(w[selectedItem] + " " + v[selectedItem]);
        }

        // 메모리 측정 종료
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // 메모리 사용량 출력
        System.out.println("메모리 사용량: " + (memoryAfter - memoryBefore) + " bytes");
    }

    static int calcMaxCargoValue(int i, int N) {
        if (i < 0) return 0;

        if((i % 2 == 0 && N % 2 == 0) || (i % 2 == 1 && N % 2 == 1)){
            // i가 짝수면 짝수인 열만 데이터 유지하기
                if(maxValueTable[i][(N + 1) / 2] == null){   // 0, 2, 4, 6 -> 0, 1, 2, 3
                    if(w[i] > N){
                        maxValueTable[i][(N + 1) / 2] = calcMaxCargoValue(i  - 1, N);
                    } else {
                        int excludeNewItemValue = calcMaxCargoValue(i - 1, N);
                        int includeNewItemValue = calcMaxCargoValue(i - 1, N - w[i]) + v[i];

                        if(includeNewItemValue > excludeNewItemValue){
                            maxValueTable[i][(N + 1) / 2] = includeNewItemValue;
                            if(!selectedItems.contains(i)){
                                selectedItems.add(i);
                            }
                        } else {
                            maxValueTable[i][(N + 1) / 2] = excludeNewItemValue;
                        }
                    }
                }
                return maxValueTable[i][(N + 1) / 2]; // 값이 있으면 주기
            } else {
                if(w[i] > N){
                    return calcMaxCargoValue(i  -1, N);
                } else {
                    int excludeNewItemValue = calcMaxCargoValue(i - 1, N);
                    int includeNewItemValue = calcMaxCargoValue(i - 1, N - w[i]) + v[i];

                    if(includeNewItemValue > excludeNewItemValue){
                        if(!selectedItems.contains(i)){
                            selectedItems.add(i);
                        }
                        return includeNewItemValue;
                    } else{
                        return excludeNewItemValue;
                    }
                }
            }
    }
}