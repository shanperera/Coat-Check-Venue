# Coat Check - Venue

This is a **proof of concept** application to be used by a large scale venue for efficiently checking and
returning coat's that guests have checked in.

The system uses Firebase DB to store all relevant user and venue data, this allows the data to be 
fetched quickly whenever required by the venue or the user.

### Note:
Realtime Firebase DB data are stored as JSON objects. To ensure data security and integrity, each venue is given a unique user ID,
this allows for each venue to quickly access their relevant data and keeps the data of other venues private and inaccessible.

The database has a flattened structure to ensure minimal delay and absolute accuracy when storing and retrieving data.

This project uses the Zebra Crossing barcode scanning library for Android: https://github.com/zxing/zxing

## **Overall Structure of the Firebase DB:**
### Venue
A **Venue** JSON array stores the data of all venues registered with Coat Check. 
A venue is given a unique user id and their metadata is stored in their respective object. For readability, 
the venue objects are labelled venue1, venue2, etc... When in production, the venue objects will be referenced by their user ID.

<img src="/rm-images/Venue.PNG" />

### Coats
A seperate JSON array named **Coats** holds the coat room data for each venue. This array stores the username of the guest who's coat is 
hung on a specific hanger. The hanger data can be either "Available" or store a username, "Available" indicates the hanger is not in use.

<img src="/rm-images/Coats.PNG" width=228 height=455/>

### Users
A seperate JSON array named **Users** stores an array of Venues and a single integer. In each Venue array is an array of Users who have
a coat checked in with this venue. Each array is referenced by the coat owner's username, the data includes the hanger that their coat is hung on, their full name, email and phone number. This data is used to quickly retrieve the owner's coat, or to help the venue contact the user if there is any issue with their coat. The single integer in the **Users** parent array stores the total number of users with checked coats.

<img src="/rm-images/Users.PNG" width=366 height=531/>

## **How it works:**
A companion application is optional, it will simply ask for a user to register an account using Google or Facebook, the companion application will store the user's personal information like their phone number and email. When the user arrives at the venue they simply have to press a button on their app that says "Check Coat", the app will generate a QR Code and then the coat check attendant at the venue will scan the QR Code using this Coat-Check-Venue application. The user companion app will store the coat hanger data for their coat, and the venue application will push the data to the Firebase DB.

When the user returns to retrieve their coat, the user will show the QR code generated by their companion app to the coat check attendant, the attendant then scans the QR code with the Coat-Check-Venue application and their coat data will be retrieved from the Firebase DB. Once the attendant has verified the coat has been returned, the user's data will be removed from the venue's Firebase DB and their hanger will be marked available.

If the user does not wish to download the companion app, they can simply give the coat check attendant their phone number. The user's
phone number will be used to identify their coat. This allows the coat check process to continue running smoothly so other guests don't
have to wait in line while one user waits for the companion app to finish downloading on their phone.

## Coat Check UX
Please note that this is a proof of concept application, the UX is very minimal and simply functional. Before release, the UX will be
completely overhauled.

### **Main Menu**
<img src="/rm-images/MainMenu.PNG" width=418 height=246/>

Pressing either Check Coat or Return Coat will open the respective Activity.

### **Check Coat**
<img src="/rm-images/Check.PNG" width=418 height=246/>

Pressing Phone Num. will push the user's coat to the Firebase DB using their phone number. Pressing QR Code will initialize the ZXing library and the camera which will allow the attendant to scan the QR Code to push the user data to the Firebase DB.


### **Return Coat**
<img src="/rm-images/Return.PNG" width=418 height=246/>
Pressing Phone Num. will retrieve the user's coat from the Firebase DB using their phone number. Pressing QR Code will initialize the ZXing library and the camera which will allow the attendant to scan the QR Code and retrieve the user's coat from the Firebase DB.
