_This is an assignment to the [Software Architecture](https://ohm-softa.github.io) class at the [Technische Hochschule Nürnberg](http://www.th-nuernberg.de)._


# Assignment 7: Composite and Observer

First of all:

![Don't panic](./assets/images/dontpanic.jpg)

In this assignment, we will use the [_composite_](https://en.wikipedia.org/wiki/Composite_pattern) and a variant of the [_observer_](https://en.wikipedia.org/wiki/Observer_pattern) patterns.


## Setup

0. Install [Android Studio](https://developer.android.com/studio/index.html).
1. Create a fork of this repository (button in the right upper corner)
2. Clone the project (get the link by clicking the green _Clone or download button_)
3. Import the project to your Android Studio; it behaves almost the same as IntelliJ.
4. Check the [Android Studio guide](./AndroidStudio-Guide.md) to create a virtual device and run the empty app for the first time.

> Note: It is absolutely essential that you do these steps at home -- you will have to download a ton to get started.


## Composite Pattern

Although we will not directly implement the pattern, it is the cornerstone of the Android user interface library.
The base class of anything that you see in the app is (at some point) `android.view.View` ([documentation](https://developer.android.com/reference/android/view/View.html)).
A view can either be a "primitive" such as a `Button` or a `TextView`, or a container class that contains other views, such as a `ListView` or a `ConstraintLayout`.
In user interface language, these views are also called _widgets_, and Android provides a large collection of standard widgets such as buttons, selectors, text inputs, etc.
When you design an activity (a certain "view" of the app), you will make a hierarchical composition of such widgets.

> Note: It is also possible to design your own widgets, but that is a topic for another day.

The following wireframe shows how the app should look like when we are finished.

![Wireframe](./assets/images/wireframe.svg)

As you can see, it is a rather simple layout; it is a hierarchical composition of a main app view, button, checkbox and a list view, which contains the individual dishes.

```
- main app view
  + button "Refresh"
  + checkbox "vegetarian only"
  + list view
    * dish 1: "Rinderroulade ..."
    * dish 2: "Hühnchenfilet ..."
    * ...
```

In Android, layouts are created by writing this schema in a specific XML dialect.
These _layout files_ are stored in `app/res/layout`, e.g. `activity_main.xml`.
When you run the app, it will start the main activity, in our example `MainActivity`.
Usually, the `onCreate` method is overwritten:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // this will inflate the layout from res/layout/activity_main.xml
    setContentView(R.layout.activity_main);

    // add your code here
}
```

Where the static variable `R.layout.activity_main` refers to the file `app/layout/activity_main.xml`, and the `setContentView` will inflate the layout based on the content of this file.

So how are the components layed out on the screen?
If you look at the [Android docs](https://developer.android.com/index.html) for design recommondations you'll notice that the `ConstraintLayout` is currently the preferred way to layout Android apps.
So in the example above, `main app view` is actually a _layout_ container, and in our case, we will use `ConstraintLayout` (which should be the default).


### Basic Layout

Open the `activity_main.xml`; note that you can use the _design mode_ (WYSIWYG) or _text_ mode, to write the XML code manually.

Using the editor, add a `Button`, `CheckBox` and `ListView` to your `ConstraintLayout`.

Make sure that each element has an id associated with it.
You will find it on the very top in the attributes bar on the left (WYSIWIG), or by setting the `android:id="@+id/<some_id>"` attribute; note the `+`, it signals that you _define_ the id, rather than referencing it.

Now we need to define the geographical relation of the components.
Change to the text mode of the editor, and add the appropriate attributes to the elements.
For example:

```
app:layout_constraintStart_toStartOf="..."  // start is the "left" side of the component
app:layout_constraintEnd_toEndOf="..."      // end is the "right" side
app:layout_constraintTop_toTopOf="..."
app:layout_constraintTop_toBottomOf="..."
```

Instead of the `...`, write identifiers of other elements, e.g. `parent` for the enclosing container, or `@id/<some_id>` (note the absence of `+`, since we're referencing).
* As we want to interact with both of them afterwards set ids. (Think of a naming convention you'll remember later when you need the ids.)

For example, the `CheckBox` ([see documentation](https://developer.android.com/reference/android/widget/CheckBox.html)) could be defined as

```xml
<CheckBox
    android:id="@+id/checkBox1"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_marginTop="8dp"
    android:text="Only vegie"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toBottomOf="@id/button1" />
```

On the "Java side" of the app, you can use the `findViewById()` method to get a handle on a widget, for example

```java
CheckBox cb = (CheckBox) findViewById(R.id.checkBox1);
```


### `ListView` Elements

A `ListView` needs a layout to be used to display the elements in the list.
In our case, we want to display a simple line of text.
Create a new **Layout resource file** in the `layout` folder, name it `meal_entry.xml` and set the `Root element` to `TextView`.

The key to populating a list view is the `BaseAdapter` provides the _data_ for a list view.
In our case, we will use an `ArrayAdapter`, that allows us to render the content of an array.
[According to the documentation](https://developer.android.com/reference/android/widget/ArrayAdapter.html), it uses the `toString()` method of each object in the array and place it into a `TextView`.
Once you hand this adapter to the list view, it will update its display.

```java
ListView lv = (ListView) findViewById(R.id.listView1);

lv.setAdapter(new ArrayAdapter<>(
	MainActivity.this,     // context we're in; typically the activity
	R.layout.meal_entry,   // where to find the layout for each item
	new String[] {"Hello", "world"} // your data
));
```

Go ahead and try displaying a few `String` items in your app.


## Observer Pattern

The activity (app) is pretty static for now since it does not allow for any user interaction.
Android (and in fact: most GUI libraries) use a variant of the _observer pattern_ to model user input such as clicks (taps), key presses, etc.

The idea is to provide a _callback_ to a widget that has the desired action.
Depending on the version of Java you use, you can set it either using a _lambda_ expression, or by using an instance of the `View.OnClickListener` instance.

```java
Button btn = (Button) findViewById(R.id.button1);
btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
   		// do crazy stuff when you click the button :-)     
    }
});
```

Go ahead and add an `OnClickListener` to your `Button` that causes the list to display the string `"Hans"` and `"Dampf"`.


### Tips and Good Practice

As you can see, programming user interfaces can quickly become quite a mess.
It is good practice to make your code modular and reusable.
Here are a few tips:

- Cache references to widgets by storing the result of `findViewById` in a local variable or an attribute; obtain the references after inflating the layout in the `onCreate()` method.
- Use anonymous inner classes (or lambda expressions) only for very short methods; move longer logic to separate methods or even classes.
- For heavy lifting such as file IO, sorting, network etc. use threads (more later).
- Use an advanced toolkit such as [Butterknife](jakewharton.github.io/butterknife/) to reduce the amount of boilerplate code to write.


## Business Logic

With all of the user interface and interactions in place (at least in principle), we're ready to add actual logic (and data) to our app.


### OpenMensaAPI

The [OpenMensa API](http://doc.openmensa.org/api/v2/) is an API to retrieve the menu of a certain day of a certain canteen.
In the previous assignment, we used [Retrofit](http://square.github.io/retrofit/) to interact with the API.
This time, we will use it again -- but don't worry you won't have to implement any TypeAdapters or anything else, it's straight forward this time.

* Add the method `getMeals` to the interface `OpenMensaAPI` (as shown in the following UML).
	Make sure to use the correct annotations to make the proper call.
* Complete the test in the class `OpenMensaAPITests` to ensure that your implementation is working correctly.

![API spec](./assets/images/APISpec.svg)

**Hints:** 
- As shown last week, parameters of methods have to be mapped to parameters in the annotation.
	The inline comment in the interface shows the actual query we want to produce, which requires canteen id and date (Rosenheim is 229).
- The `Meal` class is provided.
- Check out the `setup()` provided in `OpenMensaAPITests`: it adds an interceptor to Retrofit that will print out every request (and response) on the logcat.


### Putting the Pieces Together

Change your code so that once you click the button, it retrieves today's menu, and renders the dishes' names on the list view.

**Hints:**
- You can get today's date from the `Calendar` API using the `SimpleDateFormat` helper class:
	
```java
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
String today = sdf.format(new Date());
```

- You will need to map the list of `Meal`s to a list of `String`s.
- Android forbids running network code on the UI thread; use the `Call.enqueue()` instead of the `Call.execute()` method, and pass it a `Callback<>` handler.
- In the network callback, make sure to check if the response was successful, by checking `response.isSuccesful()` (which returns true for `2XX` codes).

**Your app should now be able to display what you can eat after this assignment or what you might have eaten before this assignment!**


### Filter the Meals

The last part is to implement the filter for vegetarian meals only.
Unfortunately, the API does not expose any marker so we will use a simple workaround:
a meal is vegetarian if none of the notes contains the string `"fleisch"`.

> Note: The solution is **really** simple, no regular expressions or alike needed.

- Add a `isVegetarian()` method to the `Meal` class using the above workaround.
- Change your code so that it filters the results if the checkbox is checked.

> Note: Kudos if you can also trigger a refresh if the checkbox selection changes.
