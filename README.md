A very custom-made, but simple, radio app for my personal use.

This app loads custom radio lists from Google Firebase's Real time Database, giving me the ability to edit the app without a reinstall or even a restart.

I have made this in order to combine my favorite channels from Morocco, Denmark, USA, and France into one single app, without having to use a large ad-supported alternative like Tune in.

Side Notes:
* This is an Android app that can run on minimum SDK level of 16 (Android 4.1).
* You will need to supply your own Firebase credentials file (google-services.json) to run this app. Put this file in the app/ directory.
* You will also need to setup a simple Real Time Database with the following document:

{
  "stations" : [ {
    "active" : true,
    "favorite": false,
    "id" : 0,
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