# -*- coding: utf-8 -*-

from nltk.corpus import stopwords
from nltk.cluster.util import cosine_distance as cd
import numpy as np
import networkx as nx
import PyPDF2 
from PIL import Image
import pytesseract
import cv2
import os
import nltk 
nltk.download('words')
import sys
nltk.download('stopwords')
import math
from textblob import TextBlob as tb

#Input article → split into sentences → remove stop words → 
#build a similarity matrix → generate rank based on matrix → 
#pick top N sentences for summary.

from pyrebase import pyrebase

config = {
            "apiKey": "AIzaSyAkk15EfQ_sng9SIVc8M9xLb62VsH40jKw",
            "authDomain": "summarizer-7cdea.firebaseapp.com",
            "databaseURL": "https://summarizer-7cdea.firebaseio.com",
            "projectId": "summarizer-7cdea",
            "storageBucket": "summarizer-7cdea.appspot.com",
            "messagingSenderId": "1095367000107"
    }

firebase = pyrebase.initialize_app(config)
db = firebase.database()

def stream_handler(message):
    from pyrebase import pyrebase

    config = {
            "apiKey": "AIzaSyAkk15EfQ_sng9SIVc8M9xLb62VsH40jKw",
            "authDomain": "summarizer-7cdea.firebaseapp.com",
            "databaseURL": "https://summarizer-7cdea.firebaseio.com",
            "projectId": "summarizer-7cdea",
            "storageBucket": "summarizer-7cdea.appspot.com",
            "messagingSenderId": "1095367000107"
    }

    firebase = pyrebase.initialize_app(config)
    storage = firebase.storage()
    os.chdir("C:/Users/Dell/Desktop/AIML/Proj/SoftwareProject")
    storage.child("input").download("download.pdf")
    filename="C:/Users/Dell/Desktop/AIML/Proj/SoftwareProject/download.pdf"
    
    if filename.endswith(".pdf"):
        
        pdfFileObj = open(filename,'rb')
        
        pdfReader = PyPDF2.PdfFileReader(pdfFileObj)
        
        num_pages = pdfReader.numPages
        
        cnt = 0
        text = ""
        
        while cnt < num_pages:
            pageObj = pdfReader.getPage(cnt)
            cnt +=1
            text += pageObj.extractText()
            
        f = open('Input.txt','w')
        f.write(text)
        f.close()
            
    elif filename.endswith(".png") or filename.endswith(".jpg"):

        def preprocess (image): 
            try:
                gray = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU)[1]
            except:
                gray = image
            filename = "{}.jpg".format(100)
            cv2.imwrite(filename, gray)
            return filename
        
        def ocr(filename):
            path = os.getcwd()
            im = Image.open(path+"/"+filename)
            text = pytesseract.image_to_string(im)
            os.remove(filename)
            return text

        im = cv2.imread(filename)
        x = preprocess(im)
        with open('Input.txt','w') as f:
            f.write(ocr(x))
    else:
        print("Extensions allowed are .pdf,.png,.jpg")
        sys.exit('Error!')
    
#Summarization process starts.......

    def rd_art(fl_nme):
        file = open(fl_nme, "r")
        fdata = file.readlines()
        article = fdata[0].split(".")
        sents = []
        
        for sent in article:
            sent=sent + "\n"
            sents.append(sent.replace("[^a-zA-Z]", " ").split(" "))
        sents.pop() 
    
        return sents

    def sent_sim(st1, st2, stopwords=None):
        if stopwords is None:
            stopwords = []
 
        st1 = [w.lower() for w in st1]
        st2 = [w.lower() for w in st2]
 
        words = list(set(st1 + st2))
 
        vec1 = vec2 = [0] * len(words)
 
        for wor in st1:
            if wor in stopwords:
                continue
            vec1[wor.index(wor)] = vec1[wor.index(wor)]+1
 
        for wor in st2:
            if wor in stopwords:
                continue
            vec2[wor.index(wor)] = vec2[wor.index(wor)]+1
 
        return 1-cd(vec1, vec2)
 
    def sim_mat(sent, stop_words):
        sim_mat = np.zeros((len(sent), len(sent)))
        
        for id1 in range(len(sent)):
            for id2 in range(len(sent)):
                if id1 == id2: 
                    continue 
                sim_mat[id1][id2] = sent_sim(sent[id1], sent[id2], stop_words)

        return sim_mat


    def gen_sum(f_name, storage, firebase1,top_n):
        stp_wrds = stopwords.words('english')
        sum_txt = []
        
        sent =  rd_art(f_name)
        
        sent_sim_mat = sim_mat(sent, stp_wrds)

        sent_sim_grph = nx.from_numpy_array(sent_sim_mat)
        score = nx.pagerank(sent_sim_grph)
        
        rank_sent = sorted(((score[i],s) for i,s in enumerate(sent)), reverse=True)    
        m=int(top_n)
        for i in range(m):
            sum_txt.append(" ".join(rank_sent[i][1]))  
        
        doc1=tb(" ".join(sum_txt))
        blblst=[doc1]
        def tf(wrd, blb):
            return blb.words.count(wrd) / len(blb.words)

        def contain(wrd, blblst):
            return sum(1 for blb in blblst if wrd in blb.words)
        
        def idf(wrd, blblst):
            return math.log(len(blblst) / (1 + contain(wrd, blblst)))
        
        def tfidf(wrd, blb, blblst):
            return tf(wrd, blb) * idf(wrd, blblst)
        
        name1=firebase1.get('/input','topic')
        l=name1+'.txt'
        f = open(l,'a')
        f.write("Topic- " + name1 + "\n\n")
        f.write(" ".join(sum_txt))
        f.write("\n\n\n")
        for i, blb in enumerate(blblst):
            f.write("Top words in the document are\n")
            sc = {wrd: tfidf(wrd, blb, blblst) for wrd in blb.words}
            sort_wrds = sorted(sc.items(), key=lambda xa: xa[   1], reverse=True)
            for wrd, sco in sort_wrds[:5]:
                f.write("\tWord: {}, TF-IDF Score: {}\n".format(wrd, round(sco, 5)))
        f.close()
        storage.child(name1).put(l)
        mal=storage.child(name1).get_url(1)
        firebase1.put('/output','outfile',mal)
        firebase1.put('/output','topic',name1)
    from firebase import firebase
    firebase1=firebase.FirebaseApplication('https://summarizer-7cdea.firebaseio.com/summarizer-7cdea')
    top_n=firebase1.get('/input','top_sent')
    gen_sum("Input.txt",storage,firebase1,top_n)
my_stream = db.child("input").child("topic").stream(stream_handler)
#my_stream.close()