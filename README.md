# Groovesquid [![Build Status](https://api.travis-ci.org/groovesquid/groovesquid.png)](https://travis-ci.org/groovesquid/groovesquid/) [![Crowdin](https://d322cqt584bo4o.cloudfront.net/groovesquid/localized.png)](https://crowdin.com/project/groovesquid)

Instantly search, find and download over 15 million MP3s anonymously from Grooveshark with Groovesquid!

Downloads are at [http://groovesquid.com](http://groovesquid.com)

Compiling
---------

Use the provided Gradle runtime to compile.

    ./gradlew build

Contributing
------------

You can create pull requests with your own features and I'll mostly accept them and release the new builds on the website.

If you want to help with translation, please [join our Facebook Translator group](https://www.facebook.com/groups/groovesquid.translators/) and send the translated [locale_en_US.properties](https://github.com/groovesquid/groovesquid/blob/master/src/main/resources/groovesquid/properties/locale_en_US.properties) file to us or simply create a pull request.

Donations
---------

Donations are very appreciated! See http://groovesquid.com/#donate.

Todo
---------

* Fix encoding issues in FileStore.java while writing ID3 tags (äöüß etc. --> ????)
* Fix "invalid client" error on startup (not a problem yet, but may be a problem in the future)
* Find another way to design GUI than netbeans designer
* download rate (kb/s) only shown when downloads are finished
* duration & kBit/s preloading

Also see https://groovesquid.uservoice.com/forums/205365-general

License
---------
GPLv3. Copyright (c) [Maino Development](http://maino.in).
