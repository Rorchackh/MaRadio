# MaRadio Android App

A simple custom-made radio app for my personal use.

This app loads custom radio lists from [Google Firebase](https://firebase.google.com/)'s Real time Database, giving me the ability to edit add/delete/edit radio station within app without a reinstall or even a restart.

What's the point? I have made this in order to combine my favorite channels from Morocco, Denmark, USA, and France into one single app without having to use a large ad-supported alternative like [Tune In](http://tunein.com/). Obviously, there are a lot of radio apps out there (especially for android), but they all either come with preselected stations or they are a bit heavy and confusing for my simple personal use.

Side Notes:

* This is an Android app that can run on minimum SDK level of 16 (Android 4.1).
* You will need to supply your own Firebase credentials file (google-services.json) to run this app. Put this file in the app/ directory.
* All libraries used are open source. Find the list in the [Gradle build file](https://github.com/Rorchackh/MaRadio/blob/master/app/build.gradle) 
* You will also need to setup a simple Real Time Database with the following document:

~~~~
{
  "stations" : [ {
    "active" : true,
    "favorite": false,
    "id" : 0, // ids, not used yet. But keep them unique anyway
    "imageLink" : "http://imageshack.com/a/img924/6642/QrFnnu.jpg", 
    "link" : "http://hitradio-maroc.ice.infomaniak.ch/hitradio-maroc-128.mp3",
    "subtitle" : "",
    "title" : "Hit Radio"
  }, {
    "active" : true,
    "favorite": false,
    "id" : 1,
    "imageLink" : "http://imagizer.imageshack.us/a/img922/4797/MBdjcZ.jpg",
    "link" : "http://gold.ice.infomaniak.ch/gold-128.mp3",
    "subtitle" : "100% Gold",
    "title" : "Hit Radio"
  }]
}
~~~~
