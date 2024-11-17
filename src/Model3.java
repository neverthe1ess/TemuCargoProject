import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.StringTokenizer;

public class Model3 {
    public static final String SRC_INPUT_JSON = "src/input_2500.json";
    //public static int[][] items; 불필요
    public static boolean[][] includedcargoes;
    static int[][] cargoesArray;

    public static void main(String[] args) throws Exception {
        System.gc();
        // 메모리 측정 시작
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int K = Integer.parseInt(st.nextToken());
        int N = Integer.parseInt(st.nextToken());
        // 입력 조건 예외 처리
        if (N < 1) {
            System.out.println("잘못된 입력입니다.");
            System.exit(-1);
        } else {
            inputFileReader(K);
            System.out.println("최대 가치 출력 " + calcMaxCargoValue(K, N));
            outputFileWriter(K, N);
        }

        // 메모리 측정 종료
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        // 메모리 사용량 출력
        System.out.println("메모리 사용량: " + (memoryAfter - memoryBefore) + " bytes");

    }

    static int calcMaxCargoValue(int K, int N) {
        int[] maxValueTablecache = new int[N + 1];

        includedcargoes = new boolean[K + 1][N + 1]; // 초기화 추가

        for (int i = 1; i <= K; i++) {
            for (int j = N; j >= cargoesArray[i][0]; j--) { // items -> cargoesArray 으로 수정, j = 1 -> j = N 으로 수정
                if (maxValueTablecache[j] < maxValueTablecache[j - cargoesArray[i][0]] + cargoesArray[i][1]) {  // items -> cargoesArray 으로 수정
                    maxValueTablecache[j] = maxValueTablecache[j - cargoesArray[i][0]] + cargoesArray[i][1];    // items -> cargoesArray 으로 수정
                    includedcargoes[i][j] = true;
                }
            }
        }
        return maxValueTablecache[N];
    }

    static int[][] inputFileReader(int K) throws Exception {
        JSONParser parser = new JSONParser();
        // JSON 파일 읽기
        FileReader reader = new FileReader(SRC_INPUT_JSON);
        // JSON 객체들의 모임인 JsonArray로 파싱
        JSONArray JsonArray = (JSONArray) parser.parse(reader);

        cargoesArray = new int[K + 1][2]; // 초기화 추가

        for (int i = 0; i < K; i++) {
            JSONObject productJsonObject = (JSONObject) JsonArray.get(i);
            Long idxLong = (Long) productJsonObject.get("idx");
            Long weightLong = (Long) productJsonObject.get("weight");
            Long valueLong = (Long) productJsonObject.get("value");

            int idx = idxLong.intValue();
            int weight = weightLong.intValue();
            int value = valueLong.intValue();
            cargoesArray[idx][0] = weight;
            cargoesArray[idx][1] = value;
        }
        return cargoesArray;
    }

    static void outputFileWriter(int K, int N) throws Exception {
        JSONArray cargoJsonArray = new JSONArray();
        JSONArray offloadJsonArray = new JSONArray();

        //JSONObject cargoJsonObject = new JSONObject(); //순서도에 맞게 2개의 for문 내부로 이동

        boolean[] isincludedcargoes = new boolean[N + 1];

        for (int i = K; i > 0; i--) { // i = N -> i = K 로 수정, 순서도에서 실수
            JSONObject cargoJsonObject = new JSONObject();
            if (includedcargoes[i][N]) { // K -> N 으로 수정, 순서도에서 실수
                cargoJsonObject.put("idx", i);
                cargoJsonObject.put("weight", cargoesArray[i][0]);
                cargoJsonObject.put("value", cargoesArray[i][1]);
                cargoJsonArray.add(cargoJsonObject);

                isincludedcargoes[i] = true;
                N = N - cargoesArray[i][0];

            }
        }
        for (int i = 1; i <= K; i++) { //i = K; i <= 0 -> i = 1; i <= K로 수정, 순서도에서 실수
            JSONObject cargoJsonObject = new JSONObject();
            if (!isincludedcargoes[i]) {
                cargoJsonObject.put("idx", i);
                cargoJsonObject.put("weight", cargoesArray[i][0]);
                cargoJsonObject.put("value", cargoesArray[i][1]);
                offloadJsonArray.add(cargoJsonObject);
            }
        }

        writeJsonToFile("cargo_manifest.json", cargoJsonArray);
        writeJsonToFile("offload.json", offloadJsonArray);
    }

    private static void writeJsonToFile(String fileName, JSONArray srcJsonArray) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(srcJsonArray.toJSONString().replace("},{", "},\n{")); // 보기 좋게 개행 문자 추가
        fileWriter.flush();
    }
}