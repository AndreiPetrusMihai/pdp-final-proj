import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Utils {
    private static int weekdays = 5;


    public static boolean verifyTimetable(List<Integer> subjects, int noOfGroups, int classesPerSubject) {
        int subjectIndex = 0;

        try {
            Map<Integer, List<Integer>> groupRegisteredSubjects = new HashMap<>();

            for (int group = 1; group <= noOfGroups; group++) {
                groupRegisteredSubjects.put(group, new ArrayList<>(Collections.nCopies(Program.noOfSubjects, 0)));
            }

            while (subjectIndex < subjects.size()) {
                for (int day = 1; day <= weekdays; day++) {
                    Map<Integer, Boolean> occupiedSubjectsThisHour = new HashMap<>();
                    for (int group = 1; group <= noOfGroups; group++) {
                        if (subjectIndex >= subjects.size()) break;

                        if (occupiedSubjectsThisHour.getOrDefault(subjects.get(subjectIndex), false)) {
                            return false;
                        }

                        occupiedSubjectsThisHour.put(subjects.get(subjectIndex), true);

                        List<Integer> groupRegisteredSubject = groupRegisteredSubjects.get(group);
                        Integer prevValue = groupRegisteredSubject.get(subjects.get(subjectIndex) - 1);
                        groupRegisteredSubject.set(subjects.get(subjectIndex) - 1, prevValue + 1);

                        groupRegisteredSubjects.replace(group, groupRegisteredSubject);

                        if (groupRegisteredSubjects.get(group).get(subjects.get(subjectIndex) - 1) > classesPerSubject) {
                            return false;
                        }

                        subjectIndex++;
                        if (subjectIndex >= subjects.size()) break;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            System.out.println(subjects.size());
            System.out.println(subjectIndex);
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void ExhaustiveSearch(List<Integer> generatedSubjects, int currentIndex, int totalSubjectsInWeek, int numberOfSubjects, Thread program) {
        for (int subject = 1; subject <= numberOfSubjects; subject++) {
            generatedSubjects.set(currentIndex, subject);
            if (currentIndex == totalSubjectsInWeek - 1) {
                if (verifyTimetable(generatedSubjects, Program.noOfGroups, Program.classesPerSubject)) {
                    System.out.println("Found good solution");

                    int subjectIndex = 0;
                    StringBuilder stringBuilder = new StringBuilder();
                    List<Integer> solution = new ArrayList<>();

                    while (subjectIndex < totalSubjectsInWeek) {
                        for (int day = 1; day <= Program.weekDays; day++) {
                            for (int group = 1; group <= Program.noOfGroups; group++) {
                                if (subjectIndex >= totalSubjectsInWeek) break;
                                stringBuilder.append(generatedSubjects.get(subjectIndex)).append(" ");
                                solution.add(generatedSubjects.get(subjectIndex));
                                subjectIndex++;
                            }
                            stringBuilder.append(" ");
                            if (subjectIndex >= totalSubjectsInWeek) break;
                        }
                        stringBuilder.append("\n");
                    }
                    writeToFile(solution);
                    System.out.println(stringBuilder);
                    program.interrupt();
                }
            } else {
                ExhaustiveSearch(generatedSubjects, currentIndex + 1, totalSubjectsInWeek, numberOfSubjects, program);
            }
        }
    }

    public static void writeToFile(List<Integer> solution) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("solution.txt"));
            StringBuilder headerBuilder = new StringBuilder("Solution:\n");
            for (int day = 1; day <= Program.weekDays; day++) {
                headerBuilder.append("|  ").append(day).append("  |");
            }
            headerBuilder.append("\n");

            for (int day = 1; day <= Program.weekDays; day++) {
                headerBuilder.append("|");
                for (int group = 1; group <= Program.noOfGroups; group++) {
                    headerBuilder.append(group).append("   ");
                }
                headerBuilder.delete(headerBuilder.length() - 3, headerBuilder.length());
                headerBuilder.append("|");
            }

            headerBuilder.append("\n");

            writer.write(headerBuilder.toString());

            StringBuilder builder = new StringBuilder();
            int subjectIndex = 0;
            for (Integer subject : solution) {
                if (subjectIndex % Program.weekDays * Program.noOfGroups == 1) {
                    builder.append("|");
                } else if (subjectIndex % Program.noOfGroups == 0) {
                    if(subjectIndex % Program.weekDays * Program.noOfGroups != 0)
                    builder.append("||");
                    else builder.append("|");
                }

                if (subjectIndex > Program.weekDays * Program.noOfGroups) {
                    builder.append("\n");
                }
                builder.append(subject);
                if (subjectIndex % Program.noOfGroups == 0)
                    builder.append("   ");

                subjectIndex++;
            }
            writer.write(builder.toString());

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
