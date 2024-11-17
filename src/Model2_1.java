import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Model2_1 {
    public static final String SRC_INPUT_JSON = "src/input_2500.json"; // 입력 파일 이름 상수
    static Integer[][] maxValueTable;
    static int[] w;
    static int[] v;
    static boolean[][] selected;  // 각 아이템이 최적 조합인지 저장하는 배열

    public static void main(String[] args) throws Exception {
        System.gc();
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        int K = Integer.parseInt(st.nextToken());
        int N = Integer.parseInt(st.nextToken());

        // 입력 조건 예외 처리
        if(N < 1) {
            System.out.println("잘못된 입력입니다.");
            System.exit(-1);
        }

        w = new int[K];
        v = new int[K];
        int weightSum = inputFileReader(); // JSON 파일 읽기
        if(weightSum < N) {
            N = weightSum;
        }

        maxValueTable = new Integer[K][(N / 2) + 2];
        selected = new boolean[K][(N / 2) + 2];

        // 최대 가치 계산
        System.out.println("최대 가치: " + calcMaxCargoValue(K - 1, N));

        // 선택된 아이템 추적(백트래킹)
        List<Integer> selectedItems = findSelectedItems(K - 1, N);

        outputFileWriter(selectedItems);

        // 메모리 측정 코드
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("메모리 사용량: " + (memoryAfter - memoryBefore) + " bytes");
    }

    static int calcMaxCargoValue(int i, int N) {
        // 재귀 종료 조건(Base Condition)
        if (i < 0) return 0;
        // i가 홀수 N이 홀수 일때, i가 짝수 N가 짝수일때
        if((i % 2 == 0 && N % 2 == 0) || (i % 2 == 1 && N % 2 == 1)) {
            if(maxValueTable[i][(N + 1) / 2] == null) {
                if(w[i] > N) {
                    // 이미 화물 무게가 적재량보다 커서 비행기에 넣을 수 없을때
                    maxValueTable[i][(N + 1) / 2] = calcMaxCargoValue(i - 1, N);
                    // 최적 조합이 아님을 표기함
                    selected[i][(N + 1) / 2] = false;
                } else {
                    // 새로운 화물을 비행기의 넣을때와 넣지 않을 때 비교
                    int excludeValue = calcMaxCargoValue(i - 1, N);
                    int includeValue = calcMaxCargoValue(i - 1, N - w[i]) + v[i];

                    // 더 크다면 최적 조합에 포함하기
                    if(includeValue > excludeValue) {
                        maxValueTable[i][(N + 1) / 2] = includeValue;
                        selected[i][(N + 1) / 2] = true;
                    } else {
                        maxValueTable[i][(N + 1) / 2] = excludeValue;
                        selected[i][(N + 1) / 2] = false;
                    }
                }
            }
            // 값이 저장되어 있다면 주기(캐싱)
            return maxValueTable[i][(N + 1) / 2];
        } else { // 홀홀, 짝짝이 아니면 재귀를 수행하나, i -1에서 홀홀, 짝짝이 맞을거라서 조기에 재귀가 종료됨. Ex. (2,3) -> (1,3)
            if(w[i] > N) {
                return calcMaxCargoValue(i - 1, N);
            } else {
                return Math.max(calcMaxCargoValue(i - 1, N), calcMaxCargoValue(i - 1, N - w[i]) + v[i]);
            }
        }
    }

    static int inputFileReader() throws Exception{
        JSONParser parser = new JSONParser();
        // JSON 파일 읽기
        FileReader reader = new FileReader(SRC_INPUT_JSON);
        int weightSum = 0;

        // JSON 객체들의 모임인 JsonArray로 파싱
        JSONArray JsonArray = (JSONArray) parser.parse(reader);
        for (Object obj : JsonArray) {
            JSONObject productJsonObject = (JSONObject) obj;

            Long idxLong =(Long) productJsonObject.get("idx");
            Long weightLong = (Long) productJsonObject.get("weight");
            Long valueLong = (Long) productJsonObject.get("value");

            int idx = idxLong.intValue();
            int weight = weightLong.intValue();
            int value = valueLong.intValue();

            w[idx] = weight;
            v[idx] = value;

            weightSum += weight;
        }
        return weightSum;
    }

    static void outputFileWriter(List<Integer> cargoManifest) throws Exception{
        JSONArray cargoJsonArray = new JSONArray();
        JSONArray offloadJsonArray = new JSONArray();

        for(int i = 0; i < w.length; i++){
            if(cargoManifest.contains(i)){
                addToJsonArray(i, cargoJsonArray);
            } else {
                addToJsonArray(i, offloadJsonArray);
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

    private static void addToJsonArray(int i, JSONArray targetJsonArray) {
        JSONObject cargoJsonObject = new JSONObject();
        cargoJsonObject.put("idx", i);
        cargoJsonObject.put("weight", w[i]);
        cargoJsonObject.put("value", v[i]);
        targetJsonArray.add(cargoJsonObject);
    }


    static List<Integer> findSelectedItems(int i, int N) {
        List<Integer> result = new ArrayList<>();
        while (i >= 0) {
            if ((i % 2 == 0 && N % 2 == 0) || (i % 2 == 1 && N % 2 == 1)) {
                if (selected[i][(N + 1) / 2]) {
                    result.add(i);
                    N -= w[i];
                }
            } else {
                if (N >= w[i] && calcMaxCargoValue(i, N) != calcMaxCargoValue(i - 1, N)) {
                    result.add(i);
                    N -= w[i];
                }
            }
            i--;
        }
        return result;
    }
}
