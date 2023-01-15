//omer maoz 206947186 and for ben shitrit 208631887 made this project

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class Main{

    //insertion sort O(N^2).
    public static void sort (Process[] list){
        Process tmp;
        for(int i=1;i<list.length;i++){
            if(list[i].arrival_time < list[i-1].arrival_time)
                for(int j=i; j>0; j--){
                    if(list[j].arrival_time < list[j-1].arrival_time){
                        tmp = list[j];
                        list[j] = list[j-1];
                        list[j-1] = tmp;
                    }
                }
        }
    }

    //check if all the processes done
    public static int isDone (Process[] list)
    {
        int count = 0;
        for (int i = 0; i < list.length; i++)
        {
            if (list[i].arrival_time != 999)
                count++;
        }
        return count;
    }

    //find min burst_time of process that exists at this timeline
    public static int find_OkMin (Process[] list, float totalCount)
    {
        Float min = (float) 999;
        int save = -1;
        for (int i = 0; i < list.length; i++)
        {
            if (min > list[i].burst_time && list[i].arrival_time < totalCount && list[i].burst_time!=0)
            {
                min = list[i].burst_time;
                save = i;
            }
        }
        return save;
    }

    //copy the process array
    public static Process[] copyArray(Process[] process){
        Process[] copy = new Process[process.length];
        for(int i=0; i<process.length; i++){
            copy[i] = new Process( process[i].arrival_time , process[i].burst_time);
        }
        return copy;
    }

    //FCFS algorithm.
    public static float fcfs(Process[] process_list , int process_count){
        Process[] temp_list = copyArray(process_list);
        Float turnaround = temp_list[0].burst_time;
        Float process_time;
        Float waiting;
        waiting = temp_list[0].arrival_time + temp_list[0].burst_time;

        for(int i=1;i<process_count;i++){
            if(temp_list[i].arrival_time < waiting) {
                process_time = waiting - temp_list[i].arrival_time + temp_list[i].burst_time;
                waiting += temp_list[i].burst_time;
            }
            else {
                process_time = temp_list[i].burst_time;
                waiting = temp_list[i].arrival_time + temp_list[i].burst_time;
            }
            turnaround += process_time;
        }
        return (turnaround /= process_count);
    }

    //LCFS NP algorithm.
    public static float ncfs_np(Process[] process_list , int process_count){
        Process[] templist = copyArray(process_list);
        Float turnaround = templist[0].burst_time;
        Float processTime = (float) 0;
        Float waiting = templist[0].arrival_time + templist[0].burst_time;
        templist[0].arrival_time = (float) 999;
        int i = 1;
        while (isDone(templist) != 0)
        {
            if (templist[process_count - i].arrival_time <= waiting)
            {
                processTime = waiting - templist[process_count - i].arrival_time + templist[process_count - i].burst_time;
                waiting +=  templist[process_count - i].burst_time;
                turnaround += processTime;
                templist[process_count - i].arrival_time = (float) 999;
                i = 1;
            }
            else
            {
                if (process_count == 2)
                {
                    if (templist[0].arrival_time == (float) 999)
                    {
                        waiting = templist[process_count - i].arrival_time;
                        i = 1;
                    }
                    else
                        i++;
                }
                else
                {
                    if (templist[1].arrival_time == (float) 999 && templist[process_count - i - 1].arrival_time == (float) 999) //All current timeline processes have been completed
                    {
                        waiting = templist[process_count - i].arrival_time;
                        i = 1;
                    }
                    if (process_count - i == 1)
                    {
                        if (templist[process_count - i].arrival_time < waiting) //There is another process that has not yet ended in the current timeline
                        {
                            processTime = waiting - templist[process_count - i].arrival_time + templist[process_count - i].burst_time;
                            waiting +=  templist[process_count - i].burst_time;
                        }
                        else
                        {
                            processTime = templist[process_count - i].burst_time;
                            waiting = templist[process_count - i].arrival_time;
                        }
                        turnaround += processTime;
                        templist[process_count - i].arrival_time = (float) 999;
                        i = 1;
                    }
                    else
                        i++;
                }
            }
        }

        return (turnaround /= process_count);
    }

    //LCFS P algorithm.
    public static float ncfs_p(Process[] process_list , int process_count){

        Process[]temp_list = copyArray(process_list);
        Float waiting = (float)0;
        Float turnaround = (float)0;
        boolean firstRound = true;

        //first loop
        for(int i = 0; i<process_count; i++){

            //if the p[i].at < waiting -> correct waiting
            if(temp_list[i].arrival_time > waiting){
                waiting = temp_list[i].arrival_time;
                i--;
                continue;
            }

            if((i+1) == process_count){
                waiting += temp_list[i].burst_time;
                turnaround += waiting - temp_list[i].arrival_time;
                temp_list[i].burst_time = (float)0;
                break;
            }

            if(firstRound){
                waiting = temp_list[i].arrival_time + temp_list[i].burst_time;
                if(temp_list[i+1].arrival_time < waiting){
                    waiting = temp_list[i+1].arrival_time;
                }
                if(temp_list[i+1].arrival_time < waiting + temp_list[i].burst_time)
                    temp_list[i].burst_time -= (temp_list[i+1].arrival_time - temp_list[i].arrival_time);
                else
                    temp_list[i].burst_time = (float) 0;
                if(temp_list[i].burst_time == 0)
                    turnaround += (waiting - temp_list[0].arrival_time);
                firstRound = false;
                continue;
            }

            if(temp_list[i+1].arrival_time < waiting + temp_list[i].burst_time ){
                temp_list[i].burst_time -= (temp_list[i+1].arrival_time - temp_list[i].arrival_time);
                waiting += (temp_list[i+1].arrival_time - temp_list[i].arrival_time);
                if(temp_list[i].burst_time == 0)
                    turnaround += (waiting - temp_list[i].arrival_time);
            }
            //p2.at >= waiting
            else {
                waiting += temp_list[i].burst_time;
                temp_list[i].burst_time = (float)0;
                if(temp_list[i].burst_time == 0)
                    turnaround += (waiting - temp_list[i].arrival_time);
            }
        }

        //second loop -> from the end to the beginning
        for(int j = process_count-2; j>=0; j--){
            if(temp_list[j].burst_time > 0){
                waiting += temp_list[j].burst_time;
                temp_list[j].burst_time = (float)0;
                turnaround += (waiting - temp_list[j].arrival_time);
            }
        }

        return (turnaround /= process_count);

    }

    //Round Robin (NP) algorithm.
    public static float roundRobin(Process[] process_list , int process_count){
        Process[] OriginalList = copyArray(process_list);
        Process[] templist = copyArray(process_list);
        Float turnaround = (float)0;
        float timeQuantum=2;
        Float totalCount = templist[0].arrival_time;
        int size= process_count;
        Float turnAroundArr[]= new Float[process_count];

        int i=0;
        while (isDone(templist) != 0)
        {
            if(templist[i%size].arrival_time<=totalCount && templist[i%size].burst_time!=0) //the turn of the process
            {
                if(templist[i%size].burst_time!=1)
                {
                    templist[i%size].burst_time-=timeQuantum;
                    totalCount+=timeQuantum;
                }
                else
                {
                    templist[i%size].burst_time-=1;
                    totalCount+=1;
                }
            }

            else if(templist[i%size].arrival_time>totalCount && templist[i%size].burst_time!=0 ) //all the process before me are done
            {
                if((i%size!=0) && templist[(i-1)%size].burst_time==0 )
                    totalCount= templist[i%size].arrival_time;
            }

            if(templist[(i+1)%size].arrival_time <totalCount && (i+1%size!=size-1)) //we miss process
            {
                templist[(i+1)%size].arrival_time=totalCount;
            }


            if(templist[i%size].burst_time==0 && templist[i%size].arrival_time!=(float)999 ) // the process is done
            {

                if(OriginalList[i%size].burst_time==0)
                    turnAroundArr[i%size]=(float) 0;

                else
                    turnAroundArr[i%size]=totalCount-OriginalList[i%size].arrival_time;

                templist[i%size].arrival_time=(float)999;

            }

            i++;
        }

        for(i=0;i<templist.length;i++)
        {
            turnaround+= turnAroundArr[i];
        }
        return (turnaround/process_count);
    }


    //SJF (NP) algorithm.
    public static float sjf(Process[] process_list , int process_count) {
        Process[] temp_list = copyArray(process_list);
        Float waiting = temp_list[0].arrival_time;
        Float turnaround = (float) 0;
        float count = 0;
        float totalCount = temp_list[0].arrival_time;
        int i = 0;
        int j = i + 1;


        while (isDone(temp_list) != 0) {
            count = 0;
            if (i == -1) {
                totalCount = temp_list[0].arrival_time;
                i = 0;
                j = i + 1;
            }
            while (totalCount < temp_list[j].arrival_time && temp_list[j].arrival_time != 999 && temp_list[j].arrival_time < totalCount + temp_list[i].burst_time) //There is free time until the next process arrives
            {
                count++;
                totalCount++;
            }
            temp_list[i].burst_time -= count;
            if (temp_list[j].burst_time < temp_list[i].burst_time && temp_list[j].burst_time != 0 && temp_list[j].arrival_time < totalCount + temp_list[i].burst_time) //There is another process in the current timeline that has a higher priority
            {
                i = j;
                j++;
            } else {
                if (j == temp_list.length - 1 || temp_list[j].arrival_time == 999 || temp_list[j].arrival_time > totalCount + temp_list[i].burst_time) //can finish the whole process
                {
                    totalCount += temp_list[i].burst_time;
                    count = temp_list[i].burst_time;
                    turnaround += totalCount - temp_list[i].arrival_time;
                    temp_list[i].arrival_time = (float) 999;
                    temp_list[i].burst_time -= count;
                    sort(temp_list);
                    i = find_OkMin(temp_list, totalCount); //find min burst_time of process that exists at this timeline
                    j = i + 1;
                } else
                    j++;
            }
        }
        return (turnaround /= process_count);
    }


    public static void main(String[] args) {
        //get the file and use it with the bufferReader.
        File file = new File("input5.txt");
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        int process_count;

        try {
            //read the first line of the file ->L = number of processes.
            process_count = Integer.parseInt(bufferedReader.readLine());
            Process[] process_list = new Process[process_count];
            String[] arr = new String[2];
            String row;
            int i = 0;

            //adding all the processes to the list.
            while ((row = bufferedReader.readLine()) != null) {
                arr = row.split(",");
                process_list[i] = new Process(Float.parseFloat(arr[0]), Float.parseFloat(arr[1]));
                i++;
            }

            System.out.println(file+"\n");

            //sort the list by arrival time.
            sort(process_list);

            System.out.println("FCFS: mean turnaround = " + fcfs(process_list, process_count));
            System.out.println("LCFS (NP): mean turnaround = " + ncfs_np(process_list, process_count));
            System.out.println("LCFS (P): mean turnaround = " + ncfs_p(process_list, process_count));
            System.out.println("RR: mean turnaround = " + roundRobin(process_list, process_count));
            System.out.println("SJF: mean turnaround = " + sjf(process_list, process_count));

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static class Process {
        public float arrival_time;
        public float burst_time;
        public boolean term;

        //constructor
        public Process(float arrival_time, float burst_time) {
            super();
            this.arrival_time = arrival_time;
            this.burst_time = burst_time;
            term = false;
        }
    }
}


