#!/usr/bin/env python
# coding: utf-8

from gensim.summarization.summarizer import summarize
from gensim.summarization import keywords
import PyPDF4
import os
from pyrebase import pyrebase

config = {
            "apiKey": "AIzaSyAkk15EfQ_sng9SIVc8M9xLb62VsH40jKw",
            "authDomain": "summarizer-7cdea.firebaseapp.com",
            "databaseURL": "https://summarizer-7cdea.firebaseio.com",
            "projectId": "summarizer-7cdea",
            "storageBucket": "summarizer-7cdea.appspot.com",
            "messagingSenderId": "1095367000107"
    }

firebase2 = pyrebase.initialize_app(config)
db = firebase2.database()

def stream_handler(message):
    storage = firebase2.storage()      
    os.chdir("C:/Users/Dell/Desktop/AIML/Proj/SoftwareProject")
    storage.child("input.pdf").download("don.pdf")
    filename="C:/Users/Dell/Desktop/AIML/Proj/SoftwareProject/don.pdf"
    pdfFileObj = open(filename,'rb')
    
    pdfReader = PyPDF4.PdfFileReader(pdfFileObj)
    
    num_pages = pdfReader.numPages
        
    cnt = 0
    text = ""
    
    while cnt < num_pages:
        pageObj = pdfReader.getPage(cnt)
        cnt +=1
        text += pageObj.extractText()
        
    from firebase import firebase
    firebase1=firebase.FirebaseApplication('https://summarizer-7cdea.firebaseio.com/summarizer-7cdea')            
    name1=firebase1.get('/input','topic')  
    l=name1+'.txt'     
    f = open(l,'a')
    f.write("Topic- " + name1 + "\n\n")
    word1=firebase1.get('/input','top_sent')
    f.write(summarize(text,word_count=int(word1)))
    f.write("\n\nKeywords\n")
    f.write(str(keywords(text).split('\n')))
    f.close()
    storage.child(name1).put(l)
    mal=storage.child(name1).get_url(1)
     
    firebase1.put('/output','outfile',mal)
    firebase1.put('/output','topic',name1)
    print(1)
my_stream = db.child("input").child("topic").stream(stream_handler)
#my_stream.close()