import java.util.ArrayList;

public class checkSwallow {

    private ArrayList<Double> template;
    private int sumDiff;
    double average, minDiff, maxDiff, rangeDiff;
    private ArrayList<Double> diff;
    private ArrayList<Double> smoothingDiff;
    private ArrayList<Double> dtwDistance;
    private SPRING_DTW spring_dtw;
    private int onset = (int) Double.POSITIVE_INFINITY;

    checkSwallow(ArrayList<Double> temp){
        diff = new ArrayList<>();
        smoothingDiff = new ArrayList<>();
        template = temp;
        spring_dtw = new SPRING_DTW(template);
        dtwDistance = new ArrayList<>();
    }


    boolean checkOnset(ArrayList<Double> data, int time){
        if(time == 0){
            diff.add(0d);
            smoothingDiff.add(0d);
        } else if (time>5) {
            diff.add(Math.abs(data.get(time) - data.get(time-1)));
            smoothingDiff.add((diff.get(time)+diff.get(time-1)+diff.get(time-2)+diff.get(time-3)+diff.get(time-4))/5);
        } else {
            diff.add(Math.abs(data.get(time) - data.get(time-1)));
            smoothingDiff.add(0d);
        }

        /*最初の安静時は3.5秒間*/
        if(time<35){
            sumDiff += smoothingDiff.get(time);
        }
        else if(time==35){
            average = sumDiff/35;
            for (int j=0;j<35;j++){
                if(minDiff > smoothingDiff.get(j)){
                    minDiff = smoothingDiff.get(j);
                }
                if(maxDiff < smoothingDiff.get(j)){
                    maxDiff = smoothingDiff.get(j);
                }
            }
            rangeDiff = maxDiff - minDiff;
        }
        else {
            if (smoothingDiff.get(time) > average + 1.5*rangeDiff) {
                onset = time;
                return true;
            }
        }
        return false;
    }

    boolean checkOffset(ArrayList<Double> data, int time, int select){
        boolean detection;

        if (select == 1){
            detection =  spring_dtw.calcLongV(data.get(time), time + 1, 3.0, onset+1); //長いV字型
        }
        else if(select == 2) {
            detection = spring_dtw.calcBustub(data.get(time), time + 1, 6.0, onset+1); //バスタブ型
        }
        else {
            detection = spring_dtw.calcLongV(data.get(time), time + 1, 4.0, onset+1); //間の形
        }
        dtwDistance.add(spring_dtw.getDistance().get(time + 1).get(template.size()));

        return detection;
    }

    void addData(ArrayList<Double> data, int time, int select, int on){

        if (onset == (int) Double.POSITIVE_INFINITY){
            onset = on;
        }

        if (select == 1){
            spring_dtw.addDataLongV(data.get(time), time+1, onset+1);
        }
        else if (select == 2){
            spring_dtw.addDataBustub(data.get(time), time+1, onset+1);
        }
        else {
            spring_dtw.addDataLongV(data.get(time), time+1, onset+1);
        }

        dtwDistance.add(spring_dtw.getDistance().get(time+1).get(template.size()));
    }

    ArrayList<Double> getDTW(){
        return dtwDistance;
    }

    SPRING_DTW getSPRING_DTW(){
        return spring_dtw;
    }

}
