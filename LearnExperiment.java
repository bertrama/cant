//run multiple tests
import java.io.*;
import java.util.*;

public class LearnExperiment extends CANTExperiment {

    public static int goalsActions = 2;

    public LearnExperiment () {
        trainingLength = 0; 
        inTest = true;
    }

    private int goalEvery = 100;
    private int measureEvery = 10;
    private int successes = 0;

    private int[][][] ssA = new int[goalsActions][goalsActions][2];

    private void initMemory () {
        for (int i=0; i<ssA.length; i++) {
            for (int j=0; j<ssA[0].length; j++) {
                for (int s=0; s<ssA[0][0].length; s++) {
                    ssA[i][j][s] = 1;
                }
            }
        }
    }

    private float[] pOfGoals () {
        int[] arrayN = new int[ssA.length];
        for (int goal=0; goal<arrayN.length; goal++) {
            int count =0;
            for (int action=0; action<ssA[0].length; action++) {
                for (int s=0; s<ssA[0][0].length; s++) {
                    count = count + ssA[goal][action][s];
                }
            }
            arrayN[goal] = count;
        }
        int sum = 0;
        for (int i=0; i<arrayN.length; i++) {
            sum = sum + arrayN[i];
        }
        float[] arrayF = new float[arrayN.length];
        for (int i=0; i<arrayF.length; i++) {
            arrayF[i] = (float)arrayN[i] / (float)sum;
        }
        return (arrayF);
    }

    private float[] pOfSuccesses () {
        int[] arrayN = new int[ssA[0][0].length];
        for (int s=0; s<arrayN.length; s++) {
            int count =0;
            for (int goal=0; goal<ssA.length; goal++) {
                for (int action=0; action<ssA[0].length; action++) {
                    count = count + ssA[goal][action][s];
                }
            }
            arrayN[s] = count;
        }
        int sum = 0;
        for (int i=0; i<arrayN.length; i++) {
            sum = sum + arrayN[i];
        }
        float[] arrayF = new float[arrayN.length];
        for (int i=0; i<arrayF.length; i++) {
            arrayF[i] = (float)arrayN[i] / (float)sum;
        }
        return (arrayF);
    }

    private float[][] pOfGoalsAndSuccesses () {
        int[][] arrayN = new int[ssA.length][ssA[0][0].length];
        for (int goal=0; goal<arrayN.length; goal++) {
            for (int s=0; s<arrayN[0].length; s++) {
                int count = 0;
                for (int action=0; action<ssA[0].length; action++) {
                    count = count + ssA[goal][action][s];
                }
                arrayN[goal][s] = count;
            }
        }
        int sum = 0;
        for (int i=0; i<arrayN.length; i++) {
            for (int j=0; j<arrayN[0].length; j++) {
                sum = sum + arrayN[i][j];
            }
        }
        float[][] arrayF = new float[arrayN.length][arrayN[0].length];
        for (int i=0; i<arrayF.length; i++) {
            for (int j=0; j<arrayF[0].length; j++) {
                arrayF[i][j] = (float)arrayN[i][j] / (float)sum;
            }
        }
        return (arrayF);
    }

    private float infGoalsAndSuccesses () {
        float [] ps = pOfSuccesses();
        float [] px = pOfGoals();
        float [][] pxs = pOfGoalsAndSuccesses();
        float result = 0;
        for (int s=0; s<ps.length; s++) {
            for (int x=0; x<px.length; x++) {
                result = result + pxs[x][s]*(float)Math.log(pxs[x][s]/(px[x]*ps[s]));
            }
        }
        return (result);
    }

    private float pOfSuccess () {
        float [] ps = pOfSuccesses();
        return (ps[1]);
    }

    private float hOfSuccess () {
        float [] ps = pOfSuccesses();
        float result =0;
        for (int i=0; i<ps.length; i++) {
            result = result - ps[i]*(float)Math.log(ps[i]);
        }
        return (result);
    }

    private boolean netOn(String name, int threshold) {
        LearnNet net = (LearnNet)getNet(name);
        int neuronsFired=0;
        for (int neuron = 0; neuron < net.getSize(); neuron ++) {
            if (net.neurons[neuron].getFired()) neuronsFired++;
        }
        if (neuronsFired > threshold) {
            System.out.println(name+" is on");
            return true;
        }
        else {
            System.out.println(name+" is off");
            return false;
        }
    }

    private int activityCA(LearnNet net, int CA, int neuronsCA) {
        int neuronsFired=0;
        for (int neuron = CA*neuronsCA; neuron < (CA+1)*neuronsCA; neuron ++) {
            if (net.neurons[neuron].getFired()) neuronsFired++;
        }
        return neuronsFired;
    }

    private float activityRatio(LearnNet net, int CA, int neuronsCA) {
        return (float)activityCA(net,CA,neuronsCA)/net.getSize();
    }

    private float netActivity(String name) {
        LearnNet net = (LearnNet)getNet(name);
        return activityRatio(net,0,net.getSize());
    }

    private boolean factOn()   {return netOn("BaseNet", 50);}
    private boolean actionOn() {return netOn("ActionNet", 50);}

    private int whichCAon(LearnNet net, int neuronsCA, int threshold) {
        //        LearnNet net = (LearnNet)getNet(name);
        int numCAs = (int) (net.getSize() / neuronsCA);
        int activeCA =0;
        int neuronsFired=0;
        int lastCAFired=0;
        for (int currentCA =0; currentCA < numCAs; currentCA++) {
            neuronsFired = activityCA(net,currentCA,neuronsCA);
            if ((neuronsFired > lastCAFired) && (neuronsFired > threshold)) {
                lastCAFired = neuronsFired;
                activeCA = currentCA;
            }
        }
        if (lastCAFired > 0) return (activeCA);
        else return (99);
    }

    public void isSuccess (LearnNet factNet, int goal, int action) {
        int neurons = 0;
        LearnNet valueNet = (LearnNet)getNet("ValueNet");
        valueNet.clear();
        if (goal == action) {
            neurons = 50;
            successes++;
            updateA(goal,action,1);
            //System.out.print(", 1");
        }
        else {
            updateA(goal,action,0);
            //System.out.print(", 0");
        }
        valueNet.setCurrentPattern(0);
        valueNet.setNeuronsToStimulate(neurons);
    }

    public void switchPattern (LearnNet net, int noOfPatterns, int neurons) {
        net.clear();
        int newPattern = (int) (noOfPatterns * Math.random());
        net.setCurrentPattern(newPattern);
        net.setNeuronsToStimulate(neurons);
        //System.out.println("Pattern: "+newPattern);
    }

    public void updateA (int goal, int action, int success) {
        if ((goal < ssA.length) && (action < ssA[0].length))
            ssA[goal][action][success] = ssA[goal][action][success] + 1;
    }

    public void printMemory () {
        for (int i=0; i<ssA.length; i++) {
            for (int s=0; s<ssA[0][0].length; s++) {
                for (int j=0; j<ssA[0].length; j++) {
                    System.out.print(" "+ssA[i][j][s]);
                }
                System.out.print(" |");
            }
            System.out.println("");
        }
    }

    private void printGoals () {
        float [] ps = pOfGoals();
        for (int i=0; i<ps.length; i++) {
            System.out.print(" "+ps[i]);
        }
        System.out.println("");
    }

    private void printSuccesses () {
        float [] ps = pOfSuccesses();
        for (int i=0; i<ps.length; i++) {
            System.out.print(" "+ps[i]);
        }
        System.out.println("");
    }

    private void printGoalsAndSuccesses () {
        float [][] ps = pOfGoalsAndSuccesses();
        for (int i=0; i<ps.length; i++) {
            for (int j=0; j<ps[0].length; j++) {
                System.out.print(" "+ps[i][j]);
            }
            System.out.println("");
        }
    }

    public boolean isEndEpoch(int currentStep) {
        LearnNet factNet = (LearnNet)getNet("BaseNet");
        LearnNet actionNet = (LearnNet)getNet("ActionNet");
        LearnNet exploreNet = (LearnNet)getNet("ExploreNet");
        int goal = (int)factNet.getCurrentPattern();
        if (currentStep % measureEvery ==0) {
            int action = whichCAon(actionNet,200,50);
            System.out.print("   "+ goal+"   "+action);
            isSuccess(factNet,goal,action);
            System.out.print("   "+successes+
                             "   "+pOfSuccess()+
                             "   "+hOfSuccess()+
                             "   "+infGoalsAndSuccesses()+
                             "   "+netActivity("ExploreNet")+
                             "   "+netActivity("ValueNet"));
            System.out.print("\n");
        }
        if (currentStep % goalEvery == 0) {
            //printMemory();
            //printGoals();
            //printGoalsAndSuccesses();
            //printSuccesses();
            switchPattern(factNet,goalsActions,50);
        }
        return (false);
    }

    public void measure(int currentStep) {
        if (currentStep % measureEvery ==0) {
            System.out.print(currentStep/measureEvery);
        }
    }

    public void printExpName () {
        initMemory();
        System.out.println("# Learning Experiment");
        System.out.println("# Cy: Goal: Act: Success: Ss: P: H: I: Explore: Value: ");
    }
}
