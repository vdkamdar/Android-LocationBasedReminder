# Android-LocationBasedReminder

# This repositiry is protected by Apache License 2.0

Sensors used: GPS sensor, Capacitive sensor
Services: Geofence service, Maps API, Places API
Tools: Android Studio

The main idea of the application is to let users set reminders based on location and operate the features of the alarm using touch gesture recognition. Whenever the user is in the geofence of the set location, the reminder will cause a notification and pop-up the canvas. The user can then custom set the gestures or use pre-defined gestures.

- For detecting location, Geofence service is used to maintain radius around user's current location. 
- For touch gesture recognition, Naive-Bayes classifier algorithm is used to create a probabilistic model
- User is able to define and store their own gestures in the Database and also purge the Database

The application is very useful while travelling, as the simple gesture based actions make it easy for the user to perform actions while driving without any fuss.

