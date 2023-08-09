package org.example;

import io.realm.kotlin.UpdatePolicy;
import io.realm.kotlin.notifications.ResultsChange;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * This class shows how you can bridge between Realm Kotlin and a Java code base
 * using a repository interface.
 *
 * The repository interface is created on the Kotlin side, but will only expose
 * methods that are nice to use from the Java side.
 */
public class RealmTestRunner {

    /**
     * Run all test methods
     */
    public void start() throws InterruptedException {
        RealmRepository repo = new RealmRepository();

        info("Open Realm");
        repo.openRealm();

        // Register Flow
        CountDownLatch bg1Started = new CountDownLatch(1);
        Thread t1 = new Thread(() -> {
            repo.updatesAsRxJavaObserverable().subscribe((ResultsChange<Person> event) -> {
                info("Got update from RxJava observable: " + event.getClass() + ", size: " + event.getList().size());
            });
            bg1Started.countDown();
        });
        t1.start();
        bg1Started.await();

        // Register callbacks
        CountDownLatch bg2Started = new CountDownLatch(1);
        Thread t2 = new Thread(() -> {
            CancellationToken token = null;
            try {
                token = repo.updatesAsCallbacks(event -> {
                    info("Got update from Callback observable: " + event.getClass() + ", size: " + event.getList().size());
                });
                bg2Started.countDown();
            } catch (Exception e) {
                if (token != null) {
                    token.cancel();
                }
            }
        });
        t2.start();
        bg2Started.await();

        info("Write 5 persons");
        repo.updateData(realm -> {
            for (int i = 0; i < 5; i++) {
                Person p = new Person();
                p.setName("Person " + i);
                p.setAge(i);
                // Use helper methods instead of accessing Kotlin extension functions
                ArrayList<Child> children = new ArrayList<Child>(2);
                children.add(new Child("Child 1"));
                children.add(new Child("Child 2"));
                p.setChildren(children);
                realm.copyToRealm(p, UpdatePolicy.ERROR);
            }
        });

        info("Read data");
        List<Person> result = repo.readData("TRUEPREDICATE");
        info("Found " + result.size() + " persons.");
        t1.interrupt();
        t2.interrupt();
        repo.closeRealm();
        info("Closed Realm");
    }

    private void info(String message) {
        System.out.println(message);
    }
}
