package com.example.myapplication;

public class note {
    public int tid;
    public String text;
    public String date;
    public int pid;
    public note(int i,String t,String d,int pid){
        this.date=d;
        this.pid=pid;
        this.text=t;
        this.tid=i;
    }
    public note(String t,String d,int pid){
        this.date=d;
        this.pid=pid;
        this.text=t;
    }
    public note(){

    }
}

