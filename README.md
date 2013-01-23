omnibus
=======

Omnibus is a small, clean Data Bus for Android, allowing the construction of loosely-coupled apps.

Activities and Fragments subscribe to receive values of a certain type:

  bus.subscribe(User.class, new Subscriber<User>() {
    public void receive(User user) {
      doSomething(user);
    }
  });

and those values are published to the bus either by a simple publish:

  bus.publish(user);
  
or via a Provider:

  bus.publish(User.class, new Provider<User>() {
    public void provide(Subscriber<T> subscriber, String[] params) {
      subscriber.receive(user);
    }
  });

Where would I use this?
Anywhere you need to share data between different parts of your app. The most common way is to implement Providers for external (web) APIs that your app calls, and have your Activities/Fragments subscribe to them.
Other good examples are Navigation & Preferences 

What boilerplate is needed?
Activities and Fragments should call Bus.attach(this) and Bus.detach(this), usually in onResume() and onPause(). Bus.attach() can be called in onCreate() also (attaching twice is harmless).

What about threading?
The bus core itself is entirely synchronous, but it is designed so that asynchronous Providers are very easy to implement.

Can it leak memory?
Each Activity or Fragment gets its own BusContext when it calls Bus.attach(). The BusContext cleans up all the Subscribers when onDetach() is called.
Providers and Values posted to the bus via publish() are intended to have app-scope and be finite in number.

What if my Providers need parameters?
There are two ways to do this:
1) It's possible to pass a String array of parameters via request(Class, String[])
2) Providers can pull their parameters from the bus

What if I want to write a value to an external API?
update(T value) allows you to pass a value for update to a Provider




