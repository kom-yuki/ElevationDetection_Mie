import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class calcMean {
    public static void main(String args[]) throws Exception {
        //テンプレート作成
        File file_ave = new File("./Template");
        recursiveDeleteFile(file_ave);
        file_ave.mkdir();
        recursiveDeleteFile(new File("./clustering/.DS_Store"));
        File[] clustering = new File("./clustering/").listFiles();

        int clusterNum = clustering.length;

        for (int i = 0; i < clusterNum; i++) {
            String dirStringAve = "./clustering/cluster" + String.valueOf(i + 1) + "/";
            File dirAve = new File(dirStringAve);
            File[] filesAve;
            filesAve = dirAve.listFiles();

            ArrayList<ArrayList<Double>> sequences = new ArrayList<>();

            for (int j = 0; j < filesAve.length; j++) {
                if (!filesAve[j].isDirectory() && !filesAve[j].getName().equals(".DS_Store") && !filesAve[j].getName().contains(".png")) {
                    BufferedReader br = new BufferedReader(new FileReader(dirStringAve + filesAve[j].getName()));
                    ArrayList<Double> data = new ArrayList<>();
                    String line;
                    while ((line = br.readLine()) != null) {
                        data.add(Double.parseDouble(line));
                    }
                    sequences.add(data);
                    br.close();
                }
            }

            ArrayList<Double> ave = euclideanAverage(sequences);

            FileWriter fwAve;
            fwAve = new FileWriter("./clustering/cluster" + String.valueOf(i + 1) + "/mean" + String.valueOf(i+1) + ".dat");

            for (double elem : ave) {
                fwAve.write(String.valueOf(elem) + "\n");
            }
            fwAve.close();
        }
    }

    private static ArrayList<Double> euclideanAverage(ArrayList<ArrayList<Double>> sequences){
        int aveLength = sequences.get(0).size();
        double[] ave = new double[aveLength];
        ArrayList<Double> average = new ArrayList<>();

        for (ArrayList<Double> elem : sequences){
            for (int i=0; i<ave.length; i++){
                ave[i] += elem.get(i);
            }
        }

        for (int i=0; i<ave.length; i++){
            ave[i] /= sequences.size();
            average.add(ave[i]);
        }

        return average;
    }

    private static void recursiveDeleteFile(final File file) throws Exception {
        // 存在しない場合は処理終了
        if (!file.exists()) {
            return;
        }
        // 対象がディレクトリの場合は再帰処理
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                recursiveDeleteFile(child);
            }
        }
        // 対象がファイルもしくは配下が空のディレクトリの場合は削除する
        file.delete();
    }
}
