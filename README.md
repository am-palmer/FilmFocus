FilmFocus
=================

An Android App written in Kotlin, which helps users to manage a film watchlist, as well as track the films they have watched.

Key technologies: Single-activity pattern with fragments, MVVM architechure. Uses Jetpack components including Room, LiveData, and Navigation.

![searching for films](https://github.com/am-palmer/FilmFocus/blob/master/screenshots/searching.png)
![film details](https://github.com/am-palmer/FilmFocus/blob/master/screenshots/film_details.png)
![marking a film as watched](https://github.com/am-palmer/FilmFocus/blob/master/screenshots/marking_watched.png)
![history](https://github.com/am-palmer/FilmFocus/blob/master/screenshots/history.png)

Please note that to run the app you will need a res/values/keys.xml file containing an `OMDB_API_KEY` string resource, which should be a valid API key for OMDB: ([see here for details](https://www.omdbapi.com/apikey.aspx))

Features
------------
FilmFocus allows users to browse films and TV shows and view information about them, such as their poster, genre, plot description, and IMDB rating. Users can long tap films to perform actions, such as adding them to their Watchlist, or marking them as watched (or dropped)\
Users can leave a short review and rating for films as they please, and these will be shown in the History view. A Navigation drawer allows users to switch between the views easily. Users can also search their Watchlist (which filters the watchlist to match by title)

Technical Details
------------

* FilmFocus uses a [single activity pattern](https://www.youtube.com/watch?v=2k8x8V77CrU), with UI information contained within fragments. Users switch between fragments using a navigation drawer.
* Data is stored using [Room](https://developer.android.com/topic/libraries/architecture/room), the Jetpack abstraction layer over SQLite, meaning that information such as the user's Watchlist is updated on the fly and stored locally when the app is closed.
* FilmFocus employs a MVVM design pattern. 
    * Jetpack [ViewModels](https://developer.android.com/topic/libraries/architecture/viewmodel) interact with remote data and Room data access objects to retreive information for display to the user. 
    * Observable [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) objects are used, meaning that data is retained even through lifecycle events (for instance fragment recreation on rotation of the device).
* Jetpack [Navigation](https://developer.android.com/guide/navigation?hl=en) graphs dictate transitions between fragments 
    * The [SafeArgs](https://developer.android.com/guide/navigation/navigation-pass-data?hl=en#Safe-args) plugin allows for defined arguments to be passed between fragments - for example the name and year of a film, or information pertaining to a user's review and rating of a film.
* Film details are loaded from the OMDB API, and stored as objects which are collected in ArrayLists for display in the UI. The app makes calls to the API to load given information such as a film's poster or plot, and views are programmatically modified to display details about a given film.\
* Each of the three main views - Browse, Watchlist and History - use [RecyclerViews](https://developer.android.com/guide/topics/ui/layout/recyclerview) to show content, for resource efficiency. 
    * The Browse Fragment uses a [listener interface](https://developer.android.com/training/basics/fragments/communicating) to detect when the user has scrolled to the end of the RecyclerView, at which point more search results are loaded from the API (if they are available).\
* Care has been taken to ensure a user-friendly experience, with [Toast messages](https://developer.android.com/guide/topics/ui/notifiers/toasts) to indicate when an action is performed (for example, informing a user that a film has been added to their Watchlist, or, alternatively, that a film is already in their Watchlist), and Dialogs are displayed to prompt the user to confirm any major actions (such as clearing the Watchlist)\
* Layouts for landscape orientation are included. Empty Views explain to the user what actions they can take in a given view, when needed.

Issues / Upcoming Features
------------
The API adapter relies on ASyncTask, which has since been depreciated; it should be replaced with [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html).\
The user's Watchlist and History are stored locally in Room, meaning they are locally stored on the device. While this does not use much space, it means that the data could be lost if the device is reset, damaged, or itself lost. A more robust system would rely on or extend the storage with a cloud solution.
