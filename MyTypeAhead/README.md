A search bar for wikipedia with live typeahead in Android. 

* `Scheduler`, `subscribeOn`, `observeOn` that help work with multiple threads on Android. This is important because we don't want to block the main (UI) thread while the background thread needs to listen to user interaction happening on the main thread. 
* hot and cold observables. In this example, the user input in the search bar is 'hot'. 
* `debounce` operator.
* Maybe `Subject`. This is a seemingly controversial concept in Rx. I'm still investigating whether this is the correct abstraction to use in our example.

Installation:
* Need Android Studio version >= 2.1 to support lambda

