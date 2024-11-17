package dummymodel;

import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Model1_3 {
    public static final String SRC_INPUT_JSON = "src/input_1500.json";
    public static int[][] items;
    static int K;
    // 적재 가능 무게(N) 입력
    static int N;
    static int W_i;
    static int V_i;
    static int maxValueTable[][];
    static int Load_items_0[];
    static int Load_items[][];

    public static void main(String[] args) throws Exception {
        System.gc();
        // 메모리 측정 시작
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        N = Integer.parseInt(st.nextToken());

        // 입력 조건 예외 처리
        if (N < 1) {
            System.out.println("잘못된 입력입니다.");
            System.exit(-1);
        }

        items = inputFileReader(); // JSON 파일 읽기
        if (items.length < 1) {
            System.out.println("잘못된 입력입니다.");
            System.exit(-1);
        }

        maxValueTable = new int[N + 1][K];
        Load_items_0 = new int[N];
        Load_items = new int[N][K];

        calcMaxCargoValue();
    }

    static void calcMaxCargoValue() throws Exception {
        for (int i = 0; i < K; i++) {
            W_i = items[i + 1][0];
            V_i = items[i + 1][1];
            Arrays.fill(Load_items_0, 0);

            for (int j = W_i; j <= N; j++) {
                if (maxValueTable[i][j] < maxValueTable[i][j - W_i] + V_i) {
                    maxValueTable[i + 1][j] = maxValueTable[i][j - W_i] + V_i;
                    Load_items_0[j] = 1;
                } else {
                    maxValueTable[i + 1][j] = maxValueTable[i][j];
                }
            }

            for (int k = N; k >= W_i; k--) {
                if (Load_items_0[k] == 1) {
                    System.arraycopy(Load_items[k - W_i], 0, Load_items[k], 0, K);
                    Load_items[k][i] = 1;
                }
            }
        }

        System.out.println("최대 가치: " + maxValueTable[N][K-1]);
        ArrayList<int[]> Best_Load = new ArrayList<>();
        ArrayList<int[]> selecteditems = new ArrayList<>();

        for (int i = 0; i < K; i++) {
            if (Load_items[N][i] == 1) {
                int[] loadInfo = {i, items[i][0], items[i][1]};

                Best_Load.add(loadInfo);
            }
        }
        selecteditems = Best_Load;  // 무슨 의도?
        outputFileWriter(selecteditems);
    }

    static int[][] inputFileReader() throws Exception {
        JSONParser parser = new JSONParser();
        FileReader reader = new FileReader(SRC_INPUT_JSON);
        JSONArray jsonArray = (JSONArray) parser.parse(reader);

        int[][] cargoesArray = new int[jsonArray.size()][2];

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject productJsonObject = (JSONObject) jsonArray.get(i);
            int weight = ((Long) productJsonObject.get("weight")).intValue();
            int value = ((Long) productJsonObject.get("value")).intValue();
            cargoesArray[i][0] = weight;
            cargoesArray[i][1] = value;
        }
        return cargoesArray;
    }

    static void outputFileWriter(ArrayList<int[]> selectedItems) throws Exception {
        JSONArray jsonArray = new JSONArray();

        for (int[] item : selectedItems) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("idx", item[0]);
            jsonObject.put("weight", item[1]);
            jsonObject.put("value", item[2]);
            jsonArray.add(jsonObject);
        }

        try (FileWriter file = new FileWriter("output.json")) {
            file.write(jsonArray.toJSONString());
        }

        System.out.println("파일 저장이 완료되었습니다: output.json");
    }
}