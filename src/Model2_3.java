import java.io.*;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Model2_3 {
    // 입력 JSON 파일 경로
    public static final String SRC_INPUT_JSON = "src/input_worst.json";
    // 최대 가치 계산 테이블
    static int[][] maxValueTable;
    // 아이템의 개수
    static int K;
    // 적재 가능 최대 무게
    static int N;
    // 무게값
    static int[] w;
    // 가치값
    static int[] v;
    // 각 아이템이 최적 조합인지 저장하는 배열
    static boolean[][] selected;

    public static void main(String[] args) throws Exception {
        System.gc();
        // 메모리 측정 시작
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        K = Integer.parseInt(st.nextToken());
        N = Integer.parseInt(st.nextToken());

        // 입력 조건 예외 처리
        if(N < 1) {
            System.out.println("잘못된 입력입니다.");
            System.exit(-1);
        }
        // 아이템의 개수에 따라 무게와 가치 대입
        w = new int[K];
        v = new int[K];

        // 아이템의 무게 합을 받아옴
        int weightSum = inputFileReader();
        // 만약 무게 합이 N보다 작다면 N으로 선정(즉, 배열의 크기를 줄임)
        if(weightSum < N){
            N = weightSum;
        }
        maxValueTable = new int[K][(N / 2) + 2];
        selected = new boolean[K][(N / 2) + 2];

        // 최대 가치 계산
        System.out.println("최대 가치: " + calcMaxCargoValue(K - 1, N));

        // 선택된 아이템 추적(백트래킹)
        List<Integer> selectedItems = findSelectedItems(K - 1, N);

        outputFileWriter(selectedItems);

        // 메모리 측정 종료
        long memoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        // 메모리 사용량 출력
        System.out.println("메모리 사용량: " + (memoryAfter - memoryBefore) + " bytes");
    }
    // 최대 적재 가치를 계산하는 메소드
    static int calcMaxCargoValue(int i, int N) {
        // 가치가 없을 경우 종료
        if (i < 0) return 0;

        // 최대 가치를 테이블에 저장하여 중복 계산 방지
        if((i % 2 == 0 && N % 2 == 0) || (i % 2 == 1 && N % 2 == 1)) {
            // 테이블이 빈 경우
            if(maxValueTable[i][(N + 1) / 2] == 0) {
                // 최대로 적재하여서 담을 수 없는 경우
                if(w[i] > N) {
                    maxValueTable[i][(N + 1) / 2] = calcMaxCargoValue(i - 1, N);
                    selected[i][(N + 1) / 2] = false;
                } else {
                    // 제한 값 대입
                    int excludeValue = calcMaxCargoValue(i - 1, N);
                    // 포함 값 대입
                    int includeValue = calcMaxCargoValue(i - 1, N - w[i]) + v[i];
                    // 포함할 경우 최대값 선택
                    if(includeValue > excludeValue) {
                        maxValueTable[i][(N + 1) / 2] = includeValue;
                        selected[i][(N + 1) / 2] = true;
                    } else {
                        // 제한할 경우 최대값 선택
                        maxValueTable[i][(N + 1) / 2] = excludeValue;
                        selected[i][(N + 1) / 2] = false;
                    }
                }
            }
            return maxValueTable[i][(N + 1) / 2];
        } else {
            // 계산이 중복될떄
            // 최대로 적재하여서 담을 수 없는 경우
            if(w[i] > N) {
                // 기본 값 반환
                return calcMaxCargoValue(i - 1, N);
            } else {
                // 최대 값 반환
                return Math.max(calcMaxCargoValue(i - 1, N), calcMaxCargoValue(i - 1, N - w[i]) + v[i]);
            }
        }
    }

    // JSON 파일에서 물품의 무게와 가치를 읽어오는 메소드
    static int inputFileReader() throws Exception{
        JSONParser parser = new JSONParser();
        // JSON 파일 읽기
        FileReader reader = new FileReader(SRC_INPUT_JSON);
        int weightSum = 0;
        // JSON 객체들의 모임인 JsonArray로 파싱
        JSONArray JsonArray = (JSONArray) parser.parse(reader);
        for (Object element : JsonArray) {
            JSONObject productJsonObject = (JSONObject) element;
            // JSON에서 데이터 추출 및 배열에 저장
            Long idxLong =(Long) productJsonObject.get("idx");
            Long weightLong = (Long) productJsonObject.get("weight");
            Long valueLong = (Long) productJsonObject.get("value");

            int idx = idxLong.intValue();
            int weight = weightLong.intValue();
            int value = valueLong.intValue();

            w[idx] = weight;
            v[idx] = value;
            //N보다 작은지 확인하기 위해서 무게 합을 더함
            weightSum += weight;
        }
        return weightSum;
    }
    // 선택된 물품 정보를 JSON 파일로 출력하는 메소드
    static void outputFileWriter(List<Integer> cargoManifest) throws Exception{
        // 적재된 물품 정보 저장할 Json 배열
        JSONArray cargoJsonArray = new JSONArray();
        // 하차할 물품 정보를 저장할 Json 배열
        JSONArray offloadJsonArray = new JSONArray();
        // 적재 목록에 따라 배열 추가
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
    // JSON 배열을 파일로 저장하는 메소드
    private static void writeJsonToFile(String fileName, JSONArray srcJsonArray) throws IOException {
        // 파일 경로로 부터 쓰기 위해 선언
        FileWriter fileWriter = new FileWriter(fileName);
        // 개행 문자 추가
        fileWriter.write(srcJsonArray.toJSONString().replace("},{", "},\n{"));
        // 내부 버퍼의 내용을 파일에 쓰기
        fileWriter.flush();
    }

    // JSON 배열에 물품 객체를 추가하는 메소드
    private static void addToJsonArray(int i, JSONArray targetJsonArray) {
        // 물품 정보를 담을 JsonObject 선언
        JSONObject cargoJsonObject = new JSONObject();
        // cargoJsonObject에 추가
        cargoJsonObject.put("idx", i);
        cargoJsonObject.put("weight", w[i]);
        cargoJsonObject.put("value", v[i]);
        targetJsonArray.add(cargoJsonObject);
    }

    // 선택된 물품을 추적하여 리스트에 담는 백트래킹 메소드
    static List<Integer> findSelectedItems(int i, int N) {
        List<Integer> result = new ArrayList<>();
        while (i >= 0) {
            // 최대 가치를 테이블에 저장하여 중복 계산 방지
            if ((i % 2 == 0 && N % 2 == 0) || (i % 2 == 1 && N % 2 == 1)) {
                // 선택 여부 확인 후 무게 조정
                if (selected[i][(N + 1) / 2]) {
                    result.add(i);
                    N -= w[i];
                }
            } else {
                // 중복 계산 될때
                // 무게가 가치보다 크고 최대 적재 가치가 전 가치 보다 같지 않을 경우
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