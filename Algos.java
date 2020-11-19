import java.io.FileNotFoundException;
import java.util.*;

public class Algos extends lab2{
//    Queue<Process> ready; // = new LinkedList<>();
//    Queue<Process> blocked; // = new LinkedList<>();
//    Queue<Process> unstarted; // = new LinkedList<>();


    void FCFS(ArrayList<Process> sorted) throws FileNotFoundException {

        int terminatedNum = 0;
        int blockedCycleNum = 0;
        Process curr; //current job

        ArrayList<Process> FCFSprocesses = new ArrayList<>();
        Queue<Process> unstarted = new LinkedList<>();
        Queue<Process> ready = new LinkedList<>();
        Queue<Process> blocked = new LinkedList<>();
        PriorityQueue<Process> ordering = new PriorityQueue<>(new Process.IDComparator());

        int cycle = 0;
        int prevCPUburst = 0;
        curr = null;
        random = 0;

        FCFSprocesses.addAll(sorted);

        if (verbose){
            System.out.println("\nThis detailed printout gives the state and remaining burst for each process");
            System.out.printf("\n%s%2d%s","Before Cycle\t",cycle,":");
            for (int i = 0; i < processNum; i++){
                System.out.printf("\t%11s %d",FCFSprocesses.get(i).state, 0);
            }
        }

        cycle++;

        for (int i = 0; i < processNum; i++){
            if (FCFSprocesses.get(i).A == 0){
                FCFSprocesses.get(i).state = "ready";
                ready.add(FCFSprocesses.get(i));
            }else{
                unstarted.add(FCFSprocesses.get(i));
            }
        }

        label:
        while (terminatedNum != processNum){

            if (!blocked.isEmpty()) {
                for (Process p : FCFSprocesses) {
                    if (p.state.equals("blocked")) {
                        p.ioBurst--;
                        if (p.ioBurst == 0) {
                            p.state = "ready";
                            ordering.add(p);
                        }
                    }
                }
                ready.addAll(ordering);
                ordering.clear();
            }

            while (!unstarted.isEmpty() && unstarted.peek().A + 1 == cycle){
                Process popped = unstarted.poll();
                popped.state = "ready";
                ready.add(popped);
            }

            if (curr == null){
                if (ready.peek() != null) {
                    curr = ready.poll();
                    curr.state = "running";
                    prevCPUburst = Math.min(curr.remain, randomOS(curr.B));
                    curr.cpuBurst = prevCPUburst;
                    random++;
                }
            }else{
                curr.cpuBurst--;
                curr.remain--;
                if (curr.cpuBurst == 0) {
                    switch (curr.remain) {
                        case 0:
                            curr.state = "terminated";
                            curr.finish = cycle - 1;
                            curr.turnaround = curr.finish - curr.A;
                            terminatedNum++;
                            if (terminatedNum == processNum) {
                                break label;
                            }
                            curr = null;
                            break;
                        default:
                            curr.state = "blocked";
                            curr.ioBurst = prevCPUburst * curr.M;
                            curr.io += curr.ioBurst;
                            blocked.add(curr);
                            curr = null;
                            break;
                    }

                    if (ready.peek() != null) {
                        curr = ready.poll();
                        //                    if (!curr.state.equals("blocked")) {
                        curr.state = "running";
                        //                    }
                        prevCPUburst = Math.min(curr.remain, randomOS(curr.B));
                        curr.cpuBurst = prevCPUburst;
                        random++;
                    }
                }
            }

            if (verbose){
                System.out.printf("\n%s%2d%s","Before Cycle\t",cycle,":");
                for(int i = 0; i < processNum; i++){
                    if (FCFSprocesses.get(i).state.equals("running")){
                        System.out.printf("\t%11s %d",FCFSprocesses.get(i).state,FCFSprocesses.get(i).cpuBurst);
                    }else if (FCFSprocesses.get(i).state.equals("terminated")) {
                        System.out.printf("\t%11s %d", FCFSprocesses.get(i).state, FCFSprocesses.get(i).cpuBurst);
                    }else if (FCFSprocesses.get(i).state.equals("ready")){
                        System.out.printf("\t%11s %d", FCFSprocesses.get(i).state, 0);
                    }else{
                        System.out.printf("\t%11s %d",FCFSprocesses.get(i).state,FCFSprocesses.get(i).ioBurst);
                    }
                }
            }

            boolean IO = false;
            for (Process p : FCFSprocesses){
                if (p.state.equals("blocked")){
                    IO = true;
                }if (p.state.equals("ready")){
                    p.waiting++;
                }
            }

            if (IO){
                blockedCycleNum++;
            }

            cycle++;

        }

        System.out.println("\nThe scheduling algorithm used was First Come First Served\n");

        int finishTime = 0;
        int totalCPU = 0;
        int totalTurn = 0;
        int totalWait = 0;

        for (Process p : FCFSprocesses){
            System.out.println("Process "+p.id+":");
            System.out.println("\t\t(A,B,C,M) = ("+p.A+","+p.B+","+p.C+","+p.M+")");
            totalCPU+=p.C;
            System.out.println("\t\tFinishing time: "+p.finish);
            if (p.finish > finishTime){
                finishTime = p.finish;
            }
            System.out.println("\t\tTurnaround time: "+p.turnaround);
            totalTurn+=p.turnaround;
            System.out.println("\t\tI/O time: " + p.io);
            System.out.println("\t\tWaiting time: "+p.waiting);
            totalWait += p.waiting;
            System.out.println();

        }

        System.out.println("Summary Data: ");
        System.out.println("\t\tFinishing time: "+finishTime);
        System.out.println("\t\tCPU Utilization: "+(double)totalCPU/finishTime);
        System.out.println("\t\tI/O Utilization: "+(double) blockedCycleNum/finishTime);
        System.out.println("\t\tThroughput: "+(double)(100*processNum)/finishTime + " processes per hundred cycles");
        System.out.println("\t\tAverage turnaround time: "+(double)totalTurn/processNum);
        System.out.println("\t\tAverage waiting time: "+(double)totalWait/processNum);


        FCFSprocesses.clear();
    }//end of FCFS

    void RR(ArrayList<Process> sorted) throws FileNotFoundException {
//        printing p1 = new printing();
//        p1.printP();

        int terminatedNum = 0;
        int blockedCycleNum = 0;
        Process curr; //current job

        ArrayList<Process> RRprocesses = new ArrayList<>();
        Queue<Process> unstarted = new LinkedList<>();
        Queue<Process> ready = new LinkedList<>();
        Queue<Process> blocked = new LinkedList<>();
        ArrayList<Process> ordering = new ArrayList<>();
        int cycle = 0;
        curr = null;
        random = 0;

        RRprocesses.addAll(sorted);

        if (verbose){
            System.out.println("\nThis detailed printout gives the state and remaining burst for each process");
            System.out.printf("\n%s%2d%s","Before Cycle\t",cycle,":");
            for (int i = 0; i < processNum; i++){
                System.out.printf("\t%11s %d",RRprocesses.get(i).state, 0);
            }
        }

        cycle++;

        for (int i = 0; i < processNum; i++){
            if (RRprocesses.get(i).A == 0){
                RRprocesses.get(i).state = "ready";
                ready.add(RRprocesses.get(i));
            }else{
                unstarted.add(RRprocesses.get(i));
            }
        }

//        Collections.sort(ordering, new Process.Arrival_IDComparator());
//        ready.addAll(ordering);
//        ordering.clear();

        label:
        while (terminatedNum != processNum){

            if (!blocked.isEmpty()) {
                for (Process p : RRprocesses) {
                    if (p.state.equals("blocked")) {
                        p.ioBurst--;
                        if (p.ioBurst == 0) {
                            p.state = "ready";
                            ready.add(p);
//                            ordering.add(p);
                            blocked.remove(p);
                        }
                    }
                }
            }


            while (!unstarted.isEmpty() && unstarted.peek().A + 1 == cycle){
                Process popped = unstarted.poll();
                popped.state = "ready";
                ready.add(popped);
//                ordering.add(popped);
            }

//            Collections.sort(ordering, new Process.Arrival_IDComparator());
//            ready.addAll(ordering);
//            ordering.clear();

            if (curr != null) {
                curr.cpuBurst--;
                curr.remain--;
                curr.quantum--;
//                if (curr.cpuBurst == 0) {
                if (curr.remain == 0) {
                    curr.state = "terminated";
                    curr.finish = cycle - 1;
                    curr.turnaround = curr.finish - curr.A;
                    terminatedNum++;
                    if (terminatedNum == processNum) {
                        break label;
                    }
                    curr = null;
                    if (ready.peek() != null) {
                        curr = ready.poll();
                        curr.state = "running";
                        curr.quantum = 2;
                        if (curr.cpuBurst <= 0){
                            curr.prevCPU = Math.min(curr.remain, randomOS(curr.B));
                            curr.cpuBurst = curr.prevCPU;
                            random++;
//                        curr.cpuBurst = Math.min(curr.remain, randomOS(curr.B));
                        }
                    }
                } else if (curr.cpuBurst == 0) {
                    curr.state = "blocked";
                    curr.ioBurst = curr.prevCPU * curr.M;
                    curr.io += curr.ioBurst;
                    blocked.add(curr);
                    curr = null;
//                    Collections.sort(ordering, new Process.Arrival_IDComparator());
//                    ready.addAll(ordering);
//                    ordering.clear();
                    if (ready.peek() != null) {
                        curr = ready.poll();
                        curr.state = "running";
                        curr.quantum = 2;
                        if (curr.cpuBurst <= 0){
                            curr.prevCPU = Math.min(curr.remain, randomOS(curr.B));
                            curr.cpuBurst = curr.prevCPU;
                            random++;
                        }
                    }
                } else if (curr.quantum == 0) {
                    curr.state = "ready";
                    ready.add(curr);
//                    ordering.add(curr);
//                    Collections.sort(ordering, new Process.Arrival_IDComparator());
//                    ready.addAll(ordering);
//                    ordering.clear();
                    if (ready.peek() != null) {
                        curr = ready.poll();
                        curr.state = "running";
                        curr.quantum = 2;
                        if (curr.cpuBurst <= 0){
                            curr.prevCPU = Math.min(curr.remain, randomOS(curr.B));
                            curr.cpuBurst = curr.prevCPU;
                            random++;
//                        curr.cpuBurst = Math.min(curr.remain, randomOS(curr.B));
                        }
                    }
//                    ready.add()
//                    Collections.sort(ordering, new Process.Arrival_IDComparator());
//                    ready.addAll(ordering);
//                    ordering.clear();
                } else {
                    if (!curr.state.equals("running") && ready.peek() != null) {
                        curr = ready.poll();
                        curr.state = "running";
                        curr.quantum = 2;
                        if (curr.cpuBurst == 0) {
                            curr.prevCPU = Math.min(curr.remain, randomOS(curr.B));
                            curr.cpuBurst = curr.prevCPU;
                            random++;
                        }
                    }
                }

//                Collections.sort(ordering, new Process.Arrival_IDComparator());
//                ready.addAll(ordering);
//                ordering.clear();
            }else {
//                Collections.sort(ordering, new Process.Arrival_IDComparator());
//                ready.addAll(ordering);
//                ordering.clear();
                if (ready.peek() != null) {
                    curr = ready.poll();
                    curr.state = "running";
                    curr.quantum = 2;
                    if (curr.cpuBurst <= 0){
                        curr.prevCPU = Math.min(curr.remain, randomOS(curr.B));
                        curr.cpuBurst = curr.prevCPU;
                        random++;
//                        curr.cpuBurst = Math.min(curr.remain, randomOS(curr.B));
                    }
                }
            }


            if (verbose){
                System.out.printf("\n%s%2d%s","Before Cycle\t",cycle,":");
                for(int i = 0; i < processNum; i++){
                    if (RRprocesses.get(i).state.equals("running")){
                        System.out.printf("\t%11s %d",RRprocesses.get(i).state,Math.min(RRprocesses.get(i).quantum, RRprocesses.get(i).cpuBurst));
                    }else if (RRprocesses.get(i).state.equals("terminated")) {
                        System.out.printf("\t%11s %d", RRprocesses.get(i).state, RRprocesses.get(i).cpuBurst);
                    }else if (RRprocesses.get(i).state.equals("ready")){
                        System.out.printf("\t%11s %d", RRprocesses.get(i).state, 0);
                    }else{
                        System.out.printf("\t%11s %d",RRprocesses.get(i).state,RRprocesses.get(i).ioBurst);
                    }
                }
            }

            boolean IO = false;
            for (Process p : RRprocesses){
                if (p.state.equals("blocked")){
                    IO = true;
                }if (p.state.equals("ready")){
                    p.waiting++;
                }
            }

            if (IO){
                blockedCycleNum++;
            }

            cycle++;

        }

        System.out.println("\nThe scheduling algorithm used was Round Robbin\n");

        int finishTime = 0;
        int totalCPU = 0;
        int totalTurn = 0;
        int totalWait = 0;

        for (Process p : RRprocesses){
            System.out.println("Process "+p.id+":");
            System.out.println("\t\t(A,B,C,M) = ("+p.A+","+p.B+","+p.C+","+p.M+")");
            totalCPU+=p.C;
            System.out.println("\t\tFinishing time: "+p.finish);
            if (p.finish > finishTime){
                finishTime = p.finish;
            }
            System.out.println("\t\tTurnaround time: "+p.turnaround);
            totalTurn+=p.turnaround;
            System.out.println("\t\tI/O time: " + p.io);
            System.out.println("\t\tWaiting time: "+p.waiting);
            totalWait += p.waiting;
            System.out.println();

        }

        System.out.println("Summary Data: ");
        System.out.println("\t\tFinishing time: "+finishTime);
        System.out.println("\t\tCPU Utilization: "+(double)totalCPU/finishTime);
        System.out.println("\t\tI/O Utilization: "+(double) blockedCycleNum/finishTime);
        System.out.println("\t\tThroughput: "+(double)(100*processNum)/finishTime + " processes per hundred cycles");
        System.out.println("\t\tAverage turnaround time: "+(double)totalTurn/processNum);
        System.out.println("\t\tAverage waiting time: "+(double)totalWait/processNum);

        RRprocesses.clear();
    }//end of RR

    void LCFS(ArrayList<Process> sorted) throws FileNotFoundException {
//        printing p1 = new printing();
//        p1.printP();

        int terminatedNum = 0;
        int blockedCycleNum = 0;
        Process curr; //current job

        ArrayList<Process> LCFSprocesses = new ArrayList<>();
        Queue<Process> unstarted = new LinkedList<>();
        Queue<Process> ready = new LinkedList<>();
        Stack<Process> readyStack = new Stack<>();
        Queue<Process> blocked = new LinkedList<>();
        ArrayList<Process> ordering = new ArrayList<>();
        int cycle = 0;
//        int prevCPUburst = 0;
        curr = null;
        random = 0;

        LCFSprocesses.addAll(sorted);

//        Collections.sort(processes, new Process.Dec_Arrival_IDComparator());

        if (verbose){
            System.out.println("\nThis detailed printout gives the state and remaining burst for each process");
            System.out.printf("\n%s%2d%s","Before Cycle\t",cycle,":");
            for (int i = 0; i < processNum; i++){
                System.out.printf("\t%11s %d",LCFSprocesses.get(i).state, 0);
            }
        }

        cycle++;

        for (int i = 0; i < processNum; i++){
            if (LCFSprocesses.get(i).A == 0){
                LCFSprocesses.get(i).state = "ready";
                ordering.add(LCFSprocesses.get(i));
//                readyStack.push(LCFSprocesses.get(i));
            }else{
                unstarted.add(LCFSprocesses.get(i));
            }
        }

        Collections.sort(ordering, new Process.Dec_Arrival_IDComparator());
        readyStack.addAll(ordering);
        ordering.clear();

        label:
        while (terminatedNum != processNum){

            if (!blocked.isEmpty()) {
                for (Process p : LCFSprocesses) {
                    if (p.state.equals("blocked")) {
                        p.ioBurst--;
                        if (p.ioBurst == 0) {
                            p.state = "ready";
//                            readyStack.add(p);
                            ordering.add(p);
                            blocked.remove(p);
                        }
                    }
                }
//                Collections.sort(ordering, new Process.Dec_Arrival_IDComparator());
//                readyStack.addAll(ordering);
//                ordering.clear();
            }

            while (!unstarted.isEmpty() && unstarted.peek().A + 1 == cycle){
                Process popped = unstarted.poll();
                popped.state = "ready";
//                readyStack.add(popped);
                ordering.add(popped);
            }

            Collections.sort(ordering, new Process.Dec_Arrival_IDComparator());
            readyStack.addAll(ordering);
            ordering.clear();

            if (curr == null){
                if (!readyStack.isEmpty()) {
                    if (readyStack.peek() != null) {
                        curr = readyStack.pop();
                        curr.state = "running";
                        curr.prevCPU = Math.min(curr.remain, randomOS(curr.B));
                        curr.cpuBurst = curr.prevCPU;
                        random++;
                    }
                }
            }else {
                curr.cpuBurst--;
                curr.remain--;
                if (curr.cpuBurst == 0) {
                    switch (curr.remain) {
                        case 0:
                            curr.state = "terminated";
                            curr.finish = cycle - 1;
                            curr.turnaround = curr.finish - curr.A;
                            terminatedNum++;
                            if (terminatedNum == processNum) {
                                break label;
                            }
                            curr = null;
                            break;
                        default:
                            curr.state = "blocked";
                            curr.ioBurst = curr.prevCPU * curr.M;
                            curr.io += curr.ioBurst;
                            blocked.add(curr);
                            curr = null;
                            break;
                    }

                    if (!readyStack.isEmpty()) {

                        if (readyStack.peek() != null) {
                            curr = readyStack.pop();
                            //                    if (!curr.state.equals("blocked")) {
                            curr.state = "running";
                            //                    }
                            curr.prevCPU = Math.min(curr.remain, randomOS(curr.B));
                            curr.cpuBurst = curr.prevCPU;
                            random++;
                        }
                    }
                }
            }

            if (verbose){
                System.out.printf("\n%s%2d%s","Before Cycle\t",cycle,":");
                for(int i = 0; i < processNum; i++){
                    if (LCFSprocesses.get(i).state.equals("running")){
                        System.out.printf("\t%11s %d",LCFSprocesses.get(i).state,LCFSprocesses.get(i).cpuBurst);
                    }else if (LCFSprocesses.get(i).state.equals("terminated")) {
                        System.out.printf("\t%11s %d", LCFSprocesses.get(i).state, LCFSprocesses.get(i).cpuBurst);
                    }else if (LCFSprocesses.get(i).state.equals("ready")){
                        System.out.printf("\t%11s %d", LCFSprocesses.get(i).state, 0);
                    }else{
                        System.out.printf("\t%11s %d",LCFSprocesses.get(i).state,LCFSprocesses.get(i).ioBurst);
                    }
                }
            }

            boolean IO = false;
            for (Process p : LCFSprocesses){
                if (p.state.equals("blocked")){
                    IO = true;
                }if (p.state.equals("ready")){
                    p.waiting++;
                }
            }

            if (IO){
                blockedCycleNum++;
            }

            cycle++;

        }

        System.out.println("\nThe scheduling algorithm used was Last Come First Served\n");

        int finishTime = 0;
        int totalCPU = 0;
        int totalTurn = 0;
        int totalWait = 0;

        for (Process p : LCFSprocesses){
            System.out.println("Process "+p.id+":");
            System.out.println("\t\t(A,B,C,M) = ("+p.A+","+p.B+","+p.C+","+p.M+")");
            totalCPU+=p.C;
            System.out.println("\t\tFinishing time: "+p.finish);
            if (p.finish > finishTime){
                finishTime = p.finish;
            }
            System.out.println("\t\tTurnaround time: "+p.turnaround);
            totalTurn+=p.turnaround;
            System.out.println("\t\tI/O time: " + p.io);
            System.out.println("\t\tWaiting time: "+p.waiting);
            totalWait += p.waiting;
            System.out.println();

        }

        System.out.println("Summary Data: ");
        System.out.println("\t\tFinishing time: "+finishTime);
        System.out.println("\t\tCPU Utilization: "+(double)totalCPU/finishTime);
        System.out.println("\t\tI/O Utilization: "+(double) blockedCycleNum/finishTime);
        System.out.println("\t\tThroughput: "+(double)(100*processNum)/finishTime + " processes per hundred cycles");
        System.out.println("\t\tAverage turnaround time: "+(double)totalTurn/processNum);
        System.out.println("\t\tAverage waiting time: "+(double)totalWait/processNum);


        LCFSprocesses.clear();
    }//end of LCFS

    void HPRN(ArrayList<Process> sorted) throws FileNotFoundException {
//        printing p1 = new printing();
//        p1.printP();

        int terminatedNum = 0;
        int blockedCycleNum = 0;
        Process curr; //current job

        ArrayList<Process> HPRNprocesses = new ArrayList<>();
        Queue<Process>unstarted = new LinkedList<>();
        PriorityQueue<Process> ready = new PriorityQueue<>(Comparator.comparingDouble(Process::getPenaltyR).reversed().thenComparingInt(Process::getA).thenComparingInt(Process::getId));
        Queue<Process>blocked = new LinkedList<>();
        ArrayList<Process> ordering = new ArrayList<>();
        int cycle = 0;
        curr = null;
        random = 0;

        HPRNprocesses.addAll(sorted);

        if (verbose){
            System.out.println("\nThis detailed printout gives the state and remaining burst for each process");
            System.out.printf("\n%s%2d%s","Before Cycle\t",cycle,":");
            for (int i = 0; i < processNum; i++){
                System.out.printf("\t%11s %d",HPRNprocesses.get(i).state, 0);
            }
        }

        cycle++;

        for (int i = 0; i < processNum; i++){
            if (HPRNprocesses.get(i).A == 0){
                HPRNprocesses.get(i).state = "ready";
                ready.add(HPRNprocesses.get(i));
            }else{
                unstarted.add(HPRNprocesses.get(i));
            }
        }

        if (!ready.isEmpty()){
            for (Process p: ready) {
                p.penaltyR = ((double) (cycle - p.A)) / Math.max(1, p.C - p.remain);
            }
        }

        label:
        while (terminatedNum != processNum){

            if (!blocked.isEmpty()) {
                for (Process p : HPRNprocesses) {
                    if (p.state.equals("blocked")) {
                        p.ioBurst--;
                        if (p.ioBurst == 0) {
                            p.state = "ready";
//                            ordering.add(p);
                            ready.add(p);
                            blocked.remove(p);
                        }
                    }
                }
//                Collections.sort(readyArr, new Process.penaltyRComparator());
//                ready.addAll(readyArr);
//                readyArr.clear();
            }

            while (!unstarted.isEmpty() && unstarted.peek().A + 1 == cycle){
                Process popped = unstarted.poll();
                popped.state = "ready";
//                ordering.add(popped);
                ready.add(popped);
            }

            if (!ready.isEmpty()){
                for (Process p: ready) {
                    p.penaltyR = ((float) (cycle - p.A)) / Math.max(1, p.C - p.remain);
                }
            }

//            ready.addAll(ordering);
////            Collections.sort(readyArr, new Process.penaltyRComparator());
//            ready.addAll(ordering);
//            ordering.clear();
////            readyArr.clear();

            if (curr == null){

                if (!ready.isEmpty()){
                    for (Process p: ready) {
                        p.penaltyR = ((double) (cycle - p.A)) / Math.max(1, p.C - p.remain);
                    }
                }

//                Collections.sort(readyArr, new Process.penaltyRComparator());
//                ready.addAll(readyArr);
//                readyArr.clear();

                if (ready.peek() != null) {
                    curr = ready.poll();
                    curr.state = "running";
                    curr.prevCPU = Math.min(curr.remain, randomOS(curr.B));
                    curr.cpuBurst = curr.prevCPU;
                    random++;
                }
            }else{
                curr.cpuBurst--;
                curr.remain--;
                if (curr.cpuBurst == 0) {
                    switch (curr.remain) {
                        case 0:
                            curr.state = "terminated";
                            curr.finish = cycle - 1;
                            curr.turnaround = curr.finish - curr.A;
                            terminatedNum++;
                            if (terminatedNum == processNum) {
                                break label;
                            }
                            curr = null;
                            break;
                        default:
                            curr.state = "blocked";
                            curr.ioBurst = curr.prevCPU * curr.M;
                            curr.io += curr.ioBurst;
                            blocked.add(curr);
                            curr = null;
                            break;
                    }


                    if (ready.peek() != null) {
                        if (!ready.isEmpty()){
                            for (Process p: ready) {
                                p.penaltyR = ((float) (cycle - p.A)) / Math.max(1, p.C - p.remain);
                            }
                        }
                        curr = ready.poll();
                        //                    if (!curr.state.equals("blocked")) {
                        curr.state = "running";
                        //                    }
                        curr.prevCPU = Math.min(curr.remain, randomOS(curr.B));
                        curr.cpuBurst = curr.prevCPU;
                        random++;
                    }
                }
            }


            if (verbose){
                System.out.printf("\n%s%2d%s","Before Cycle\t",cycle,":");
                for(int i = 0; i < processNum; i++){
                    if (HPRNprocesses.get(i).state.equals("running")){
                        System.out.printf("\t%11s %d",HPRNprocesses.get(i).state,HPRNprocesses.get(i).cpuBurst);
                    }else if (HPRNprocesses.get(i).state.equals("terminated")) {
                        System.out.printf("\t%11s %d", HPRNprocesses.get(i).state, HPRNprocesses.get(i).cpuBurst);
                    }else if (HPRNprocesses.get(i).state.equals("ready")){
                        System.out.printf("\t%11s %d", HPRNprocesses.get(i).state, 0);
                    }else{
                        System.out.printf("\t%11s %d",HPRNprocesses.get(i).state,HPRNprocesses.get(i).ioBurst);
                    }
                }
            }

            boolean IO = false;
            for (Process p : HPRNprocesses){
                if (p.state.equals("blocked")){
                    IO = true;
                }if (p.state.equals("ready")){
                    p.waiting++;
                }
            }

            if (IO){
                blockedCycleNum++;
            }

            if (!ready.isEmpty()){
                for (Process p: ready) {
                    p.penaltyR = ((float) (cycle - p.A)) / Math.max(1, p.C - p.remain);
                }
            }


            cycle++;

        }

        System.out.println("\nThe scheduling algorithm used was Highest Penalty Ratio Next\n");

        int finishTime = 0;
        int totalCPU = 0;
        int totalTurn = 0;
        int totalWait = 0;

        for (Process p : HPRNprocesses){
            System.out.println("Process "+p.id+":");
            System.out.println("\t\t(A,B,C,M) = ("+p.A+","+p.B+","+p.C+","+p.M+")");
            totalCPU+=p.C;
            System.out.println("\t\tFinishing time: "+p.finish);
            if (p.finish > finishTime){
                finishTime = p.finish;
            }
            System.out.println("\t\tTurnaround time: "+p.turnaround);
            totalTurn+=p.turnaround;
            System.out.println("\t\tI/O time: " + p.io);
            System.out.println("\t\tWaiting time: "+p.waiting);
            totalWait += p.waiting;
            System.out.println();

        }

        System.out.println("Summary Data: ");
        System.out.println("\t\tFinishing time: "+finishTime);
        System.out.println("\t\tCPU Utilization: "+(double)totalCPU/finishTime);
        System.out.println("\t\tI/O Utilization: "+(double) blockedCycleNum/finishTime);
        System.out.println("\t\tThroughput: "+(double)(100*processNum)/finishTime + " processes per hundred cycles");
        System.out.println("\t\tAverage turnaround time: "+(double)totalTurn/processNum);
        System.out.println("\t\tAverage waiting time: "+(double)totalWait/processNum);


        HPRNprocesses.clear();
    }//end of HPRN
}
