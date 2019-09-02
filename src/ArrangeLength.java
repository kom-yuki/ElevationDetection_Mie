import java.io.*;
import java.util.ArrayList;

public class ArrangeLength {
    public static void main(String args[]) throws Exception {

        String dirString = "./NormalizedData/MendelsohnManeuver/";
        File dir = new File(dirString);
        File[] files;
        files = dir.listFiles();

        java.util.Arrays.sort(files, new java.util.Comparator<File>() {
            public int compare(File file1, File file2) {
                return file1.getName().compareTo(file2.getName());
            }
        });


        assert files != null;

        /*長さ合わせ*/
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory() && !files[i].getName().equals(".DS_Store")) {
                BufferedReader br = new BufferedReader(new FileReader(dirString + files[i].getName()));
                System.out.println(files[i].getName());

                String line;
                ArrayList<Double> data = new ArrayList<>();
                int onset = 0, offset = 0, flag = 0;
                int count = 0;

                while ((line = br.readLine()) != null) {
                    String[] cell = line.split("\t", 0);
                    data.add(Double.parseDouble(cell[0]));
                    if (flag == 0 && Integer.parseInt(cell[1]) == 1) {
                        onset = count;
                        flag = 1;
                    } else if (flag == 1 && Integer.parseInt(cell[1]) == 1) {
                        offset = count;
                        flag = 2;
                    }
                    count++;
                }
                br.close();

                ArrayList<Double> rest1 = new ArrayList<>();
                ArrayList<Double> rest2 = new ArrayList<>();
                ArrayList<Double> elevation = new ArrayList<>();
                double rest1Mean, rest1SD, rest2Mean;

                for (int n = 0; n < data.size(); n++) {
                    if (n < onset) {
                        rest1.add(data.get(n));
                    } else if (n >= onset && n <= offset) {
                        elevation.add(data.get(n));
                    } else {
                        rest2.add(data.get(n));
                    }
                }

                rest1Mean = calcMean(rest1);
                rest1SD = calcSD(rest1, rest1Mean);
                rest2Mean = calcMean(rest2);

                if (rest2Mean < rest1Mean + 2*rest1SD && rest2Mean > rest1Mean - 2*rest1SD){
                    ArrangeLength method = new ArrangeLength();
                    ArrayList<Double> rest1_new = method.scaling(rest1, 30);
                    ArrayList<Double> rest2_new = method.scaling(rest2, 30);
                    ArrayList<Double> elevation_new = method.scaling(elevation, 30);


                    /*長さ合わせたやつの出力処理*/
                    FileWriter fw2 = new FileWriter("./ArrangeLength/MendelsohnManeuver/" + files[i].getName());

                    for (double elem : rest1_new) {
                        fw2.write(String.valueOf(elem) + "\n");
                    }
                    for (double elem : elevation_new) {
                        fw2.write(String.valueOf(elem) + "\n");
                    }
                    for (double elem : rest2_new) {
                        fw2.write(String.valueOf(elem) + "\n");
                    }
                    fw2.close();
                }
            }
        }
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

    public ArrayList<Double> scaling(ArrayList<Double> data, int size){
        ArrayList<Double> output = new ArrayList<>();
        double scale = (double) size / data.size();

        for (int x = 0; x < size; x++) {
            double x0 = x / scale; //x0は拡大後座標xに対応する元座標位置
            int x1 = (int) Math.floor(x0);
            double dx = x0 - x1;
            double dat = 0;
            if (x1 + 1 < data.size()) {
                dat = data.get(x1) * (1 - dx) + data.get(x1 + 1) * dx;
            } else {
                dat = data.get(x1);
            }
            output.add(dat);
        }

        return output;
    }

    public static double calcMean(ArrayList<Double> data){
        double mean = 0;

        for (double elem : data){
            mean += elem;
        }
        return mean/data.size();
    }

    public static double calcSD(ArrayList<Double> data, double mean){
        double sd, sum=0;
        for (double elem : data){
            sum += Math.pow(elem-mean,2);
        }
        sd = Math.sqrt(sum/(data.size()-1));
        return sd;
    }
}