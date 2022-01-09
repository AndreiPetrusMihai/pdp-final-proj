import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Program implements Runnable {
    public static int weekDays = 5;
    public static int noOfGroups = 2;
    public static int noOfSubjects = 2;
    public static int classesPerSubject = 2;

    private final List<Integer> processStartSubjects;
    private final int threadNo;

    public Program(List<Integer> processStartSubjects, int threadNo) {
        this.processStartSubjects = processStartSubjects;
        this.threadNo = threadNo;
    }

    @Override
    public void run() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Thread no. ").append(threadNo).append(" started\n").append("Subjects: ");
        for (Integer subjects : processStartSubjects) {
            stringBuilder.append(subjects).append(",");
        }
        stringBuilder.append("\n");
        System.out.println(stringBuilder);

        int totalSubjectsInWeek = classesPerSubject * noOfSubjects * noOfGroups;

        for(Integer subject: processStartSubjects){
            System.out.println("Starting with subject id:" + subject);

            List<Integer> generatedSubjects = new ArrayList<>(Collections.nCopies(totalSubjectsInWeek,0));
            generatedSubjects.set(0,subject);

            Utils.ExhaustiveSearch(generatedSubjects,1,totalSubjectsInWeek,noOfSubjects,Thread.currentThread());
        }


        System.out.println("Thread no. " + threadNo + " started");

    }

    public void stop(){
        Thread.currentThread().interrupt();
    }
}
