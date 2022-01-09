import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    public static void main(String[] args) {
        int subjects = Program.noOfSubjects;
        int noOfThreads;

        long start = System.currentTimeMillis();

        Scanner scanner = new Scanner(System.in);
        System.out.println("No of threads: ");
        noOfThreads = Integer.parseInt(scanner.nextLine());

        List<Thread> threadList = new ArrayList<>();

        int nrOfProcesses = Math.min(noOfThreads, subjects);

        int subjectsPerThread = (subjects + nrOfProcesses - 1) / nrOfProcesses;

        for (int processIndex = 1; processIndex <= nrOfProcesses; processIndex++) {
            List<Integer> processStartSubjects = new ArrayList<>(Collections.nCopies(subjectsPerThread,0));

            int subjectId = processIndex;

            System.out.println("Process " + processIndex + " will start with subjects: ");

            for (int i = 0; i < subjectsPerThread; i++) {
                if (subjectId <= subjects) {
                    processStartSubjects.set(i,subjectId);
                    subjectId += nrOfProcesses;
                }
//                System.out.println(processStartSubjects.get(i) + " ");
            }

            Program process = new Program(processStartSubjects,processIndex);
            threadList.add(new Thread(process));
            threadList.get(threadList.size()-1).start();
        }
        for(Thread t:threadList){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Execution time: "+(end-start));
    }
}
