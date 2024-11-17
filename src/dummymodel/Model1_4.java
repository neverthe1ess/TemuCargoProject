package dummymodel;

import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Model1_4 {
    // 입력 JSON 파일 경로
    public static final String SRC_INPUT_JSON = "src/input_1500.json";
    // 아이템의 무게와 가치를 저장할 배열
    public static int[][] items;
    // 아이템의 개수
    static int K;
    // 적재 가능 최대 무게
    static int N;
    // 무게값
    static int W_i;
    // 가치값
    static int V_i;
    // 최대 가치를 계산하기 위한 테이블
    static int maxValueTable[][];
    // 적재된 아이템을 표시하는 배열
    static int Load_items_0[];
    // 적재된 아이템을 저장하는 배열
    static int Load_items[][];
    // 선택된 아이템 배열
    static ArrayList<int[]> selecteditems = new ArrayList<>();

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

        // 정보 읽기
        items = inputFileReader(); // JSON 파일 읽기
        K = items.length;
        if (K < 1) {
            System.out.println("잘못된 입력입니다.");
            System.exit(-1);
        }
        // DP 테이블 및 배열 초기화
        maxValueTable = new int[N+ 1][K];
        Load_items_0 = new int[N];
        Load_items = new int[N][K];
        // 최대 적재 가치 계산
        calcMaxCargoValue();
        // 최대 가치 출력
        System.out.println("최대 가치: " + maxValueTable[N][K-1]);
        selecteditems = findSelectedItems();
        // 최적 조합 저장
        outputFileWriter(selecteditems);
    }

    static void calcMaxCargoValue() throws Exception {
        for (int i = 0; i < K; i++) {
            W_i = items[i + 1][0];
            V_i = items[i + 1][1];
            // 적재된 아이템을 표시하는 배열 초기화
            Arrays.fill(Load_items_0, 0);
            for (int j = W_i; j <= N; j++) {
                // 최대 가치 값보다 현재 가치가 낮다면
                if (maxValueTable[i][j] < maxValueTable[i][j - W_i] + V_i) {
                    // 최대 가치 테이블 갱신
                    maxValueTable[i + 1][j] = maxValueTable[i][j - W_i] + V_i;
                    Load_items_0[j] = 1;
                } else {
                    // 최대 가치 테이블에 기존 가치 테이블 값 대입
                    maxValueTable[i + 1][j] = maxValueTable[i][j];
                }
            }
            for (int k = N; k >= W_i; k--) {
                // 최대 가치가 변경 되었었었다면
                if (Load_items_0[k] == 1) {
                    //System.arraycopy 데이터를 복사하기 위해 사용
                    System.arraycopy(Load_items[k - W_i], 0, Load_items[k], 0, K);
                    Load_items[k][i] = 1;
                }
            }
        }
    }
    // 파일 읽기
    static int[][] inputFileReader() throws Exception {
        // Json 객체를 파싱하기 위한 선언
        JSONParser parser = new JSONParser();
        // 파일경로로 부터 파일 읽기
        FileReader reader = new FileReader(SRC_INPUT_JSON);
        // 파일에서 JsonArray로 객체 파싱
        JSONArray jsonArray = (JSONArray) parser.parse(reader);
        // 물건 배열
        int[][] cargoesArray = new int[jsonArray.size()][2];
        // 물건배열에 무게, 가치 추가
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject productJsonObject = (JSONObject) jsonArray.get(i);
            int weight = ((Long) productJsonObject.get("weight")).intValue();
            int value = ((Long) productJsonObject.get("value")).intValue();
            cargoesArray[i][0] = weight;
            cargoesArray[i][1] = value;
        }
        return cargoesArray;
    }
    // 파일 출력
    static void outputFileWriter(ArrayList<int[]> selectedItems) throws Exception {
        // JsonArray 선언
        JSONArray jsonArray = new JSONArray();
        // JsonObject 선언
        JSONObject jsonObject = new JSONObject();
        // jsonObject에 선택된 아이템들 추가하고 jsonArray에 추가
        for (int[] item : selectedItems) {
            jsonObject.put("idx", item[0]);
            jsonObject.put("weight", item[1]);
            jsonObject.put("value", item[2]);
            jsonArray.add(jsonObject);
        }
        // 파일에 json형으로 jsonArray 쓰기
        try (FileWriter file = new FileWriter("output.json")) {
            file.write(jsonArray.toJSONString());
        }

        System.out.println("파일 저장이 완료되었습니다: output.json");
    }
    static ArrayList<int[]> findSelectedItems() throws Exception {
        ArrayList<int[]> Best_Load = new ArrayList<>();
        // 최적 조합 저장
        for (int i = 0; i < K; i++) {
            if (Load_items[N][i] == 1) {
                int[] loadInfo = {i, items[i][0], items[i][1]};
                Best_Load.add(loadInfo);
            }
        }
        return Best_Load;
    }
}