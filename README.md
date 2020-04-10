FilmFocus
=================

An Android App written in Kotlin, which helps users to manage a film watchlist and log films they have watched, with a rating and review.

![searching for films](https://github.com/am-palmer/FilmFocus/blob/master/screenshots/searching.png)


Please note that to run the app you will need a res/values/keys.xml file containing an OMDB_API_KEY string resource, which should be an API key for OMDB: ([see here for details](https://www.omdbapi.com/apikey.aspx))

Features
------------
FilmFocus allows users to browse films and TV shows and view information about them, such as their poster, genre, plot description, and IMDB rating. Users can long tap films to perform actions, such as adding them to their Watchlist, or marking them as watched (or dropped)
Users can leave a short review and rating for films as they please, and these will be shown in the History view. A Navigation drawer allows users to switch between the views easily. Users can also search their Watchlist (which filters the watchlist to match by title)

Technical Details
------------

FilmFocus uses a [single activity pattern](https://www.youtube.com/watch?v=2k8x8V77CrU), with UI information contained within fragments. Users switch between fragments using a navigation drawer. Fragments communicate with the Activity using listener interfaces, such as when a user adds a film to their watchlist from the Browse Fragment, or chooses to edit an item in their History.
Film details are loaded from the OMDB API, and stored as objects which are collected in ArrayLists for display in the UI. The app makes calls to the API to load given information such as a film's poster or plot, and views are programmatically modified to display details about a given film.

Each of the three main views - Browse, Watchlist and History - use RecyclerViews to show content, for resource efficiency. The Browse Fragment uses a listener interface to detect when the user has scrolled to the end of the RecyclerView, at which point more search results are loaded from the API (if they are available).
The user's Watchlist and History are stored in SharedPrefs, and are loaded upon running the app, and updated when the user performs certain actions such as adding a film to their Watchlist or marking a film as watched.

Care has been taken to ensure a user-friendly experience, with Toast messages to indicate when an action is performed (for example, informing a user that a film has been added to their Watchlist, or, alternatively, that a film is already in their Watchlist), and Dialogs are displayed to prompt the user to confirm any major actions (such as clearing the Watchlist)
Layouts for landscape orientation are included. Empty Views explain to the user what actions they can take in a given view, when needed.

Current Issues
------------
There are issues surrounding the seperation of business and UI logic; these have been noted and will be addressed in future, by employing a concrete design pattern through refactoring.
The app does not completely save on destruction by the system - this means that a user's search might be lost (and they will have to search again) if they leave the app and come back to it. This bug will be fixed as I expand my knowledge of the Activity and Fragment lifecycles.
The API adapter relies on ASyncTask, which has since been depreciated; it should be replaced with Coroutines.
The user's Watchlist and History are stored in SharedPrefs, meaning they are locally stored on the device. While this does not use much space (as the data is only simple ArrayLists), it means that the data could be lost if the device is reset, damaged, or itself lost. A more robust system would rely on or extend the storage with a cloud solution.
