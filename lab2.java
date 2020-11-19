import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class lab2 {

    public static int random = 0; //how many lines to skip when reading random-numbers
    public static boolean verbose = false;
    public static int processNum;
    public static boolean showRandom = false;
    private static ArrayList<Process> original;
//    private static ArrayList<Process> sorted;

    public static int randomOS(int U) throws FileNotFoundException {
        File f = new File("random-numbers");
        String line = null;
        try (Stream<String> lines = Files.lines(Paths.get(String.valueOf(f)))){
            line = lines.skip(random).findFirst().get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (showRandom) {
            System.out.print("\nFind burst when choosing ready process to run " + line);
        }

        return 1+(Integer.parseInt(line) % U);
    }//end of randomOS

    public static void main(String[] args) throws IOException {
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(reader);
        String[] input = in.readLine().split(" ");
        File f;

        if (input.length == 4){
            if (input[1].equals("--verbose")) {
                verbose = true;
            }
            if (input[2].equals("--show")) {
                showRandom = true;
            }
            f = new File(input[3]);
        }else if (input.length == 3){
            if (input[1].equals("--verbose")) {
                verbose = true;
            }
                f = new File(input[2]);
        }else{
            f = new File(input[1]);
        }

        BufferedReader read = new BufferedReader(new FileReader(f));
        String line = null;

        StringBuffer sb = new StringBuffer();

        while ((line = read.readLine()) != null){
            if (line.trim().length() == 0){
                continue;
            }
            sb.append(line.trim()+" ");
        }
        reader.close();

        String[] arr = sb.toString().replaceAll("[^0-9]", " ").trim().split("\\W+");

        processNum = Integer.parseInt(arr[0]);

        original = new ArrayList<>();

        for (int i = 1; i < arr.length-1; i+=4){
            int A = Integer.parseInt(arr[i]);
            int B = Integer.parseInt(arr[i+1]);
            int C = Integer.parseInt(arr[i+2]);
            int M = Integer.parseInt(arr[i+3]);
            original.add(new Process(A,B,C,M));
        }

        ArrayList<Process> originCopy = (ArrayList<Process>) original.clone();

        Collections.sort(originCopy, new Process.arrivalComparator());

        ArrayList<Process> sorted = new ArrayList<>();

        for (int i = 0; i < originCopy.size(); i++) {
            originCopy.get(i).setId(i);
        }

        for (int i = 0; i < originCopy.size(); i++){
            sorted.add(originCopy.get(i));
        }

//        List<Process> finalize = Collections.unmodifiableList(sorted);

        System.out.print("The original input was: "+processNum+" ");
        for (int i = 0; i < original.size(); i++){
            System.out.print("("+ original.get(i).A+" "+original.get(i).B+" "+original.get(i).C+" "+original.get(i).M+") ");
        }

        System.out.println();

        System.out.print("The (sorted) input is: "+processNum+" ");

        for (int i = 0; i < sorted.size(); i++){
            System.out.print("("+ sorted.get(i).A+" "+sorted.get(i).B+" "+sorted.get(i).C+" "+sorted.get(i).M+") ");
        }

        System.out.println();

        ArrayList<Process> sortedCopy1 = new ArrayList<>();
        ArrayList<Process> sortedCopy2 = new ArrayList<>();
        ArrayList<Process> sortedCopy3 = new ArrayList<>();
        ArrayList<Process> sortedCopy4 = new ArrayList<>();


        for (int i1 = 0; i1 < sorted.size(); i1++) {
            Process i = sorted.get(i1);
            sortedCopy1.add(Process.clone(i));
            sortedCopy2.add(Process.clone(i));
            sortedCopy3.add(Process.clone(i));
            sortedCopy4.add(Process.clone(i));
        }


        Algos a1 = new Algos();
        a1.FCFS(sortedCopy1);
        sortedCopy1.clear();
        System.out.println("------------------------end of FCFS--------------------------");

        Algos a2 = new Algos();
        a2.RR(sortedCopy2);
        sortedCopy2.clear();
        System.out.println("-----------------------end of RR--------------------------");

        Algos a3 = new Algos();
        a3.LCFS(sortedCopy3);
        sortedCopy3.clear();
        System.out.println("------------------------end of LCFS-------------------------");

        Algos a4 = new Algos();
        a4.HPRN(sortedCopy4);
        sortedCopy4.clear();
        System.out.println("------------------------end of HPRN-------------------------");




    }//end of main

}
