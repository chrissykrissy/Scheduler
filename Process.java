import java.util.Comparator;

class Process implements Comparable<Process>{

    //init + print
    int A;
    int B;
    int C;
    int M;
    int id;
    int quantum = 2;
    double penaltyR;

    String state; //indicates 1)running 2)blocked 3)ready 4)unstarted

    //print ea process
    int finish;
    int turnaround; //finish - A(arrival)
    int io; //blocked
    int waiting; //ready

    //calculations
    int cpuBurst; //UDRI in interval (0, B]
    int ioBurst; //preceding CPU burst * M
    int remain; // if value return randomOS(B) > totalCPU remaining, next CPUburst == remaining time
    int prevCPU; //for calculation


    public Process(int A, int B, int C, int M) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.M = M;
        this.state = "unstarted";
        this.remain = C;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Process clone(Process p){
        Process result = new Process(p.A, p.B, p.C, p.M);
        result.id = p.id;
        return result;
    }

    @Override
    public int compareTo(Process process) {
        if (this.A < process.A) {
            return -1;
        } else if (this.A == process.A) {
            return 0;
        } else {
            return 1;
        }
    }

    public int compareID(Process process) {
        if (this.id < process.id) {
            return -1;
        } else {
            return 1; //never same
        }
    }

    static class arrivalComparator implements Comparator<Process> {

        @Override
        public int compare(Process process, Process t1) {
            return process.compareTo(t1);
        }
    }

    static class IDComparator implements Comparator<Process> {

        @Override
        public int compare(Process p1, Process p2) {
            return p1.compareID(p2);
        }
    }

    static class Arrival_IDComparator implements Comparator<Process> {

        @Override
        public int compare(Process p1, Process p2) {
            if (p1.A == p2.A) {
                return p1.id < p2.id ? -1 : 1;
            }
            return p1.A < p2.A ? -1 : 1;
        }
    }

    static class Dec_Arrival_IDComparator implements Comparator<Process> {

        @Override
        public int compare(Process p1, Process p2) {
            if (p1.A == p2.A) {
                return p1.id > p2.id ? -1 : 1;
            }
            return p1.A > p2.A ? -1 : 1;
        }
    }

    static class penaltyRComparator implements Comparator<Process>{
        @Override
        public int compare(Process p1, Process p2) {
            if (p1.penaltyR == p2.penaltyR) {
                return p1.id < p2.id ? -1 : 1;
            }
            return p1.A < p2.A ? -1 : 1;
        }
    }

    public double getPenaltyR() {
        return penaltyR;
    }

    public int getA() {
        return A;
    }


    public int getId() {
        return id;
    }

}