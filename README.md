# Vent
Vent is a mobile application for android where users can find a virtual friend to whom they can vent.  

## Download and Installation
### APK
Application can be downloaded via the url: https://github.com/avinash-saraf/Vent/releases/download/v1.0.0/app-debug.apk

## Features
- A user can add another user from the user base randomly as contact. 
- A user can private chat with any contact and send messages containing text or images.
- A user can play calming music in the background while on the homepage.
- A user can play tic-tac-toe with the computer.

## Implementation
The following is how some of the most important parts of the app was implemented.
- Sign-Up/Login using mobile number using Firebase Authentication as the backend. Unique id generated for each user account by FirebaseAuth is used to save user info (such as account details, and messages sent) in Firebase Realtime Database. (user's node is set as unique id)
- Two-factor authentication
  - After creating account with mobile number, user is prompted to enter details such as 'displayname', 'country' and 'status'.
- Main Activity has TabLayout using ViewPager and SectionsPagerAdapter. There are two tabs, "Chats" and "Recreation".
- Random User Chat
  - When the current user clicks the floating action button (+ icon on the bottom right of the ChatsFragment), an alert dialog pops up with the option to randomly add a new contact from the userbase. When this option is clicked, a random integer is chosen within the range of the number of users. Each node uptil our chosen user, is iterated through to find our randomly selected contact. The reason it iterates through the nodes is because each node value is unique, so its not possible to predict what the chosen user's id will be. Before this random user is added as a contact, some checks are performed on the chosen user. Firstly, the chosen user should not be the same as the current user. this shouldnt be a problem when the userbase is larger. Secondly, the chosen user should not be in the current user's contact list. If the random user passes these tests, we can then add him/her in the current users contact list.
  - A user can chat with the added contact by sending text messages and images. 
 
- Private chat
  - Messages sent b/w two users by saving each message on Firebase Realtime Database for each user (messages node -> sender/reciever user's unique id -> reciever/sender user's unique id) and displaying using RecyclerView. Automatic scroll to last message sent in the chat activity.
  - Images can be sent as well. Images are displayed using url of image location in Firebase Storage. Images are compressed to 30% quality before saving in Firebase Storage.
  - State (online/last time seen) of the reciever user will be displayed below their name in the private chat activity
  - Layout is auto-scrolled to the latest message.
  - All the contacts user has chatted with will be displayed on the "Chats" Tab.
  - Last message sent between users in private chat can be seen below the reciever user's name on the home page.
  
- Contacts
  - Online state of a contact will be displayed with a green dot in the contacts page.
  - A user can remove an existing contact.
 
- Playing music
  - User can play calming music in the background when on the homepage (music can be toggled on or off). Music is always turned off when the user closes the app.

- Tic-Tac-Toe
  - User can play tic-toe-toe with computer. User starts as X and computer randomly makes move in response. Tic-tac-toe board made using buttons and changing the button text when necessary.
  
## Screenshots
  Note: Due to resizing, the images may not reflect the true quality (for that, click on the images to open them in a new tab)<br><br>
   - Chats Tab <br> <br>
  <img src="https://user-images.githubusercontent.com/82901399/129341551-71ad0ab8-0dcd-45b2-8ed0-d808ca8e048d.png" alt="Chats Tab" width="432" height="740"> <img src="https://user-images.githubusercontent.com/82901399/129341547-28eae9a2-ce18-4186-89c0-8004ec9cbe8c.png" alt="Chat Activity" width="432" height="740"> <br> <br>
   - Clicking the plus (+) icon in the bottom-right corner of the chats tab <br> <br>
  <img src="https://user-images.githubusercontent.com/82901399/129341559-335ef184-bdae-4767-9540-0c79e1770f2a.png" alt="Add new contact" width="432" height="740"> <br> <br>
  
   - Clicking the contacts icon in the top-right corner of the main page <br><br>
  <img src="https://user-images.githubusercontent.com/82901399/129344378-db220930-2119-4fbd-afc4-8d0b3ff54deb.png" alt="Contacts" width="432" height="740"> <img src="https://user-images.githubusercontent.com/82901399/129344696-a6146b28-f3c6-4d51-8b4b-8e98d62a8419.png" alt="Contact Profile" width="432" height="740">
  
   - Recreation Tab <br> <br>
  <img src="https://user-images.githubusercontent.com/82901399/129341553-f3754ab3-86bd-49d2-b436-89c113d4249b.png" alt="Recreation Tab" width="432" height="740"> <img src="https://user-images.githubusercontent.com/82901399/129341556-8f2c001b-ff0e-41c5-8909-a22c67fe6ee7.png" alt="Tic-Tac-Toe Game" width="432" height="740"><br><br>
  
  


  

  

