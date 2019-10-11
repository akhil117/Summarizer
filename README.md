# Summarizer

## Introduction

With the text summarizer app, the users can summarize a complex, huge text into a simple and understandable summary. 
The main stakeholders of the app are college students and other stakeholders such as employees, teachers, etc. The users can share the summarized text with their friends, colleagues, etc.

## Problem Statement

We all interact with applications which uses text summarization. Many of those applications are platform dependent
which publishes articles on daily news, entertainment, sports. With our busy schedule, we prefer to read the summary of those 
article before we decide to jump in for reading the entire article.Reading a summary help us to identify the interest area, 
gives a brief context of the story.

## Working 

We followed an extractive summarization technique for summarizing the text. Extractive summarization methods attempt to
summarize articles by selecting a subset of words that retain the most important points.

## Abstract Model
![Screenshot](https://github.com/akhil117/Summarizer/blob/master/app/src/main/res/drawable/block_3.png)

### Android ScreenShots
![Screenshot](https://github.com/akhil117/Summarizer/blob/master/app/src/main/res/drawable/splash.jpeg) 
![Screenshot](https://github.com/akhil117/Summarizer/blob/master/app/src/main/res/drawable/login.jpeg)
![Screenshot](https://github.com/akhil117/Summarizer/blob/master/app/src/main/res/drawable/googlesignin.jpeg)
![Screenshot]https://github.com/akhil117/Summarizer/blob/master/app/src/main/res/drawable/newuser.jpeg)
![Screenshot](https://github.com/akhil117/Summarizer/blob/master/app/src/main/res/drawable/viewhistory.jpeg)
![Screenshot](https://github.com/akhil117/Summarizer/blob/master/app/src/main/res/drawable/home.jpeg)
![Screenshot](https://github.com/akhil117/Summarizer/blob/master/app/src/main/res/drawable/navigation.jpeg)
![Screenshot](https://github.com/akhil117/Summarizer/blob/master/app/src/main/res/drawable/history.jpeg)
![Screenshot](https://github.com/akhil117/Summarizer/blob/master/app/src/main/res/drawable/viewhistory.jpeg)

## Android Libraries
```
implementation 'com.google.android.gms:play-services-auth:15.0.1' 
implementation 'com.github.javiersantos:MaterialStyledDialogs:2.1' 
implementation 'com.android.support:cardview-v7:28.0.0' 
implementation 'com.google.firebase:firebase-database:16.0.1'
```


## MachineLearning 

The generate_summary function inputs the file name from the storage and all the stopwords are removed from the input file 
such as a, an, the. Next, the paragraph is breakdown into sentences and send to sentence_similar_matrix where you call the  
similarity_matrix function which will convert the words into vectors because only for the vectors you can apply a cosine 
distance to find the similarity between the words.

Based on these similarities you can generate the no of top sentences and the summarized text.
Now the text is converted to the  URL with name mal and the topic name with name1 and stored in the firebase.


## Team Members
BATCHU SAI AKHIL (AM.EN.U4CSE16117)

SITARAMI REDDY   (AM.EN.U4CSE16118)

CK PHANI DATTA   (AM.EN.U4CSE16119)

SUMANTH          (AM.EN.U4CSE16129)

K.K NIKHIL       (AM.EN.U4CSE16134)


