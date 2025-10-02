# Android SMS Forwarder App

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blue.svg) ![Android](https://img.shields.io/badge/Android-Studio-green.svg) ![Firebase](https://img.shields.io/badge/Firebase-Auth-blue.svg)

A simple Android application that listens for incoming SMS messages and forwards them to a Firebase backend. Built with **Kotlin** and **Jetpack Compose**, this app demonstrates background SMS processing, anonymous Firebase authentication, and calling Firebase Cloud Functions.

---

## Features

* ✅ Listen for incoming SMS messages in the background
* ✅ Read SMS and forward them to Firebase Cloud Functions
* ✅ Anonymous Firebase authentication
* ✅ Real-time logging of SMS messages for debugging
* ✅ Jetpack Compose UI for displaying anonymous UID

---

## Getting Started

### Prerequisites

* Android Studio Flamingo or higher
* Kotlin 1.9+
* Firebase project (with Firestore or Functions configured)
* Android device or emulator with SMS capability

### Installation

1. Clone the repository:

```bash
git clone https://github.com/<your-username>/android-sms-forwarder-app.git
cd android-sms-forwarder-app
```

2. Open the project in **Android Studio**.

3. Add your `google-services.json` to the `app/` directory:

```text
app/google-services.json
```

4. Sync Gradle and build the project.

---

## Permissions

The app requires the following permissions:

* `RECEIVE_SMS` – To receive incoming SMS messages
* `READ_SMS` – Optional, if you want to read message content
* `INTERNET` – To communicate with Firebase

These are requested at runtime for Android 6.0+.

---

## Usage

1. Launch the app on your device.
2. Grant the required SMS permissions when prompted.
3. The app will sign in anonymously to Firebase and display your UID.
4. Incoming SMS messages will be automatically sent to the configured Firebase Cloud Function.

---

## Firebase Setup

The app uses Firebase Authentication and Functions.

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/).
2. Enable **Anonymous Authentication** in Firebase Authentication.
3. Add a Firebase Cloud Function named `addExpenseFromSms` to handle incoming SMS data. Example data payload:

```json
{
  "uid": "USER_UID",
  "sender": "SENDER_NUMBER",
  "message": "MESSAGE_BODY"
}
```

4. Deploy the function to Firebase:

```bash
firebase deploy --only functions
```

Example Firebase Cloud Function (Node.js):

```javascript
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.addExpenseFromSms = functions.https.onCall(async (data, context) => {
    const { uid, sender, message } = data;
    const db = admin.firestore();

    try {
        await db.collection('sms_messages').add({
            uid,
            sender,
            message,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        });
        return { success: true };
    } catch (error) {
        throw new functions.https.HttpsError('internal', error.message);
    }
});
```

---

## Project Structure

```
app/
├─ src/main/java/com/example/smsforwarder/
│  ├─ MainActivity.kt        # Main app activity with UI and Firebase auth
│  ├─ SmsReceiver.kt         # BroadcastReceiver for incoming SMS
├─ AndroidManifest.xml       # Permissions and receiver declaration
```

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/NewFeature`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push to the branch: `git push origin feature/NewFeature`
5. Create a Pull Request

---

## License

MIT License © 2025 [Ashish Mathew](https://github.com/<your-username>)

---

## Notes

* The app only works on real devices that can receive SMS.
* Make sure to comply with local regulations regarding SMS processing.
