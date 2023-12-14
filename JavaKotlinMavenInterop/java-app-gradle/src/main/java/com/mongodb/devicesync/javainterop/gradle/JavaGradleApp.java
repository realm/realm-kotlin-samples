package com.mongodb.devicesync.javainterop.gradle;

import io.realm.kotlin.MutableRealm;
import io.realm.kotlin.notifications.InitialResults;
import io.realm.kotlin.notifications.ResultsChange;
import io.realm.kotlin.notifications.UpdatedResults;
import com.mongodb.devicesync.kotlin.CancellationToken;
import com.mongodb.devicesync.kotlin.Person;
import com.mongodb.devicesync.kotlin.RealmRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Java app, demonstrating how you can create an interop between a separate module containing
 * Realm Kotlin.
 */
public class JavaGradleApp {
    public static void main(String[] args) throws InterruptedException {
        RealmRepository repository = new RealmRepository();

        println("Opening Realm");
        repository.openRealm();

        println("Write data");
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Person umanagedPerson = new Person();
            umanagedPerson.setName("Person-" + i);
            umanagedPerson.setAge(i);
            persons.add(umanagedPerson);
        }
        repository.writeData(persons);

        println("Query data");
        List<Person> result = repository.readData("age <= 3");
        for (Person person : result) {
            println(person.toString());
        }

        println("Listen to data changes");
        // Use latches to control the timing for the purpose of this app.
        CountDownLatch initialUpdateLatch = new CountDownLatch(1);
        CountDownLatch waitForUpdateLatch = new CountDownLatch(1);
        CancellationToken listenerToken = repository.updatesAsCallbacks((ResultsChange<Person> change) -> {
            // Updates will happen on the thread determined by dispatcher running
            // the flow on the Kotlin side. In this case a custom worker thread.
            if (change instanceof InitialResults) {
                println("Initial event: (size=" + change.getList().size() + ")");
                initialUpdateLatch.countDown();
            } else if (change instanceof UpdatedResults) {
                UpdatedResults<Person> c = (UpdatedResults) change;
                println("Update event: (" +
                        "insertions=" + c.getInsertions().length +
                        ", deletions=" + c.getDeletions().length +
                        ", changes=" + c.getChanges().length +
                        ")");
                waitForUpdateLatch.countDown();
            } else {
                throw new IllegalStateException();
            }
        });
        initialUpdateLatch.await();

        println("Update data");
        repository.updateData((MutableRealm realm) -> {
            realm.findLatest(result.get(0)).setName("UpdatedName");
        });
        waitForUpdateLatch.await();

        println("Close Realm");
        listenerToken.cancel();
        repository.closeRealm();
    }

    private static void println(String message) {
        System.out.println("JavaApp: " + message);
    }

}
