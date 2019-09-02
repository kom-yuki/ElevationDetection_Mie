import java.io.*;
import java.util.ArrayList;

public class Normalize {
    public ArrayList<Double> normalize(ArrayList<Double> data){
        ArrayList<Double> normalizeData = new ArrayList<>();
        double min=Double.MAX_VALUE, max=Double.MIN_VALUE;

        for(double elem : data){
            if(elem < min) min = elem;
            if(max < elem) max = elem;
        }

        System.out.println("min\t" + String.valueOf(min) + "\tmax\t" + String.valueOf(max));
        for (double elem : data){
            normalizeData.add((elem-min)/(max-min));
        }

        return normalizeData;
    }

    public static void main(String args[]) throws IOException {
        //String select = "forTemplate";
        String select = "MendelsohnManeuver";

        String dirString;
        if (select.equals("forTemplate")){
            dirString = "./Data/forTemplate/";
        }
        else {
            dirString = "./Data/MendelsohnManeuver/data/";
        }

        File dir = new File(dirString);
        File[] files;
        files = dir.listFiles();

        assert files != null;


        for(int i = 0; i<files.length; i++){
            if(!files[i].isDirectory() && !files[i].getName().equals(".DS_Store")){
                BufferedReader br = new BufferedReader(new FileReader(dirString + files[i].getName()));
                ArrayList<Double> data = new ArrayList<>();
                String line;
                int count=0;
                int onset = 0,offset=0,flag=0;
                int sensorPosition = 0;
                while ((line = br.readLine()) != null) {
                    String[] cell = line.split(",",0);
                    if (count == 2){
                        sensorPosition = Integer.parseInt(cell[1]);
                    }
                    if(count > 5){
                        data.add(Double.parseDouble(cell[sensorPosition-1]));
                        if(flag==0 && Integer.parseInt(cell[3])==1){
                            onset = count-8;
                            flag = 1;
                        }
                        else if(flag==1 && Integer.parseInt(cell[3])==1){
                            offset = count-8;
                            flag=2;
                        }
                    }
                    count++;
                }
                System.out.println(files[i].getName() + ":" + String.valueOf(onset) + "," + String.valueOf(offset));
                br.close();
                FileWriter fw2 = new FileWriter("./NormalizedData/" + select + "/" + files[i].getName().replace("csv","dat"));
                Normalize normalize = new Normalize();
                ArrayList<Double> normalizeData = normalize.normalize(data);

                count = 0;
                for (double elem : normalizeData){
                    fw2.write(String.valueOf(elem) + "\t");
                    if(count==onset || count==offset){
                        fw2.write("1");
                    }
                    else {
                        fw2.write("0");
                    }
                    fw2.write("\n");
                    count++;
                }
                fw2.close();
            }
        }

    }
}
