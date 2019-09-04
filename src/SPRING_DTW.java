import java.util.ArrayList;
import java.util.Collections;

class SPRING_DTW {

    private ArrayList<Double> streamingData = new ArrayList<>();
    private ArrayList<ArrayList<Double>> distance = new ArrayList<>();
    private ArrayList<Double> DTWdiff = new ArrayList<>();
    private ArrayList<Double> smoothingDTWDiff = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> start = new ArrayList<>();
    private ArrayList<Double> template;
    private int t_start, t_end;
    private double d_min;

    public static final int TempOnSet=5;
    public static final int TempOffSet=25;

    SPRING_DTW(ArrayList<Double> temp){
        template = temp;

        ArrayList<Double> distanceCol = new ArrayList<>();
        ArrayList<Integer> startCol = new ArrayList<>();
        for (int i=0; i<=template.size(); i++){
            if (i==0){
                distanceCol.add(Double.POSITIVE_INFINITY);
                startCol.add(0);
            }
            else {
                distanceCol.add(Double.POSITIVE_INFINITY);
                startCol.add(0);
            }
        }
        distance.add(distanceCol);
        start.add(startCol);
        d_min = Double.POSITIVE_INFINITY;
    }

    boolean calcBustub(double newValue, int t, double e, int onset){
        streamingData.add(newValue);
        ArrayList<Double> distanceCol = new ArrayList<>();
        ////distanceCol.add(0.0); //入力データは始端点フリー
        if (t == onset){
            distanceCol.add(0.0);
        }
        else {
            distanceCol.add(Double.POSITIVE_INFINITY); //入力データは始端点固定
        }
        distance.add(distanceCol);

        ArrayList<Integer> startCol = new ArrayList<>();
        startCol.add(t);
        start.add(startCol);

        for (int i=1; i<=template.size(); i++){
            double d_best;
            double di;
            int si;
            double A,B,C;

            if (i>=TempOnSet && i<TempOffSet){
                //傾斜制限かける
                A = distance.get(t-1).get(i); //横から
                B = distance.get(t-1).get(i-1); //傾き1の斜めから
                if (i >= 2){
                    C = distance.get(t-1).get(i-2); //傾き2の斜めから
                }
                else {
                    C = Double.POSITIVE_INFINITY;
                }

                //最小値を選択
                if(A <= B && A <= C){
                    d_best = A;
                    si = start.get(t-1).get(i);
                }
                else if (B <= C){
                    d_best = B;
                    si = start.get(t-1).get(i-1);
                }
                else {
                    d_best = C;
                    si = start.get(t-1).get(i-2);
                }
            }
            else {
                //傾斜制限無し
                A = distance.get(t).get(i - 1); //di-1，下から
                B = distance.get(t - 1).get(i); //d'i，横から
                C = distance.get(t - 1).get(i - 1); //d'i-1，斜めから

                //最小値を選択
                if(A <= B && A <= C){
                    d_best = A;
                    si = start.get(t).get(i-1);
                }
                else if (B <= C){
                    d_best = B;
                    si = start.get(t-1).get(i);
                }
                else {
                    d_best = C;
                    si = start.get(t-1).get(i-1);
                }
            }

            start.get(t).add(si);

            di = Math.pow(newValue-template.get(i-1), 2) + d_best;
            distance.get(t).add(di);
        }

        addDiff(t);
        addSmoothingDTWDiff(t);

        //if (distance.get(t).get(template.size()) <= e && smoothingDTWDiff.get(t-1) < -5.2) {
        if (distance.get(t).get(template.size()) <= e && distance.get(t).get(template.size())-distance.get(t-5).get(template.size()) < -5) {
            t_start = start.get(t).get(template.size());
            t_end = t;

            System.out.println("出力時間：" + t);
            System.out.println("DTW距離：" + distance.get(t).get(template.size()));
            System.out.println("開始点：" + t_start);
            System.out.println("終了点：" + t_end);

            //Reset the array of d_i
            /*
            for (int i = 1; i <= template.size(); i++) {
                distance.get(t).remove(i);
                distance.get(t).add(i, Double.POSITIVE_INFINITY);
            }
            */
            return true;
        }

        return false;
    }

    boolean calcLongV(double newValue, int t, double e, int onset){
        streamingData.add(newValue);
        ArrayList<Double> distanceCol = new ArrayList<>();
        //distanceCol.add(0.0); //入力データは始端点フリー
        if (t == onset){
            distanceCol.add(0.0);
        }
        else {
            distanceCol.add(Double.POSITIVE_INFINITY); //入力データは始端点固定
        }

        distance.add(distanceCol);

        ArrayList<Integer> startCol = new ArrayList<>();
        startCol.add(t);
        start.add(startCol);

        for (int i=1; i<=template.size(); i++){
            double d_best;
            double di;
            int si;
            double A,B,C;

            if (i>=TempOnSet && i<TempOffSet){
                //傾斜制限かける
                A = distance.get(t).get(i-1); //下から
                B = distance.get(t-1).get(i-1); //傾き1の斜めから
                C = distance.get(t-2).get(i-1); //傾き1/2の斜めから

                //最小値を選択
                if(A <= B && A <= C){
                    d_best = A;
                    si = start.get(t).get(i-1);
                }
                else if (B <= C){
                    d_best = B;
                    si = start.get(t-1).get(i-1);
                }
                else {
                    d_best = C;
                    si = start.get(t-2).get(i-1);
                }
            }
            else {
                //傾斜制限無し
                A = distance.get(t).get(i - 1); //di-1，下から
                B = distance.get(t - 1).get(i); //d'i，横から
                C = distance.get(t - 1).get(i - 1); //d'i-1，斜めから

                //最小値を選択
                if(A <= B && A <= C){
                    d_best = A;
                    si = start.get(t).get(i-1);
                }
                else if (B <= C){
                    d_best = B;
                    si = start.get(t-1).get(i);
                }
                else {
                    d_best = C;
                    si = start.get(t-1).get(i-1);
                }
            }

            start.get(t).add(si);

            di = Math.pow(newValue-template.get(i-1), 2) + d_best;
            distance.get(t).add(di);
        }

        if (d_min <= e){
            ArrayList<Double> temp = new ArrayList<>(distance.get(t));
            ArrayList<Integer> temp2 = new ArrayList<>(start.get(t));

            temp.remove(0);
            temp.remove(0);
            temp2.remove(0);
            temp2.remove(0);

            Collections.sort(temp); //diの最小値を先頭に
            Collections.sort(temp2); //Siの最小値を先頭に

            if (temp.get(0) >= d_min || temp2.get(0) > t_end){

                System.out.println("出力時間：" + t);
                System.out.println("DTW距離：" + d_min);
                System.out.println("開始点：" + t_start);
                System.out.println("終了点：" + t_end);

                return true;
            }
        }

        if (distance.get(t).get(template.size()) <= e && distance.get(t).get(template.size()) < d_min){
            d_min = distance.get(t).get(template.size());
            t_start = start.get(t).get(template.size());
            t_end = t;
        }

        return false;
    }


    void addDataBustub(double newValue, int t, int onset){
        streamingData.add(newValue);
        ArrayList<Double> distanceCol = new ArrayList<>();
        //distanceCol.add(0.0); //入力データは始端点フリー
        if (t == onset){
            distanceCol.add(0.0);
        }
        else {
            distanceCol.add(Double.POSITIVE_INFINITY); //入力データは始端点固定
        }

        distance.add(distanceCol);

        ArrayList<Integer> startCol = new ArrayList<>();
        startCol.add(t);
        start.add(startCol);

        for (int i=1; i<=template.size(); i++){
            double d_best;
            double di;
            int si;
            double A,B,C;

            if (i>=TempOnSet && i<TempOffSet){
                //傾斜制限かける
                A = distance.get(t-1).get(i); //横から
                B = distance.get(t-1).get(i-1); //傾き1の斜めから
                if (i>=2){
                    C = distance.get(t-1).get(i-2); //傾き2の斜めから
                }
                else {
                    C = Double.POSITIVE_INFINITY;
                }

                //最小値を選択
                if(A <= B && A <= C){
                    d_best = A;
                    si = start.get(t-1).get(i);
                }
                else if (B <= C){
                    d_best = B;
                    si = start.get(t-1).get(i-1);
                }
                else {
                    d_best = C;
                    si = start.get(t-1).get(i-2);
                }
            }
            else {
                //傾斜制限無し
                A = distance.get(t).get(i - 1); //di-1，下から
                B = distance.get(t - 1).get(i); //d'i，横から
                C = distance.get(t - 1).get(i - 1); //d'i-1，斜めから

                //最小値を選択
                if(A <= B && A <= C){
                    d_best = A;
                    si = start.get(t).get(i-1);
                }
                else if (B <= C){
                    d_best = B;
                    si = start.get(t-1).get(i);
                }
                else {
                    d_best = C;
                    si = start.get(t-1).get(i-1);
                }
            }

            start.get(t).add(si);

            di = Math.pow(newValue-template.get(i-1), 2) + d_best;
            distance.get(t).add(di);
        }
        addDiff(t);
        addSmoothingDTWDiff(t);
    }

    void addDataLongV(double newValue, int t, int onset){
        streamingData.add(newValue);
        ArrayList<Double> distanceCol = new ArrayList<>();
        //distanceCol.add(0.0); //入力データは始端点フリー
        if (t == onset){
            distanceCol.add(0.0);
        }
        else {
            distanceCol.add(Double.POSITIVE_INFINITY); //入力データは始端点固定
        }

        distance.add(distanceCol);

        ArrayList<Integer> startCol = new ArrayList<>();
        startCol.add(t);
        start.add(startCol);

        for (int i=1; i<=template.size(); i++) {
            double d_best;
            double di;
            int si;
            double A, B, C;

            //傾斜制限無し
            A = distance.get(t).get(i - 1); //di-1，下から
            B = distance.get(t - 1).get(i); //d'i，横から
            C = distance.get(t - 1).get(i - 1); //d'i-1，斜めから

            //最小値を選択
            if (A <= B && A <= C) {
                d_best = A;
                si = start.get(t).get(i - 1);
            } else if (B <= C) {
                d_best = B;
                si = start.get(t - 1).get(i);
            } else {
                d_best = C;
                si = start.get(t - 1).get(i - 1);

            }

            start.get(t).add(si);

            di = Math.pow(newValue - template.get(i - 1), 2) + d_best;
            distance.get(t).add(di);
        }
    }

    void addDiff(int time){
        if (time > 10){
            double mean = (distance.get(time-1).get(template.size()) + distance.get(time-2).get(template.size()) + distance.get(time-3).get(template.size())
                    + distance.get(time-4).get(template.size()) + distance.get(time-5).get(template.size())
                    + distance.get(time-6).get(template.size()) + distance.get(time-7).get(template.size()) + distance.get(time-8).get(template.size())
                    + distance.get(time-9).get(template.size()) + distance.get(time-10).get(template.size()) )/10;
            DTWdiff.add(distance.get(time).get(template.size()) - mean);
        }
        else if (time > 0){
            DTWdiff.add(distance.get(time).get(template.size()) - distance.get(time-1).get(template.size()));
        }
        else {
            DTWdiff.add(0.0);
        }
    }

    void addSmoothingDTWDiff(int time){
        /*
        if (time >= 5){
            smoothingDTWDiff.add((DTWdiff.get(time-1) + DTWdiff.get(time-2) + DTWdiff.get(time-3) + DTWdiff.get(time-4) + DTWdiff.get(time-5)) / 5);
        }
        else {
            smoothingDTWDiff.add(0.0);
        }
        */
        smoothingDTWDiff.add(DTWdiff.get(time-1));
    }

    double getMean(ArrayList<Double> list, int time){
        if (time >= 5){
            return (list.get(time-1) + list.get(time-2) + list.get(time-3) + list.get(time-4) + list.get(time-5))/5;
        }
        else {
            return list.get(time);
        }
    }

    ArrayList<ArrayList<Double>> getDistance(){
        return distance;
    }

    int getT_end(){
        return t_end;
    }

    ArrayList<Double> getSmoothingDTWDiff(){
        return smoothingDTWDiff;
    }
}
