using MPI;
using System;
using System.Collections.Generic;

namespace FinalProjMPI
{



    internal class Program
    {
        public static int weekDays = 5;
        public static int noOfGroups = 2;
        public static int noOfSubjects = 2;
        public static int classesPerSubject = 2;

        public static bool VerifyTimeTable(int[] subjects)
        {
            int subjectIndex = 0;
            Console.WriteLine("Subjects:");
            Console.WriteLine("Subjects: " + subjects.Length);

            for (int i = 0; i < subjects.Length; i++)
            {
                Console.Write(subjects[i] + " ");
            }
            Console.WriteLine();

            try
            {
                Dictionary<int, int[]> groupRegisteredSubjects = new Dictionary<int, int[]>();

                //Keep track to make sure that all groups have all the required subjects
                for (int group = 1; group <= noOfGroups; group++)
                {
                    groupRegisteredSubjects.Add(group, new int[noOfSubjects]);
                }

                while (subjectIndex < subjects.Length)
                {
                    for (int day = 1; day <= weekDays; day++)
                    {
                        //Check that no 2 groups have the same subject at the same time(teacher won't be available)
                        Dictionary<int, bool> occupiedSubjectsThisHour = new Dictionary<int, bool>();
                        for (int group = 1; group <= noOfGroups; group++)
                        {
                            if (subjectIndex >= subjects.Length) break;


                            if (occupiedSubjectsThisHour.GetValueOrDefault(subjects[subjectIndex], false) == true)
                            {
                                return false;
                            }

                            occupiedSubjectsThisHour.Add(subjects[subjectIndex], true);

                            groupRegisteredSubjects[group][subjects[subjectIndex] - 1]++;

                            if (groupRegisteredSubjects[group][subjects[subjectIndex] - 1] > classesPerSubject)
                            {
                                return false;
                            }


                            subjectIndex++;
                        }
                        if (subjectIndex >= subjects.Length) break;
                    }
                }

                return true;

            }
            catch (Exception ex)
            {
                Console.WriteLine(subjects.Length);
                Console.WriteLine(subjectIndex);
                Console.WriteLine(ex.ToString());
                return false;
            }


        }

        public static void STExhaustiveS(int[] generatedSubjects, int currentIndex, int totalSubjectsInWeek, int numberOfSubjects)
        {
            for (int subject = 1; subject <= numberOfSubjects; subject++)
            {
                generatedSubjects[currentIndex] = subject;
                //Console.WriteLine("Adding: " + subject);
                if (currentIndex == totalSubjectsInWeek - 1)
                {
                    if (VerifyTimeTable(generatedSubjects))
                    {
                        Console.WriteLine("Found good solution");

                        int subjectIndex = 0;
                        while (subjectIndex < totalSubjectsInWeek)
                        {
                            for (int day = 1; day <= weekDays; day++)
                            {
                                for (int group = 1; group <= noOfGroups; group++)
                                {
                                    if (subjectIndex >= totalSubjectsInWeek) break;
                                    Console.Write(generatedSubjects[subjectIndex] + " ");

                                    subjectIndex++;
                                }
                                Console.Write("  ");
                                if (subjectIndex >= totalSubjectsInWeek) break;

                            }
                            Console.WriteLine();


                        }
                        Console.WriteLine();
                        Console.WriteLine();

                    }
                    else
                    {
                        //Console.WriteLine("Found bad solution");
                    }
                }
                else
                {
                    STExhaustiveS(generatedSubjects, currentIndex + 1, totalSubjectsInWeek, numberOfSubjects);
                }
            }

        }

        public static void STChild()
        {
            Console.WriteLine("In child");
            int weekDays = Communicator.world.Receive<int>(0, 0);
            int groups = Communicator.world.Receive<int>(0, 0);
            int subjects = Communicator.world.Receive<int>(0, 0);
            int classesPerSubject = Communicator.world.Receive<int>(0, 0);
            Console.WriteLine();
            int[] processStartSubjects = Communicator.world.Receive<int[]>(0, 0);

            //Console.WriteLine("Process " + Communicator.world.Rank + " received:");

            int totalSubjectsInWeek = classesPerSubject * subjects * groups;

            for (int i = 0; i < processStartSubjects.Length; i++)
            {
                Console.Write(processStartSubjects[i] + " ");
            }
            Console.WriteLine();
            for (int i = 0; i < processStartSubjects.Length; i++)
            {
                Console.WriteLine("Starting with: " + processStartSubjects[i]);
                //We loop through each designated start subject for this process
                int[] generatedSubjects = new int[totalSubjectsInWeek];

                generatedSubjects[0] = processStartSubjects[i];

                STExhaustiveS(generatedSubjects, 1, totalSubjectsInWeek, subjects);
            }
            Console.WriteLine();

        }

        static void Main(string[] args)
        {
            using (new MPI.Environment(ref args))
            {
                if (Communicator.world.Rank == 0)
                {
                    Console.WriteLine("In master");

                    //main process
                    var weekDays = Program.weekDays;
                    var groups = Program.noOfGroups;
                    var subjects = Program.noOfSubjects;
                    var classesPerSubject = Program.classesPerSubject;
                    var mpiProcesses = (Communicator.world.Size - 1) > subjects ? subjects : (Communicator.world.Size - 1);
                    Console.WriteLine(mpiProcesses);

                    var subjectPerProcess = (subjects + mpiProcesses - 1) / mpiProcesses;



                    for (int processIndex = 1; processIndex <= mpiProcesses; processIndex++)
                    {
                        int[] processStartSubjects = new int[subjectPerProcess];
                        int subjectNumber = processIndex;
                        Console.WriteLine("Process " + processIndex + " will start with:");

                        for (int i = 0; i < subjectPerProcess; i++)
                        {
                            if (subjectNumber <= subjects)
                            {
                                processStartSubjects[i] = subjectNumber;
                                subjectNumber += mpiProcesses;
                            }
                            Console.Write(processStartSubjects[i] + " ");

                        }
                        Console.WriteLine();


                        Communicator.world.Send<int>(weekDays, processIndex, 0);
                        Communicator.world.Send<int>(groups, processIndex, 0);
                        Communicator.world.Send<int>(subjects, processIndex, 0);
                        Communicator.world.Send<int>(classesPerSubject, processIndex, 0);
                        Communicator.world.Send<int[]>(processStartSubjects, processIndex, 0);

                    }


                    for (int processIndex = 1; processIndex < mpiProcesses; processIndex++)
                    {


                    }

                }
                else
                {
                    STChild();
                }
            }
        }
    }
}
