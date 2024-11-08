import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
 * 1차 코드
 * 2차원 dp(홀홀, 짝짝) + 재귀(짝이나 홀이 나올때까지 재귀, 나오면 DP)
 * n, k이 어느정도 높아지면 stackOverflow나 OutOfMemory(Heap 부족) 가능성 있음
 * VM Options 설정에서 늘려주기(-Xms4096m -Xmx26624m -Xss1024m)
 * json_simple 라이브러리 사용을 위해 maven 환경이 아닌 경우(인텔리제이)
 * File -> project structure -> Module -> Dependencies -> json_simple.jar 파일 추가
 * https://code.google.com/archive/p/json-simple/downloads
 * */

public class Model2 {
    public static final String SRC_INPUT_JSON = "src/input_1500.json";
    static Integer[][] maxValueTable;
    static int[] w;
    static int[] v;
    static boolean[][] selected;  // 각 아이템이 최적 조합인지 저장하는 배열

    public static void main(String[] args) throws Exception {
        System.gc();
        // 메모리 측정 시작
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
        maxValueTable = new Integer[K][(N / 2) + 2];
        selected = new boolean[K][(N / 2) + 2];
        inputFileReader(); // JSON 파일 읽기

        // 최대 가치 계산
        System.out.println("최대 가치: " + calcMaxCargoValue(K - 1, N));

        // 선택된 아이템 추적(백트래킹)
        List<Integer> selectedItems = findSelectedItems(K - 1, N);

        // 최적 조합의 value를 모두 더했을 때 정답 값(maxValue)이 나와야 함.
        // 즉, 최적의 조합을 정상적으로 출력하였는지 검사하는 코드
        // TODO 순서도 완성 후 코드 지우기, 실제 코드에서 미반영함.
        int sum = 0;
        for (int idx : selectedItems) {
            sum += v[idx];
        }
        System.out.println("최적 조합의 Value 합:" +sum);
        // TODO 여기까지 제거 예정

        outputFileWriter(selectedItems);

        // 메모리 측정 종료
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        // 메모리 사용량 출력
        System.out.println("메모리 사용량: " + (memoryAfter - memoryBefore) + " bytes");
    }

    static int calcMaxCargoValue(int i, int N) {
        if (i < 0) return 0;

        if((i % 2 == 0 && N % 2 == 0) || (i % 2 == 1 && N % 2 == 1)) {
            if(maxValueTable[i][(N + 1) / 2] == null) {
                if(w[i] > N) {
                    maxValueTable[i][(N + 1) / 2] = calcMaxCargoValue(i - 1, N);
                    selected[i][(N + 1) / 2] = false;
                } else {
                    int excludeValue = calcMaxCargoValue(i - 1, N);
                    int includeValue = calcMaxCargoValue(i - 1, N - w[i]) + v[i];

                    if(includeValue > excludeValue) {
                        maxValueTable[i][(N + 1) / 2] = includeValue;
                        selected[i][(N + 1) / 2] = true;
                    } else {
                        maxValueTable[i][(N + 1) / 2] = excludeValue;
                        selected[i][(N + 1) / 2] = false;
                    }
                }
            }
            return maxValueTable[i][(N + 1) / 2];
        } else {
            if(w[i] > N) {
                return calcMaxCargoValue(i - 1, N);
            } else {
                return Math.max(calcMaxCargoValue(i - 1, N), calcMaxCargoValue(i - 1, N - w[i]) + v[i]);
            }
        }
    }

    static void inputFileReader() throws Exception{
        JSONParser parser = new JSONParser();
        // JSON 파일 읽기
        FileReader reader = new FileReader(SRC_INPUT_JSON);
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
        }
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
