###################
Project description
###################
This project was written as final exam for the Udacity "Developing Android Apps" course
(https://www.udacity.com/course/ud853).

This app is not to be published!

Purpose of the app
-----------------
As a Formula 1 Fan, I follow RSS feeds and websites. Although the articles on the site were good,
the website was flooded with commercials (sometimes up to 66% of the page). The provided app is
very slow (lots of loading screens) and the UI is terrible.

The idea of the app is to download the overview RSS Feed and store it into a database. Additionally
to downloading the RSS Feed, the service also loads the page linked in the feed and parses/stores
the content in the database. This will a) provide the user a seamless experience and it's also
working if the reception is bad or gone.

To implement a real RSS Feed changes in the following files are needed
----------------------------------------------------------------------

NewsUtility.java
- downloadRssFeed: use JSoup or any other library to download an rss feed
- downloadFullArticle: use JSoup or any other library to download an article. Tipp: use a regular
  expression to extract only the article

AndroidManifest.xml
- Add the required internet rights to allow downloads