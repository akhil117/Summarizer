package com.example.akhilbatchu.summarizer;

public class outputs {
    private String outfile;
    private String topic;
    private String date;
    public  outputs()
    {

    }

    public outputs(String outfile, String topic, String date) {
        this.outfile = outfile;
        this.topic = topic;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOutfile() {
        return outfile;
    }

    public void setOutfile(String outfile) {
        this.outfile = outfile;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}

