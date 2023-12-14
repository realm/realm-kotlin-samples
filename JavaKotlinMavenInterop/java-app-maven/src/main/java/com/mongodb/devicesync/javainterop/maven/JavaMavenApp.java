package com.mongodb.devicesync.javainterop.maven;

import com.mongodb.devicesync.kotlin.CancellationToken;
import com.mongodb.devicesync.kotlin.Person;
import com.mongodb.devicesync.kotlin.RealmRepository;
import io.realm.kotlin.MutableRealm;
import io.realm.kotlin.notifications.InitialResults;
import io.realm.kotlin.notifications.ResultsChange;
import io.realm.kotlin.notifications.UpdatedResults;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Java app, demonstrating how you can create an interop between a separate module containing
 * Realm Kotlin.
 */
public class JavaMavenApp {
    public static void main(String[] args) throws InterruptedException {
        RealmRepository repository = new RealmRepository();

        println("Opening Realm");
        repository.openRealm();

        // It looks like there might be a problem with writeBlocking and Flows.
        // See https://github.com/realm/realm-kotlin/issues/1606
        println("Listen to data changes");
        CountDownLatch waitForUpdateLatch = new CountDownLatch(2);
        CancellationToken listenerToken = repository.updatesAsCallbacks((ResultsChange<Person> change) -> {
            // Updates will happen on the thread determined by dispatcher running
            // the flow on the Kotlin side. In this case Dispatchers.Default, which
            // is a worker thread
            if (change instanceof InitialResults) {
                println("Initial event: (size=" + change.getList().size() + ")");
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

        println("Update data");
        repository.updateData((MutableRealm realm) -> {
            realm.findLatest(result.get(0)).setName("UpdatedName");
        });
        waitForUpdateLatch.await();
        listenerToken.cancel();
        println("Close Realm");
        repository.closeRealm();
    }

    private static void println(String message) {
        System.out.println("JavaApp: " + message);
    }

}
