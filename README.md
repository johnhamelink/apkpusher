APKPusher
=========

Simple android app that accepts APKs sent through a socket connection and installs them.

APKPusher is a small android app that sits in the background and runs a simple TCP SocketServer.
This allows you to quickly push apps over your local network or the internet.

This app is designed to be used in a similar way to how someone would use TFTP - it is not designed
to be secure or multi-functional, it's simply designed to allow you to quickly accept APKs sent over
the network.

You can use this app in conjunction with the [APKPusher Command Line application](https://github.com/johnhamelink/apkpusher-cli)
which could easily be placed inside a deployment routine.

Todo:
-----

In future I want to install applications as root without requiring any user input, if the phone
has been rooted.
