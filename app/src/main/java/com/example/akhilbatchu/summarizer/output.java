package com.example.akhilbatchu.summarizer;

public class output {
    private String outfile;
    private String topic;
    public  output()
    {

    }

    public output(String outfile, String topic) {
        this.outfile = outfile;
        this.topic = topic;
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
