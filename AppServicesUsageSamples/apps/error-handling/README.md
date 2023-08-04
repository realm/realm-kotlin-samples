# Client reset and error handling
This sample aims to demonstrate how to handle synchronization errors in Realm. In this demo, we will link sync errors to the user UI, and showcase the different client reset strategies available.

## Error handling
Sync errors handling is centralized in a single callback function that can be defined in the Realm configuration. The callback would be invoked on each single exception that occurs and it is up to the user to react to it or not. 

Device Sync will automatically recover its regular functionality from most of the errors. In a few cases, the exceptions might be fatal and will require some user interaction.

The demo makes it possible to trigger compensating write errors. Whenever the `Comp. write` button is clicked, an entry with wrong permissions is added to the server-side database. The server would detect such incongruency and will send a `CompensatingWriteException` to the Client. The information about the compensating write will be processed and displayed to the user. We will display other exception messages directly with no processing.

![alt text](compensating-write.png "Compensating write")

## Client reset
The server will reset the client whenever there is a discrepancy in the data history that cannot be resolved. By default, Realm will try to recover any unsynced changes from the client while resetting. However, there are other strategies available: You can discard the changes, do a manual recovery, or perform a backup.

A menu will open just after taping on the button for opening this demo, prompting you to select a strategy to evaluate. Once selected, you will be taken to the demo.

To trigger a client reset, first, you will need to disconnect from the server. Tap the `Disconnect` button.

![alt text](step1.png "Step 1")

Then add several entries by tapping the `Add entry` button. Doing this will add data to the local realm. Because the app is disconnected this data will not be uploaded to the server.

![alt text](step2.png "Step 2")

Now, tap on the `Client reset` button, it tells the server to reset this client. After this, the app will reconnect automatically and the client reset will run.

![alt text](step3.png "Step 3")

After the client reset a confirmation message will be displayed and the data should reflect the selected strategy (either being retained or deleted).

![alt text](step4.png "Step 4")
