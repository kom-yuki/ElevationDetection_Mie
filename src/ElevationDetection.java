import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class ElevationDetection {

    public static void main(String args[]) throws IOException {

        FileWriter fw_table = new FileWriter("./Result.csv");
        fw_table.write(",,,ST,,,DTW,,Difference\n");
        fw_table.write("Subject,Template,OnSet,OffSet,Time,OnSet,OffSet,Time,OnSet,OffSet,Time\n");

        //テンプレートの読み込み
        ArrayList<Double> template1 = new ArrayList<>();
        ArrayList<Double> template2 = new ArrayList<>();
        ArrayList<Double> template3 = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader("./Template/longV.dat"));
        String line;
        while((line = br.readLine()) != null){
            template1.add(Double.parseDouble(line));
        }
        br.close();

        br = new BufferedReader(new FileReader("./Template/bustub.dat"));
        while((line = br.readLine()) != null){
            template2.add(Double.parseDouble(line));
        }
        br.close();

        br = new BufferedReader(new FileReader("./Template/bustub_longV.dat"));
        while((line = br.readLine()) != null){
            template3.add(Double.parseDouble(line));
        }
        br.close();


        //正規化センサデータの取得
        String dirString = "./NormalizedData/MendelsohnManeuver/";
        File dir = new File(dirString);
        File[] files;
        files = dir.listFiles();
        java.util.Arrays.sort(files, new Comparator<File>() {
            public int compare(File file1, File file2){
                return file1.getName().compareTo(file2.getName());
            }
        });


        for (File file : files) {
            if (!file.isDirectory() && !file.getName().equals(".DS_Store")) {
                //一行ずつデータの読み込み
                BufferedReader br2 = new BufferedReader(new FileReader(dirString + file.getName()));
                FileWriter fw_result = new FileWriter("./result/result_" + file.getName().replace("dat", "csv"));
                FileWriter fw_distance = new FileWriter("./DTWDistance/DTWDistance_" + file.getName().replace("dat", "csv"));

                fw_table.write(file.getName() + ",");

                //挙上検出用準備
                String line2;
                int time = 0, usedTemplate = 0;
                int onsetST = 0, offsetST = 0, flagST = 0;
                int onset = 0, offset = 0, detectedPoint = 0;
                int flagDetection = 0;
                ArrayList<Double> data = new ArrayList<>();

                checkSwallow check1 = new checkSwallow(template1);
                checkSwallow check2 = new checkSwallow(template2);
                checkSwallow check3 = new checkSwallow(template3);


                //一行づつ読み込んで，挙上判定（リアルタイム挙上判定）
                while ((line2 = br2.readLine()) != null) {
                    String[] cell = line2.split("\t", 0);

                    //STの判定取得
                    if (flagST == 0 && Integer.parseInt(cell[1]) == 1) {
                        onsetST = time;
                        flagST = 1;
                    } else if (flagST == 1 && Integer.parseInt(cell[1]) == 1) {
                        offsetST = time;
                        flagST = 2;
                    }

                    data.add(Double.parseDouble(cell[0]));

                    //挙上判定
                    if (flagDetection == 0){
                        if (check1.checkOnset(data, time)){
                            onset = time;
                            flagDetection = 1;
                        }
                        check1.addData(data, time, 1);
                        check2.addData(data, time, 2);
                        check3.addData(data, time, 3);
                    }
                    else if (flagDetection == 1 && time >= onset + 15){
                        //select = 1はSPRINGで最適部分検出，select = 2は最初に閾値下回った部分を検出
                        if (check1.checkOffset(data, time, 1)) { //checker1は長いV字型
                            usedTemplate = 1;
                            detectedPoint = time;
                            offset = check1.getSPRING_DTW().getT_end() - 1;
                            flagDetection = 2;
                            check2.addData(data, time, 2);
                            check3.addData(data, time, 3);
                        }
                        else if (check2.checkOffset(data, time, 2) ){
                            usedTemplate = 2;
                            offset = time - 1;
                            flagDetection = 2;
                            check3.addData(data, time, 3);
                        }
                        else if (check3.checkOffset(data, time, 3)){
                            usedTemplate = 3;
                            detectedPoint = time;
                            offset = check3.getSPRING_DTW().getT_end() - 1;
                            flagDetection = 2;
                        }
                    }
                    else{
                        check1.addData(data, time, 1);
                        check2.addData(data, time, 2);
                        check3.addData(data, time, 3);
                    }
                    time++;
                }

                //DTW距離確認用
                fw_distance.write("longV,bustub,bustub_longV,,diff2\n");
                for (int t=0; t<time; t++){
                    double DTWDistance = check1.getDTW().get(t);
                    double DTWDistance2 = check2.getDTW().get(t);
                    double DTWDistance3 = check3.getDTW().get(t);
                    double smoothingDTWDiff = check2.getSPRING_DTW().getSmoothingDTWDiff().get(t);

                    if (t<onset + 15){
                        fw_distance.write(",,,\n");
                    }
                    else {
                        fw_distance.write(String.valueOf(DTWDistance) + "," + String.valueOf(DTWDistance2) + "," + String.valueOf(DTWDistance3) + ",," + String.valueOf(smoothingDTWDiff)+"\n");
                    }
                }
                fw_distance.close();


                //挙上判定結果確認用
                if (onset == 0){
                    onset = (int) Double.POSITIVE_INFINITY;
                }
                if (offset == 0){
                    offset = (int) Double.POSITIVE_INFINITY;
                }
                int count = 0;
                for (double elem : data) {
                    fw_result.write(String.valueOf(elem) + ",");
                    if (count == onsetST || count == offsetST) {
                        if (count == onset || count == offset) {
                            fw_result.write("1,1\n");
                        }
                        else if (count == detectedPoint){
                            fw_result.write("1,0.5\n");
                        }
                        else {
                            fw_result.write("1,-1\n");
                        }
                    } else {
                        if (count == onset || count == offset) {
                            fw_result.write("-1,1\n");
                        }
                        else if (count == detectedPoint && usedTemplate == 1){
                            fw_result.write("-1,0.5\n");
                        }
                        else if (count == detectedPoint && usedTemplate == 3){
                            fw_result.write("-1,0.75\n");
                        }
                        else {
                            fw_result.write("-1,-1\n");
                        }
                    }
                    count++;
                }

                //結果確認用
                fw_table.write(String.valueOf(usedTemplate) + ",");
                fw_table.write(String.valueOf(onsetST / 10.0) + "," + String.valueOf(offsetST / 10.0) + "," + String.valueOf(offsetST / 10.0 - onsetST / 10.0) + ",");
                fw_table.write(String.valueOf(onset / 10.0) + "," + String.valueOf(offset / 10.0) + "," + String.valueOf(offset / 10.0 - onset / 10.0) + ",");
                fw_table.write(String.valueOf(onset / 10.0 - onsetST / 10.0) + "," + String.valueOf(offset / 10.0 - offsetST / 10.0) + "," + String.valueOf((offset / 10.0 - onset / 10.0) - (offsetST / 10.0 - onsetST / 10.0)) + "\n");

                System.out.println(file.getName().replace(".dat", ""));
                System.out.println(String.valueOf(onsetST) + " : " + String.valueOf(offsetST));
                System.out.println(String.valueOf(onset) + " : " + String.valueOf(offset));
                System.out.println("テンプレート：" + String.valueOf(usedTemplate));
                System.out.println();

                fw_result.close();
                fw_distance.close();
            }
        }

        fw_table.close();
    }
}
